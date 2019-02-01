package south.pole.star.regist;

public class RegistMeta {


    private Address address = new Address();

    private ServiceMeta serviceMeta = new ServiceMeta();

    public String getHost(){return address.getHost();}

    public void setHost(String host){address.setHost(host);}

    public void setPort(int port){
        address.setPort(port);
    }

    public int getPort(){
        return address.getPort();
    }

    public void setClassName(String className){
        this.serviceMeta.setClassName(className);
    }

    public String getClassName(){
        return this.serviceMeta.getClassName();
    }

    public void setVersion(String version){
        this.serviceMeta.setVersion(version);
    }

    public String getVersion(){
        return this.serviceMeta.getVersion();
    }


    public static class Address{

        private String host;

        private int port;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public Address(){}

        public Address(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }


    public static class ServiceMeta{

        private String className;

        private String version;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
        public ServiceMeta(){}

        public ServiceMeta(String className, String version) {
            this.className = className;
            this.version = version;
        }
    }
}
