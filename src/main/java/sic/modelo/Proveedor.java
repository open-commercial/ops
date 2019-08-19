package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"razonSocial", "idSucursal"})
public class Proveedor implements Serializable {

    private long id_Proveedor;
    private String nroProveedor;
    private String razonSocial;
    private CategoriaIVA categoriaIVA;
    private Long idFiscal;
    private String telPrimario;
    private String telSecundario;
    private String contacto;
    private String email;
    private String web;
    private Ubicacion ubicacion;
    private Long idSucursal;
    private String nombreSucursal;
    private boolean eliminado;

    @Override
    public String toString() {
        return razonSocial;
    }
}