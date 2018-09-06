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
                .addGenTable("device")
                .setIgnoreColumnsStr("created*,updated_on")
                .setGenRule(true)
                .setDatabase("device")
                .setHost("localhost")
                .setPort(3307)
                .setJdbcArg("verifyServerCertificate=false&useSSL=true&requireSSL=true")
//            .setTableNamePattern("device")
//                .setIgnoreColumnsStr("photo_path,degree")
                .setSrcFolderPath("src/test/codegen")
                .setDomainPackage("com.github.edgar615.test.codegen.domain");

        new Generator(options).generate();

    }

}