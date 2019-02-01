package south.pole.star.regist;

import org.apache.curator.framework.CuratorFramework;

public interface IZKListener {

    void executor(CuratorFramework client);
}
