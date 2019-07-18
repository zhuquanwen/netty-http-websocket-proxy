package com.iscas.cs.server.unproxy.self.web.entity;

import java.util.Date;


public class CheckAllBean extends BaseMonitorBean{

	public CheckAllBean(){

	}

	private CPUMonitorBean cpuMonitorBean;
	private DiskMonitorBean diskMonitorBean;
	private IOMonitorBean ioMonitorBean;
	private JVMMonitorBean jvmMonitorBean;
	private MemMonitorBean memMonitorBean;
	private NetMonitorBean netMonitorBean;
	private Date timeStamp;

	public CPUMonitorBean getCpuMonitorBean() {
		return cpuMonitorBean;
	}

	public void setCpuMonitorBean(CPUMonitorBean cpuMonitorBean) {
		this.cpuMonitorBean = cpuMonitorBean;
	}

	public DiskMonitorBean getDiskMonitorBean() {
		return diskMonitorBean;
	}

	public void setDiskMonitorBean(DiskMonitorBean diskMonitorBean) {
		this.diskMonitorBean = diskMonitorBean;
	}

	public IOMonitorBean getIoMonitorBean() {
		return ioMonitorBean;
	}

	public void setIoMonitorBean(IOMonitorBean ioMonitorBean) {
		this.ioMonitorBean = ioMonitorBean;
	}

	public JVMMonitorBean getJvmMonitorBean() {
		return jvmMonitorBean;
	}

	public void setJvmMonitorBean(JVMMonitorBean jvmMonitorBean) {
		this.jvmMonitorBean = jvmMonitorBean;
	}

	public MemMonitorBean getMemMonitorBean() {
		return memMonitorBean;
	}

	public void setMemMonitorBean(MemMonitorBean memMonitorBean) {
		this.memMonitorBean = memMonitorBean;
	}

	public NetMonitorBean getNetMonitorBean() {
		return netMonitorBean;
	}

	public void setNetMonitorBean(NetMonitorBean netMonitorBean) {
		this.netMonitorBean = netMonitorBean;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
}
