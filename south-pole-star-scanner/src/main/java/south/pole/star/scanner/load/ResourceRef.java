package south.pole.star.scanner.load;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Created by IntelliJ IDEA.
 * Description:  -----require需求|ask问题|jira
 * Design :  ----the  design about train of thought 设计思路
 * User: yinnan
 * Date: 2019/1/3
 * Time: 16:02
 * Email:yinnan@huli.com
 *
 * @author yinnan
 * @since 1.0-SNAPSHOT
 */
@Slf4j
public class ResourceRef implements Comparable<ResourceRef> {

    private boolean api;

    private Properties properties = new Properties();

    private Resource resource;

    private String[] modifiers;

    public static ResourceRef toResourceRef(Resource resource) throws IOException {
        String[] modifiers = null;
        ResourceRef resourceRef = new ResourceRef();
        resourceRef.setApi(false);
        resourceRef.setResource(resource);
        Resource propertiesResource = resourceRef.getInnerResource("META-INF/rose.properties");
        if (!"jar".equals(resourceRef.getProtocol())) {
            return null;
        }else {
            JarFile jarFile = new JarFile(resourceRef.getResource().getFile());
            Manifest manifest = jarFile.getManifest();
            if (manifest != null) {
                Attributes attributes = manifest.getMainAttributes();
                String attrValue = attributes.getValue("southStar");
                if (attrValue == null) {
                    attrValue = attributes.getValue("southStarApi");
                    if (attrValue != null) {
                        resourceRef.setApi(true);
                    }
                }
                if (attrValue != null) {
                modifiers = StringUtils.split(attrValue, ", ;\n\r\t");
                    log.debug("modifiers[by manifest.mf][" + resourceRef.getResource().getURI()
                            + "]=" + Arrays.toString(modifiers));
                }
            }
        }
        resourceRef.setModifiers(modifiers);
        return resourceRef;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }

    public void setModifiers(String[] modifiers) {
        this.modifiers = modifiers;
        if (modifiers == null) {
            properties.remove("southStar");
            properties.remove("southStarApi");
        } else {
            StringBuilder sb = new StringBuilder();
            final String separator = ", ";
            for (String m : modifiers) {
                sb.append(m).append(separator);
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - separator.length());
            }
            properties.put("southStar", sb.toString());
            properties.put("southStarApi", sb.toString());
        }
    }

    public String[] getModifiers() {
        return modifiers;
    }

    public boolean isApi() {
        return api;
    }

    public void setApi(boolean api) {
        this.api = api;
    }


    public boolean hasModifier(String modifier) {
        if (modifier.startsWith("<") && modifier.endsWith(">")) {
            return ArrayUtils.contains(modifiers, modifier.substring(1, modifier.length() - 1));
        }
        return ArrayUtils.contains(modifiers, "**") || ArrayUtils.contains(modifiers, "*")
                || ArrayUtils.contains(modifiers, modifier);
    }

    public Resource getInnerResource(String subPath) throws IOException {
        Assert.isTrue(!subPath.startsWith("/"));
        String rootPath = resource.getURI().getPath();
        if (getProtocol().equals("jar")) {
            return new UrlResource("jar:file:" + rootPath + "!/" + subPath);
        } else {
            // FileSystemResource不用file:打头
            return new FileSystemResource(rootPath + subPath);
        }
    }

    public Resource[] getInnerResources(ResourcePatternResolver resourcePatternResolver,
                                        String subPath) throws IOException {
        subPath = getInnerResourcePattern(subPath);
        return resourcePatternResolver.getResources(subPath);
    }

    public String getInnerResourcePattern(String subPath) throws IOException {
        Assert.isTrue(!subPath.startsWith("/"), subPath);
        String rootPath = resource.getURI().getPath();
        if (getProtocol().equals("jar")) {
            subPath = "jar:file:" + rootPath + ResourceUtils.JAR_URL_SEPARATOR + subPath;
        } else {
            subPath = "file:" + rootPath + subPath;
        }
        return subPath;
    }

    public String getProtocol() {
        if (resource.getFilename().toLowerCase().endsWith(".jar")
                || resource.getFilename().toLowerCase().endsWith(".zip")
                || resource.getFilename().toLowerCase().endsWith(".tar")
                || resource.getFilename().toLowerCase().endsWith(".gz")) {
            return "jar";
        }
        return "file";
    }

    @Override
    public int compareTo(ResourceRef o) {
        try {
            return this.resource.getURI().compareTo(o.resource.getURI());
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}
