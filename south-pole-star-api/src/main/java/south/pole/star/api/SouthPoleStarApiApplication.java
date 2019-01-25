package south.pole.star.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import south.pole.star.api.spring.annotations.EnableSouthStar;

/**
 * Created by IntelliJ IDEA.
 * Description:  -----require需求|ask问题|jira
 * Design :  ----the  design about train of thought 设计思路
 * User: yinnan
 * Date: 2018/12/21
 * Time: 15:00
 * Email:yinnan@huli.com
 *
 * @author yinnan
 * @since 1.0-SNAPSHOT
 */
@SpringBootApplication
@EnableSouthStar
public class SouthPoleStarApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SouthPoleStarApiApplication.class, args);
    }

}

