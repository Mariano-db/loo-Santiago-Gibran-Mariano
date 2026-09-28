package astore.exepciones;

public class pagoinvalidoexception extends Exception {

    public pagoinvalidoexception(String message) {
        super(message);
    }

    public pagoinvalidoexception(String message, Throwable cause) {
        super(message, cause);
    }
}
