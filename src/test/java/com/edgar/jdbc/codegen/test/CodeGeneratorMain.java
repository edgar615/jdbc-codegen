package com.edgar.jdbc.codegen.test;

import com.edgar.jdbc.codegen.CodeGenerator;

/**
 * Created by Administrator on 2015/6/9.
 */
public class CodeGeneratorMain {

  public static void main(String[] args) throws Exception {
    CodeGenerator generator = new CodeGenerator();
    generator.setPropertiesFile("src/test/resources/codegenerator.properties");
    generator.generate();
  }
}