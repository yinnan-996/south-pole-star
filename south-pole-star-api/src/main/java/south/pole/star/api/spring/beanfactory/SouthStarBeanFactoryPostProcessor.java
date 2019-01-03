package south.pole.star.api.spring.beanfactory;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import south.pole.star.api.spring.config.AbstractSouthStarConfigurationProcessor;

/**
 * Created by IntelliJ IDEA.
 * Description:  -----require需求|ask问题|jira
 * Design :  ----the  design about train of thought 设计思路
 * User: yinnan
 * Date: 2019/1/3
 * Time: 16:16
 * Email:yinnan@huli.com
 *
 * @author yinnan
 * @since 1.0-SNAPSHOT
 */
@Slf4j
public class SouthStarBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor {


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        if(log.isDebugEnabled()){
            log.info("SouthStarBeanFactoryPostProcessor postProcessBeanFactory begin ");
        }
        SouthStarConfigurationProcessorHandler southStarConfigurationProcessorHandler = new SouthStarConfigurationProcessorHandler();
        String processorId = southStarConfigurationProcessorHandler.getProcessorId();
        AbstractSouthStarConfigurationProcessor abstractSouthStarConfigurationProcessor = AbstractSouthStarConfigurationProcessor.getInstance(processorId);
        if(log.isDebugEnabled()){
            log.info("SouthStarBeanFactoryPostProcessor abstractSouthStarConfigurationProcessor begin ");
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        if(log.isDebugEnabled()){
            log.info("SouthStarBeanFactoryPostProcessor postProcessBeanDefinitionRegistry begin");
        }
    }

    @ConfigurationProperties(prefix = "south.star.config.server", ignoreUnknownFields = false)
    @PropertySource("classpath:application.properties")
    @Data
    protected class SouthStarConfigurationProcessorHandler {

        private String processorId;

    }
}
