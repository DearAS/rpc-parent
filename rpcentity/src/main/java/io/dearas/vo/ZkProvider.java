package io.dearas.vo;

/**
 * Zkprovider 提供者
 * Created by tnp on 03/05/2017.
 */
public class ZkProvider {
    // 接口的全限定类名
    private String interfaceName;
    private String ipAddr;
    private String port;
    private Long timestamp;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
