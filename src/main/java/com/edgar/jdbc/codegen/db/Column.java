package com.edgar.jdbc.codegen.db;

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

  private Column(String name, int size, String defaultValue, boolean isNullable,
                 boolean isAutoInc,
                 boolean isIgnore,
                 boolean isPrimary,
                 boolean isVersion,
                 int type) {
    this.name = name;
    this.size = size;
    this.defaultValue = defaultValue;
    this.isNullable = isNullable;
    this.isAutoInc = isAutoInc;
    this.isIgnore = isIgnore;
    this.isPrimary = isPrimary;
    this.isVersion = isVersion;
    this.type = type;
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
           '}';
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

    private ColumnBuilder() {
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
                        isVersion, type);
    }
  }
}
