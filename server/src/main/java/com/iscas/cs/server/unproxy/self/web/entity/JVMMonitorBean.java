package com.iscas.cs.server.unproxy.self.web.entity;

import java.util.Date;


public class JVMMonitorBean extends BaseMonitorBean{
	public JVMMonitorBean(){

	}

	private Date 	timeStamp;
	private String  total;
	private String  used;
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
