package com.github.edgar615.jdbc.codegen.gen;

import com.github.edgar615.jdbc.codegen.db.DBFetcher;
import com.github.edgar615.jdbc.codegen.db.ParameterType;
import com.github.edgar615.jdbc.codegen.db.Table;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Edgar on 2017/5/17.
 *
 * @author Edgar  Date 2017/5/17
 */
public class Generator {

  private static final Logger LOGGER = LoggerFactory.getLogger(Generator.class);

  private static final String domainTplFile = "tpl/domain.hbs";

  private static final String kitTplFile = "tpl/domainKit.hbs";

  private static final String ruleTplFile = "tpl/rule.hbs";

  private static final String daoTplFile = "tpl/dao.hbs";

  private static final String daoImplTplFile = "tpl/daoImpl.hbs";

  private static final String mapperXmlTplFile = "tpl/xmlMapper.hbs";

  private static final String mapperTplFile = "tpl/mapper.hbs";

  private static final String wildcardEvictCacheMapperTplFile = "tpl/mapperWildcardCache.hbs";

  private final CodegenOptions options;

  public Generator(CodegenOptions options) {
    this.options = options;
  }

  private void generateDomain(Table table) {
    Codegen codegen = new Codegen(options.getSrcFolderPath(), options.getDomainPackage(), "",
        domainTplFile);
    table.getColumns().stream()
        .filter(c -> !c.isIgnore())
        .map(c -> c.getParameterType())
        .forEach(t -> {
          if (t == ParameterType.DATE) {
            codegen.addImport("java.util.Date");
          }
          if (t == ParameterType.TIMESTAMP) {
            codegen.addImport("java.sql.Timestamp");
          }
          if (t == ParameterType.BIGDECIMAL) {
            codegen.addImport("java.math.BigDecimal");
          }
        });
    codegen.addImport("com.google.common.base.MoreObjects");
    codegen.addImport("com.github.edgar615.util.db.Persistent");
    codegen.addImport("com.github.edgar615.util.db.PrimaryKey");
    boolean containsVersion = table.getColumns().stream()
        .filter(c -> !c.isIgnore())
        .anyMatch(c -> c.isVersion());
    if (containsVersion) {
      codegen.addImport("com.github.edgar615.util.db.VersionKey");
    }
    if (table.getContainsVirtual()) {
      codegen.addImport("com.github.edgar615.util.db.VirtualKey");
    }
    codegen.genCode(table);
  }

  private void generateKit(Table table) {
    Codegen codegen = new Codegen(options.getSrcFolderPath(), options.getDomainPackage(), "Kit",
        kitTplFile);
    table.getColumns().stream()
        .filter(c -> !c.isIgnore())
        .map(c -> c.getParameterType())
        .forEach(t -> {
          if (t == ParameterType.DATE) {
            codegen.addImport("java.util.Date");
          }
          if (t == ParameterType.TIMESTAMP) {
            codegen.addImport("java.sql.Timestamp");
          }
          if (t == ParameterType.BIGDECIMAL) {
            codegen.addImport("java.math.BigDecimal");
          }
        });
    codegen.addImport("java.util.List");
    codegen.addImport("java.util.Map");
    codegen.addImport("com.google.common.collect.Lists");
    codegen.addImport("com.github.edgar615.util.db.PersistentKit");
    codegen.genCode(table);
  }

  private void generateRule(Table table) {
    Codegen codegen = new Codegen(this.options.getSrcFolderPath(),
        this.options.getDomainPackage(), "Rule", ruleTplFile);
    codegen.addImport("com.google.common.collect.ArrayListMultimap")
        .addImport("com.google.common.collect.Multimap")
        .addImport("com.github.edgar615.util.validation.Rule");
    codegen.genCode(table);
  }

  private void generateDao(Table table) {
    Codegen daoGen = new Codegen(this.options.getSrcFolderPath(),
        this.options.getDaoOptions().getDaoPackage(), "Dao", daoTplFile);
    daoGen.addVariable("domainPackage", this.options.getDomainPackage());
    daoGen.addImport("com.github.edgar615.util.db.BaseDao");
    if (!this.options.getDomainPackage().equals(this.options.getDaoOptions().getDaoPackage())) {
      daoGen.addImport(this.options.getDomainPackage() + "." + table.getUpperCamelName());
    }

    daoGen.genCode(table);
  }

  private void generateDaoImpl(Table table) {
    String daoImplPackage = this.options.getDaoOptions().getDaoPackage() + ".impl";
    Codegen daoImplGen = new Codegen(this.options.getSrcFolderPath(),
        daoImplPackage, "DaoImpl", daoImplTplFile);
    daoImplGen.addVariable("daoPackage", this.options.getDaoOptions().getDaoPackage());
    daoImplGen.addVariable("domainPackage", this.options.getDomainPackage());
    daoImplGen.addVariable("supportSpring", this.options.getDaoOptions().isSupportSpring());
    daoImplGen.addImport("com.github.edgar615.util.db.BaseDaoImpl")
        .addImport("com.github.edgar615.util.db.Jdbc");

    if (!this.options.getDomainPackage().equals(daoImplPackage)) {
      daoImplGen.addImport(this.options.getDomainPackage() + "." + table.getUpperCamelName());
    }

    daoImplGen.addImport(
        this.options.getDaoOptions().getDaoPackage() + "." + table.getUpperCamelName() + "Dao");
    if (this.options.getDaoOptions().isSupportSpring()) {
      daoImplGen.addImport("org.springframework.stereotype.Service");
    }
    daoImplGen.genCode(table);
  }

  private void generateMapperClass(Table table) {
    Codegen codegen;
    if (this.options.getMybatisOptions().isCacheWildcardEvict()) {
      codegen = new Codegen(this.options.getSrcFolderPath(),
          this.options.getMybatisOptions().getMapperClassPackage(), "Mapper", wildcardEvictCacheMapperTplFile);
    } else {
      codegen = new Codegen(this.options.getSrcFolderPath(),
          this.options.getMybatisOptions().getMapperClassPackage(), "Mapper", mapperTplFile);
    }

    codegen.addImport("com.github.edgar615.util.mybatis.BaseMapper");
    codegen.addImport("com.github.edgar615.util.search.Example");
    codegen.addImport("java.util.List");
    codegen.addImport("java.util.Map");
    codegen.addImport("org.apache.ibatis.annotations.Mapper");
    codegen.addImport("org.apache.ibatis.annotations.Param");
    codegen.addImport("org.springframework.cache.annotation.CacheConfig");
    codegen.addImport("org.springframework.cache.annotation.CacheEvict");
    codegen.addImport("org.springframework.cache.annotation.Cacheable");
    codegen.addVariable("domainPackage", this.options.getDomainPackage());
    if (!this.options.getDomainPackage().equals(this.options.getMybatisOptions().getMapperClassPackage())) {
      codegen.addImport(this.options.getDomainPackage() + "." + table.getUpperCamelName());
    }
    codegen.genCode(table);
  }

  private void generateMapperXml(Table table) {
    Codegen codegen = new Codegen(this.options.getMybatisOptions().getXmlFolderPath(),
        this.options.getMybatisOptions().getMapperClassPackage(), "Mapper", mapperXmlTplFile);
    codegen.addVariable("domainPackage", this.options.getDomainPackage());
    codegen.addVariable("mapperPackage", this.options.getMybatisOptions().getMapperClassPackage());
    codegen.addVariable("mapperSuffix", "Mapper");
    codegen.setFileType(".xml");
    codegen.setCommentStart("<!-- START Do not remove/edit this line. CodeGenerator will preserve any code between start and end tags. -->");
    codegen.setCommentEnd("<!-- END Do not remove/edit this line. CodeGenerator will preserve any code between start and end tags.-->");
    codegen.setIsCommentStart("<!-- START ");
    codegen.setIsCommentEnd("<!-- END ");
    codegen.genCode(table);
  }

  public void generate() {
    List<Table> tables = new DBFetcher(options).fetchTablesFromDb();
    tables.stream()
        .filter(t -> !t.isIgnore())
        .forEach(t -> generateDomain(t));
    tables.stream()
        .filter(t -> !t.isIgnore())
        .forEach(t -> generateKit(t));
    if (options.isGenRule()) {
      tables.stream()
          .filter(t -> !t.isIgnore())
          .forEach(t -> generateRule(t));
    }
    if (options.isGenDao() && options.getDaoOptions() != null) {
      tables.stream()
          .filter(t -> !t.isIgnore())
          .forEach(t -> generateDao(t));
      if (options.getDaoOptions().isGenImpl()) {
        tables.stream()
            .filter(t -> !t.isIgnore())
            .forEach(t -> generateDaoImpl(t));
      }
    }

    if (options.isGenMybatis() && options.getMybatisOptions() != null) {
      tables.stream()
          .filter(t -> !t.isIgnore())
          .forEach(t -> generateMapperClass(t));
      tables.stream()
          .filter(t -> !t.isIgnore())
          .forEach(t -> generateMapperXml(t));
    }
  }


}
