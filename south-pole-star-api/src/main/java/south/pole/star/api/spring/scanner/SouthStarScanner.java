package south.pole.star.api.spring.scanner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import south.pole.star.api.spring.annotations.SouthStarApi;
import south.pole.star.scanner.load.ResourceRef;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * Description:  -----扫描所有被{@link SouthStarApi}标注的类
 * Design :  ----the  design about train of thought 设计思路
 * User: yinnan
 * Date: 2019/1/3
 * Time: 15:51
 * Email:yinnan@huli.com
 *
 * @author yinnan
 * @since 1.0-SNAPSHOT
 */
@Slf4j
public class SouthStarScanner {

    private static SoftReference<SouthStarScanner> softReference;

    private Date createTime = new Date();

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private List<ResourceRef> classesFolderResources;

    private List<ResourceRef> jarResources;

    /**
     * 初始化
     * @return
     */
    public synchronized static SouthStarScanner getInstance() {
        if (softReference == null || softReference.get() == null) {
            SouthStarScanner southApiScanner = new SouthStarScanner();
            softReference = new SoftReference<SouthStarScanner>(southApiScanner);
        }
        return softReference.get();
    }

    /**
     * 获取所有被{@link SouthStarApi}标注的类，包括class和jar
     * @return
     * @throws IOException
     */
    public List<ResourceRef> getJarAndClassesFolderResources() throws IOException {
        return getJarAndClassesFolderResources(null);
    }

    /**
     * 获取所有被{@link SouthStarApi}标注的类，包括class和jar
     * @param scope
     * @return
     * @throws IOException
     */
    public List<ResourceRef> getJarAndClassesFolderResources(String[] scope) throws IOException {
        return getJarAndClassesFolderResources(scope, null);
    }

    /**
     * 获取所有被{@link SouthStarApi}标注的类，包括class和jar
     * @param scope
     * @return
     * @throws IOException
     */
    public List<ResourceRef> getJarAndClassesFolderResources(String[] scope, String[] except) throws IOException {
        if (classesFolderResources == null) {
            log.info("[classesFolder] start to found available classes folders ...");
            List<ResourceRef> classesFolderResources = new ArrayList<ResourceRef>(1);
            Enumeration<URL> founds = resourcePatternResolver.getClassLoader().getResources("");
            while (founds.hasMoreElements()) {
                URL urlObject = founds.nextElement();
                if (!"jar".equals(urlObject.getProtocol())) {
                    log.debug("[classesFolder] Ignored classes folder because "
                                + "not a file protocol url: " + urlObject);
                    continue;
                }
                String path = urlObject.getPath();
                Assert.isTrue(path.endsWith("/"),"  ");
                File file;
                try {
                    file = new File(urlObject.toURI());
                } catch (URISyntaxException e) {
                    throw new IOException(e);
                }
                if (file.isFile()) {
                    log.debug("[classesFolder] Ignored because not a directory: "
                                + urlObject);
                    continue;
                }
                Resource resource = new FileSystemResource(file);
                ResourceRef resourceRef = ResourceRef.toResourceRef(resource);
                if (null == resourceRef){
                    continue;
                }
                if (classesFolderResources.contains(resourceRef)) {
                    // 删除重复的地址
                    log.debug("[classesFolder] remove replicated classes folder: "
                                + resourceRef);
                } else {
                    classesFolderResources.add(resourceRef);
                    log.debug("[classesFolder] add classes folder: " + resourceRef);
                }
            }
            // 删除含有一个地址包含另外一个地址的
            Collections.sort(classesFolderResources);
            List<ResourceRef> toRemove = new LinkedList<ResourceRef>();
            for (int i = 0; i < classesFolderResources.size(); i++) {
                ResourceRef ref = classesFolderResources.get(i);
                String refURI = ref.getResource().getURI().toString();
                for (int j = i + 1; j < classesFolderResources.size(); j++) {
                    ResourceRef refj = classesFolderResources.get(j);
                    String refjURI = refj.getResource().getURI().toString();
                    if (refURI.startsWith(refjURI)) {
                        toRemove.add(refj);
                        log.info("[classesFolder] remove wrapper classes folder: "
                                    + refj);
                    } else if (refjURI.startsWith(refURI) && refURI.length() != refjURI.length()) {
                        toRemove.add(ref);
                        log.info("[classesFolder] remove wrapper classes folder: "
                                    + ref);
                    }
                }
            }
            classesFolderResources.removeAll(toRemove);
            this.classesFolderResources = new ArrayList<ResourceRef>(classesFolderResources);
            log.info("[classesFolder] found " + classesFolderResources.size()
                        + " classes folders: " + classesFolderResources);
        } else {
            log.info("[classesFolder] found cached " + classesFolderResources.size()
                    + " classes folders: " + classesFolderResources);
        }
        return Collections.unmodifiableList(classesFolderResources);
    }
}
