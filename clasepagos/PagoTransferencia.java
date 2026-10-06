package clasepagos;

public class PagoTransferencia extends Pago {

    private String referenciaBancaria;

    public PagoTransferencia() {
    }

    @Override
    public boolean procesar(double monto) {
        if (referenciaBancaria == null || !referenciaBancaria.matches("[A-Za-z0-9]{6,30}")) {
            setMotivoRechazo("La referencia bancaria debe tener de 6 a 30 letras o numeros.");
            return false;
        }
        return true;
    }

    @Override
    public String tipo() {
        return "TRANSFERENCIA";
    }

    @Override
    public String getReferencia() {
        return referenciaBancaria;
    }

    public String getReferenciaBancaria() {
        return referenciaBancaria;
    }

    public void setReferenciaBancaria(String referenciaBancaria) {
        this.referenciaBancaria = referenciaBancaria;
    }
}
