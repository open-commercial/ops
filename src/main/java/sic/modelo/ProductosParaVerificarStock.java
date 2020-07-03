package sic.modelo;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductosParaVerificarStock {

    private Long idSucursal;
    private Long idPedido;
    private long[] idProducto;
    private BigDecimal[] cantidad;

}
