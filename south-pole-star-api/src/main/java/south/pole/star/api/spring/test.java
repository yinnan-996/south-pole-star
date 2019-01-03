package south.pole.star.api.spring;

import south.pole.star.api.spring.annotations.*;
import south.pole.star.api.spring.load.ResourceRef;

import java.util.List;

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
@SouthStarApi
public interface test {

    public List<ResourceRef> testResourceRef(String testString, int testInt);
}
