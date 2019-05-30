package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RenglonCuentaCorriente implements Serializable {
    
    private Long idRenglonCuentaCorriente;
    private Long idMovimiento;
    private TipoDeComprobante tipoComprobante;
    private long serie;    
    private long numero;
    private String descripcion;    
    private boolean eliminado;
    private Date fecha;
    private Date fechaVencimiento;   
    private BigDecimal monto;
    private CuentaCorriente cuentaCorriente;
    private Factura factura; 
    private Nota nota;    
    private long cae;   
    private BigDecimal saldo;    
}