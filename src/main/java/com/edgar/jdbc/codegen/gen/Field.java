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
package com.edgar.jdbc.codegen.gen;

import com.edgar.jdbc.codegen.db.Column;
import com.google.common.base.CaseFormat;

import java.sql.Types;

/**
 * 属性
 */
public class Field {

  /**
   * 参数类型
   */
  private ParameterType type;

  /**
   * 字段名称
   */
  private String colName;

  /**
   * 字段的驼峰名称
   */
  private String humpName;

  /**
   * 长度
   */
  private int size;

  /**
   * 能否为空
   */
  private boolean isNullable = true;

  /**
   * 是否是主键
   */
  private boolean isPrimary = false;

  /**
   * 是否是自增字段
   */
  private boolean isAutoInc = false;

  /**
   * 默认值
   */
  private String defaultValue;

  private Field() {

  }

  public static Field create(Column column) {
    Field field = new Field();
    field.setHumpName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, column.getName()));
    field.setColName(column.getName());
    field.setNullable(column.isNullable());
    field.setAutoInc(column.isAutoInc());
    field.setDefaultValue(column.getDefaultValue());
    field.setPrimary(column.isPrimary());
    field.setSize(column.getSize());
    //TODO
    field.setType(field.getType(column));
    return field;
  }

  public boolean isPrimary() {
    return isPrimary;
  }

  public void setPrimary(boolean isPrimary) {
    this.isPrimary = isPrimary;
  }

  public boolean isAutoInc() {
    return isAutoInc;
  }

  public void setAutoInc(boolean isAutoInc) {
    this.isAutoInc = isAutoInc;
  }

  public String getHumpName() {
    return humpName;
  }

  public void setHumpName(String humpName) {
    this.humpName = humpName;
  }

  public int getSize() {
    return this.size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public boolean isNullable() {
    return this.isNullable;
  }

  public void setNullable(boolean isNullable) {
    this.isNullable = isNullable;
  }

  public String getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public ParameterType getType() {
    return type;
  }

  public void setType(ParameterType type) {
    this.type = type;
  }

  public String getColName() {
    return this.colName;
  }

  public void setColName(String colName) {
    this.colName = colName;
  }

  private ParameterType getType(Column column)  {

    int colType = column.getType();
    ParameterType parameter = null;
    if ((colType == Types.VARCHAR) || (colType == Types.LONGVARCHAR) || (colType == Types.CLOB)) {
      parameter = ParameterType.STRING;
    } else if (colType == Types.BIGINT) {
      parameter = ParameterType.LONG;
    } else if ((colType == Types.DOUBLE) || (colType == Types.NUMERIC)) {
      parameter = ParameterType.DOUBLE;
    } else if ((colType == Types.FLOAT) || (colType == Types.DECIMAL)) {
      parameter = ParameterType.FLOAT;
    } else if ((colType == Types.INTEGER) || (colType == Types.SMALLINT) || (colType == Types.TINYINT)) {
      parameter = ParameterType.INTEGER;
    } else if ((colType == Types.TIMESTAMP) || (colType == Types.TIME) || (colType == Types.DATE)) {
      parameter = ParameterType.DATE;
    } else if ((colType == Types.BIT) || (colType == Types.BOOLEAN)) {
      parameter = ParameterType.BOOLEAN;
    } else if (colType == Types.CHAR) {
      parameter = ParameterType.STRING;
    } else {
      // no specific type found so set to generic object
      parameter = ParameterType.OBJECT;
    }
    return parameter;
  }
}
