package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nroGasto", "empresa"})
public class Gasto implements Serializable {

    private long id_Gasto;
    private long nroGasto;
    private Date fecha;
    private String concepto;
    private Long idEmpresa;
    private String nombreEmpresa;
    private Long idUsuario;
    private String nombreUsuario;
    private Long idFormaDePago;
    private String nombreFormaDePago;
    private BigDecimal monto;
    private boolean eliminado;
}
