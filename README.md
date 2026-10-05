# A-Store

Sistema de tienda en línea de merch de la **Universidad Anáhuac Cancún**. Proyecto escolar para la materia de Lenguaje Orientado a Objetos (LOO).

## Descripción

A-Store modela el dominio de una tienda en línea: catálogo de productos con variantes (talla/color), carrito de compras, pagos con múltiples métodos, inventario con reservas, facturación, órdenes de compra a proveedores, impresión/recogida de pedidos, reportes y seguridad con roles y permisos.

## Estado del proyecto

El diseño orientado a objetos está completo y el código **ya compila**: cada carpeta es un paquete Java real (con su `package` correspondiente), los nombres de clase coinciden con su archivo, y se agregaron las clases de dominio que faltaban (`Orden`, `Pedido`, `ReglaDescuento`, `EstrategiaDescuento`). El catálogo de productos ya vive en JSON (ver [Persistencia en JSON](#persistencia-en-json)) y **el carrito de compras ya funciona de punta a punta**: agregar productos (incluyendo elegir color/talla cuando aplica), quitar, ver totales y convertir el carrito en un pedido, todo desde el menú de `Main`. El resto de la lógica de negocio (pagos, ventas, inventario, etc.) sigue siendo un esqueleto a propósito: retorna valores por defecto (`0`, `false`, `null`, listas vacías) hasta que se implemente. Ver [Pendientes](#pendientes).

No hay ningún dato de ejemplo/ficticio en el código: el catálogo en `data/productos.json` es el catálogo real de la tienda.

## Estructura del proyecto

Cada carpeta es un paquete de Java (el nombre del paquete es el nombre de la carpeta tal cual):

| Paquete | Contenido |
|---|---|
| `Catalogo` | Producto, Categoria, EstadoProducto, VarianteProducto, Promocion |
| `clasecarrostotal` | CarritoCompra, ItemCarrito, DetalleCarrito, EstadoCarrito |
| `clasedescuentos` | Cupon, ReglaDescuento y sus variantes (cantidad, fecha, producto, rol, cupón), EstrategiaDescuento (SinDescuento, DescuentoMontoFijo) |
| `clasepagos` | Pago y sus variantes (tarjeta, PayPal, efectivo, transferencia), EstrategiaPago |
| `claseprovedores` | Proveedor, OrdenCompra, DetalleOrdenCompra, Suministro |
| `claseimpresion` | SolicitudImpresion, Recogida |
| `clasereportes` | Reporte y sus variantes (ventas, inventario, clientes), GeneradorReportes |
| `claseseguridad` | Usuario y sus roles (Administrador, Vendedor, Cliente), Rol, Permiso |
| `Exepciones` | Excepciones propias del dominio (stock insuficiente, carrito vacío, pago inválido, etc.) |
| `Facturacion` | Factura y DatosFiscales |
| `Inventario` | Inventario, Existencia, Reserva, MovimientoInventario, ObservadorStock |
| `Servicios` | Interfaces e implementaciones de servicios (ventas, inventario, pagos, clientes), repositorio genérico (`IRepositorio`) y los repositorios JSON (`RepositorioCategoriaJson`, `RepositorioProductoJson`) |
| `Ventas` | Orden, Pedido y sus detalles, Devolucion |
| `Utils` | Configuración general del sistema |
| `Json` | Parser y escritor de JSON propios, sin dependencias externas (`JsonLector`, `JsonEscritor`) |

`Main.java`, en la raíz del proyecto, es el punto de entrada.

## Persistencia en JSON

No se usa ninguna librería externa: `Json/JsonLector.java` y `Json/JsonEscritor.java` son un parser y un escritor de JSON hechos a mano (objetos, arreglos, strings con escapes, números y booleanos), suficientes para las necesidades del proyecto.

Sobre eso, `Servicios/RepositorioCategoriaJson.java` y `Servicios/RepositorioProductoJson.java` implementan `IRepositorio<T, ID>` leyendo y escribiendo:

- `data/categorias.json` — las 5 categorías de la tienda.
- `data/productos.json` — el catálogo real de A-Store: 14 productos. Los de ropa (`Gorra Anahuac`, `Sueter Anahuac`) traen variantes por color (naranja, blanco, gris, negro) y talla (XS–XXL): 24 variantes cada uno. Los termos no tienen variantes: cada uno es un producto distinto (`Termo de Metal Anahuac`, apto para bebidas calientes y frías; `Termo Plastico Anahuac`, solo frías), diferenciados por su campo `capacidad`.

Precios, stock y SKUs son valores que yo definí (a petición tuya) como punto de partida; no vienen de ningún dato ficticio del negocio, así que edítalos libremente en los `.json` cuando tengas los reales — no hace falta tocar código Java para eso.

Cada `Producto` en el JSON referencia su categoría por `categoriaId`; el repositorio resuelve esa referencia contra `RepositorioCategoriaJson` al cargar.

## Carrito de compras

`clasecarrostotal/CarritoCompra.java` ya tiene lógica real, no solo esqueleto:

- `agregarProducto(producto, variante, cantidad)` — si el producto ya está en el carrito con la misma variante, suma la cantidad en vez de duplicar la línea. Hay un overload sin `variante` para productos sin variantes.
- `eliminarProducto(indice)` / `actualizarCantidad(indice, cantidad)`.
- `calcularTotal()` / `totalConDescuento()` (aplica la `EstrategiaDescuento` del carrito si tiene una asignada).
- `validarNoVacio()` / `convertirAPedido()` — ambos lanzan `CarritoVacioException` si el carrito está vacío.

Para soportar esto se le agregó un campo `variante` a `ItemCarrito` y a `Ventas/DetallePedido.java` (antes solo guardaban el producto base, sin decir qué color/talla se eligió).

Desde `Main`, las opciones 2–5 del menú ejercitan todo este flujo contra el catálogo real en JSON. El pedido generado al finalizar la compra **se imprime pero no se guarda** — todavía no hay un repositorio JSON para `Pedido`/`Orden`.

## Patrones de diseño utilizados

- **Strategy**: métodos de pago (`EstrategiaPago`) y descuentos (`EstrategiaDescuento`).
- **Observer**: notificación de stock bajo (`ObservadorStock` → `NotificadorStockBajo`).
- **Repository**: interfaz genérica `IRepositorio<T, ID>` como base para la futura persistencia en JSON.
- **Estados como enum**: `EstadoPago`, `EstadoOrden`, `EstadoPedido`, `EstadoCarrito`, `EstadoFactura`, `EstadoOrdenCompra`, `EstadoReserva`, `EstadoRecogida`.

## Tecnologías

- Java (sin dependencias externas)
- Persistencia: archivos JSON en `data/`, leídos/escritos con un parser propio (ver [Persistencia en JSON](#persistencia-en-json))

## Cómo compilar y ejecutar

Desde la raíz del proyecto:

```bash
javac -d out $(find . -name "*.java")
java -cp out Main
```

El programa lee `data/categorias.json` y `data/productos.json` con rutas relativas, así que hay que ejecutarlo desde la raíz del proyecto (si usas VS Code, confirma que la carpeta raíz del workspace sea esta).

## Verificación del estado actual

Última revisión: compila con `javac` 25 (90 archivos `.java`, 0 errores, 5 warnings `[serial]` cosméticos en `Exepciones/`). El flujo de consola (ver catálogo → agregar al carrito → ver carrito → finalizar compra) se ejecutó y funciona. Todo lo demás es esqueleto.

## Qué le falta y cómo mejorarlo

Ordenado por prioridad. Cada punto indica **qué hacer** y **qué archivos/paquetes tocar**.

### 1. Sistema de usuarios (login y registro)
Hoy `Main` crea un `Cliente` "Invitado" en memoria en cada ejecución, y `Usuario.autenticar()` siempre devuelve `false`.
- Crear `Servicios/RepositorioUsuarioJson.java` (`IRepositorio<Usuario, Long>`) con `data/usuarios.json`; un campo `rol` decide si se instancia `Cliente`, `Vendedor` o `Administrador`.
- Implementar `autenticar`, `iniciarSesion`, `cerrarSesion`, `tienePermiso`, `verificarPermiso` en `claseseguridad/Usuario.java` (hash con `MessageDigest` SHA-256 + sal, sin librerías).
- Agregar registro / login / seguir como invitado al menú de `Main.java`; implementar `Servicios/IServiciosClientes`.
- Validar correo único y formato de contraseña; lanzar `Exepciones/AccesoDenegadoException` cuando falte permiso.
- Necesita un administrador inicial en `data/usuarios.json` (pedir credenciales de prueba al equipo).

### 2. Persistir pedidos e historial de compras
`CarritoCompra.convertirAPedido()` genera el `Pedido`, pero se pierde al cerrar.
- Crear `Servicios/RepositorioPedidoJson.java` + `data/pedidos.json`.
- Ligar `Ventas/Pedido` al `Cliente` y añadir en `Main` la opción "Ver mis pedidos" (depende del punto 1).
- Decidir si se usa `Pedido` u `Orden` como entidad final; hoy `Ventas/Orden` y `DetalleOrden` no se usan y no tienen campo `variante`.

### 3. Stock real, también por variante
`Producto.stock` es un total; finalizar compra no descuenta nada y se puede vender de más.
- Conectar `Inventario/Existencia` (ya tiene `varianteProducto`, `stockFisico`, `stockReservado`) y `Inventario/Inventario`.
- Implementar `Servicios/ServiciosInventario` (hoy todo devuelve `0`/vacío), `Reserva`, `MovimientoInventario`.
- Validar en `CarritoCompra.agregarProducto` y lanzar `Exepciones/StockInsuficienteException`.
- Descontar al finalizar compra en `Main`/`Servicios/ServicioVentas`; persistir existencias en `data/existencias.json`.
- Activar el Observer: `Inventario/NotificadorStockBajo` cuando baje del mínimo.

### 4. Pagos
Solo `PagoEfectivo` tiene lógica; tarjeta, transferencia y PayPal devuelven `false`.
- Implementar `procesar()` en `clasepagos/PagoTarjeta`, `PagoTransferencia`, `PagoPaypal` (simulado, con validaciones: número de tarjeta Luhn, vencimiento, etc.).
- Implementar `Servicios/ServicioPagos.procesarPago` (hoy vacío) y lanzar `Exepciones/PagoInvalidoException`.
- Agregar paso "elegir método de pago" al checkout de `Main`.

### 5. Descuentos y cupones desde el menú
`CarritoCompra.totalConDescuento()` ya funciona, pero nada en `Main` lo usa.
- Implementar `aplicar`/`esAplicable` en `clasedescuentos/Regla*` y `Cupon`; guardar cupones en `data/cupones.json`.
- Añadir "Aplicar cupón" al menú de `Main` y mostrar el desglose del descuento.

### 6. Panel de administrador
- Menú solo para `Administrador`: alta/baja/edición de productos (los repositorios JSON ya tienen `guardar`/`eliminar`), cambio de precio y stock, gestión de cupones.
- Tocar: `Main.java` (o nueva clase de menú), `Catalogo/`, `claseseguridad/Permiso`, `Rol`.

### 7. Paquetes que siguen vacíos de lógica
- `Facturacion` (`Factura`, `DatosFiscales`): generar factura a partir de un pedido pagado, con IVA de `Utils/ConfiguracionSistema`.
- `clasereportes` (`ReporteVentas`, `ReporteInventario`, `ReporteClientes`, `GeneradorReportes`): leer pedidos/existencias/usuarios persistidos (depende de 1–3).
- `claseprovedores` (`OrdenCompra`, `Proveedor`, `Suministro`): reabastecer inventario desde una orden de compra.
- `claseimpresion` (`SolicitudImpresion`, `Recogida`): estados de impresión y recogida del pedido.
- `Ventas/Devolucion`: devoluciones que reingresen stock.

### 8. Calidad del código
- **Validación en setters**: precio y stock no negativos, correo con formato válido (`Catalogo/Producto`, `claseseguridad/Usuario`, etc.).
- **Tests unitarios**: no hay ninguno. Decidir si se usa JUnit con `.jar` manual en `lib/` o pruebas propias sin dependencias.
- **Warnings `[serial]`**: añadir `serialVersionUID` a las 5 excepciones de `Exepciones/`.
- **Errores de entrada en `Main`**: manejar entradas no numéricas sin que truene el menú.
- **Persistencia**: los repositorios JSON leen/escriben el archivo completo en cada operación; está bien a esta escala, pero habría que cachear si crece.

### Orden sugerido
1 → 2 → 3 → 4 (checkout completo con usuarios, pedidos, stock y pago) → 5 → 6 → 7 → 8 (los tests conviene ir escribiéndolos desde el punto 1).

## Equipo

Proyecto colaborativo — Santiago, Gibran y Mariano (Universidad Anáhuac Cancún).
