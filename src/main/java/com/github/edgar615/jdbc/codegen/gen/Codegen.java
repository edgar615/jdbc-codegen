package com.github.edgar615.jdbc.codegen.gen;

import com.github.edgar615.jdbc.codegen.db.Table;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Edgar on 2017/11/15.
 *
 * @author Edgar  Date 2017/11/15
 */
class Codegen {

  private String commentStart =
      "/* START Do not remove/edit this line. CodeGenerator "
          + "will preserve any code between start and end tags.*/";

  private String commentEnd =
      "/* END Do not remove/edit this line. CodeGenerator will "
          + "preserve any code between start and end tags.*/";

  private String isCommentStart = "/* START";

  private String isCommentEnd = "/* END";

  private static final Handlebars handlebars = new Handlebars();
  private static final Logger LOGGER = LoggerFactory.getLogger(Generator.class);

  static {
    handlebars.registerHelper("safestr", new Helper<String>() {
      @Override
      public Object apply(String str, Options options) throws IOException {
        return new Handlebars.SafeString(str);
      }
    });
    handlebars.registerHelper("lowUnderscoreToLowCamel", new Helper<String>() {
      @Override
      public Object apply(String str, Options options) throws IOException {
        return (CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str));
      }
    });
    handlebars.registerHelper("upperUnderscoreToLowCamel", new Helper<String>() {
      @Override
      public Object apply(String str, Options options) throws IOException {
        return (CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_UNDERSCORE, str));
      }
    });

    handlebars.registerHelper("serialVersionUID", new Helper<String>() {
      @Override
      public Object apply(String str, Options options) throws IOException {
        return Hashing.farmHashFingerprint64().hashString(str, Charset.defaultCharset()).padToLong() + "L";
      }
    });
    handlebars.registerHelpers(new HelperSource());
  }

  private final List<String> imports = Lists.newArrayList();
  private final String srcFolderPath;
  private final String packageName;
  private final String suffix;
  private final String tpl;
  private final String tplFile;
  private final Map<String, Object> variables = new HashMap<>();
  private String fileType = ".java";

  Codegen(String srcFolderPath, String packageName, String suffix,
      String tplFile) {
    this.srcFolderPath = srcFolderPath;
    this.packageName = packageName;
    this.suffix = suffix;
    this.tplFile = tplFile;
    this.tpl = resolveFile(tplFile);
  }

  private synchronized String readFromFileURL(URL url) {
    File resource;
    try {
      resource = new File(URLDecoder.decode(url.getPath(), "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    boolean isDirectory = resource.isDirectory();
    if (isDirectory) {
      throw new RuntimeException(url + "is dir");
    }
    try {
      String data = new String(Files.readAllBytes(resource.toPath()));
      return data;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private ClassLoader getClassLoader() {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    if (cl == null) {
      cl = getClass().getClassLoader();
    }
    return cl;
  }

  public Codegen setFileType(String fileType) {
    this.fileType = fileType;
    return this;
  }

  public Codegen addVariable(String name, Object value) {
    this.variables.put(name, value);
    return this;
  }

  public Codegen addVariables(Map<String, Object> variables) {
    this.variables.putAll(variables);
    return this;
  }

  public Codegen addImport(String imp) {
    this.imports.add(imp);
    return this;
  }

  public void setCommentStart(String commentStart) {
    this.commentStart = commentStart;
  }

  public void setCommentEnd(String commentEnd) {
    this.commentEnd = commentEnd;
  }

  public void setIsCommentStart(String isCommentStart) {
    this.isCommentStart = isCommentStart;
  }

  public void setIsCommentEnd(String isCommentEnd) {
    this.isCommentEnd = isCommentEnd;
  }

  public void genCode(Table table) {
    try {
      StringBuffer userSource = readUserSourceCode(table);
      Template template = handlebars.compileInline(tpl);
      Map<String, Object> codeGenVariables = new HashMap<>();
      codeGenVariables.putAll(variables);
      codeGenVariables.put("suffix", suffix);
      codeGenVariables.put("table", table);
      codeGenVariables.put("package", packageName);
      codeGenVariables.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
      codeGenVariables.put("userSource", userSource.toString());
      codeGenVariables.put("imports", sortedImports());
      String code = template.apply(codeGenVariables);
      createFile(table, code);
    } catch (Throwable e) {
      LOGGER.error("{}", table.getName(), e);
      throw new RuntimeException(table.getName(), e);
    }
  }

  private List<String> sortedImports() {
    //java和javax开头的在最后面，
    return imports.stream()
        .sorted(Comparator.comparing(i -> i))
        .collect(Collectors.toList());
  }

  private void createPackage(String rootFolderPath, String packageName) throws Exception {
    String path = "";
    if (!Strings.isNullOrEmpty(packageName)) {
      path = CharMatcher.anyOf(".").replaceFrom(packageName, "/");
      if (!Strings.isNullOrEmpty(rootFolderPath)) {
        path = rootFolderPath + "/" + path;
      }
      LOGGER.info("Generated code will be in folder:{}", path);
      File file = new File(path);
      if (!file.exists()) {
        file.mkdirs();
        LOGGER.info("Package structure created:" + path);
      } else {
        LOGGER.info("Package structure:{} exists.", path);
      }
    }
  }

  private void createFile(Table table, String code) throws Exception {
    createPackage(srcFolderPath, packageName);
    String fileName = this.getSourceFileName(table);
    File file = new File(fileName);
    FileWriter writer = new FileWriter(file);
    writer.write(code);
    writer.close();
    LOGGER.info("Class File created:" + file.getPath());
  }

  private String getSourceFileName(Table table) {
    String path = "";
    if (!Strings.isNullOrEmpty(packageName)) {
      path = CharMatcher.anyOf(".").replaceFrom(this.packageName, "/") + "/";
    }
    if (!Strings.isNullOrEmpty(this.srcFolderPath)) {
      path = this.srcFolderPath + "/" + path;
    }
    String name = table.getUpperCamelName();
    if (!Strings.isNullOrEmpty(suffix)) {
      name = name + suffix;
    }
    String fileName = path + name + fileType;
    return fileName;
  }

  private StringBuffer readUserSourceCode(Table table) {
    StringBuffer userSourceBuf = new StringBuffer();
    String fileName = this.getSourceFileName(table);
    File file = new File(fileName);
    if (!file.exists()) {
      userSourceBuf.append(commentStart)
          .append("\n\t")
          .append(commentEnd);
      return userSourceBuf;
    }

    LOGGER.debug("File:{} exists, appending to existing file...", file.getPath());

    try {
      LOGGER.debug("Reading file :{}", file.getName());
      String contents =
          com.google.common.io.Files.asByteSource(file).asCharSource(Charset.defaultCharset())
              .read();

      int startIndex = contents.indexOf(isCommentStart);
      int endIndex = contents.indexOf(isCommentEnd);
      LOGGER.debug("Start index:{} End index:{}", startIndex, endIndex);
      if (startIndex != -1 && endIndex != -1) {
        userSourceBuf.append(contents.substring(startIndex, endIndex));
        userSourceBuf.append(commentEnd + "\n\n");
      }
      // save the imports
      List<String> lines = com.google.common.io.Files.readLines(file, Charset.defaultCharset());
      for (String line : lines) {
        if (line.startsWith("import")) {
          String[] tokens = Iterables.toArray(Splitter.on(" ").split(line), String.class);
          if (tokens.length > 2) {
            String iClass = tokens[1] + " " + tokens[2].substring(0, tokens[2].length() - 1);
            LOGGER.debug("iClass:{}", iClass);
            if (!imports.contains(iClass)) {
              imports.add(iClass);
            }
          } else {
            String iClass = tokens[1].substring(0, tokens[1].length() - 1);
            LOGGER.debug("iClass:{}", iClass);
            if (!imports.contains(iClass)) {
              imports.add(iClass);
            }
          }
        }
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (userSourceBuf.length() == 0) {
      userSourceBuf.append(commentStart)
          .append("\n\t")
          .append(commentEnd);
    }
    return userSourceBuf;

  }

  private String resolveFile(String fileName) {
    // First look for file with that name on disk
    File file = new File(fileName);
    // We need to synchronized here to avoid 2 different threads to copy the file to the cache
    // directory and so
    // corrupting the content.
    synchronized (this) {
      ClassLoader cl = getClassLoader();
      URL url = cl.getResource(fileName);
      if (url != null) {
        String prot = url.getProtocol();
        switch (prot) {
          case "file":
            return readFromFileURL(url);
          case "jar":
            return readFromJarURL(url);
          default:
            throw new IllegalStateException("Invalid url protocol: " + prot);
        }
      }
    }
    throw new IllegalStateException("Invalid fileName: " + fileName);
  }

  private String readFromJarURL(URL url) {
    try {
      JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
      JarFile jarFile = jarURLConnection.getJarFile();
      // 遍历Jar包
      Enumeration<JarEntry> entries = jarFile.entries();
      while (entries.hasMoreElements()) {
        JarEntry jarEntry = entries.nextElement();
        String fileName = jarEntry.getName();
        if (fileName.equals(tplFile)) {
          return new String(ByteStreams.toByteArray(jarFile.getInputStream(jarEntry)));
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return null;
  }
}
