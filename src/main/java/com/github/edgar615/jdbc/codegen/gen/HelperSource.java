package com.github.edgar615.jdbc.codegen.gen;

import com.github.jknack.handlebars.Options;

import java.io.IOException;

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

}