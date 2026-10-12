package Servicios;

import claseseguridad.Administrador;
import claseseguridad.Cliente;
import claseseguridad.Permiso;
import claseseguridad.TipoCliente;
import claseseguridad.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class ServicioClientes implements IServiciosClientes {

    private static final Pattern FORMATO_EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final int LONGITUD_MINIMA_PASSWORD = 8;

    private final RepositorioUsuarioJson repositorioUsuarios;

    public ServicioClientes(RepositorioUsuarioJson repositorioUsuarios) {
        this.repositorioUsuarios = repositorioUsuarios;
    }

    @Override
    public Cliente registrarCliente(String nombre, String email, String password) {
        Cliente cliente = new Cliente();
        cliente.setTipo(TipoCliente.REGISTRADO);
        prepararUsuario(cliente, nombre, email, password);
        repositorioUsuarios.guardar(cliente);
        return cliente;
    }

    public Administrador registrarAdministrador(String nombre, String email, String password) {
        Administrador administrador = new Administrador();
        prepararUsuario(administrador, nombre, email, password);
        administrador.asignarPermiso(new Permiso(Permiso.GESTIONAR_PRODUCTOS));
        administrador.asignarPermiso(new Permiso(Permiso.GESTIONAR_USUARIOS));
        administrador.asignarPermiso(new Permiso(Permiso.VER_VENTAS));
        repositorioUsuarios.guardar(administrador);
        return administrador;
    }

    public boolean hayUsuarios() {
        return !repositorioUsuarios.listarTodos().isEmpty();
    }

    @Override
    public Optional<Usuario> iniciarSesion(String email, String password) {
        return repositorioUsuarios.buscarPorEmail(email)
                .filter(usuario -> usuario.iniciarSesion(password));
    }

    @Override
    public void cerrarSesion(Usuario usuario) {
        if (usuario != null) {
            usuario.cerrarSesion();
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return repositorioUsuarios.buscarPorId(id);
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return repositorioUsuarios.listarTodos();
    }

    @Override
    public void actualizarDatos(Usuario usuario) {
        repositorioUsuarios.guardar(usuario);
    }

    private void prepararUsuario(Usuario usuario, String nombre, String email, String password) {
        validarNombre(nombre);
        validarEmail(email);
        validarPassword(password);

        String emailNormalizado = email.trim().toLowerCase();
        usuario.setNombre(nombre.trim());
        usuario.setEmail(emailNormalizado);
        usuario.setUsername(emailNormalizado);
        usuario.setRol(RepositorioUsuarioJson.crearRol(RepositorioUsuarioJson.nombreRol(usuario)));
        usuario.establecerPassword(password);
        usuario.activar();
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }
    }

    private void validarEmail(String email) {
        if (email == null || !FORMATO_EMAIL.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("El correo no tiene un formato valido");
        }
        if (repositorioUsuarios.buscarPorEmail(email).isPresent()) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese correo");
        }
    }

    private void validarPassword(String password) {
        if (password == null || password.length() < LONGITUD_MINIMA_PASSWORD) {
            throw new IllegalArgumentException("La contrasena debe tener al menos " + LONGITUD_MINIMA_PASSWORD + " caracteres");
        }
        boolean tieneLetra = password.chars().anyMatch(Character::isLetter);
        boolean tieneNumero = password.chars().anyMatch(Character::isDigit);
        if (!tieneLetra || !tieneNumero) {
            throw new IllegalArgumentException("La contrasena debe tener al menos una letra y un numero");
        }
    }
}
