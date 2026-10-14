# API HTTP de A-Store

Servidor hecho solo con el JDK (`com.sun.net.httpserver`), sin librerías. Habla JSON y sirve el frontend estático desde `frontend/`.

## Ejecutar

Lo más fácil, desde la raíz del repositorio: `python3 server.py` (compila si hace falta y acepta las mismas opciones de abajo).

A mano, desde `backend/`:

```bash
javac -d out $(find . -name "*.java")
java -cp out Api.ServidorApi
```

Opciones (todas opcionales):

| Opción | Por defecto | Para qué |
|---|---|---|
| `--port` | `8080` | Puerto |
| `--host` | `127.0.0.1` | Interfaz (usa `0.0.0.0` para abrirlo a la red) |
| `--data` | `data` | Carpeta con los `.json` |
| `--front` | `../frontend` | Carpeta del frontend estático |

Al arrancar, `http://127.0.0.1:8080/` sirve `frontend/index.html` y `/api/...` sirve la API.
Si no hay ningún usuario, crea el administrador inicial corriendo una vez la consola (`java -cp out Main`) o registrándote por la API y editando el rol en `data/usuarios.json`.

## Convenciones

- Cuerpos de petición y respuesta en JSON (`Content-Type: application/json`).
- **Autenticación**: `POST /api/login`, `/api/registro` o `/api/invitado` devuelven un `token`. Mándalo en cada petición como `Authorization: Bearer <token>`. Cada token tiene su propio carrito en el servidor y expira tras 12 h sin usarse.
- **Errores**: siempre `{"error": "mensaje"}` con el código HTTP que corresponda.
- **CORS** abierto (`*`), por si el frontend se sirve desde otro origen.
- Un solo hilo de negocio a la vez (cerrojo global): evita corrupción de los `.json` con peticiones simultáneas.

| Código | Cuándo |
|---|---|
| 400 | Datos faltantes o inválidos (JSON mal formado, campo obligatorio, valores fuera de rango) |
| 401 | Falta el token, expiró o las credenciales son incorrectas |
| 402 | Pago rechazado |
| 403 | La cuenta no tiene el permiso o no puede hacer esa acción |
| 404 | El recurso o la ruta no existen |
| 405 | Método no permitido en esa ruta |
| 409 | Conflicto: stock insuficiente, SKU o código repetido, carrito vacío, cupón no válido |
| 500 | Error interno (se registra en la consola del servidor) |

## Endpoints

### Públicos

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/salud` | Estado del servidor, nombre de la tienda y moneda |
| GET | `/api/categorias` | Categorías |
| GET | `/api/productos` | Productos activos. Filtros: `?categoriaId=1`, `?q=texto` (nombre, descripción o SKU) |
| GET | `/api/productos/{id}` | Detalle de un producto activo, con sus variantes |

### Sesión

| Método | Ruta | Cuerpo | Descripción |
|---|---|---|---|
| POST | `/api/registro` | `{nombre, email, password}` | Crea una cuenta de cliente e inicia sesión (201) |
| POST | `/api/login` | `{email, password}` | Inicia sesión |
| POST | `/api/invitado` | — | Sesión de invitado con carrito (201) |
| POST | `/api/logout` | — | Cierra la sesión |
| GET | `/api/yo` | — | Usuario actual con su rol y permisos |

La contraseña pide mínimo 8 caracteres con al menos una letra y un número; el correo debe ser único.

### Carrito y compra (requieren token)

| Método | Ruta | Cuerpo | Descripción |
|---|---|---|---|
| GET | `/api/carrito` | — | Líneas, subtotal, descuento, cupón y total |
| POST | `/api/carrito/items` | `{productoId, varianteId?, cantidad?}` | Agrega (suma si ya está esa variante). `varianteId` es obligatorio si el producto tiene variantes. `cantidad` por defecto 1 |
| PUT | `/api/carrito/items/{indice}` | `{cantidad}` | Cambia la cantidad (0 elimina la línea) |
| DELETE | `/api/carrito/items/{indice}` | — | Elimina una línea |
| DELETE | `/api/carrito` | — | Vacía el carrito |
| POST | `/api/carrito/cupon` | `{codigo}` | Aplica un cupón vigente |
| DELETE | `/api/carrito/cupon` | — | Quita el cupón |
| POST | `/api/checkout` | ver abajo | Paga y crea el pedido (201). Solo cuentas de cliente, incluido invitado |
| GET | `/api/pedidos` | — | Pedidos del cliente registrado |

**Cuerpo de `/api/checkout`** según `metodoPago`:

| `metodoPago` | Campos extra |
|---|---|
| `TARJETA` | `numeroTarjeta` (texto), `mesVencimiento`, `anioVencimiento`, `cvv` |
| `TRANSFERENCIA` | `referenciaBancaria` (6 a 20 letras o números) |
| `PAYPAL` | `cuentaPaypal` (correo) |
| `EFECTIVO` | `montoRecibido` (≥ total). La respuesta incluye `cambio` |

El pago es **simulado**: valida los datos (Luhn, vigencia, formato) pero no hay pasarela real. Si el pago o el stock fallan, el carrito queda intacto.

### Administración

Requieren iniciar sesión con la cuenta que tenga el permiso indicado.

| Método | Ruta | Permiso | Descripción |
|---|---|---|---|
| GET | `/api/admin/productos` | `GESTIONAR_PRODUCTOS` | Todos los productos, incluidos los inactivos |
| GET | `/api/admin/productos/{id}` | `GESTIONAR_PRODUCTOS` | Un producto |
| POST | `/api/admin/productos` | `GESTIONAR_PRODUCTOS` | Crea. Obligatorios: `nombre`, `sku`, `precio`, `stock`, `categoriaId`. Opcionales: `descripcion`, `capacidad`, `dimensiones`, `marca`, `imagen`, `activo`, `variantes` |
| PUT | `/api/admin/productos/{id}` | `GESTIONAR_PRODUCTOS` | Edición parcial: solo cambia los campos enviados. Si envías `variantes`, reemplaza la lista |
| DELETE | `/api/admin/productos/{id}` | `GESTIONAR_PRODUCTOS` | Elimina |
| GET/POST | `/api/admin/cupones` | `GESTIONAR_CUPONES` | Lista / crea (`codigo`, `tipoDescuento` = `PORCENTAJE` o `MONTO_FIJO`, `valor`, `fechaInicio`, `fechaFin` en `AAAA-MM-DD`, `activo?`) |
| PUT/DELETE | `/api/admin/cupones/{id}` | `GESTIONAR_CUPONES` | Edita (parcial) / elimina |
| GET | `/api/admin/usuarios` | `GESTIONAR_USUARIOS` | Usuarios registrados (sin hash ni sal) |
| GET | `/api/admin/pedidos` | `VER_VENTAS` | Todos los pedidos |
| PUT | `/api/admin/pedidos/{id}/estado` | `VER_VENTAS` | `{estado}`: `PENDIENTE`, `PAGADO`, `PREPARANDO`, `LISTO_PARA_RECOGER`, `ENTREGADO` o `CANCELADO`. Cancelar repone el stock; un pedido cancelado no se reactiva |

Variante en `variantes`: `{sku, color?, talla?, modelo?, precioVenta?}` (el precio por defecto es el del producto).

## Ejemplos

```bash
curl -s localhost:8080/api/productos?q=termo

TOKEN=$(curl -s -X POST localhost:8080/api/login \
  -d '{"email":"cliente@correo.com","password":"Clave1234"}' | python3 -c 'import sys,json;print(json.load(sys.stdin)["token"])')

curl -s -X POST localhost:8080/api/carrito/items \
  -H "Authorization: Bearer $TOKEN" -d '{"productoId":1,"cantidad":2}'

curl -s -X POST localhost:8080/api/checkout \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"metodoPago":"PAYPAL","cuentaPaypal":"cliente@correo.com"}'
```

## Estructura del código (`backend/Api/`)

| Clase | Papel |
|---|---|
| `ServidorApi` | Arranque, tabla de rutas, CORS, errores y archivos estáticos |
| `Peticion` | Lee cuerpo JSON, parámetros y token; valida tipos |
| `Controlador*` | Catálogo, autenticación, carrito/checkout y administración |
| `Mapeadores` | Convierte objetos del dominio en JSON |
| `GestorSesiones`, `Sesion` | Tokens, usuario y carrito por sesión |
| `ContextoApi` | Repositorios y servicios compartidos |
