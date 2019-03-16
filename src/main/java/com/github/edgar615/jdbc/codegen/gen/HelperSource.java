package com.github.edgar615.jdbc.codegen.gen;

import com.github.edgar615.jdbc.codegen.db.DbColumn;
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
    List<DbColumn> columns = (List<DbColumn>) v2;
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
    List<DbColumn> columns = (List<DbColumn>) v1;
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
    DbColumn column = (DbColumn) v1;
    StringBuilder sb = new StringBuilder("private ")
        .append(column.getParameterType().getName())
        .append(" ")
        .append(column.getLowerCamelName())
        .append(";\n");
    return sb.toString();
  }

}
