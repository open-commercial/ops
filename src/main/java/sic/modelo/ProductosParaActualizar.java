package sic.modelo;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductosParaActualizar {

    private long[] idProducto;
    private BigDecimal descuentoRecargoPorcentaje;
    private Long idMedida;
    private Long idRubro;
    private Long idProveedor;
    private BigDecimal gananciaNeto;
    private BigDecimal gananciaPorcentaje;
    private BigDecimal ivaNeto;
    private BigDecimal ivaPorcentaje;
    private BigDecimal precioCosto;
    private BigDecimal precioLista;
    private BigDecimal precioVentaPublico;
    private BigDecimal porcentajeBonificacionPrecio;
    private Boolean publico;

}
