package south.pole.star.regist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperRegistFactory implements RegistFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperRegistFactory.class);

    private static RegistService registService;


    public static RegistService getRegistService(REGIST_TYPE regist_type,String registHost,int registPort) {
        if(registService==null){
            synchronized (ZookeeperRegistFactory.class){
                if(registService==null){
                    switch (regist_type){
                        case ZOOKEEPER:
                            registService = new ZookeeperRegistService(registHost,registPort);
                    }
                }
            }
        }
        return registService;
    }

}


