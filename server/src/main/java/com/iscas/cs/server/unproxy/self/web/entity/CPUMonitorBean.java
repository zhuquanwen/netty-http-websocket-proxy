package com.iscas.cs.server.unproxy.self.web.entity;



import com.iscas.cs.server.unproxy.self.web.utils.DataUtils;

import java.util.Date;


public class CPUMonitorBean extends BaseMonitorBean{
	public CPUMonitorBean(){

	}

	private Date 	timeStamp;
	private double  rate;

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate  = DataUtils.formatDouble(rate);
	}
}
