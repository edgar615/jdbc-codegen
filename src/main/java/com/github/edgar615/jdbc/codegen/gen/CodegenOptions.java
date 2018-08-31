package com.github.edgar615.jdbc.codegen.gen;

import com.github.edgar615.mysql.mapping.TableMappingOptions;

import java.util.List;

/**
 * Created by Edgar on 2016/4/1.
 *
 * @author Edgar  Date 2016/4/1
 */
public class CodegenOptions extends TableMappingOptions {
  private static final String DEFAULT_SRC_FOLDER_PATH = "src";

  //domain
  private static final String DEFAULT_DOMAIN_PACKAGE = "com.github.edgar615.codegen.domain";

  private static final boolean DEFAULT_GEN_RULE = false;

  private String srcFolderPath = DEFAULT_SRC_FOLDER_PATH;

  private String domainPackage = DEFAULT_DOMAIN_PACKAGE;

  private boolean genRule = DEFAULT_GEN_RULE;

  private boolean genMybatis = false;

  private MybatisOptions mybatisOptions;

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

  public boolean isGenMybatis() {
    return genMybatis;
  }

  public CodegenOptions setGenMybatis(MybatisOptions options) {
    this.mybatisOptions = options;
    this.genMybatis = true;
    return this;
  }

  public MybatisOptions getMybatisOptions() {
    return mybatisOptions;
  }

  public String getSrcFolderPath() {
    return srcFolderPath;
  }

  public CodegenOptions setSrcFolderPath(String srcFolderPath) {
    this.srcFolderPath = srcFolderPath;
    return this;
  }

  public CodegenOptions setHost(String host) {
    super.setHost(host);
    return this;
  }

  public CodegenOptions setPort(int port) {
    super.setPort(port);
    return this;
  }

  public CodegenOptions setDatabase(String database) {
    super.setDatabase(database);
    return this;
  }

  public CodegenOptions setJdbcArg(String jdbcArg) {
    super.setJdbcArg(jdbcArg);
    return this;
  }


  public String getDomainPackage() {
    return domainPackage;
  }

  public CodegenOptions setDomainPackage(String domainPackage) {
    this.domainPackage = domainPackage;
    return this;
  }

  public CodegenOptions setIgnoreTablesStr(String ignoreTablesStr) {
    super.setIgnoreTablesStr(ignoreTablesStr);
    return this;
  }

  public CodegenOptions addGenTable(String tableName) {
    super.addGenTable(tableName);
    return this;
  }

  public CodegenOptions addGenTables(List<String> tableNames) {
    super.addGenTables(tableNames);
    return this;
  }

  public CodegenOptions setIgnoreColumnsStr(String ignoreColumnsStr) {
    super.setIgnoreColumnsStr(ignoreColumnsStr);
    return this;
  }


  public CodegenOptions setDriverClass(String driverClass) {
    super.setDriverClass(driverClass);
    return this;
  }

  public CodegenOptions setUsername(String username) {
    super.setUsername(username);
    return this;
  }

  public CodegenOptions setPassword(String password) {
    super.setPassword(password);
    return this;
  }

}
