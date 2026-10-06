package Servicios;

import Catalogo.Producto;
import Catalogo.VarianteProducto;
import Exepciones.StockInsuficienteException;
import Inventario.Existencia;
import Inventario.MovimientoInventario;
import Inventario.NotificadorStockBajo;
import Inventario.ObservadorStock;
import Inventario.TipoMovimiento;
import Ventas.DetallePedido;
import Ventas.Pedido;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ServiciosInventario implements IServiciosInventario {

    public static final int MINIMO_PRODUCTO = 10;
    public static final int MINIMO_VARIANTE = 2;

    private final RepositorioExistenciaJson existencias;
    private final RepositorioMovimientoJson movimientos;
    private final RepositorioProductoJson productos;
    private final List<ObservadorStock> observadores = new ArrayList<>();
    private final NotificadorStockBajo notificador = new NotificadorStockBajo();

    public ServiciosInventario(RepositorioExistenciaJson existencias, RepositorioMovimientoJson movimientos,
                               RepositorioProductoJson productos) {
        this.existencias = existencias;
        this.movimientos = movimientos;
        this.productos = productos;
        this.observadores.add(notificador);
        if (!existencias.existeArchivo()) {
            cargarDesdeCatalogo();
        }
    }

    private void cargarDesdeCatalogo() {
        List<Existencia> lista = new ArrayList<>();
        long id = 1;
        for (Producto p : productos.listarTodos()) {
            List<VarianteProducto> variantes = p.getVariantes();
            if (variantes.isEmpty()) {
                lista.add(nueva(id++, p.getId(), null, p.getStock(), MINIMO_PRODUCTO));
            } else {
                int base = p.getStock() / variantes.size();
                int resto = p.getStock() % variantes.size();
                for (int i = 0; i < variantes.size(); i++) {
                    lista.add(nueva(id++, p.getId(), variantes.get(i).getId(), base + (i < resto ? 1 : 0), MINIMO_VARIANTE));
                }
            }
        }
        existencias.reemplazarTodos(lista);
        for (Existencia e : lista) {
            registrarMovimiento(e, TipoMovimiento.ENTRADA, e.getStockFisico(), "Carga inicial desde el catalogo", null);
        }
    }

    private Existencia nueva(Long id, Long productoId, Long varianteId, int stock, int minimo) {
        Existencia e = new Existencia();
        e.setId(id);
        e.setProductoId(productoId);
        e.setVarianteId(varianteId);
        e.setStockFisico(Math.max(stock, 0));
        e.setStockMinimo(minimo);
        return e;
    }

    @Override
    public int consultarStock(Long productoId, Long varianteId) {
        return existencias.buscar(productoId, varianteId).map(Existencia::calcularDisponible).orElse(0);
    }

    public Optional<Existencia> obtener(Long productoId, Long varianteId) {
        return existencias.buscar(productoId, varianteId);
    }

    public List<Existencia> listar() {
        return existencias.listarTodos();
    }

    public List<Existencia> bajoMinimo() {
        List<Existencia> bajos = new ArrayList<>();
        for (Existencia e : existencias.listarTodos()) {
            if (e.estaBajoMinimo()) {
                bajos.add(e);
            }
        }
        return bajos;
    }

    public List<MovimientoInventario> ultimosMovimientos(int cuantos) {
        List<MovimientoInventario> todos = movimientos.listarTodos();
        return todos.subList(Math.max(0, todos.size() - cuantos), todos.size());
    }

    public NotificadorStockBajo getNotificador() {
        return notificador;
    }

    public void agregarObservador(ObservadorStock observador) {
        observadores.add(observador);
    }

    public String describir(Long productoId, Long varianteId) {
        Optional<Producto> p = productos.buscarPorId(productoId);
        String nombre = p.map(Producto::getNombre).orElse("Producto #" + productoId);
        if (varianteId != null && p.isPresent()) {
            for (VarianteProducto v : p.get().getVariantes()) {
                if (v.getId().equals(varianteId)) {
                    return nombre + " (" + v.getColor() + ", " + v.getTalla() + ")";
                }
            }
        }
        return nombre;
    }

    public void registrarProducto(Producto producto) {
        if (producto.getVariantes().isEmpty()) {
            crearExistencia(producto.getId(), null, MINIMO_PRODUCTO);
        } else {
            for (VarianteProducto v : producto.getVariantes()) {
                crearExistencia(producto.getId(), v.getId(), MINIMO_VARIANTE);
            }
        }
    }

    private void crearExistencia(Long productoId, Long varianteId, int minimo) {
        if (existencias.buscar(productoId, varianteId).isEmpty()) {
            existencias.guardar(nueva(null, productoId, varianteId, 0, minimo));
        }
    }

    public void eliminarProducto(Long productoId) {
        for (Existencia e : existencias.listarTodos()) {
            if (productoId.equals(e.getProductoId())) {
                existencias.eliminar(e.getId());
            }
        }
    }

    @Override
    public void reabastecer(Long productoId, Long varianteId, int cantidad, String motivo, String referencia) {
        Existencia e = existenciaOFalla(productoId, varianteId);
        e.reponer(cantidad);
        existencias.guardar(e);
        registrarMovimiento(e, TipoMovimiento.ENTRADA, cantidad, motivo, referencia);
        sincronizarProducto(productoId);
    }

    public void ajustar(Long productoId, Long varianteId, int nuevoStock, String motivo) {
        Existencia e = existenciaOFalla(productoId, varianteId);
        int diferencia = nuevoStock - e.getStockFisico();
        e.setStockFisico(nuevoStock);
        existencias.guardar(e);
        registrarMovimiento(e, TipoMovimiento.AJUSTE, diferencia, motivo, null);
        sincronizarProducto(productoId);
        avisarSiBajo(e);
    }

    public void establecerMinimo(Long productoId, Long varianteId, int minimo) {
        Existencia e = existenciaOFalla(productoId, varianteId);
        e.setStockMinimo(minimo);
        existencias.guardar(e);
    }

    public void validarDisponibilidad(List<DetallePedido> lineas) throws StockInsuficienteException {
        StringBuilder faltantes = new StringBuilder();
        for (DetallePedido d : agrupar(lineas)) {
            Long varianteId = d.getVariante() == null ? null : d.getVariante().getId();
            int disponible = consultarStock(d.getProducto().getId(), varianteId);
            if (disponible < d.getCantidad()) {
                faltantes.append(String.format("%n  - %s: pides %d, hay %d", describir(d.getProducto().getId(), varianteId),
                        d.getCantidad(), disponible));
            }
        }
        if (faltantes.length() > 0) {
            throw new StockInsuficienteException("Stock insuficiente:" + faltantes);
        }
    }

    @Override
    public void confirmarSalida(Pedido pedido) throws StockInsuficienteException {
        validarDisponibilidad(pedido.getDetalles());
        for (DetallePedido d : pedido.getDetalles()) {
            Long productoId = d.getProducto().getId();
            Long varianteId = d.getVariante() == null ? null : d.getVariante().getId();
            Existencia e = existenciaOFalla(productoId, varianteId);
            e.retirar(d.getCantidad());
            existencias.guardar(e);
            registrarMovimiento(e, TipoMovimiento.SALIDA, -d.getCantidad(), "Venta", "Pedido #" + pedido.getId());
            sincronizarProducto(productoId);
            avisarSiBajo(e);
        }
    }

    public void reponerPedido(Pedido pedido, String motivo) {
        for (DetallePedido d : pedido.getDetalles()) {
            Long varianteId = d.getVariante() == null ? null : d.getVariante().getId();
            reabastecer(d.getProducto().getId(), varianteId, d.getCantidad(), motivo, "Pedido #" + pedido.getId());
        }
    }

    private List<DetallePedido> agrupar(List<DetallePedido> lineas) {
        List<DetallePedido> agrupadas = new ArrayList<>();
        for (DetallePedido d : lineas) {
            Long varianteId = d.getVariante() == null ? null : d.getVariante().getId();
            DetallePedido existente = null;
            for (DetallePedido a : agrupadas) {
                Long varianteA = a.getVariante() == null ? null : a.getVariante().getId();
                if (a.getProducto().getId().equals(d.getProducto().getId())
                        && java.util.Objects.equals(varianteA, varianteId)) {
                    existente = a;
                }
            }
            if (existente == null) {
                DetallePedido copia = new DetallePedido();
                copia.setProducto(d.getProducto());
                copia.setVariante(d.getVariante());
                copia.setCantidad(d.getCantidad());
                agrupadas.add(copia);
            } else {
                existente.setCantidad(existente.getCantidad() + d.getCantidad());
            }
        }
        return agrupadas;
    }

    private Existencia existenciaOFalla(Long productoId, Long varianteId) {
        return existencias.buscar(productoId, varianteId).orElseThrow(
                () -> new IllegalArgumentException("No hay existencias registradas para " + describir(productoId, varianteId)));
    }

    private void registrarMovimiento(Existencia e, TipoMovimiento tipo, int cantidad, String motivo, String referencia) {
        MovimientoInventario m = new MovimientoInventario();
        m.setProductoId(e.getProductoId());
        m.setVarianteId(e.getVarianteId());
        m.setDescripcion(describir(e.getProductoId(), e.getVarianteId()));
        m.setTipo(tipo);
        m.setCantidad(cantidad);
        m.setStockResultante(e.getStockFisico());
        m.setFecha(LocalDateTime.now());
        m.setMotivo(motivo);
        m.setReferencia(referencia);
        movimientos.guardar(m);
    }

    private void sincronizarProducto(Long productoId) {
        int total = 0;
        for (Existencia e : existencias.listarTodos()) {
            if (productoId.equals(e.getProductoId())) {
                total += e.calcularDisponible();
            }
        }
        final int stockTotal = total;
        productos.buscarPorId(productoId).ifPresent(p -> {
            p.actualizarStock(stockTotal);
            productos.guardar(p);
        });
    }

    private void avisarSiBajo(Existencia e) {
        if (e.estaBajoMinimo()) {
            productos.buscarPorId(e.getProductoId()).ifPresent(p -> {
                for (ObservadorStock o : observadores) {
                    o.notificarStockBajo(p, e.calcularDisponible());
                }
            });
        }
    }
}
