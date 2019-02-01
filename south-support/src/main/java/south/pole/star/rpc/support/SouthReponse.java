package south.pole.star.rpc.support;

import java.io.Serializable;

public class SouthReponse implements Serializable {

    private String requestId;  //对应请求id
    private Exception exception; //失败抛出的异常
    private Object result;   //结果

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "SouthReponse{" +
                "requestId='" + requestId + '\'' +
                ", exception=" + exception +
                ", result=" + result +
                '}';
    }
}
