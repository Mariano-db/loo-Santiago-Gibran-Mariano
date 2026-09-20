public abstract class usuario {

    private long id;
    private string nombre;
    private string username;
    private string email;
    private string passwordhash;
    private string telefono;
    private rol rol;
    private set<permiso> permisos;
    private boolean activo;

    public usuario() {
    }

    public boolean autenticar(string password) {
        return false;
    }

    public void verificarpermiso(permiso permiso) {
    }

    public boolean tienepermiso(permiso permiso) {
        return false;
    }

    public void asignarpermiso(permiso permiso) {
    }

    public boolean iniciarsesion() {
        return false;
    }

    public void cerrarsesion() {
    }

    public void actualizarperfil() {
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    public long getid() {
        return id;
    }

    public void setid(long id) {
        this.id = id;
    }

    public string getnombre() {
        return nombre;
    }

    public void setnombre(string nombre) {
        this.nombre = nombre;
    }

    public string getusername() {
        return username;
    }

    public void setusername(string username) {
        this.username = username;
    }

    public string getemail() {
        return email;
    }

    public void setemail(string email) {
        this.email = email;
    }

    public string getpasswordhash() {
        return passwordhash;
    }

    public void setpasswordhash(string passwordhash) {
        this.passwordhash = passwordhash;
    }

    public string gettelefono() {
        return telefono;
    }

    public void settelefono(string telefono) {
        this.telefono = telefono;
    }

    public rol getrol() {
        return rol;
    }

    public void setrol(rol rol) {
        this.rol = rol;
    }

    public set<permiso> getpermisos() {
        return permisos;
    }

    public void setpermisos(set<permiso> permisos) {
        this.permisos = permisos;
    }

    public boolean isactivo() {
        return activo;
    }

    public void setactivo(boolean activo) {
        this.activo = activo;
    }
}
