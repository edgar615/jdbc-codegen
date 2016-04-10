package com.edgar.jdbc.codegen.gen;

import com.edgar.jdbc.codegen.CodegenOptions;
import com.edgar.jdbc.codegen.db.Table;
import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * 领域类.
 *
 * @author Edgar  Date 2016/4/1
 */
public class Domain extends BaseClass {

  private final List<Field> fields = new ArrayList<>();

  private String tableName;

  private Field pkField;

  private Domain(CodegenOptions options) {
    super(options);
  }

  public static Domain create(Table table, CodegenOptions options) {
    long pkCount = table.getColumns().stream().filter(column -> column.isPrimary()).count();
    if (pkCount != 1) {
      throw new IllegalArgumentException("codegen only support 1 primary key");
    }
    Domain domain = new Domain(options);
    domain.setTableName(table.getName());
    String humpName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, table.getName());
    domain.setName(humpName);
    table.getColumns().stream().filter(column -> !column.isIgnore())
            .forEach(column -> domain.getFields().add(Field.create(column)));
    domain.getFields().forEach(field -> {
      if (field.getType() == ParameterType.DATE) {
        if (!domain.getImports().contains("java.util.Date")) {
          domain.getImports().add("java.util.Date");
        }
      }
      if (field.getType() == ParameterType.TIMESTAMP) {
        if (!domain.getImports().contains("java.sql.Timestamp")) {
          domain.getImports().add("java.sql.Timestamp");
        }
      }
      if (field.isPrimary()) {
        domain.setPkField(field);
      }
    });
    return domain;
  }

  public Field getPkField() {
    return pkField;
  }

  public void setPkField(Field pkField) {
    this.pkField = pkField;
  }

  public StringBuffer getSourceBuf() {
    return sourceBuf;
  }

  public void printFields(CodegenOptions options) {
    sourceBuf.append("\tprivate static final long serialVersionUID = 1L;\n\n");

    for (Field field : fields) {

      String fieldName = field.getHumpName();
//      StringBuffer modifiers = new StringBuffer("");
//      if (!field.getModifiers().isEmpty()) {
//        for (String modifier : field.getModifiers()) {
//          modifiers.append(modifier + " ");
//        }
//      }

      if (options.isJsr303()) {
        // generate the jsr303 annotations
        //notnull只用在insert中，，因为并不会每次修改都将每个属性提交到rest接口
        if (!field.isNullable() && !field.isPrimary()) {
          if ("javax.validation.groups.Default".equals(options.getInsertGroup())) {
            if (field.getType() == ParameterType.STRING) {
              sourceBuf.append("\t@NotEmpty");
            } else {
              sourceBuf.append("\t@NotNull");
            }
            sourceBuf.append("\n");
          } else {
            if (field.getType() == ParameterType.STRING) {
              sourceBuf.append("\t@NotEmpty(groups = {");
            } else {
              sourceBuf.append("\t@NotNull(groups = {");
            }
            sourceBuf.append(options.getInsertGroup() + ".class");
            sourceBuf.append("})\n");
          }

        }
        if (field.getSize() > 0) {
          if (field.getType() == ParameterType.STRING) {
            sourceBuf.append("\t@Size(max=" + field.getSize() + ")\n");
          }
        }

      }
      sourceBuf.append("\tprivate " + field.getType().getName() + " " + fieldName);
      if (!Strings.isNullOrEmpty(field.getDefaultValue())) {
        if (field.isPrimary()) {
          // this is a pk so ignore
          sourceBuf.append(";\n\n");
        } else {
          ParameterType t = field.getType();
          String val = field.getDefaultValue();
          switch (t) {
            case BOOLEAN:
              if ("0".equals(val)) {
                sourceBuf.append(" = " + false + ";\n\n");
              } else if ("1".equals(val)) {
                sourceBuf.append(" = " + true + ";\n\n");
              } else {
                sourceBuf.append(";\n\n");
              }

              break;
            case INTEGER:
              // postgres default values for int columns are stored as floats. e.g 100.0
              if (CharMatcher.anyOf(".").matchesAnyOf(val)) {
                String[] tokens = Iterables.toArray(Splitter.on(".").split(val), String.class);
                val = tokens[0];
              }
              sourceBuf.append(" = " + Integer.parseInt(val) + ";\n\n");
              break;
            case LONG:
              sourceBuf.append(" = " + Long.parseLong(val) + "L;\n\n");
              break;
            case DOUBLE:
              sourceBuf.append(" = " + Float.parseFloat(val) + "D;\n\n");
              break;
            case FLOAT:
              sourceBuf.append(" = " + Float.parseFloat(val) + "F;\n\n");
              break;
            case DATE:
            case TIMESTAMP:
              if (val.equalsIgnoreCase("now()")) {
                sourceBuf.append(" = new Date ();\n\n");
              } else {
                sourceBuf.append(";\n\n");
              }
              break;
            case STRING:
            case CHAR:
              //不同数据库不同,只考虑了mysql
              sourceBuf.append(" = \"" + val + "\";\n\n");
              break;
            default:
              sourceBuf.append(";\n\n");
          }
        }
      } else {
        sourceBuf.append(";\n\n");
      }
    }
  }

  public void printSetGetMethods() {
    for (Field field : fields) {
      String paramType = field.getType().getName();
      //getter
      String mName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, field.getColName());
      sourceBuf.append("\tpublic " + paramType + " get" + mName + "() ");
      printOpenBrace(0, 1);
      sourceBuf.append("\t\treturn this." + field.getHumpName() + ";\n");
      printCloseBrace(1, 2);
      // setter
      sourceBuf.append("\tpublic void set" + mName + "(");
      sourceBuf.append(paramType + " " + field.getHumpName());
      sourceBuf.append(") ");
      printOpenBrace(0, 1);
      sourceBuf.append("\t\tthis." + field.getHumpName() + " = " + field.getHumpName() + ";\n");
      printCloseBrace(1, 2);

    }
  }

  public void printToString() {
    //override toString()
    sourceBuf.append("\t@Override\n\tpublic String toString() ");
    printOpenBrace(0, 1);
    sourceBuf.append("\t\treturn MoreObjects.toStringHelper(\"" + name + "\")\n");
    for (Field field : fields) {
      String fieldName = field.getHumpName();
      sourceBuf.append("\t\t\t.add(\"" + fieldName + "\", " + fieldName + ")\n");
    }
    sourceBuf.append("\t\t\t.toString();\n");
    printCloseBrace(1, 2);
  }

  @Override
  public void printSource(CodegenOptions options) {
    printPackage();
    printImports();
    printClassComments();
    printClassDefn(getName());
    printClassExtends(ImmutableList.of(options.getDomainExtend()));
    Iterable<String> iterable = Splitter.on(",").omitEmptyStrings().trimResults().split(options.getDomainInterfaces());

    printClassImplements(Lists.newArrayList(iterable));
    printBlank(1);
    printOpenBrace(0, 2);

    //属性
    printFields(options);
    //getter setter
    printSetGetMethods();

    printUserSourceCode();

    printToString();

    printCloseBrace(0, 2);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public List<Field> getFields() {
    return fields;
  }

  public List<String> getImports() {
    return imports;
  }

}
