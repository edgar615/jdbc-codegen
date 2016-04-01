package com.edgar.jdbc.codegen.db;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据表.
 *
 * @author Edgar  Date 2016/4/1
 */
public class Table {

  private final String name;

  /**
   * 是否忽略该字段，依赖于codegen的配置.
   */
  private boolean isIgnore;

  private final List<Column> columns = new ArrayList<>();

  private Table(String name) {
    this.name = name;
  }

  public static Table create(String name) {
    return new Table(name);
  }

  public void addColumn(Column column) {
    columns.add(column);
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

  @Override
  public String toString() {
    return "Table{" +
           "name='" + name + '\'' +
           ", isIgnore=" + isIgnore +
           ", columns=" + columns +
           '}';
  }
}
