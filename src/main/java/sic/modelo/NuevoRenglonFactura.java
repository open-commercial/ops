package sic.modelo;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = "idProducto")
public class NuevoRenglonFactura {

    private long idProducto;
    private BigDecimal cantidad;
    private BigDecimal bonificacion;

}
