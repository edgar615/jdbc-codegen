package com.mulampaka.spring.data.jdbc.codegen.test;

import com.generated.code.domain.CompanyConfig;
import com.generated.code.repository.db.CompanyConfigDB;
import org.testng.annotations.Test;

/**
 * Created by Administrator on 2015/5/29.
 */
public class InsertTest {

    @Test
    public void generateInsert() {
        for (CompanyConfigDB.COLUMNS column : CompanyConfigDB.COLUMNS.values() ) {
            System.out.println(column.getColumnName());
        }
    }
}
