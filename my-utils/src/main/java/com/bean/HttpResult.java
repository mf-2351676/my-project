package com.bean;

/**
 * @author menfeng
 * @version 2018年12月12日
 */
public class HttpResult {
	
	private int code;
	private String body;
	
	public HttpResult(int code, String body) {
		super();
		this.code = code;
		this.body = body;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "HttpResult [code=" + code + ", body=" + body + "]";
	}
}
