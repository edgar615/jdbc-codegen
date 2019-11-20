package com.github.edgar615.jdbc.codegen.db;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据表.
 *
 * @author Edgar  Date 2016/4/1
 */
public class Table {

  private final String remarks;

  private final String name;

  private final List<Column> columns = new ArrayList<>();

  /**
   * 是否忽略该字段，依赖于codegen的配置.
   */
  private boolean isIgnore;

  private Table(String name, String remarks) {
    this.name = name;
    this.remarks = remarks;
  }

  @Override
  public String toString() {
    return "Table{" +
        "remarks='" + remarks + '\'' +
        ", name='" + name + '\'' +
        ", isIgnore=" + isIgnore +
        ", columns=" + columns +
        '}';
  }

  public static Table create(String name, String remarks) {
    return new Table(name, remarks);
  }

  public String getName() {
    return name;
  }

  public List<Column> getColumns() {
    return columns;
  }

  public boolean isIgnore() {
    return isIgnore;
  }

  public void setIgnore(boolean isIgnore) {
    this.isIgnore = isIgnore;
  }

  public String getRemarks() {
    return remarks;
  }

  public String getFields() {
    return Joiner.on(",\n\t\t\t\t\t\t")
        .join(columns.stream()
            .filter(c -> !c.isIgnore())
            .map(c -> "\"" + c.getLowerCamelName() + "\"")
            .collect(Collectors.toList
                ()));
  }

  public String getUpperUnderscoreFields() {
    return Joiner.on(",\n\t\t\t\t\t\t")
        .join(columns.stream()
            .filter(c -> !c.isIgnore())
            .map(c -> c.getUpperUnderScoreName())
            .collect(Collectors.toList
                ()));
  }

  public String getVirtualFields() {
    return Joiner.on(",\n\t\t\t\t\t\t")
        .join(columns.stream()
            .filter(c -> !c.isIgnore())
            .filter(c -> c.isGenColumn())
            .map(c -> "\"" + c.getLowerCamelName() + "\"")
            .collect(Collectors.toList()));
  }

  public String getUpperUnderscoreVirtualFields() {
    return Joiner.on(",\n\t\t\t\t\t\t")
        .join(columns.stream()
            .filter(c -> !c.isIgnore())
            .filter(c -> c.isGenColumn())
            .map(c -> c.getUpperUnderScoreName())
            .collect(Collectors.toList()));
  }

  public boolean getContainsVirtual() {
    return columns.stream()
        .filter(c -> !c.isIgnore())
        .anyMatch(c -> c.isGenColumn());
  }

  public String getPk() {
    return columns.stream()
        .filter(c -> !c.isIgnore())
        .filter(c -> c.isPrimary())
        .map(c -> c.getName())
        .findFirst()
        .get();
  }

  public ParameterType getPkType() {
    return columns.stream()
        .filter(c -> !c.isIgnore())
        .filter(c -> c.isPrimary())
        .map(c -> c.getParameterType())
        .findFirst()
        .get();
  }

  public String getUpperCamelName() {
    return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
  }

  public String getLowerCamelName() {
    return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
  }

  public void addColumn(Column column) {
    columns.add(column);
  }

}
