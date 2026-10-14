# A-Store — Backend

Esta carpeta es el backend del proyecto (ver el [README raíz](../README.md)). Todos los comandos y rutas de este documento son relativos a `backend/`.

Sistema de tienda en línea de merch de la **Universidad Anáhuac Cancún**. Proyecto escolar para la materia de Lenguaje Orientado a Objetos (LOO).

## Descripción

A-Store modela el dominio de una tienda en línea: catálogo de productos con variantes (talla/color), carrito de compras, usuarios con roles y permisos, cupones, panel de administrador y, como esqueleto pendiente de implementar, pagos, inventario con reservas, facturación, órdenes de compra a proveedores, impresión/recogida de pedidos y reportes.

## Estado del proyecto

El código **compila sin errores** (95 archivos `.java`). Cada carpeta es un paquete Java real y el punto de entrada es `Main.java`, un menú de consola.

**Ya funciona de punta a punta:**

- **API HTTP** con todo lo de abajo expuesto como JSON, más administración de pedidos (cambiar estado; cancelar repone stock). Ver [API.md](API.md).
- Catálogo de productos y categorías persistido en JSON.
- Usuarios: registro, inicio de sesión, invitado, cierre de sesión, roles y permisos.
- Carrito de compras (con variantes color/talla), con validación de stock.
- Checkout completo: aplicar cupón, elegir método de pago (simulado), descontar stock y guardar el pedido en `data/pedidos.json`.
- Historial de compras del cliente ("Mis pedidos").
- Panel de administrador: CRUD de productos, cambio de precio y stock, activar/desactivar y gestión de cupones.

**Sigue siendo esqueleto a propósito** (retorna valores por defecto: `0`, `false`, `null`, listas vacías): inventario por variante y reservas (`Inventario/`), `ServicioVentas`, facturación, reportes, proveedores, impresión/recogida y devoluciones. Ver [Pendientes](#qué-le-falta-y-cómo-mejorarlo).

No hay datos ficticios en el código: el catálogo en `data/productos.json` es el catálogo real de la tienda.

## Estructura del proyecto

Cada carpeta es un paquete de Java (el nombre del paquete es el nombre de la carpeta tal cual):

| Paquete | Contenido |
|---|---|
| `Catalogo` | Producto, Categoria, EstadoProducto, VarianteProducto, Promocion |
| `clasecarrostotal` | CarritoCompra, ItemCarrito, DetalleCarrito, EstadoCarrito |
| `clasedescuentos` | Cupon, TipoDescuento, ReglaDescuento y sus variantes (cantidad, fecha, producto, rol, cupón), EstrategiaDescuento (SinDescuento, DescuentoMontoFijo), DescuentoAplicado |
| `clasepagos` | Pago y sus variantes (tarjeta, PayPal, efectivo, transferencia), EstrategiaPago |
| `claseprovedores` | Proveedor, OrdenCompra, DetalleOrdenCompra, Suministro |
| `claseimpresion` | SolicitudImpresion, Recogida |
| `clasereportes` | Reporte y sus variantes (ventas, inventario, clientes), GeneradorReportes |
| `claseseguridad` | Usuario y sus roles (Administrador, Vendedor, Cliente), Rol, Permiso, TipoCliente |
| `Exepciones` | Excepciones propias del dominio (acceso denegado, stock insuficiente, carrito vacío, pago inválido, producto no encontrado) |
| `Facturacion` | Factura y DatosFiscales |
| `Inventario` | Inventario, Existencia, Reserva, MovimientoInventario, ObservadorStock |
| `Servicios` | Servicios (`ServicioClientes`, `ServicioPedidos` para el checkout, `ServicioPagos`), repositorio genérico (`IRepositorio`) y los repositorios JSON (categorías, productos, usuarios, cupones y pedidos) |
| `Ventas` | Orden, Pedido y sus detalles, Devolucion |
| `Api` | API HTTP sobre el JDK (servidor, controladores, sesiones). Ver [API.md](API.md) |
| `Utils` | `ConfiguracionSistema` (singleton con nombre de tienda, moneda e IVA) y `HashUtil` (hash de contraseñas) |
| `Json` | Parser y escritor de JSON propios, sin dependencias externas (`JsonLector`, `JsonEscritor`) |

En `backend/`: `Main.java` (punto de entrada de consola, menú del cliente), `MenuAdministrador.java` (panel de administrador) y `API.md` (documentación de la API HTTP). El punto de entrada web es `Api.ServidorApi`.

## Flujo de la aplicación

1. Si `data/usuarios.json` no tiene usuarios, `Main` pide crear la **cuenta del administrador inicial**.
2. Pantalla de inicio: iniciar sesión, registrarse, seguir como invitado o salir.
3. Menú principal:

| Opción | Acción |
|---|---|
| 1 | Ver catálogo (solo productos activos, con stock) |
| 2 | Agregar producto al carrito (elige color/talla si tiene variantes; valida stock) |
| 3 | Ver carrito: subtotal, cupón y total |
| 4 | Eliminar producto del carrito |
| 5 | Aplicar (o quitar) un cupón de descuento |
| 6 | Finalizar compra: elegir método de pago, cobrar, descontar stock y guardar el pedido |
| 7 | Mis pedidos (solo clientes registrados) |
| 8 | Cerrar sesión (vuelve a la pantalla de inicio) |
| 9 | Ver usuarios registrados (solo con permiso `GESTIONAR_USUARIOS`) |
| 10 | Panel de administrador (solo con permiso `GESTIONAR_PRODUCTOS`) |
| 0 | Salir |

4. Solo las cuentas de **cliente** (registrado o invitado) pueden comprar; administrador y vendedor reciben un aviso.
5. Panel de administrador: listar, agregar, editar, cambiar precio, cambiar stock, activar/desactivar y eliminar productos, y gestionar cupones (requiere `GESTIONAR_CUPONES`).

## Usuarios, roles y permisos

- `Usuario` es abstracta; sus subclases son `Cliente` (registrado o invitado), `Vendedor` y `Administrador`.
- Las contraseñas **nunca se guardan en claro**: `HashUtil` genera una sal y guarda el hash en `data/usuarios.json` (`passwordHash` + `sal`).
- Al registrarse se valida: nombre no vacío, correo con formato válido y único, y contraseña de mínimo 8 caracteres con al menos una letra y un número.
- `Usuario.verificarPermiso` lanza `AccesoDenegadoException` si no hay sesión activa o falta el permiso.
- Permisos disponibles (`Permiso`): `GESTIONAR_USUARIOS`, `GESTIONAR_PRODUCTOS`, `GESTIONAR_CUPONES`, `VER_VENTAS`.
- `Rol.crearPorNombre` asigna permisos por rol: `ADMINISTRADOR` tiene los cuatro, `VENDEDOR` solo `VER_VENTAS` y `CLIENTE` ninguno. Un usuario también puede tener permisos individuales guardados en su JSON.
- Para tener un administrador, borra el contenido de `data/usuarios.json` (dejar `[]`) y arranca el programa: pedirá crear el administrador inicial.

## Persistencia en JSON

No se usa ninguna librería externa: `Json/JsonLector.java` y `Json/JsonEscritor.java` son un parser y un escritor de JSON hechos a mano (objetos, arreglos, strings con escapes, números y booleanos), suficientes para las necesidades del proyecto.

Sobre eso, los repositorios de `Servicios/` implementan `IRepositorio<T, ID>` leyendo y escribiendo:

| Archivo | Repositorio | Contenido |
|---|---|---|
| `data/categorias.json` | `RepositorioCategoriaJson` | 5 categorías: Ropa, Termos, Papelería, Accesorios y Peluches |
| `data/productos.json` | `RepositorioProductoJson` | Catálogo real de A-Store, con variantes por color y talla en la ropa |
| `data/usuarios.json` | `RepositorioUsuarioJson` | Usuarios con rol, hash de contraseña, sal y permisos |
| `data/cupones.json` | `RepositorioCuponJson` | Cupones de descuento (porcentaje o monto fijo, con vigencia) |
| `data/pedidos.json` | `RepositorioPedidoJson` | Pedidos con sus líneas, cupón, descuento, método y referencia de pago. Se crea al primer pedido |

Precios, stock y SKUs de los productos son valores de partida; edítalos libremente en los `.json` cuando tengas los reales, sin tocar código Java. Cada `Producto` referencia su categoría por `categoriaId`, y el repositorio la resuelve al cargar.

## Carrito, checkout y pagos

`clasecarrostotal/CarritoCompra.java`:

- `agregarProducto(producto, variante, cantidad)` — suma la cantidad si el producto ya está con la misma variante y lanza `StockInsuficienteException` si, entre todas las variantes del mismo producto, se pasa del stock.
- `eliminarProducto(indice)` / `actualizarCantidad(indice, cantidad)`.
- `aplicarCupon(cupon)` / `quitarCupon()` — `Cupon` implementa `EstrategiaDescuento`; solo se acepta si `esValido()`.
- `calcularTotal()` / `totalConDescuento()`.
- `convertirAPedido()` — lanza `CarritoVacioException` si está vacío y copia cupón y descuento al `Pedido`.

`Servicios/ServicioPedidos.finalizarCompra(carrito, estrategiaPago)` hace, en orden:

1. Revisa que el cupón siga vigente.
2. Vuelve a leer el stock **actual** del catálogo y valida cada producto (existe, está activo y alcanza).
3. Cobra con `ServicioPagos` (lanza `PagoInvalidoException` si se rechaza). Si falla algo hasta aquí, no cambia nada.
4. Convierte el carrito en `Pedido` con estado `PAGADO`.
5. Descuenta el stock y guarda el pedido en `data/pedidos.json`.

**Stock**: se maneja por **producto** (`Producto.stock`), no por variante. Las variantes de talla/color comparten el stock del producto base. El stock por variante queda pendiente.

**Pagos (simulados, no hay pasarela real):**

| Método | Qué valida |
|---|---|
| `PagoTarjeta` | Número con algoritmo de Luhn, vencimiento vigente y CVV de 3 o 4 dígitos. Solo se guarda el número enmascarado y un código de autorización; el número y el CVV se descartan |
| `PagoTransferencia` | Referencia bancaria de 6 a 20 letras o números |
| `PagoPaypal` | Correo con formato válido |
| `PagoEfectivo` | Monto recibido mayor o igual al total; calcula el cambio |

Los precios del catálogo se tratan como **precio final**: no se suma IVA aparte al total.

## Patrones de diseño utilizados

- **Strategy**: métodos de pago (`EstrategiaPago`) y descuentos (`EstrategiaDescuento`, que `Cupon` implementa).
- **Observer**: notificación de stock bajo (`ObservadorStock` → `NotificadorStockBajo`), aún sin activar.
- **Repository**: interfaz genérica `IRepositorio<T, ID>` con implementaciones en JSON.
- **Singleton**: `ConfiguracionSistema`.
- **Estados como enum**: `EstadoPago`, `EstadoOrden`, `EstadoPedido`, `EstadoCarrito`, `EstadoFactura`, `EstadoOrdenCompra`, `EstadoReserva`, `EstadoRecogida`.

## Tecnologías

- Java (sin dependencias externas)
- Persistencia: archivos JSON en `data/`, leídos y escritos con un parser propio

## Cómo compilar y ejecutar

Desde la raíz del repositorio:

```bash
cd backend
javac -d out $(find . -name "*.java")
java -cp out Main
```

El programa lee los archivos de `data/` con rutas relativas, así que hay que ejecutarlo desde `backend/` (si usas VS Code, abre esa carpeta o configura el directorio de trabajo).

## Qué le falta y cómo mejorarlo

Ordenado por prioridad. Cada punto indica **qué hacer** y **qué archivos/paquetes tocar**.

### 1. Stock por variante e inventario real
Hoy el stock es un total por producto; las variantes no tienen el suyo.
- Conectar `Inventario/Existencia` (ya tiene `varianteProducto`, `stockFisico`, `stockReservado`) y `Inventario/Inventario`.
- Implementar `Servicios/ServiciosInventario` (hoy devuelve `0`/vacío), `Reserva` y `MovimientoInventario`; persistir en `data/existencias.json`.
- Activar el Observer: `Inventario/NotificadorStockBajo` cuando baje del mínimo.
- Evitar la condición de carrera entre validar y descontar stock (hoy son lecturas y escrituras de archivo separadas).

### 2. Pedidos: ciclo de vida
Los pedidos se crean como `PAGADO`. Cambiar su estado y cancelar (con reposición de stock) ya existe en la API (`PUT /api/admin/pedidos/{id}/estado`).
- Agregar esa misma opción al panel de administrador de consola, o dejarla solo en la web.
- Decidir si se mantiene `Ventas/Orden`/`DetalleOrden` o se eliminan, porque `Pedido` ya cumple ese papel.

### 3. Pagos reales o más realistas
- Distinguir tarjeta de crédito/débito (`TipoTarjeta` no se pide hoy).
- Que el efectivo quede `PENDIENTE` hasta recogerlo, en vez de `PAGADO` de inmediato.
- Implementar `Pago`/`DetallePago` como registro persistido del cobro.

### 4. Cupones
- Límite de usos por cupón y/o por cliente, y monto mínimo de compra.
- Implementar `aplicar`/`esAplicable` en `clasedescuentos/Regla*` si se quieren reglas por cantidad, fecha, producto o rol.

### 5. Paquetes que siguen vacíos de lógica
- `Facturacion` (`Factura`, `DatosFiscales`): generar factura a partir de un pedido pagado, con IVA de `Utils/ConfiguracionSistema`.
- `clasereportes` (`ReporteVentas`, `ReporteInventario`, `ReporteClientes`, `GeneradorReportes`): leer pedidos/existencias/usuarios persistidos (depende de 1 y 2).
- `claseprovedores` (`OrdenCompra`, `Proveedor`, `Suministro`): reabastecer inventario desde una orden de compra.
- `claseimpresion` (`SolicitudImpresion`, `Recogida`): estados de impresión y recogida del pedido.
- `Ventas/Devolucion`: devoluciones que reingresen stock.
- `Servicios/ServicioVentas`: hoy `confirmarVenta` devuelve `null`.

### 6. Calidad del código
- **Validación en setters**: precio y stock no negativos (`Catalogo/Producto`, etc.).
- **Tests unitarios**: no hay ninguno. Decidir si se usa JUnit con `.jar` manual en `lib/` o pruebas propias sin dependencias.
- **Seguridad del login**: no hay límite de intentos fallidos y la contraseña se muestra al teclearla en la consola.
- **Duplicidad de stock**: existe en `Producto.stock` y en `Inventario/Existencia`, sin conexión entre ambos.
- **`Usuario.actualizarPerfil()`** sigue vacío.
- **Persistencia**: los repositorios JSON leen/escriben el archivo completo en cada operación; está bien a esta escala, pero habría que cachear si crece.

### Orden sugerido
Antes del frontend: API HTTP (ver el [README raíz](../README.md)). Después: 1 → 2 → 3 → 4 → 5 → 6; los tests conviene ir escribiéndolos desde ya.

## Equipo

Proyecto colaborativo — Santiago, Gibran, Montufar y Mariano (Universidad Anáhuac Cancún).
