package com.github.edgar615.jdbc.codegen.test;

import com.github.edgar615.jdbc.codegen.gen.CodegenOptions;
import com.github.edgar615.jdbc.codegen.gen.Generator;

/**
 * Created by Administrator on 2015/6/9.
 */
public class FetchDataFromTest {

  public static void main(String[] args) throws Exception {
    CodegenOptions options = new CodegenOptions().setUsername("admin")
            .setPassword("csst")
//            .addGenTable("dict")
//            .addGenTable("dict_item")
            .addGenTable("user")
            .addGenTable("sys_subsystem")
            .addGenTable("sys_permission")
            .setIgnoreColumnsStr("created*,updated_on")
            .setGenRule(true)
            .setDatabase("user_new")
            .setHost("test.ihorn.com.cn")
            .setJdbcArg("verifyServerCertificate=false&useSSL=true&requireSSL=true")
//            .setTableNamePattern("device")
//                .setIgnoreColumnsStr("photo_path,degree")
            .setSrcFolderPath("src/main/java")
            .setDomainPackage("com.github.edgar615.om.system.basic.domain");

    new Generator(options).generate();

  }

}