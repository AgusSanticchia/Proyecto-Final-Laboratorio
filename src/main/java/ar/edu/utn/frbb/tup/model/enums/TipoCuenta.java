package ar.edu.utn.frbb.tup.model.enums;

public enum TipoCuenta {

    CUENTA_CORRIENTE_PESOS("CC$"),
    CAJA_AHORRO_PESOS("CA$"),
    CAJA_AHORRO_DOLAR_US("CAU$S");

    private final String descripcion;

    TipoCuenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static TipoCuenta fromString(String text) {
        for (TipoCuenta tipo : TipoCuenta.values()) {
            if (tipo.descripcion.equalsIgnoreCase(text)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("No se pudo encontrar un TipoCuenta con la descripción: " + text);
    }
}
