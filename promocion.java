import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class promocion {

    private Long id;
    private String nombre;
    private String descripcion;
    private tipodescuento tipoDescuento;
    private double valor;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activa;
    private List<producto> productos = new ArrayList<>();

    public promocion() {
    }

    public boolean estaActiva() {
        return activa;
    }

    public double calcularDescuento(double monto) {
        return 0;
    }

    public void activar() {
        this.activa = true;
    }

    public void desactivar() {
        this.activa = false;
    }

    public void agregarRegla() {
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

    public tipodescuento getTipoDescuento() {
        return tipoDescuento;
    }

    public void setTipoDescuento(tipodescuento tipoDescuento) {
        this.tipoDescuento = tipoDescuento;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public List<producto> getProductos() {
        return productos;
    }

    public void setProductos(List<producto> productos) {
        this.productos = productos;
    }
}
