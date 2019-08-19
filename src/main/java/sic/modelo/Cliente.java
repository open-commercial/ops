package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombreFiscal", "idFiscal", "nombreSucursal"})
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
    private Long idSucursal;
    private String nombreSucursal;    
    private Long idViajante;
    private String nombreViajante;
    private Long idCredencial;
    private String nombreCredencial;
    private boolean predeterminado;
    private Ubicacion ubicacionFacturacion;
    private Ubicacion ubicacionEnvio;
  
    @Override
    public String toString() {
        return nombreFiscal;
    }
}
