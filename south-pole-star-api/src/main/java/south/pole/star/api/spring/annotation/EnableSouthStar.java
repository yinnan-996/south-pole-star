package south.pole.star.api.spring.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * Description:  -----require需求|ask问题|jira
 * Design :  ----the  design about train of thought 设计思路
 * User: yinnan
 * Date: 2018/12/20
 * Time: 15:19
 * Email:yinnan@huli.com
 *
 * @author yinnan
 * @since 1.0-SNAPSHOT
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(SouthStarConfigRegistrar.class)
public @interface EnableSouthStar {
}
