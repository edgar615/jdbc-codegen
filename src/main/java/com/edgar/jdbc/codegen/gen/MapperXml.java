package com.edgar.jdbc.codegen.gen;

import com.edgar.jdbc.codegen.CodegenOptions;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by edgar on 16-4-9.
 */
public class MapperXml implements Codegen {

  private final static Logger LOGGER = LoggerFactory.getLogger(MapperXml.class);

  private static final String COMMENT_START = "<!-- START 写在START和END中间的代码不会被替换 -->";

  private static final String COMMENT_END = "<!-- END 写在START和END中间的代码不会被替换-->";

  private static final String IS_COMMENT_SATRT = "<!-- START";

  private static final String IS_COMMENT_END = "<!-- END";

  private final StringBuffer userSourceBuf = new StringBuffer("");


  private final StringBuffer sourceBuf = new StringBuffer();

  private final String resultMap;
  private final Domain domain;

  private final CodegenOptions options;

  public MapperXml(Domain domain, CodegenOptions options) {
    this.domain = domain;
    this.resultMap = domain.getName() + "ResultMap";
    this.options = options;
  }

  @Override
  public void createFile() throws Exception {
    String fileName = this.getSourceFileName();
    File file = new File(fileName);
    if (file.exists()) {
      LOGGER.debug("File:{} exists, appending to existing file...", fileName);
      this.readUserSourceCode(file);
      //logger.debug ("User Source code:{}", this.userSourceBuf);
      this.userSourceBuf.toString();
    }

    FileWriter writer = new FileWriter(file);
    this.generateSource();
    writer.write(sourceBuf.toString());
    writer.close();
    LOGGER.info("Class File created:" + fileName);
  }

  private String getSourceFileName() {

    String path = "";
    if (!Strings.isNullOrEmpty(options.getXmlPackage())) {
      path = CharMatcher.anyOf(".").replaceFrom(options.getXmlPackage(), "/") + "/";
    }
    if (!Strings.isNullOrEmpty(options.getResourceFolderPath())) {
      path = options.getResourceFolderPath() + "/" + path;
    }

    return path + this.domain.getName() + "Mapper.xml";
  }

  public void generateSource() {
    printDocType();
    printNamespace();
    printResultMap();
    printAllColumn();
    printLimitSql();

    printInsertSql();
    printDeleteByPkSql();
    printUpdateByPkSql();
    printUpdateNullByPrimaryKey();
    printSelectByPkSql();

    printUserSourceCode();

    sourceBuf.append("</mapper>");
  }

  private void printUserSourceCode() {
    String userSource = this.userSourceBuf.toString();
    if (Strings.isNullOrEmpty(userSource)) {
      this.sourceBuf.append(COMMENT_START + "\n\n" + COMMENT_END + "\n\n");
    } else {
      this.sourceBuf.append("\t" + userSource);
    }

  }

  private void readUserSourceCode(File file) {
    try {
      LOGGER.debug("Reading file :{}", file.getName());
      String contents = Files.asByteSource(file).asCharSource(Charset.defaultCharset()).read();
      //LOGGER.trace ("File contents:{}", contents);

      int startIndex = contents.indexOf(IS_COMMENT_SATRT);
      int endIndex = contents.indexOf(IS_COMMENT_END);
      LOGGER.debug("Start index:{} End index:{}", startIndex, endIndex);
      if (startIndex != -1 && endIndex != -1) {
        userSourceBuf.append(contents.substring(startIndex, endIndex));
        userSourceBuf.append(COMMENT_END + "\n\n");
      }

    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    } finally {

    }

  }

  private void printDocType() {
    sourceBuf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "<!DOCTYPE mapper\n" +
            "        PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"\n" +
            "        \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
  }

  private void printNamespace() {
    sourceBuf.append("<mapper namespace=\"" + options.getMapperPackage() + "." + domain.getName() +
            "Mapper\">\n");
  }

  private void printResultMap() {
    sourceBuf.append("\t<resultMap id=\"" + resultMap + "\" type=\"" +
            this.domain.getName() + "\">\n");
    domain.getFields().forEach(field ->
                    sourceBuf.append("\t\t<result column=\"" + field.getColName() + "\" property=\"" + field
                            .getHumpName() + "\" />\n")
    );
    sourceBuf.append("\t</resultMap>\n\n");
  }

  private void printAllColumn() {
    sourceBuf.append("\t<sql id=\"all_column\">\n\t\t");
    int i = this.domain.getFields().size();
    for (Field field : this.domain.getFields()) {
      sourceBuf.append(field.getColName());
      if (--i > 0) {
        sourceBuf.append(", ");
      }
    }
    sourceBuf.append("\n");
    sourceBuf.append("\t</sql>\n\n");
  }

  private void printLimitSql() {

    sourceBuf.append("\t<sql id=\"limit\">\n");
    sourceBuf.append("\t\t<if test=\"limit != null\">\n");
    sourceBuf.append("\t\t\tlimit\n");
    sourceBuf.append("\t\t\t<if test=\"offset != null\">\n");
    sourceBuf.append("\t\t\t#{offset},\n");
    sourceBuf.append("\t\t\t</if>\n");
    sourceBuf.append("\t\t\t#{limit}\n");
    sourceBuf.append("\t\t</if>\n");
    sourceBuf.append("\t</sql>\n\n");
  }

  private void printInsertSql() {

    if (domain.getPkField().isAutoInc()) {
      sourceBuf.append("\t<insert id=\"insert\" parameterType=\"" + domain.getName() + "\"");
      String key = domain.getPkField().getColName();
      sourceBuf.append(" useGeneratedKeys=\"true\" keyProperty=\"id\" keyColumn=\"" + key + "\"");
      sourceBuf.append(">\n");
      sourceBuf.append("\t\tinsert into \n\t\t").append(domain.getTableName()).append("(");

      List<String> columns = new ArrayList<>();
      List<String> args = new ArrayList<>();
      for (Field field : this.domain.getFields()) {
        if (!field.isAutoInc()) {
          columns.add(field.getColName());
          args.add("#{" + field.getHumpName() + "}");
        }
      }

      sourceBuf.append(Joiner.on(",").join(columns))
              .append(") \n\t\tvalues(").append(Joiner.on(",").join(args)).append(")");

      sourceBuf.append("\n\n\t\t<selectKey resultType=\"int\" order=\"AFTER\" keyProperty=\"id\" " +
              "keyColumn=\"" + key + "\">");
      sourceBuf.append("\n\t\t\tselect LAST_INSERT_ID() as " + key);
      sourceBuf.append("\n\t\t</selectKey>");
      sourceBuf.append("\n\t</insert>");
      sourceBuf.append("\n\n");
    } else {
      sourceBuf.append("\t<insert id=\"insert\" parameterType=\"" + domain.getName() + "\"");
      sourceBuf.append(">\n");
      sourceBuf.append("\t\tinsert into \n\t\t").append(domain.getTableName()).append("(");

      List<String> columns = new ArrayList<>();
      List<String> args = new ArrayList<>();
      for (Field field : this.domain.getFields()) {
        if (!field.isAutoInc()) {
          columns.add(field.getColName());
          args.add("#{" + field.getHumpName() + "}");
        }
      }

      sourceBuf.append(Joiner.on(",").join(columns))
              .append(") \n\t\tvalues(").append(Joiner.on(",").join(args)).append(")");
      sourceBuf.append("\n\t</insert>");
      sourceBuf.append("\n\n");
    }

  }

  private void printDeleteByPkSql() {
    Optional<Field> optional = this.domain.getFields().stream().filter(field ->
            field.getColName().equalsIgnoreCase(options.getVersionColumn()))
            .findFirst();
    //存在乐观锁字段
    if (optional.isPresent()) {
      sourceBuf.append("\t<delete id=\"deleteByPrimaryKey\" parameterType=\"");
      sourceBuf.append("map");
      sourceBuf.append("\">\n");
      sourceBuf.append("\t\tdelete from ");
      sourceBuf.append(domain.getTableName());
      sourceBuf.append(" \n\t\twhere ");
      sourceBuf.append(domain.getPkField().getColName());
      sourceBuf.append(" = #{id}");
      sourceBuf.append(optional.get().getColName());
      sourceBuf.append(" = #{" + optional.get().getHumpName() + "}");
      sourceBuf.append("\n\t</delete>");
      sourceBuf.append("\n\n");
    } else {
      sourceBuf.append("\t<delete id=\"deleteByPrimaryKey\" parameterType=\"");
      sourceBuf.append(domain.getPkField().getType().getPrimitiveName());
      sourceBuf.append("\">\n");
      sourceBuf.append("\t\tdelete from ");
      sourceBuf.append(domain.getTableName());
      sourceBuf.append(" \n\t\twhere ");
      sourceBuf.append(domain.getPkField().getColName());
      sourceBuf.append(" = #{id}");
      sourceBuf.append("\n\t</delete>");
      sourceBuf.append("\n\n");

    }
  }

  private void printUpdateByPkSql() {
    Optional<Field> optional = this.domain.getFields().stream().filter(field ->
            field.getColName().equalsIgnoreCase(options.getVersionColumn()))
            .findFirst();

    if (optional.isPresent()) {

      sourceBuf.append("\t<update id=\"updateByPrimaryKey\" parameterType=\"" + domain.getName() + "\">\n");
      sourceBuf.append("\t\tupdate ").append(domain.getTableName()).append("\n\t\t<set>\n ");
      List<String> sets = new ArrayList<>();
      for (Field field : this.domain.getFields()) {
        StringBuffer set = new StringBuffer();
        if (!optional.get().getColName().equalsIgnoreCase(field.getColName())) {
          set.append("\t\t\t<if test=\"" + field.getHumpName() + " != null\">")
                  .append(" \n\t\t\t\t" + field.getColName() + " = #{" + field.getHumpName() + "},")
                  .append("\n\t\t\t</if>\n");
          sets.add(set.toString());
        }
      }
      sourceBuf.append(Joiner.on("").join(sets));
      sourceBuf.append("\t\t</set>");
      sourceBuf.append("\n\t\t\t, " + optional.get().getColName());
      sourceBuf.append(" = " + optional.get().getColName() + " + 1");
      sourceBuf.append(" \n\t\twhere ");
      sourceBuf.append(domain.getPkField().getColName());
      sourceBuf.append(" = #{" + domain.getPkField().getHumpName() + "}");
      sourceBuf.append("\n\t</update>");
      sourceBuf.append("\n\n");

    } else {
      sourceBuf.append("\t<update id=\"updateByPrimaryKey\" parameterType=\"" + domain.getName() + "\">\n");
      sourceBuf.append("\t\tupdate ").append(domain.getTableName()).append("\n\t\t<set>\n ");
      List<String> sets = new ArrayList<>();
      for (Field field : this.domain.getFields()) {
        StringBuffer set = new StringBuffer();
        set.append("\t\t\t<if test=\"" + field.getHumpName() + " != null\">")
                .append(" \n\t\t\t\t" + field.getColName() + " = #{" + field.getHumpName() + "},")
                .append("\n\t\t\t</if>\n");
        sets.add(set.toString());
      }
      sourceBuf.append(Joiner.on("").join(sets));
      sourceBuf.append("\t\t</set>");
      sourceBuf.append(" \n\t\twhere ");
      sourceBuf.append(domain.getPkField().getColName());
      sourceBuf.append(" = #{" + domain.getPkField().getHumpName() + "}");
      sourceBuf.append("\n\t</update>");
      sourceBuf.append("\n\n");
    }
  }

  private void printUpdateNullByPrimaryKey() {
    Optional<Field> optional = this.domain.getFields().stream().filter(field ->
            field.getColName().equalsIgnoreCase(options.getVersionColumn()))
            .findFirst();

    if (optional.isPresent()) {

      sourceBuf.append("\t<update id=\"updateNullByPrimaryKey\" parameterType=\"map\">\n");
      sourceBuf.append("\t\tupdate ").append(domain.getTableName()).append("\n\t\t<set>\n ");
      List<String> sets = new ArrayList<>();
      for (Field field : this.domain.getFields()) {
        StringBuffer set = new StringBuffer();
        set.append("\n\t\t\t<choose>");
        set.append("\n\t\t\t\t<when test=\"nulls.contains('" + field.getHumpName() + "')\">");
        set.append(" \n\t\t\t\t\t" + field.getColName() + " = null,");
        set.append("\n\t\t\t\t</when>");
        set.append("\n\t\t\t\t<otherwise>");
        set.append("\n\t\t\t\t\t<if test=\"entity." + field.getHumpName() + " != null\">")
                .append(" \n\t\t\t\t\t\t" + field.getColName() + " = #{entity." + field.getHumpName
                        () + "},")
                .append("\n\t\t\t\t\t</if>");
        set.append("\n\t\t\t\t</otherwise>");
        set.append("\n\t\t\t</choose>");
        sets.add(set.toString());
      }
      sourceBuf.append(Joiner.on("").join(sets));
      sourceBuf.append("\t\t</set>");
      sourceBuf.append("\n\t\t\t, " + optional.get().getColName());
      sourceBuf.append(" = " + optional.get().getColName() + " + 1");
      sourceBuf.append(" \n\t\twhere ");
      sourceBuf.append(domain.getPkField().getColName());
      sourceBuf.append(" = #{" + domain.getPkField().getHumpName() + "}");
      sourceBuf.append("\n\t</update>");
      sourceBuf.append("\n\n");

    } else {
      sourceBuf.append("\t<update id=\"updateNullByPrimaryKey\" parameterType=\"map\">\n");
      sourceBuf.append("\t\tupdate ").append(domain.getTableName()).append("\n\t\t<set>\n ");
      List<String> sets = new ArrayList<>();
      for (Field field : this.domain.getFields()) {
        StringBuffer set = new StringBuffer();
        set.append("\n\t\t\t<choose>");
        set.append("\n\t\t\t\t<when test=\"nulls.contains('" + field.getHumpName() + "')\">");
        set.append(" \n\t\t\t\t\t" + field.getColName() + " = null,");
        set.append("\n\t\t\t\t</when>");
        set.append("\n\t\t\t\t<otherwise>");
        set.append("\n\t\t\t\t\t<if test=\"entity." + field.getHumpName() + " != null\">")
                .append(" \n\t\t\t\t\t\t" + field.getColName() + " = #{entity." + field.getHumpName
                        () + "},")
                .append("\n\t\t\t\t\t</if>");
        set.append("\n\t\t\t\t</otherwise>");
        set.append("\n\t\t\t</choose>");
        sets.add(set.toString());
      }
      sourceBuf.append(Joiner.on("").join(sets));
      sourceBuf.append("\t\t</set>");
      sourceBuf.append(" \n\t\twhere ");
      sourceBuf.append(domain.getPkField().getColName());
      sourceBuf.append(" = #{" + domain.getPkField().getHumpName() + "}");
      sourceBuf.append("\n\t</update>");
      sourceBuf.append("\n\n");
    }
  }


  private void printSelectByPkSql() {
    sourceBuf.append("\t<select id=\"selectByPrimaryKey\" resultMap=\"" + resultMap + "\" " +
            "parameterType=\"");
    sourceBuf.append(domain.getPkField().getType().getPrimitiveName());
    sourceBuf.append("\">\n");
    sourceBuf.append("\t\tselect");
    sourceBuf.append("\n\t\t\t<include refid=\"all_column\" />");
    sourceBuf.append(" \n\t\tfrom ");
    sourceBuf.append(this.domain.getTableName());
    sourceBuf.append(" \n\t\twhere ");
    sourceBuf.append(domain.getPkField().getColName());
    sourceBuf.append(" = #{id}");
    sourceBuf.append("\n\t</select>");
    sourceBuf.append("\n\n");
  }

}
