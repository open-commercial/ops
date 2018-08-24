package sic.modelo;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NuevoRenglonPedido {

    long idProductoItem;
    private BigDecimal cantidad;
    private BigDecimal descuentoPorcentaje;

}
