package com.github.edgar615.jdbc.codegen;

import com.github.edgar615.mysql.mapping.ParameterType;
import com.github.edgar615.mysql.mapping.Table;
import com.github.edgar615.mysql.mapping.TableMapping;
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

  private static final String tplFile = "tpl/domain.hbs";

  private static final String ruleTplFile = "tpl/rule.hbs";

  private static final String daoTplFile = "tpl/dao.hbs";

  private static final String daoImplTplFile = "tpl/daoImpl.hbs";

  private final CodegenOptions options;

  public Generator(CodegenOptions options) {
    this.options = options;
  }

  private void generateDomain(Table table) {
    Codegen codegen = new Codegen(options.getSrcFolderPath(), options.getDomainPackage(), "",
        tplFile);
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
    codegen.addImport("com.google.common.base.MoreObjects");
    codegen.addImport("com.google.common.collect.Lists");
    codegen.addImport("com.google.common.collect.Maps");
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

  public void generate() throws Exception {
    List<Table> tables = new TableMapping(options).fetchTable();
    tables.stream()
        .forEach(t -> generateDomain(t));
    if (options.isGenRule()) {
      tables.stream()
          .forEach(t -> generateRule(t));
    }
    if (options.isGenDao() && options.getDaoOptions() != null) {
      tables.stream()
          .forEach(t -> generateDao(t));
      if (options.getDaoOptions().isGenImpl()) {
        tables.stream()
            .forEach(t -> generateDaoImpl(t));
      }
    }
  }


}
