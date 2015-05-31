package com.edgar.core.jdbc;

import com.edgar.core.jdbc.RowUnmapper;
import com.generated.code.repository.db.CompanyConfigDB;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;

/**
 * Created by edgar on 15-5-29.
 */
public class InsertHelper<T> {

    public int insert(NamedParameterJdbcTemplate jdbcTemplate, String sql, Map<String, Object> args) {
        return jdbcTemplate.update(sql, args);
    }
}
