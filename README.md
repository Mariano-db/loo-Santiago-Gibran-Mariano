# A-Store

Sistema de tienda en línea de merch de la **Universidad Anáhuac Cancún**, hecho en Java y usado desde la consola. Proyecto escolar para la materia de Lenguaje Orientado a Objetos (LOO).

## Qué hace

- **Cliente**: ver el catálogo (con variantes de color/talla y existencias), armar un carrito, aplicar cupones, pagar (tarjeta, PayPal, transferencia o efectivo), ver su historial de pedidos con el estado de la recogida y las devoluciones, pedir facturas y solicitar impresiones.
- **Administrador**: administrar productos, categorías, cupones, pedidos, usuarios, facturas, proveedores y órdenes de compra, impresiones y recogidas, devoluciones, inventario y reportes.
- **Invitado**: puede ver el catálogo y llenar un carrito, pero para pagar tiene que iniciar sesión o registrarse.

### Por qué solo hay dos roles

La tienda la dirige **una sola persona**, así que no existe un rol de "vendedor" separado: todo lo que haría un vendedor (ver pedidos, entregar, devolver, atender impresiones) lo hace el **Administrador**. Los roles son `Administrador` y `Cliente`.

## Cómo compilar, ejecutar y probar

Desde la raíz del proyecto (las rutas a `data/` son relativas, así que hay que ejecutar desde aquí):

```bash
javac -d out $(find . -name "*.java")
java -cp out Main
```

**Cuentas de prueba**: `data/CREDENCIALES_PRUEBA.txt` tiene un administrador y un cliente. Es solo para desarrollo: cámbialo o bórralo cuando haya credenciales reales.

## Estructura del proyecto

Cada carpeta es un paquete de Java (el nombre del paquete es el nombre de la carpeta tal cual). `Main.java`, en la raíz, es el punto de entrada.

| Paquete | Contenido |
|---|---|
| `Catalogo` | Producto, Categoria, EstadoProducto, VarianteProducto, Promocion |
| `clasecarrostotal` | CarritoCompra, ItemCarrito, EstadoCarrito |
| `clasedescuentos` | Cupon, estrategias de descuento (porcentaje, monto fijo) y la jerarquía `ReglaDescuento` (ver [clases sin uso](#clases-del-diseño-original-que-todavía-no-se-usan)) |
| `clasepagos` | Pago y sus variantes (tarjeta, PayPal, efectivo, transferencia) |
| `claseprovedores` | Proveedor, Suministro, OrdenCompra, DetalleOrdenCompra |
| `claseimpresion` | SolicitudImpresion, Recogida |
| `clasereportes` | Reporte y sus variantes (ventas, inventario, clientes), GeneradorReportes |
| `claseseguridad` | Usuario, Administrador, Cliente, Contrasenas (hash), Rol, Permiso |
| `Exepciones` | Excepciones propias (stock insuficiente, carrito vacío, pago inválido, acceso denegado...) |
| `Facturacion` | Factura y DatosFiscales |
| `Inventario` | Existencia, MovimientoInventario, ObservadorStock / NotificadorStockBajo |
| `Ventas` | Pedido, DetallePedido, EstadoPedido, Devolucion |
| `Servicios` | Repositorios JSON, servicios de negocio y `Almacen` |
| `Panel` | Todo lo que interactúa con la consola: menús del cliente y del administrador |
| `Json` | Lector y escritor de JSON propios |
| `Utils` | Configuración general (nombre de la tienda, moneda, IVA) |

### Cómo está organizado el código

Hay tres capas, y cada una solo le habla a la de abajo:

1. **Menús (`Panel/`)**: leen lo que escribe la persona y muestran resultados. No tienen reglas de negocio. `Main` solo hace el ciclo "acceso → menú según el rol". `MenuCliente` es la tienda, `ProcesoCompra` son los pasos de "Finalizar compra", `PanelAdministrador` reparte a un panel por tema (`PanelProductos`, `PanelCupones`, `PanelPedidos`...). `Entrada` es el único lugar que lee del teclado.
2. **Servicios (`Servicios/Servicio*.java`)**: las reglas (validar un pago, descontar stock, prorratear una devolución...). Lanzan excepciones con mensajes legibles que los menús muestran tal cual.
3. **Repositorios (`Servicios/Repositorio*Json.java`)**: guardan y leen objetos en `data/`.

`Servicios/Almacen` crea todos los repositorios y servicios una sola vez y los entrega junto a los menús, para no pasar diez parámetros a cada método.

## Persistencia en JSON

No se usa ninguna librería externa. Todo vive en `data/` como arreglos JSON:

| Archivo | Contenido |
|---|---|
| `categorias.json`, `productos.json` | Catálogo (los productos de ropa traen sus variantes) |
| `usuarios.json` | Cuentas (contraseña guardada como hash con sal) |
| `pedidos.json`, `recogidas.json`, `devoluciones.json` | Ventas |
| `cupones.json` | Cupones de descuento (`BIENVENIDO10` es de prueba) |
| `existencias.json`, `movimientos_inventario.json` | Stock real y su historial |
| `facturas.json` | Facturas |
| `proveedores.json`, `ordenes_compra.json` | Compras |
| `impresiones.json` | Solicitudes de impresión |

Los archivos que no existen se crean al usarse por primera vez. Precios, stock y SKUs de `productos.json` son valores iniciales definidos por el equipo: se pueden editar sin tocar el código (el stock, mejor desde el panel de Inventario).

## Flujos importantes

**Finalizar compra** (`Panel/ProcesoCompra`), en este orden a propósito para no cobrar de más:
1. El carrito no está vacío. 2. Hay stock de cada línea. 3. El cupón sigue siendo válido (se relee del archivo). 4. Se cobra con el método elegido. 5. Se crea y guarda el pedido ya como `PAGADO`. 6. Se descuenta el stock y se crea la recogida (7 días de plazo).
Si el pago se rechaza o se cancela, no se guarda nada y el carrito queda igual.

**Inventario**: el stock se lleva por producto o, si tiene variantes, **por cada color/talla**. Entra con órdenes de compra recibidas y devoluciones reingresadas; sale con ventas; cancelar un pedido pagado que no se ha entregado lo repone. Cada cambio queda en `movimientos_inventario.json`.

**Reportes**: ventas (brutas, descuentos, devoluciones, netas, por método de pago, top de productos), inventario (valor y stock bajo) y clientes. Se pueden guardar como `.txt` en `reportes/`.

## Partes difíciles de entender (y por qué están así)

Estas son las partes del código que no son obvias a primera vista. El código no lleva comentarios; esta sección es la explicación.

- **`Json/JsonLector` y `JsonEscritor`**: un parser de JSON hecho a mano (recursivo: un objeto o arreglo vuelve a llamar al lector para cada uno de sus elementos). Existe porque el proyecto no usa librerías externas. Solo soporta lo que necesitamos: objetos, arreglos, strings con escapes, números, booleanos y `null`.
- **`Servicios/RepositorioJsonBase<T>`**: una clase base con el código común de todos los repositorios (leer el archivo completo, agregar o quitar un elemento, escribirlo de nuevo, asignar el siguiente id). Cada repositorio concreto solo dice **cómo convertir su objeto a un mapa y de vuelta** (`aMapa` / `deMapa`). Leer y escribir el archivo completo en cada operación sería lento con muchos datos, pero es simple y suficiente a esta escala.
- **`claseseguridad/Contrasenas`**: la contraseña nunca se guarda tal cual. Se guarda `sal:hash` (ambos en Base64): la **sal** es un texto aleatorio distinto por usuario, y el **hash** es el SHA-256 de sal + contraseña. Así dos usuarios con la misma contraseña tienen hashes distintos. Para verificar se repite el cálculo con la sal guardada y se compara.
- **Los pedidos guardan una copia de los datos del producto** (`RepositorioPedidoJson`): nombre, SKU, color, talla y precio de ese momento. Así el historial no cambia si luego se edita o elimina el producto del catálogo.
- **IVA en las facturas** (`Facturacion/Factura.calcularMontos`): los precios de la tienda **ya incluyen IVA**, entonces el subtotal se obtiene dividiendo el total entre `1 + tasa` y el IVA es la diferencia. Es un comprobante interno, **no un CFDI timbrado**.
- **Pagos simulados** (`clasepagos/`): validan los datos (tarjeta con el algoritmo de Luhn, vencimiento y CVV; correo de PayPal; referencia bancaria) pero **no se conectan a ningún banco**. El número de tarjeta y el CVV se borran de memoria apenas se procesa el pago y nunca se guardan; solo queda el número enmascarado y el código de autorización.
- **Reembolso de devoluciones** (`ServicioDevoluciones`): si el pedido tuvo descuento, el reembolso se **prorratea** (precio × cantidad × total pagado ÷ suma de las líneas) para no devolver más de lo que se pagó.
- **`Producto.stock` vs `existencias.json`**: el stock de verdad está en las existencias (una por producto o por variante). `Producto.stock` se mantiene como la **suma** de ellas (`ServiciosInventario.sincronizarProducto`) solo para poder mostrarlo sin consultar el inventario. En la primera ejecución, las existencias se generan repartiendo el stock que traía `productos.json` entre las variantes.
- **Validar y luego descontar el stock**: `validarDisponibilidad` revisa todas las líneas antes de tocar nada, y `confirmarSalida` la vuelve a llamar. Así un pedido se descuenta completo o no se descuenta.
- **Estado `VENCIDA` de una recogida** (`Recogida.estadoActual`): no se guarda; se calcula al consultar (si no se entregó y ya pasó la fecha límite, está vencida).
- **Cupón y descuentos** (`CarritoCompra.calcularDescuento`): si hay un cupón aplicado, manda el cupón; si no, la estrategia de descuento del carrito. El cupón se vuelve a validar cada vez que cambia el total (por la compra mínima) y otra vez al pagar.
- **`Entrada.texto` termina el programa** si se cierra la entrada (Ctrl+D). Es a propósito: sin más datos que leer no hay nada útil que hacer, y evita un error feo.
- **Observer de stock bajo** (`NotificadorStockBajo`): junta alertas mientras el programa corre y el panel de administrador las muestra al abrirse. Las alertas **no se guardan**: se pierden al cerrar el programa; el reporte de inventario siempre muestra lo que está bajo el mínimo.
- **Expresiones regulares**: el RFC (`DatosFiscales`) es 3 o 4 letras + 6 dígitos de fecha + 3 de homoclave; el correo es `algo@dominio.ext`; el usuario, de 3 a 20 letras, números o `. _ -`.

## Patrones de diseño utilizados

- **Strategy**: métodos de pago (`Pago` y sus variantes, con `EstrategiaPago`) y descuentos (`EstrategiaDescuento`).
- **Observer**: aviso de stock bajo (`ObservadorStock` → `NotificadorStockBajo`).
- **Repository**: `IRepositorio<T, ID>` y los repositorios JSON.
- **Template Method**: `Reporte.generar()` arma el encabezado y delega el cuerpo; `RepositorioJsonBase` define los pasos y las subclases aportan la conversión.
- **Singleton**: `ConfiguracionSistema`.
- **Estados como enum**: pedido, pago, factura, recogida, impresión, orden de compra, carrito.

## Clases del diseño original que todavía no se usan

Se conservan porque forman parte del diseño orientado a objetos del proyecto, pero **no tienen lógica funcionando todavía**:

- `ReglaPorCantidad`, `ReglaPorFecha`, `ReglaPorProducto`, `ReglaPorRol`: reglas de descuento automáticas. Al llamarlas lanzan `UnsupportedOperationException`. Su método `aplica()` no recibe datos del carrito, así que habría que rediseñarlo antes de implementarlas. Hoy los descuentos se hacen solo con cupones. (`ReglaPorCupon` sí funciona, pero nada la usa.)
- `ReglaDescuento` (base), `DescuentoAplicado`, `SinDescuento`: sin uso.
- `TipoReporte`: sin uso.
- `Rol` y `Permiso`: `Usuario.tienePermiso` y `verificarPermiso` funcionan, pero nadie asigna permisos todavía; el panel de administrador se protege comprobando que el usuario sea `Administrador`.
- `Promocion`: calcula su descuento, pero no está conectada al carrito.
- `Cliente.puntosFidelidad`: se guarda, pero no se usa.

## Pendientes

- Alta y edición de **variantes** (color/talla) desde el panel de administrador (hoy se editan en `data/productos.json`).
- Reservas temporales de stock mientras alguien paga.
- Descuentos automáticos (reglas `ReglaPor*`) y promociones conectadas al carrito.
- Facturas como CFDI real (hoy son comprobantes internos) y pagos con una pasarela real.

## Tecnologías

- Java 17 o superior, sin dependencias externas (probado con Java 25).
- Persistencia en archivos JSON con un parser propio.

## Equipo

Proyecto colaborativo — Montufar, Santiago, Gibran y Mariano (Universidad Anáhuac Cancún).
