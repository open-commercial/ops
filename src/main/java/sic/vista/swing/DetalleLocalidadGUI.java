package sic.vista.swing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Localidad;
import sic.modelo.Provincia;
import sic.modelo.Rol;
import sic.modelo.Ubicacion;
import sic.modelo.UsuarioActivo;

public class DetalleLocalidadGUI extends JDialog {

    private List<Localidad> localidades;
    private Localidad localidadSeleccionada;
    private final List<Rol> rolesDeUsuarioActivo = UsuarioActivo.getInstance().getUsuario().getRoles();
    private final Ubicacion ubicacion;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleLocalidadGUI() {
        this.ubicacion = null;
        this.initComponents();
        this.setIcon();
    }
    
    public DetalleLocalidadGUI(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
        this.initComponents();
        this.setIcon();
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetalleLocalidadGUI.class.getResource("/sic/icons/Map_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }
    
    private void cargarProvincias() {
        try {
            cmbProvinciasBusqueda.removeAllItems();
            List<Provincia> provincias = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/ubicaciones/provincias", Provincia[].class)));
            provincias.stream().forEach(p -> {
                cmbProvinciasBusqueda.addItem(p);
            });
            if (this.ubicacion != null && this.ubicacion.getIdProvincia() != null) {
                Provincia provinciaASeleccionar = RestClient.getRestTemplate().getForObject("/ubicaciones/provincias/" + this.ubicacion.getIdProvincia(), Provincia.class);
                cmbProvinciasBusqueda.setSelectedItem(provinciaASeleccionar);
            }
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
                localidades = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                        .getForObject("/ubicaciones/localidades/provincias/" + provincia.getId_Provincia(),
                                Localidad[].class)));
                localidades.stream().forEach(l -> cmbLocalidad.addItem(l));
            }
            if (this.ubicacion != null && this.ubicacion.getIdLocalidad() != null) {
                Localidad localidadASeleccionar = RestClient.getRestTemplate().getForObject("/ubicaciones/localidades/" + this.ubicacion.getIdLocalidad(), Localidad.class);
                cmbLocalidad.setSelectedItem(localidadASeleccionar);
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
    
    public Localidad getLocalidadSeleccionada() {
        return localidadSeleccionada;
    }    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPrincipal = new javax.swing.JPanel();
        lblProv = new javax.swing.JLabel();
        cmbProvinciasBusqueda = new javax.swing.JComboBox<>();
        lblLocalidades = new javax.swing.JLabel();
        cmbLocalidad = new javax.swing.JComboBox<>();
        panelInferior = new javax.swing.JPanel();
        lblNombre = new javax.swing.JLabel();
        txt_Nombre = new javax.swing.JTextField();
        txtCodigoPostal = new javax.swing.JTextField();
        lblCP = new javax.swing.JLabel();
        btnAceptar = new javax.swing.JButton();
        chkEnvioGratuito = new javax.swing.JCheckBox();
        chkEnvio = new javax.swing.JLabel();
        lblCostoEnvio = new javax.swing.JLabel();
        ftfCostoEnvio = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Administrar Localidades");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panelPrincipal.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblProv.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblProv.setText("Provincia:");

        cmbProvinciasBusqueda.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbProvinciasBusquedaItemStateChanged(evt);
            }
        });

        lblLocalidades.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLocalidades.setText("Localidades:");

        cmbLocalidad.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbLocalidadItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblProv, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblLocalidades, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbLocalidad, 0, 263, Short.MAX_VALUE)
                    .addComponent(cmbProvinciasBusqueda, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblProv)
                    .addComponent(cmbProvinciasBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLocalidades)
                    .addComponent(cmbLocalidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(111, 111, 111))
        );

        panelInferior.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblNombre.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNombre.setText("Nombre:");

        txt_Nombre.setEditable(false);

        txtCodigoPostal.setEditable(false);

        lblCP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCP.setText("Código Postal:");

        btnAceptar.setForeground(java.awt.Color.blue);
        btnAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btnAceptar.setText("Aceptar");
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        chkEnvioGratuito.setEnabled(false);

        chkEnvio.setText("Envío Gratuito:");

        lblCostoEnvio.setText("Costo de Envío:");

        ftfCostoEnvio.setEditable(false);
        ftfCostoEnvio.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));

        javax.swing.GroupLayout panelInferiorLayout = new javax.swing.GroupLayout(panelInferior);
        panelInferior.setLayout(panelInferiorLayout);
        panelInferiorLayout.setHorizontalGroup(
            panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInferiorLayout.createSequentialGroup()
                .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInferiorLayout.createSequentialGroup()
                        .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblNombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblCP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, 0)
                        .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCodigoPostal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_Nombre, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnAceptar)
                        .addGroup(panelInferiorLayout.createSequentialGroup()
                            .addComponent(lblCostoEnvio)
                            .addGap(0, 0, 0)
                            .addComponent(ftfCostoEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 4, Short.MAX_VALUE))
            .addGroup(panelInferiorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkEnvio)
                .addGap(0, 0, 0)
                .addComponent(chkEnvioGratuito)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelInferiorLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ftfCostoEnvio, txtCodigoPostal, txt_Nombre});

        panelInferiorLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {chkEnvio, lblCP, lblCostoEnvio, lblNombre});

        panelInferiorLayout.setVerticalGroup(
            panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInferiorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNombre))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtCodigoPostal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCP))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkEnvio)
                    .addComponent(chkEnvioGratuito))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ftfCostoEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCostoEnvio))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAceptar)
                .addContainerGap())
        );

        panelInferiorLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ftfCostoEnvio, txtCodigoPostal, txt_Nombre});

        panelInferiorLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {chkEnvio, lblCP, lblCostoEnvio, lblNombre});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelInferior, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelInferior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        localidadSeleccionada = (Localidad) cmbLocalidad.getSelectedItem();
        this.dispose();
    }//GEN-LAST:event_btnAceptarActionPerformed

    private void cmbProvinciasBusquedaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbProvinciasBusquedaItemStateChanged
        this.cargarLocalidadesDeLaProvincia((Provincia) cmbProvinciasBusqueda.getSelectedItem());
    }//GEN-LAST:event_cmbProvinciasBusquedaItemStateChanged

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
       this.cargarProvincias();
       this.cargarLocalidadesDeLaProvincia((Provincia) cmbProvinciasBusqueda.getSelectedItem());
        if (!rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)) {
            btnAceptar.setEnabled(false);
        }
    }//GEN-LAST:event_formWindowOpened

    private void cmbLocalidadItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbLocalidadItemStateChanged
        localidadSeleccionada = (Localidad) cmbLocalidad.getSelectedItem();
        if (localidadSeleccionada != null) {
            txt_Nombre.setText(localidadSeleccionada.getNombre());
            txtCodigoPostal.setText(localidadSeleccionada.getCodigoPostal());
            chkEnvioGratuito.setSelected(localidadSeleccionada.isEnvioGratuito());
            if (localidadSeleccionada.getCostoEnvio() != null) {
                ftfCostoEnvio.setText(localidadSeleccionada.getCostoEnvio().toString());
            }
        }
    }//GEN-LAST:event_cmbLocalidadItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JLabel chkEnvio;
    private javax.swing.JCheckBox chkEnvioGratuito;
    private javax.swing.JComboBox<Localidad> cmbLocalidad;
    private javax.swing.JComboBox<Provincia> cmbProvinciasBusqueda;
    private javax.swing.JFormattedTextField ftfCostoEnvio;
    private javax.swing.JLabel lblCP;
    private javax.swing.JLabel lblCostoEnvio;
    private javax.swing.JLabel lblLocalidades;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblProv;
    private javax.swing.JPanel panelInferior;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JTextField txtCodigoPostal;
    private javax.swing.JTextField txt_Nombre;
    // End of variables declaration//GEN-END:variables
}
