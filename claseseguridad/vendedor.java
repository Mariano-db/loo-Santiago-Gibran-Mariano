public class vendedor extends usuario {

    private string turno;

    public vendedor() {
    }

    public venta registrarventa() {
        return null;
    }

    public list<pedido> consultarpedidos() {
        return new arraylist<>();
    }

    public cliente buscarcliente() {
        return null;
    }

    public string getturno() {
        return turno;
    }

    public void setturno(string turno) {
        this.turno = turno;
    }
}