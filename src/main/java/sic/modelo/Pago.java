package sic.modelo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nroPago", "empresa"})
public class Pago implements Serializable {

    private Long id_Pago;    
    private long nroPago;
    private FormaDePago formaDePago;    
    private Factura factura;
    private Nota notaDebito;
    private double monto;
    private Date fecha;
    private String nota;
    private Empresa empresa;
    private boolean eliminado;
}
