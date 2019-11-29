package sic.modelo;

import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"idCantidadSucursal", "idSucursal"})
public class CantidadEnSucursal {

    private Long idCantidadSucursal;
    private BigDecimal cantidad;
    private String estanteria;
    private String estante;
    public Long idSucursal;
    public String nombreSucursal;
}
