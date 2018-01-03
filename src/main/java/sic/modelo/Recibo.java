package sic.modelo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nroRecibo", "fecha"})
public class Recibo implements Serializable {
    
    private Long idRecibo;  
    private long serie;   
    private long nroRecibo;    
    private boolean eliminado;
    private String concepto;
    private Date fecha;
    private double monto;
    private double saldoSobrante;
    private String nombreFormaDePago;   
    private String nombreEmpresa;
    private String razonSocialCliente;
    private String nombreUsuario;
    
}
