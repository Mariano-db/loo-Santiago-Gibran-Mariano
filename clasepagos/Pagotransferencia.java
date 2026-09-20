public class Pagotransferencia extends Pago implements Estrategiapago {

    private String referenciaBancaria;

    public Pagotransferencia() {
    }

    @Override
    public boolean procesar(double monto) {
        return false;
    }

    @Override
    public String tipo() {
        return "TRANSFERENCIA";
    }

    public String getReferenciaBancaria() {
        return referenciaBancaria;
    }

    public void setReferenciaBancaria(String referenciaBancaria) {
        this.referenciaBancaria = referenciaBancaria;
    }
}
