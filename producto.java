import java.util.ArrayList;
import java.util.List;

public class producto {

    private Long id;
    private String sku;
    private String nombre;
    private String descripcion;
    private double precioBase;
    private double precioUnitario;
    private double tasaImpuesto;
    private int stock;
    private String marca;
    private String imagen;
    private categoria categoria;
    private estadoproducto estado;
    private String capacidad;
    private String dimensiones;
    private boolean activo;
    private List<varianteproducto> variantes = new ArrayList<>();
    private List<promocion> promociones = new ArrayList<>();

    public producto() {
    }

    public double precioConImpuesto() {
        return precioUnitario + (precioUnitario * tasaImpuesto);
    }

    public void actualizarStock(int cantidad) {
        this.stock = cantidad;
    }

    public void cambiarPrecio(double nuevoPrecio) {
        this.precioUnitario = nuevoPrecio;
    }

    public void agregarVariante() {
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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getTasaImpuesto() {
        return tasaImpuesto;
    }

    public void setTasaImpuesto(double tasaImpuesto) {
        this.tasaImpuesto = tasaImpuesto;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(categoria categoria) {
        this.categoria = categoria;
    }

    public estadoproducto getEstado() {
        return estado;
    }

    public void setEstado(estadoproducto estado) {
        this.estado = estado;
    }

    public String getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(String capacidad) {
        this.capacidad = capacidad;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<varianteproducto> getVariantes() {
        return variantes;
    }

    public void setVariantes(List<varianteproducto> variantes) {
        this.variantes = variantes;
    }

    public List<promocion> getPromociones() {
        return promociones;
    }

    public void setPromociones(List<promocion> promociones) {
        this.promociones = promociones;
    }
}
