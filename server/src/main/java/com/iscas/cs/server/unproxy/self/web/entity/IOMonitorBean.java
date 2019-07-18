package com.iscas.cs.server.unproxy.self.web.entity;



import com.iscas.cs.server.unproxy.self.web.utils.DataUtils;

import java.util.Date;


public class IOMonitorBean extends BaseMonitorBean{
	public IOMonitorBean(){

	}

	private Date 	timeStamp;
	private double  readRate;
	private double  writeRate;

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public double getReadRate() {
		return readRate;
	}

	public void setReadRate(double readRate) {
		this.readRate = DataUtils.formatDouble(readRate);
	}

	public double getWriteRate() {
		return writeRate;
	}

	public void setWriteRate(double writeRate) {
		this.writeRate = DataUtils.formatDouble(writeRate);
	}
}
