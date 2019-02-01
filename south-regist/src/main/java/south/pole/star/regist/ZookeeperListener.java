package south.pole.star.regist;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperListener implements IZKListener {

    private String path;

    public ZookeeperListener(String path) {
        this.path = path;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperListener.class);

    @Override
    public void executor(CuratorFramework client) {
        //使用Curator的NodeCache来做ZNode的监听，不用我们自己实现重复监听
        final NodeCache cache = new NodeCache(client, path);
        cache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                byte[] data = cache.getCurrentData().getData();
            }
        });
        try {
            cache.start(true);
        } catch (Exception e) {
            LOGGER.error("Start NodeCache error for path: {}, error info: {}", path, e.getMessage());

        }
    }
}
