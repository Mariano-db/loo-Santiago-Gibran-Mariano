import java.util.ArrayList;
import java.util.List;

public class Proveedor {

    private Long id;
    private String nombre;
    private String telefono;
    private String correo;
    private boolean activo;
    private List<Suministro> suministros = new ArrayList<>();
    private List<OrdenCompra> ordenesCompra = new ArrayList<>();

    public Proveedor() {
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<Suministro> getSuministros() {
        return suministros;
    }

    public void setSuministros(List<Suministro> suministros) {
        this.suministros = suministros;
    }

    public List<OrdenCompra> getOrdenescompra() {
        return ordenesCompra;
    }

    public void setOrdenescompra(List<Ordencompra> ordenesCompra) {
        this.ordenesCompra = ordenesCompra;
    }
}
