package net.ltsoftware.usercenter.service;

import java.util.List;

/**
 * BaseService
 *
 * @param <T>
 * @author
 */
public interface BaseService<T, E> {
    //CRUD
    T selectByPrimaryKey(Long key) throws Exception;

    Integer updateByPrimaryKey(T t) throws Exception;

    Integer deleteByPrimaryKey(Long key) throws Exception;

    Integer insert(T t) throws Exception;

    List<T> selectByExample(E e) throws Exception;
    //public Integer deleteByEntity(T entity) throws Exception;
}
