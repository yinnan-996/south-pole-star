package south.pole.star.rpc.support;


import java.io.Serializable;
import java.util.Arrays;

public class SouthRequest implements Serializable {


    private String requestId;  //请求id
    private Class className;  //调用类名
    private String methodName; //调用方法名
    private String versin;//服务版本号
    private Class<?>[] parameterTypes; //方法参数类型
    private Object[] parameters;   //方法参数

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public Class getClassName() {
        return className;
    }

    public void setClassName(Class className) {
        this.className = className;
    }

    public String getVersin() {
        return versin;
    }

    public void setVersin(String versin) {
        this.versin = versin;
    }

    @Override
    public String toString() {
        return "SouthRequest{" +
                "requestId='" + requestId + '\'' +
                ", className=" + className +
                ", methodName='" + methodName + '\'' +
                ", versin='" + versin + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}
