package Panel;

import Servicios.ServicioUsuarios;
import claseseguridad.Cliente;
import claseseguridad.TipoCliente;
import claseseguridad.Usuario;

import java.util.Optional;

public class MenuAcceso {

    private final Entrada in;
    private final ServicioUsuarios servicioUsuarios;

    public MenuAcceso(Entrada in, ServicioUsuarios servicioUsuarios) {
        this.in = in;
        this.servicioUsuarios = servicioUsuarios;
    }

    public Usuario pedirUsuario() {
        while (true) {
            System.out.println("1. Iniciar sesion");
            System.out.println("2. Registrarse");
            System.out.println("3. Continuar como invitado");
            System.out.println("0. Salir");
            String opcion = in.texto("Elige una opcion: ");
            System.out.println();

            switch (opcion) {
                case "1": {
                    Optional<Usuario> usuario = iniciarSesion();
                    if (usuario.isPresent()) {
                        return usuario.get();
                    }
                    break;
                }
                case "2":
                    registrar();
                    break;
                case "3":
                    return crearInvitado();
                case "0":
                    return null;
                default:
                    System.out.println("Opcion invalida.");
            }
            System.out.println();
        }
    }

    private Optional<Usuario> iniciarSesion() {
        String identificador = in.texto("Usuario o correo: ");
        String password = in.texto("Contrasena: ");
        Optional<Usuario> usuario = servicioUsuarios.iniciarSesion(identificador, password);
        if (usuario.isPresent()) {
            System.out.println("Bienvenido, " + usuario.get().getNombre() + ".");
            System.out.println();
        } else {
            System.out.println("Usuario o contrasena incorrectos.");
        }
        return usuario;
    }

    private void registrar() {
        String nombre = in.texto("Nombre completo: ");
        String username = in.texto("Usuario: ");
        String email = in.texto("Correo: ");
        String telefono = in.texto("Telefono (opcional): ");
        String password = in.texto("Contrasena (minimo 8 caracteres, con letra y numero): ");
        try {
            servicioUsuarios.registrar(nombre, username, email, telefono, password);
            System.out.println("Registro exitoso. Ya puedes iniciar sesion.");
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo registrar: " + e.getMessage());
        }
    }

    private Usuario crearInvitado() {
        Cliente invitado = new Cliente();
        invitado.setNombre("Invitado");
        invitado.setTipo(TipoCliente.INVITADO);
        invitado.activar();
        invitado.iniciarSesion();
        return invitado;
    }
}
