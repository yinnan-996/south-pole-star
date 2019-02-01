package south.pole.star.serialization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SerializationUtils {


    private static final Logger LOGGER = LoggerFactory.getLogger(SerializationUtils.class);

    private static JavaSerialization javaSerialization = new JavaSerialization();

    public static byte[] encode(Object obj,Class serialClass) throws IOException {
        if(Serialization.class.isAssignableFrom(serialClass)){
            return javaSerialization.serialize(obj);
        }
        return new byte[0];
    }

    public static Object decode(byte[] data,Class serialClass,Class objClass){
        LOGGER.info("[SerializationUtils] decode data={},serialClass={},objClass={}",data,serialClass,objClass);
        if(Serialization.class.isAssignableFrom(serialClass)){
            return javaSerialization.deserialize(data,objClass);
        }
        return null;
    }
}
