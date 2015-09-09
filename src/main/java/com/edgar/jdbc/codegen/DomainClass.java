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
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent the generated Java bean Class. Class name is same as the
 * table name in singular form. e.g For employees table , Employee.java is
 * generated
 *
 * @author Kalyan Mulampaka, Edgar
 */
public class DomainClass extends BaseClass {

    final static Logger logger = LoggerFactory.getLogger(DomainClass.class);

    private boolean generateJsr303Annotations = false;
    private List<String> jsr303InsertGroups = new ArrayList<String>();
    private List<String> jsr303UpdateGroups = new ArrayList<String>();

    public DomainClass() {
        super.setInterfaceName("com.edgar.core.repository.Persistable");
        this.addImports();
    }

    public List<String> getJsr303InsertGroups() {
        return this.jsr303InsertGroups;
    }

    public void setJsr303InsertGroups(List<String> jsr303InsertGroups) {
        this.jsr303InsertGroups = jsr303InsertGroups;
    }

    public List<String> getJsr303UpdateGroups() {
        return this.jsr303UpdateGroups;
    }

    public void setJsr303UpdateGroups(List<String> jsr303UpdateGroups) {
        this.jsr303UpdateGroups = jsr303UpdateGroups;
    }

    @Override
    protected void addImports() {
    }

    public boolean isGenerateJsr303Annotations() {
        return this.generateJsr303Annotations;
    }

    public void setGenerateJsr303Annotations(boolean generateJsr303Annotations) {
        this.generateJsr303Annotations = generateJsr303Annotations;
        if (this.generateJsr303Annotations) {
            this.imports.add("javax.validation.constraints.Null");
            this.imports.add("javax.validation.constraints.NotNull");
            this.imports.add("javax.validation.constraints.Size");
            this.imports.add("org.hibernate.validator.constraints.NotEmpty");
        }
    }

    @Override
    protected void printClassImplements() {
        if (!Strings.isNullOrEmpty(interfaceName)) {
            String implementsClass = this.interfaceName.substring(CharMatcher.anyOf(".").lastIndexIn(this.interfaceName) + 1);
            sourceBuf.append(" implements " + implementsClass);
            if (pkeys.isEmpty()) {
                sourceBuf.append("<String>");
            } else if (pkeys.size() == 1) {
                sourceBuf.append("<" + this.pkeys.values().iterator().next().getName() + ">");
            } else {
                sourceBuf.append("<Map<String, Object>>");
            }
        }
        sourceBuf.append(" ");
    }

    protected void printFields() {
        sourceBuf.append("\tprivate static final long serialVersionUID = 1L;\n\n");

        for (Field field : fields) {

            String type = field.getType().getName();
            if (field.isPrimitive()) {
                type = field.getType().getPrimitiveName();
            }

            String fieldName = field.getHumpName();
            StringBuffer modifiers = new StringBuffer("");
            if (!field.getModifiers().isEmpty()) {
                for (String modifier : field.getModifiers()) {
                    modifiers.append(modifier + " ");
                }
            }

            if (this.generateJsr303Annotations) {
                // generate the jsr303 annotations
                if (!field.isNullable()) {
                    if (field.getType() == ParameterType.STRING) {
                        sourceBuf.append("\t@NotEmpty\n");
                    } else if (field.getColName().equalsIgnoreCase("id")) {
                        //update groups
                        sourceBuf.append("\t@NotNull(groups = {");
                        int i = this.jsr303UpdateGroups.size();
                        for (String name : this.jsr303UpdateGroups) {
                            sourceBuf.append(name + ".class");
                            if (--i > 0)
                                sourceBuf.append(", ");
                        }
                        sourceBuf.append("})\n");

                        // insert groups
                        sourceBuf.append("\t@Null(groups = {");
                        i = this.jsr303InsertGroups.size();
                        for (String name : this.jsr303InsertGroups) {
                            sourceBuf.append(name + ".class");
                            if (--i > 0)
                                sourceBuf.append(", ");
                        }
                        sourceBuf.append("})\n");
                    } else if (field.getColName().endsWith("id")) {
                        //update groups
                        sourceBuf.append("\t@NotNull(groups = {");
                        int i = this.jsr303UpdateGroups.size();
                        i = this.jsr303InsertGroups.size();
                        if (i > 0) {
                            for (String name : this.jsr303InsertGroups) {
                                sourceBuf.append(name + ".class");
                                if (--i > 0)
                                    sourceBuf.append(", ");
                            }
                            if (!this.jsr303UpdateGroups.isEmpty()) {
                                sourceBuf.append(", ");
                            }
                        }
                        for (String name : this.jsr303UpdateGroups) {
                            sourceBuf.append(name + ".class");
                            if (--i > 0)
                                sourceBuf.append(", ");
                        }

                        sourceBuf.append("})\n");
                    } else {
                        sourceBuf.append("\t@NotNull\n");
                    }
                }
                if (field.getSize() > 0) {
                    if (field.getType() == ParameterType.STRING) {
                        sourceBuf.append("\t@Size(max=" + field.getSize() + ")\n");
                    }
                }

            }
            sourceBuf.append("\tprivate " + modifiers.toString() + type + " " + fieldName);
            if (!Strings.isNullOrEmpty(field.getDefaultValue())) {
                logger.debug("Found default value:{}", field.getDefaultValue());
                if (this.pkeys.containsKey(field.getColName())) {
                    // this is a pk so ignore
                    sourceBuf.append(";\n\n");
                } else {
                    ParameterType t = field.getType();
                    String val = field.getDefaultValue();
                    switch (t) {
                        case BOOLEAN:
                            if ("0".equals(val)) {
                                sourceBuf.append(" = " + false + ";\n\n");
                            } else if ("1".equals(val)) {
                                sourceBuf.append(" = " + true + ";\n\n");
                            } else {
                                sourceBuf.append(";\n\n");
                            }

                            break;
                        case INTEGER:
                            // postgres default values for int columns are stored as floats. e.g 100.0
                            if (CharMatcher.anyOf(".").matchesAnyOf(val)) {
                                String[] tokens = Iterables.toArray(Splitter.on(".").split(val), String.class);
                                val = tokens[0];
                            }
                            sourceBuf.append(" = " + Integer.parseInt(val) + ";\n\n");
                            break;
                        case LONG:
                            sourceBuf.append(" = " + Long.parseLong(val) + "L;\n\n");
                            break;
                        case DOUBLE:
                            sourceBuf.append(" = " + Float.parseFloat(val) + "D;\n\n");
                            break;
                        case FLOAT:
                            sourceBuf.append(" = " + Float.parseFloat(val) + "F;\n\n");
                            break;
                        case DATE:
                        case TIMESTAMP:
                            if (val.equalsIgnoreCase("now()")) {
                                sourceBuf.append(" = new Date ();\n\n");
                            } else {
                                sourceBuf.append(";\n\n");
                            }
                            break;
                        case STRING:
                        case CHAR:
                            DATABASE d = DATABASE.getByName(this.getDbProductName());
                            logger.debug("Database:{}", d);
                            switch (d) {
                                case POSTGRESQL:
                                    String[] tokens = Iterables.toArray(Splitter.on("::").split(val), String.class); // usual form 'value' :: character varying
                                    if (tokens != null && tokens.length > 0) {
                                        sourceBuf.append(" = \"" + tokens[0].substring(1, tokens[0].length() - 1) + "\";\n\n");
                                    } else {
                                        sourceBuf.append(";\n\n");
                                    }
                                    break;
                                case MYSQL:
                                    sourceBuf.append(" = \"" + val + "\";\n\n");
                                    break;
                                default:
                                    sourceBuf.append(";\n\n");
                                    break;
                            }
                            break;
                        default:
                            sourceBuf.append(";\n\n");
                    }
                }
            } else {
                sourceBuf.append(";\n\n");
            }
        }
    }

    protected void printInterfaceImpl() {
        // add the interface impl methods

        if (this.pkeys.size() > 1) {
            sourceBuf.append("@Override\n");
            sourceBuf.append("\tpublic Map<String, Object> getId() ");
            super.printOpenBrace(0, 1);
            sourceBuf.append("\t\tMap<String, Object> mapping = new HashMap<String, Object>();\n");
            for (String key : this.pkeys.keySet()) {
                sourceBuf.append("\t\tmapping.put(\"" + key + "\"," + CodeGenUtil.normalize(key) + ");\n");
            }
            sourceBuf.append("\t\treturn Collections.unmodifiableMap(mapping);\n");
            super.printCloseBrace(1, 2);

            sourceBuf.append("\t@Override\n");
            sourceBuf.append("\tpublic void setId(Map<String, Object> map) ");
            super.printOpenBrace(0, 1);
            for (String key : this.pkeys.keySet()) {
                sourceBuf.append("\t\tthis." + CodeGenUtil.normalize(key.toLowerCase()) + " = map.get(" + CodeGenUtil.normalize(key.toLowerCase()) + ");\n");
            }
            super.printCloseBrace(1, 2);

        } else if (this.pkeys.size() == 1) {
            String key = this.pkeys.keySet().iterator().next();
            ParameterType keyType = this.pkeys.get(key);
            sourceBuf.append("\t@Override\n");
            sourceBuf.append("\tpublic " + keyType.getName() + " getId () ");
            super.printOpenBrace(0, 1);
            sourceBuf.append("\t\treturn this." + CodeGenUtil.normalize(key.toLowerCase()) + ";\n");
            super.printCloseBrace(1, 2);

            sourceBuf.append("\t@Override\n");
            sourceBuf.append("\tpublic void setId(" + keyType.getName() + " id) ");
            super.printOpenBrace(0, 1);
            sourceBuf.append("\t\tthis." + CodeGenUtil.normalize(key.toLowerCase()) + " = id;\n");
            super.printCloseBrace(1, 2);

        } else {
            sourceBuf.append("\t@Override\n");
            sourceBuf.append("\tpublic String getId() ");
            super.printOpenBrace(0, 1);
            sourceBuf.append("\t\tthrow new UnsupportedOperationException(\"There is no primary key\");\n");
            super.printCloseBrace(1, 2);

            sourceBuf.append("\t@Override\n");
            sourceBuf.append("\tpublic void setId(String id) ");
            super.printOpenBrace(0, 1);
            sourceBuf.append("\t\tthrow new UnsupportedOperationException(\"There is no primary key\");\n");
            super.printCloseBrace(1, 2);
        }
    }

    protected void printMethods() {
        for (Method method : methods) {
            String methodName = method.getName();
            String paramName = CodeGenUtil.normalize(method.getParameter().getName().toLowerCase());
            String paramType = "";
            ParameterType pType = method.getParameter().getType();
            if (pType == ParameterType.OBJECT) {
                String name = method.getParameter().getClassName();
                if (Strings.isNullOrEmpty(name))
                    name = method.getParameter().getName();
                paramType = name;
            } else {
                paramType = pType.getPrimitiveName();
            }
            String fieldName = method.getParameter().getName();

            if (fieldName.equalsIgnoreCase("id") && this.pkeys.containsKey(fieldName)) {
                // id
                logger.debug("Found id as pk, it is handled in the pk section, so not adding setter and getter");
                continue;
            }

            // setter
            if (method.isGenerateSetter()) {
                logger.debug("Method name:{}", methodName);
                if (pType == ParameterType.LIST) {
                    String mName = CodeGenUtil.pluralizeName(methodName);
                    logger.debug("Pluralized Method name:{}", mName);
                    sourceBuf.append("\tpublic void set" + mName + "(");
                    sourceBuf.append("List<" + methodName + "> " + paramName);
                    sourceBuf.append(") ");

                    // implementation
                    super.printOpenBrace(0, 1);
                    sourceBuf.append("\t\tthis." + paramName + " = " + paramName + ";\n");
                    super.printCloseBrace(1, 2);
                } else {
                    sourceBuf.append("\tpublic void set" + methodName + "(");
                    sourceBuf.append(paramType + " " + paramName);
                    sourceBuf.append(") ");

                    // implementation
                    super.printOpenBrace(0, 1);
                    sourceBuf.append("\t\tthis." + paramName + " = " + paramName + ";\n");
                    super.printCloseBrace(1, 2);
                }
            }

            // getter
            if (method.isGenerateGetter()) {
                if (pType == ParameterType.LIST) {
                    String mName = CodeGenUtil.pluralizeName(methodName);
                    sourceBuf.append("\tpublic List<" + methodName + "> get" + mName + "() ");
                    super.printOpenBrace(0, 1);
                    sourceBuf.append("\t\treturn this." + paramName + ";\n");
                    super.printCloseBrace(1, 2);
                } else {
                    sourceBuf.append("\tpublic " + paramType + " get" + methodName + "() ");
                    super.printOpenBrace(0, 1);
                    sourceBuf.append("\t\treturn this." + paramName + ";\n");
                    super.printCloseBrace(1, 2);
                }
            }
        }
    }

    protected void printToString() {
        //override toString()
        sourceBuf.append("\t@Override\n\tpublic String toString() ");
        this.printOpenBrace(0, 1);
        sourceBuf.append("\t\treturn MoreObjects.toStringHelper(\"" + name + "\")\n");
        for (Field field : fields) {
            String fieldName = field.getHumpName();
            sourceBuf.append("\t\t\t.add(\"" + fieldName + "\", " + fieldName + ")\n");
        }
        sourceBuf.append("\t\t\t.toString();\n");
        this.printCloseBrace(1, 2);
    }

    protected void preprocess() {
        if (this.pkeys.size() > 0) {
            if (this.pkeys.size() > 1) {
                this.imports.add("java.util.HashMap");
                this.imports.add("java.util.Map");
                this.imports.add("java.util.Collections");
            }
            this.imports.add("com.google.common.base.MoreObjects");
        }

    }

    public void generateSource() {
        this.preprocess();

        super.printPackage();
        super.printImports();
        super.printClassComments();
        super.printClassDefn();
        this.printClassImplements();

        super.printOpenBrace(0, 2);

        this.printFields();

        super.printCtor();

        this.printInterfaceImpl();

        this.printMethods();

        this.printToString();

        super.printUserSourceCode();

        super.printCloseBrace(0, 0); // end of class
        //logger.debug ("Printing Class file content:\n" + sourceBuf.toString ());

    }

}
