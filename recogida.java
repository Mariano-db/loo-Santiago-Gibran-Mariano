import java.time.LocalDateTime;

public class recogida {
    private final int id;
    private LocalDateTime fecha;
    private LocalDateTime horaEntrega;
    private estadorecogida estado;

    public recogida(int id, LocalDateTime fecha, 
                    LocalDateTime horaEntrega, estadorecogida estado) {
        this.id = id;
        this.fecha = fecha;
        this.horaEntrega = horaEntrega;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public LocalDateTime getHoraEntrega() {
        return horaEntrega;
    }

    public estadorecogida getEstado() {
        return estado;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public void setHoraEntrega(LocalDateTime horaEntrega) {
        this.horaEntrega = horaEntrega;
    }

    public void registrarEntrega() {
        this.estado = estadorecogida.ENTREGADA;
    }

    public boolean asignarViaje() {
        if (this.estado == estadorecogida.PENDIENTE) {
            this.estado = estadorecogida.LISTA;
            return true;
        }
        return false;
    }
}