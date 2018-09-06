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
  }


}
