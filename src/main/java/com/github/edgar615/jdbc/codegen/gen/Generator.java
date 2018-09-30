package com.github.edgar615.jdbc.codegen.gen;

import com.github.edgar615.jdbc.codegen.db.DBFetcher;
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

  private static final String tplFile = "tpl/domain.hbs";

  private static final String ruleTplFile = "tpl/rule.hbs";

  private static final String daoTplFile = "tpl/dao.hbs";

  private static final String daoImplTplFile = "tpl/daoImpl.hbs";

  private final CodegenOptions options;

  private final Codegen domainGen;

  public Generator(CodegenOptions options) {
    this.options = options;
    domainGen = new Codegen(options.getSrcFolderPath(), options.getDomainPackage(), "", tplFile);

  }


  public void generate() {
    List<Table> tables = new DBFetcher(options).fetchTablesFromDb();
    tables.stream()
        .filter(t -> !t.isIgnore())
        .forEach(t -> domainGen.genCode(t));
    if (options.isGenRule()) {
      Codegen ruleGen = new Codegen(this.options.getSrcFolderPath(),
          this.options.getDomainPackage(), "Rule", ruleTplFile);
      tables.stream()
          .filter(t -> !t.isIgnore())
          .forEach(t -> ruleGen.genCode(t));
    }
    if (options.isGenDao()) {
      //接口
      Codegen daoGen = new Codegen(this.options.getSrcFolderPath(),
          this.options.getDaoOptions().getDaoPackage(), "Dao", daoTplFile);
      daoGen.addVariable("domainPackage", this.options.getDomainPackage());
      tables.stream()
          .filter(t -> !t.isIgnore())
          .forEach(t -> daoGen.genCode(t));
      //实现类
      Codegen daoImplGen = new Codegen(this.options.getSrcFolderPath(),
          this.options.getDaoOptions().getDaoPackage() + ".impl", "DaoImpl", daoImplTplFile);
      daoImplGen.addVariable("daoPackage", this.options.getDaoOptions().getDaoPackage());
      daoImplGen.addVariable("domainPackage", this.options.getDomainPackage());
      daoImplGen.addVariable("supportSpring", this.options.getDaoOptions().isSupportSpring());
      tables.stream()
          .filter(t -> !t.isIgnore())
          .forEach(t -> daoImplGen.genCode(t));

    }
  }


}
