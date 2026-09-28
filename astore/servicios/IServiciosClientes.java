package astore.servicios;

import java.util.Optional;

import astore.seguridad.Cliente;

public interface IServiciosClientes {

    void registrarCliente(Cliente cliente);

    Optional<Cliente> buscarPorId(String id);

    void actualizarDatos(Object... args);
}
