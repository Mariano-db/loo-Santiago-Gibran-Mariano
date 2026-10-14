package Api;

@FunctionalInterface
public interface Manejador {

    Respuesta manejar(Peticion peticion) throws Exception;
}
