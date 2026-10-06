package Facturacion;

import java.util.ArrayList;
import java.util.List;

public class DatosFiscales {

    private String rfc;
    private String nombreRazonSocial;
    private String regimenFiscal;
    private String codigoPostal;
    private String usoCFDI;

    public DatosFiscales() {
    }

    public List<String> validar() {
        List<String> errores = new ArrayList<>();

        if (rfc == null || !rfc.toUpperCase().matches("[A-Z&Ñ]{3,4}\\d{6}[A-Z0-9]{3}")) {
            errores.add("El RFC no tiene un formato valido (12 o 13 caracteres).");
        }
        if (nombreRazonSocial == null || nombreRazonSocial.isBlank()) {
            errores.add("El nombre o razon social no puede estar vacio.");
        }
        if (regimenFiscal == null || regimenFiscal.isBlank()) {
            errores.add("El regimen fiscal no puede estar vacio.");
        }
        if (codigoPostal == null || !codigoPostal.matches("\\d{5}")) {
            errores.add("El codigo postal debe tener 5 digitos.");
        }
        if (usoCFDI == null || usoCFDI.isBlank()) {
            errores.add("El uso de CFDI no puede estar vacio.");
        }
        return errores;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc == null ? null : rfc.trim().toUpperCase();
    }

    public String getNombreRazonSocial() {
        return nombreRazonSocial;
    }

    public void setNombreRazonSocial(String nombreRazonSocial) {
        this.nombreRazonSocial = nombreRazonSocial;
    }

    public String getRegimenFiscal() {
        return regimenFiscal;
    }

    public void setRegimenFiscal(String regimenFiscal) {
        this.regimenFiscal = regimenFiscal;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getUsoCFDI() {
        return usoCFDI;
    }

    public void setUsoCFDI(String usoCFDI) {
        this.usoCFDI = usoCFDI;
    }
}
