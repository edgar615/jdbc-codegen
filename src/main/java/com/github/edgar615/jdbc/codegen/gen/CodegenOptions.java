package com.github.edgar615.jdbc.codegen.gen;

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

  //domain
  public static final String DEFAULT_DOMAIN_PACKAGE = "com.github.edgar615.code.domain";

  //支持通配符,product, exa*, *e
  public static final String DEFAULT_IGNORE_TABLES = null;

  //支持通配符,product, exa*, *e
  public static final String DEFAULT_IGNORE_COLUMN = null;

  public static final String DEFAULT_JDBC_DRIVER = "com.mysql.jdbc.Driver";

  public static final String DEFAULT_JDBC_URL = "jdbc:mysql://localhost:3306/test";

  public static final String DEFAULT_USERNAME = "root";

  public static final String DEFAULT_PASSWORD = "";

  private static final String DEFAULT_TABLE_NAME_PATTERN = null;

  private static final boolean DEFAULT_GEN_RULE = false;

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

  //只生成这些表
  private final List<String> tableList = new ArrayList<>();

  //查询的表名
  private String tableNamePattern = DEFAULT_TABLE_NAME_PATTERN;

  private String srcFolderPath = DEFAULT_SRC_FOLDER_PATH;

  private String domainPackage = DEFAULT_DOMAIN_PACKAGE;

  private String ignoreTablesStr = DEFAULT_IGNORE_TABLES;

  private String ignoreColumnsStr = DEFAULT_IGNORE_COLUMN;

  private String driverClass = DEFAULT_JDBC_DRIVER;

  private String jdbcUrl = DEFAULT_JDBC_URL;

  private String username = DEFAULT_USERNAME;

  private String password = DEFAULT_PASSWORD;

  private boolean genRule = DEFAULT_GEN_RULE;

  /**
   * Default constructor
   */
  public CodegenOptions() {
    setIgnoreTable();

    setIgnoreColumn();
  }

  public boolean isGenRule() {
    return genRule;
  }

  public CodegenOptions setGenRule(boolean genRule) {
    this.genRule = genRule;
    return this;
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

  public String getDomainPackage() {
    return domainPackage;
  }

  public CodegenOptions setDomainPackage(String domainPackage) {
    this.domainPackage = domainPackage;
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

  public CodegenOptions addGenTable(String tableName) {
    this.tableList.add(tableName);
    return this;
  }

  public List<String> getTableList() {
    return tableList;
  }

  public CodegenOptions addGenTables(List<String> tableNames) {
    this.tableList.addAll(tableNames);
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

  public List<String> getIgnoreColumnStartsWithPattern() {
    return ignoreColumnStartsWithPattern;
  }

  public List<String> getIgnoreColumnEndsWithPattern() {
    return ignoreColumnEndsWithPattern;
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

  public String getTableNamePattern() {
    return tableNamePattern;
  }

  public CodegenOptions setTableNamePattern(String tableNamePattern) {
    this.tableNamePattern = tableNamePattern;
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
          this.ignoreColumnEndsWithPattern.add(token.substring(1, token.length()));
        } else if (CharMatcher.anyOf("*").lastIndexIn(token) == token.length() - 1) {
          this.ignoreColumnStartsWithPattern.add(token.substring(0, token.length() - 1));
        } else {
          this.ignoreColumnList.add(token);
        }
      }
    }
  }
}
