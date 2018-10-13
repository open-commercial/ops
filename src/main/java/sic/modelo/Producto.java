package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"descripcion", "nombreEmpresa"})
public class Producto implements Serializable {

    private long id_Producto;
    private String codigo;
    private String descripcion;
    private BigDecimal cantidad;
    private BigDecimal cantMinima;
    private BigDecimal ventaMinima;
    private String nombreMedida;
    private BigDecimal precioCosto;
    private BigDecimal gananciaPorcentaje;
    private BigDecimal gananciaNeto;
    private BigDecimal precioVentaPublico;
    private BigDecimal ivaPorcentaje;
    private BigDecimal ivaNeto;
    private BigDecimal precioLista;    
    private String nombreRubro;
    private boolean ilimitado;    
    private boolean publico;
    private Date fechaUltimaModificacion;    
    private String estanteria;    
    private String estante;    
    private String razonSocialProveedor;    
    private String nota;    
    private Date fechaAlta;    
    private Date fechaVencimiento;   
    private String nombreEmpresa;
    private boolean eliminado;
}
