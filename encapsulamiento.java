class CuentaBancaria {
    private double saldo;

    public void depositar(double cantidad) {
        if (cantidad > 0) {
            saldo += cantidad;
        }
    }

    public double getSaldo() {
        return saldo;
    }
}
