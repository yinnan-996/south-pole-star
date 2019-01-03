package south.pole.star.api.spring.beanfactory;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import south.pole.star.api.spring.annotations.EnableSouthStar;
import south.pole.star.api.spring.config.SouthStarConfigurationProcessorHandler;
import south.pole.star.api.spring.utils.BeanRegistrationUtil;

/**
 * Created by IntelliJ IDEA.
 * Description:  -----require需求|ask问题|jiraJadeBeanFactoryPostProcessor
 * Design :  ----the  design about train of thought 设计思路
 * User: yinnan
 * Date: 2018/12/21
 * Time: 15:00
 * Email:yinnan@huli.com
 *
 * @author yinnan
 * @since 1.0-SNAPSHOT
 */
@Slf4j
public class SouthStarRegistrar implements ImportBeanDefinitionRegistrar {

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata
                .getAnnotationAttributes(EnableSouthStar.class.getName()));
        if(log.isDebugEnabled()) {
            log.info("attributes ={} ", JSONObject.toJSONString(attributes));
        }
        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, SouthStarBeanFactoryPostProcessor.class);
    }

}
