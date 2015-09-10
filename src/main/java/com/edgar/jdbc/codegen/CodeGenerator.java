/**
 *
 * Copyright 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * @author Kalyan Mulampaka
 */
package com.edgar.jdbc.codegen;

import com.edgar.jdbc.codegen.util.CodeGenUtil;
import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Code generator class which creates Java bean classes and interfaces for all
 * the tables in the input database schema. Reads the properties file named
 * generator.properties.
 *删除了对外键的生成，因为在大多数的应用中，都不会考虑到外键
 * @author Kalyan Mulampaka， Edgar
 */
public class CodeGenerator {
    final static Logger logger = LoggerFactory.getLogger(CodeGenerator.class);
    private Properties properties;
    //忽略的字段
    private List<String> ignoreColumnList = new ArrayList<String>();
    //更新时忽略的字段
    private List<String> ignoreUpdatedColumnList = new ArrayList<String>();
    //乐观锁字段
    private List<String> optimisticLockColumnList = new ArrayList<String>();
    //忽略的表
    private List<String> ignoreTableList = new ArrayList<String>();
    //使用前缀匹配忽略的表
    private List<String> ignoreTableStartsWithPattern = new ArrayList<String>();
    //使用后缀匹配忽略的表
    private List<String> ignoreTableEndsWithPattern = new ArrayList<String>();

    private String propertiesFile;

    private String srcFolderPath;
    private String domainPackageName;
    private String dbPackageName;
    private String mapperPackageName;
    private String rootResourceFolderPath;
    private String mapperXmlPackgeName;
    private String rootFolderPath;
    private boolean generateJsr303Annotations = false;

    public CodeGenerator() {

    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getPropertiesFile() {
        return this.propertiesFile;
    }

    public void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    private void loadProperties() throws Exception {
        if (Strings.isNullOrEmpty(this.propertiesFile)) {
            logger.error("Properties file is not set");
            throw new Exception("Properties file is not set");
        }
        logger.info("Loading properties from file:" + propertiesFile);
        File f = new File(this.propertiesFile);
        if (!f.exists()) {
            throw new Exception(this.propertiesFile + " file does not exist");
        }
        FileInputStream propFile = new FileInputStream(propertiesFile);
        this.properties = new Properties();
        this.properties.load(propFile);
        logger.debug("Loaded properties:" + this.properties);
        propFile.close();

    }

    private void setProperties() throws Exception {
        if (this.properties == null || this.properties.isEmpty()) {
            throw new Exception("Properties are not set.");
        }
        srcFolderPath = this.properties.getProperty("src.folder.path");
        logger.info("Generated Sources will be in folder:{}", srcFolderPath);
        domainPackageName = this.properties.getProperty("domain.package.name");
        dbPackageName = this.properties.getProperty("repository.db.package.name");
        mapperPackageName = this.properties.getProperty("repository.package.name");
        rootResourceFolderPath = this.properties.getProperty("resource.folder.path");
        mapperXmlPackgeName = this.properties.getProperty("mapper.xml.package.name");
        rootFolderPath = this.properties.getProperty("src.folder.path");

        String generateJsr303AnnotationsStr = this.properties.getProperty("generate.jsr303.annotations");

        if (!Strings.isNullOrEmpty(generateJsr303AnnotationsStr)) {
            generateJsr303Annotations = Boolean.parseBoolean(generateJsr303AnnotationsStr);
        }

        // 忽略的字段
        String ignoreColumnListStr = this.properties.getProperty("ignore.columnlist");
        if (!Strings.isNullOrEmpty(ignoreColumnListStr)) {
            StringTokenizer strTok = new StringTokenizer(ignoreColumnListStr, ",");
            while (strTok.hasMoreTokens()) {
                this.ignoreColumnList.add(strTok.nextToken().toLowerCase().trim());
            }
            logger.info("Ignore column list:{}", this.ignoreColumnList);
        }

        // 更新和插入忽略的字段
        String ignoreUpdatedColumnListStr = this.properties.getProperty("ignore.updated.columnlist");
        if (!Strings.isNullOrEmpty(ignoreUpdatedColumnListStr)) {
            StringTokenizer strTok = new StringTokenizer(ignoreUpdatedColumnListStr, ",");
            while (strTok.hasMoreTokens()) {
                this.ignoreUpdatedColumnList.add(strTok.nextToken().toLowerCase().trim());
            }
            logger.info("Ignore updated column list:{}", this.ignoreUpdatedColumnList);
        }

        // 乐观锁字段
        String optimisticLockColumnListStr = this.properties.getProperty("optimistic.lock.columnlist");
        if (!Strings.isNullOrEmpty(optimisticLockColumnListStr)) {
            StringTokenizer strTok = new StringTokenizer(optimisticLockColumnListStr, ",");
            while (strTok.hasMoreTokens()) {
                this.optimisticLockColumnList.add(strTok.nextToken().toLowerCase().trim());
            }
            logger.info("OptimisticLock updated column list:{}", this.optimisticLockColumnList);
        }

        //忽略的table
        String ignoreTableListStr = this.properties.getProperty("ignore.tablelist");
        if (!Strings.isNullOrEmpty(ignoreTableListStr)) {
            StringTokenizer strTok = new StringTokenizer(ignoreTableListStr, ",");
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
            logger.info("Ignore table list:{}", this.ignoreTableList);
            logger.info("Ignore table Starts with pattern:{}", this.ignoreTableStartsWithPattern);
            logger.info("Ignore table Ends with pattern:{}", this.ignoreTableEndsWithPattern);
        }

    }

    public void generate() {
        Connection conn = null;
        try {
            //加载属性文件
            this.loadProperties();
            this.setProperties();
            conn = this.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();

            if (metaData != null) {

                //创建包
                CodeGenUtil.createPackage(srcFolderPath, domainPackageName);
//				CodeGenUtil.createPackage (srcFolderPath, dbPackageName);
                CodeGenUtil.createPackage(srcFolderPath, mapperPackageName);
                CodeGenUtil.createPackage(rootResourceFolderPath, mapperXmlPackgeName);

                //读取table
                ResultSet rset = metaData.getTables(null, null, null, new String[]{"TABLE"});
                while (rset.next()) {
                    String tableName = rset.getString("TABLE_NAME");
                    logger.info("Found Table:" + tableName);
                    if (this.ignoreTable(tableName.toLowerCase())) {
                        logger.info("Table:{} is in the ignore table list, not generating code for this table.", tableName);
                        continue;
                    }
                    logger.debug("DB Product name:{}", metaData.getDatabaseProductName());
                    logger.debug("DB Product version:{}", metaData.getDatabaseProductVersion());

                    // for each table create the classes
                    this.createClasses(metaData, tableName);

                }
            }
        } catch (Exception e) {
            logger.error("Error occcured during code generation." + e);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    logger.warn("Error closing db connection.{}", e);
                }
            }
        }
    }

    private boolean ignoreTable(String tableName) {

        // first do a actual match
        if (this.ignoreTableList.contains(tableName.toLowerCase())) {
            return true;
        }
        // do a startswith check
        for (String ignoreStartsWithPattern : this.ignoreTableStartsWithPattern) {
            if (tableName.startsWith(ignoreStartsWithPattern)) {
                return true;
            }
        }
        // do a startswith check
        for (String ignoreEndsWithPattern : this.ignoreTableEndsWithPattern) {
            if (tableName.endsWith(ignoreEndsWithPattern)) {
                return true;
            }
        }
        return false;
    }

    private void createClasses(DatabaseMetaData metaData, String tableName) throws Exception {

        String generateJsr303AnnotationsStr = this.properties.getProperty("generate.jsr303.annotations");
        boolean generateJsr303Annotations = false;
        if (!Strings.isNullOrEmpty(generateJsr303AnnotationsStr)) {
            generateJsr303Annotations = Boolean.parseBoolean(generateJsr303AnnotationsStr);
        }

        List<Field> fields = new ArrayList<Field>();
        List<Field> dbFields = new ArrayList<Field>();
        List<Method> methods = new ArrayList<Method>();
        String humpTableName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName.toLowerCase());
        DomainClass domainClass = new DomainClass();
        domainClass.setDbProductName(metaData.getDatabaseProductName());
        domainClass.setDbProductVersion(metaData.getDatabaseProductVersion());
        domainClass.setName(humpTableName);
        domainClass.setRootFolderPath(rootFolderPath);
        domainClass.setPackageName(domainPackageName);
        domainClass.setFields(fields);
        domainClass.setMethods(methods);
        domainClass.setGenerateJsr303Annotations(generateJsr303Annotations);

        if (generateJsr303Annotations) {
            String insertGrpClass = this.properties.getProperty("insert.group.class");
            // TODO
            if (!Strings.isNullOrEmpty(insertGrpClass)) {
                String[] classes = Iterables.toArray(Splitter.on(",").trimResults().split(insertGrpClass), String.class);
                for (String c : classes) {
                    if (!domainClass.getImports().contains(c.trim()))
                        domainClass.getImports().add(c.trim());
                    String[] classNameTokens = Iterables.toArray(Splitter.on(".").split(c.trim()), String.class);
                    // add only the class name and the FQ class name since the import is added
                    domainClass.getJsr303InsertGroups().add(classNameTokens[classNameTokens.length - 1]);
                }
            }
            // TODO
            String updateGrpClass = this.properties.getProperty("update.group.class");
            if (!Strings.isNullOrEmpty(updateGrpClass)) {
                String[] classes = Iterables.toArray(Splitter.on(",").trimResults().split(updateGrpClass), String.class);
                for (String c : classes) {
                    if (!domainClass.getImports().contains(c.trim()))
                        domainClass.getImports().add(c.trim());
                    String[] classNameTokens = Iterables.toArray(Splitter.on(".").split(c.trim()), String.class);
                    // add only the class name and the FQ class name since the import is added
                    domainClass.getJsr303UpdateGroups().add(classNameTokens[classNameTokens.length - 1]);
                }
            }
        }

        // create the db class
        DBClass dbClass = new DBClass();
        dbClass.getImports().add(domainPackageName + "." + domainClass.getName());
        dbClass.setName(humpTableName);
        dbClass.setRootFolderPath(rootFolderPath);
        dbClass.setPackageName(dbPackageName);
        dbClass.setFields(dbFields);

        //mapper xml
        MapperXmlClass mapperXmlClass = new MapperXmlClass();
        mapperXmlClass.setName(humpTableName);
        mapperXmlClass.setTableName(tableName);
        mapperXmlClass.setRootFolderPath(rootResourceFolderPath);
        mapperXmlClass.setPackageName(mapperXmlPackgeName);
        mapperXmlClass.setFields(dbFields);
        mapperXmlClass.setMapperPackageName(mapperPackageName);
        mapperXmlClass.setIgnoreUpdatedColumnListStr(ignoreUpdatedColumnList);

        //创建MyBatis的Mapper
        MapperClass mapperClass = new MapperClass();
        mapperClass.createLogger();
        mapperClass.getImports().add(domainPackageName + "." + domainClass.getName());
        mapperClass.setName(humpTableName);
        mapperClass.setRootFolderPath(rootFolderPath);
        mapperClass.setPackageName(mapperPackageName);
        //主键
        ResultSet pkSet = metaData.getPrimaryKeys(null, null, tableName);
        while (pkSet.next()) {
            String pkColName = pkSet.getString("COLUMN_NAME").toLowerCase();
            String pkName = pkSet.getString("PK_NAME").toLowerCase();
            String keySeq = pkSet.getString("KEY_SEQ").toLowerCase();
            domainClass.getPkeys().put(pkColName, null);
            logger.debug("PK:ColName:{}, PKName:{}, Key Seq:{}", new Object[]{pkColName, pkName, keySeq});
        }

        mapperClass.setPkeys(domainClass.getPkeys());
        dbClass.setPkeys(domainClass.getPkeys());
        mapperXmlClass.setPkeys(domainClass.getPkeys());

        //字段
        ResultSet cset = metaData.getColumns(null, null, tableName, null);
        while (cset.next()) {
            String colName = cset.getString("COLUMN_NAME").toLowerCase();
            logger.debug("Found Column:" + colName);
            int colSize = cset.getInt("COLUMN_SIZE");
            logger.debug("Column size:{}", colSize);

            String defaultValue = cset.getString("COLUMN_DEF");
            logger.debug("Column Default value:{}", defaultValue);
            String nullable = cset.getString("IS_NULLABLE");
            boolean isNullable = false;
            logger.debug("Is nullable:{}", nullable);
            if ("YES".equalsIgnoreCase(nullable)) {
                logger.debug("{} is nullable", colName);
                isNullable = true;
            } else {
                logger.debug("{} is not nullable", colName);
                isNullable = false;
            }

            //根据字段类型映射属性类型
            Parameter parameter = getParameter(domainClass, dbClass, cset, colName);

            String humpName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, colName);
            //add to db fields only
            Field dbField = new Field();
            dbFields.add(dbField);
            dbField.setColName(colName);
            dbField.setHumpName(humpName);
            dbField.setType(parameter.getType());

            //属性、方法
            if (!this.ignoreColumnList.contains(colName)) {
                Method method = new Method();
                methods.add(method);
                method.setName(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, humpName));
                method.setParameter(parameter);

                Field field = new Field();
                fields.add(field);
                field.setNullable(isNullable);
                field.setSize(colSize);
                field.setColName(colName);
                field.setHumpName(humpName);
                field.setType(parameter.getType());
                field.setDefaultValue(defaultValue);
                field.setPrimitive(parameter.getType().isPrimitive());

                boolean isPkCol = domainClass.getPkeys().containsKey(colName);
                if (isPkCol) {
                    domainClass.getPkeys().put(colName, parameter.getType());
                    logger.debug("Found pk col:{} , type:{}", colName, parameter.getType().getName());

                }
                //判断mapperXmlClass的乐观锁字段
                if (optimisticLockColumnList.contains(colName)) {
                    mapperXmlClass.addOptimisticLockColumn(colName);
                }
            } else {
                logger.debug("ColName:{} is in ignore column list, so not adding to domain class", colName);
            }
        }

        domainClass.createFile();
//		dbClass.createFile ();
        mapperClass.createFile();
        mapperXmlClass.createFile();
    }


    private Parameter getParameter(DomainClass domainClass, DBClass dbClass, ResultSet cset, String colName) throws SQLException {
        int type = cset.getInt("DATA_TYPE");
        logger.debug("Column DataType:" + type);

        Parameter parameter = null;
        if ((type == Types.VARCHAR) || (type == Types.LONGVARCHAR) || (type == Types.CLOB)) {
            parameter = new Parameter(colName, ParameterType.STRING);
        } else if (type == Types.BIGINT) {
            parameter = new Parameter(colName, ParameterType.LONG);
        } else if ((type == Types.DOUBLE) || (type == Types.NUMERIC)) {
            parameter = new Parameter(colName, ParameterType.DOUBLE);
        } else if ((type == Types.FLOAT) || (type == Types.DECIMAL)) {
            parameter = new Parameter(colName, ParameterType.FLOAT);
        } else if ((type == Types.INTEGER) || (type == Types.SMALLINT) || (type == Types.TINYINT)) {
            parameter = new Parameter(colName, ParameterType.INTEGER);
        } else if ((type == Types.TIMESTAMP) || (type == Types.TIME) || (type == Types.DATE)) {
            if (!domainClass.getImports().contains("java.util.Date")) {
                domainClass.getImports().add("java.util.Date");
            }
            if (!dbClass.getImports().contains("java.sql.Timestamp")) {
                dbClass.getImports().add("java.sql.Timestamp");
            }
            parameter = new Parameter(colName, ParameterType.DATE);
        } else if ((type == Types.BIT) || (type == Types.BOOLEAN)) {
            parameter = new Parameter(colName, ParameterType.BOOLEAN);
        } else if (type == Types.CHAR) {
            parameter = new Parameter(colName, ParameterType.STRING);
        } else {
            // no specific type found so set to generic object
            parameter = new Parameter(colName, ParameterType.OBJECT);
        }
        return parameter;
    }

    private Connection getConnection() throws SQLException {
        Connection conn = null;
        logger.info("Trying to connect to db using the properties...");
        String url = this.properties.getProperty("jdbc.url");
        String userName = this.properties.getProperty("jdbc.username");
        String password = this.properties.getProperty("jdbc.password");
        logger.info("Connecting to database at:[" + url + "]" + " with username/password:[" + userName + "/" + password + "]");
        Properties connProps = new Properties();
        if (userName == null && password == null) {
            conn = DriverManager.getConnection(url);
        } else {
            connProps.put("user", userName);
            connProps.put("password", password);
            conn = DriverManager.getConnection(url, connProps);
        }

        logger.info("Connected to database");
        return conn;
    }


}
