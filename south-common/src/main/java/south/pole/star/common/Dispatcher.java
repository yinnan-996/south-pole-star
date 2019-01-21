package south.pole.star.common;

public interface Dispatcher<T> {


    int CONSUMER_SIZE = 1;

    int BUFFER_SIZE = 32768;
    /**
     * 任务处理
     * @param task
     * @return
     */
    boolean dispatch(T task,int retrySize);

    /**
     * 如果重试次数大于retrySize,则放弃任务
     * @param retrySize
     * @param failCount
     * @return
     */
    default boolean discard(int retrySize,int failCount){
        if(failCount>retrySize){
            return true;
        }else {
            return false;
        }
    };
}
