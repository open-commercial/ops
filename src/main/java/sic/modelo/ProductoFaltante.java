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
public class ProductoFaltante {

    private long idProducto;
    private String codigo;
    private String descripcion;
    private long idSucursal;
    private String nombreSucursal;
    private BigDecimal cantidadSolicitada;
    private BigDecimal cantidadDisponible;
}
