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
            cmb_ProvinciasBusqueda.removeAllItems();
            List<Provincia> provincias = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/ubicaciones/provincias", Provincia[].class)));
            provincias.stream().forEach(p -> {
                cmb_ProvinciasBusqueda.addItem(p);
            });
            if (this.ubicacion != null && this.ubicacion.getIdProvincia() != null) {
                Provincia provinciaASeleccionar = RestClient.getRestTemplate().getForObject("/ubicaciones/provincias/" + this.ubicacion.getIdProvincia(), Provincia.class);
                cmb_ProvinciasBusqueda.setSelectedItem(provinciaASeleccionar);
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
        lbl_Prov = new javax.swing.JLabel();
        cmb_ProvinciasBusqueda = new javax.swing.JComboBox<>();
        lbl_Localidades = new javax.swing.JLabel();
        cmbLocalidad = new javax.swing.JComboBox<>();
        panelInferior = new javax.swing.JPanel();
        lbl_Nombre = new javax.swing.JLabel();
        txt_Nombre = new javax.swing.JTextField();
        txt_CodigoPostal = new javax.swing.JTextField();
        lbl_CP = new javax.swing.JLabel();
        btnAceptar = new javax.swing.JButton();
        chkEnvioGratuito = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
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

        lbl_Prov.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Prov.setText("Provincia:");

        cmb_ProvinciasBusqueda.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_ProvinciasBusquedaItemStateChanged(evt);
            }
        });

        lbl_Localidades.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Localidades.setText("Localidades:");

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
                    .addComponent(lbl_Prov, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Localidades, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbLocalidad, 0, 263, Short.MAX_VALUE)
                    .addComponent(cmb_ProvinciasBusqueda, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Prov)
                    .addComponent(cmb_ProvinciasBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Localidades)
                    .addComponent(cmbLocalidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(111, 111, 111))
        );

        panelInferior.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_Nombre.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Nombre.setText("Nombre:");

        txt_Nombre.setEditable(false);

        txt_CodigoPostal.setEditable(false);

        lbl_CP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_CP.setText("Código Postal:");

        btnAceptar.setForeground(java.awt.Color.blue);
        btnAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btnAceptar.setText("Aceptar");
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        chkEnvioGratuito.setEnabled(false);

        jLabel1.setText("Envío Gratuito:");

        jLabel2.setText("Costo de Envío:");

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
                            .addComponent(lbl_Nombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_CP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, 0)
                        .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_CodigoPostal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_Nombre, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnAceptar)
                        .addGroup(panelInferiorLayout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addGap(0, 0, 0)
                            .addComponent(ftfCostoEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 4, Short.MAX_VALUE))
            .addGroup(panelInferiorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(0, 0, 0)
                .addComponent(chkEnvioGratuito)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelInferiorLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ftfCostoEnvio, txt_CodigoPostal, txt_Nombre});

        panelInferiorLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, lbl_CP, lbl_Nombre});

        panelInferiorLayout.setVerticalGroup(
            panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInferiorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Nombre))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_CodigoPostal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_CP))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(chkEnvioGratuito))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ftfCostoEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAceptar)
                .addContainerGap())
        );

        panelInferiorLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ftfCostoEnvio, txt_CodigoPostal, txt_Nombre});

        panelInferiorLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel2, lbl_CP, lbl_Nombre});

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

    private void cmb_ProvinciasBusquedaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_ProvinciasBusquedaItemStateChanged
        this.cargarLocalidadesDeLaProvincia((Provincia) cmb_ProvinciasBusqueda.getSelectedItem());
    }//GEN-LAST:event_cmb_ProvinciasBusquedaItemStateChanged

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
       this.cargarProvincias();
       this.cargarLocalidadesDeLaProvincia((Provincia) cmb_ProvinciasBusqueda.getSelectedItem());
        if (!rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)) {
            btnAceptar.setEnabled(false);
        }
    }//GEN-LAST:event_formWindowOpened

    private void cmbLocalidadItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbLocalidadItemStateChanged
        localidadSeleccionada = (Localidad) cmbLocalidad.getSelectedItem();
        if (localidadSeleccionada != null) {
            txt_Nombre.setText(localidadSeleccionada.getNombre());
            txt_CodigoPostal.setText(localidadSeleccionada.getCodigoPostal());
            chkEnvioGratuito.setSelected(localidadSeleccionada.isEnvioGratuito());
            if (localidadSeleccionada.getCostoEnvio() != null) {
                ftfCostoEnvio.setText(localidadSeleccionada.getCostoEnvio().toString());
            }
        }
    }//GEN-LAST:event_cmbLocalidadItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JCheckBox chkEnvioGratuito;
    private javax.swing.JComboBox<Localidad> cmbLocalidad;
    private javax.swing.JComboBox<Provincia> cmb_ProvinciasBusqueda;
    private javax.swing.JFormattedTextField ftfCostoEnvio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lbl_CP;
    private javax.swing.JLabel lbl_Localidades;
    private javax.swing.JLabel lbl_Nombre;
    private javax.swing.JLabel lbl_Prov;
    private javax.swing.JPanel panelInferior;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JTextField txt_CodigoPostal;
    private javax.swing.JTextField txt_Nombre;
    // End of variables declaration//GEN-END:variables
}
