package sic.vista.swing;

public class CerrarPedidoGUI extends javax.swing.JDialog {

    public CerrarPedidoGUI(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelCerrarPedido = new javax.swing.JPanel();
        lblCalle = new javax.swing.JLabel();
        txtCalle = new javax.swing.JTextField();
        lblNumero = new javax.swing.JLabel();
        ftfNumero = new javax.swing.JFormattedTextField();
        lblPiso = new javax.swing.JLabel();
        txtPiso = new javax.swing.JFormattedTextField();
        lblDepartamento = new javax.swing.JLabel();
        txtDepartamento = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDescripcion = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtLocalidad = new javax.swing.JTextField();
        btnModificar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        ftfLatitud = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        ftfLongitud = new javax.swing.JFormattedTextField();
        btnCerrarPedido = new javax.swing.JButton();
        chkEnviarUbicacionFacturacion = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Detalle de Envío");

        PanelCerrarPedido.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblCalle.setForeground(java.awt.Color.red);
        lblCalle.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCalle.setText("* Calle:");

        lblNumero.setForeground(java.awt.Color.red);
        lblNumero.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNumero.setText("* Número:");

        ftfNumero.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        lblPiso.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPiso.setText("Piso:");

        txtPiso.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        lblDepartamento.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDepartamento.setText("Departamento:");

        txtDepartamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDepartamentoActionPerformed(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Descripción:");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Localidad:");

        txtLocalidad.setEditable(false);
        txtLocalidad.setEnabled(false);

        btnModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btnModificar.setText("Modificar");
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Latitud:");

        ftfLatitud.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##0.#######"))));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Longitud:");

        ftfLongitud.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##0.#######"))));

        javax.swing.GroupLayout PanelCerrarPedidoLayout = new javax.swing.GroupLayout(PanelCerrarPedido);
        PanelCerrarPedido.setLayout(PanelCerrarPedidoLayout);
        PanelCerrarPedidoLayout.setHorizontalGroup(
            PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCerrarPedidoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelCerrarPedidoLayout.createSequentialGroup()
                        .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDepartamento)
                            .addComponent(jLabel3)
                            .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lblPiso)
                                .addComponent(lblNumero))
                            .addComponent(lblCalle))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(ftfNumero, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPiso, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDepartamento, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCalle, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(PanelCerrarPedidoLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLocalidad)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelCerrarPedidoLayout.createSequentialGroup()
                        .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ftfLatitud, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ftfLongitud, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        PanelCerrarPedidoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, lblCalle, lblDepartamento, lblNumero, lblPiso});

        PanelCerrarPedidoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ftfLatitud, ftfLongitud, ftfNumero, txtCalle, txtDepartamento, txtDescripcion, txtPiso});

        PanelCerrarPedidoLayout.setVerticalGroup(
            PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCerrarPedidoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCalle)
                    .addComponent(txtCalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNumero)
                    .addComponent(ftfNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPiso)
                    .addComponent(txtPiso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDepartamento)
                    .addComponent(txtDepartamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtLocalidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnModificar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(ftfLatitud, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(ftfLongitud, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelCerrarPedidoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, lblCalle, lblDepartamento, lblNumero, lblPiso});

        PanelCerrarPedidoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ftfLatitud, ftfLongitud, ftfNumero, txtCalle, txtDepartamento, txtDescripcion, txtLocalidad, txtPiso});

        btnCerrarPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btnCerrarPedido.setText("Cerrar Pedido");

        chkEnviarUbicacionFacturacion.setText("Enviar a la dirección de Facturación");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(chkEnviarUbicacionFacturacion))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(PanelCerrarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCerrarPedido)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(chkEnviarUbicacionFacturacion)
                .addGap(7, 7, 7)
                .addComponent(PanelCerrarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCerrarPedido)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
//        DetalleLocalidadGUI gui_DetalleLocalidad = new DetalleLocalidadGUI(ubicacionAModificar);
//        gui_DetalleLocalidad.setModal(true);
//        gui_DetalleLocalidad.setLocationRelativeTo(this);
//        gui_DetalleLocalidad.setVisible(true);
//        ubicacionAModificar.setNombreLocalidad(gui_DetalleLocalidad.getLocalidadSeleccionada().getNombre());
//        ubicacionAModificar.setIdLocalidad(gui_DetalleLocalidad.getLocalidadSeleccionada().getId_Localidad());
//        ubicacionAModificar.setCodigoPostal(gui_DetalleLocalidad.getLocalidadSeleccionada().getCodigoPostal());
//        ubicacionAModificar.setNombreProvincia(gui_DetalleLocalidad.getLocalidadSeleccionada().getNombreProvincia());
//        ubicacionAModificar.setIdProvincia(gui_DetalleLocalidad.getLocalidadSeleccionada().getIdProvincia());
//        txtLocalidad.setText(ubicacionAModificar.getNombreLocalidad() + " (" + ubicacionAModificar.getCodigoPostal() + ") " + ubicacionAModificar.getNombreProvincia());
    }//GEN-LAST:event_btnModificarActionPerformed

    private void txtDepartamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDepartamentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepartamentoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelCerrarPedido;
    private javax.swing.JButton btnCerrarPedido;
    private javax.swing.JButton btnModificar;
    private javax.swing.JCheckBox chkEnviarUbicacionFacturacion;
    private javax.swing.JFormattedTextField ftfLatitud;
    private javax.swing.JFormattedTextField ftfLongitud;
    private javax.swing.JFormattedTextField ftfNumero;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel lblCalle;
    private javax.swing.JLabel lblDepartamento;
    private javax.swing.JLabel lblNumero;
    private javax.swing.JLabel lblPiso;
    private javax.swing.JTextField txtCalle;
    private javax.swing.JTextField txtDepartamento;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtLocalidad;
    private javax.swing.JFormattedTextField txtPiso;
    // End of variables declaration//GEN-END:variables
}
