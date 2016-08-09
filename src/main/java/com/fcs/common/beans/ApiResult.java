package com.fcs.common.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author wuqq
 * 
 * @param <T>
 */
public class ApiResult<T> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4563841876874219154L;

	/**
	 * 
	 */
	private int status = 200 ;

	/**
	 * 
	 */
	private T result;

	/**
	 * 
	 */
	private String message;

	/**
	 * 某些情况下, 我们希望通过数字来约定调用状态时, 可以使用这个字段.但是必须在接口上写清楚
	 */
	private int code;
	
	/**
	 * 
	 */
	private Map<Object, Object> attach = new HashMap<>() ;
	
	/**
	 * 
	 * @return
	 */
	public T getResult() {
		return result;
	}

	
	/**
	 * 不推荐使用, 设置结果请使用ok接口, 这个方法的存在使用了序列化
	 * @param result
	 */
	@Deprecated
	public void setResult(T result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	


	/**
	 * 只应该在调用失败的时候, 才使用这个方法设置出错信息
	 * 
	 * @param message
	 */
	public ApiResult<T> error(String message) {
		this.status = 500;
		this.message = message;
		return this;
	}

	public ApiResult<T> ok() {
		this.status = 200;
		return this;
	}
	
	public ApiResult<T> ok(T t) {
		this.result = t;
		this.status = 200;
		return this;
	}

	/**
	 * 调用是否成功
	 * 
	 * @return
	 */
	public boolean isOK() {
		return status == 200;
	}

	public int getStatus() {
		return status;
	}


	public void addAttach(Object k, Object v) {
		if(attach==null) attach = new HashMap<>() ;
		attach.put(k, v) ;
	}
	
	
	@SuppressWarnings("unchecked")
	public <V> V getAttach(Object k) {
		return (V) (attach == null ? null : attach.get(k)) ;
	}
	

	@Override
	public String toString() {
		return "ApiResult [status=" + status + ", result=" + result + ", message=" + message + ", code=" + code + ", attach=" + attach + "]";
	}
	
	
	
	
	/**
	 * 不推荐使用, 设置状态请使用ok、error接口, 它存在是为了序列化
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	
	/**
	 * 不推荐使用, 设置结果请使用error接口, 它存在是为了序列化
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/** 不要使用这个方法, 它存在是为了序列化 */
	@Deprecated
	public Map<Object, Object> getAttach() {
		return attach;
	}
	
	/** 不要使用这个方法, 它存在是为了序列化 */
	@Deprecated
	public void setAttach(Map<Object, Object> attach) {
		this.attach = attach;
	}
	
}
