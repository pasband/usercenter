package net.ltsoftware.platform.usercenter.service;

/**
 * BaseService
 * @author
 *
 * @param <T>
 */
public interface BaseService<T> {
	//CRUD
	public T selectByPrimaryKey(Long key) throws Exception;
	public Integer updateByPrimaryKey(T t) throws Exception;
	public Integer deleteByPrimaryKey(Long key) throws Exception;
	public Integer insert(T t) throws Exception;
	//public Integer deleteByEntity(T entity) throws Exception;
}
