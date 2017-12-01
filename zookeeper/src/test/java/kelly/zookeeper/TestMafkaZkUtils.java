//package kelly.zookeeper;
//
//import com.alibaba.dubbo.common.utils.StringUtils;
//import com.sun.tools.javac.util.Pair;
//import org.I0Itec.zkclient.exception.ZkNoNodeException;
//import org.I0Itec.zkclient.exception.ZkNodeExistsException;
//import org.apache.zookeeper.data.Stat;
//import org.slf4j.Logger;
//import org.springframework.util.NumberUtils;
//
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//import  org.I0Itec.zkclient.ZkClient;
//
//import static javafx.scene.input.KeyCode.Z;
//
///**
// * Created with IntelliJ IDEA. * User: lizhitao * Date: 14-6-6 * Time: 下午3:01 * To change this template use File | Settings | File Templates.
// */
//public class TestMafkaZkUtils {
//    private static final Logger logger = Logger.getLogger(TestMafkaZkUtils.class);
//    /********** kafka zk root conf *********/
//    public static final String ConsumersPath = "/consumers";
//    public static final String BrokerIdsPath = "/brokers/ids";
//    public static final String BrokerTopicsPath = "/brokers/topics";
//    public static final String TopicConfigPath = "/config/topics";
//    public final String TopicConfigChangesPath = "/config/changes";
//    public static final String ControllerPath = "/controller";
//    public static final String ControllerEpochPath = "/controller_epoch";
//    public static final String ReassignPartitionsPath = "/admin/reassign_partitions";
//    public static final String DeleteTopicsPath = "/admin/delete_topics";
//    public static final String PreferredReplicaLeaderElectionPath = "/admin/preferred_replica_election";
//
//    public static String getTopicPath(String topic) {
//        return BrokerTopicsPath + "/" + topic;
//    }
//
//    public static String getTopicPartitionsPath(String topic) {
//        return getTopicPath(topic) + "/partitions";
//    }
//
//    public static String getTopicConfigPath(String topic) {
//        return TopicConfigPath + "/" + topic;
//    }
//
//    public static String getDeleteTopicPath(String clusterName, String topic) {
//        return DeleteTopicsPath + "/" + topic;
//    }
//
//    public static String getBrokerIdsPath() {
//        return BrokerIdsPath;
//    }
//
//    public static List<MafkaBroker> getAllBrokersInCluster(ZkClient zkClient, String clusterName) {
//        if (!pathExists(zkClient, getBrokerIdsPath())) {
//            throw new ZkNoNodeException(getBrokerIdsPath());
//        }
//        List<String> brokerIds = getChildrenParentMayNotExist(zkClient, getBrokerIdsPath());
//        Collections.sort(brokerIds); //
//        List<String> MafkaBroker getBrokerInfo(ZkClient zkClient, int brokerId)
//        List<MafkaBroker> retList = new ArrayList<MafkaBroker>();
//        for (String brokerIdStr : brokerIds) {
//            MafkaBroker broker = getBrokerInfo(zkClient, Integer.valueOf(brokerIdStr));
//            if (broker != null) retList.add(broker);
//        }
//        return retList;
//    }
//
//    public static String getMetadataBrokerList(ZkClient zkClient, String clusterName) {
//        List<MafkaBroker> brokers = TestMafkaZkUtils.getAllBrokersInCluster(zkClient, clusterName);
//        StringBuffer sb = new StringBuffer();
//        for (MafkaBroker broker : brokers) {
//            logger.info(broker);
//            if (sb.length() > 0) sb.append(",");
//            sb.append(broker.getHost()).append(":").append(broker.getPort());
//        }
//        return sb.toString();
//    }
//
//    /**
//     * get children nodes name * @param zkClient zkClient * @param path full path * @return children nodes name or null while path not exist
//     */
//    public static List<String> getChildrenParentMayNotExist(ZkClient zkClient, String path) {
//        try {
//            return zkClient.getChildren(path);
//        } catch (ZkNoNodeException e) {
//            return null;
//        } catch (Exception ex) {
//            logger.error("getChildrenParentMayNotExist invoke fail!", ex);
//            return null;
//        }
//    }
//
//    /**
//     * This API takes in a broker id, queries zookeeper for the broker metadata and returns the metadata for that broker * or throws an exception if the broker dies before the query to zookeeper finishes * @param brokerId The broker id * @param zkClient The zookeeper client connection * @return An optional MafkaBroker object encapsulating the broker metadata
//     */
//    public static MafkaBroker getBrokerInfo(ZkClient zkClient, int brokerId) { //Pair<String, Stat>
//        String brokerInfoStr = readDataMaybeNull(zkClient, getBrokerIdsPath() + "/" + brokerId).getLeft();
//        if (StringUtils.isNotEmpty(brokerInfoStr)) {
//            return MafkaBroker.createBroker(brokerId, brokerInfoStr);
//        } else {
//            return null;
//        }
//    }
//
//    public static Pair<String, Stat> readData(ZkClient client, String path) {
//        Stat stat = new Stat();
//        String dataStr = client.readData(path, stat);
//        return Pair.of(dataStr, stat);
//    }
//
//    public static Pair<String, Stat> readDataMaybeNull(ZkClient client, String path) {
//        Stat stat = new Stat();
//        Pair<String, Stat> dataAndStat = null;
//        try {
//            dataAndStat = Pair.of((String) client.readData(path, stat), stat);
//        } catch (ZkNoNodeException nkex) {
//            return Pair.of(null, stat);
//        } catch (Exception ex) {
//            logger.error(ex);
//        }
//        return dataAndStat;
//    }
//
//    /**
//     * Update the value of a persistent node with the given path and data. * create parrent directory if necessary. Never throw NodeExistException.
//     */
//    public void updateEphemeralPath(ZkClient client, String path, String data) {
//        try {
//            client.writeData(path, data);
//        } catch (ZkNoNodeException zkex) {
//            createParentPath(client, path);
//            client.createEphemeral(path, data);
//        } catch (Exception ex) {
//            logger.error(ex);
//        }
//    }
//
//    public static boolean deletePath(ZkClient client, String path) {
//        try {
//            return client.delete(path);
//        } catch (ZkNoNodeException zkex) { // this can happen during a connection loss event, return normally
//            logger.info(path + " deleted during connection loss; this is ok");
//            return false;
//        } catch (Exception ex) {
//            logger.error(ex);
//        }
//        return false;
//    }
//
//    public void deletePathRecursive(ZkClient client, String path) {
//        try {
//            client.deleteRecursive(path);
//        } catch (ZkNoNodeException zkex) { // this can happen during a connection loss event, return normally
//            logger.info(path + " deleted during connection loss; this is ok");
//        } catch (Exception ex) {
//            logger.error(ex);
//        }
//    }
//
//    public void maybeDeletePath(String zkUrl, String dir) {
//        try {
//            ZkClient zk = new ZkClient(zkUrl, 30 * 1000, 30 * 1000, new MafkaZKStrSerializer());
//            zk.deleteRecursive(dir);
//            zk.close();
//        } catch (Exception ex) {
//            logger.error(ex);
//        }
//    }
//
//    /**
//     * make sure a persistent path exists in ZK. Create the path if not exist.
//     */
//    public static void makeSurePersistentPathExists(ZkClient client, String path) {
//        if (!client.exists(path))
//            client.createPersistent(path, true); // won't throw NoNodeException or NodeExistsException
//    }
//
//    // /** * create the parent path */
//    private static void createParentPath(ZkClient client, String path) {
//        String parentDir = path.substring(0, path.lastIndexOf('/'));
//        if (parentDir.length() != 0) client.createPersistent(parentDir, true);
//    }
//
//    /**
//     * Create an ephemeral node with the given path and data. Create parents if necessary.
//     */
//    private static void createEphemeralPath(ZkClient client, String path, String data) {
//        try {
//            client.createEphemeral(path, data);
//        } catch (ZkNoNodeException znex) {
//            createParentPath(client, path);
//            client.createEphemeral(path, data);
//        }
//    }
//
//    /**
//     * Create an ephemeral node with the given path and data. * Throw NodeExistException if node already exists.
//     */
//    public static void createEphemeralPathExpectConflict(ZkClient client, String path, String data) {
//        try {
//            createEphemeralPath(client, path, data);
//        } catch (ZkNodeExistsException zkex) { // this can happen when there is connection loss; make sure the data is what we intend to write
//            String storedData = null;
//            try {
//                storedData = readData(client, path).getLeft();
//            } catch (ZkNoNodeException znex) {
//                logger.error(znex);
//            }
//            if (storedData == null || storedData != data) {
//                logger.info("conflict in " + path + " data: " + data + " stored data: " + storedData);
//                throw zkex;
//            } else { // otherwise, the creation succeeded, return normally
//                logger.info(path + " exists with value " + data + " during connection loss; this is ok");
//            }
//        }
//    }
//
//    /**
//     * Create an persistent node with the given path and data. Create parents if necessary.
//     */
//    public static void createPersistentPath(ZkClient client, String path, String data) {
//        try {
//            client.createPersistent(path, data);
//        } catch (ZkNoNodeException znex) {
//            createParentPath(client, path);
//            client.createPersistent(path, data);
//        }
//    }
//
//    public String createSequentialPersistentPath(ZkClient client, String path, String data) {
//        return client.createPersistentSequential(path, data);
//    }
//
//    public static List<String> getAllPartitionsByTopic(ZkClient zkClient, String topic) {
//        return getChildren(zkClient, getTopicPartitionsPath(topic));
//    }
//
//    /**
//     * Check if the given path exists
//     */
//    public static boolean pathExists(ZkClient zkClient, String path) {
//        logger.info("pathExists:" + path + " zkClient:" + zkClient);
//        return zkClient.exists(path);
//    }
//
//    /**
//     * 功能介绍：解析partitions列表数据,partitions以字符串方式存储，用逗号分隔。 * @param zkClient * @return
//     */
//    public static String getAllPartitionsSepCommaByTopic(ZkClient zkClient, String topic) {
//        logger.info("getTopicPartitionsPath(clusterName, topic):" + getTopicPartitionsPath(topic));
//        if (!pathExists(zkClient, getTopicPartitionsPath(topic))) {
//            throw new ZkNoNodeException(getTopicPartitionsPath(topic));
//        }
//        List<String> partitions = getChildren(zkClient, getTopicPartitionsPath(topic));
//        Collections.sort(partitions, new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                final int p1 = (o1 == null) ? 0 : Integer.parseInt(o1);
//                final int p2 = (o2 == null) ? 0 : Integer.parseInt(o2);
//                return NumberUtils.compare(p1, p2);
//            }
//        });
//        StringBuffer parts = new StringBuffer();
//        for (String partition : partitions) {
//            if (parts.length() > 0) parts.append(",");
//            parts.append(partition);
//        }
//        return parts.toString();
//    }
//
//    public static List<String> getChildren(ZkClient client, String path) {
//        return client.getChildren(path);
//    }
//
//    public static List<MafkaBroker> getAllBrokersInCluster(ZkClient zkClient) {
//        List<String> brokerIds = getChildrenParentMayNotExist(zkClient, getBrokerIdsPath());
//        Collections.sort(brokerIds); //List<String> MafkaBroker getBrokerInfo(ZkClient zkClient, int brokerId)
//        List<MafkaBroker> retList = new ArrayList<MafkaBroker>();
//        for (String brokerIdStr : brokerIds) {
//            MafkaBroker broker = getBrokerInfo(zkClient, Integer.valueOf(brokerIdStr));
//            if (broker != null) retList.add(broker);
//        }
//        return retList;
//    }
//
//    public static void main(String[] args) {
//        ZkClient zkClient; //kafka zk根节点
//        String zkConnect = "192.168.2.225:2181,192.168.2.225:2182,192.168.2.225:2183/config/mobile/mq/mafka01";
//        int zkSessionTimeoutMs = 5000;
//        int zkConnectionTimeoutMs = 5000;
//        zkClient = new ZkClient(zkConnect, zkSessionTimeoutMs, zkConnectionTimeoutMs, new MafkaZKStrSerializer()); //获取所有broker信息
//        System.out.println(getAllBrokersInCluster(zkClient)); //获取所有partitions信息
//        System.out.println(getAllPartitionsSepCommaByTopic(zkClient, "cluster-switch-topic"));
//    }
//}