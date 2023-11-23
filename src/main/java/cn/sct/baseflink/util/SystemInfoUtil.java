package cn.sct.baseflink.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @author dengpengyu
 * @date 2022/12/5 9:55
 */
public class SystemInfoUtil {
    /**
     * 获取CPU序列号
     *
     * @return cpu串号
     * @throws IOException
     */
    public static String getCpuId() throws IOException {

        // linux，windows命令
        // linux，windows命令
        String[] linux = {"sudo", "/usr/bin/bash", "-c", "dmidecode -t 4 | grep ID |sort -u |awk -F': ' '{print $2}'"};
        String[] windows = {"wmic", "cpu", "get", "ProcessorId"};

        // 获取系统信息
        String property = System.getProperty("os.name");
        Process process = Runtime.getRuntime().exec(property.contains("Window") ? windows : linux);
        Scanner sc = new Scanner(process.getInputStream(), "utf-8");
        process.getOutputStream().close();

        return sc.next() +
                sc.next() +
                sc.next() +
                sc.next() +
                sc.next() +
                sc.next() +
                sc.next() +
                sc.next();
    }

    /**
     *
     * @return 多个mac地址
     * @throws Exception
     */
    public static List<String> getMacList() throws Exception {
        java.util.Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        StringBuilder sb = new StringBuilder();
        ArrayList<String> tmpMacList = new ArrayList<>();
        while (en.hasMoreElements()) {
            NetworkInterface iface = en.nextElement();
            List<InterfaceAddress> addrs = iface.getInterfaceAddresses();
            for (InterfaceAddress addr : addrs) {
                InetAddress ip = addr.getAddress();
                NetworkInterface network = NetworkInterface.getByInetAddress(ip);
                if (network == null) {
                    continue;
                }
                byte[] mac = network.getHardwareAddress();
                if (mac == null) {
                    continue;
                }
                sb.delete(0, sb.length());
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                }
                tmpMacList.add(sb.toString());
            }
        }
        if (tmpMacList.size() <= 0) {
            return tmpMacList;
        }
        /**
         * 去重，别忘了同一个网卡的ipv4,ipv6得到的mac都是一样的，肯定有重复，下面这段代码是。。流式处理
         */
        return tmpMacList.stream().distinct().collect(Collectors.toList());
    }
}
