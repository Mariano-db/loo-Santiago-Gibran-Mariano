package Exepciones;

public class CarritoVacioException extends Exception {

    private static final long serialVersionUID = 1L;

    public CarritoVacioException(String message) {
        super(message);
    }

    public CarritoVacioException(String message, Throwable cause) {
        super(message, cause);
    }
}
