package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombreFiscal", "idFiscal", "nombreEmpresa"})
public class Cliente implements Serializable {

    private long id_Cliente;    
    private BigDecimal bonificacion;
    private String nroCliente;
    private String nombreFiscal;
    private String nombreFantasia; 
    private CategoriaIVA categoriaIVA;
    private Long idFiscal;
    private String email;
    private String telefono;    
    private String contacto;
    private Date fechaAlta;    
    private Long idEmpresa;
    private String nombreEmpresa;    
    private Long idViajante;
    private String nombreViajante;    
    private Long idCredencial;
    private String nombreCredencial;    
    private boolean predeterminado;
    private Long idUbicacionFacturacion;
    private String detalleUbicacionFacturacion;
    private Long idUbicacionEnvio;
    private String detalleUbicacionEnvio;

    @Override
    public String toString() {
        return nombreFiscal;
    }
}
