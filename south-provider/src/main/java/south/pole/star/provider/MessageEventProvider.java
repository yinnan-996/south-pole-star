package south.pole.star.provider;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.EventTranslatorVararg;
import com.lmax.disruptor.RingBuffer;
import south.pole.star.common.MessageEvent;


public class MessageEventProvider<R> implements EventTranslatorOneArg<MessageEvent<R>,R> {

    private final RingBuffer<R> ringBuffer;

    public MessageEventProvider(RingBuffer<R> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(MessageEvent<R> value){
        ringBuffer.publishEvent((EventTranslatorVararg<R>) this,value);
    }

    @Override
    public void translateTo(MessageEvent<R> rMessageEvent, long l, R r) {
        rMessageEvent.setMessage(r);
    }
}
