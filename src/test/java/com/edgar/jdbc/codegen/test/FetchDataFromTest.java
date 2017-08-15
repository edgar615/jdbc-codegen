package com.edgar.jdbc.codegen.test;

import com.edgar.jdbc.codegen.gen.CodegenOptions;
import com.edgar.jdbc.codegen.gen.Generator;

/**
 * Created by Administrator on 2015/6/9.
 */
public class FetchDataFromTest {

  public static void main(String[] args) throws Exception {
    CodegenOptions options = new CodegenOptions().setUsername("admin")
            .setPassword("csst")
            .setIgnoreTablesStr("dict*")
            .setIgnoreColumnsStr("created*,updated_on")
            .setJdbcUrl(
                    "jdbc:mysql://test.ihorn.com.cn:3306/oem")
//            .setTableNamePattern("device")
            .setIgnoreColumnsStr("photo_path,degree");

    new Generator(options).generate();

  }

}