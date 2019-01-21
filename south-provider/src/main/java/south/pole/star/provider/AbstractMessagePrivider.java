package south.pole.star.provider;

import com.lmax.disruptor.EventTranslatorOneArg;
import south.pole.star.common.MessageEvent;

@Deprecated
public abstract class AbstractMessagePrivider<R> implements EventTranslatorOneArg<MessageEvent<R>,R> {

}
