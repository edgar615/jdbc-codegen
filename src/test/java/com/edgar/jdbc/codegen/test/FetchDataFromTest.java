package com.edgar.jdbc.codegen.test;

import com.edgar.jdbc.codegen.CodegenOptions;
import com.edgar.jdbc.codegen.CodeGenerator;

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
                    "jdbc:mysql://localhost:3306/fire")
            .setJsr303(true)
            .setIgnoreColumnsStr("photo_path,degree")
            .setDomainExtend("com.csst.core.model.BaseModel")
            .setMapperExtends("com.csst.core.mapper.BaseMapper");

    CodeGenerator.create(options).generateCode();
  }
}