package sic.modelo;

import lombok.Data;
import lombok.EqualsAndHashCode;

    
@Data
@EqualsAndHashCode(callSuper = true)
public class CuentaCorrienteProveedor extends CuentaCorriente {

    private Proveedor proveedor;
    
}
