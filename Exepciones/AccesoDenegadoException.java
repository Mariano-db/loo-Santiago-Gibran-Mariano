package Exepciones;

public class AccesoDenegadoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AccesoDenegadoException(String message) {
        super(message);
    }

    public AccesoDenegadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
