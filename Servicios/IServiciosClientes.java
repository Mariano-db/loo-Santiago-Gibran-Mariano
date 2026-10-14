package Servicios;

import claseseguridad.Cliente;
import claseseguridad.Usuario;

import java.util.List;
import java.util.Optional;

public interface IServiciosClientes {

    Cliente registrarCliente(String nombre, String email, String password);

    Optional<Usuario> iniciarSesion(String email, String password);

    void cerrarSesion(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    List<Usuario> listarUsuarios();

    void actualizarDatos(Usuario usuario);
}
