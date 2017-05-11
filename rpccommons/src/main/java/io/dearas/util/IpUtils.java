package io.dearas.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by tnp on 03/05/2017.
 */
public class IpUtils {
    public static String getLocalIp() throws SocketException {
        Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        String ipStr = "";
        while (allNetInterfaces.hasMoreElements())
        {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            System.out.println(netInterface.getName());
            if("en0".equalsIgnoreCase(netInterface.getName())){
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements())
                {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address)
                    {
                        System.out.println("本机的IP = " + ip.getHostAddress());
                        return ip.getHostAddress(); // 返回第一个ip
                    }
                }
            }

        }
        return ipStr;
    }
}
