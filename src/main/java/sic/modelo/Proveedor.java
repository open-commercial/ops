package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"razonSocial", "empresa"})
public class Proveedor implements Serializable {

    private long id_Proveedor;
    private String codigo;
    private String razonSocial;
    private String direccion;
    private CondicionIVA condicionIVA;
    private String idFiscal;
    private String telPrimario;
    private String telSecundario;
    private String contacto;
    private String email;
    private String web;
    private Localidad localidad;
    private Empresa empresa;
    private boolean eliminado;

    @Override
    public String toString() {
        return razonSocial;
    }
}