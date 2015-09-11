package com.edgar.core.repository;

import java.util.List;
import java.util.Map;

public interface BaseMapper<T, ID> {

    T selectByPrimaryKey(ID id);

    int insert(T entity);

    int updateByPrimaryKey(T entity);

    int deleteByPrimaryKey(ID id);

    int updateByPrimaryKeyWithLock(T entity);

    int deleteByPrimaryKeyWithLock(Map<String, Object> params);

    int count(Map<String, Object> params);

    List<T> query(Map<String, Object> params);
}