package sic.vista.swing;

import java.util.ResourceBundle;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import sic.modelo.Ubicacion;

public class DetalleUbicacionGUI extends JDialog {

    private Ubicacion ubicacionAModificar;

    public DetalleUbicacionGUI(Ubicacion ubicacionAModificar) {
        this.ubicacionAModificar = ubicacionAModificar;
        initComponents();
    }

    public Ubicacion getUbicacionModificada() {
        return ubicacionAModificar;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlDetalleUbicacion = new javax.swing.JPanel();
        lblLatitud = new javax.swing.JLabel();
        lblLongitud = new javax.swing.JLabel();
        lblDescripcion = new javax.swing.JLabel();
        lblLocalidad = new javax.swing.JLabel();
        txtDescripcion = new javax.swing.JTextField();
        txtLocalidad = new javax.swing.JTextField();
        btnModificar = new javax.swing.JButton();
        ftfLatitud = new javax.swing.JFormattedTextField();
        ftfLongitud = new javax.swing.JFormattedTextField();
        lblCalle = new javax.swing.JLabel();
        lblNumero = new javax.swing.JLabel();
        lblPiso = new javax.swing.JLabel();
        lblDepartamento = new javax.swing.JLabel();
        txtCalle = new javax.swing.JTextField();
        txtDepartamento = new javax.swing.JTextField();
        ftfNumero = new javax.swing.JFormattedTextField();
        txtPiso = new javax.swing.JFormattedTextField();
        btnSeleccionar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Administrar Ubicación");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        pnlDetalleUbicacion.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblLatitud.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLatitud.setText("Latitud:");

        lblLongitud.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLongitud.setText("Longitud:");

        lblDescripcion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDescripcion.setText("Descripción:");

        lblLocalidad.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLocalidad.setText("Localidad:");

        txtLocalidad.setEditable(false);
        txtLocalidad.setEnabled(false);

        btnModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btnModificar.setText("Modificar");
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });

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

        txtPiso.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        javax.swing.GroupLayout pnlDetalleUbicacionLayout = new javax.swing.GroupLayout(pnlDetalleUbicacion);
        pnlDetalleUbicacion.setLayout(pnlDetalleUbicacionLayout);
        pnlDetalleUbicacionLayout.setHorizontalGroup(
            pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetalleUbicacionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPiso)
                    .addComponent(lblNumero)
                    .addComponent(lblCalle)
                    .addComponent(lblLocalidad)
                    .addComponent(lblDepartamento)
                    .addComponent(lblDescripcion)
                    .addComponent(lblLatitud)
                    .addComponent(lblLongitud))
                .addGap(0, 0, 0)
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDetalleUbicacionLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ftfLatitud, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ftfLongitud, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDepartamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPiso, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ftfNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDetalleUbicacionLayout.createSequentialGroup()
                                .addComponent(txtLocalidad)
                                .addGap(0, 0, 0)
                                .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(txtCalle, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnlDetalleUbicacionLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblCalle, lblDepartamento, lblDescripcion, lblLatitud, lblLocalidad, lblLongitud, lblNumero, lblPiso});

        pnlDetalleUbicacionLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ftfLatitud, ftfLongitud, ftfNumero, txtCalle, txtDepartamento, txtDescripcion, txtPiso});

        pnlDetalleUbicacionLayout.setVerticalGroup(
            pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetalleUbicacionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCalle)
                    .addComponent(txtCalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNumero)
                    .addComponent(ftfNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPiso)
                    .addComponent(txtPiso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDepartamento)
                    .addComponent(txtDepartamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDescripcion)
                    .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtLocalidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLocalidad)
                    .addComponent(btnModificar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblLatitud)
                    .addComponent(ftfLatitud, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetalleUbicacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLongitud)
                    .addComponent(ftfLongitud, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pnlDetalleUbicacionLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblDescripcion, lblLatitud, lblLocalidad, lblLongitud});

        pnlDetalleUbicacionLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ftfLatitud, ftfLongitud, ftfNumero, txtCalle, txtDepartamento, txtDescripcion, txtLocalidad, txtPiso});

        btnSeleccionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btnSeleccionar.setText("Seleccionar");
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
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSeleccionar))
                    .addComponent(pnlDetalleUbicacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                txtPiso.setText(ubicacionAModificar.getPiso().toString());
            }
            if (ubicacionAModificar.getDepartamento() != null) {
                txtDepartamento.setText(ubicacionAModificar.getDepartamento());
            }
            if (ubicacionAModificar.getDescripcion() != null) {
                txtDescripcion.setText(ubicacionAModificar.getDescripcion());
            }
            if (ubicacionAModificar.getNombreLocalidad() != null) {
                txtLocalidad.setText(ubicacionAModificar.getNombreLocalidad() + " " + ubicacionAModificar.getNombreProvincia());
            }
            if (ubicacionAModificar.getLatitud() != null) {
                ftfLatitud.setText(ubicacionAModificar.getLatitud().toString());
            }
            if (ubicacionAModificar.getLongitud() != null) {
                ftfLongitud.setText(ubicacionAModificar.getLongitud().toString());
            }
        }
    }//GEN-LAST:event_formWindowOpened

    private void btnSeleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionarActionPerformed
        if (ubicacionAModificar == null) {
            ubicacionAModificar = new Ubicacion();
        }
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
                ubicacionAModificar.setCalle(txtCalle.getText());
                ubicacionAModificar.setNumero(Integer.valueOf(ftfNumero.getText()));
                if (!txtPiso.getText().isEmpty()) {
                    ubicacionAModificar.setPiso(Integer.valueOf(txtPiso.getText()));
                }
                if (!txtDepartamento.getText().isEmpty()) {
                    ubicacionAModificar.setDepartamento(txtDepartamento.getText());
                }
                if (!txtDescripcion.getText().isEmpty()) {
                    ubicacionAModificar.setDescripcion(txtDescripcion.getText());
                }
                if (!ftfLatitud.getText().isEmpty()) {
                    ubicacionAModificar.setLatitud(Double.valueOf(ftfLatitud.getText()));
                }
                if (!ftfLongitud.getText().isEmpty()) {
                    ubicacionAModificar.setLongitud(Double.valueOf(ftfLongitud.getText()));
                }
                ubicacionAModificar.setDescripcion(txtDescripcion.getText());
                this.dispose();
            }
        }
    }//GEN-LAST:event_btnSeleccionarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        DetalleLocalidadGUI gui_DetalleLocalidad = new DetalleLocalidadGUI(ubicacionAModificar);
        gui_DetalleLocalidad.setModal(true);
        gui_DetalleLocalidad.setLocationRelativeTo(this);
        gui_DetalleLocalidad.setVisible(true);
        ubicacionAModificar.setNombreLocalidad(gui_DetalleLocalidad.getLocalidadSeleccionada().getNombre());
        ubicacionAModificar.setIdLocalidad(gui_DetalleLocalidad.getLocalidadSeleccionada().getId_Localidad());
        ubicacionAModificar.setCodigoPostal(gui_DetalleLocalidad.getLocalidadSeleccionada().getCodigoPostal());
        ubicacionAModificar.setNombreProvincia(gui_DetalleLocalidad.getLocalidadSeleccionada().getNombreProvincia());
        ubicacionAModificar.setIdProvincia(gui_DetalleLocalidad.getLocalidadSeleccionada().getIdProvincia());
        txtLocalidad.setText(ubicacionAModificar.getNombreLocalidad() + " (" + ubicacionAModificar.getCodigoPostal() + ") " + ubicacionAModificar.getNombreProvincia());
    }//GEN-LAST:event_btnModificarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnSeleccionar;
    private javax.swing.JFormattedTextField ftfLatitud;
    private javax.swing.JFormattedTextField ftfLongitud;
    private javax.swing.JFormattedTextField ftfNumero;
    private javax.swing.JLabel lblCalle;
    private javax.swing.JLabel lblDepartamento;
    private javax.swing.JLabel lblDescripcion;
    private javax.swing.JLabel lblLatitud;
    private javax.swing.JLabel lblLocalidad;
    private javax.swing.JLabel lblLongitud;
    private javax.swing.JLabel lblNumero;
    private javax.swing.JLabel lblPiso;
    private javax.swing.JPanel pnlDetalleUbicacion;
    private javax.swing.JTextField txtCalle;
    private javax.swing.JTextField txtDepartamento;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtLocalidad;
    private javax.swing.JFormattedTextField txtPiso;
    // End of variables declaration//GEN-END:variables
}
