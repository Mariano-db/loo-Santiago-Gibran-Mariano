import java.util.List;
import java.util.Optional;

public interface irepositorio<T, ID> {

    void guardar(T entidad);

    Optional<T> buscarPorId(ID id);

    List<T> listarTodos();

    void eliminar(ID id);
}