package sic.modelo;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductosParaActualizar {

    private long[] idProducto;
    private BigDecimal descuentoRecargoPorcentaje;
    private BigDecimal cantidadVentaMinima;
    private Long idMedida;
    private Long idRubro;
    private Long idProveedor;
    private BigDecimal gananciaPorcentaje;
    private BigDecimal ivaPorcentaje;
    private BigDecimal precioCosto;
    private BigDecimal porcentajeBonificacionPrecio;
    private BigDecimal porcentajeBonificacionOferta;
    private Boolean publico;
    private Boolean paraCatalogo;

}
