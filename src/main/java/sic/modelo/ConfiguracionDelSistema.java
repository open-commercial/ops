package sic.modelo;

import java.io.Serializable;
import lombok.Data;

@Data
public class ConfiguracionDelSistema implements Serializable {

    private long id_ConfiguracionDelSistema;
    private boolean usarFacturaVentaPreImpresa;
    private int cantidadMaximaDeRenglonesEnFactura;
    private boolean facturaElectronicaHabilitada;
    private byte[] certificadoAfip;
    private boolean existeCertificado;
    private String firmanteCertificadoAfip;
    private String passwordCertificadoAfip;
    private int nroPuntoDeVentaAfip;
    private boolean emailSenderHabilitado;
    private String emailUsername;
    private String emailPassword;
    private long idSucursal;
    private String nombreSucursal;

}
