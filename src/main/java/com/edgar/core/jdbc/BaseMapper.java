package com.edgar.core.jdbc;

public interface BaseMapper<T, ID> {

    T selectByPrimaryKey(ID id);
}