public abstract class reporte {

    protected string titulo;
    protected localdate fechainicio;
    protected localdate fechafin;

    public reporte() {
    }

    public final string generar() {
        return generarcuerpo();
    }

    protected abstract string generarcuerpo();

    public string gettitulo() {
        return titulo;
    }