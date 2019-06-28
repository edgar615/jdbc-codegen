package com.github.edgar615.jdbc.codegen.gen;

/**
 * 用于生成DAO的参数.为了避免后期扩展，单独列出来.
 *
 * @author Edgar
 * @create 2018-08-31 19:31
 **/
public class MybatisOptions {

  private static final String DEFAULT_MAPPER_CLASS_PACKAGE = "com.github.edgar615.codegen.dao";
  private static final String DEFAULT_XML_FOLDER = "src";
  private static final boolean DEFAULT_SUPPORT_SPRING = true;
  private static final boolean DEFAULT_CACHE_WILDCARD_EVICT = true;

  private String mapperClassPackage = DEFAULT_MAPPER_CLASS_PACKAGE;

  private boolean supportSpring = DEFAULT_SUPPORT_SPRING;

  private boolean cacheWildcardEvict = DEFAULT_CACHE_WILDCARD_EVICT;

  private String xmlFolderPath = DEFAULT_XML_FOLDER;

  public String getMapperClassPackage() {
    return mapperClassPackage;
  }

  public String getXmlFolderPath() {
    return xmlFolderPath;
  }

  public MybatisOptions setXmlFolderPath(String xmlFolderPath) {
    this.xmlFolderPath = xmlFolderPath;
    return this;
  }

  public MybatisOptions setMapperClassPackage(String mapperClassPackage) {
    this.mapperClassPackage = mapperClassPackage;
    return this;
  }

  public boolean isSupportSpring() {
    return supportSpring;
  }

  public MybatisOptions setSupportSpring(boolean supportSpring) {
    this.supportSpring = supportSpring;
    return this;
  }

  public boolean isCacheWildcardEvict() {
    return cacheWildcardEvict;
  }

  public MybatisOptions setCacheWildcardEvict(boolean cacheWildcardEvict) {
    this.cacheWildcardEvict = cacheWildcardEvict;
    return this;
  }
}
