package sic.modelo;

import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"idProducto"})
public class RenglonTraspaso {

    private Long idRenglonTraspaso;
    private Long idProducto;
    private String codigoProducto;
    private String descripcionProducto;
    private String nombreMedidaProducto;
    private BigDecimal cantidadProducto;
}
