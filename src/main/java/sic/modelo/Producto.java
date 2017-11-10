package sic.modelo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"descripcion", "nombreEmpresa"})
public class Producto implements Serializable {

    private long id_Producto;
    private String codigo;
    private String descripcion;
    private double cantidad;
    private double cantMinima;
    private double ventaMinima;
    private String nombreMedida;
    private double precioCosto;
    private double ganancia_porcentaje;
    private double ganancia_neto;
    private double precioVentaPublico;
    private double iva_porcentaje;
    private double iva_neto;
    private double impuestoInterno_porcentaje;
    private double impuestoInterno_neto;
    private double precioLista;    
    private String nombreRubro;
    private boolean ilimitado;    
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
