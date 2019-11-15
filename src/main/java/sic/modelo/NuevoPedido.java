package sic.modelo;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class NuevoPedido { 
  
    private String observaciones;        
    private Long idSucursal;
    private TipoDeEnvio tipoDeEnvio;
    private Long idUsuario;
    private Long idCliente;
    private List<NuevoRenglonPedido> renglones;
    private BigDecimal recargoPorcentaje;
    private BigDecimal descuentoPorcentaje;
    
}
