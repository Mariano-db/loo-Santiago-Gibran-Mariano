abstract class Animal {
    abstract void hacerSonido();
}

class Perro extends Animal {
    void hacerSonido() {
        System.out.println("Guau!");
    }
}
