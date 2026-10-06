package Exepciones;

public class StockInsuficienteException extends Exception {

    private static final long serialVersionUID = 1L;

    public StockInsuficienteException(String message) {
        super(message);
    }

    public StockInsuficienteException(String message, Throwable cause) {
        super(message, cause);
    }
}
