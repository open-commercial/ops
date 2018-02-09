package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nroPago", "nombreEmpresa"})
public class Pago implements Serializable {

    private Long id_Pago;    
    private long nroPago;
    private String nombreFormaDePago;    
    private BigDecimal monto;
    private Date fecha;
    private String nota;
    private String nombreEmpresa;
    private boolean eliminado;
}
