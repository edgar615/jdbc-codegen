package com.edgar.jdbc.codegen.gen;

import com.edgar.jdbc.codegen.CodegenOptions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Created by edgar on 16-4-9.
 */
public class Mapper extends BaseClass {

  private final String domainName;

  private final ParameterType pkType;

  private CodegenOptions options;

  public Mapper(String domainName, ParameterType pkType, CodegenOptions options) {
    super(options);
    this.domainName = domainName;
    this.pkType = pkType;
    this.name = domainName + "Mapper";
  }

  private void printClassAnnotations() {
    sourceBuf.append("@Repository\n");
  }

  @Override
  public void printSource(CodegenOptions options) {
    if (options.isGenRepositoryAnnotation()) {
      this.imports.add("org.springframework.stereotype.Repository");
    }
    printPackage();
    printImports();
    printClassComments();
    if (options.isGenRepositoryAnnotation()) {
      printClassAnnotations();
    }
    printClassDefn(getName());
    printClassExtends(ImmutableList.of(options.getMapperExtends()));
    printMapperGeneric();

    printBlank(1);
    Iterable<String> iterable = Splitter.on(",").omitEmptyStrings().trimResults().split(options.getDomainInterfaces());

    printClassImplements(Lists.newArrayList(iterable));
    printOpenBrace(0, 2);

    printUserSourceCode();

    printCloseBrace(0, 2);
  }

  public void printMapperGeneric() {
    sourceBuf.append("<");
    sourceBuf.append(domainName);
    sourceBuf.append(", ");
    sourceBuf.append(pkType.getName());
    sourceBuf.append(">");
  }
}
