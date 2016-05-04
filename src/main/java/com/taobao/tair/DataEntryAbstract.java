package com.taobao.tair;


public abstract class DataEntryAbstract<T> {

	protected T value;
	
	public T getValue() {
		return value;
	}
	
	protected void setValue(T value) {
		this.value = value;
	}
	
}
