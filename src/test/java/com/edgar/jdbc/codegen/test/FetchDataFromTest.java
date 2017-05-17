package com.edgar.jdbc.codegen.test;

import com.edgar.jdbc.codegen.gen.CodegenOptions;
import com.edgar.jdbc.codegen.gen.Generator;

/**
 * Created by Administrator on 2015/6/9.
 */
public class FetchDataFromTest {

  public static void main(String[] args) throws Exception {
    CodegenOptions options = new CodegenOptions().setUsername("root")
            .setPassword("123456")
            .setIgnoreTablesStr("*io,his*")
            .setIgnoreColumnsStr("created*,updated_on")
            .setJdbcUrl(
                    "jdbc:mysql://localhost:3306/device")
            .setTableNamePattern("device")
            .setIgnoreColumnsStr("photo_path,degree");

    new Generator(options).generate();

  }

}