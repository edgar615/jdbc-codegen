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

import java.util.ArrayList;
import java.util.List;

public class Field {
  private ParameterType type;

  private String colName;

  private String humpName;

  private int size;

  private boolean isNullable = true;

  private boolean isPrimitive = false;

  private boolean isAutoInc = false;

  private boolean persistable = true;

  private String defaultValue;

  private List<String> modifiers = new ArrayList<String>();

  public Field() {

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

  public boolean isPrimitive() {
    return this.isPrimitive;
  }

  public void setPrimitive(boolean isPrimitive) {
    this.isPrimitive = isPrimitive;
  }

  public String getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public boolean isPersistable() {
    return this.persistable;
  }

  public void setPersistable(boolean persistable) {
    this.persistable = persistable;
  }

  public ParameterType getType() {
    return this.type;
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

  public List<String> getModifiers() {
    return this.modifiers;
  }

  public void setModifiers(List<String> modifiers) {
    this.modifiers = modifiers;
  }


}
