package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id_Caja", "idSucursal"})
public class Caja implements Serializable {

    private long idCaja;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private Long idSucursal;
    private String nombreSucursal;
    private Long idUsuarioAbreCaja;
    private String nombreUsuarioAbreCaja;
    private Long idUsuarioCierraCaja;
    private String nombreUsuarioCierraCaja;
    private EstadoCaja estado;
    private BigDecimal saldoApertura;
    private BigDecimal saldoSistema;
    private BigDecimal saldoReal;    
}
