package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "razonSocial")
public class Proveedor implements Serializable {

    private long idProveedor;
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
    private boolean eliminado;
    private BigDecimal saldoCuentaCorriente;
    private Date fechaUltimoMovimiento;

    @Override
    public String toString() {
        return razonSocial;
    }
}