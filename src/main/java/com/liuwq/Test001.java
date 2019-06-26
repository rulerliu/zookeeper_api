package com.liuwq;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * @description:
 * @author: liuwq
 * @date: 2019/6/26 0026 下午 4:50
 * @version: V1.0
 */
public class Test001 {

    // 会话超时时间，设置为与系统默认时间一致
    private static final int SESSION_TIMEOUT = 3000000;

    private static ZooKeeper zk;

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {

        /**
         * 事件状态：Event.KeeperState.Disconnected、NoSyncConnected、SyncConnected、AuthFailed、ConnectedReadOnly、SaslAuthenticated、Expired
         *
         * 事件类型：Event.EventType.None、NodeCreated、NodeDeleted、NodeDataChanged、NodeChildrenChanged
         */
        zk = new ZooKeeper("localhost:2181", SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                Event.KeeperState state = event.getState();
                Event.EventType type = event.getType();
                String path = event.getPath();
                System.out.println("state:" + state + ",type:" + type + ",path:" + path);
                System.out.println("接收内容：" + event.toString());
            }
        });

        /**
         * acl:权限列表：提供默认的权限OPEN_ACL_UNSAFE、CREATOR_ALL_ACL、READ_ACL_UNSAFE
         * OPEN_ACL_UNSAFE：完全开放
         * CREATOR_ALL_ACL：创建该znode的连接拥有所有权限
         * READ_ACL_UNSAFE：所有的客户端都可读
         *
         * 自定义权限　　ACL aclIp = new ACL(ZooDefs.Perms.READ, new Id("ip","127.0.0.1"));
         *               ACL aclDigest = new ACL(ZooDefs.Perms.READ | ZooDefs.Perms.WRITE,
         *                  new Id("digest", DigestAuthenticationProvider.generateDigest("id:pass")));
         *
         * session设置权限　zk.addAuthInfo("digest", "id:pass".getBytes());　　
         */

        /**
         * createMode:节点类型
         * PERSISTENT：持久化节点
         * PERSISTENT_SEQUENTIAL：持久化有序节点
         * EPHEMERAL：临时节点（连接断开自动删除）
         * EPHEMERAL_SEQUENTIAL：临时有序节点（连接断开自动删除）
         */
        System.out.println("/n1. 创建 ZooKeeper 节点znode ： mynode, 数据： mydata ，权限：OPEN_ACL_UNSAFE ，节点类型： EPHEMERAL");
        zk.create("/mynode", "mydata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);


        System.out.println("/n2. 查看是否创建成功： ");
        System.out.println(new String(zk.getData("/mynode", true, null)));


        System.out.println("/n3. 修改节点数据 ");
        zk.setData("/mynode", "shenlan211314".getBytes(), -1);


        System.out.println("/n4. 查看是否修改成功： ");
        System.out.println(new String(zk.getData("/mynode", true, null)));

        System.out.println("/n5. 删除节点 ");
//        zk.delete("/mynode", -1);

        System.out.println("/n6. 查看节点是否被删除： ");
        System.out.println(" 节点状态： [" + zk.exists("/mynode", true) + "]");


        zk.close();
    }

}
