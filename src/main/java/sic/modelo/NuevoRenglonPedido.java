package sic.modelo;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NuevoRenglonPedido {

    private long idProductoItem;
    private BigDecimal cantidad;
    private BigDecimal descuentoPorcentaje;

}
