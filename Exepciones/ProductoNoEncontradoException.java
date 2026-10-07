package Exepciones;

public class ProductoNoEncontradoException extends Exception {

    public ProductoNoEncontradoException(String message) {
        super(message);
    }

    public ProductoNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
