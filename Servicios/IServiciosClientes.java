package Servicios;

import claseseguridad.Cliente;

import java.util.Optional;

public interface IServiciosClientes {

    void registrarCliente(Cliente cliente);

    Optional<Cliente> buscarPorId(String id);

    void actualizarDatos(Object... args);
}
