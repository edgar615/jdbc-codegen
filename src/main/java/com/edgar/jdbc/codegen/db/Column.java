package com.edgar.jdbc.codegen.db;

import com.google.common.base.CaseFormat;

import java.sql.Types;

/**
 * 数据库的字段.
 *
 * @author Edgar  Date 2016/4/1
 */
public class Column {

  /**
   * 字段名
   */
  private final String name;

  /**
   * 长度
   */
  private final int size;

  /**
   * 默认值
   */
  private final String defaultValue;

  /**
   * 是否可以为空
   */
  private final boolean isNullable;

  /**
   * 是否自增
   */
  private final boolean isAutoInc;

  /**
   * 是否忽略该字段，依赖于codegen的配置.
   */
  private final boolean isIgnore;

  /**
   * 是否是版本号，如果是版本号，修改时改版本自动加1
   */
  private final boolean isVersion;

  /**
   * 是否是主键
   */
  private final boolean isPrimary;

  private final int type;

  private final String remarks;

  private Column(String name, int size, String defaultValue, boolean isNullable,
                 boolean isAutoInc,
                 boolean isIgnore,
                 boolean isPrimary,
                 boolean isVersion,
                 int type,
                 String remarks) {
    this.name = name;
    this.size = size;
    this.defaultValue = defaultValue;
    this.isNullable = isNullable;
    this.isAutoInc = isAutoInc;
    this.isIgnore = isIgnore;
    this.isPrimary = isPrimary;
    this.isVersion = isVersion;
    this.type = type;
    this.remarks = remarks;
  }

  public static ColumnBuilder builder() {
    return new ColumnBuilder();
  }

  public String getName() {
    return name;
  }

  public int getSize() {
    return size;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public boolean isNullable() {
    return isNullable;
  }

  public boolean isAutoInc() {
    return isAutoInc;
  }

  public boolean isIgnore() {
    return isIgnore;
  }

  public boolean isVersion() {
    return isVersion;
  }

  public boolean isPrimary() {
    return isPrimary;
  }

  public int getType() {
    return type;
  }

  public String getRemarks() {
    return remarks;
  }

  public String getUpperCamelName() {
    return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
  }

  public String getLowerCamelName() {
    return (CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name));
  }

  @Override
  public String toString() {
    return "Column{" +
           "name='" + name + '\'' +
           ", size=" + size +
           ", defaultValue='" + defaultValue + '\'' +
           ", isNullable=" + isNullable +
           ", isAutoInc=" + isAutoInc +
           ", isIgnore=" + isIgnore +
           ", isVersion=" + isVersion +
           ", isPrimary=" + isPrimary +
           ", type=" + type +
           ", remarks=" + remarks +
           '}';
  }

  public ParameterType getParameterType() {
    ParameterType parameter;
    if ((type == Types.VARCHAR) || (type == Types.LONGVARCHAR) || (type == Types.CLOB)) {
      parameter = ParameterType.STRING;
    } else if (type == Types.BIGINT) {
      parameter = ParameterType.LONG;
    } else if ((type == Types.DOUBLE) || (type == Types.NUMERIC)) {
      parameter = ParameterType.BIGDECIMAL;
    } else if ((type == Types.FLOAT) || (type == Types.DECIMAL)) {
      parameter = ParameterType.BIGDECIMAL;
    } else if ((type == Types.INTEGER) || (type == Types.SMALLINT) || (type
                                                                             == Types.TINYINT)) {
      parameter = ParameterType.INTEGER;
    } else if ((type == Types.TIMESTAMP) || (type == Types.TIME) || (type == Types.DATE)) {
      parameter = ParameterType.DATE;
    } else if ((type == Types.BIT) || (type == Types.BOOLEAN)) {
      parameter = ParameterType.BOOLEAN;
    } else if (type == Types.CHAR) {
      parameter = ParameterType.STRING;
    } else {
      // no specific type found so set to generic object
      parameter = ParameterType.OBJECT;
    }
    return parameter;
  }

  public static class ColumnBuilder {
    private String name;

    private int size;

    private String defaultValue;

    private boolean isNullable = true;

    private boolean isAutoInc = false;

    private boolean isIgnore = false;

    private boolean isPrimary = false;

    private boolean isVersion;

    private int type;

    private String remarks;

    private ColumnBuilder() {
    }

    public String getRemarks() {
      return remarks;
    }

    public ColumnBuilder setRemarks(String remarks) {
      this.remarks = remarks;
      return this;
    }

    public ColumnBuilder setType(int type) {
      this.type = type;
      return this;
    }

    public ColumnBuilder setName(String name) {
      this.name = name;
      return this;
    }

    public ColumnBuilder setSize(int size) {
      this.size = size;
      return this;
    }

    public ColumnBuilder setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
      return this;
    }

    public ColumnBuilder setNullable(boolean isNullable) {
      this.isNullable = isNullable;
      return this;
    }

    public ColumnBuilder setAutoInc(boolean isAutoInc) {
      this.isAutoInc = isAutoInc;
      return this;
    }

    public ColumnBuilder setIgnore(boolean isIgnore) {
      this.isIgnore = isIgnore;
      return this;
    }

    public ColumnBuilder setPrimary(boolean isPrimary) {
      this.isPrimary = isPrimary;
      return this;
    }

    public ColumnBuilder setVersion(boolean isVersion) {
      this.isVersion = isVersion;
      return this;
    }

    public Column build() {
      return new Column(name, size, defaultValue, isNullable, isAutoInc, isIgnore, isPrimary,
                        isVersion, type, remarks);
    }
  }
}
