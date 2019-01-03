package south.pole.star.api.spring.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Description:  -----require需求|ask问题|jira
 * Design :  ----the  design about train of thought 设计思路
 * User: yinnan
 * Date: 2019/1/3
 * Time: 17:52
 * Email:yinnan@huli.com
 *
 * @author yinnan
 * @since 1.0-SNAPSHOT
 */
public abstract class AbstractSouthStarConfigurationProcessor {

    /**
     * 抽象工厂Map
     */
    private static Map<String,AbstractSouthStarConfigurationProcessor> IMPL = new HashMap<>();

    /**
     * 获取对应的实例化类
     * @param productType
     * @return
     */
    public static AbstractSouthStarConfigurationProcessor getInstance(String productType){
        return IMPL.get(productType);
    }

    /**
     * 抽象工厂注册
     * @param productType
     * @param instance
     */
    static void regist(String productType,AbstractSouthStarConfigurationProcessor instance){
        IMPL.put(productType,instance);
    }

    /**
     * 获取信息
     * @return
     */
    public abstract String getInfo();
}
