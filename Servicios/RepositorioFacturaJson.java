package Servicios;

import Facturacion.DatosFiscales;
import Facturacion.EstadoFactura;
import Facturacion.Factura;
import Ventas.Pedido;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RepositorioFacturaJson extends RepositorioJsonBase<Factura> {

    private final IRepositorio<Pedido, Long> repositorioPedidos;

    public RepositorioFacturaJson(String ruta, IRepositorio<Pedido, Long> repositorioPedidos) {
        super(ruta);
        this.repositorioPedidos = repositorioPedidos;
    }

    public List<Factura> listarPorCliente(long clienteId) {
        return listarTodos().stream()
                .filter(f -> f.getPedido() != null && f.getPedido().getCliente() != null
                        && f.getPedido().getCliente().getId() == clienteId)
                .collect(Collectors.toList());
    }

    @Override
    protected Map<String, Object> aMapa(Factura f) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", f.getId());
        m.put("pedidoId", f.getPedido() == null ? null : f.getPedido().getId());
        m.put("fechaSolicitud", comoTexto(f.getFechaSolicitud()));
        m.put("fechaEmision", comoTexto(f.getFechaEmision()));
        m.put("estado", f.getEstado().name());
        m.put("tasaIva", f.getTasaIva());
        m.put("subtotal", f.getSubtotal());
        m.put("iva", f.getIva());
        m.put("total", f.getTotal());
        DatosFiscales d = f.getDatosFiscales();
        m.put("rfc", d.getRfc());
        m.put("nombreRazonSocial", d.getNombreRazonSocial());
        m.put("regimenFiscal", d.getRegimenFiscal());
        m.put("codigoPostal", d.getCodigoPostal());
        m.put("usoCFDI", d.getUsoCFDI());
        return m;
    }

    @Override
    protected Factura deMapa(Map<?, ?> m) {
        Factura f = new Factura();
        f.setId(entero(m, "id"));
        Long pedidoId = entero(m, "pedidoId");
        if (pedidoId != null) {
            repositorioPedidos.buscarPorId(pedidoId).ifPresent(f::setPedido);
        }
        f.setFechaSolicitud(fechaHora(m, "fechaSolicitud"));
        f.setFechaEmision(fechaHora(m, "fechaEmision"));
        f.setEstado(EstadoFactura.valueOf(texto(m, "estado")));
        f.setTasaIva(decimal(m, "tasaIva"));
        f.setSubtotal(decimal(m, "subtotal"));
        f.setIva(decimal(m, "iva"));
        f.setTotal(decimal(m, "total"));
        DatosFiscales d = new DatosFiscales();
        d.setRfc(texto(m, "rfc"));
        d.setNombreRazonSocial(texto(m, "nombreRazonSocial"));
        d.setRegimenFiscal(texto(m, "regimenFiscal"));
        d.setCodigoPostal(texto(m, "codigoPostal"));
        d.setUsoCFDI(texto(m, "usoCFDI"));
        f.setDatosFiscales(d);
        return f;
    }

    @Override
    protected Long idDe(Factura f) {
        return f.getId();
    }

    @Override
    protected void asignarId(Factura f, long id) {
        f.setId(id);
    }
}
