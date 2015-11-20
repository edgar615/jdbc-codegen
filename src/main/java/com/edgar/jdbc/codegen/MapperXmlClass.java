/**
 *
 * Copyright 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * @author Kalyan Mulampaka
 */
package com.edgar.jdbc.codegen;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import com.edgar.jdbc.codegen.util.CodeGenUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent the db metadata, row mappers and unmappers
 *
 * @author Kalyan Mulampaka
 */
public class MapperXmlClass extends BaseClass {

  final static Logger logger = LoggerFactory.getLogger(MapperXmlClass.class);

  public static String DB_CLASSSUFFIX = "Mapper";

  private boolean hasAutoIncCol;

  private String mapperPackageName;

  private String resultMap;

  private List<String> ignoreUpdatedColumnListStr;

  private String optimisticLockColumn;

  private String comment_start = "<!-- START 写在START和END中间的代码不会被替换 -->";

  private String comment_end = "<!-- END 写在START和END中间的代码不会被替换-->";

  private String is_comment_start = "<!-- START";

  private String is_comment_end = "<!-- END";

  public MapperXmlClass() {
    this.addImports();
    this.classSuffix = DB_CLASSSUFFIX;
  }

  public boolean isHasAutoIncCol() {
    return hasAutoIncCol;
  }

  public void setHasAutoIncCol(boolean hasAutoIncCol) {
    this.hasAutoIncCol = hasAutoIncCol;
  }

  public void setOptimisticLockColumn(String optimisticLockColumn) {
    this.optimisticLockColumn = optimisticLockColumn;
  }

  public void setIgnoreUpdatedColumnListStr(List<String> ignoreUpdatedColumnListStr) {
    this.ignoreUpdatedColumnListStr = ignoreUpdatedColumnListStr;
  }

  public void setMapperPackageName(String mapperPackageName) {
    this.mapperPackageName = mapperPackageName;
  }

  public String cusgenerateUserSourceCodeTags() {
    return "\t" + comment_start + "\n\n\t" + comment_end + "\n\n";
  }

  public void generateSource() {
    // generate the default stuff from the super class
    this.resultMap = name + "ResultMap";
    this.printDocType();
    this.printRootMapper();
  }

  protected String getSourceFileName() {
    String path = "";
    if (!Strings.isNullOrEmpty(this.packageName)) {
      path = CharMatcher.anyOf(".").replaceFrom(this.packageName, "/") + "/";
    }
    if (!Strings.isNullOrEmpty(this.rootFolderPath)) {
      path = this.rootFolderPath + "/" + path;
    }

    String fileName = path + name + classSuffix + ".xml";
    return fileName;
  }

  protected void printDocType() {
    sourceBuf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                             "<!DOCTYPE mapper\n" +
                             "        PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"\n" +
                             "        \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
  }

  protected void printRootMapper() {
    sourceBuf.append("<mapper namespace=\"" + mapperPackageName + "." + name + classSuffix +
                             "\">\n");
    printResultMap();
    printAllColumn();
    printLimitSql();
    printInsertSql();
    printDeleteByPkSql();
    printDeleteByPkSqlLock();
    printUpdateByPkSql();
    printUpdateNullByPrimaryKey();
    printUpdateByPkSqlLock();
    printSelectByPkSql();

    printUserSourceCode();

    sourceBuf.append("</mapper>");
  }

  protected void printUserSourceCode() {
    String userSource = this.userSourceBuf.toString();
    if (Strings.isNullOrEmpty(userSource)) {
      this.sourceBuf.append(cusgenerateUserSourceCodeTags());
    } else {
      this.sourceBuf.append("\t" + userSource);
    }

  }

  protected void readUserSourceCode(File file) {
    try {
      logger.debug("Reading file :{}", file.getName());
      String contents = FileUtils.readFileToString(file);
      //logger.trace ("File contents:{}", contents);

      int startIndex = contents.indexOf(is_comment_start);
      int endIndex = contents.indexOf(is_comment_end);
      logger.debug("Start index:{} End index:{}", startIndex, endIndex);
      if (startIndex != -1 && endIndex != -1) {
        userSourceBuf.append(contents.substring(startIndex, endIndex));
        userSourceBuf.append(comment_end + "\n\n");
      }
      // save the imports
      List<String> lines = FileUtils.readLines(file);
      for (String line : lines) {
        if (line.startsWith("import")) {
          String[] tokens = Iterables.toArray(Splitter.on(" ").split(line), String.class);
          if (tokens.length > 2) {
            String iClass = tokens[1] + " " + tokens[2].substring(0, tokens[2].length() - 1);
            logger.debug("iClass:{}", iClass);
            if (!this.imports.contains(iClass)) {
              this.imports.add(iClass);
            }
          } else {
            String iClass = tokens[1].substring(0, tokens[1].length() - 1);
            if (!this.imports.contains(iClass)) {
              this.imports.add(iClass);
            }
          }
        }
      }

    } catch (Exception e) {
      logger.error(e.getMessage());
    } finally {

    }

  }

  protected void printResultMap() {
    sourceBuf.append("\t<resultMap id=\"" + resultMap + "\" type=\"" +
                             name + "\">\n");
    for (Field field : this.fields) {
      if (field.isPersistable()) {
        sourceBuf.append("\t\t<result column=\"" + field.getColName() + "\" property=\"" + field
                .getHumpName() + "\" />\n");
      }
    }
    sourceBuf.append("\t</resultMap>\n\n");
  }

  protected void printAllColumn() {
    sourceBuf.append("\t<sql id=\"all_column\">\n\t\t");
    int i = this.fields.size();
    for (Field field : this.fields) {
      sourceBuf.append(field.getColName());
      if (--i > 0) {
        sourceBuf.append(", ");
      }
    }
    sourceBuf.append("\n");
    sourceBuf.append("\t</sql>\n\n");
  }

  protected void printLimitSql() {

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

  /**
   * 生成insert的sql语句
   */
  protected void printInsertSql() {
    if (pkeys.size() == 1 && this.hasAutoIncCol) {
      sourceBuf.append("\t<insert id=\"insert\" parameterType=\"" + name + "\"");
      String key = pkeys.entrySet().iterator().next().getKey();
      sourceBuf.append(" useGeneratedKeys=\"true\" keyProperty=\"id\" keyColumn=\"" + key + "\"");
      sourceBuf.append(">\n");
      sourceBuf.append("\t\tinsert into \n\t\t").append(tableName).append("(");
      List<String> columns = new ArrayList<>();
      List<String> args = new ArrayList<>();
      for (Field field : this.fields) {
        if (!this.ignoreUpdatedColumnListStr.contains(field.getColName().toLowerCase()) && field
                .isPersistable() && !field.isAutoInc()) {
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
      sourceBuf.append("\t<insert id=\"insert\" parameterType=\"" + name + "\">\n");
      sourceBuf.append("\t\tinsert into \n\t\t").append(tableName).append("(");
      List<String> columns = new ArrayList<>();
      List<String> args = new ArrayList<>();
      for (Field field : this.fields) {
        if (!this.ignoreUpdatedColumnListStr.contains(field.getColName().toLowerCase()) && field
                .isPersistable() && !field.isAutoInc()) {
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

  /**
   * 生成根据主键删除的sql
   */
  protected void printDeleteByPkSql() {
    if (pkeys.isEmpty()) {
      return;
    }
    sourceBuf.append("\t<delete id=\"deleteByPrimaryKey\" parameterType=\"");
    if (pkeys.size() == 1) {
      sourceBuf.append(pkeys.entrySet().iterator().next().getValue().getPrimitiveName());
    } else {
      sourceBuf.append("map");
    }
    sourceBuf.append("\">\n");
    sourceBuf.append("\t\tdelete from ")
            .append(tableName);
    if (pkeys.size() == 1) {
      sourceBuf.append(" \n\t\twhere ");
      String key = pkeys.entrySet().iterator().next().getKey();
      sourceBuf.append(key).append(" = #{id}");
    } else if (pkeys.size() > 1) {
      sourceBuf.append(" \n\t\twhere ");
      int i = pkeys.size();
      for (String key : pkeys.keySet()) {
        sourceBuf.append(key).append(" = ");
        sourceBuf.append("#{" + key + "}");
        if (--i > 0) {
          sourceBuf.append(" and ");
        }
      }
    }
    sourceBuf.append("\n\t</delete>");
    sourceBuf.append("\n\n");
  }

  /**
   * 生成根据主键删除的sql
   */
  protected void printDeleteByPkSqlLock() {
    if (pkeys.isEmpty() || Strings.isNullOrEmpty(optimisticLockColumn)) {
      return;
    }
    sourceBuf.append("\t<delete id=\"deleteByPrimaryKeyWithLock\" parameterType=\"");
    sourceBuf.append("map");
    sourceBuf.append("\">\n");
    sourceBuf.append("\t\tdelete from ")
            .append(tableName);
    if (pkeys.size() == 1) {
      sourceBuf.append(" \n\t\twhere ");
      String key = pkeys.entrySet().iterator().next().getKey();
      sourceBuf.append(key).append(" = #{id}");
    } else if (pkeys.size() > 1) {
      sourceBuf.append(" \n\t\twhere ");
      int i = pkeys.size();
      for (String key : pkeys.keySet()) {
        sourceBuf.append(key).append(" = ");
        sourceBuf.append("#{" + key + "}");
        if (--i > 0) {
          sourceBuf.append(" \n\t\tand ");
        }
      }
    }
    sourceBuf.append(" \n\t\tand ").append(optimisticLockColumn).append(" = ");
    sourceBuf.append("#{" + CodeGenUtil.normalize(optimisticLockColumn.toLowerCase()) + "}");
    sourceBuf.append("\n\t</delete>");
    sourceBuf.append("\n\n");
  }

  /**
   * 生成根据主键更新的sql
   */
  protected void printUpdateByPkSql() {
    if (pkeys.isEmpty()) {
      return;
    }
    sourceBuf.append("\t<update id=\"updateByPrimaryKey\" parameterType=\"" + name + "\">\n");
    sourceBuf.append("\t\tupdate ").append(tableName).append("\n\t\t<set>\n ");
    List<String> sets = new ArrayList<>();
    for (Field field : this.fields) {
      if (!this.ignoreUpdatedColumnListStr.contains(field.getColName().toLowerCase()) && field
              .isPersistable() && !field.getColName().equalsIgnoreCase(this.optimisticLockColumn)) {
        StringBuffer set = new StringBuffer();
        set.append("\t\t\t<if test=\"" + field.getHumpName() + " != null\">")
                .append(" \n\t\t\t\t" + field.getColName() + " = #{" + field.getHumpName() + "},")
                .append("\n\t\t\t</if>\n");
        sets.add(set.toString());
      }
    }
    sourceBuf.append(Joiner.on("").join(sets));

    sourceBuf.append("\t\t</set>");
    if (!Strings.isNullOrEmpty(optimisticLockColumn)) {
      sourceBuf.append("\n\t\t\t, " + optimisticLockColumn + " = " + optimisticLockColumn + " + 1");
    }
    if (pkeys.size() == 1) {
      sourceBuf.append(" \n\t\twhere ");
      String key = pkeys.entrySet().iterator().next().getKey();
      sourceBuf.append(key).append(" = #{" + CodeGenUtil.normalize(key) + "}");
    } else if (pkeys.size() > 1) {
      sourceBuf.append(" \n\t\twhere ");
      int i = pkeys.size();
      for (String key : pkeys.keySet()) {
        sourceBuf.append(key).append(" = ").append(" #{" + CodeGenUtil.normalize(key) + "}");
        if (--i > 0) {
          sourceBuf.append(" \n\t\tand ");
        }
      }
    }
    sourceBuf.append("\n\t</update>");
    sourceBuf.append("\n\n");
  }

  protected void printUpdateNullByPrimaryKey() {
    if (pkeys.isEmpty()) {
      return;
    }
    sourceBuf.append("\t<update id=\"updateNullByPrimaryKey\" parameterType=\"map\">\n");
    sourceBuf.append("\t\tupdate ").append(tableName).append("\n\t\t<set>\n ");
    List<String> sets = new ArrayList<>();
    for (Field field : this.fields) {
      if (!this.ignoreUpdatedColumnListStr.contains(field.getColName().toLowerCase()) && field
              .isPersistable() && !field.getColName().equalsIgnoreCase(this.optimisticLockColumn)) {
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
    }
    sourceBuf.append(Joiner.on("").join(sets));

    sourceBuf.append("\t\t</set>");
    if (!Strings.isNullOrEmpty(optimisticLockColumn)) {
      sourceBuf.append("\n\t\t\t, " + optimisticLockColumn + " = " + optimisticLockColumn + " + 1");
    }

    if (pkeys.size() == 1) {
      sourceBuf.append(" \n\t\twhere ");
      String key = pkeys.entrySet().iterator().next().getKey();
      sourceBuf.append(key).append(" = #{entity." + CodeGenUtil.normalize(key) + "}");
    } else if (pkeys.size() > 1) {
      sourceBuf.append(" \n\t\twhere ");
      int i = pkeys.size();
      for (String key : pkeys.keySet()) {
        sourceBuf.append(key).append(" = ").append(" #{entity." + CodeGenUtil.normalize(key) + "}");
        if (--i > 0) {
          sourceBuf.append(" \n\t\tand ");
        }
      }
    }
    sourceBuf.append("\n\t</update>");
    sourceBuf.append("\n\n");
  }

  /**
   * 生成根据主键更新的sql
   */
  protected void printUpdateByPkSqlLock() {
    if (pkeys.isEmpty() || Strings.isNullOrEmpty(optimisticLockColumn)) {
      return;
    }
    sourceBuf.append("\t<update id=\"updateByPrimaryKeyWithLock\" parameterType=\"" + name +
                             "\">\n");

    sourceBuf.append("\t\tupdate ").append(tableName);
    sourceBuf.append("\n\t\t<set>\n");
    List<String> sets = new ArrayList<>();
    for (Field field : this.fields) {
      if (!this.ignoreUpdatedColumnListStr.contains(field.getColName().toLowerCase()) && field
              .isPersistable() && !this.optimisticLockColumn.equalsIgnoreCase(field.getColName())) {
        StringBuffer set = new StringBuffer();
        set.append("\t\t\t<if test=\"" + field.getHumpName() + " != null\">")
                .append(" \n\t\t\t\t" + field.getColName() + " = #{" + field.getHumpName() + "},")
                .append("\n\t\t\t</if>\n");
        sets.add(set.toString());
      }
    }
    sourceBuf.append(Joiner.on("").join(sets));

    sourceBuf.append("\t\t</set>");
    sourceBuf.append("\n\t\t\t, " + optimisticLockColumn + " = " + optimisticLockColumn + " + 1");
    if (pkeys.size() == 1) {
      sourceBuf.append(" \n\t\twhere ");
      String key = pkeys.entrySet().iterator().next().getKey();
      sourceBuf.append(key).append(" = #{" + CodeGenUtil.normalize(key) + "}");
    } else if (pkeys.size() > 1) {
      sourceBuf.append(" \n\t\twhere ");
      int i = pkeys.size();
      for (String key : pkeys.keySet()) {
        sourceBuf.append(key).append(" = ").append(" #{" + CodeGenUtil.normalize(key) + "}");
        if (--i > 0) {
          sourceBuf.append(" \nt\tand ");
        }
      }
    }
    sourceBuf.append(" \n\t\tand ").append(optimisticLockColumn).append(" = #{" + CodeGenUtil
            .normalize(optimisticLockColumn) + "}");
    sourceBuf.append("\n\t</update>");
    sourceBuf.append("\n\n");
  }

  /**
   * 生成根据主键删除的sql
   */
  protected void printSelectByPkSql() {
    if (pkeys.isEmpty()) {
      return;
    }
    sourceBuf.append("\t<select id=\"selectByPrimaryKey\" resultMap=\"" + resultMap + "\" " +
                             "parameterType=\"");
    if (pkeys.size() == 1) {
      sourceBuf.append(pkeys.entrySet().iterator().next().getValue().getPrimitiveName());
    } else {
      sourceBuf.append("map");
    }
    sourceBuf.append("\">\n");
    sourceBuf.append("\t\tselect");
    int i = this.fields.size();
    for (Field field : this.fields) {
      if (field.isPersistable()) {
        sourceBuf.append(" ").append(field.getColName());
        if (--i > 0) {
          sourceBuf.append(",");
        }
      }
    }
    sourceBuf.append(" \n\t\tfrom ")
            .append(tableName);
    if (pkeys.size() == 1) {
      sourceBuf.append(" \n\t\twhere ");
      String key = pkeys.entrySet().iterator().next().getKey();
      sourceBuf.append(key).append(" = #{id}");
    } else if (pkeys.size() > 1) {
      sourceBuf.append(" \n\t\twhere ");
      i = pkeys.size();
      for (String key : pkeys.keySet()) {
        sourceBuf.append(key).append(" = ");
        sourceBuf.append("#{" + key + "}");
        if (--i > 0) {
          sourceBuf.append(" \n\t\tand ");
        }
      }
    }
    sourceBuf.append("\n\t</select>");
    sourceBuf.append("\n\n");
  }

  protected void preprocess() {

  }

  @Override
  protected void addImports() {

  }

}
