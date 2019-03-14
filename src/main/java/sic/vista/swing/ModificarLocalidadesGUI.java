package sic.vista.swing;

import java.math.BigDecimal;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Localidad;

public class ModificarLocalidadesGUI extends javax.swing.JDialog{
    
    private final Localidad localidadSeleccionada;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ModificarLocalidadesGUI(Localidad localidad) {
        initComponents();
        localidadSeleccionada = localidad;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlLocalidad = new javax.swing.JPanel();
        lblProvincia = new javax.swing.JLabel();
        lblLocalidad = new javax.swing.JLabel();
        lblCodigoPostal = new javax.swing.JLabel();
        chkEnvio = new javax.swing.JLabel();
        chkEnvioGratuito = new javax.swing.JCheckBox();
        lblCostoEnvio = new javax.swing.JLabel();
        ftfCostoEnvio = new javax.swing.JFormattedTextField();
        lblDetalleCodigoPostal = new javax.swing.JLabel();
        lblDetalleProvincia = new javax.swing.JLabel();
        lblDetalleLocalidad = new javax.swing.JLabel();
        btnAceptar = new javax.swing.JButton();

        setTitle("Modificar Localidad");
        setFocusableWindowState(false);
        setIconImage(null);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        pnlLocalidad.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

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

        lblDetalleProvincia.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblDetalleLocalidad.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        javax.swing.GroupLayout pnlLocalidadLayout = new javax.swing.GroupLayout(pnlLocalidad);
        pnlLocalidad.setLayout(pnlLocalidadLayout);
        pnlLocalidadLayout.setHorizontalGroup(
            pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLocalidadLayout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblLocalidad, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                    .addComponent(lblCodigoPostal)
                    .addComponent(lblProvincia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkEnvio)
                    .addComponent(lblCostoEnvio))
                .addGroup(pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlLocalidadLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblDetalleCodigoPostal, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ftfCostoEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDetalleLocalidad, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDetalleProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlLocalidadLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkEnvioGratuito)))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pnlLocalidadLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {chkEnvio, lblCodigoPostal, lblCostoEnvio});

        pnlLocalidadLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ftfCostoEnvio, lblDetalleCodigoPostal, lblDetalleLocalidad, lblDetalleProvincia});

        pnlLocalidadLayout.setVerticalGroup(
            pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLocalidadLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblProvincia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDetalleProvincia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblLocalidad)
                    .addComponent(lblDetalleLocalidad))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCodigoPostal)
                    .addComponent(lblDetalleCodigoPostal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkEnvioGratuito)
                    .addComponent(chkEnvio))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlLocalidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ftfCostoEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCostoEnvio))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        pnlLocalidadLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ftfCostoEnvio, lblDetalleCodigoPostal, lblDetalleLocalidad, lblDetalleProvincia});

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
                    .addComponent(pnlLocalidad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAceptar)
                .addContainerGap())
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

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
       lblDetalleLocalidad.setText(this.localidadSeleccionada.getNombre());
       lblDetalleProvincia.setText(this.localidadSeleccionada.getNombreProvincia());
       lblDetalleCodigoPostal.setText(this.localidadSeleccionada.getCodigoPostal());
       chkEnvioGratuito.setSelected(this.localidadSeleccionada.isEnvioGratuito());
       ftfCostoEnvio.setValue(this.localidadSeleccionada.getCostoEnvio());
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JLabel chkEnvio;
    private javax.swing.JCheckBox chkEnvioGratuito;
    private javax.swing.JFormattedTextField ftfCostoEnvio;
    private javax.swing.JLabel lblCodigoPostal;
    private javax.swing.JLabel lblCostoEnvio;
    private javax.swing.JLabel lblDetalleCodigoPostal;
    private javax.swing.JLabel lblDetalleLocalidad;
    private javax.swing.JLabel lblDetalleProvincia;
    private javax.swing.JLabel lblLocalidad;
    private javax.swing.JLabel lblProvincia;
    private javax.swing.JPanel pnlLocalidad;
    // End of variables declaration//GEN-END:variables
}
