package Exepciones;

public class PagoInvalidoException extends Exception {

    public PagoInvalidoException(String message) {
        super(message);
    }

    public PagoInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
