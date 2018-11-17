package sic.modelo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CuentaCorrienteCliente extends CuentaCorriente {

    private Cliente cliente;

}
