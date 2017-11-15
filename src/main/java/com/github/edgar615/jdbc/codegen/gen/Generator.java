package com.github.edgar615.jdbc.codegen.gen;

import com.github.edgar615.jdbc.codegen.db.DBFetcher;
import com.github.edgar615.jdbc.codegen.db.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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

  private final Codegen ruleGen;

  public Generator(CodegenOptions options) {
    this.options = options;
    String packageName = options.getDomainPackage();
    String srcFolderPath = options.getSrcFolderPath();
    domainGen = new Codegen(srcFolderPath, packageName, "", tplFile);
    ruleGen = new Codegen(srcFolderPath, packageName, "Rule", ruleTplFile);
  }


  public void generate() {
    List<Table> tables = new DBFetcher(options).fetchTablesFromDb();
    tables.stream()
            .filter(t -> !t.isIgnore())
            .forEach(t -> domainGen.genCode(t));
    if (options.isGenRule()) {
      tables.stream()
              .filter(t -> !t.isIgnore())
              .forEach(t -> ruleGen.genCode(t));
    }
  }


}
