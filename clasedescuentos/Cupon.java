package clasedescuentos;

import java.time.LocalDate;

public class Cupon {

    private Long id;
    private String codigo;
    private TipoDescuento tipo;
    private double valor;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private double compraMinima;
    private int usosMaximos;
    private int usosActuales;
    private boolean activo;

    public Cupon() {
    }

    public boolean esValido() {
        if (!activo) {
            return false;
        }
        LocalDate hoy = LocalDate.now();
        if (fechaInicio != null && hoy.isBefore(fechaInicio)) {
            return false;
        }
        if (fechaFin != null && hoy.isAfter(fechaFin)) {
            return false;
        }
        return usosMaximos == 0 || usosActuales < usosMaximos;
    }

    public boolean esAplicable(double montoCompra) {
        return esValido() && montoCompra >= compraMinima;
    }

    public String motivoNoAplicable(double montoCompra) {
        if (!activo) {
            return "El cupon esta desactivado.";
        }
        LocalDate hoy = LocalDate.now();
        if (fechaInicio != null && hoy.isBefore(fechaInicio)) {
            return "El cupon aun no es valido (inicia el " + fechaInicio + ").";
        }
        if (fechaFin != null && hoy.isAfter(fechaFin)) {
            return "El cupon expiro el " + fechaFin + ".";
        }
        if (usosMaximos != 0 && usosActuales >= usosMaximos) {
            return "El cupon ya alcanzo su limite de usos.";
        }
        if (montoCompra < compraMinima) {
            return String.format("El cupon requiere una compra minima de $%.2f MXN.", compraMinima);
        }
        return null;
    }

    public EstrategiaDescuento comoEstrategia() {
        if (tipo == TipoDescuento.PORCENTAJE) {
            DescuentoPorcentaje estrategia = new DescuentoPorcentaje();
            estrategia.setPorcentaje(valor);
            return estrategia;
        }
        DescuentoMontoFijo estrategia = new DescuentoMontoFijo();
        estrategia.setMonto(valor);
        return estrategia;
    }

    public double calcularDescuento(double montoCompra) {
        return comoEstrategia().calcularDescuento(montoCompra);
    }

    public void registrarUso() {
        usosActuales++;
    }

    public String descripcion() {
        return tipo == TipoDescuento.PORCENTAJE
                ? String.format("%s (%.0f%% de descuento)", codigo, valor)
                : String.format("%s ($%.2f MXN de descuento)", codigo, valor);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoDescuento getTipo() {
        return tipo;
    }

    public void setTipo(TipoDescuento tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        if (valor < 0 || Double.isNaN(valor)) {
            throw new IllegalArgumentException("El valor del cupon no puede ser negativo.");
        }
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

    public double getCompraMinima() {
        return compraMinima;
    }

    public void setCompraMinima(double compraMinima) {
        if (compraMinima < 0 || Double.isNaN(compraMinima)) {
            throw new IllegalArgumentException("La compra minima no puede ser negativa.");
        }
        this.compraMinima = compraMinima;
    }

    public int getUsosMaximos() {
        return usosMaximos;
    }

    public void setUsosMaximos(int usosMaximos) {
        if (usosMaximos < 0) {
            throw new IllegalArgumentException("Los usos maximos no pueden ser negativos.");
        }
        this.usosMaximos = usosMaximos;
    }

    public int getUsosActuales() {
        return usosActuales;
    }

    public void setUsosActuales(int usosActuales) {
        this.usosActuales = usosActuales;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
