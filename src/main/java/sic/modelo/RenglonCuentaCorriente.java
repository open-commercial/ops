package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private LocalDateTime fecha;
    private LocalDate fechaVencimiento;   
    private BigDecimal monto;
    private CuentaCorriente cuentaCorriente;
    private Factura factura; 
    private Nota nota;    
    private long cae;   
    private BigDecimal saldo;    
}