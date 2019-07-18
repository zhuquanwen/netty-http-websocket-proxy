package com.iscas.cs.server.unproxy.self.web.entity;



import com.iscas.cs.server.unproxy.self.web.utils.DataUtils;

import java.util.Date;


public class NetMonitorBean extends BaseMonitorBean{
	public NetMonitorBean(){

	}

	//时间戳
	private Date 	timeStamp;
	
	//网络发送速率 MB/s
	private double  sendRate;
	//网络接收速率  MB/s
	private double  rcvdRate;

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public double getSendRate() {
		return sendRate;
	}

	public void setSendRate(double sendRate) {
		this.sendRate = DataUtils.formatDouble(sendRate);;
	}

	public double getRcvdRate() {
		return rcvdRate;
	}

	public void setRcvdRate(double rcvdRate) {
		this.rcvdRate = DataUtils.formatDouble(rcvdRate);;
	}
}
