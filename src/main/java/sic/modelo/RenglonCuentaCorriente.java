package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
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
    private Long idSucursal;
    private String nombreSucursal;
    private boolean eliminado;
    private LocalDateTime fecha;
    private BigDecimal monto;
    private BigDecimal saldo;    
}