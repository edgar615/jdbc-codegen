package com.github.edgar615.jdbc.codegen.gen;

/**
 * 用于生成DAO的参数.为了避免后期扩展，单独列出来.
 *
 * @author Edgar
 * @create 2018-08-31 19:31
 **/
public class MybatisOptions {

  private static final String DEFAULT_MAPPER_CLASS_PACKAGE = "com.github.edgar615.codegen.dao";
  private static final String DEFAULT_MAPPER_XML_PACKAGE = "com.github.edgar615.codegen.dao";
  private static final boolean DEFAULT_SUPPORT_SPRING = true;

  private String mapperClassPackage = DEFAULT_MAPPER_CLASS_PACKAGE;

  private String mapperXmlPackage = DEFAULT_MAPPER_XML_PACKAGE;

  private boolean supportSpring = DEFAULT_SUPPORT_SPRING;

  public String getMapperClassPackage() {
    return mapperClassPackage;
  }

  public MybatisOptions setMapperClassPackage(String mapperClassPackage) {
    this.mapperClassPackage = mapperClassPackage;
    return this;
  }

  public String getMapperXmlPackage() {
    return mapperXmlPackage;
  }

  public MybatisOptions setMapperXmlPackage(String mapperXmlPackage) {
    this.mapperXmlPackage = mapperXmlPackage;
    return this;
  }

  public boolean isSupportSpring() {
    return supportSpring;
  }

  public MybatisOptions setSupportSpring(boolean supportSpring) {
    this.supportSpring = supportSpring;
    return this;
  }
}
