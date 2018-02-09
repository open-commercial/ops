package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nroCaja", "empresa"})
public class Caja implements Serializable {

    private long id_Caja;
    private int nroCaja;
    private Date fechaApertura;
    private Date fechaCorteInforme;
    private Date fechaCierre;
    private Empresa empresa;
    private Usuario usuarioAbreCaja;
    private Usuario usuarioCierraCaja;
    private String observacion;
    private EstadoCaja estado;
    private BigDecimal saldoInicial;
    private BigDecimal saldoFinal;
    private BigDecimal saldoReal;
    private boolean eliminada;
    private Map<Long, Double> totalesPorFomaDePago;
    private BigDecimal totalAfectaCaja;
    private BigDecimal totalGeneral;

}
