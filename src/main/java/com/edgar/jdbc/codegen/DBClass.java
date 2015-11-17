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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  public void generateSource() {
    // generate the default stuff from the super class
    super.printPackage();

    super.printImports();

    super.printClassComments();

    super.printClassDefn();

    super.printClassImplements();

    this.printOpenBrace(0, 2);

    this.printDBTableInfo();

    this.printColumnsEnum();

    this.printCtor();

    super.printUserSourceCode();

    this.printCloseBrace(0, 0); // end of class
    //logger.debug ("Printing Class file content:\n" + sourceBuf.toString ());
  }

  @Override
  protected void addImports() {
    this.imports.add("java.util.LinkedHashMap");
    this.imports.add("java.util.Map");
    this.imports.add("java.util.Collections");
  }

  protected void printDBTableInfo() {
    // add the table name
    sourceBuf.append("\tprivate static String TABLE_NAME = \"" + this.name.toUpperCase() + "\";" +
                             "\n\n");

    // add the table name
    sourceBuf.append("\tprivate static String TABLE_ALIAS = \"" + CodeGenUtil.createTableAlias
            (this.name.toLowerCase()) + "\";\n\n");

    sourceBuf.append("\tpublic static String getTableName()\n\t{\n\t\treturn TABLE_NAME;\n\t}\n\n");

    sourceBuf.append("\tpublic static String getTableAlias()\n\t{\n\t\treturn TABLE_NAME + \" as " +
                             "\" + TABLE_ALIAS;\n\t}\n\n");

    sourceBuf.append("\tpublic static String getAlias()\n\t{\n\t\treturn TABLE_ALIAS;\n\t}\n\n");
  }

  protected void preprocess() {

  }

  private void printColumnsEnum() {
    sourceBuf.append("\tpublic enum COLUMNS ");
    this.printOpenBrace(0, 1);

    for (Field field : this.fields) {
      if (field.isPersistable()) {
        sourceBuf.append("\t\t" + field.getColName().toUpperCase() + "(\"" + field.getColName() +
                                 "\"),\n");
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
    sourceBuf.append("\t\t\treturn TABLE_ALIAS  + \".\" + this.columnName + \" as \" + " +
                             "TABLE_ALIAS + \"_\" + this.columnName;\n");
    this.printCloseBrace(2, 2);

    sourceBuf.append("\t\tpublic String getColumnAliasName () ");
    this.printOpenBrace(2, 1);
    sourceBuf.append("\t\t\treturn TABLE_ALIAS + \"_\" + this.columnName;\n");
    this.printCloseBrace(2, 2);

    this.printCloseBrace(1, 2);
  }

}
