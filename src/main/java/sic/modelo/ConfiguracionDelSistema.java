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
    private String firmanteCertificadoAfip;    
    private String passwordCertificadoAfip;
    private int nroPuntoDeVentaAfip;
    private Empresa empresa;
}
