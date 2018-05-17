package com.github.edgar615.jdbc.codegen.gen;

import com.github.edgar615.jdbc.codegen.db.Column;
import com.github.jknack.handlebars.Options;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
            .map(c ->  c.getParameterType().getName())
            .findFirst();
    return optional.orElse("");
  }

}