package south.pole.star.provider;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import south.pole.star.common.AbstractEventHandler;
import south.pole.star.common.Dispatcher;
import south.pole.star.common.MessageEvent;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 任务分发
 * @param <R> 任务
 * @param <T> 任务处理器,WorkHandler
 */
public class TaskDispatcher<R,T extends AbstractEventHandler> implements Dispatcher<R> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskDispatcher.class);

    private Disruptor<R> disruptor;

    private int consumeSize;

    private MessageEventProvider<MessageEvent<R>> messageEventProvider = new MessageEventProvider(disruptor.getRingBuffer());

    private AbstractEventHandler[] eventHandlers = new AbstractEventHandler[consumeSize];

    private static Executor executor = Executors.newCachedThreadPool();

    @Override
    public boolean dispatch(R task, int retrySize) {
        int i=0;
        try {
            MessageEvent messageEvent = new MessageEvent();
            messageEvent.setMessage(task);
            messageEventProvider.onData(messageEvent);
        }catch (Exception ex){
            LOGGER.error("[TaskDispatcher 分配任务失败],error={}",ex);
            if(!discard(retrySize,++i)){
                try {
                    Thread.sleep(Long.valueOf(50*i));
                } catch (InterruptedException e) {
                    LOGGER.error("[dispatch retry] i={},error={})",i,e);
                }
                dispatch(task,++i);
            }else {
                return false;
            }
        }
        return true;
    }


    EventFactory<MessageEvent<Runnable>> eventFactory = new EventFactory<MessageEvent<Runnable>>() {
        public MessageEvent newInstance() {
            return new MessageEvent<Runnable>();
        }
    };

    public TaskDispatcher(int consumeSize,ThreadFactory threadFactory,Class<T> tls) throws IllegalAccessException, InstantiationException {

        if(consumeSize>1){
            this.consumeSize = consumeSize;
        }else {
            this.consumeSize = CONSUMER_SIZE;
        }
        for (int i=0;i<this.consumeSize;i++){
            T t = tls.newInstance();
            eventHandlers[i] = t;
        }
        this.disruptor =new Disruptor(eventFactory, BUFFER_SIZE,threadFactory,ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWithWorkerPool(eventHandlers);
        this.disruptor.start();
    }


    public void execute(R task,int retrySize){
        if(!dispatch(task,0)){
            if(task instanceof Runnable){
                executor.execute((Runnable)task);
            }
        }
    }

}
