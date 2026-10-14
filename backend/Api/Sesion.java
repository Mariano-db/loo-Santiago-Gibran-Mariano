package Api;

import clasecarrostotal.CarritoCompra;
import claseseguridad.Cliente;
import claseseguridad.Usuario;

import java.time.Instant;

public final class Sesion {

    private final String token;
    private final Usuario usuario;
    private CarritoCompra carrito;
    private Instant ultimoUso;

    public Sesion(String token, Usuario usuario) {
        this.token = token;
        this.usuario = usuario;
        this.carrito = nuevoCarrito();
        this.ultimoUso = Instant.now();
    }

    public CarritoCompra nuevoCarrito() {
        CarritoCompra nuevo = new CarritoCompra();
        if (usuario instanceof Cliente) {
            nuevo.setCliente((Cliente) usuario);
        }
        return nuevo;
    }

    public void reiniciarCarrito() {
        this.carrito = nuevoCarrito();
    }

    public void tocar() {
        this.ultimoUso = Instant.now();
    }

    public String getToken() {
        return token;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public CarritoCompra getCarrito() {
        return carrito;
    }

    public Instant getUltimoUso() {
        return ultimoUso;
    }
}
