package com.github.edgar615.jdbc.codegen.gen;

/**
 * 用于生成Mybatis的参数.
 *
 * @author Edgar
 * @create 2018-08-31 19:31
 **/
public class MybatisOptions {
    private static final String DEFAULT_MAPPER_FOLDER_PATH = "src";

    private static final String DEFAULT_XML_FOLDER_PATH = "src";

    private static final String DEFAULT_MAPPER_PACKAGE = "com.github.edgar615.codegen.mapper";

    private static final String DEFAULT_XML_PACKAGE = "com.github.edgar615.codegen.mapper";

    private String mapperFolderPath = DEFAULT_MAPPER_FOLDER_PATH;

    private String mapperPackage = DEFAULT_MAPPER_PACKAGE;

    private String xmlFolderPath = DEFAULT_MAPPER_FOLDER_PATH;

    private String xmlPackage = DEFAULT_MAPPER_PACKAGE;

    public String getMapperFolderPath() {
        return mapperFolderPath;
    }

    public MybatisOptions setMapperFolderPath(String mapperFolderPath) {
        this.mapperFolderPath = mapperFolderPath;
        return this;
    }

    public String getMapperPackage() {
        return mapperPackage;
    }

    public MybatisOptions setMapperPackage(String mapperPackage) {
        this.mapperPackage = mapperPackage;
        return this;
    }

    public String getXmlFolderPath() {
        return xmlFolderPath;
    }

    public MybatisOptions setXmlFolderPath(String xmlFolderPath) {
        this.xmlFolderPath = xmlFolderPath;
        return this;
    }

    public String getXmlPackage() {
        return xmlPackage;
    }

    public MybatisOptions setXmlPackage(String xmlPackage) {
        this.xmlPackage = xmlPackage;
        return this;
    }
}
