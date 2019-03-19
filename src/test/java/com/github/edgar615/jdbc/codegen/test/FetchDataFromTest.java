package com.github.edgar615.jdbc.codegen.test;

import com.github.edgar615.jdbc.codegen.gen.CodegenOptions;
import com.github.edgar615.jdbc.codegen.gen.DaoOptions;
import com.github.edgar615.jdbc.codegen.gen.Generator;
import com.github.edgar615.jdbc.codegen.gen.MybatisOptions;

/**
 * Created by Administrator on 2015/6/9.
 */
public class FetchDataFromTest {

  public static void main(String[] args) throws Exception {
    CodegenOptions options = new CodegenOptions().setUsername("root")
        .setPassword("123456")
        .addGenTable("sys_user")
        .setIgnoreColumnsStr("created*,updated_on")
        .setGenRule(true)
        .setDatabase("tabao")
        .setHost("localhost")
        .setPort(3306)
        .setJdbcArg("verifyServerCertificate=false&useSSL=true&requireSSL=true")
//            .setTableNamePattern("device")
//                .setIgnoreColumnsStr("photo_path,degree")
        .setSrcFolderPath("src/test/codegen")
        .setDomainPackage("com.github.edgar615.util.mybatis")
//        .setDaoOptions(new DaoOptions().setGenImpl(false).setDaoPackage("com.github.edgar615.test.codegen.dao")
//        .setSupportSpring(true))
        .setMybatisOptions(new MybatisOptions().setMapperClassPackage("com.github.edgar615.util.mybatis").setXmlFolderPath("src/test/resources/mapper"));

    new Generator(options).generate();

  }

}
