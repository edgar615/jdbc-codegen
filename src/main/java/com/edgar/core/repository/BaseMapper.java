package com.edgar.core.repository;

public interface BaseMapper<T, ID> {

    T selectByPrimaryKey(ID id);
}