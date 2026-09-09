import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Ordencompra {

    private Long id;
    private LocalDateTime fecha;
    private EstadoOrdenCompra estado;
    private double total;
    private List<DetalleOrdenCompra> detalles = new ArrayList<>();

    public Ordencompra() {
    }

    public void agregarDetalle() {
    }

    public double calcularTotal() {
        return 0;
    }

    public void marcarRecibida() {
        this.estado = EstadoOrdenCompra.RECIBIDA;
    }

    public void cancelar() {
        this.estado = EstadoOrdenCompra.CANCELADA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public EstadoOrdenCompra getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrdenCompra estado) {
        this.estado = estado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<Detalleordencompra> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<Detalleordencompra> detalles) {
        this.detalles = detalles;
    }
}
