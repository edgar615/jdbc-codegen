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

import com.edgar.jdbc.codegen.util.CodeGenUtil;
import com.edgar.jdbc.codegen.util.StringUtils;
import com.edgar.jdbc.codegen.util.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent the db metadata, row mappers and unmappers
 *
 * @author Kalyan Mulampaka
 */
public class DBClass extends BaseClass {

    final static Logger logger = LoggerFactory.getLogger(DBClass.class);
    public static String DB_CLASSSUFFIX = "DB";

    public DBClass() {
        this.addImports();
        this.classSuffix = DB_CLASSSUFFIX;
    }

    @Override
    protected void addImports() {
        this.imports.add("java.sql.SQLException");
        this.imports.add("org.springframework.jdbc.core.RowMapper");
        this.imports.add("java.sql.ResultSet");
        this.imports.add("java.util.LinkedHashMap");
        this.imports.add("java.util.Map");
        this.imports.add("java.util.Collections");
        this.imports.add("com.edgar.core.jdbc.RowUnmapper");
    }

    protected void printDBTableInfo() {
        // add the table name
        sourceBuf.append("\tprivate static String TABLE_NAME = \"" + this.name.toUpperCase() + "\";\n\n");

        // add the table name
        sourceBuf.append("\tprivate static String TABLE_ALIAS = \"" + CodeGenUtil.createTableAlias(this.name.toLowerCase()) + "\";\n\n");

        sourceBuf.append("\tpublic static String getTableName()\n\t{\n\t\treturn TABLE_NAME;\n\t}\n\n");

        sourceBuf.append("\tpublic static String getTableAlias()\n\t{\n\t\treturn TABLE_NAME + \" as \" + TABLE_ALIAS;\n\t}\n\n");

        sourceBuf.append("\tpublic static String getAlias()\n\t{\n\t\treturn TABLE_ALIAS;\n\t}\n\n");
    }

    protected void printSelectAllColumns() {
        sourceBuf.append("\tpublic static String selectAllColumns(boolean ... useAlias)\n\t{\n\t\treturn (useAlias[0] ? TABLE_ALIAS : TABLE_NAME) + \".*\";\n\t}\n\n");
    }

    /**
     * 生成insert的sql语句
     */
    protected void printNamedInsertSql() {
        List<String> columns = new ArrayList<>();
        List<String> args = new ArrayList<>();
        for (Field field : this.fields) {
            if (field.isPersistable()) {
                columns.add(field.getName());
                args.add(":" + field.getName());
            }
        }
        sourceBuf.append("\tpublic static final String NAMED_INSERT_SQL = \"insert into ")
                .append(name).append("(").append(StringUtils.join(columns, ", "))
                .append(") values(").append(StringUtils.join(args, ", ")).append(")\";\n\n");
    }

    /**
     * 生成根据主键删除的sql
     */
    protected void printNamedDeleteByPkSql() {
        sourceBuf.append("\tpublic static final String NAMED_DELETE_BY_PK_SQL = \"delete from ")
                .append(name);
        if (!pkeys.isEmpty()) {
            sourceBuf.append(" where ");
            for (String key : pkeys.keySet()) {
                sourceBuf.append(key).append(" = ");
                sourceBuf.append(":" + key + " ");
            }
        }
        sourceBuf.append("\";\n\n");
    }

    /**
     * 生成根据主键删除的sql
     */
    protected void printDeleteByPkSql() {
        sourceBuf.append("\tpublic static final String DELETE_BY_PK_SQL = \"delete from ")
                .append(name);
        if (!pkeys.isEmpty()) {
            sourceBuf.append(" where ");
            for (String key : pkeys.keySet()) {
                sourceBuf.append(key).append(" = ");
                sourceBuf.append("?");
            }
        }
        sourceBuf.append("\";\n\n");
    }

    /**
     * 生成根据主键更新的sql
     */
    protected void printNamedUpdateByPkSql() {
        sourceBuf.append("\tpublic static final String NAMED_UPDATE_BY_PK_SQL = \"update ")
                .append(name).append(" set ");
        int i = this.fields.size();
        for (Field field : this.fields) {
            if (field.isPersistable()) {
                sourceBuf.append(" ").append(field.getName()).append(" = ");
                sourceBuf.append(":" + field.getName());
                if (--i > 0) {
                    sourceBuf.append(",");
                }
            }
        }
        if (!pkeys.isEmpty()) {
            sourceBuf.append(" where ");
            for (String key : pkeys.keySet()) {
                sourceBuf.append(key).append(" = ");
                sourceBuf.append(":" + key + " ");
            }
        }
        sourceBuf.append("\";\n\n");
    }

    /**
     * 生成根据主键删除的sql
     */
    protected void printNamedSelectByPkSql() {
        sourceBuf.append("\tpublic static final String NAMED_SELECT_BY_PK_SQL = \"select");
        int i = this.fields.size();
        for (Field field : this.fields) {
            if (field.isPersistable()) {
                sourceBuf.append(" ").append(field.getName());
                if (--i > 0) {
                    sourceBuf.append(",");
                }
            }
        }
        sourceBuf.append(" from ")
                .append(name);
        if (!pkeys.isEmpty()) {
            sourceBuf.append(" where ");
            i = pkeys.size();
            for (String key : pkeys.keySet()) {
                sourceBuf.append(key).append(" = ");
                sourceBuf.append(":" + key + " ");
                if (--i > 0) {
                    sourceBuf.append(" and ");
                }
            }
        }
        sourceBuf.append("\";\n\n");
    }

    /**
     * 生成根据主键更新的sql
     */
    protected void printSelectByPkSql() {
        sourceBuf.append("\tpublic static final String SELECT_BY_PK_SQL = \"select");
        int i = this.fields.size();
        for (Field field : this.fields) {
            if (field.isPersistable()) {
                sourceBuf.append(" ").append(field.getName());
                if (--i > 0) {
                    sourceBuf.append(",");
                }
            }
        }
        sourceBuf.append(" from ")
                .append(name);
        if (!pkeys.isEmpty()) {
            sourceBuf.append(" where ");
            i = pkeys.size();
            for (String key : pkeys.keySet()) {
                sourceBuf.append(key).append(" = ");
                sourceBuf.append("?");
                if (--i > 0) {
                    sourceBuf.append(" and ");
                }
            }
        }
        sourceBuf.append("\";\n\n");
    }

    protected void printRowMapper() {
        String name = WordUtils.capitalize(CodeGenUtil.normalize(this.name));
        // create mapper
        sourceBuf.append("\tpublic static final RowMapper<" + name + "> ROW_MAPPER = new " + name + "RowMapper ();\n");

        sourceBuf.append("\tpublic static final class  " + name + "RowMapper implements RowMapper<" + name + "> ");
        this.printOpenBrace(0, 1);

        sourceBuf.append("\t\tpublic " + name + " mapRow(ResultSet rs, int rowNum) throws SQLException ");
        this.printOpenBrace(2, 1);
        sourceBuf.append("\t\t\t" + name + " obj = new " + name + "();\n");
        for (Field field : this.fields) {
            if (field.isPersistable()) {
                String typeName = field.getType().getName();
                if (field.getType() == ParameterType.INTEGER) {
                    typeName = "Int";
                } else if (field.getType() == ParameterType.DATE) {
                    typeName = "Timestamp";
                }
                sourceBuf.append("\t\t\tobj.set" + WordUtils.capitalize(CodeGenUtil.normalize(field.getName())) + "(rs.get" + typeName + "(COLUMNS." + field.getName().toUpperCase() + ".getColumnName()));\n");
            }
        }

//        if (this.pkeys.size() > 1) {
//            sourceBuf.append("\t\t\tobj.setPersisted(true);\n");
//        }
        sourceBuf.append("\t\t\treturn obj;\n");
        this.printCloseBrace(2, 1);// end of method
        this.printCloseBrace(1, 2); // end of inner mapper class
    }

    protected void printRowUnMapper() {
        String name = WordUtils.capitalize(CodeGenUtil.normalize(this.name));
        // create unmapper
        sourceBuf.append("\tpublic static final RowUnmapper<" + name + "> ROW_UNMAPPER = new " + name + "RowUnmapper ();\n");
        sourceBuf.append("\tpublic static final class " + name + "RowUnmapper implements RowUnmapper<" + name + "> ");
        this.printOpenBrace(0, 1);
        String objName = name.toLowerCase();
        sourceBuf.append("\t\tpublic Map<String, Object> mapColumns(" + name + " " + objName + ") ");
        this.printOpenBrace(2, 1);
        sourceBuf.append("\t\t\tMap<String, Object> mapping = new LinkedHashMap<String, Object>();\n");
        for (Field field : this.fields) {
            if (field.isPersistable()) {
                if (field.getType() == ParameterType.DATE) {
                    sourceBuf.append("\t\t\tif (" + objName + ".get" + WordUtils.capitalize(CodeGenUtil.normalize(field.getName())) + "() != null)\n");
                    sourceBuf.append("\t\t\t\tmapping.put(COLUMNS." + field.getName().toUpperCase() + ".getColumnName(), new Timestamp (" + objName + ".get" + WordUtils.capitalize(CodeGenUtil.normalize(field.getName())) + "().getTime()));\n");
                } else {
                    sourceBuf.append("\t\t\tmapping.put(COLUMNS." + field.getName().toUpperCase() + ".getColumnName(), " + objName + ".get" + WordUtils.capitalize(CodeGenUtil.normalize(field.getName())) + "());\n");
                }
            }
        }
        sourceBuf.append("\t\t\treturn Collections.unmodifiableMap(mapping);\n");
        this.printCloseBrace(2, 1);
        this.printCloseBrace(1, 2);// end of inner unmapper class
    }

    protected void printAliasRowMapper() {
        String name = WordUtils.capitalize(CodeGenUtil.normalize(this.name));
        // create alias mapper
        sourceBuf.append("\tpublic static final RowMapper<" + name + "> ALIAS_ROW_MAPPER = new " + name + "AliasRowMapper ();\n");

        sourceBuf.append("\tpublic static final class  " + name + "AliasRowMapper implements RowMapper<" + name + "> ");
        this.printOpenBrace(0, 1);
        List<Relation> relations = this.relations.get(this.name);

        if (relations != null && !relations.isEmpty()) {
            boolean loadAllRelations = false;

            for (Relation relation : relations) {
                switch (relation.getType()) {
                    case ONE_TO_ONE:
                        loadAllRelations = true;
                        String child = CodeGenUtil.normalize(relation.getChild());
                        sourceBuf.append("\t\tprivate boolean load" + WordUtils.capitalize(child) + " = false;\n");
                        sourceBuf.append("\t\tpublic void setLoad" + WordUtils.capitalize(child) + " (boolean load" + WordUtils.capitalize(child) + ") ");
                        this.printOpenBrace(2, 1);
                        sourceBuf.append("\t\t\tthis.load" + WordUtils.capitalize(child) + " = load" + WordUtils.capitalize(child) + ";\n");
                        this.printCloseBrace(2, 2);
                        break;
                    case ONE_TO_MANY:
                    case UNKNOWN:
                        break;
                }
            }
            if (loadAllRelations) {
                sourceBuf.append("\t\tprivate boolean loadAllRelations = false;\n");
                sourceBuf.append("\t\tpublic void setLoadAllRelations (boolean loadAllRelations) ");
                this.printOpenBrace(2, 1);
                sourceBuf.append("\t\t\tthis.loadAllRelations = loadAllRelations;\n");
                this.printCloseBrace(2, 2);
            }
        }

        if (!this.fkeys.isEmpty()) {
            sourceBuf.append("\t\tprivate boolean loadAllFKeys = false;\n");
            sourceBuf.append("\t\tpublic void setLoadAllFKeys (boolean loadAllFKeys) ");
            this.printOpenBrace(2, 1);
            sourceBuf.append("\t\t\tthis.loadAllFKeys = loadAllFKeys;\n");
            this.printCloseBrace(2, 2);

            for (String fkColName : this.fkeys.keySet()) {
                ForeignKey fkey = this.fkeys.get(fkColName);
                String refObj = WordUtils.capitalize(CodeGenUtil.normalize(fkey.getFieldName()));
                sourceBuf.append("\t\tprivate boolean load" + refObj + " = false;\n");
                sourceBuf.append("\t\tpublic void setLoad" + refObj + " (boolean load" + refObj + ") ");
                this.printOpenBrace(2, 1);
                sourceBuf.append("\t\t\tthis.load" + refObj + " = load" + refObj + ";\n");
                this.printCloseBrace(2, 2);
            }
        }

        sourceBuf.append("\t\tpublic " + name + " mapRow(ResultSet rs, int rowNum) throws SQLException ");
        this.printOpenBrace(2, 1);
        sourceBuf.append("\t\t\t" + name + " obj = new " + name + "();\n");
        for (Field field : this.fields) {
            if (field.isPersistable()) {
                String typeName = field.getType().getName();
                if (field.getType() == ParameterType.INTEGER) {
                    typeName = "Int";
                } else if (field.getType() == ParameterType.DATE) {
                    typeName = "Timestamp";
                }
                sourceBuf.append("\t\t\tobj.set" + WordUtils.capitalize(CodeGenUtil.normalize(field.getName())) + "(rs.get" + typeName + "(COLUMNS." + field.getName().toUpperCase() + ".getColumnAliasName()));\n");
            }
        }
//        if (this.pkeys.size() > 1) {
//            sourceBuf.append("\t\t\tobj.setPersisted(true);\n");
//        }
        if (!this.fkeys.isEmpty()) {
            for (String fkColName : this.fkeys.keySet()) {
                ForeignKey fkey = this.fkeys.get(fkColName);
                String refObj = WordUtils.capitalize(CodeGenUtil.normalize(fkey.getFieldName()));
                String refClass = WordUtils.capitalize(CodeGenUtil.normalize(fkey.getRefTableName()));
                sourceBuf.append("\t\t\tif (this.loadAllFKeys || this.load" + refObj + ")\n");
                sourceBuf.append("\t\t\t\tobj.set" + refObj + "(" + refClass + DBClass.DB_CLASSSUFFIX + ".ALIAS_ROW_MAPPER.mapRow(rs, rowNum)" + ");\n");
            }
        }
        this.printRelations();
        sourceBuf.append("\t\t\treturn obj;\n");
        this.printCloseBrace(2, 1); // end of method
        this.printCloseBrace(1, 2); // end of inner alias mapper class
    }

    protected void printRelations() {
        List<Relation> relations = this.relations.get(this.name);
        if (relations != null && !relations.isEmpty()) {
            for (Relation relation : relations) {
                switch (relation.getType()) {
                    case ONE_TO_ONE:
                        String child = CodeGenUtil.normalize(relation.getChild());
                        sourceBuf.append("\t\t\tif (this.loadAllRelations || this.load" + WordUtils.capitalize(child) + ")\n");
                        sourceBuf.append("\t\t\t\tobj.set" + WordUtils.capitalize(child) + "(" + WordUtils.capitalize(child) + DBClass.DB_CLASSSUFFIX + ".ALIAS_ROW_MAPPER.mapRow(rs, rowNum)" + ");\n");
                        break;
                    case ONE_TO_MANY:
                    case UNKNOWN:
                        break;
                }
            }
        }
    }

    protected void printAllAliasesMethod() {
        // create all aliases
        sourceBuf.append("\tpublic static StringBuffer getAllColumnAliases () ");
        this.printOpenBrace(0, 1);
        sourceBuf.append("\t\tStringBuffer strBuf = new StringBuffer ();\n");
        sourceBuf.append("\t\tint i = COLUMNS.values ().length;\n");
        sourceBuf.append("\t\tfor (COLUMNS c : COLUMNS.values ()) ");
        this.printOpenBrace(2, 1);
        sourceBuf.append("\t\t\tstrBuf.append (c.getColumnAliasAsName ());\n");
        sourceBuf.append("\t\t\tif (--i > 0)\n");
        sourceBuf.append("\t\t\t\tstrBuf.append (\", \");\n");
        this.printCloseBrace(2, 1);
        sourceBuf.append("\t\treturn strBuf;\n");
        this.printCloseBrace(1, 2);
    }


    private void printColumnsEnum() {
        sourceBuf.append("\tpublic enum COLUMNS ");
        this.printOpenBrace(0, 1);

        for (Field field : this.fields) {
            if (field.isPersistable()) {
                sourceBuf.append("\t\t" + field.getName().toUpperCase() + "(\"" + field.getName() + "\"),\n");
            }
        }
        sourceBuf.append("\t\t;\n");
        sourceBuf.append("\n");
        sourceBuf.append("\t\tprivate String columnName;\n\n");
        // create the constructor
        sourceBuf.append("\t\tprivate COLUMNS (String columnName) ");
        this.printOpenBrace(2, 1);
        sourceBuf.append("\t\t\tthis.columnName = columnName;\n");
        this.printCloseBrace(2, 2);
        //create setters/getters
        sourceBuf.append("\t\tpublic void setColumnName (String columnName) ");
        this.printOpenBrace(2, 1);
        sourceBuf.append("\t\t\tthis.columnName = columnName;\n");
        this.printCloseBrace(2, 2);

        sourceBuf.append("\t\tpublic String getColumnName () ");
        this.printOpenBrace(2, 1);
        sourceBuf.append("\t\t\treturn this.columnName;\n");
        this.printCloseBrace(2, 2);

        sourceBuf.append("\t\tpublic String getColumnAlias () ");
        this.printOpenBrace(2, 1);
        sourceBuf.append("\t\t\treturn TABLE_ALIAS + \".\" + this.columnName;\n");
        this.printCloseBrace(2, 2);

        sourceBuf.append("\t\tpublic String getColumnAliasAsName () ");
        this.printOpenBrace(2, 1);
        sourceBuf.append("\t\t\treturn TABLE_ALIAS  + \".\" + this.columnName + \" as \" + TABLE_ALIAS + \"_\" + this.columnName;\n");
        this.printCloseBrace(2, 2);

        sourceBuf.append("\t\tpublic String getColumnAliasName () ");
        this.printOpenBrace(2, 1);
        sourceBuf.append("\t\t\treturn TABLE_ALIAS + \"_\" + this.columnName;\n");
        this.printCloseBrace(2, 2);

        this.printCloseBrace(1, 2);
    }

    protected void preprocess() {

    }

    public void generateSource() {
        // generate the default stuff from the super class
        super.printPackage();

        super.printImports();

        super.printClassComments();

        super.printClassDefn();

        super.printClassImplements();

        this.printOpenBrace(0, 2);

        this.printDBTableInfo();

        this.printNamedInsertSql();

        this.printNamedUpdateByPkSql();

        this.printNamedSelectByPkSql();

        this.printNamedDeleteByPkSql();

        this.printSelectByPkSql();

        this.printDeleteByPkSql();

        this.printSelectAllColumns();

        this.printColumnsEnum();

        this.printCtor();

        this.printRowMapper();

        this.printRowUnMapper();

        this.printAliasRowMapper();

        this.printAllAliasesMethod();

        super.printUserSourceCode();

        this.printCloseBrace(0, 0); // end of class
        //logger.debug ("Printing Class file content:\n" + sourceBuf.toString ());
    }

}