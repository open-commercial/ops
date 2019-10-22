package sic.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class NuevoPedido { 
 
    private LocalDateTime fechaVencimiento;    
    private String observaciones;        
    private Long idEmpresa;
    private Long idUsuario;
    private Long idCliente;
    private Long idSucursal;
    private TipoDeEnvio tipoDeEnvio;
    private List<RenglonPedido> renglones;
    private BigDecimal subTotal;
    private BigDecimal recargoPorcentaje;
    private BigDecimal recargoNeto;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal descuentoNeto;
    private BigDecimal total;
    
}
