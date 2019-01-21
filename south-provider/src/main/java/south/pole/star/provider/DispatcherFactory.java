package south.pole.star.provider;

import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispatcherFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherFactory.class);

    public static TaskDispatcher getTaskDispatcher() throws InstantiationException, IllegalAccessException {
        try {
            return new TaskDispatcher(Runtime.getRuntime().availableProcessors(),
                    new DefaultThreadFactory("task-dispatcher-Thread-pool"),
                    MessageEventProvider.class);
        } catch (Exception e) {
            LOGGER.error("[DispatcherFactory] 启动分发任务线程失败");
            throw  e;
        }

    }
}
