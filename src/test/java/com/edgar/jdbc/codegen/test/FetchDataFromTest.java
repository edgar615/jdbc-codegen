package com.edgar.jdbc.codegen.test;

import com.edgar.jdbc.codegen.CodegenOptions;
import com.edgar.jdbc.codegen.FetchDataFromDB;
import com.edgar.jdbc.codegen.db.Table;
import com.edgar.jdbc.codegen.gen.ClassDefSourceGen;
import com.edgar.jdbc.codegen.gen.Domain;

import java.util.List;

/**
 * Created by Administrator on 2015/6/9.
 */
public class FetchDataFromTest {

  public static void main(String[] args) throws Exception {
    CodegenOptions options = new CodegenOptions().setUsername("admin")
            .setPassword("csst")
            .setIgnoreTablesStr("*io,his*")
            .setIgnoreColumnsStr("created*,updated_on")
            .setJdbcUrl(
                    "jdbc:mysql://10.4.7"
                    + ".48:3306/task");
    FetchDataFromDB fetchDataFromDB = new FetchDataFromDB(options);
    List<Table> tables = fetchDataFromDB.fetchTablesFromDb();
    tables.forEach(table -> System.out.println(table));

    tables.forEach(table -> {
      Domain domain = Domain.create(table);
      ClassDefSourceGen gen = new ClassDefSourceGen();
      System.out.println(gen.gen(domain, options));
    });
  }
}