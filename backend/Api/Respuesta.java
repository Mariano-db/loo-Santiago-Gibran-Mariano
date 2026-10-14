package Api;

public final class Respuesta {

    private final int estado;
    private final Object cuerpo;

    private Respuesta(int estado, Object cuerpo) {
        this.estado = estado;
        this.cuerpo = cuerpo;
    }

    public static Respuesta ok(Object cuerpo) {
        return new Respuesta(200, cuerpo);
    }

    public static Respuesta creado(Object cuerpo) {
        return new Respuesta(201, cuerpo);
    }

    public static Respuesta con(int estado, Object cuerpo) {
        return new Respuesta(estado, cuerpo);
    }

    public int getEstado() {
        return estado;
    }

    public Object getCuerpo() {
        return cuerpo;
    }
}
