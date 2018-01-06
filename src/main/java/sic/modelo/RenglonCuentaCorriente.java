package sic.modelo;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RenglonCuentaCorriente implements Serializable {
    
    private Long idRenglonCuentaCorriente;
    private Long idMovimiento;
    private TipoDeComprobante tipo_comprobante;
    private long serie;    
    private long numero;
    private String descripcion;    
    private boolean eliminado;
    private Date fecha;
    private Date fechaVencimiento;   
    private double monto;
    private CuentaCorriente cuentaCorriente;
    private Factura factura; 
    private Pago pago;
    private Nota nota;    
    private long CAE;   
    private double saldo;    
}