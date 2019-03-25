package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"nombre"})
public class Localidad implements Serializable {

    private long idLocalidad;
    private String nombre;
    private String codigoPostal;
    private Long idProvincia;
    private String nombreProvincia;
    private boolean envioGratuito;
    private BigDecimal costoEnvio;

    @Override
    public String toString() {
        return nombre;
    }
}
