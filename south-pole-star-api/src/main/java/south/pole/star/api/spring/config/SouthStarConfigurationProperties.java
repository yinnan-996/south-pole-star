package south.pole.star.api.spring.config;

import lombok.Data;

/**
 * Created by IntelliJ IDEA.
 * Description:  -----require需求|ask问题|jira
 * Design :  ----the  design about train of thought 设计思路
 * User: yinnan
 * Date: 2019/1/3
 * Time: 17:37
 * Email:yinnan@huli.com
 *
 * @author yinnan
 * @since 1.0-SNAPSHOT
 */
@Data
public class SouthStarConfigurationProperties {

    /**
     * 服务方IP地址
     */
    private String ipAddress;

    /**
     * 服务方host名称
     */
    private String hostName;

    /**
     * 调用协议
     */
    private String protocol;
}
