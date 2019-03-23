package sic.vista.swing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Localidad;
import sic.modelo.Provincia;
import sic.modelo.Ubicacion;

public class DetalleUbicacionGUI extends JDialog {

    private Ubicacion ubicacionAModificar;
    private Localidad localidadSeleccionada;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleUbicacionGUI(Ubicacion ubicacionAModificar, String tituloVentana) {
        this.ubicacionAModificar = ubicacionAModificar;
        initComponents();
        this.modificarTituloVentana(tituloVentana);
    }

    public Ubicacion getUbicacionModificada() {
        return ubicacionAModificar;
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

    private void seleccionarProvinciaDelCliente() {
        if (this.ubicacionAModificar != null && this.ubicacionAModificar.getIdProvincia() != null) {
            Provincia provinciaASeleccionar = RestClient.getRestTemplate().getForObject("/ubicaciones/provincias/" + this.ubicacionAModificar.getIdProvincia(), Provincia.class);
            cmbProvinciasBusqueda.setSelectedItem(provinciaASeleccionar);
        }
    }

    private void cargarLocalidadesDeLaProvincia(Provincia provincia) {
        try {
            cmbLocalidad.removeAllItems();
            if (provincia != null) {
                List<Localidad> localidades = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                        .getForObject("/ubicaciones/localidades/provincias/" + provincia.getId_Provincia(),
                                Localidad[].class)));
                localidades.stream().forEach(l -> cmbLocalidad.addItem(l));
            }
            if (this.ubicacionAModificar != null && this.ubicacionAModificar.getIdLocalidad() != null) {
                Localidad localidadASeleccionar = RestClient.getRestTemplate().getForObject("/ubicaciones/localidades/" + this.ubicacionAModificar.getIdLocalidad(), Localidad.class);
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
    
    private void modificarTituloVentana(String tituloVentana) {
        this.setTitle(tituloVentana);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlDetalleUbicacion = new javax.swing.JPanel();
        lblLatitud = new javax.swing.JLabel();
        lblLongitud = new javax.swing.JLabel();
        lblDescripcion = new javax.swing.JLabel();
        txtDescripcion = new javax.swing.JTextField();
        ftfLatitud = new javax.swing.JFormattedTextField();
        ftfLongitud = new javax.swing.JFormattedTextField();
        lblCalle = new javax.swing.JLabel();
        lblNumero = new javax.swing.JLabel();
        lblPiso = new javax.swing.JLabel();
        lblDepartamento = new javax.swing.JLabel();
        txtCalle = new javax.swing.JTextField();
        txtDepartamento = new javax.swing.JTextField();
        ftfNumero = new javax.swing.JFormattedTextField();
        ftfPiso = new javax.swing.JFormattedTextField();
        lblProvincia = new javax.swing.JLabel();
        cmbProvinciasBusqueda = new javax.swing.JComboBox<>();
        lblLocalidades = new javax.swing.JLabel();
        cmbLocalidad = new javax.swing.JComboBox<>();
        txtCodigoPostal = new javax.swing.JTextField();
        lblCP = new javax.swing.JLabel();
        btnSeleccionar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        pnlDetalleUbicacion.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblLatitud.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLatitud.setText("Latitud:");

        lblLongitud.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLongitud.setText("Longitud:");

        lblDescripcion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDescripcion.setText("Descripción:");

        ftfLatitud.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##0.#######"))));

        ftfLongitud.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##0.#######"))));

        lblCalle.setForeground(java.awt.Color.red);
        lblCalle.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCalle.setText("* Calle:");

        lblNumero.setForeground(java.awt.Color.red);
        lblNumero.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNumero.setText("* Número:");

        lblPiso.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPiso.setText("Piso:");

        lblDepartamento.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDepartamento.setText("Departamento:");

        ftfNumero.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        ftfPiso.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        lblProvincia.setForeground(java.awt.Color.red);
        lblProvincia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblProvincia.setText("* Provincia:");

        cmbProvinciasBusqueda.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbProvinciasBusquedaItemStateChanged(evt);
            }
        });

        lblLocalidades.setForeground(java.awt.Color.red);
        lblLocalidades.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLocalidades.setText("* Localidad:");

        txtCodigoPostal.setEditable(false);

        lblCP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCP.setText("Código Postal:");

        javax.swing.GroupLayout pnlDetalleUbicacionLayout = new javax.swing.GroupLayout(pnlDetalleUbicacion);
        pnlDetalleUbicacion.setLayout(pnlDetalleUbicacionLayout);
        pnlDetalleUbicacionLayout.setHorizontalGroup(
            pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetalleUbicacionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDetalleUbicacionLayout.createSequentialGroup()
                        .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPiso)
                            .addComponent(lblNumero)
                            .addComponent(lblCalle)
                            .addComponent(lblDepartamento)
                            .addComponent(lblDescripcion))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCalle)
                            .addComponent(ftfNumero)
                            .addComponent(txtDepartamento)
                            .addComponent(txtDescripcion)
                            .addComponent(ftfPiso, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDetalleUbicacionLayout.createSequentialGroup()
                        .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLatitud)
                            .addComponent(lblLongitud))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ftfLatitud)
                            .addComponent(ftfLongitud)))
                    .addGroup(pnlDetalleUbicacionLayout.createSequentialGroup()
                        .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblCP, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                            .addComponent(lblProvincia, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblLocalidades, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbLocalidad, javax.swing.GroupLayout.Alignment.TRAILING, 0, 440, Short.MAX_VALUE)
                            .addComponent(cmbProvinciasBusqueda, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtCodigoPostal, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );

        pnlDetalleUbicacionLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblCP, lblCalle, lblDepartamento, lblDescripcion, lblLatitud, lblLocalidades, lblLongitud, lblNumero, lblPiso, lblProvincia});

        pnlDetalleUbicacionLayout.setVerticalGroup(
            pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetalleUbicacionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCalle)
                    .addComponent(txtCalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblNumero)
                    .addComponent(ftfNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblPiso)
                    .addComponent(ftfPiso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDepartamento)
                    .addComponent(txtDepartamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDescripcion)
                    .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblProvincia)
                    .addComponent(cmbProvinciasBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblLocalidades)
                    .addComponent(cmbLocalidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCP)
                    .addComponent(txtCodigoPostal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblLatitud)
                    .addComponent(ftfLatitud, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblLongitud)
                    .addComponent(ftfLongitud, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlDetalleUbicacionLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblDescripcion, lblLatitud, lblLongitud});

        pnlDetalleUbicacionLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ftfLatitud, ftfLongitud, ftfNumero, ftfPiso, txtCalle, txtDepartamento, txtDescripcion});

        btnSeleccionar.setForeground(java.awt.Color.blue);
        btnSeleccionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/ArrowRight_16x16.png"))); // NOI18N
        btnSeleccionar.setText("Continuar");
        btnSeleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlDetalleUbicacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSeleccionar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlDetalleUbicacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSeleccionar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if (ubicacionAModificar != null) {
            txtCalle.setText(ubicacionAModificar.getCalle());
            ftfNumero.setText(ubicacionAModificar.getNumero().toString());
            if (ubicacionAModificar.getPiso() != null) {
                ftfPiso.setText(ubicacionAModificar.getPiso().toString());
            }
            if (ubicacionAModificar.getDepartamento() != null) {
                txtDepartamento.setText(ubicacionAModificar.getDepartamento());
            }
            if (ubicacionAModificar.getDescripcion() != null) {
                txtDescripcion.setText(ubicacionAModificar.getDescripcion());
            }
            if (ubicacionAModificar.getLatitud() != null) {
                ftfLatitud.setText(ubicacionAModificar.getLatitud().toString());
            }
            if (ubicacionAModificar.getLongitud() != null) {
                ftfLongitud.setText(ubicacionAModificar.getLongitud().toString());
            }
        } else {
            ubicacionAModificar = new Ubicacion();
        }
        this.cargarProvincias();
        this.seleccionarProvinciaDelCliente();
        this.cargarLocalidadesDeLaProvincia((Provincia) cmbProvinciasBusqueda.getSelectedItem());
    }//GEN-LAST:event_formWindowOpened

    private void btnSeleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionarActionPerformed
        
        if (txtCalle.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_ubicacion_calle_vacia"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            if (ftfNumero.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_ubicacion_numero_vacio"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                if (cmbProvinciasBusqueda.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_ubicacion_provincia_vacia"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (cmbLocalidad.getSelectedItem() == null) {
                        JOptionPane.showMessageDialog(this,
                                ResourceBundle.getBundle("Mensajes").getString("mensaje_ubicacion_localidad_vacia"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        ubicacionAModificar.setCalle(txtCalle.getText());
                        ubicacionAModificar.setNumero(Integer.valueOf(ftfNumero.getText()));
                        if (!ftfPiso.getText().isEmpty() && !ftfPiso.getText().isEmpty()) {
                            ubicacionAModificar.setPiso(Integer.valueOf(ftfPiso.getText().trim()));
                        }
                        ubicacionAModificar.setDepartamento(txtDepartamento.getText().trim());
                        ubicacionAModificar.setDescripcion(txtDescripcion.getText().trim());
                        if (!ftfLatitud.getText().isEmpty() && !ftfLatitud.getText().isEmpty()) {
                            ubicacionAModificar.setLatitud(Double.valueOf(ftfLatitud.getText().trim()));
                        }
                        if (!ftfLongitud.getText().isEmpty() && !ftfLongitud.getText().isEmpty()) {
                            ubicacionAModificar.setLongitud(Double.valueOf(ftfLongitud.getText().trim()));
                        }
                        ubicacionAModificar.setDescripcion(txtDescripcion.getText().trim());
                        localidadSeleccionada = (Localidad) cmbLocalidad.getSelectedItem();
                        if (localidadSeleccionada != null) {
                            ubicacionAModificar.setIdLocalidad(localidadSeleccionada.getId_Localidad());
                            ubicacionAModificar.setNombreLocalidad(localidadSeleccionada.getNombre());
                            ubicacionAModificar.setCodigoPostal(localidadSeleccionada.getCodigoPostal());
                            ubicacionAModificar.setNombreProvincia(localidadSeleccionada.getNombreProvincia());
                        }
                        this.dispose();
                    }
                }
            }
        }
    }//GEN-LAST:event_btnSeleccionarActionPerformed

    private void cmbProvinciasBusquedaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbProvinciasBusquedaItemStateChanged
        this.cargarLocalidadesDeLaProvincia((Provincia) cmbProvinciasBusqueda.getSelectedItem());
    }//GEN-LAST:event_cmbProvinciasBusquedaItemStateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if ((ubicacionAModificar.getCalle() == null || ubicacionAModificar.getCalle().isEmpty())
                || (ubicacionAModificar.getNumero() == null)) {
            ubicacionAModificar = null;
        }
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSeleccionar;
    private javax.swing.JComboBox<Localidad> cmbLocalidad;
    private javax.swing.JComboBox<Provincia> cmbProvinciasBusqueda;
    private javax.swing.JFormattedTextField ftfLatitud;
    private javax.swing.JFormattedTextField ftfLongitud;
    private javax.swing.JFormattedTextField ftfNumero;
    private javax.swing.JFormattedTextField ftfPiso;
    private javax.swing.JLabel lblCP;
    private javax.swing.JLabel lblCalle;
    private javax.swing.JLabel lblDepartamento;
    private javax.swing.JLabel lblDescripcion;
    private javax.swing.JLabel lblLatitud;
    private javax.swing.JLabel lblLocalidades;
    private javax.swing.JLabel lblLongitud;
    private javax.swing.JLabel lblNumero;
    private javax.swing.JLabel lblPiso;
    private javax.swing.JLabel lblProvincia;
    private javax.swing.JPanel pnlDetalleUbicacion;
    private javax.swing.JTextField txtCalle;
    private javax.swing.JTextField txtCodigoPostal;
    private javax.swing.JTextField txtDepartamento;
    private javax.swing.JTextField txtDescripcion;
    // End of variables declaration//GEN-END:variables
}
