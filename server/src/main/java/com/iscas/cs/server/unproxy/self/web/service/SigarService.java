package com.iscas.cs.server.unproxy.self.web.service;

import com.iscas.cs.server.proxy.util.ConfigUtils;
import com.iscas.cs.server.unproxy.self.web.entity.*;
import com.iscas.cs.server.unproxy.self.web.utils.SystemUtils;
import org.hyperic.sigar.*;

import java.net.InetAddress;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class SigarService {

    private static Sigar sigar;
    String dllDirPath;

    String currentSystemArch;
    Boolean currentSystemType;

    public SigarService(){
        this.currentSystemArch = getCurrentSystemArch();
        this.currentSystemType = SystemUtils.isLinuxSystem();
        System.setProperty("java.library.path", System.getProperty("java.library.path")+";"+ ConfigUtils.getCacheTacticsProps().getDllPath());

        if(sigar==null){
            this.sigar = new Sigar();
        }
    }

    // 获取当前系统的类型
    private String getCurrentSystemArch(){
        String v=System.getProperty("os.arch");
        String currentSysType = "";
        if(v.equals("amd64")){
            currentSysType = "amd64";
        }else if(v.equals("x86")){
            currentSysType = "x86";
        }
        return currentSysType;
    }


    public CPUMonitorBean getCPUInfo() {
        CPUMonitorBean cpuMonitorBean = new CPUMonitorBean();
        try{
            CpuPerc perc = sigar.getCpuPerc();
            //System.out.println("整体cpu的占用情况:");
            //System.out.println("cpu的空闲率: " + CpuPerc.format(perc.getIdle()));//获取当前cpu的空闲率
            //System.out.println("cpu的占用率: "+ CpuPerc.format(perc.getCombined()));//获取当前cpu的占用率
            cpuMonitorBean = new CPUMonitorBean();
            cpuMonitorBean.setTimeStamp(new Date());
            cpuMonitorBean.setRate(perc.getCombined());
        }catch (SigarException e){
            e.printStackTrace();
        }

        return cpuMonitorBean;
    }
    public MemMonitorBean getMemInfo(){
        MemMonitorBean memMonitorBean = new MemMonitorBean();
        try{
            Mem mem = sigar.getMem();
            Swap swap = sigar.getSwap();
            memMonitorBean.setTimeStamp(new Date());
            memMonitorBean.setTotal(Long.toString(mem.getTotal()));
            memMonitorBean.setUsed(Long.toString(mem.getUsed()));
            memMonitorBean.setFree(Long.toString(mem.getFree()));
        }catch (SigarException e){
            e.printStackTrace();
        }

        return memMonitorBean;
    }

    public IOMonitorBean getIOInfo(){
        IOMonitorBean ioMonitorBean = new IOMonitorBean();
        try{
            FileSystem fslist[] = sigar.getFileSystemList();
            for (int i = 0; i < 1; i++) {
                FileSystem fs = fslist[i];
                FileSystemUsage usage = null;
                usage = sigar.getFileSystemUsage(fs.getDirName());
                switch (fs.getType()) {
                    case 0: // TYPE_UNKNOWN ：未知
                        break;
                    case 1: // TYPE_NONE
                        break;
                    case 2: // TYPE_LOCAL_DISK : 本地硬盘
                        // 文件系统总大小
                        break;
                    case 3:// TYPE_NETWORK ：网络
                        break;
                    case 4:// TYPE_RAM_DISK ：闪存
                        break;
                    case 5:// TYPE_CDROM ：光驱
                        break;
                    case 6:// TYPE_SWAP ：页面交换
                        break;
                }

                ioMonitorBean.setTimeStamp(new Date());
                ioMonitorBean.setReadRate(usage.getDiskReads());
                ioMonitorBean.setWriteRate(usage.getDiskWrites());
            }
        }catch (SigarException e){
            e.printStackTrace();
        }

        return ioMonitorBean;
    }


    public JVMMonitorBean getJVMInfo(){
        JVMMonitorBean jvmMonitorBean = new JVMMonitorBean();
        try{
            Runtime r = Runtime.getRuntime();
            Properties props = System.getProperties();
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            String ip = addr.getHostAddress();
            Map<String, String> map = System.getenv();
            jvmMonitorBean.setTimeStamp(new Date());                         //时间
            jvmMonitorBean.setTotal(Long.toString(r.totalMemory()));				    //JVM总量
            jvmMonitorBean.setFree(Long.toString(r.freeMemory()));					    //JVM空闲量
            jvmMonitorBean.setUsed(Long.toString(r.totalMemory()-r.freeMemory()));    //JVM使用量
        }catch (Exception e){
            e.printStackTrace();
        }

        return jvmMonitorBean;
    }

    public NetMonitorBean getNetInfo(){
        NetMonitorBean netMonitorBean = new NetMonitorBean();
        try {
            String ifNames[] = sigar.getNetInterfaceList();
            long rxbps;
            long txbps;
            InetAddress addr = InetAddress.getLocalHost();
            String ip=addr.getHostAddress();//获得本机IP
            for (int i = 0; i < ifNames.length; i++) {
                String name = ifNames[i];
                NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
                if(ifconfig.getAddress().equals(ip)){
                    NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);
                    try {
                        long start = System.currentTimeMillis();
                        NetInterfaceStat statStart = sigar.getNetInterfaceStat(name);
                        long rxBytesStart = statStart.getRxBytes();   //字节  ----转化为B   ----- 转化为KB  字节/8/
                        long txBytesStart = statStart.getTxBytes();
                        Thread.sleep(1000);    //设置休眠时间为1秒
                        long end = System.currentTimeMillis();
                        NetInterfaceStat statEnd = sigar.getNetInterfaceStat(name);
                        long rxBytesEnd = statEnd.getRxBytes();
                        long txBytesEnd = statEnd.getTxBytes();

                        rxbps = (rxBytesEnd - rxBytesStart)/((end-start)/1000);  //字节/秒
                        txbps = (txBytesEnd - txBytesStart)/((end-start)/1000);
                        netMonitorBean.setRcvdRate(rxbps);
                        netMonitorBean.setSendRate(txbps);
                        netMonitorBean.setTimeStamp(new Date());
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
                if ((ifconfig.getFlags() & 1L) <= 0L) {
                    continue;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return netMonitorBean;
    }


    public DiskMonitorBean getDiskInfo(){
        DiskMonitorBean diskMonitorBean = new DiskMonitorBean();
        try{
            FileSystem [] fileSystemArray = sigar.getFileSystemList();
            long total = 0L;
            long used  = 0L;
            long free  = 0L;
            for ( FileSystem fileSystem:fileSystemArray ) {
                FileSystemUsage fileSystemUsage = null;
                try {
                    fileSystemUsage = sigar.getFileSystemUsage(fileSystem.getDirName());
                } catch (SigarException e) {//当fileSystem.getType()为5时会出现该异常——此时文件系统类型为光驱
                    continue;
                }
                total+= fileSystemUsage.getTotal();
                used+=fileSystemUsage.getUsed();
                free+=fileSystemUsage.getFree();
                diskMonitorBean.setTimeStamp(new Date());
                diskMonitorBean.setTotal(Long.toString(total));
                diskMonitorBean.setUsed(Long.toString(used));
                diskMonitorBean.setFree(Long.toString(free));
            }
        }catch (SigarException e){
            e.printStackTrace();
        }

        return diskMonitorBean;
    }


    public CheckAllBean getAllInfo(){

        CheckAllBean result = new CheckAllBean();
        // JVM信息
        JVMMonitorBean jvmMonitorBean = new JVMMonitorBean();
        jvmMonitorBean = getJVMInfo();
        // cpu信息
        CPUMonitorBean cpuMonitorBean = new CPUMonitorBean();
        cpuMonitorBean = getCPUInfo();
        // 内存信息
        MemMonitorBean memMonitorBean = new MemMonitorBean();
        memMonitorBean = getMemInfo();
        // 文件系统信息
        DiskMonitorBean diskMonitorBean = new DiskMonitorBean();
        diskMonitorBean = getDiskInfo();
        // IO系统信息
        IOMonitorBean ioMonitorBean = new IOMonitorBean();
        ioMonitorBean = getIOInfo();
        // Net系统信息
        NetMonitorBean netMonitorBean = new NetMonitorBean();
        netMonitorBean = getNetInfo();

        result.setJvmMonitorBean(jvmMonitorBean);
        result.setCpuMonitorBean(cpuMonitorBean);
        result.setMemMonitorBean(memMonitorBean);
        result.setDiskMonitorBean(diskMonitorBean);
        result.setIoMonitorBean(ioMonitorBean);
        result.setNetMonitorBean(netMonitorBean);
        result.setTimeStamp(new Date());

        return result;
    }

    public static void main(String[]  args){
        SigarService monitor = new SigarService();
        CheckAllBean resultSet = monitor.getAllInfo();
        System.out.println(resultSet.getCpuMonitorBean().getRate());
    }
}
