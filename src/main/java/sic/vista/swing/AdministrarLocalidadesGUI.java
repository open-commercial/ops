package sic.vista.swing;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Localidad;
import sic.modelo.Provincia;

public class AdministrarLocalidadesGUI extends javax.swing.JInternalFrame {
    
    private Localidad localidadSeleccionada;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public AdministrarLocalidadesGUI() {
        initComponents();
    }
    
    private void cargarProvincias() {
        try {
            cmbProvinciasBusqueda.removeAllItems();
            List<Provincia> provincias = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/ubicaciones/provincias", Provincia[].class)));
            provincias.stream().forEach(p -> {
                cmbProvinciasBusqueda.addItem(p);
            });
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarLocalidadesDeLaProvincia(Provincia provincia) {
        cmbLocalidad.removeAllItems();
        try {
            if (!provincia.getNombre().equals("")) {
                List<Localidad> localidades = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                        .getForObject("/ubicaciones/localidades/provincias/" + provincia.getId_Provincia(),
                                Localidad[].class)));
                localidades.stream().forEach(l -> cmbLocalidad.addItem(l));
            }
            localidadSeleccionada = (Localidad) cmbLocalidad.getSelectedItem();            
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlLocalidad = new javax.swing.JPanel();
        cmbProvinciasBusqueda = new javax.swing.JComboBox<>();
        cmbLocalidad = new javax.swing.JComboBox<>();
        lblProvincia = new javax.swing.JLabel();
        lblLocalidad = new javax.swing.JLabel();
        lblCodigoPostal = new javax.swing.JLabel();
        chkEnvio = new javax.swing.JLabel();
        chkEnvioGratuito = new javax.swing.JCheckBox();
        lblCostoEnvio = new javax.swing.JLabel();
        ftfCostoEnvio = new javax.swing.JFormattedTextField();
        lblDetalleCodigoPostal = new javax.swing.JLabel();
        btnAceptar = new javax.swing.JButton();

        setClosable(true);
        setTitle("Administrar Localidades");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditMap_16x16.png"))); // NOI18N
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

        pnlLocalidad.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        cmbProvinciasBusqueda.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbProvinciasBusquedaItemStateChanged(evt);
            }
        });

        cmbLocalidad.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbLocalidadItemStateChanged(evt);
            }
        });

        lblProvincia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblProvincia.setText("Provincia:");

        lblLocalidad.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLocalidad.setText("Localidad:");

        lblCodigoPostal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigoPostal.setText("Código Postal:");

        chkEnvio.setText("Envío Gratuito:");

        lblCostoEnvio.setText("Costo de Envío:");

        ftfCostoEnvio.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));

        lblDetalleCodigoPostal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDetalleCodigoPostal.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        javax.swing.GroupLayout pnlLocalidadLayout = new javax.swing.GroupLayout(pnlLocalidad);
        pnlLocalidad.setLayout(pnlLocalidadLayout);
        pnlLocalidadLayout.setHorizontalGroup(
            pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLocalidadLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLocalidadLayout.createSequentialGroup()
                        .addGroup(pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblLocalidad, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                            .addComponent(lblProvincia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbLocalidad, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbProvinciasBusqueda, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLocalidadLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLocalidadLayout.createSequentialGroup()
                                .addComponent(lblCodigoPostal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblDetalleCodigoPostal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(pnlLocalidadLayout.createSequentialGroup()
                                    .addComponent(chkEnvio)
                                    .addGap(0, 0, 0)
                                    .addComponent(chkEnvioGratuito))
                                .addGroup(pnlLocalidadLayout.createSequentialGroup()
                                    .addComponent(lblCostoEnvio)
                                    .addGap(0, 0, 0)
                                    .addComponent(ftfCostoEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );

        pnlLocalidadLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {chkEnvio, lblCodigoPostal, lblCostoEnvio});

        pnlLocalidadLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ftfCostoEnvio, lblDetalleCodigoPostal});

        pnlLocalidadLayout.setVerticalGroup(
            pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLocalidadLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblProvincia)
                    .addComponent(cmbProvinciasBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmbLocalidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLocalidad))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCodigoPostal)
                    .addComponent(lblDetalleCodigoPostal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkEnvio)
                    .addComponent(chkEnvioGratuito))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ftfCostoEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCostoEnvio))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlLocalidadLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ftfCostoEnvio, lblDetalleCodigoPostal});

        btnAceptar.setForeground(java.awt.Color.blue);
        btnAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btnAceptar.setText("Aceptar");
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlLocalidad, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnAceptar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlLocalidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAceptar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        localidadSeleccionada.setEnvioGratuito(chkEnvioGratuito.isSelected());
        try {
            if (ftfCostoEnvio.getText() != null && !ftfCostoEnvio.getText().isEmpty()) {
                localidadSeleccionada.setCostoEnvio(new BigDecimal(ftfCostoEnvio.getText().trim()));
            }
            RestClient.getRestTemplate().put("/ubicaciones/localidades", this.localidadSeleccionada);
            JOptionPane.showMessageDialog(this, "La Localidad se modificó correctamente!",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAceptarActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        this.cargarProvincias();
        this.cargarLocalidadesDeLaProvincia((Provincia) cmbProvinciasBusqueda.getSelectedItem());
        lblDetalleCodigoPostal.setText(localidadSeleccionada.getCodigoPostal());
    }//GEN-LAST:event_formInternalFrameOpened

    private void cmbLocalidadItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbLocalidadItemStateChanged
        localidadSeleccionada = (Localidad) cmbLocalidad.getSelectedItem();
        if (localidadSeleccionada != null) {
            chkEnvioGratuito.setSelected(localidadSeleccionada.isEnvioGratuito());
            lblDetalleCodigoPostal.setText(localidadSeleccionada.getCodigoPostal());
            if (localidadSeleccionada.getCostoEnvio() != null) {
                ftfCostoEnvio.setValue(localidadSeleccionada.getCostoEnvio());
                try {
                    ftfCostoEnvio.commitEdit();
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_cmbLocalidadItemStateChanged

    private void cmbProvinciasBusquedaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbProvinciasBusquedaItemStateChanged
        this.cargarLocalidadesDeLaProvincia((Provincia) cmbProvinciasBusqueda.getSelectedItem());
    }//GEN-LAST:event_cmbProvinciasBusquedaItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JLabel chkEnvio;
    private javax.swing.JCheckBox chkEnvioGratuito;
    private javax.swing.JComboBox<Localidad> cmbLocalidad;
    private javax.swing.JComboBox<Provincia> cmbProvinciasBusqueda;
    private javax.swing.JFormattedTextField ftfCostoEnvio;
    private javax.swing.JLabel lblCodigoPostal;
    private javax.swing.JLabel lblCostoEnvio;
    private javax.swing.JLabel lblDetalleCodigoPostal;
    private javax.swing.JLabel lblLocalidad;
    private javax.swing.JLabel lblProvincia;
    private javax.swing.JPanel pnlLocalidad;
    // End of variables declaration//GEN-END:variables
}
