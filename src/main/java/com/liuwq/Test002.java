package com.liuwq;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class Test002 {

    //参数1 连接地址
    private static final String ADDRES = "127.0.0.1:2181";
    // 参数2 zk超时时间
    private static final int TIMEOUT = 5000;
    // 计数器
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException, KeeperException, IOException {

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

        // 2.创建临时节点
        String s1 = zooKeeper.create("/mayikt1", "mayikt".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("s1:" + s1);

        // 3.创建持久节点
        String s2 = zooKeeper.create("/mayikt2", "mayikt".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("s2:" + s2);

        // 4.创建持久节点带顺序
        String s3 = zooKeeper.create("/mayikt3", "mayikt".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        System.out.println("s3:" + s3);

        // 5.创建临时节点带顺序
        String s4 = zooKeeper.create("/mayikt4", "mayikt".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("s4:" + s4);

        zooKeeper.close();
    }

}
