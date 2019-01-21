package south.pole.star.api.spring.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * Description:  -----用于确认解析配置文件的类
 * Design :  ----用于确认配置文件的类
 * User: yinnan
 * Date: 2019/1/3
 * Time: 17:42
 * Email:yinnan@huli.com
 *
 * @author yinnan
 * @since 1.0-SNAPSHOT
 */
@Slf4j
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "south.star.config.server", ignoreUnknownFields = false)
@Data
public class SouthStarConfigurationProcessorHandler {

    private String processorId;

}
