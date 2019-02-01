package south.pole.star;

public class HelloServiceImpl implements HelloService {

    public String helloWorld(String word){
        System.out.println("SAY "+word);
        return "SAY "+word;
    }
}
