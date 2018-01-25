package com.github.edgar615.jdbc.codegen.test;

import com.github.edgar615.jdbc.codegen.gen.CodegenOptions;
import com.github.edgar615.jdbc.codegen.gen.Generator;

/**
 * Created by Administrator on 2015/6/9.
 */
public class FetchDataFromTest {

  public static void main(String[] args) throws Exception {
    CodegenOptions options = new CodegenOptions().setUsername("root")
            .setPassword("123456")
//            .addGenTable("dict")
//            .addGenTable("dict_item")
            .addGenTable("sys_resource")
            .addGenTable("sys_subsystem")
            .addGenTable("sys_permission")
            .setIgnoreColumnsStr("created*,updated_on")
            .setGenRule(true)
            .setJdbcUrl(
                    "jdbc:mysql://localhost:3306/sys")
//            .setTableNamePattern("device")
//                .setIgnoreColumnsStr("photo_path,degree")
            .setSrcFolderPath("src/main/java")
            .setDomainPackage("com.github.edgar615.om.system.basic.domain");

    new Generator(options).generate();

  }

}