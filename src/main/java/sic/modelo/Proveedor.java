package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"razonSocial", "empresa"})
public class Proveedor implements Serializable {

    private long id_Proveedor;
    private String codigo;
    private String razonSocial;
    private String direccion;
    private CategoriaIVA categoriaIVA;
    private Long idFiscal;
    private String telPrimario;
    private String telSecundario;
    private String contacto;
    private String email;
    private String web;
    private Localidad localidad;
    private Empresa empresa;
    private boolean eliminado;
    private BigDecimal saldoCuentaCorriente; 
    private Date fechaUltimoMovimiento;

    @Override
    public String toString() {
        return razonSocial;
    }
}