package south.pole.star.rpc.support;

public class SouthReponse {

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
}
