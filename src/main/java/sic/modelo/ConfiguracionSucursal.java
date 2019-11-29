package sic.modelo;

import java.io.Serializable;
import lombok.Data;

@Data
public class ConfiguracionSucursal implements Serializable {

    private long idConfiguracionSucursal;
    private boolean usarFacturaVentaPreImpresa;
    private int cantidadMaximaDeRenglonesEnFactura;
    private boolean facturaElectronicaHabilitada;
    private byte[] certificadoAfip;
    private boolean existeCertificado;
    private String firmanteCertificadoAfip;
    private String passwordCertificadoAfip;
    private int nroPuntoDeVentaAfip;
    private boolean puntoDeRetiro;
    private long idSucursal;
    private String nombreSucursal;

}
