package south.pole.star.api.spring.annotations;

import java.lang.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * Description:  -----所有标注此注解的类都会被南极星自动代理
 * Design :  ----通过扫描所有包含此注解的类都会被南极星的默认ClassLoader代理
 * User: yinnan
 * Date: 2019/1/3
 * Time: 15:47
 * Email:yinnan@huli.com
 *
 * @author yinnan
 * @since 1.0-SNAPSHOT
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface SouthStarApi {
    String config() default "default";
}
