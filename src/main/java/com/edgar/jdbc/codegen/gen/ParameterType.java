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

/**
 * Enum class for method parameter or return types
 *
 * @author Kalyan Mulampaka
 */
public enum ParameterType {
  OBJECT("Object", "Object", false),
  STRING("String", "String", false),
  BOOLEAN("Boolean", "boolean", true),
  DATE("Date", "Date", false),
  TIMESTAMP("Timestamp", "Timestamp", false),
  LONG("Long", "long", true),
  INTEGER("Integer", "int", true),
  FLOAT("Float", "float", true),
  DOUBLE("Double", "double", true),
  CHAR("Character", "char", true),
  LIST("List", "List", false);

  private String name;

  private String primitiveName;

  private boolean isPrimitive;

  private ParameterType(String name, String primitiveName, boolean isPrimitive) {
    this.name = name;
    this.primitiveName = primitiveName;
    this.isPrimitive = isPrimitive;

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPrimitiveName() {
    return this.primitiveName;
  }

  public void setPrimitiveName(String primitiveName) {
    this.primitiveName = primitiveName;
  }

  public boolean isPrimitive() {
    return isPrimitive;
  }

  public void setPrimitive(boolean isPrimitive) {
    this.isPrimitive = isPrimitive;
  }
}
