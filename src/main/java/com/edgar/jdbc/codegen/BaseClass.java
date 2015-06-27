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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public abstract class BaseClass {
    final static Logger logger = LoggerFactory.getLogger(BaseClass.class);


    protected String dbProductName;
    protected String dbProductVersion;

    protected String rootFolderPath;
    protected String packageName;
    protected List<String> imports = new ArrayList<String>();
    protected String name;
    protected String classSuffix = "";
    protected String extendsClassName;
    protected String interfaceName;
    protected List<Field> fields;
    protected List<Method> methods;
    protected Map<String, ParameterType> pkeys = new HashMap<String, ParameterType>();
    protected Map<String, ForeignKey> fkeys = new TreeMap<String, ForeignKey>();
    protected Map<String, List<Relation>> relations = new HashMap<String, List<Relation>>();
    protected StringBuffer userSourceBuf = new StringBuffer("");
    protected StringBuffer sourceBuf = new StringBuffer("");

    protected String[] dontPluralizeWords = null;
    protected int fieldNameCounter = 0;

    private static String COMMENT_START = "/* START 写在START和END中间的代码不会被替换*/";
    private static String COMMENT_END = "/* END 写在START和END中间的代码不会被替换*/";

    private static String IS_COMMENT_START = "/* START";
    private static String IS_COMMENT_END = "/* END";

    public BaseClass() {

    }

    abstract protected void preprocess();

    abstract protected void addImports();

    abstract public void generateSource();

    protected void createLogger() {
        this.imports.add("org.slf4j.LoggerFactory");
        this.imports.add("org.slf4j.Logger");
    }

    protected void printPackage() {
        sourceBuf.append("package " + packageName + ";\n\n");
    }

    protected void printImports() {
        if (!this.imports.isEmpty()) {
            for (String importClass : this.imports) {
                sourceBuf.append("import " + importClass + ";\n");
            }
        }
    }

    public static StringBuffer generateClassComments() {
        StringBuffer strBuf = new StringBuffer("");
        strBuf.append("\n/**\n");
        strBuf.append(" * This class is generated by Jdbc code generator.\n");
        strBuf.append(" *\n");
        strBuf.append(" * @author Jdbc Code Generator\n");
        strBuf.append(" */\n");
        return strBuf;
    }

    public static String generateUserSourceCodeTags() {
        return "\t" + COMMENT_START + "\n\n\t" + COMMENT_END + "\n\n";
    }

    protected void printClassComments() {
        sourceBuf.append(generateClassComments());
    }

    protected void printOpenBrace(int indentLevel, int newLines) {
        for (int i = 0; i < indentLevel; i++) {
            sourceBuf.append("\t");
        }
        sourceBuf.append("{");
        if (newLines == 0)
            newLines = 1; // add atleast 1 new line
        for (int i = 0; i < newLines; i++) {
            sourceBuf.append("\n");
        }
    }

    protected void printLogger() {
        sourceBuf.append("\tfinal static Logger logger = LoggerFactory.getLogger (" + this.name + this.classSuffix + ".class);\n\n");
    }

    protected void printClassDefn() {
        sourceBuf.append("public class " + WordUtils.capitalize(CodeGenUtil.normalize(name)) + this.classSuffix);
    }

    protected void printClassExtends() {
        if (StringUtils.isNotBlank(extendsClassName)) {
            String extendsClass = this.extendsClassName.substring(StringUtils.lastIndexOf(this.extendsClassName, ".") + 1);
            sourceBuf.append(" extends " + extendsClass);
        }
    }

    protected void printClassImplements() {
        if (StringUtils.isNotBlank(interfaceName)) {
            String implementsClass = this.interfaceName.substring(StringUtils.lastIndexOf(this.interfaceName, ".") + 1);
            sourceBuf.append(" implements " + implementsClass);
        }
        sourceBuf.append(" ");
    }

    protected void printCloseBrace(int indentLevel, int newLines) {
        for (int i = 0; i < indentLevel; i++) {
            sourceBuf.append("\t");
        }
        sourceBuf.append("}");
        for (int i = 0; i < newLines; i++) {
            sourceBuf.append("\n");
        }
    }

    protected void printCtor() {
        // no args constructor
        sourceBuf.append("\tpublic " + WordUtils.capitalize(CodeGenUtil.normalize(name)) + this.classSuffix + " () ");
        this.printOpenBrace(1, 2);
        this.printCloseBrace(1, 2);
    }

    protected String getSourceFileName() {
        String path = "";
        if (StringUtils.isNotBlank(this.packageName)) {
            path = StringUtils.replace(this.packageName, ".", "/") + "/";
        }
        if (StringUtils.isNotBlank(this.rootFolderPath)) {
            path = this.rootFolderPath + "/" + path;
        }

        String fileName = path + WordUtils.capitalize(CodeGenUtil.normalize(name)) + classSuffix + ".java";
        return fileName;
    }

    public void createFile() throws Exception {
        String fileName = this.getSourceFileName();
        File file = new File(fileName);
        if (file.exists()) {
            logger.debug("File:{} exists, appending to existing file...", fileName);
            this.readUserSourceCode(file);
            //logger.debug ("User Source code:{}", this.userSourceBuf);
            this.userSourceBuf.toString();
        }

        FileWriter writer = new FileWriter(file);
        this.generateSource();
        writer.write(sourceBuf.toString());
        writer.close();
        logger.info("Class File created:" + fileName);
    }

    protected void readUserSourceCode(File file) {
        try {
            logger.debug("Reading file :{}", file.getName());
            String contents = FileUtils.readFileToString(file);
            //logger.trace ("File contents:{}", contents);

            int startIndex = StringUtils.indexOf(contents, IS_COMMENT_START);
            int endIndex = StringUtils.indexOf(contents, IS_COMMENT_END);
            logger.debug("Start index:{} End index:{}", startIndex, endIndex);
            if (startIndex != -1 && endIndex != -1) {
                userSourceBuf.append(contents.substring(startIndex, endIndex));
                userSourceBuf.append(COMMENT_END + "\n\n");
            }
            // save the imports
            List<String> lines = FileUtils.readLines(file);
            for (String line : lines) {
                if (StringUtils.startsWith(line, "import")) {
                    String[] tokens = StringUtils.split(line, " ");
                    if (tokens.length > 2) {
                        String iClass = tokens[1] + " " + tokens[2].substring(0, tokens[2].length() - 1);
                        logger.debug("iClass:{}", iClass);
                        if (!this.imports.contains(iClass)) {
                            this.imports.add(iClass);
                        }
                    } else {
                        String iClass = tokens[1].substring(0, tokens[1].length() - 1);
                        if (!this.imports.contains(iClass)) {
                            this.imports.add(iClass);
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {

        }

    }

    protected void printUserSourceCode() {
        String userSource = this.userSourceBuf.toString();
        if (StringUtils.isBlank(userSource)) {
            this.sourceBuf.append(BaseClass.generateUserSourceCodeTags());
        } else {
            this.sourceBuf.append("\t" + userSource);
        }

    }

    public Map<String, List<Relation>> getRelations() {
        return this.relations;
    }

    public void setRelations(Map<String, List<Relation>> relations) {
        this.relations = relations;
    }

    public StringBuffer getSourceBuf() {
        return this.sourceBuf;
    }

    public void setSourceBuf(StringBuffer sourceBuf) {
        this.sourceBuf = sourceBuf;
    }

    public Map<String, ForeignKey> getFkeys() {
        return this.fkeys;
    }

    public void setFkeys(Map<String, ForeignKey> fkeys) {
        this.fkeys = fkeys;
    }

    public String getRootFolderPath() {
        return this.rootFolderPath;
    }

    public void setRootFolderPath(String rootFolderPath) {
        this.rootFolderPath = rootFolderPath;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<String> getImports() {
        return this.imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassSuffix() {
        return this.classSuffix;
    }

    public void setClassSuffix(String classSuffix) {
        this.classSuffix = classSuffix;
    }

    public String getInterfaceName() {
        return this.interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
        if (StringUtils.isNotBlank(this.interfaceName)) {
            this.imports.add(this.interfaceName);
        }
    }

    public List<Field> getFields() {
        return this.fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Method> getMethods() {
        return this.methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    public String getExtendsClassName() {
        return this.extendsClassName;
    }

    public void setExtendsClassName(String extendsClassName) {
        this.extendsClassName = extendsClassName;
        if (StringUtils.isNotBlank(extendsClassName)) {
            this.imports.add(extendsClassName);
        }
    }


    public Map<String, ParameterType> getPkeys() {
        return this.pkeys;
    }

    public void setPkeys(Map<String, ParameterType> keys) {
        this.pkeys = keys;
    }

    public String getDbProductName() {
        return this.dbProductName;
    }

    public void setDbProductName(String dbProductName) {
        this.dbProductName = dbProductName;
    }

    public String getDbProductVersion() {
        return this.dbProductVersion;
    }

    public void setDbProductVersion(String dbProductVersion) {
        this.dbProductVersion = dbProductVersion;
    }


    public String[] getDontPluralizeWords() {
        return this.dontPluralizeWords;
    }

    public void setDontPluralizeWords(String[] dontPluralizeWords) {
        this.dontPluralizeWords = dontPluralizeWords;
    }

    public boolean containsFieldName(String name) {
        for (Field field : fields) {
            if (CodeGenUtil.normalize(field.getName()).equals(name))
                return true;
        }
        return false;
    }

    public enum DATABASE {
        POSTGRESQL("PostgreSQL"),
        MYSQL("MySQL"),
        UNKNOWN("unknown");

        private String name;

        private DATABASE(String name) {
            this.name = name;
        }

        public static DATABASE getByName(String name) {
            for (DATABASE d : DATABASE.values()) {
                if (d.getName().equalsIgnoreCase(name))
                    return d;
            }
            return UNKNOWN;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

}
