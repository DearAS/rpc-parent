package io.dearas.zookeeper;

import io.dearas.util.IpUtils;
import io.dearas.vo.ZkProvider;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by tnp on 03/05/2017.
 */
public class ZkUtils {
    public static void main(String[] args) throws KeeperException, InterruptedException {
        ZkProvider provider = new ZkProvider();
        provider.setInterfaceName("Test.Interface");
        try {
            provider.setIpAddr(IpUtils.getLocalIp());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        provider.setPort("8000");
        provider.setTimestamp(System.currentTimeMillis());
//        zooKeeper.exists(parentName,null);
        ZkUtils.createProvider(provider);
    }


    private static String parentName="/RPC_";
    private static String subComsumer = "customer";
    private static String subProvider = "providers";
    private static String zkAddr = "cs.24bao.com.cn:21810";
    private static Integer timeout = 6000;
    private static ZooKeeper zooKeeper;
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
     * 从zk中获取接口数据
     */
    public static String getAddrFromInterface(String interfaceName) throws KeeperException, InterruptedException {
        synchronized (new Object()){
            zkConnection();
        }
        countDownLatch.await();

        String addr="";
        if((zooKeeper.exists(parentName+"/"+interfaceName,null))!=null){
            List<String> list = zooKeeper.getChildren(parentName + "/" + interfaceName +"/"+ subProvider, null);
            if(list!=null && list.size() > 0){
                String str = list.get(0);
                byte[] data = zooKeeper.getData(parentName + "/" + interfaceName +"/"+ subProvider + "/" + str, false, null);
                addr = new String(data);
            }
        }
        return addr;

    }
    /**
     * 创建rpc提供者
     * @param provider
     */
    public static void createProvider(ZkProvider provider){

        synchronized (new Object()){
            zkConnection();
        }

        try {
            countDownLatch.await();

            createParent();
            // 创建接口节点/临时节点
            createInterfaceNode(provider);
            // 创建提供者节点-临时节点
            createProviderNode(provider);
            /**
             * 创建临时节点。不带序列，zookeeper.close()连接关闭，则节点自动消失。
             */
            String zNodeName = subProvider +"&"+provider.getIpAddr() +":" + provider.getPort() + "&"
                    +provider.getInterfaceName() + "&" + provider.getTimestamp();
            zooKeeper.create(parentName+"/"+provider.getInterfaceName()+"/"+subProvider+"/"+zNodeName,(provider.getIpAddr() +":" + provider.getPort()) .getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void zkConnection() {
        if(zooKeeper != null){

        }else{
            try {
                // 加载zookeeper客户端
                zooKeeper = new ZooKeeper(zkAddr, timeout, new Watcher() {
                    // Watcher zk监听器
                    public void process(WatchedEvent event) {
                        // 如果连接成功
                        if(Event.KeeperState.SyncConnected == event.getState()){
                            countDownLatch.countDown(); // -1
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createProviderNode(ZkProvider provider) throws KeeperException, InterruptedException {
        if(zooKeeper.exists(parentName+"/"+provider.getInterfaceName()+"/"+subProvider,null) == null){
            zooKeeper.create(parentName+"/"+provider.getInterfaceName()+"/"+subProvider,null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    private static void createInterfaceNode(ZkProvider provider) throws KeeperException, InterruptedException {
        if(zooKeeper.exists(parentName+"/"+provider.getInterfaceName(),null) == null){
            zooKeeper.create(parentName+"/"+provider.getInterfaceName(),null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    private static void createParent() throws KeeperException, InterruptedException {
        if(zooKeeper.exists(parentName,null) == null){
            // 创建父节点
            zooKeeper.create(parentName,null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }
}
