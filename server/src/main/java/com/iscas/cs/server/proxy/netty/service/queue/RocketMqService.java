package com.iscas.cs.server.proxy.netty.service.queue;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.iscas.cs.server.proxy.util.ConfigUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.TopicConfig;
import org.apache.rocketmq.common.admin.ConsumeStats;
import org.apache.rocketmq.common.admin.OffsetWrapper;
import org.apache.rocketmq.common.admin.TopicOffset;
import org.apache.rocketmq.common.admin.TopicStatsTable;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.body.ClusterInfo;
import org.apache.rocketmq.common.protocol.body.TopicList;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @program: cache-server
 * @description: rocketmq操作类
 * @author: LiangJian
 * @create: 2019-07-11 13:49
 **/
public class RocketMqService implements IQueueService {
    private static String nameServerAddr;
    private static String brokerName;
    private static DefaultMQAdminExt mqAdminExt;
    private Map<String, DefaultMQProducer> registedProducers = new HashMap<>();

    static {
        Properties props = new Properties();
        InputStream is = ConfigUtils.class.getResourceAsStream("/rocketmq.properties");
        try {
            props.load(is);
            nameServerAddr = props.getProperty("rocketmq.nameserver");
            brokerName = props.getProperty("rocketmq.brokername");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null == mqAdminExt) {
            try {
                mqAdminExt = new DefaultMQAdminExt();
                mqAdminExt.setInstanceName(Long.toString(System.currentTimeMillis()));
                mqAdminExt.setNamesrvAddr(nameServerAddr);
                mqAdminExt.start();
            } catch (Exception e) {
                throw new RuntimeException("RocketMQ监控客户端启动失败！", e);
            }
        }
    }

    public RocketMqService() {
        Runtime.getRuntime().addShutdownHook(new cleaner());
    }

    /*
     * @description: 生产者注册
     * @auther: LiangJian
     * @date: 2019/7/12
     * @param: groupName 组名
     * @param: producer 生产者对象
     * @return: void
     **/
    private void registerProducer(String groupName, DefaultMQProducer producer) {
        this.registedProducers.put(groupName, producer);
    }

    /*
     * @description: 在写入数据前创建topic
     * @auther: LiangJian
     * @date: 2019/7/12
     * @param:  topic 主题名称
     * @return: void
     **/
    private void prepareTopic(String topic) {
        if (!isTopicExist(topic)) {
            createOrUpdateTopic(topic);
        }
    }

    /*
     * @description: 向队列中发送一条消息
     * @auther: LiangJian
     * @date: 2019/7/12
     * @param: id 消息key
     * @param: data 消息体
     * @param: topic 主题名
     * @param: user 主题中的tag名，占位
     * @return: void
     **/
    @Override
    public void put(String id, byte[] data, String topic, String user) {
        //创建topic
        prepareTopic(topic);
        //用主题名+"ProducerGroup"作为组名，用于负载均衡
        String groupName = topic + "ProducerGroup";
        DefaultMQProducer producer = null;

        //注册producer，避免重复创建连接
        if (!registedProducers.containsKey(groupName)) {
            producer = new DefaultMQProducer(groupName);
            producer.setNamesrvAddr(nameServerAddr);
            try {
                producer.start();
                registerProducer(groupName, producer);
            } catch (MQClientException e) {
                throw new RuntimeException("RocketMQ生产者启动失败！", e);
            }
        } else {
            producer = registedProducers.get(groupName);
        }

        try {
            //发送消息
            SendResult sendResult = producer.send(new Message(topic, user, id, data));
            System.out.printf("%s%n", sendResult);
        } catch (Exception e) {
            throw new RuntimeException("放入RocketMQ失败！", e);
        }
    }

    /*
     * @description: 在队首拉取一条消息
     * @auther: LiangJian
     * @date: 2019/7/12
     * @param: topic 主题名
     * @return: 消息byte数组
     **/
    @Override
    public byte[] get(String topic) {
        String groupName = topic + "ConsumerGroup";
        byte[] result = null;

        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer(groupName);
        consumer.setNamesrvAddr(nameServerAddr);
        Set<MessageQueue> mqs = null;
        try {
            consumer.start();
            //获取该topic下的所有队列（创建topic时已指定为1个）
            mqs = consumer.fetchSubscribeMessageQueues(topic);
        } catch (MQClientException e) {
            e.printStackTrace();
        }

        //遍历所有quenes（只有1个）
        for (MessageQueue mq : mqs) {
            //只进行1次拉取
            for (int i = 0; i < 1; i++) {
                try {
                    //获取上次消费消息offset
                    long offset = consumer.fetchConsumeOffset(mq, true);
                    if(offset < 0) {
                        offset = 0;
                    }
                    //设置1次拉取1条消息（manNums=1）
                    PullResult pullResult = consumer.pullBlockIfNotFound(mq, null, offset, 1);
                    switch (pullResult.getPullStatus()) {
                        //根据结果状态，如果找到消息，进行消费
                        case FOUND:
                            List<MessageExt> messageExtList = pullResult.getMsgFoundList();
                            for (MessageExt m : messageExtList) {
                                result = m.getBody();
                                //将本地消费者offset同步到Broker，当前情况为offset+1
                                consumer.getOffsetStore().updateConsumeOffsetToBroker(mq, pullResult.getNextBeginOffset(), true);
                            }
                            break;
                        case NO_MATCHED_MSG:
                            break;
                        case NO_NEW_MSG:
                            break;
                        case OFFSET_ILLEGAL:
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //拉取1条消息后关闭consumer，避免一直消费
        consumer.shutdown();
        return result;
    }

    /*
     * @description: 判断topic是否存在
     * @auther: LiangJian
     * @date: 2019/7/12
     * @param: topic 主题名
     * @return: 存在=true，不存在=false
     **/
    private boolean isTopicExist(String topic) {
        try {
            TopicList topicList = mqAdminExt.fetchAllTopicList();
            topicList.setTopicList(Sets.newHashSet(Iterables.filter(topicList.getTopicList(), new Predicate<String>() {
                @Override
                public boolean apply(String s) {
                    return !(s.startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX) || s.startsWith(MixAll.DLQ_GROUP_TOPIC_PREFIX));
                }
            })));

            return topicList.getTopicList().contains(topic);

        } catch (Exception e) {
            throw new RuntimeException("RocketMQ获取所有topics失败！", e);
        }
    }

    /*
     * @description: 创建topic
     * @auther: LiangJian
     * @date: 2019/7/12
     * @param: topic 主题名
     * @return: void
     **/
    private void createOrUpdateTopic(String topic) {
        //创建topic配置类，指定readQueueNums = 1, writeQueueNums = 1
        TopicConfig topicConfig = new TopicConfig(topic, 1, 1, 6);
        try {
            //获取集群信息
            ClusterInfo clusterInfo = mqAdminExt.examineBrokerClusterInfo();
            //创建topic
            mqAdminExt.createAndUpdateTopicConfig(clusterInfo.getBrokerAddrTable().get(brokerName).selectBrokerAddr(), topicConfig);
        } catch (Exception e) {
            throw new RuntimeException("RocketMQ创建topic失败！", e);
        }
    }

    /*
     * @description: 判断topic是否为空
     * @auther: LiangJian
     * @date: 2019/7/12
     * @param: topic 主题名
     * @return: 空=true，非空=false
     **/
    @Override
    public boolean isEmpty(String topic) {
        //初始化监控客户端
        if (null == mqAdminExt) {
            try {
                mqAdminExt = new DefaultMQAdminExt();
                mqAdminExt.setInstanceName(Long.toString(System.currentTimeMillis()));
                mqAdminExt.setNamesrvAddr(nameServerAddr);
                mqAdminExt.start();
            } catch (Exception e) {
                throw new RuntimeException("RocketMQ监控客户端启动失败！", e);
            }
        }

        long diffTotal = 0L;
        try{
            //当消费端未订阅改topic时，会抛出异常，需要判断队列长度是否为空
            ConsumeStats consumeStats = mqAdminExt.examineConsumeStats(topic + "ConsumerGroup");
            List<MessageQueue> mqList = new LinkedList();
            mqList.addAll(consumeStats.getOffsetTable().keySet());
            Collections.sort(mqList);
            //遍历所有的队列，计算堆积量
            for (MessageQueue mq : mqList) {
                //只计算group下此生产端发送对应的Topic
                if(topic.equals(mq.getTopic())){
                    OffsetWrapper offsetWrapper = (OffsetWrapper)consumeStats.getOffsetTable().get(mq);
                    long diff = offsetWrapper.getBrokerOffset() - offsetWrapper.getConsumerOffset();
                    diffTotal += diff;
                }
            }
        }catch(Throwable e){
            //处理该topic还没有消费者时的情况
            try {
                TopicStatsTable topicStatsTable = mqAdminExt.examineTopicStats(topic);
                List<MessageQueue> mqList = new LinkedList();
                mqList.addAll(topicStatsTable.getOffsetTable().keySet());
                Collections.sort(mqList);
                diffTotal = 0L;
                for (MessageQueue mq : mqList) {
                    TopicOffset topicOffset = (TopicOffset) topicStatsTable.getOffsetTable().get(mq);
                    long diff = topicOffset.getMaxOffset() - topicOffset.getMinOffset();
                    diffTotal += diff;
                }
            } catch (Throwable e1) {
                diffTotal = 0L;
            }
        }
        return diffTotal == 0;
    }

    /*
     * @description: 测试方法
     * @auther: LiangJian
     * @date: 2019/7/12
     * @param:  * @param null
     * @return:
     **/
    public static void main(String[] args) throws Exception{
        byte[] msg1 = "first".getBytes();
        String key1 = "0001";
        String topic1 = "cacheTopic1";
        String tag1 = "user1";

        byte[] msg2 = "second".getBytes();
        String key2 = "0002";
        String topic2 = "cacheTopic2";
        String tag2 = "user2";

        byte[] msg3 = "third".getBytes();
        String key3 = "0003";
        String topic3 = "cacheTopic1";
        String tag3 = "user3";

        byte[] msg4 = "forth".getBytes();
        String key4 = "0004";
        String topic4 = "cacheTopic4";
        String tag4 = "user4";

        RocketMqService rocketMqService = new RocketMqService();
        boolean isEmpty = rocketMqService.isEmpty(topic4);
        for(int i = 0; i < 10; i++) {
            rocketMqService.put(key4, msg4, topic4, tag4);
        }
        rocketMqService.put(key1, msg1, topic1, tag1);
        rocketMqService.put(key2, msg2, topic2, tag2);
        rocketMqService.put(key3, msg3, topic3, tag3);

        for(int i = 0; i < 5; i++) {
            byte[] result = rocketMqService.get(topic4);
            if (result.length > 0) {
                System.out.println("fff " + new String(result));
            }
        }
        boolean afterConsumer = rocketMqService.isEmpty(topic4);
    }

    @Override
    public boolean isEmpty(String topic, String user) {
        return false;
    }

    @Override
    public byte[] get(String topic, String user) {
        return new byte[0];
    }

    @Override
    public List<String> getAllIds(String topic) {
        return null;
    }

    @Override
    public byte[] getById(String topic, String id) {
        return new byte[0];
    }

    @Override
    public List<String> getAllIds(String topic, String user) {
        return null;
    }

    @Override
    public byte[] getById(String topic, String user, String id) {
        return new byte[0];
    }

    /*
     * @description: 程序退出后释放资源
     * @auther: LiangJian
     * @date: 2019/7/12
     * @param:
     * @return:
     **/
    private class cleaner extends Thread {
        @Override
        public void run() {
            //释放生产者
            for(DefaultMQProducer producer : registedProducers.values()){
                producer.shutdown();
            }
            //释放管理客户端
            mqAdminExt.shutdown();
        }
    }

}
