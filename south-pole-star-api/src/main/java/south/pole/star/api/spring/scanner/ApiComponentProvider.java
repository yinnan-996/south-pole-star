package south.pole.star.api.spring.scanner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import south.pole.star.api.spring.annotations.SouthStarApi;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Description:  -----require需求|ask问题|jira
 * Design :  ----the  design about train of thought 设计思路
 * User: yinnan
 * Date: 2019/1/7
 * Time: 16:16
 * Email:yinnan@huli.com
 *
 * @author yinnan
 * @since 1.0-SNAPSHOT
 */
@Slf4j
public class ApiComponentProvider {

    /**
     *
     */
    public ApiComponentProvider() {
        includeFilters.add(new AnnotationTypeFilter(SouthStarApi.class));
    }

    /**
     *
     */
    private final List<TypeFilter> includeFilters = new LinkedList<TypeFilter>();

    /**
     *
     */
    private final List<TypeFilter> excludeFilters = new LinkedList<TypeFilter>();

    /**
     *
     */
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    /**
     *
     */
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(
            resourcePatternResolver);


    public Set<BeanDefinition> findCandidateComponents(String url) {
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        Set<BeanDefinition> candidates = new LinkedHashSet<BeanDefinition>();
        String packageSearchPath = url + "**/*.class";
        try {
            Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
            for (int i = 0; i < resources.length; i++) {
                Resource resource = resources[i];
                log.trace("[southStar find api] scanning " + resource);
                // resourcePatternResolver.getResources出来的classPathResources，metadataReader对其进行getInputStream的时候为什么返回null呢？
                // 不得不做一个exists判断
                if (!resource.exists()) {
                    log.debug("Ignored because not exists:" + resource);
                } else if (resource.isReadable()) {
                    MetadataReader metadataReader = metadataReaderFactory
                            .getMetadataReader(resource);
                    if (isCandidateComponent(metadataReader)) {
                        ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(
                                metadataReader);
                        sbd.setResource(resource);
                        sbd.setSource(resource);
                        if (sbd.getMetadata().isInterface() && sbd.getMetadata().isIndependent()) {
                            log.debug("Identified candidate component class: " + resource);
                            candidates.add(sbd);
                        } else {
                            log.trace("Ignored because not a interface top-level class: "
                                        + resource);
                        }
                    } else {
                        log.trace("Ignored because not matching any filter: " + resource);
                    }
                } else {
                    log.trace("Ignored because not readable: " + resource);
                }
            }
        }catch (IOException ex) {
            throw new BeanDefinitionStoreException("I/O failure during jade scanning", ex);
        }
        return candidates;
    }


    /**
     * Determine whether the given class does not match any exclude filter
     * and does match at least one include filter.
     *
     * @param metadataReader the ASM ClassReader for the class
     * @return whether the class qualifies as a candidate component
     */
    protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
        for (TypeFilter tf : this.excludeFilters) {
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return false;
            }
        }
        for (TypeFilter tf : this.includeFilters) {
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return true;
            }
        }
        return false;
    }

    public List<TypeFilter> getIncludeFilters() {
        return includeFilters;
    }

    public List<TypeFilter> getExcludeFilters() {
        return excludeFilters;
    }

    public ResourcePatternResolver getResourcePatternResolver() {
        return resourcePatternResolver;
    }

    public void setResourcePatternResolver(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public MetadataReaderFactory getMetadataReaderFactory() {
        return metadataReaderFactory;
    }

    public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
        this.metadataReaderFactory = metadataReaderFactory;
    }
}
