# A-Store

Tienda en línea de merch de la **Universidad Anáhuac Cancún**. Proyecto escolar para la materia de Lenguaje Orientado a Objetos (LOO).

## Estructura del repositorio

```
.
├── server.py   Lanzador: compila y levanta el servidor con un solo comando
├── backend/    Java puro (sin dependencias): lógica de negocio, persistencia en JSON y API HTTP
├── frontend/   Página web en HTML, CSS y JavaScript plano (sin build ni Node)
└── README.md
```

| Carpeta | Qué contiene | Documentación |
|---|---|---|
| [`backend/`](backend/) | Catálogo, carrito, checkout, usuarios, roles y permisos, cupones y panel de administrador (consola), más la API HTTP. Datos en `backend/data/*.json` | [backend/README.md](backend/README.md), [backend/API.md](backend/API.md) |
| [`frontend/`](frontend/) | Aún vacío. Aquí irá la web que consumirá la API del backend | — |

## Cómo ejecutar el backend (modo consola)

```bash
cd backend
javac -d out $(find . -name "*.java")
java -cp out Main
```

Hay que ejecutarlo desde `backend/`, porque lee `data/*.json` con rutas relativas.

## Ejecutar la API HTTP

Desde la raíz del proyecto:

```bash
python3 server.py
```

Compila el backend si hace falta (la primera vez y cada vez que cambies código Java) y lo levanta en `http://127.0.0.1:8080`. Para detenerlo: `Ctrl+C`.

Opciones: `--port 9000`, `--host 0.0.0.0` (abrirlo a la red), `--data <carpeta>`. Requiere Java (JDK 17 o superior) y Python 3.

Sin Python, a mano: `cd backend && javac -d out $(find . -name "*.java") && java -cp out Api.ServidorApi`.

Documentación completa de endpoints en [backend/API.md](backend/API.md).

## Hoja de ruta

1. **Reorganizar el repositorio** en `backend/` y `frontend/` (hecho).
2. Cerrar lo que la web necesita: pedidos persistidos, stock real, cupones en el checkout y pago simulado (hecho).
3. **API HTTP** en el backend (paquete `Api/`, servidor HTTP del JDK, sin librerías) (hecho).
4. Construir el **frontend** en HTML, CSS y JS plano que consuma la API.

El detalle de cada pendiente está en [backend/README.md](backend/README.md#qué-le-falta-y-cómo-mejorarlo).

## Equipo

Proyecto colaborativo — Santiago, Gibran, Montufar y Mariano (Universidad Anáhuac Cancún).
