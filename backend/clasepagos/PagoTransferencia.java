package clasepagos;

public class PagoTransferencia extends Pago implements EstrategiaPago {

    private String referenciaBancaria;

    public PagoTransferencia() {
    }

    @Override
    public boolean procesar(double monto) {
        return monto > 0 && referenciaBancaria != null && referenciaBancaria.trim().matches("[A-Za-z0-9]{6,20}");
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
