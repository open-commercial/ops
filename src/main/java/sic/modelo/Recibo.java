package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombreSucursal", "numSerie", "numRecibo", "fecha"})
public class Recibo implements Serializable {
    
    private Long idRecibo;  
    private long numSerie;   
    private long numRecibo;    
    private boolean eliminado;
    private String concepto;
    private LocalDateTime fecha;
    private BigDecimal monto;
    private long idFormaDePago;
    private String nombreFormaDePago;   
    private long idSucursal;
    private String nombreSucursal;
    private Long idCliente;
    private String nombreFiscalCliente;
    private Long idProveedor;
    private String razonSocialProveedor;
    private Long idViajante;
    private String nombreViajante;
    private String nombreUsuario;
    
}
