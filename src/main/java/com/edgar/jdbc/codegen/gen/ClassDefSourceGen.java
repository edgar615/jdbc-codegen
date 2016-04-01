package com.edgar.jdbc.codegen.gen;

import com.edgar.jdbc.codegen.CodegenOptions;

/**
 * Created by Edgar on 2016/4/1.
 *
 * @author Edgar  Date 2016/4/1
 */
public class ClassDefSourceGen implements SourceGen {
  @Override
  public String gen(Domain domain, CodegenOptions options) {
    StringBuffer sourceBuf = new StringBuffer();
    printPackage(sourceBuf, options.getDomainPackage());
    printImports(sourceBuf, domain.getImports());
    printClassComments(sourceBuf);
    printClassDefn(sourceBuf, domain.getName());
//    printClassExtends(sourceBuf, options.getDomainExtend());
//    this.printClassImplements();
    printOpenBrace(sourceBuf, 0, 2);

    printCloseBrace(sourceBuf, 0, 2);
    return sourceBuf.toString();
  }
}
