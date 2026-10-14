package Exepciones;

public class CarritoVacioException extends Exception {

    public CarritoVacioException(String message) {
        super(message);
    }

    public CarritoVacioException(String message, Throwable cause) {
        super(message, cause);
    }
}
