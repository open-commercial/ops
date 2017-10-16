package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FacturaCompra extends Factura implements Serializable {

    private Proveedor proveedor;
}
