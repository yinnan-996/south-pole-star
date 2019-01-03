package south.pole.star.api.spring.load;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

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
public class ResourceRef implements Comparable<ResourceRef> {

    private Properties properties = new Properties();

    private Resource resource;

    private String[] modifiers;

    public Properties getProperties() {
        return properties;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }

    public String[] getModifiers() {
        return modifiers;
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
