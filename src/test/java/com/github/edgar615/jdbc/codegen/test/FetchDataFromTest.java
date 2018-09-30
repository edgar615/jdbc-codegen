package com.github.edgar615.jdbc.codegen.test;

import com.github.edgar615.jdbc.codegen.gen.CodegenOptions;
import com.github.edgar615.jdbc.codegen.gen.DaoOptions;
import com.github.edgar615.jdbc.codegen.gen.Generator;

/**
 * Created by Administrator on 2015/6/9.
 */
public class FetchDataFromTest {

  public static void main(String[] args) throws Exception {
    CodegenOptions options = new CodegenOptions().setUsername("device")
        .setPassword("fec40bf3aff7209a")
//            .addGenTable("dict")
//            .addGenTable("dict_item")
        .addGenTable("app_push_policy")
        .setIgnoreColumnsStr("created*,updated_on")
        .setGenRule(true)
        .setDatabase("device")
        .setHost("test.ihorn.com.cn")
        .setPort(3307)
        .setJdbcArg("verifyServerCertificate=false&useSSL=true&requireSSL=true")
//            .setTableNamePattern("device")
//                .setIgnoreColumnsStr("photo_path,degree")
        .setSrcFolderPath("src/test/codegen")
        .setDomainPackage("com.github.edgar615.test.codegen.domain")
        .setDaoOptions(new DaoOptions().setDaoPackage("com.github.edgar615.test.codegen.dao")
        .setSupportSpring(true));

    new Generator(options).generate();

  }

}