package south.pole.star.rpc.support;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class SouthResultFuture  {

    /*0:完成,1:未完成*/
    private volatile int status;

    private SouthReponse southReponse;

    Sync sync = new Sync();

    public boolean isDone(){
        return sync.isDone();
    }

    public SouthReponse get(){
        sync.acquire(1);
        return southReponse;

    }

    public void done(SouthReponse southReponse){
        this.southReponse = southReponse;
        sync.release(1);
    }

    public static class Sync extends AbstractQueuedSynchronizer {

        private final int done = 1;

        private final int doing = 0;

        @Override
        protected boolean tryAcquire(int arg) {
            return getState()==1;
        }


        @Override
        protected boolean tryRelease(int arg) {
            if(getState()==done){
                return true;
            }else{
                if(compareAndSetState(doing,done)){
                    return true;
                }else{
                    return false;
                }
            }
        }

        public boolean isDone(){
            return getState()==done;
        }
    }


}
