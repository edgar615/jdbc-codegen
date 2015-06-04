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
package com.mulampaka.spring.data.jdbc.codegen;

import com.mulampaka.spring.data.jdbc.codegen.util.CodeGenUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
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

    private String repositoryPackageName;

    private String resultMap;

    private List<String> ignoreUpdatedColumnListStr;

    private List<String> optimisticLockColumnList = new ArrayList<>();

    private String comment_start = "<!-- START 写在START和END中间的代码不会被替换 -->";
    private String comment_end = "<!-- END 写在START和END中间的代码不会被替换-->";

    private String is_comment_start = "<!-- START";
    private String is_comment_end = "<!-- END";

    public MapperXmlClass() {
        this.addImports();
        this.classSuffix = DB_CLASSSUFFIX;
    }

    public void addOptimisticLockColumn(String optimisticLockColumn) {
        this.optimisticLockColumnList.add(optimisticLockColumn);
    }

    public void setIgnoreUpdatedColumnListStr(List<String> ignoreUpdatedColumnListStr) {
        this.ignoreUpdatedColumnListStr = ignoreUpdatedColumnListStr;
    }

    public void setRepositoryPackageName(String repositoryPackageName) {
        this.repositoryPackageName = repositoryPackageName;
    }

    protected String getSourceFileName() {
        String path = "";
        if (StringUtils.isNotBlank(this.packageName)) {
            path = StringUtils.replace(this.packageName, ".", "/") + "/";
        }
        if (StringUtils.isNotBlank(this.rootFolderPath)) {
            path = this.rootFolderPath + "/" + path;
        }

        String fileName = path + WordUtils.capitalize(CodeGenUtil.normalize(name)) + classSuffix + ".xml";
        return fileName;
    }

    protected void printDocType() {
        sourceBuf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE mapper\n" +
                "        PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"\n" +
                "        \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
    }

    protected void printRootMapper() {
        sourceBuf.append("<mapper namespace=\"" + repositoryPackageName + "." + WordUtils.capitalize(CodeGenUtil.normalize(name)) + classSuffix + "\">\n");
        printResultMap();
        printInsertSql();
        printDeleteByPkSql();
        printDeleteByPkSqlLock();
        printUpdateByPkSql();
        printUpdateByPkSqlLock();
        printSelectByPkSql();

        printUserSourceCode();

        sourceBuf.append("</mapper>");
    }

    protected void printUserSourceCode() {
        String userSource = this.userSourceBuf.toString();
        if (StringUtils.isBlank(userSource)) {
            this.sourceBuf.append(cusgenerateUserSourceCodeTags());
        } else {
            this.sourceBuf.append("\t" + userSource);
        }

    }

    public String cusgenerateUserSourceCodeTags() {
        return "\t" + comment_start + "\n\n\t" + comment_end + "\n\n";
    }

    protected void readUserSourceCode(File file) {
        try {
            logger.debug("Reading file :{}", file.getName());
            String contents = FileUtils.readFileToString(file);
            //logger.trace ("File contents:{}", contents);

            int startIndex = StringUtils.indexOf(contents, is_comment_start);
            int endIndex = StringUtils.indexOf(contents, is_comment_end);
            logger.debug("Start index:{} End index:{}", startIndex, endIndex);
            if (startIndex != -1 && endIndex != -1) {
                userSourceBuf.append(contents.substring(startIndex, endIndex));
                userSourceBuf.append(comment_end + "\n\n");
            }
            // save the imports
            List<String> lines = FileUtils.readLines(file);
            for (String line : lines) {
                if (StringUtils.startsWith(line, "import")) {
                    String[] tokens = StringUtils.split(line, " ");
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
                WordUtils.capitalize(CodeGenUtil.normalize(name)) + "\">\n");
        for (Field field : this.fields) {
            if (field.isPersistable()) {
                sourceBuf.append("\t\t<result column=\"" + field.getName() + "\" property=\"" + CodeGenUtil.normalize(field.getName()) + "\" />\n");
            }
        }
        sourceBuf.append("\t</resultMap>\n\n");
    }

    /**
     * 生成insert的sql语句
     */
    protected void printInsertSql() {

        sourceBuf.append("\t<insert id=\"insert\" parameterType=\"" + WordUtils.capitalize(CodeGenUtil.normalize(name)) + "\">\n");
        sourceBuf.append("\t\tinsert into \n\t\t").append(name).append("(");
        List<String> columns = new ArrayList<>();
        List<String> args = new ArrayList<>();
        for (Field field : this.fields) {
            if (!this.ignoreUpdatedColumnListStr.contains(field.getName().toLowerCase()) && field.isPersistable()) {
                columns.add(field.getName());
                args.add("${" + CodeGenUtil.normalize(field.getName().toLowerCase()) + "}");
            }
        }
        sourceBuf.append(StringUtils.join(columns, ", "))
                .append(") \n\t\tvalues(").append(StringUtils.join(args, ", ")).append(")");
        sourceBuf.append("\n\t</insert>");
        sourceBuf.append("\n\n");
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
                .append(name);
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
        if (pkeys.isEmpty() || optimisticLockColumnList.isEmpty()) {
            return;
        }
        sourceBuf.append("\t<delete id=\"deleteByPrimaryKeyWithLock\" parameterType=\"");
        if (pkeys.size() == 1) {
            sourceBuf.append(pkeys.entrySet().iterator().next().getValue().getPrimitiveName());
        } else {
            sourceBuf.append("map");
        }
        sourceBuf.append("\">\n");
        sourceBuf.append("\t\tdelete from ")
                .append(name);
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
        for (String o : optimisticLockColumnList) {
            sourceBuf.append(" \nt\tand ").append(o).append(" = ");
            sourceBuf.append("#{" + o + "}");
        }
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
        sourceBuf.append("\t<update id=\"updateByPrimaryKey\" parameterType=\"" + WordUtils.capitalize(CodeGenUtil.normalize(name)) + "\">\n");
        sourceBuf.append("\t\tupdate ").append(name).append(" set ");
        int i = this.fields.size();
        for (Field field : this.fields) {
            if (!this.ignoreUpdatedColumnListStr.contains(field.getName().toLowerCase()) && field.isPersistable()) {
                sourceBuf.append(" \n\t\t").append(field.getName()).append(" = ");
                sourceBuf.append("${" + CodeGenUtil.normalize(field.getName().toLowerCase()) + "}");
                if (--i > 0) {
                    sourceBuf.append(",");
                }
            }
        }

        if (pkeys.size() == 1) {
            sourceBuf.append(" \n\t\twhere ");
            String key = pkeys.entrySet().iterator().next().getKey();
            sourceBuf.append(key).append(" = #{" + CodeGenUtil.normalize(key) + "}");
        } else if (pkeys.size() > 1) {
            sourceBuf.append(" \n\t\twhere ");
            i = pkeys.size();
            for (String key : pkeys.keySet()) {
                sourceBuf.append(key).append(" = ");
                sourceBuf.append(key).append(" = #{" + CodeGenUtil.normalize(key) + "}");
                if (--i > 0) {
                    sourceBuf.append(" and ");
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
        if (pkeys.isEmpty()) {
            return;
        }
        sourceBuf.append("\t<update id=\"updateByPrimaryKeyWithLock\" parameterType=\"" + WordUtils.capitalize(CodeGenUtil.normalize(name)) + "\">\n");
        sourceBuf.append("\t\tupdate ").append(name).append(" set ");
        int i = this.fields.size();
        for (Field field : this.fields) {
            if (!this.ignoreUpdatedColumnListStr.contains(field.getName().toLowerCase()) && field.isPersistable()) {
                sourceBuf.append(" \n\t\t").append(field.getName()).append(" = ");
                sourceBuf.append("${" + CodeGenUtil.normalize(field.getName().toLowerCase()) + "}");
                if (--i > 0) {
                    sourceBuf.append(",");
                }
            }
        }

        if (pkeys.size() == 1) {
            sourceBuf.append(" \n\t\twhere ");
            String key = pkeys.entrySet().iterator().next().getKey();
            sourceBuf.append(key).append(" = #{" + CodeGenUtil.normalize(key) + "}");
        } else if (pkeys.size() > 1) {
            sourceBuf.append(" \n\t\twhere ");
            i = pkeys.size();
            for (String key : pkeys.keySet()) {
                sourceBuf.append(key).append(" = ");
                sourceBuf.append(key).append(" = #{" + CodeGenUtil.normalize(key) + "}");
                if (--i > 0) {
                    sourceBuf.append(" \nt\tand ");
                }
            }
        }
        for (String o : optimisticLockColumnList) {
            sourceBuf.append(" \nt\tand ").append(o).append(" = #{" + CodeGenUtil.normalize(o) + "}");
        }
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
        sourceBuf.append("\t<select id=\"selectByPrimaryKey\" resultMap=\"" + resultMap + "\" parameterType=\"");
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
                sourceBuf.append(" ").append(field.getName());
                if (--i > 0) {
                    sourceBuf.append(",");
                }
            }
        }
        sourceBuf.append(" \n\t\tfrom ")
                .append(name);
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
                    sourceBuf.append(" and ");
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

    public void generateSource() {
        // generate the default stuff from the super class
        this.resultMap = WordUtils.capitalize(CodeGenUtil.normalize(name)) + "ResultMap";
        this.printDocType();
        this.printRootMapper();
    }

}
