package Exepciones;

public class PagoInvalidoException extends Exception {

    private static final long serialVersionUID = 1L;

    public PagoInvalidoException(String message) {
        super(message);
    }

    public PagoInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
