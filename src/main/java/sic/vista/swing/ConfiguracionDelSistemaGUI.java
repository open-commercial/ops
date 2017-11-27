package sic.vista.swing;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.ConfiguracionDelSistema;
import sic.modelo.EmpresaActiva;
import sic.util.FiltroCertificados;
import sic.util.Utilidades;

public class ConfiguracionDelSistemaGUI extends JInternalFrame {

    private ConfiguracionDelSistema cdsModificar;  
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
    public ConfiguracionDelSistemaGUI() {
        this.initComponents();    
        this.setEstadoComponentes(false);
    }

    private void setEstadoComponentes(boolean estado) {
        btn_BuscarCertificado.setEnabled(estado);
        lbl_certEstado.setEnabled(estado);
        txt_FirmanteCert.setEnabled(estado);
        txt_contraseniaCert.setEnabled(estado);
        txt_PuntoDeVentaNro.setEnabled(estado);
        txt_PuntoDeVentaNro.setEnabled(estado);
    }

    private void cargarConfiguracionParaModificar() {
        chk_PreImpresas.setSelected(cdsModificar.isUsarFacturaVentaPreImpresa());
        txt_CantMaximaRenglones.setValue(cdsModificar.getCantidadMaximaDeRenglonesEnFactura());
        if (cdsModificar.isFacturaElectronicaHabilitada() && (cdsModificar.getCertificadoAfip() != null)) {
              chk_UsarFE.setSelected(true);
              lbl_certEstado.setText("Cargado");
              lbl_certEstado.setForeground(Color.GREEN);
              txt_FirmanteCert.setText(cdsModificar.getFirmanteCertificadoAfip());
              txt_PuntoDeVentaNro.setText("" + cdsModificar.getNroPuntoDeVentaAfip());
        }
    }

    private ConfiguracionDelSistema getConfiguracionDelSistema() {
        cdsModificar.setUsarFacturaVentaPreImpresa(chk_PreImpresas.isSelected());
        cdsModificar.setCantidadMaximaDeRenglonesEnFactura(
                Integer.parseInt(txt_CantMaximaRenglones.getValue().toString()));
        if (chk_UsarFE.isSelected()) {
            cdsModificar.setFacturaElectronicaHabilitada(chk_UsarFE.isSelected());
            cdsModificar.setFirmanteCertificadoAfip(txt_FirmanteCert.getText());
            cdsModificar.setPasswordCertificadoAfip(new String(txt_contraseniaCert.getPassword()));
            if (!txt_PuntoDeVentaNro.getText().equals("")) {
                cdsModificar.setNroPuntoDeVentaAfip(Integer.parseInt(txt_PuntoDeVentaNro.getText().trim()));
            } else {
                cdsModificar.setNroPuntoDeVentaAfip(0);
            }
        } else {
            cdsModificar.setFacturaElectronicaHabilitada(false);
            cdsModificar.setCertificadoAfip(null);
            cdsModificar.setFirmanteCertificadoAfip(null);
            cdsModificar.setPasswordCertificadoAfip(null);
            cdsModificar.setNroPuntoDeVentaAfip(0);
        }
        return cdsModificar;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelGeneral = new javax.swing.JPanel();
        panelReportes = new javax.swing.JPanel();
        lbl_PreImpresas = new javax.swing.JLabel();
        chk_PreImpresas = new javax.swing.JCheckBox();
        lbl_CantMaxRenglones = new javax.swing.JLabel();
        txt_CantMaximaRenglones = new javax.swing.JFormattedTextField();
        panelFE = new javax.swing.JPanel();
        lbl_UsarFE = new javax.swing.JLabel();
        chk_UsarFE = new javax.swing.JCheckBox();
        lbl_Certificado = new javax.swing.JLabel();
        btn_BuscarCertificado = new javax.swing.JButton();
        lbl_Firmante = new javax.swing.JLabel();
        txt_FirmanteCert = new javax.swing.JTextField();
        lbl_Contrasenia = new javax.swing.JLabel();
        txt_contraseniaCert = new javax.swing.JPasswordField();
        lbl_certEstado = new javax.swing.JLabel();
        lbl_PuntoDeVenta = new javax.swing.JLabel();
        txt_PuntoDeVentaNro = new javax.swing.JTextField();
        lbl_Leyenda = new javax.swing.JLabel();
        btn_Guardar = new javax.swing.JButton();

        setClosable(true);
        setTitle("Configuración del Sistema");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Gears_16x16.png"))); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        panelGeneral.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        panelReportes.setBorder(javax.swing.BorderFactory.createTitledBorder("Reportes"));

        lbl_PreImpresas.setText("Usar Facturas Pre Impresas:");

        lbl_CantMaxRenglones.setText("Cantidad máxima de renglones:");

        txt_CantMaximaRenglones.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        javax.swing.GroupLayout panelReportesLayout = new javax.swing.GroupLayout(panelReportes);
        panelReportes.setLayout(panelReportesLayout);
        panelReportesLayout.setHorizontalGroup(
            panelReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelReportesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_CantMaxRenglones, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                    .addComponent(lbl_PreImpresas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chk_PreImpresas)
                    .addComponent(txt_CantMaximaRenglones, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelReportesLayout.setVerticalGroup(
            panelReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelReportesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chk_PreImpresas)
                    .addGroup(panelReportesLayout.createSequentialGroup()
                        .addComponent(lbl_PreImpresas)
                        .addGap(18, 18, 18)
                        .addGroup(panelReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbl_CantMaxRenglones)
                            .addComponent(txt_CantMaximaRenglones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelFE.setBorder(javax.swing.BorderFactory.createTitledBorder("Factura Electronica"));

        lbl_UsarFE.setText("Usar AFIP Factura Electronica:");

        chk_UsarFE.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_UsarFEItemStateChanged(evt);
            }
        });

        lbl_Certificado.setText("Certificado Digital AFIP:");

        btn_BuscarCertificado.setForeground(java.awt.Color.blue);
        btn_BuscarCertificado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Certificate_16x16.png"))); // NOI18N
        btn_BuscarCertificado.setText("Buscar");
        btn_BuscarCertificado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarCertificadoActionPerformed(evt);
            }
        });

        lbl_Firmante.setText("Firmante Certificado:");

        lbl_Contrasenia.setText("Contraseña Certificado:");

        lbl_certEstado.setForeground(java.awt.Color.red);
        lbl_certEstado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_certEstado.setText("No cargado");

        lbl_PuntoDeVenta.setText("Punto de Venta Nro:");

        txt_PuntoDeVentaNro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_PuntoDeVentaNroKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout panelFELayout = new javax.swing.GroupLayout(panelFE);
        panelFE.setLayout(panelFELayout);
        panelFELayout.setHorizontalGroup(
            panelFELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFELayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(lbl_UsarFE, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                        .addComponent(lbl_Certificado, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbl_Firmante, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbl_Contrasenia, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(lbl_PuntoDeVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txt_FirmanteCert, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(chk_UsarFE)
                        .addGroup(panelFELayout.createSequentialGroup()
                            .addComponent(btn_BuscarCertificado)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lbl_certEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txt_contraseniaCert)
                    .addComponent(txt_PuntoDeVentaNro, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelFELayout.setVerticalGroup(
            panelFELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFELayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_UsarFE)
                    .addComponent(chk_UsarFE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Certificado)
                    .addComponent(btn_BuscarCertificado)
                    .addComponent(lbl_certEstado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Firmante)
                    .addComponent(txt_FirmanteCert, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Contrasenia)
                    .addComponent(txt_contraseniaCert, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_PuntoDeVenta)
                    .addComponent(txt_PuntoDeVentaNro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelGeneralLayout = new javax.swing.GroupLayout(panelGeneral);
        panelGeneral.setLayout(panelGeneralLayout);
        panelGeneralLayout.setHorizontalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(panelReportes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelFE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelGeneralLayout.setVerticalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelReportes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lbl_Leyenda.setFont(new java.awt.Font("DejaVu Sans", 1, 15)); // NOI18N
        lbl_Leyenda.setText("La siguiente configuración se aplica a la empresa seleccionada:");

        btn_Guardar.setForeground(java.awt.Color.blue);
        btn_Guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btn_Guardar.setText("Guardar");
        btn_Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GuardarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbl_Leyenda)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(panelGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_Guardar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_Leyenda)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_Guardar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
        try {
            RestClient.getRestTemplate().put("/configuraciones-del-sistema", this.getConfiguracionDelSistema());
            JOptionPane.showMessageDialog(this, "La Configuración se guardó correctamente.", "Información", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_GuardarActionPerformed

    private void btn_BuscarCertificadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarCertificadoActionPerformed
        JFileChooser menuElegirCertificado = new JFileChooser();
        menuElegirCertificado.setAcceptAllFileFilterUsed(false);
        menuElegirCertificado.addChoosableFileFilter(new FiltroCertificados());
        try {
            if (menuElegirCertificado.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                if (Utilidades.esTamanioValido(menuElegirCertificado.getSelectedFile(), 100000)) {
                    File file = menuElegirCertificado.getSelectedFile();
                    cdsModificar.setFacturaElectronicaHabilitada(true);
                    cdsModificar.setCertificadoAfip(Utilidades.convertirFileIntoByteArray(file));
                    lbl_certEstado.setText("Cargado");
                    lbl_certEstado.setForeground(Color.GREEN);
                } else {
                    JOptionPane.showMessageDialog(this, "El tamaño del archivo seleccionado, supera el límite de 512kb.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (IOException ex) {
            String mensaje = ResourceBundle.getBundle("Mensajes").getString("mensaje_error_IOException");
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_BuscarCertificadoActionPerformed

    private void chk_UsarFEItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_UsarFEItemStateChanged
        if (chk_UsarFE.isSelected()) {
            this.setEstadoComponentes(true);
        } else {                       
            this.setEstadoComponentes(false);
        }
    }//GEN-LAST:event_chk_UsarFEItemStateChanged

    private void txt_PuntoDeVentaNroKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_PuntoDeVentaNroKeyTyped
        char keyChar = evt.getKeyChar();
        if (!Character.isDigit(keyChar) || keyChar == '0') {
            evt.consume();
        }
    }//GEN-LAST:event_txt_PuntoDeVentaNroKeyTyped

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            cdsModificar = RestClient.getRestTemplate().getForObject("/configuraciones-del-sistema/empresas/"
                    + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(), ConfiguracionDelSistema.class);
            this.cargarConfiguracionParaModificar();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }        
    }//GEN-LAST:event_formInternalFrameOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_BuscarCertificado;
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JCheckBox chk_PreImpresas;
    private javax.swing.JCheckBox chk_UsarFE;
    private javax.swing.JLabel lbl_CantMaxRenglones;
    private javax.swing.JLabel lbl_Certificado;
    private javax.swing.JLabel lbl_Contrasenia;
    private javax.swing.JLabel lbl_Firmante;
    private javax.swing.JLabel lbl_Leyenda;
    private javax.swing.JLabel lbl_PreImpresas;
    private javax.swing.JLabel lbl_PuntoDeVenta;
    private javax.swing.JLabel lbl_UsarFE;
    private javax.swing.JLabel lbl_certEstado;
    private javax.swing.JPanel panelFE;
    private javax.swing.JPanel panelGeneral;
    private javax.swing.JPanel panelReportes;
    private javax.swing.JFormattedTextField txt_CantMaximaRenglones;
    private javax.swing.JTextField txt_FirmanteCert;
    private javax.swing.JTextField txt_PuntoDeVentaNro;
    private javax.swing.JPasswordField txt_contraseniaCert;
    // End of variables declaration//GEN-END:variables
}
