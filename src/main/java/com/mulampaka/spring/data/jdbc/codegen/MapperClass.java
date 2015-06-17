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
package com.mulampaka.spring.data.jdbc.codegen;

import com.mulampaka.spring.data.jdbc.codegen.util.CodeGenUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapperClass extends BaseClass {

    final static Logger logger = LoggerFactory.getLogger(RepositoryClass2.class);
    private static String CLASS_SUFFIX = "Mapper";

    public MapperClass() {
        this.classSuffix = CLASS_SUFFIX;
        super.setExtendsClassName("com.edgar.core.repository.BaseMapper");
        this.addImports();
    }

    @Override
    protected void addImports() {
        this.imports.add("org.springframework.stereotype.Repository");
    }

    protected void printClassAnnotations() {
        sourceBuf.append("@Repository\n");
    }

    protected void printClassDefn() {
        sourceBuf.append("public interface " + WordUtils.capitalize(CodeGenUtil.normalize(name)) + this.classSuffix);
    }

    @Override
    protected void printClassExtends() {
        super.printClassExtends();

        if (StringUtils.isNotBlank(extendsClassName)) {
            sourceBuf.append("<");
            sourceBuf.append(this.name + ", ");
            if (this.pkeys.size() == 0) {
                sourceBuf.append("String");
            } else if (pkeys.size() == 1) {
                sourceBuf.append(this.pkeys.values().iterator().next().getName());
            } else {
                sourceBuf.append("Map<String, Object>");
            }
            sourceBuf.append(">");

        }
    }

    protected void preprocess() {
        if (this.pkeys.size() != 1) {
            if (this.pkeys.size() > 1) {
                this.imports.add("java.util.Map");
            }
        }
    }

    @Override
    public void generateSource() {
        this.preprocess();

        this.name = WordUtils.capitalize(CodeGenUtil.normalize(this.name));

        super.printPackage();
        super.printImports();
        super.printClassComments();

        this.printClassAnnotations();
        this.printClassDefn();
        this.printClassExtends();
        super.printClassImplements();

        super.printOpenBrace(0, 2);
        super.printLogger();
//        this.printCtor();

//        this.printFKeyMethods();

//        this.printInterfaceImpl();

        super.printUserSourceCode();

        super.printCloseBrace(0, 2);
    }


}
