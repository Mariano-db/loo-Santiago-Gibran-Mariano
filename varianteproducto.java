public class varianteproducto {

    private Long id;
    private String sku;
    private String talla;
    private String color;
    private String modelo;
    private double precioVenta;
    private estadoproducto estado;

    public varianteproducto() {
    }

    public void actualizarPrecio() {
    }

    public boolean estaDisponible() {
        return false;
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

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public estadoproducto getEstado() {
        return estado;
    }

    public void setEstado(estadoproducto estado) {
        this.estado = estado;
    }
}
