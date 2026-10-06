package Facturacion;

import Ventas.DetallePedido;
import Ventas.Pedido;

import java.time.LocalDateTime;
import java.util.List;

public class Factura {

    private Long id;
    private Pedido pedido;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaEmision;
    private EstadoFactura estado;
    private DatosFiscales datosFiscales;
    private double tasaIva;
    private double subtotal;
    private double iva;
    private double total;

    public Factura() {
    }

    public void solicitar() {
        this.estado = EstadoFactura.SOLICITADA;
        this.fechaSolicitud = LocalDateTime.now();
    }

    public void validarDatosFiscales() {
        if (datosFiscales == null) {
            throw new IllegalArgumentException("Faltan los datos fiscales.");
        }
        List<String> errores = datosFiscales.validar();
        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join(" ", errores));
        }
    }

    public void calcularMontos(double totalConIva, double tasaIva) {
        this.tasaIva = tasaIva;
        this.total = redondear(totalConIva);
        this.subtotal = redondear(totalConIva / (1 + tasaIva));
        this.iva = redondear(this.total - this.subtotal);
    }

    public void emitir() {
        this.estado = EstadoFactura.EMITIDA;
        this.fechaEmision = LocalDateTime.now();
    }

    public void cancelar() {
        this.estado = EstadoFactura.CANCELADA;
    }

    public String folio() {
        return id == null ? "(sin folio)" : String.format("F-%06d", id);
    }

    public String aTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== FACTURA ").append(folio()).append(" ==========\n");
        sb.append("Estado: ").append(estado).append('\n');
        sb.append("Solicitada: ").append(fechaSolicitud).append('\n');
        if (fechaEmision != null) {
            sb.append("Emitida: ").append(fechaEmision).append('\n');
        }
        sb.append("Pedido: #").append(pedido == null ? "?" : pedido.getId()).append('\n');
        sb.append("Receptor: ").append(datosFiscales.getNombreRazonSocial())
                .append(" | RFC ").append(datosFiscales.getRfc()).append('\n');
        sb.append("Regimen: ").append(datosFiscales.getRegimenFiscal())
                .append(" | C.P. ").append(datosFiscales.getCodigoPostal())
                .append(" | Uso CFDI: ").append(datosFiscales.getUsoCFDI()).append('\n');
        sb.append("--------------------------------------\n");
        if (pedido != null) {
            for (DetallePedido d : pedido.getDetalles()) {
                String variante = d.getVariante() == null ? ""
                        : " (" + d.getVariante().getColor() + ", " + d.getVariante().getTalla() + ")";
                sb.append(String.format("%dx %s%s  $%.2f%n", d.getCantidad(), d.getProducto().getNombre(),
                        variante, d.getSubtotal()));
            }
            sb.append("--------------------------------------\n");
        }
        sb.append(String.format("Subtotal:  $%.2f%n", subtotal));
        sb.append(String.format("IVA (%.0f%%): $%.2f%n", tasaIva * 100, iva));
        sb.append(String.format("TOTAL:     $%.2f MXN%n", total));
        return sb.toString();
    }

    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public EstadoFactura getEstado() {
        return estado;
    }

    public void setEstado(EstadoFactura estado) {
        this.estado = estado;
    }

    public DatosFiscales getDatosFiscales() {
        return datosFiscales;
    }

    public void setDatosFiscales(DatosFiscales datosFiscales) {
        this.datosFiscales = datosFiscales;
    }

    public double getTasaIva() {
        return tasaIva;
    }

    public void setTasaIva(double tasaIva) {
        this.tasaIva = tasaIva;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
