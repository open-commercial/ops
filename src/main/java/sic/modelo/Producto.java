package sic.modelo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"descripcion", "empresa"})
public class Producto implements Serializable {

    private long id_Producto;
    private String codigo;
    private String descripcion;
    private double cantidad;
    private double cantMinima;
    private double ventaMinima;
    private Medida medida;
    private double precioCosto;
    private double ganancia_porcentaje;
    private double ganancia_neto;
    private double precioVentaPublico;
    private double iva_porcentaje;
    private double iva_neto;
    private double impuestoInterno_porcentaje;
    private double impuestoInterno_neto;
    private double precioLista;    
    private Rubro rubro;
    private boolean ilimitado;    
    private Date fechaUltimaModificacion;    
    private String estanteria;    
    private String estante;    
    private Proveedor proveedor;    
    private String nota;    
    private Date fechaAlta;    
    private Date fechaVencimiento;   
    private Empresa empresa;
    private boolean eliminado;

    @Override
    public String toString() {
        return descripcion;
    }
}
