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

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Utility class used in code generation
 *
 * @author Kalyan Mulampaka
 */
public class CodeGenUtil {

  final static Logger logger = LoggerFactory.getLogger(CodeGenUtil.class);

  private CodeGenUtil() {

  }
  public static boolean checkIfSourceCodeExists(String rootFolderPath, String packageName) throws
          Exception {
    boolean exists = false;
    String path = "";
    if (!Strings.isNullOrEmpty(packageName)) {
      path = CharMatcher.anyOf(".").replaceFrom(packageName, "/");
      if (!Strings.isNullOrEmpty(rootFolderPath)) {
        path = rootFolderPath + "/" + path;
      }
      logger.info("Checking if dir structure:{} exists", path);
      File file = new File(path);
      if (file.exists() && file.list().length > 0) {
        logger.debug("Found package structure:{} with {} files", path, file.list().length);
        exists = true;
      } else {
        logger.info("Package structure does not exist:" + path);
        exists = false;
      }
    }
    return exists;
  }

  /**
   * Creates the package folder structure if already not present
   *
   * @param packageName String
   * @throws Exception
   */
  public static void createPackage(String rootFolderPath, String packageName) throws Exception {
    String path = "";
    if (!Strings.isNullOrEmpty(packageName)) {
      path = CharMatcher.anyOf(".").replaceFrom(packageName, "/");
      if (!Strings.isNullOrEmpty(rootFolderPath)) {
        path = rootFolderPath + "/" + path;
      }
      logger.info("Generated code will be in folder:{}", path);
      File file = new File(path);
      if (!file.exists()) {
        file.mkdirs();
        logger.info("Package structure created:" + path);
      } else {
        logger.info("Package structure:{} exists.", path);
      }
    }
  }

}