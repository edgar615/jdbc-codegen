package com.github.edgar615.jdbc.codegen.db;

public enum ParameterType {
  OBJECT("Object", "Object", false),
  STRING("String", "String", false),
  BOOLEAN("Boolean", "boolean", true),
  DATE("Date", "Date", false),
  TIMESTAMP("Timestamp", "Timestamp", false),
  LONG("Long", "long", true),
  INTEGER("Integer", "int", true),
  SHORT("Short", "short", true),
  BYTE("Byte", "byte", true),
  FLOAT("Float", "float", true),
  BIGDECIMAL("BigDecimal", "BigDecimal", true),
  DOUBLE("Double", "double", true),
  CHAR("Character", "char", true),
  LIST("List", "List", false),
  BYTE_ARRAY("byte[]", "byte[]", false);

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
