package sic.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class NuevoPedido { 
 
    private LocalDate fechaVencimiento;    
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
