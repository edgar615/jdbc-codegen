package com.edgar.core.jdbc;

import org.springframework.jdbc.core.RowMapper;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/5/29.
 */
public class JdbcRepository<T extends Persistable<ID>, ID extends Serializable> {
    private final RowMapper<T> rowMapper;
    private final RowUnmapper<T> rowUnmapper;

    public JdbcRepository(RowMapper<T> rowMapper, RowUnmapper<T> rowUnmapper) {
        this.rowMapper = rowMapper;
        this.rowUnmapper = rowUnmapper;
    }

}
