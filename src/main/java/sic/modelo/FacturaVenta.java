package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FacturaVenta extends Factura implements Serializable {

    private Cliente cliente;
    private Usuario usuario;
}
