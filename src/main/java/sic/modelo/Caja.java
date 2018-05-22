package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id_Caja", "empresa"})
public class Caja implements Serializable {

    private long id_Caja;
    private Date fechaApertura;
    private Date fechaCierre;
    private Empresa empresa;
    private Usuario usuarioAbreCaja;
    private Usuario usuarioCierraCaja;
    private EstadoCaja estado;
    private BigDecimal saldoApertura;
    private BigDecimal saldoSistema;
    private BigDecimal saldoReal;
    private boolean eliminada;

}
