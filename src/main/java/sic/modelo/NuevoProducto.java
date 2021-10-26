package sic.modelo;

import java.math.BigDecimal;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NuevoProducto {

    private String codigo;
    private String descripcion;
    private Map<Long,BigDecimal> cantidadEnSucursal;    
    private BigDecimal precioBonificado;
    private BigDecimal cantMinima;
    private BigDecimal precioCosto;
    private BigDecimal gananciaPorcentaje;
    private BigDecimal gananciaNeto;
    private BigDecimal precioVentaPublico;
    private BigDecimal ivaPorcentaje;
    private BigDecimal ivaNeto;
    private BigDecimal precioLista;
    private boolean ilimitado;
    private boolean publico;
    private boolean oferta;
    private byte[] imagen;
    private BigDecimal porcentajeBonificacionOferta;
    private BigDecimal porcentajeBonificacionPrecio;
    private LocalDateTime fechaUltimaModificacion;
    private String nota;
    private LocalDate fechaVencimiento;
    private boolean eliminado;

}
