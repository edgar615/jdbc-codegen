package com.edgar.jdbc.codegen.db;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

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

  List<String> imports= Lists.newArrayList();

  /**
   * 是否忽略该字段，依赖于codegen的配置.
   */
  private boolean isIgnore;

  private Table(String name, String remarks) {
    this.name = name;
    this.remarks = remarks;
  }

  public static Table create(String name, String remarks) {
    return new Table(name, remarks);
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

  public String getRemarks() {
    return remarks;
  }

  public String getFields() {
    return Joiner.on(",\n\t\t\t\t\t\t")
            .join(columns.stream()
                          .filter(c -> !c.isIgnore())
                          .map(c -> "\""+c.getLowerCamelName() + "\"")
                          .collect(Collectors.toList
                                  ()));
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

  public void addImport(String imp) {
      this.imports.add(imp);
  }

  public List<String> getImports() {
    List<String> list= Lists.newArrayList();
    columns.stream()
            .filter(c -> !c.isIgnore())
            .map(c -> c.getParameterType())
            .forEach(t -> {
              if (t == ParameterType.DATE) {
                list.add("java.util.Date");
              }
              if (t == ParameterType.TIMESTAMP) {
                list.add("java.sql.Timestamp");
              }
              if (t == ParameterType.BIGDECIMAL) {
                list.add("java.math.BigDecimal");
              }
            });
    list.add("java.util.List");
    list.add("java.util.Map");
    list.add("com.google.common.base.MoreObjects");
    list.add("com.google.common.collect.Lists");
    list.add("com.google.common.collect.Maps");
    list.add("com.edgar.util.db.Persistent");
    list.addAll(imports);
    return list;
  }

  public String getUpperCamelName() {
    return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
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
}
