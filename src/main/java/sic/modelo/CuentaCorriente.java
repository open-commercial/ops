package sic.modelo;

import java.util.List;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(of = {"fechaApertura", "cliente", "empresa"})
@AllArgsConstructor
@NoArgsConstructor
public class CuentaCorriente implements Serializable {

    private Long idCuentaCorriente;
    
    private boolean eliminada;
    
    private Date fechaApertura;

    private Cliente cliente;

    private Empresa empresa;

    private double saldo;

    private List<RenglonCuentaCorriente> renglones;
    
}
