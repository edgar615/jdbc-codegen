package com.github.edgar615.jdbc.codegen.db;

import com.github.edgar615.mysql.mapping.ParameterType;
import com.google.common.base.CaseFormat;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库的字段.
 *
 * @author Edgar  Date 2016/4/1
 */
public class DbColumn {

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
   * 是否虚拟列
   */
  private final boolean isGenColumn;

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

  private DbColumn(String name, int size, String defaultValue, boolean isNullable,
      boolean isAutoInc,
      boolean isGenColumn, boolean isIgnore,
      boolean isPrimary,
      boolean isVersion,
      int type,
      String remarks) {
    this.name = name;
    this.size = size;
    this.defaultValue = defaultValue;
    this.isNullable = isNullable;
    this.isAutoInc = isAutoInc;
    this.isGenColumn = isGenColumn;
    this.isIgnore = isIgnore;
    this.isPrimary = isPrimary;
    this.isVersion = isVersion;
    this.type = type;
    this.remarks = remarks;
  }

  @Override
  public String toString() {
    return "DbColumn{" +
        "name='" + name + '\'' +
        ", size=" + size +
        ", defaultValue='" + defaultValue + '\'' +
        ", isNullable=" + isNullable +
        ", isAutoInc=" + isAutoInc +
        ", isGenColumn=" + isGenColumn +
        ", isIgnore=" + isIgnore +
        ", isVersion=" + isVersion +
        ", isPrimary=" + isPrimary +
        ", type=" + type +
        ", remarks=" + remarks +
        '}';
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

  public boolean isGenColumn() {
    return isGenColumn;
  }

  public boolean isAnnotations() {
    return isPrimary || isGenColumn || isVersion;
  }

  public List<String> getAnnotationsClass() {
    List<String> columnAnnotations = new ArrayList<>();
    if (isPrimary) {
      columnAnnotations.add("@PrimaryKey");
    }
    if (isVersion) {
      columnAnnotations.add("@VersionKey");
    }
    if (isGenColumn) {
      columnAnnotations.add("@VirtualKey");
    }
    return columnAnnotations;
  }

  public ParameterType getParameterType() {
    ParameterType parameter;
    if (isCharacterColumn()) {
      parameter = ParameterType.STRING;
    } else if (isBLOBColumn()){
      parameter = ParameterType.BYTE_ARRAY;
    } else if (isIntColumn()){
      parameter = ParameterType.INTEGER;
    } else if (type == Types.BIGINT) {
      parameter = ParameterType.LONG;
    }else if (isNumberColumn()){
      parameter = ParameterType.BIGDECIMAL;
    } else if (type == Types.TINYINT && size == 1) {
      //TINYINT转换为boolean
      parameter = ParameterType.BOOLEAN;
    } else if (isDateColumn()) {
      parameter = ParameterType.DATE;
    } else if (type == Types.BIT && size == 1) {
      //BIT转换为boolean
      parameter = ParameterType.BOOLEAN;
    } else if (type == Types.BIT && size > 1) {
      parameter = ParameterType.STRING;
    } else if (type == Types.BOOLEAN) {
      parameter = ParameterType.BOOLEAN;
    } else {
      // no specific type found so set to generic object
      parameter = ParameterType.OBJECT;
    }
    return parameter;
  }

  private boolean isCharacterColumn() {
    return type == Types.CHAR || type == Types.CLOB
        || type == Types.LONGVARCHAR || type == Types.VARCHAR
        || type == Types.LONGNVARCHAR || type == Types.NCHAR
        || type == Types.NCLOB || type == Types.NVARCHAR;
  }

  private boolean isBLOBColumn() {
    return type == Types.BINARY || type == Types.BLOB
        || type == Types.VARBINARY || type == Types.LONGVARBINARY;
  }

  private boolean isIntColumn() {
    //SMALLINT理论上是short，TINYINT理论上byte，为了简化操作，我们将tiny(1)转换为boolean
    return type == Types.INTEGER || type == Types.SMALLINT
        || (type == Types.TINYINT && size > 1);
  }

  private boolean isNumberColumn() {
    return type == Types.FLOAT || type == Types.DOUBLE
        || type == Types.NUMERIC || type == Types.DECIMAL
        || type == Types.REAL;
  }

  private boolean isDateColumn() {
    return type == Types.TIMESTAMP || type == Types.TIME
        || type == Types.DATE;
  }

  public static class ColumnBuilder {

    private String name;

    private int size;

    private String defaultValue;

    private boolean isNullable = true;

    private boolean isAutoInc = false;

    private boolean isGenColumn = false;

    private boolean isIgnore = false;

    private boolean isPrimary = false;

    private boolean isVersion;

    private int type;

    private String remarks;

    private ColumnBuilder() {
    }

    public boolean isGenColumn() {
      return isGenColumn;
    }

    public ColumnBuilder setGenColumn(boolean genColumn) {
      isGenColumn = genColumn;
      return this;
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

    public DbColumn build() {
      return new DbColumn(name, size, defaultValue, isNullable, isAutoInc, isGenColumn, isIgnore,
          isPrimary,
          isVersion, type, remarks);
    }
  }
}
