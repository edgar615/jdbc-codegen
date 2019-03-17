package com.github.edgar615.jdbc.codegen.test;

import com.github.edgar615.jdbc.codegen.gen.CodegenOptions;
import com.github.edgar615.jdbc.codegen.gen.DaoOptions;
import com.github.edgar615.jdbc.codegen.gen.Generator;

/**
 * Created by Administrator on 2015/6/9.
 */
public class FetchDataFromTest {

  public static void main(String[] args) throws Exception {
    CodegenOptions options = new CodegenOptions().setUsername("root")
        .setPassword("G4xEViQUhs@B2SAf8tqxDL")
        .addGenTable("sys_user")
        .setIgnoreColumnsStr("created*,updated_on")
        .setGenRule(true)
        .setDatabase("tabao")
        .setHost("47.92.126.53")
        .setPort(3306)
        .setJdbcArg("verifyServerCertificate=false&useSSL=true&requireSSL=true")
//            .setTableNamePattern("device")
//                .setIgnoreColumnsStr("photo_path,degree")
        .setSrcFolderPath("src/test/codegen")
        .setDomainPackage("com.github.edgar615.test.codegen.domain")
        .setDaoOptions(new DaoOptions().setGenImpl(false).setDaoPackage("com.github.edgar615.test.codegen.dao")
        .setSupportSpring(true));

    new Generator(options).generate();

  }

}
