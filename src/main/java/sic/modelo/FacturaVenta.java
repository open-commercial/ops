package sic.modelo;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FacturaVenta extends Factura implements Serializable {

    private Long idCliente;
    private String nombreFiscalCliente;
    private String nroDeCliente;
    private CategoriaIVA categoriaIVACliente;
    private Long idViajanteCliente;
    private String nombreViajanteCliente;
    private String ubicacionCliente;
}
