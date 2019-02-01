package south.pole.star.provider;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import south.pole.star.common.MessageEvent;


public class MessageEventProvider<R> implements EventTranslatorOneArg<MessageEvent,R> {

    private final RingBuffer<MessageEvent> ringBuffer;

    public MessageEventProvider(RingBuffer<MessageEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(R value){
        ringBuffer.publishEvent((EventTranslatorOneArg) this,value);
    }

    @Override
    public void translateTo(MessageEvent rMessageEvent, long l, R r) {
        rMessageEvent.setMessage(r);
    }
}
