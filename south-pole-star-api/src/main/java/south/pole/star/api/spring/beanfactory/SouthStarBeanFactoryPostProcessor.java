package south.pole.star.api.spring.beanfactory;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ResourceUtils;
import south.pole.star.api.spring.annotations.SouthStarApi;
import south.pole.star.api.spring.config.SouthStarConfigurationProcessorHandler;
import south.pole.star.scanner.load.ResourceRef;
import south.pole.star.api.spring.scanner.ApiComponentProvider;
import south.pole.star.api.spring.scanner.SouthStarScanner;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
public class SouthStarBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor ,Ordered {

    @Autowired
    private SouthStarConfigurationProcessorHandler southStarConfigurationProcessorHandler;

    /**
     * 南极星主题逻辑
     * @param configurableListableBeanFactory
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        if(log.isDebugEnabled()){
            log.info("SouthStarBeanFactoryPostProcessor postProcessBeanFactory begin ");
        }
//        String processorId = "default";
//        AbstractSouthStarConfigurationProcessor abstractSouthStarConfigurationProcessor = AbstractSouthStarConfigurationProcessor.getInstance(processorId);
//        if(log.isDebugEnabled()){
//            log.info("SouthStarBeanFactoryPostProcessor abstractSouthStarConfigurationProcessor begin ");
//            abstractSouthStarConfigurationProcessor.getInfo();
//        }
        /** 注册bean*/
        this.doPostProcessBeanFactory(configurableListableBeanFactory);
    }

    /**
     * 注册bean
     * @param configurableListableBeanFactory
     */
    private void doPostProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) {
        log.info("[south-star doPostProcessBeanFactory] starting ...");
        /** 1、获取标注未南极星标志的资源(ResourceRef)*/
        final List<ResourceRef> resources = findSouthStarResources();
        log.info("name ={}", (Object[]) configurableListableBeanFactory.getBeanNamesForAnnotation(SouthStarApi.class));
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableListableBeanFactory;
        log.info("getBeanDefinitionNames ={}", JSONObject.toJSON(defaultListableBeanFactory.getBeanDefinitionNames()));

        log.info("defaultListableBeanFactory ={}", defaultListableBeanFactory.getBeansWithAnnotation(SouthStarApi.class));
        /** 2、从获取的资源(resources)中，把符合Api的筛选出来，并以URL的形式返回*/
        List<String> urls = findSouthStarApiResources(resources);


        /** 3、从每个URL中找出符合规范的API接口，并将之以SouthStarFactoryBean的形式注册到Spring容器中*/
        findSouthStarApiDefinitions(configurableListableBeanFactory, urls);


        log.info("[south-star doPostProcessBeanFactory] end ...");
    }


    /**
     * 获取标注未南极星标志的资源(ResourceRef)
     * @return
     */
    private List<ResourceRef> findSouthStarResources() {
        final List<ResourceRef> resources;
        try {
            resources = SouthStarScanner.getInstance().getJarAndClassesFolderResources();
        } catch (IOException e) {
            throw new ApplicationContextException(
                    "error on getJarResources/getClassesFolderResources", e);
        }
        return resources;
    }

    /**
     * 获取满足API的文件URL
     * @param resources
     * @return
     */
    private List<String> findSouthStarApiResources(List<ResourceRef> resources) {
        List<String> urls = new LinkedList<String>();
        for (ResourceRef ref : resources) {
            if (ref.isApi()) {
                try {
                    Resource resource = ref.getResource();
                    File resourceFile = resource.getFile();
                    if (resourceFile.isFile()) {
                        urls.add("jar:file:" + resourceFile.toURI().getPath()
                                + ResourceUtils.JAR_URL_SEPARATOR);
                    } else if (resourceFile.isDirectory()) {
                        urls.add(resourceFile.toURI().toString());
                    }
                } catch (IOException e) {
                    throw new ApplicationContextException("error on resource.getFile", e);
                }
            }
        }
        log.info("[southApi] found " + urls.size() + " Api urls: " + urls);
        return urls;
    }

    /**
     * 从获得的目录或jar包中寻找出符合规范的Api接口，并注册到Spring容器中
     * @param configurableListableBeanFactory
     * @param urls
     */
    private void findSouthStarApiDefinitions(ConfigurableListableBeanFactory configurableListableBeanFactory, List<String> urls) {
        ApiComponentProvider provider = new ApiComponentProvider();
        Set<String> apiClassNames = new HashSet<String>();
        for (String url : urls) {
            Set<BeanDefinition> dfs = provider.findCandidateComponents(url);
            for (BeanDefinition beanDefinition : dfs) {
                String apiClassName = beanDefinition.getBeanClassName();
                if (apiClassNames.contains(apiClassName)) {
                    continue;
                }
                apiClassNames.add(apiClassName);
                this.registerApiDefinition(configurableListableBeanFactory, beanDefinition);
            }
        }
    }

    /**
     * 注册对应的bean
     * @param configurableListableBeanFactory
     * @param beanDefinition
     */
    private void registerApiDefinition(ConfigurableListableBeanFactory configurableListableBeanFactory, BeanDefinition beanDefinition) {
        log.info("registerApiDefinition starting");
        final String daoClassName = beanDefinition.getBeanClassName();
        MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
        /**
         * 添加 SouthApiFactoryBean 对应属性
         */
        propertyValues.addPropertyValue("objectType", daoClassName);
        ScannedGenericBeanDefinition scannedBeanDefinition = (ScannedGenericBeanDefinition) beanDefinition;
        scannedBeanDefinition.setPropertyValues(propertyValues);
        scannedBeanDefinition.setBeanClass(SouthStarApiFactoryBean.class);

        DefaultListableBeanFactory defaultBeanFactory = (DefaultListableBeanFactory) configurableListableBeanFactory;
        defaultBeanFactory.registerBeanDefinition(daoClassName, beanDefinition);
        log.info("registerApiDefinition end");
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        if(log.isDebugEnabled()){
            log.info("SouthStarBeanFactoryPostProcessor postProcessBeanDefinitionRegistry begin");
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
