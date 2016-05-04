/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.comm;

public class TairResponse {

	private Integer requestId;
	
	private Object response;

	public Integer getRequestId() {
		return requestId;
	}

	public Object getResponse() {
		return response;
	}

	public void setRequestId(Integer requestId) {
		this.requestId = requestId;
	}

	public void setResponse(Object response) {
		this.response = response;
	}
	
}
