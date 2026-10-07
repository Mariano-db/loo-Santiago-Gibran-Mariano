package clasepagos;

public class PagoTransferencia extends Pago implements EstrategiaPago {

    private String referenciaBancaria;

    public PagoTransferencia() {
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
