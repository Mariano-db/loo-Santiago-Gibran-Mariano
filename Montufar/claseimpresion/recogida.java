public class recogida {

    private long id;
    private localdatetime fechalimite;
    private localdatetime fechaentrega;
    private estadorecogida estado;

    public recogida() {
    }

    public void registrarentrega() {
        this.estado = estadorecogida.entregada;
    }

    public boolean estavigente() {
        return false;
    }

    public long getid() {
        return id;
    }

    public void setid(long id) {
        this.id = id;
    }

    public localdatetime getfechalimite() {
        return fechalimite;
    }

    public void setfechalimite(localdatetime fechalimite) {
        this.fechalimite = fechalimite;
    }

    public localdatetime getfechaentrega() {
        return fechaentrega;
    }

    public void setfechaentrega(localdatetime fechaentrega) {
        this.fechaentrega = fechaentrega;
    }

    public estadorecogida getestado() {
        return estado;
    }

    public void setestado(estadorecogida estado) {
        this.estado = estado;
    }
}