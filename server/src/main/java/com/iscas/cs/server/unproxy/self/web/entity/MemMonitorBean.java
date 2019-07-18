package com.iscas.cs.server.unproxy.self.web.entity;

import java.util.Date;


public class MemMonitorBean extends BaseMonitorBean{

	public MemMonitorBean(){

	}
	//时间戳
	private Date 	timeStamp;
	//内存全部MB
	private String  total;
	//内存使用
	private String  used;
	//内存空闲MB
	private String  free;

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getUsed() {
		return used;
	}

	public void setUsed(String used) {
		this.used = used;
	}

	public String getFree() {
		return free;
	}

	public void setFree(String free) {
		this.free = free;
	}
}
