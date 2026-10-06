package Exepciones;

public class ProductoNoEncontradoException extends Exception {

    private static final long serialVersionUID = 1L;

    public ProductoNoEncontradoException(String message) {
        super(message);
    }

    public ProductoNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
