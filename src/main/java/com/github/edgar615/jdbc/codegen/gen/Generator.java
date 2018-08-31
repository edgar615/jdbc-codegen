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

    private static final String mapperTplFile = "tpl/mapperClass.hbs";

    private static final String xmlTplFile = "tpl/mapperXml.hbs";

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
            Codegen ruleGen = new Codegen(this.options.getSrcFolderPath(), this.options.getDomainPackage(), "Rule", ruleTplFile);
            tables.stream()
                    .filter(t -> !t.isIgnore())
                    .forEach(t -> ruleGen.genCode(t));
        }
        if (options.isGenMybatis()) {
            Codegen mybatisMapperGen = new Codegen(this.options.getMybatisOptions().getMapperFolderPath(),
                    this.options.getMybatisOptions().getMapperPackage(), "Mapper", mapperTplFile);
            mybatisMapperGen.addVariable("domainPackage", this.options.getDomainPackage());
            tables.stream()
                    .filter(t -> !t.isIgnore())
                    .forEach(t -> mybatisMapperGen.genCode(t));

            Codegen mybatisXmlGen = new Codegen(this.options.getMybatisOptions().getXmlFolderPath(),
                    this.options.getMybatisOptions().getXmlPackage(), "Mapper", xmlTplFile);
            mybatisXmlGen.setFileType(".xml");
            mybatisXmlGen.addVariable("domainPackage", this.options.getDomainPackage())
                    .addVariable("mapperPackage", this.options.getMybatisOptions().getMapperPackage());
            tables.stream()
                    .filter(t -> !t.isIgnore())
                    .forEach(t -> mybatisXmlGen.genCode(t));
        }
    }


}
