package south.pole.star.apitest;

import south.pole.star.api.spring.annotations.*;

/**
 * Created by IntelliJ IDEA.
 * Description:  -----require需求|ask问题|jira
 * Design :  ----the  design about train of thought 设计思路
 * User: yinnan
 * Date: 2019/1/3
 * Time: 15:36
 * Email:yinnan@huli.com
 *
 * @author yinnan
 * @since 1.0-SNAPSHOT
 */
@SouthStarApi(value = "testSouthStarApi")
public interface test {

    public String getInfo();
}
