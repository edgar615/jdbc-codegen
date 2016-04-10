package com.edgar.jdbc.codegen;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Edgar on 2016/4/1.
 *
 * @author Edgar  Date 2016/4/1
 */
public class CodegenOptions {
  public static final String DEFAULT_SRC_FOLDER_PATH = "src";

  public static final String DEFAULT_RESOURCE_FOLDER_PATH = "src";

  //domain
  public static final String DEFAULT_DOMAIN_PACKAGE = "com.edgar.code.domain";

  public static final String DEFAULT_DOMAIN_INTERFACES = "";

  public static final String DEFAULT_DOMAIN_EXTEND = "";

  //mapper
  public static final String DEFAULT_MAPPER_PACKAGE = "com.edgar.code.mapper";

  public static final String DEFAULT_MAPPER_EXTENDS = "";

  public static final boolean DEFAULT_GEN_REPOSITORY_ANNOTATION = false;

  //xml
  public static final String DEFAULT_XML_PACKAGE = "com.edgar.code.mapper";

  //支持通配符,product, exa*, *e
  public static final String DEFAULT_IGNORE_TABLES = null;

  //支持通配符,product, exa*, *e
  public static final String DEFAULT_IGNORE_COLUMN = null;

  public static final String DEFAULT_VERSION_COLUMN = null;

  public static final String DEFAULT_JDBC_DRIVER = "com.mysql.jdbc.Driver";

  public static final String DEFAULT_JDBC_URL = "jdbc:mysql://localhost:3306/test";

  public static final String DEFAULT_USERNAME = "root";

  public static final String DEFAULT_PASSWORD = "";

  public static final boolean DEFAULT_JSR303 = false;

  public static final String DEFAULT_INSERT_GROUP = "javax.validation.groups.Default";

  public static final String DEFAULT_UPDATE_GROUP = "javax.validation.groups.Default";

  //忽略的字段
  private final List<String> ignoreColumnList = new ArrayList<String>();

  //使用前缀匹配忽略的表
  private final List<String> ignoreColumnStartsWithPattern = new ArrayList<String>();

  //使用后缀匹配忽略的表
  private final List<String> ignoreColumnEndsWithPattern = new ArrayList<String>();

  //忽略的表
  private final List<String> ignoreTableList = new ArrayList<String>();

  //使用前缀匹配忽略的表
  private final List<String> ignoreTableStartsWithPattern = new ArrayList<String>();

  //使用后缀匹配忽略的表
  private final List<String> ignoreTableEndsWithPattern = new ArrayList<String>();

  private String srcFolderPath;

  private String resourceFolderPath;

  private String domainPackage;

  private String domainInterfaces;

  private String domainExtend;

  private String mapperPackage;

  private String mapperExtends;

  private boolean genRepositoryAnnotation;

  private String xmlPackage;

  private String ignoreTablesStr;

  private String ignoreColumnsStr;

  private String versionColumn;

  private String driverClass;

  private String jdbcUrl;

  private String username;

  private String password;

  private boolean jsr303;

  private String insertGroup;

  private String updateGroup;

  /**
   * Default constructor
   */
  public CodegenOptions() {
    this.srcFolderPath = DEFAULT_SRC_FOLDER_PATH;
    this.resourceFolderPath = DEFAULT_RESOURCE_FOLDER_PATH;
    this.domainPackage = DEFAULT_DOMAIN_PACKAGE;
    this.domainInterfaces = DEFAULT_DOMAIN_INTERFACES;
    this.domainExtend = DEFAULT_DOMAIN_EXTEND;
    this.mapperPackage = DEFAULT_MAPPER_PACKAGE;
    this.mapperExtends = DEFAULT_MAPPER_EXTENDS;
    this.genRepositoryAnnotation = DEFAULT_GEN_REPOSITORY_ANNOTATION;
    this.xmlPackage = DEFAULT_XML_PACKAGE;
    this.ignoreTablesStr = DEFAULT_IGNORE_TABLES;
    this.ignoreColumnsStr = DEFAULT_IGNORE_COLUMN;
    this.versionColumn = DEFAULT_VERSION_COLUMN;
    this.driverClass = DEFAULT_JDBC_DRIVER;
    this.username = DEFAULT_USERNAME;
    this.password = DEFAULT_PASSWORD;
    this.jsr303 = DEFAULT_JSR303;
    this.insertGroup = DEFAULT_INSERT_GROUP;
    this.updateGroup = DEFAULT_UPDATE_GROUP;
    this.jdbcUrl = DEFAULT_JDBC_URL;

    setIgnoreTable();

    setIgnoreColumn();
  }

  public boolean isGenRepositoryAnnotation() {
    return genRepositoryAnnotation;
  }

  public void setGenRepositoryAnnotation(boolean genRepositoryAnnotation) {
    this.genRepositoryAnnotation = genRepositoryAnnotation;
  }

  public String getSrcFolderPath() {
    return srcFolderPath;
  }

  public CodegenOptions setSrcFolderPath(String srcFolderPath) {
    this.srcFolderPath = srcFolderPath;
    return this;
  }

  public String getJdbcUrl() {
    return jdbcUrl;
  }

  public CodegenOptions setJdbcUrl(String jdbcUrl) {
    this.jdbcUrl = jdbcUrl;
    return this;
  }

  public List<String> getIgnoreColumnList() {
    return ignoreColumnList;
  }

  public List<String> getIgnoreTableList() {
    return ignoreTableList;
  }

  public List<String> getIgnoreTableStartsWithPattern() {
    return ignoreTableStartsWithPattern;
  }

  public List<String> getIgnoreTableEndsWithPattern() {
    return ignoreTableEndsWithPattern;
  }

  public String getResourceFolderPath() {
    return resourceFolderPath;
  }

  public CodegenOptions setResourceFolderPath(String resourceFolderPath) {
    this.resourceFolderPath = resourceFolderPath;
    return this;
  }

  public String getDomainPackage() {
    return domainPackage;
  }

  public CodegenOptions setDomainPackage(String domainPackage) {
    this.domainPackage = domainPackage;
    return this;
  }

  public String getDomainInterfaces() {
    return domainInterfaces;
  }

  public CodegenOptions setDomainInterfaces(String domainInterfaces) {
    this.domainInterfaces = domainInterfaces;
    return this;
  }

  public String getDomainExtend() {
    return domainExtend;
  }

  public CodegenOptions setDomainExtend(String domainExtend) {
    this.domainExtend = domainExtend;
    return this;
  }

  public String getMapperPackage() {
    return mapperPackage;
  }

  public CodegenOptions setMapperPackage(String mapperPackage) {
    this.mapperPackage = mapperPackage;
    return this;
  }

  public String getMapperExtends() {
    return mapperExtends;
  }

  public CodegenOptions setMapperExtends(String mapperExtends) {
    this.mapperExtends = mapperExtends;
    return this;
  }

  public String getXmlPackage() {
    return xmlPackage;
  }

  public CodegenOptions setXmlPackage(String xmlPackage) {
    this.xmlPackage = xmlPackage;
    return this;
  }

  public String getIgnoreTablesStr() {
    return ignoreTablesStr;
  }

  public CodegenOptions setIgnoreTablesStr(String ignoreTablesStr) {
    this.ignoreTablesStr = ignoreTablesStr;
    this.setIgnoreTable();
    return this;
  }

  public String getIgnoreColumnsStr() {
    return ignoreColumnsStr;
  }

  public CodegenOptions setIgnoreColumnsStr(String ignoreColumnsStr) {
    this.ignoreColumnsStr = ignoreColumnsStr;
    this.setIgnoreColumn();
    return this;
  }

  public String getVersionColumn() {
    return versionColumn;
  }

  public CodegenOptions setVersionColumn(String versionColumn) {
    this.versionColumn = versionColumn;
    return this;
  }

  public String getDriverClass() {
    return driverClass;
  }

  public CodegenOptions setDriverClass(String driverClass) {
    this.driverClass = driverClass;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public CodegenOptions setUsername(String username) {
    this.username = username;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public CodegenOptions setPassword(String password) {
    this.password = password;
    return this;
  }

  public boolean isJsr303() {
    return jsr303;
  }

  public CodegenOptions setJsr303(boolean jsr303) {
    this.jsr303 = jsr303;
    return this;
  }

  public String getInsertGroup() {
    return insertGroup;
  }

  public CodegenOptions setInsertGroup(String insertGroup) {
    this.insertGroup = insertGroup;
    return this;
  }

  public String getUpdateGroup() {
    return updateGroup;
  }

  public CodegenOptions setUpdateGroup(String updateGroup) {
    this.updateGroup = updateGroup;
    return this;
  }

  private void setIgnoreTable() {
    if (!Strings.isNullOrEmpty(ignoreTablesStr)) {
      StringTokenizer strTok = new StringTokenizer(ignoreTablesStr, ",");
      while (strTok.hasMoreTokens()) {
        String token = strTok.nextToken().toLowerCase().trim();
        if (CharMatcher.anyOf("*").indexIn(token) == 0) {
          this.ignoreTableEndsWithPattern.add(token.substring(1, token.length()));
        } else if (CharMatcher.anyOf("*").lastIndexIn(token) == token.length() - 1) {
          this.ignoreTableStartsWithPattern.add(token.substring(0, token.length() - 1));
        } else {
          this.ignoreTableList.add(token);
        }
      }
    }
  }

  private void setIgnoreColumn() {

    if (!Strings.isNullOrEmpty(ignoreColumnsStr)) {
      StringTokenizer strTok = new StringTokenizer(ignoreColumnsStr, ",");
      while (strTok.hasMoreTokens()) {
        String token = strTok.nextToken().toLowerCase().trim();
        if (CharMatcher.anyOf("*").indexIn(token) == 0) {
          this.ignoreColumnStartsWithPattern.add(token.substring(1, token.length()));
        } else if (CharMatcher.anyOf("*").lastIndexIn(token) == token.length() - 1) {
          this.ignoreColumnEndsWithPattern.add(token.substring(0, token.length() - 1));
        } else {
          this.ignoreColumnList.add(token);
        }
      }
    }
  }
}
