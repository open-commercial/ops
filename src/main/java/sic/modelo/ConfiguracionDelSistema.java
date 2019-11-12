package sic.modelo;

import java.io.Serializable;
import lombok.Data;

@Data
public class ConfiguracionDelSistema implements Serializable {

    private long idConfiguracionDelSistema;
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
    private long idEmpresa;
    private String nombreEmpresa;

}
