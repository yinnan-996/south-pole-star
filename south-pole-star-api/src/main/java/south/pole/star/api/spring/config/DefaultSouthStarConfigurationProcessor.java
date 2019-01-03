package south.pole.star.api.spring.config;

import org.springframework.stereotype.Service;

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
@Service
public class DefaultSouthStarConfigurationProcessor extends AbstractSouthStarConfigurationProcessor{

    private static final String DEFAULT = "default";
    /**
     * 初始化
     */
    public DefaultSouthStarConfigurationProcessor(){
        regist(DEFAULT, this);
    }


    @Override
    public String getInfo() {
        return this.getClass().getSimpleName();
    }
}
