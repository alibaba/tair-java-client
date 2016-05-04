/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.etc;

public class TairClientException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public TairClientException(String message, Exception e) {
		super(message,e);
	}

	public TairClientException(String message) {
		super(message);
	}

}
