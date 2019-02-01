package south.pole.star.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import south.pole.star.common.AbstractEventHandler;
import south.pole.star.common.MessageEvent;

/**
 * disruptor 消费者处理器
 */
public class MessageEventHandler extends AbstractEventHandler<MessageEvent<Runnable>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageEventHandler.class);

    @Override
    public void onEvent(MessageEvent<Runnable> runnableMessageEvent) throws Exception {
        LOGGER.info("[MessageEventHandler] WorkHandler consume message");
        LOGGER.info("[MessageEventHandler] runnableMessageEvent={}",runnableMessageEvent);
        LOGGER.info("[MessageEventHandler] getMessage={}",runnableMessageEvent.getMessage());
        runnableMessageEvent.getMessage().run();
    }

}
