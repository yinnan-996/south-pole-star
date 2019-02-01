package south.pole.star.regist;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public interface RegistService {

    final LinkedBlockingQueue<RegistMeta> queue = new LinkedBlockingQueue<RegistMeta>();


    void provider(Object serviceProvider);

    void regist();

    void unRegist(RegistMeta registMeta);

    List<RegistMeta> findService(Class tClass,String version);
    enum RegisterState {
        PREPARE,
        DONE

    }


    int DISCONNECTED = 0;

    int CONNECTED = 1;

    int RECONNECTED = 2;

    int TIME_OUT = 60000;





}
