import java.util.Optional;

public interface iserviciosclientes {

    void registrarCliente(cliente cliente);

    Optional<cliente> buscarPorId(String id);

    void actualizarDatos(Object... args);
}
