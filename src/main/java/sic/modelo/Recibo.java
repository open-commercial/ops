package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombreEmpresa", "numSerie", "numRecibo", "fecha"})
public class Recibo implements Serializable {
    
    private Long idRecibo;  
    private long numSerie;   
    private long numRecibo;    
    private boolean eliminado;
    private String concepto;
    private Date fecha;
    private BigDecimal monto;
    private BigDecimal saldoSobrante;
    private String nombreFormaDePago;   
    private String nombreEmpresa;
    private String razonSocialCliente;
    private String razonSocialProveedor;
    private String nombreUsuario;
    
}
