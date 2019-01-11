package south.pole.star.api.spring.beanfactory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import south.pole.star.api.spring.config.SouthStarConfigurationProperties;
import south.pole.star.api.spring.invocation.SouthStarApiInvocationHandler;

import java.lang.reflect.Proxy;

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
public class SouthStarApiFactoryBean implements FactoryBean, InitializingBean {

    private SouthStarConfigurationProperties southStarConfigurationProperties;

    private Class<?> objectType;

    private Object apiObject;

    public SouthStarApiFactoryBean() {
        super();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public Object getObject() throws Exception {
        if (apiObject == null) {
            apiObject = createProxy();
            Assert.notNull(apiObject,"apiObject can't be null");
        }
        return apiObject;
    }


    @Override
    public Class<?> getObjectType() {
        return objectType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    public void setObjectType(Class<?> objectType) {
        this.objectType = objectType;
    }

    private Object createProxy() {
        SouthStarApiInvocationHandler handler = new SouthStarApiInvocationHandler();
        return Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(),
                new Class[] { objectType }, handler);
    }
}
