package Servicios;

import claseprovedores.DetalleOrdenCompra;
import claseprovedores.OrdenCompra;

public class ServicioCompras {

    private final RepositorioOrdenCompraJson ordenes;
    private final ServiciosInventario inventario;

    public ServicioCompras(RepositorioOrdenCompraJson ordenes, ServiciosInventario inventario) {
        this.ordenes = ordenes;
        this.inventario = inventario;
    }

    public void recibir(OrdenCompra orden) {
        orden.marcarRecibida();
        for (DetalleOrdenCompra d : orden.getDetalles()) {
            Long varianteId = d.getVariante() == null ? null : d.getVariante().getId();
            inventario.reabastecer(d.getProducto().getId(), varianteId, d.getCantidad(),
                    "Compra a proveedor", "Orden de compra #" + orden.getId());
        }
        ordenes.guardar(orden);
    }
}
