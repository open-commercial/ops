package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nroGasto", "idSucursal"})
public class Gasto implements Serializable {

    private long id_Gasto;
    private long nroGasto;
    private LocalDateTime fecha;
    private String concepto;
    private Long idSucursal;
    private String nombreSucursal;
    private Long idUsuario;
    private String nombreUsuario;
    private Long idFormaDePago;
    private String nombreFormaDePago;
    private BigDecimal monto;
    private boolean eliminado;
}
