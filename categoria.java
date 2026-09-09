import java.util.ArrayList;
import java.util.List;

public class categoria {

    private Long id;
    private String nombre;
    private String descripcion;
    private List<producto> productos = new ArrayList<>();

    public categoria() {
    }

    public void crear() {
    }

    public void actualizar() {
    }

    public void eliminar() {
    }

    public void agregarProducto(producto producto) {
        productos.add(producto);
    }

    public void eliminarProducto(producto producto) {
        productos.remove(producto);
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<producto> getProductos() {
        return productos;
    }

    public void setProductos(List<producto> productos) {
        this.productos = productos;
    }
}