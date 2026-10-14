#!/usr/bin/env python3
"""Levanta el servidor de A-Store: compila el backend si hace falta y lo ejecuta.

Uso (desde la raiz del proyecto):

    python3 server.py                      # http://127.0.0.1:8080
    python3 server.py --port 9000          # otro puerto
    python3 server.py --host 0.0.0.0       # accesible desde la red
    python3 server.py --data /ruta/datos   # otra carpeta de datos

Todos los argumentos se pasan tal cual a Api.ServidorApi.
"""
import shutil
import subprocess
import sys
from pathlib import Path

RAIZ = Path(__file__).resolve().parent
BACKEND = RAIZ / "backend"
SALIDA = BACKEND / "out"


def necesita_compilar(fuentes):
    if not SALIDA.is_dir():
        return True
    clases = [p.stat().st_mtime for p in SALIDA.rglob("*.class")]
    if not clases:
        return True
    return max(p.stat().st_mtime for p in fuentes) > min(clases)


def main():
    if shutil.which("javac") is None or shutil.which("java") is None:
        sys.exit("No se encontro Java (javac/java). Instala un JDK 17 o superior.")

    fuentes = sorted(BACKEND.rglob("*.java"))
    fuentes = [p for p in fuentes if SALIDA not in p.parents]
    if not fuentes:
        sys.exit("No se encontraron archivos .java en backend/")

    if necesita_compilar(fuentes):
        print("Compilando backend...")
        if SALIDA.exists():
            shutil.rmtree(SALIDA)
        comando = ["javac", "-d", "out"] + [str(p.relative_to(BACKEND)) for p in fuentes]
        if subprocess.run(comando, cwd=BACKEND).returncode != 0:
            sys.exit("La compilacion fallo.")

    # Se ejecuta desde backend/ porque los datos se leen con rutas relativas (data/).
    try:
        sys.exit(subprocess.run(["java", "-cp", "out", "Api.ServidorApi"] + sys.argv[1:], cwd=BACKEND).returncode)
    except KeyboardInterrupt:
        print("\nServidor detenido.")


if __name__ == "__main__":
    main()
