package com.github.edgar615.jdbc.codegen.gen;

import com.github.edgar615.jdbc.codegen.db.Column;
import com.github.jknack.handlebars.Options;
import com.google.common.base.Joiner;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HelperSource {

  public CharSequence eq(Object v1, Object v2, Options options) throws IOException {
    if (v1 == null) {
      return options.inverse(this);
    }
    if (v2 == null) {
      return options.inverse(this);
    }
//    Object v2 = options.hash("v");
    if (v1.toString().equals(v2.toString())) {
      return options.fn(this);
    }
    return options.inverse(this);
  }

  public CharSequence parameterType(Object v1, Object v2, Options options) throws IOException {
    if (v1 == null) {
      return options.inverse(this);
    }
    if (v2 == null) {
      return options.inverse(this);
    }
    List<Column> columns = (List<Column>) v2;
    Optional<String> optional = columns.stream()
        .filter(c -> v1.toString().equals(c.getLowerCamelName()))
        .map(c -> c.getParameterType().getName())
        .findFirst();
    return optional.orElse("");
  }

  public CharSequence columnWithComma(Object v1, Options options) throws IOException {
    if (v1 == null) {
      return options.inverse(this);
    }
    List<Column> columns = (List<Column>) v1;
    List<String> columnWithComma = columns.stream().map(c -> c.getName())
        .collect(Collectors.toList());
    return Joiner.on(",").join(columnWithComma);
  }

  /**
   * 增加注解后经常有空格，所以用这个来输出添加注解后的Field
   */
  public CharSequence columnField(Object v1, Options options) throws IOException {
    if (v1 == null) {
      return options.inverse(this);
    }
    Column column = (Column) v1;
    StringBuilder sb = new StringBuilder("private ")
        .append(column.getParameterType().getName())
        .append(" ")
        .append(column.getLowerCamelName())
        .append(";\n");
    return sb.toString();
  }

  public CharSequence newLine(Object v1, Options options) throws IOException {
    return "\n";
  }

  public CharSequence blank(Object v1, Options options) throws IOException {
    return "\t";
  }

  public CharSequence mapperXmlAllField(Object v1, Options options) throws IOException {
    if (v1 == null) {
      return options.inverse(this);
    }
    List<Column> columns = (List<Column>) v1;
    List<String> fields = columns.stream()
        .filter(c -> !c.isIgnore())
        .map(c -> c.getName())
        .collect(Collectors.toList());
    return Joiner.on(",").join(fields);
  }

  public CharSequence mapperXmlUpdateValue(Object v1, Options options) throws IOException {
    if (v1 == null) {
      return options.inverse(this);
    }
    Column column = (Column) v1;
    return " #{persistent." +column.getLowerCamelName() + "}";
  }



  public CharSequence mapperXmlInsertValues(Object v1, Options options) throws IOException {
    if (v1 == null) {
      return options.inverse(this);
    }
    List<Column> columns = (List<Column>) v1;
    List<String> fields = columns.stream()
        .filter(c -> !c.isIgnore())
        .map(c -> c.getLowerCamelName())
        .map(s -> "#{" +s + "}")
        .collect(Collectors.toList());
    return Joiner.on(",").join(fields);
  }

  public CharSequence mapperXmlSelectField(Object v1, Options options) throws IOException {
    if (v1 == null) {
      return options.inverse(this);
    }
    List<String> columns = (List<String>) v1;
    if (columns == null || columns.isEmpty()) {
      return "*";
    }
    return Joiner.on(",").join(columns);
  }
}
