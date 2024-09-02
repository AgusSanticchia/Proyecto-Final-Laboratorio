package ar.edu.utn.frbb.tup.controller.dto;

public class ClienteDto extends PersonaDto {
    private String tipoPersona;
    private String banco;

    public ClienteDto(String nombre, String apellido, long dni, String fechaNacimiento, String direccion, String telefono, String banco, String tipoPersona, int edad) {
        super(nombre, apellido, dni, fechaNacimiento, direccion, telefono, edad);
        this.banco = banco;
        this.tipoPersona = tipoPersona;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public String getBanco() {
        return banco;
    }
}
