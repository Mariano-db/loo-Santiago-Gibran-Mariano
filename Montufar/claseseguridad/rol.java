public class rol {

    private long id;
    private string nombre;
    private string descripcion;
    private set<permiso> permisos = new hashset<>();

    public rol() {
    }

    public void agregarpermiso(permiso permiso) {
        permisos.add(permiso);
    }

    public void quitarpermiso(permiso permiso) {
        permisos.remove(permiso);
    }

    public boolean tienepermiso(permiso permiso) {
        return permisos.contains(permiso);
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

    public string getdescripcion() {
        return descripcion;
    }

    public void setdescripcion(string descripcion) {
        this.descripcion = descripcion;
    }

    public set<permiso> getpermisos() {
        return permisos;
    }

    public void setpermisos(set<permiso> permisos) {
        this.permisos = permisos;
    }
}