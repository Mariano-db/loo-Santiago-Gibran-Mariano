package Servicios;

import claseseguridad.Cliente;
import claseseguridad.Contrasenas;
import claseseguridad.Rol;
import claseseguridad.TipoCliente;
import claseseguridad.Usuario;

import java.util.Optional;
import java.util.regex.Pattern;

public class ServicioUsuarios {

    private static final Pattern FORMATO_EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern FORMATO_USERNAME = Pattern.compile("^[A-Za-z0-9_.-]{3,20}$");

    private final RepositorioUsuarioJson repositorio;

    public ServicioUsuarios(RepositorioUsuarioJson repositorio) {
        this.repositorio = repositorio;
    }

    public Cliente registrar(String nombre, String username, String email, String telefono, String password) {
        validarDatos(nombre, username, email, password);

        Cliente cliente = new Cliente();
        cliente.setTipo(TipoCliente.REGISTRADO);
        completar(cliente, RepositorioUsuarioJson.ROL_CLIENTE, nombre, username, email, telefono, password);
        repositorio.guardar(cliente);
        return cliente;
    }

    private void validarDatos(String nombre, String username, String email, String password) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio.");
        }
        if (username == null || !FORMATO_USERNAME.matcher(username).matches()) {
            throw new IllegalArgumentException("El usuario debe tener de 3 a 20 caracteres (letras, numeros, . _ -).");
        }
        if (email == null || !FORMATO_EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException("El correo no tiene un formato valido.");
        }
        if (!Contrasenas.esValida(password)) {
            throw new IllegalArgumentException("La contrasena debe tener al menos 8 caracteres, una letra y un numero.");
        }
        if (repositorio.buscarPorUsername(username).isPresent()) {
            throw new IllegalArgumentException("Ese nombre de usuario ya esta registrado.");
        }
        if (repositorio.buscarPorEmail(email).isPresent()) {
            throw new IllegalArgumentException("Ese correo ya esta registrado.");
        }
    }

    private void completar(Usuario usuario, String nombreRol, String nombre, String username, String email,
                           String telefono, String password) {
        usuario.setNombre(nombre.trim());
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setTelefono(telefono == null || telefono.isBlank() ? null : telefono.trim());
        usuario.setPasswordHash(Contrasenas.hashear(password));
        Rol rol = new Rol();
        rol.setNombre(nombreRol);
        usuario.setRol(rol);
        usuario.activar();
    }

    public Optional<Usuario> iniciarSesion(String usuarioOCorreo, String password) {
        if (usuarioOCorreo == null || password == null) {
            return Optional.empty();
        }
        Optional<Usuario> encontrado = repositorio.buscarPorUsername(usuarioOCorreo);
        if (encontrado.isEmpty()) {
            encontrado = repositorio.buscarPorEmail(usuarioOCorreo);
        }
        if (encontrado.isPresent() && encontrado.get().autenticar(password) && encontrado.get().iniciarSesion()) {
            return encontrado;
        }
        return Optional.empty();
    }
}
