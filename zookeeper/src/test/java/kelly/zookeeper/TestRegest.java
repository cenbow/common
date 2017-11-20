package kelly.zookeeper;


import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.net.*;
import java.util.List;
import java.util.Map;

import static com.alibaba.dubbo.common.utils.NetUtils.getAvailablePort;
import static com.alibaba.dubbo.common.utils.NetUtils.getLocalAddress;

/**
 * Created by kelly.li on 17/11/18.
 */
public class TestRegest {

    @Test
    public void test1() {
        System.out.println(NetUtils.getLocalAddress());
        System.out.println(NetUtils.getLocalHost());
        System.out.println(NetUtils.getLogHost());
        URL url = new URL("mq", getLocalAddress().getHostAddress(), getAvailablePort());
        Map<String, String> parameter = Maps.newHashMap();
        parameter.put("app", "app1");
        parameter.put("version", "1.0");
        url = url.addParameters(parameter);
       // url.addParameter("aa","aa");
        String encodeUrl = URL.encode(url.toFullString());
        System.out.println(url.toFullString());
        System.out.println(encodeUrl);
    }


    public String getLocalHost(List<URL> registryURLs) {
        InetAddress address = getLocalAddress();
        String host = address.getHostAddress();

        boolean anyhost = false;

        //3. 若取出的是本地host, 则继续取host
        if (NetUtils.isInvalidLocalHost(host)) {
            anyhost = true;
            try {
                //4. 通过InetAddress的方式获取Host
                //默认读取本机hosts中hostname对应的IP
                //如: 你在hosts中配置了 leo 172.16.11.111
                //则读取的IP就是172.16.11.111
                host = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                // logger.warn(e.getMessage(), e);
            }
            if (NetUtils.isInvalidLocalHost(host)) {
                if (registryURLs != null && registryURLs.size() > 0) {
                    for (URL registryURL : registryURLs) {
                        try {
                            Socket socket = new Socket();
                            try {
                                //5. 通过Socket的方式获取Host
                                //一般解析到这里, 都会获取到正确的本地IP, 除非你有多网卡, 或者有VPN, 导致无法正常解析.
                                SocketAddress addr = new InetSocketAddress(registryURL.getHost(), registryURL.getPort());
                                socket.connect(addr, 1000);
                                host = socket.getLocalAddress().getHostAddress();
                                break;
                            } finally {
                                try {
                                    socket.close();
                                } catch (Throwable e) {
                                }
                            }
                        } catch (Exception e) {
                            //logger.warn(e.getMessage(), e);
                        }
                    }
                }

                //6. 遍历本地网卡, 返回第一个合理的Host
                //最后一个大招. 当上述都解析不到时, 则会遍历本地网卡.
                //逐个获取IP, 直到有一个合理的IP为止.
                if (NetUtils.isInvalidLocalHost(host)) {
                    host = NetUtils.getLocalHost();
                }
            }
        }
        return host;
    }
}
