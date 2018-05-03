package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nroCaja", "empresa"})
public class Caja implements Serializable {

    private long id_Caja;
    private int nroCaja;
    private Date fechaApertura;
    private Date fechaCierre;
    private Empresa empresa;
    private Usuario usuarioAbreCaja;
    private Usuario usuarioCierraCaja;
    private String observacion;
    private EstadoCaja estado;
    private BigDecimal saldoInicial;
    private BigDecimal saldoSistema;
    private BigDecimal saldoReal;
    private boolean eliminada;

}
