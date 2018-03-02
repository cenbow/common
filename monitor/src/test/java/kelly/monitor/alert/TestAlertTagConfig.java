package kelly.monitor.alert;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import kelly.monitor.common.AlertTagConfig;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by kelly-lee on 2018/2/9.
 */
public class TestAlertTagConfig {

    @Test
    public void testConstruct() {
        //  AlertTagConfig alertTagConfig = new AlertTagConfig(null);
//        AlertTagConfig alertTagConfig = new AlertTagConfig("host");
//        Assert.assertEquals(alertTagConfig.getTagKey(), "host");
//        Assert.assertEquals(alertTagConfig.getTagValues(), null);
//        Assert.assertEquals(alertTagConfig.getFilterType(), AlertTagConfig.FilterType.INCLUDE);
//        Assert.assertEquals(alertTagConfig.getLogicType(), AlertTagConfig.LogicType.ALL);
//
//
//        alertTagConfig = new AlertTagConfig(ImmutableList.of("192.168.1.1", "192.168.1.2", "192.168.1.3"), "host");
//        Assert.assertEquals(alertTagConfig.getTagKey(), "host");
//        Assert.assertEquals(alertTagConfig.getTagValues(), ImmutableList.of("192.168.1.1", "192.168.1.2", "192.168.1.3"));
//        Assert.assertEquals(alertTagConfig.getFilterType(), AlertTagConfig.FilterType.INCLUDE);
//        Assert.assertEquals(alertTagConfig.getLogicType(), AlertTagConfig.LogicType.ALL);


        AlertTagConfig alertTagConfig = new AlertTagConfig(AlertTagConfig.FilterType.EXCLUDE, ImmutableList.of("192.168.1.1", "192.168.1.2", "192.168.1.3"), "host");
        Assert.assertEquals(alertTagConfig.getTagKey(), "host");
        Assert.assertEquals(alertTagConfig.getTagValues(), ImmutableList.of("192.168.1.1", "192.168.1.2", "192.168.1.3"));
        Assert.assertEquals(alertTagConfig.getFilterType(), AlertTagConfig.FilterType.EXCLUDE);
        Assert.assertEquals(alertTagConfig.getLogicType(), AlertTagConfig.LogicType.ALL);
        alertTagConfig = new AlertTagConfig(AlertTagConfig.LogicType.ANY, AlertTagConfig.FilterType.EXCLUDE, ImmutableList.of("192.168.1.1", "192.168.1.2", "192.168.1.3"), "host");
        Assert.assertEquals(alertTagConfig.getTagKey(), "host");
        Assert.assertEquals(alertTagConfig.getTagValues(), ImmutableList.of("192.168.1.1", "192.168.1.2", "192.168.1.3"));
        Assert.assertEquals(alertTagConfig.getFilterType(), AlertTagConfig.FilterType.EXCLUDE);
        Assert.assertEquals(alertTagConfig.getLogicType(), AlertTagConfig.LogicType.ANY);
        System.out.println(alertTagConfig);
    }

    @Test
    public void testExclude() {
        AlertTagConfig alertTagConfig = new AlertTagConfig(AlertTagConfig.LogicType.ALL, AlertTagConfig.FilterType.EXCLUDE, ImmutableList.of("192.168.1.1", "192.168.1.2", "192.168.1.3"), "host");
        Assert.assertTrue(alertTagConfig.match(ImmutableMap.of("host", "192.168.1.4", "app", "monitor")));
        Assert.assertFalse(alertTagConfig.match(ImmutableMap.of("host", "192.168.1.3", "app", "monitor")));
        //采集数据没有设置tag
        Assert.assertTrue(alertTagConfig.match(ImmutableMap.of("host1", "192.168.1.1", "app", "monitor")));
        //采集数据tag中不包含报警设置的tag
        Assert.assertTrue(alertTagConfig.match(null));
    }

    @Test
    public void testInclude() {
        AlertTagConfig alertTagConfig = new AlertTagConfig(AlertTagConfig.LogicType.ALL, AlertTagConfig.FilterType.INCLUDE, ImmutableList.of("192.168.1.1", "192.168.1.2", "192.168.1.3"), "host");
        Assert.assertFalse(alertTagConfig.match(ImmutableMap.of("host", "192.168.1.4", "app", "monitor")));
        Assert.assertTrue(alertTagConfig.match(ImmutableMap.of("host", "192.168.1.3", "app", "monitor")));
        //采集数据tag中不包含报警设置的tag
        Assert.assertFalse(alertTagConfig.match(ImmutableMap.of("host1", "192.168.1.1", "app", "monitor")));
        //采集数据没有设置tag
        Assert.assertFalse(alertTagConfig.match(null));
    }

    @Test
    public void testAny() {
        AlertTagConfig alertTagConfig = new AlertTagConfig(AlertTagConfig.LogicType.ALL, AlertTagConfig.FilterType.INCLUDE, ImmutableList.of("*", "192.168.1.2", "192.168.1.3"), "host");
        Assert.assertTrue(alertTagConfig.match(ImmutableMap.of("host", "192.168.1.4", "app", "monitor")));
        //采集数据tag中不包含报警设置的tag
        Assert.assertTrue(alertTagConfig.match(ImmutableMap.of("host1", "192.168.1.1", "app", "monitor")));
        //采集数据没有设置tag
        Assert.assertTrue(alertTagConfig.match(null));
    }

    @Test
    public void testNull() {
        AlertTagConfig alertTagConfig = new AlertTagConfig(AlertTagConfig.LogicType.ALL, AlertTagConfig.FilterType.INCLUDE, null, "host");
        Assert.assertTrue(alertTagConfig.match(ImmutableMap.of("host", "192.168.1.4", "app", "monitor")));
        //采集数据tag中不包含报警设置的tag
        Assert.assertTrue(alertTagConfig.match(ImmutableMap.of("host1", "192.168.1.1", "app", "monitor")));
        //采集数据没有设置tag
        Assert.assertTrue(alertTagConfig.match(null));
    }


}
