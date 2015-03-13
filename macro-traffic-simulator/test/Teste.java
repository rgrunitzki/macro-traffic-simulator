

import driver.learning.DifferenceQLearning;

public class Teste {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException {
        
        Class c = DifferenceQLearning.class;
        Object objeto;
        Object o = c.getConstructors()[0];
//        objeto = Class.forName(c.getName()).getConstructor(long.class, Vertex.class, Vertex.class, Graph.class).newInstance(args);
        System.out.println(o);
    }

}
