package south.pole.star.api.spring.scanner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import south.pole.star.api.spring.annotations.SouthStarApi;
import south.pole.star.api.spring.load.ResourceRef;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    protected Date createTime = new Date();

    protected ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

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
        return new ArrayList<ResourceRef>(1);
    }
}
