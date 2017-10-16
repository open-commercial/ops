package sic.modelo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nroPago", "empresa"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id_Pago", scope = Pago.class)
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
