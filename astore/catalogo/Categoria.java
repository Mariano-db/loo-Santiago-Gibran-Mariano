package astore.catalogo;

import java.util.ArrayList;
import java.util.List;

public class Categoria {

    private Long id;
    private String nombre;
    private String descripcion;
    private List<Producto> productos = new ArrayList<>();

    public Categoria() {
    }

    public void crear() {
    }

    public void actualizar() {
    }

    public void eliminar() {
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public void eliminarProducto(Producto producto) {
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

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}
