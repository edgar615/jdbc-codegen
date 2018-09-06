package com.github.edgar615.jdbc.codegen.gen;

import com.github.edgar615.mysql.mapping.TableMappingOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Edgar on 2016/4/1.
 *
 * @author Edgar  Date 2016/4/1
 */
public class CodegenOptions extends TableMappingOptions {
  public static final String DEFAULT_SRC_FOLDER_PATH = "src";

  //domain
  public static final String DEFAULT_DOMAIN_PACKAGE = "com.github.edgar615.code.domain";

  private static final boolean DEFAULT_GEN_RULE = false;

  private String srcFolderPath = DEFAULT_SRC_FOLDER_PATH;

  private String domainPackage = DEFAULT_DOMAIN_PACKAGE;

  private boolean genRule = DEFAULT_GEN_RULE;

  private final Map<String, String> versions = new HashMap<>();

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

  @Override
  public CodegenOptions setHost(String host) {
    super.setHost(host);
    return this;
  }

  @Override
  public CodegenOptions setPort(int port) {
    super.setPort(port);
    return this;
  }

  @Override
  public CodegenOptions setDatabase(String database) {
    super.setDatabase(database);
    return this;
  }

  @Override
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

  @Override
  public CodegenOptions setIgnoreTablesStr(String ignoreTablesStr) {
    super.setIgnoreTablesStr(ignoreTablesStr);
    return this;
  }

  @Override
  public CodegenOptions addGenTable(String tableName) {
    super.addGenTable(tableName);
    return this;
  }

  @Override
  public CodegenOptions addGenTables(List<String> tableNames) {
    super.addGenTables(tableNames);
    return this;
  }

  @Override
  public CodegenOptions setIgnoreColumnsStr(String ignoreColumnsStr) {
    super.setIgnoreColumnsStr(ignoreColumnsStr);
    return this;
  }

  @Override
  public CodegenOptions setDriverClass(String driverClass) {
    super.setDriverClass(driverClass);
    return this;
  }

  @Override
  public CodegenOptions setUsername(String username) {
    super.setUsername(username);
    return this;
  }

  @Override
  public CodegenOptions setPassword(String password) {
    super.setPassword(password);
    return this;
  }

  public Map<String, String> getVersions() {
    return versions;
  }

  public CodegenOptions addVersion(String table, String column) {
    this.versions.put(table, column);
    return this;
  }

  public CodegenOptions addVersions(Map<String, String> versions) {
    this.versions.putAll(versions);
    return this;
  }
}
