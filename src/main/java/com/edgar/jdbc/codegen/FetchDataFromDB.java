package com.edgar.jdbc.codegen;

import com.edgar.jdbc.codegen.db.Column;
import com.edgar.jdbc.codegen.db.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Edgar on 2016/4/1.
 *
 * @author Edgar  Date 2016/4/1
 */
public class FetchDataFromDB {

  private static final Logger LOGGER = LoggerFactory.getLogger(FetchDataFromDB.class);

  private CodegenOptions options;

  public FetchDataFromDB(CodegenOptions options) {
    this.options = options;
  }

  public List<Table> fetchTablesFromDb() {
    List<Table> tables = new ArrayList<>();
    Connection conn = null;
    try {
      conn = this.getConnection();
      DatabaseMetaData metaData = conn.getMetaData();

      if (metaData != null) {

        //读取table
        ResultSet rset = metaData.getTables(null, null, null, new String[]{"TABLE"});
        while (rset.next()) {
          String tableName = rset.getString("TABLE_NAME");
          LOGGER.info("Found Table:" + tableName);
          LOGGER.debug("DB Product name:{}", metaData.getDatabaseProductName());
          LOGGER.debug("DB Product version:{}", metaData.getDatabaseProductVersion());

          // for each table create the classes
          Table table = this.createTable(metaData, tableName);
          if (this.ignoreTable(tableName.toLowerCase())) {
            table.setIgnore(true);
          }
          tables.add(table);
        }
      }
    } catch (Exception e) {
      LOGGER.error("Error occcured during code generation." + e);
      e.printStackTrace();
    } finally {
      if (conn != null) {
        try {
          conn.close();
        } catch (Exception e) {
          LOGGER.warn("Error closing db connection.{}", e);
        }
      }
    }
    return tables;
  }

  private boolean ignoreTable(String tableName) {

    // first do a actual match
    if (this.options.getIgnoreTableList().contains(tableName.toLowerCase())) {
      return true;
    }
    // do a startswith check
    for (String ignoreStartsWithPattern : this.options.getIgnoreTableStartsWithPattern()) {
      if (tableName.startsWith(ignoreStartsWithPattern)) {
        return true;
      }
    }
    // do a startswith check
    for (String ignoreEndsWithPattern : this.options.getIgnoreTableEndsWithPattern()) {
      if (tableName.endsWith(ignoreEndsWithPattern)) {
        return true;
      }
    }
    return false;
  }

  private Connection getConnection() throws SQLException {
    Connection conn = null;
    LOGGER.info("Trying to connect to db using the properties...");
    String userName = options.getUsername();
    String password = options.getPassword();
    LOGGER.info(
            "Connecting to database at:[" + options.getJdbcUrl() + "]" + " with username/password:["
            +
            userName + "/" + password + "]");
    Properties connProps = new Properties();
    if (userName == null && password == null) {
      conn = DriverManager.getConnection(options.getJdbcUrl());
    } else {
      connProps.put("user", userName);
      connProps.put("password", password);
      conn = DriverManager.getConnection(options.getJdbcUrl(), connProps);
    }

    LOGGER.info("Connected to database");
    return conn;
  }

  private Table createTable(DatabaseMetaData metaData, String tableName) throws Exception {

    Table table = Table.create(tableName);
    Set<String> pks = new HashSet<>();
    //主键
    ResultSet pkSet = metaData.getPrimaryKeys(null, null, tableName);
    while (pkSet.next()) {
      String pkColName = pkSet.getString("COLUMN_NAME").toLowerCase();
      String pkName = pkSet.getString("PK_NAME").toLowerCase();
      String keySeq = pkSet.getString("KEY_SEQ").toLowerCase();
      pks.add(keySeq);
      LOGGER.debug("PK:ColName:{}, PKName:{}, Key Seq:{}", new Object[]{pkColName, pkName,
              keySeq});
    }

    //字段
    ResultSet cset = metaData.getColumns(null, null, tableName, null);
    while (cset.next()) {
      Column column = createColumn(cset, pks);
      table.addColumn(column);
      LOGGER.debug("Found Column:" + column);
    }
    return table;
  }

  private Column createColumn(ResultSet cset, Set<String> pks) throws SQLException {
    Column.ColumnBuilder builder = Column.builder();

    String colName = cset.getString("COLUMN_NAME").toLowerCase();
    builder.setName(colName);
    int colSize = cset.getInt("COLUMN_SIZE");
    builder.setSize(colSize);

    String defaultValue = cset.getString("COLUMN_DEF");
    builder.setDefaultValue(defaultValue);

    String nullable = cset.getString("IS_NULLABLE");
    boolean isNullable = false;
    if ("YES".equalsIgnoreCase(nullable)) {
      builder.setNullable(true);
    } else {
      builder.setNullable(false);
    }

    String autoIncable = cset.getString("IS_AUTOINCREMENT");
    boolean isAutoInc = false;
    if ("YES".equalsIgnoreCase(autoIncable)) {
      builder.setAutoInc(true);
    } else {
      builder.setAutoInc(false);
    }

    if (pks.contains(colName)) {
      builder.setPrimary(true);
    }

    int type = cset.getInt("DATA_TYPE");
    builder.setType(type);

    //属性、方法
    //TODO 通配符
    if (this.options.getIgnoreColumnList().contains(colName)) {
      builder.setIgnore(true);
    }
    return builder.build();
  }
}
