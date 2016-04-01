package com.edgar.jdbc.codegen.gen;

import com.google.common.base.CaseFormat;

import com.edgar.jdbc.codegen.ParameterType;
import com.edgar.jdbc.codegen.db.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * 领域类.
 *
 * @author Edgar  Date 2016/4/1
 */
public class Domain {

  private final List<Field> fields = new ArrayList<>();

  private final List<String> imports = new ArrayList<String>();

  private String name;

  private String tableName;

  private Domain() {
  }

  public static Domain create(Table table) {
    Domain domain = new Domain();
    domain.setTableName(table.getName());
    String humpName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, table.getName());
    domain.setName(humpName);
    table.getColumns().forEach(column -> domain.getFields().add(Field.create(column)));
    domain.getFields().forEach(field -> {
      if (field.getType().getType() == ParameterType.DATE) {
        if (!domain.getImports().contains("java.util.Date")) {
          domain.getImports().add("java.util.Date");
        }
      }
      if (field.getType().getType() == ParameterType.TIMESTAMP) {
        if (!domain.getImports().contains("java.sql.Timestamp")) {
          domain.getImports().add("java.sql.Timestamp");
        }
      }
    });
    return domain;
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
