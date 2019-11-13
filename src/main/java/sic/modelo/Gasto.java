package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nroGasto", "idEmpresa"})
public class Gasto implements Serializable {

    private long idGasto;
    private long nroGasto;
    private LocalDateTime fecha;
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
