package com.github.edgar615.jdbc.codegen.test;

import com.github.edgar615.jdbc.codegen.gen.CodegenOptions;
import com.github.edgar615.jdbc.codegen.gen.Generator;

/**
 * Created by Administrator on 2015/6/9.
 */
public class FetchDataFromTest {

  public static void main(String[] args) throws Exception {
    CodegenOptions options = new CodegenOptions().setUsername("admin")
            .setPassword("admin")
            .setIgnoreTablesStr("dict*")
            .setIgnoreColumnsStr("created*,updated_on")
            .setGenRule(true)
            .setJdbcUrl(
                    "jdbc:mysql://127.0.0.1:3306/test")
//            .setTableNamePattern("device")
            .setIgnoreColumnsStr("photo_path,degree");

    new Generator(options).generate();

  }

}