package south.pole.star.provider;

import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import south.pole.star.consumer.MessageEventHandler;

public class DispatcherFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherFactory.class);

    private static  TaskDispatcher taskDispatcher;

    public static TaskDispatcher getTaskDispatcher() throws InstantiationException, IllegalAccessException {
        try {
            if(taskDispatcher==null){
                synchronized (DispatcherFactory.class){
                    if(taskDispatcher==null){
                        taskDispatcher = new TaskDispatcher(Runtime.getRuntime().availableProcessors(),
                                new DefaultThreadFactory("task-dispatcher-Thread-pool"),
                                MessageEventHandler.class);
                    }
                }
            }
            LOGGER.info("[getTaskDispatcher] taskDispatcher={}",taskDispatcher);
           return taskDispatcher;
        } catch (Exception e) {
            LOGGER.error("[DispatcherFactory] 启动分发任务线程失败");
            throw  e;
        }

    }
}
