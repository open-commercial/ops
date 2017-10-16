package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id_ProductoItem", "codigoItem"})
public class RenglonFactura implements Serializable {

    private long id_RenglonFactura;
    private long id_ProductoItem;
    private String codigoItem;
    private String descripcionItem;
    private String medidaItem;
    private double cantidad;
    private double precioUnitario;
    private double descuento_porcentaje;
    private double descuento_neto;
    private double iva_porcentaje;
    private double iva_neto;
    private double impuesto_porcentaje;
    private double impuesto_neto;
    private double ganancia_porcentaje;
    private double ganancia_neto;
    private double importe;
}
