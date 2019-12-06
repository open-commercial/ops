package sic.modelo;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class DetallePedido { 
  
  private Long idPedido;
  private Long idSucursal;
  private String observaciones;
  private Long idCliente;
  private TipoDeEnvio tipoDeEnvio;
  private List<NuevoRenglonPedido> renglones;
  private BigDecimal recargoPorcentaje;
  private BigDecimal descuentoPorcentaje;
    
}
