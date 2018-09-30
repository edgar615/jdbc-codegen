package com.github.edgar615.jdbc.codegen.db;

import com.github.edgar615.jdbc.codegen.gen.CodegenOptions;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 查询数据库属性.
 *
 * @author Edgar  Date 2017/5/17
 */
public class DBFetcher {

  private static final Logger LOGGER = LoggerFactory.getLogger(DBFetcher.class);

  private final CodegenOptions options;

  public DBFetcher(CodegenOptions options) {
    this.options = options;
  }

  private Connection getConnection() throws SQLException {
    Connection conn;
    String userName = options.getUsername();
    String password = options.getPassword();
    LOGGER.info(
        "Connecting to database at:[" + options.getJdbcUrl() + "]" + " with username/password:["
            +
            userName + "/" + password + "]");
    if (userName == null && password == null) {
      conn = DriverManager.getConnection(options.getJdbcUrl());
    } else {
      Properties connProps = new Properties();
      connProps.put("user", userName);
      connProps.put("password", password);
      connProps.setProperty("remarks", "true"); //设置可以获取remarks信息
      connProps.setProperty("useInformationSchema", "true");//设置可以获取tables remarks信息
      conn = DriverManager.getConnection(options.getJdbcUrl(), connProps);
    }
    LOGGER.info("Connected to database");
    return conn;
  }

  public List<Table> fetchTablesFromDb() {
    List<Table> tables = new ArrayList<>();
    Connection conn = null;
    try {
      conn = this.getConnection();
      DatabaseMetaData dbmd = conn.getMetaData();
      printSchemasInfo(dbmd);
      printDBinfo(dbmd);

      if (dbmd != null) {
        /**
         * 读取table
         * 获取给定类别中使用的表的描述。
         * 方法原型:ResultSet getTables(String catalog,String schemaPattern,String tableNamePattern,
         * String[] types);
         * catalog - 表所在的类别名称;""表示获取没有类别的列,null表示获取所有类别的列。
         * schema - 表所在的模式名称(oracle中对应于Tablespace);""表示获取没有模式的列,null标识获取所有模式的列; 可包含单字符通配符("_"),
         * 或多字符通配符("%");
         * tableNamePattern - 表名称;可包含单字符通配符("_"),或多字符通配符("%");
         * types - 表类型数组; "TABLE"、"VIEW"、"SYSTEM TABLE"、"GLOBAL TEMPORARY"、"LOCAL
         * TEMPORARY"、"ALIAS" 和 "SYNONYM";null表示包含所有的表类型;可包含单字符通配符("_"),或多字符通配符("%");
         */
        ResultSet rset =
            dbmd.getTables(options.getDatabase(), null, "%", new String[]{"TABLE"});
        while (rset.next()) {
//TABLE_CAT表类别(可为null)
//TABLE_SCHEM 表模式（可能为空）
//  TABLE_NAME表名
//  TABLE_TYPE表类型,典型的类型是 "TABLE"、"VIEW"、"SYSTEM TABLE"、"GLOBAL TEMPORARY"、"LOCAL
// TEMPORARY"、"ALIAS" 和 "SYNONYM"。
//  REMARKS表备注
          String tableName = rset.getString("TABLE_NAME");
          String tableType = rset.getString("TABLE_TYPE");
          String remarks = rset.getString("REMARKS");
          LOGGER.info("Found {}:{}, {}", tableType, tableName, remarks);
          boolean ignore = ignoreTable(tableName);
          if (!options.getTableList().isEmpty()) {
            if (!options.getTableList().contains(tableName)) {
              ignore = true;
            }
          }
          if (ignore) {
            LOGGER.info("Ignore {}", tableName);
          } else {
            Table table = Table.create(tableName, remarks);
            printIndexInfo(dbmd, table);
            fetchColumns(dbmd, table);
            tables.add(table);
          }
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

  private void printDBinfo(DatabaseMetaData dbmd) throws SQLException {
    LOGGER.info("DB Product name:{}", dbmd.getDatabaseProductName());
    LOGGER.info("DB Product version:{}", dbmd.getDatabaseProductVersion());

    LOGGER.info("DB User:{}", dbmd.getUserName());
    LOGGER.info("DB URL:{}", dbmd.getURL());
    LOGGER.info("DB Readonly:{}", dbmd.isReadOnly());
    LOGGER.info("DB Driver Name:{}", dbmd.getDriverName());
    LOGGER.info("DB Driver version:{}", dbmd.getDriverVersion());
  }

  private Table fetchColumns(DatabaseMetaData metaData, Table table) throws
      Exception {

    Set<String> pks = new HashSet<>();
    /**主键
     * 获取对给定表的主键列的描述
     * 方法原型:ResultSet getPrimaryKeys(String catalog,String schema,String table);
     * catalog - 表所在的类别名称;""表示获取没有类别的列,null表示获取所有类别的列。
     * schema - 表所在的模式名称(oracle中对应于Tablespace);""表示获取没有模式的列,null标识获取所有模式的列; 可包含单字符通配符("_"),
     * 或多字符通配符("%");
     * table - 表名称;可包含单字符通配符("_"),或多字符通配符("%");
     */
    ResultSet pkSet = metaData.getPrimaryKeys(options.getDatabase(), null, table.getName());
    while (pkSet.next()) {
//      TABLE_CAT表类别(可为null)
//      TABLE_SCHEM 表模式（可能为空）,在oracle中获取的是命名空间,其它数据库未知
//      TABLE_NAME 表名
//      COLUMN_NAME列名
//      KEY_SEQ 序列号(主键内值1表示第一列的主键，值2代表主键内的第二列)
//     PK_NAME 主键名称
      String pkColName = pkSet.getString("COLUMN_NAME").toLowerCase();
      String pkName = pkSet.getString("PK_NAME").toLowerCase();
      String keySeq = pkSet.getString("KEY_SEQ").toLowerCase();
      pks.add(pkColName);
      LOGGER.debug("PK:ColName:{}, PKName:{}, Key Seq:{}", new Object[]{pkColName, pkName,
          keySeq});
    }
    if (pks.size() != 1) {
      LOGGER.error("should be only 1 pk,but:" + pks.size());
      throw new RuntimeException("should be only 1 pk,but:" + pks.size());
    }

    /**
     * 字段
     * 获取可在指定类别中使用的表列的描述。
     * 方法原型:ResultSet getColumns(String catalog,String schemaPattern,String tableNamePattern,
     * String columnNamePattern)
     * catalog - 表所在的类别名称;""表示获取没有类别的列,null表示获取所有类别的列。
     * schema - 表所在的模式名称(oracle中对应于Tablespace);""表示获取没有模式的列,null标识获取所有模式的列; 可包含单字符通配符("_"),
     * 或多字符通配符("%");
     * tableNamePattern - 表名称;可包含单字符通配符("_"),或多字符通配符("%");
     * columnNamePattern - 列名称; ""表示获取列名为""的列(当然获取不到);null表示获取所有的列;可包含单字符通配符("_"),或多字符通配符("%");
     */
    ResultSet cset = metaData.getColumns(options.getDatabase(), null, table.getName(), "%");
    while (cset.next()) {
      Column column = createColumn(table, cset, pks);
      table.addColumn(column);
      LOGGER.debug("Found Column:" + column);
    }
    return table;
  }

  private Column createColumn(Table table, ResultSet cset, Set<String> pks) throws SQLException {
    Column.ColumnBuilder builder = Column.builder();

    /**
     * 获取可在指定类别中使用的表列的描述。
     * 方法原型:ResultSet getColumns(String catalog,String schemaPattern,String tableNamePattern,
     * String columnNamePattern)
     * catalog - 表所在的类别名称;""表示获取没有类别的列,null表示获取所有类别的列。
     * schema - 表所在的模式名称(oracle中对应于Tablespace);""表示获取没有模式的列,null标识获取所有模式的列; 可包含单字符通配符("_"),
     * 或多字符通配符("%");
     * tableNamePattern - 表名称;可包含单字符通配符("_"),或多字符通配符("%");
     * columnNamePattern - 列名称; ""表示获取列名为""的列(当然获取不到);null表示获取所有的列;可包含单字符通配符("_"),或多字符通配符("%");
     */

    //TABLE_CAT表类别（可能为空）
    //TABLE_SCHEM表模式（可能为空）,在oracle中获取的是命名空间,其它数据库未知
    //TABLE_NAME表名
    //COLUMN_NAME列名
    //DATA_TYPE对应的java.sql.Types的SQL类型(列类型ID)
    //TYPE_NAMEjava.sql.Types类型名称(列类型名称)
    //COLUMN_SIZE列大小
    //DECIMAL_DIGITS小数位数
    //NUM_PREC_RADIX基数（通常是10或2） --未知
    //NULLABLE  是否允许为null 0-不允许，1-运行 2-不确定
    //REMARKS列描述
    //COLUMN_DEF默认值
    //CHAR_OCTET_LENGTH 对于 char 类型，该长度是列中的最大字节数
    //ORDINAL_POSITION 表中列的索引（从1开始）
    //IS_NULLABLE  ISO规则用来确定某一列的是否可为空 0-不允许，1-运行 2-不确定
//IS_AUTOINCREMENT 指示此列是否是自动递增 YES -是 NO-不是 空字符串-不确定
//    IS_GENERATEDCOLUMN 指示此列是否是虚拟列  YES -是 NO-不是 空字符串-不确定

    String colName = cset.getString("COLUMN_NAME").toLowerCase();
    builder.setName(colName);

    String remarks = cset.getString("REMARKS").toLowerCase();
    builder.setRemarks(remarks);

    //pk
    if (pks.contains(colName)) {
      builder.setPrimary(true);
    }

    int colSize = cset.getInt("COLUMN_SIZE");
    builder.setSize(colSize);

    String defaultValue = cset.getString("COLUMN_DEF");
    builder.setDefaultValue(defaultValue);

    String nullable = cset.getString("IS_NULLABLE");
    if ("YES".equalsIgnoreCase(nullable)) {
      builder.setNullable(true);
    } else {
      builder.setNullable(false);
    }

    String autoIncable = cset.getString("IS_AUTOINCREMENT");
    if ("YES".equalsIgnoreCase(autoIncable)) {
      builder.setAutoInc(true);
    } else {
      builder.setAutoInc(false);
    }
    String genColumn = cset.getString("IS_GENERATEDCOLUMN");
    if ("YES".equalsIgnoreCase(genColumn)) {
      builder.setGenColumn(true);
    } else {
      builder.setGenColumn(false);
    }

    if (pks.contains(colName)) {
      builder.setPrimary(true);
    }

    int type = cset.getInt("DATA_TYPE");
    builder.setType(type);

    if (ignoreColumn(colName)) {
      builder.setIgnore(true);
    }
    if (colName.equalsIgnoreCase(options.getVersions().get(table.getName()))) {
      builder.setVersion(true);
    }
    return builder.build();
  }

  private boolean ignoreColumn(String column) {
    // first do a actual match
    if (this.options.getIgnoreColumnList().contains(column.toLowerCase())) {
      return true;
    }
    // do a startswith check
    for (String ignoreStartsWithPattern : this.options.getIgnoreColumnStartsWithPattern()) {
      if (column.startsWith(ignoreStartsWithPattern)) {
        return true;
      }
    }
    // do a startswith check
    for (String ignoreEndsWithPattern : this.options.getIgnoreColumnEndsWithPattern()) {
      if (column.endsWith(ignoreEndsWithPattern)) {
        return true;
      }
    }
    return false;
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

  private void printSchemasInfo(DatabaseMetaData dbmd) throws Exception {
    ResultSet rs;
    rs = dbmd.getSchemas();
    while (rs.next()) {
      String tableSchem = rs.getString("TABLE_SCHEM");
      LOGGER.info("schema:{}", tableSchem);
    }
  }

  private void printIndexInfo(DatabaseMetaData dbmd, Table table) throws Exception {
    /**
     * 获取给定表的索引和统计信息的描述
     * 方法原型:ResultSet getIndexInfo(String catalog,String schema,String table,boolean unique,
     * boolean approximate)
     * catalog - 表所在的类别名称;""表示获取没有类别的列,null表示获取所有类别的列。
     * schema - 表所在的模式名称(oracle中对应于Tablespace);""表示获取没有模式的列,null标识获取所有模式的列; 可包含单字符通配符("_"),
     * 或多字符通配符("%");
     * table - 表名称;可包含单字符通配符("_"),或多字符通配符("%");
     * unique - 该参数为 true时,仅返回唯一值的索引; 该参数为 false时,返回所有索引;
     * approximate - 该参数为true时,允许结果是接近的数据值或这些数据值以外的值;该参数为 false时,要求结果是精确结果;
     */
    ResultSet rs = dbmd.getIndexInfo(options.getDatabase(), null, table.getName(), false, true);
    while (rs.next()) {
      String tableCat = rs.getString("TABLE_CAT");  //表类别(可为null)
      String tableSchemaName = rs.getString("TABLE_SCHEM");//表模式（可能为空）,在oracle中获取的是命名空间,其它数据库未知
      String tableName = rs.getString("TABLE_NAME");  //表名
      boolean nonUnique =
          rs.getBoolean("NON_UNIQUE");// 索引值是否可以不唯一,TYPE为 tableIndexStatistic时索引值为 false;
      String indexQualifier =
          rs.getString("INDEX_QUALIFIER");//索引类别（可能为空）,TYPE为 tableIndexStatistic 时索引类别为 null;
      String indexName = rs.getString("INDEX_NAME");//索引的名称 ;TYPE为 tableIndexStatistic 时索引名称为 null;
      /**
       * 索引类型：
       *  tableIndexStatistic - 此标识与表的索引描述一起返回的表统计信息
       *  tableIndexClustered - 此为集群索引
       *  tableIndexHashed - 此为散列索引
       *  tableIndexOther - 此为某种其他样式的索引
       */
      short type = rs.getShort("TYPE");//索引类型;
      short ordinalPosition =
          rs.getShort("ORDINAL_POSITION");//在索引列顺序号;TYPE为 tableIndexStatistic 时该序列号为零;
      String columnName = rs.getString("COLUMN_NAME");//列名;TYPE为 tableIndexStatistic时列名称为 null;
      String ascOrDesc = rs.getString(
          "ASC_OR_DESC");//列排序顺序:升序还是降序[A:升序; B:降序];如果排序序列不受支持,可能为 null;TYPE为
      // tableIndexStatistic时排序序列为 null;
      int cardinality =
          rs.getInt("CARDINALITY");   //基数;TYPE为 tableIndexStatistic 时,它是表中的行数;否则,它是索引中唯一值的数量。
      int pages = rs.getInt("PAGES"); //TYPE为 tableIndexStatisic时,它是用于表的页数,否则它是用于当前索引的页数。
      String filterCondition = rs.getString("FILTER_CONDITION"); //过滤器条件,如果有的话(可能为 null)。

      LOGGER.info("Index name:{}", indexName);
      LOGGER.info("Index NON_UNIQUE:{}", nonUnique);

    }
  }
}
