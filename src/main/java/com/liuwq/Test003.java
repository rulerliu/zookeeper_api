package com.liuwq;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class Test003 {

    //参数1 连接地址
    private static final String ADDRES = "127.0.0.1:2181";
    // 参数2 zk超时时间
    private static final int TIMEOUT = 5000;
    // 计数器
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException, KeeperException, IOException, NoSuchAlgorithmException {

//        test();
        test2();
    }

    private static void test2() throws InterruptedException, KeeperException, IOException, NoSuchAlgorithmException  {
        // 1.创建zooKeeper连接
        ZooKeeper zooKeeper = new ZooKeeper(ADDRES, TIMEOUT, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                Event.KeeperState state = watchedEvent.getState();
                System.out.println(state.name());
                if (state == Event.KeeperState.SyncConnected) {
                    System.out.println(">>>zk连接成功");
                    countDownLatch.countDown();
                }
            }
        });

        System.out.println(">>>zk正等待连接");
        countDownLatch.await();
        System.out.println(">>>开始获取数据");

        // 2.设置zk连接账号
        zooKeeper.addAuthInfo("digest", "guest:guest123".getBytes());

        byte[] bytes = zooKeeper.getData("/memberservice", null, new Stat());
        System.out.println(">>>data:" + new String(bytes));

        Stat stat = zooKeeper.setData("/memberservice", "mayikt2".getBytes(), -1);
        System.out.println(stat);

    }

    public static void test() throws InterruptedException, KeeperException, IOException, NoSuchAlgorithmException {
        // 1.创建zooKeeper连接
        ZooKeeper zooKeeper = new ZooKeeper(ADDRES, TIMEOUT, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                Event.KeeperState state = watchedEvent.getState();
                System.out.println(state.name());
                if (state == Event.KeeperState.SyncConnected) {
                    System.out.println(">>>zk连接成功");
                    countDownLatch.countDown();
                }
            }
        });

        System.out.println(">>>zk正等待连接");
        countDownLatch.await();
        System.out.println(">>>开始创建节点");

        // 2.创建账号权限 admin可以实现读写操作
        Id id1 = new Id("digest", DigestAuthenticationProvider.generateDigest("admin:admin123"));
        ACL acl1 = new ACL(ZooDefs.Perms.ALL, id1);

        // 3.创建权限guest 只允许做读操作
        Id id2 = new Id("digest", DigestAuthenticationProvider.generateDigest("guest:guest123"));
        ACL acl2 = new ACL(ZooDefs.Perms.READ, id2);

        // 4.添加该账号
        ArrayList<ACL> aclList = new ArrayList<ACL>();
        aclList.add(acl1);
        aclList.add(acl2);

        // 5.创建该节点
        String s1 = zooKeeper.create("/memberservice", "mayikt".getBytes(), aclList, CreateMode.PERSISTENT);
        System.out.println("s1:" + s1);


        zooKeeper.close();
    }

}
