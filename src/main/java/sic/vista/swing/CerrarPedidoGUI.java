package sic.vista.swing;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ResourceBundle;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Cliente;
import sic.modelo.EmpresaActiva;
import sic.modelo.NuevoPedido;
import sic.modelo.Pedido;
import sic.modelo.Ubicacion;
import sic.modelo.UsuarioActivo;

public class CerrarPedidoGUI extends JDialog {

    private final NuevoPedido nuevoPedido;
    private final Cliente cliente;
    private Pedido pedido;
    private boolean actualizacionExitosa = false;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public CerrarPedidoGUI(NuevoPedido nuevoPedido, Cliente cliente) {
        this.nuevoPedido = nuevoPedido;
        this.cliente = cliente;
        initComponents();
    }

    public CerrarPedidoGUI(Pedido pedido, Cliente cliente) {
        this.nuevoPedido = null;
        this.cliente = cliente;
        this.pedido = pedido;
        initComponents();
    }

    private void lanzarReportePedido(Pedido pedido) {
        if (Desktop.isDesktopSupported()) {
            try {
                byte[] reporte = RestClient.getRestTemplate()
                        .getForObject("/pedidos/" + pedido.getId_Pedido() + "/reporte", byte[].class);
                File f = new File(System.getProperty("user.home") + "/Pedido.pdf");
                Files.write(f.toPath(), reporte);
                Desktop.getDesktop().open(f);
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_IOException"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_plataforma_no_soportada"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isActualizacionExitosa() {
        return this.actualizacionExitosa;
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
        lblDescripcion = new javax.swing.JLabel();
        txtDescripcion = new javax.swing.JTextField();
        lblLocalidad = new javax.swing.JLabel();
        txtLocalidad = new javax.swing.JTextField();
        btnModificarLocalidad = new javax.swing.JButton();
        lblLatitud = new javax.swing.JLabel();
        ftfLatitud = new javax.swing.JFormattedTextField();
        lblLongitud = new javax.swing.JLabel();
        ftfLongitud = new javax.swing.JFormattedTextField();
        btnCerrarPedido = new javax.swing.JButton();
        chkEnviarUbicacionFacturacion = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Detalle de Envío");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

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

        lblDescripcion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDescripcion.setText("Descripción:");

        lblLocalidad.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLocalidad.setText("Localidad:");

        txtLocalidad.setEditable(false);
        txtLocalidad.setEnabled(false);

        btnModificarLocalidad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btnModificarLocalidad.setText("Modificar");
        btnModificarLocalidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarLocalidadActionPerformed(evt);
            }
        });

        lblLatitud.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLatitud.setText("Latitud:");

        ftfLatitud.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("##0.#######"))));

        lblLongitud.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLongitud.setText("Longitud:");

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
                            .addComponent(lblDescripcion)
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
                        .addComponent(lblLocalidad)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLocalidad)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnModificarLocalidad, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelCerrarPedidoLayout.createSequentialGroup()
                        .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLatitud)
                            .addComponent(lblLongitud))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ftfLatitud, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ftfLongitud, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        PanelCerrarPedidoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblCalle, lblDepartamento, lblDescripcion, lblLatitud, lblLocalidad, lblLongitud, lblNumero, lblPiso});

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
                    .addComponent(lblDescripcion)
                    .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLocalidad)
                    .addComponent(txtLocalidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnModificarLocalidad))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLatitud)
                    .addComponent(ftfLatitud, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLongitud)
                    .addComponent(ftfLongitud, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelCerrarPedidoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblCalle, lblDepartamento, lblDescripcion, lblLatitud, lblLocalidad, lblLongitud, lblNumero, lblPiso});

        PanelCerrarPedidoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ftfLatitud, ftfLongitud, ftfNumero, txtCalle, txtDepartamento, txtDescripcion, txtLocalidad, txtPiso});

        btnCerrarPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btnCerrarPedido.setText("Cerrar Pedido");
        btnCerrarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarPedidoActionPerformed(evt);
            }
        });

        chkEnviarUbicacionFacturacion.setText("Enviar a la dirección de Facturación");
        chkEnviarUbicacionFacturacion.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkEnviarUbicacionFacturacionItemStateChanged(evt);
            }
        });

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

    private void btnModificarLocalidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarLocalidadActionPerformed
        DetalleLocalidadGUI gui_DetalleLocalidad = new DetalleLocalidadGUI(this.cliente.getUbicacionEnvio());
        gui_DetalleLocalidad.setModal(true);
        gui_DetalleLocalidad.setLocationRelativeTo(this);
        gui_DetalleLocalidad.setVisible(true);
        this.cliente.getUbicacionEnvio().setNombreLocalidad(gui_DetalleLocalidad.getLocalidadSeleccionada().getNombre());
        this.cliente.getUbicacionEnvio().setIdLocalidad(gui_DetalleLocalidad.getLocalidadSeleccionada().getId_Localidad());
        this.cliente.getUbicacionEnvio().setCodigoPostal(gui_DetalleLocalidad.getLocalidadSeleccionada().getCodigoPostal());
        this.cliente.getUbicacionEnvio().setNombreProvincia(gui_DetalleLocalidad.getLocalidadSeleccionada().getNombreProvincia());
        this.cliente.getUbicacionEnvio().setIdProvincia(gui_DetalleLocalidad.getLocalidadSeleccionada().getIdProvincia());
        txtLocalidad.setText(this.cliente.getUbicacionEnvio().getNombreLocalidad()
                + " (" + this.cliente.getUbicacionEnvio().getCodigoPostal() + ") "
                + this.cliente.getUbicacionEnvio().getNombreProvincia());
    }//GEN-LAST:event_btnModificarLocalidadActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if (this.cliente.getUbicacionEnvio() != null) {
            txtCalle.setText(this.cliente.getUbicacionEnvio().getCalle());
            ftfNumero.setText(this.cliente.getUbicacionEnvio().getNumero().toString());
            if (this.cliente.getUbicacionEnvio().getPiso() != null) {
                txtPiso.setText(this.cliente.getUbicacionEnvio().getPiso().toString());
            }
            if (this.cliente.getUbicacionEnvio().getDepartamento() != null) {
                txtDepartamento.setText(this.cliente.getUbicacionEnvio().getDepartamento());
            }
            if (this.cliente.getUbicacionEnvio().getDescripcion() != null) {
                txtDescripcion.setText(this.cliente.getUbicacionEnvio().getDescripcion());
            }
            if (this.cliente.getUbicacionEnvio().getNombreLocalidad() != null) {
                txtLocalidad.setText(this.cliente.getUbicacionEnvio().getNombreLocalidad() + " " + this.cliente.getUbicacionEnvio().getNombreProvincia());
            }
            if (this.cliente.getUbicacionEnvio().getLatitud() != null) {
                ftfLatitud.setText(this.cliente.getUbicacionEnvio().getLatitud().toString());
            }
            if (this.cliente.getUbicacionEnvio().getLongitud() != null) {
                ftfLongitud.setText(this.cliente.getUbicacionEnvio().getLongitud().toString());
            }
        } else {
            this.cliente.setUbicacionEnvio(new Ubicacion());
        }
    }//GEN-LAST:event_formWindowOpened

    private void btnCerrarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarPedidoActionPerformed
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
                cliente.getUbicacionEnvio().setCalle(txtCalle.getText());
                cliente.getUbicacionEnvio().setNumero(Integer.valueOf(ftfNumero.getText()));
                if (!txtPiso.getText().isEmpty()) {
                    cliente.getUbicacionEnvio().setPiso(Integer.valueOf(txtPiso.getText()));
                }
                if (!txtDepartamento.getText().isEmpty()) {
                    cliente.getUbicacionEnvio().setDepartamento(txtDepartamento.getText());
                }
                if (!txtDescripcion.getText().isEmpty()) {
                    cliente.getUbicacionEnvio().setDescripcion(txtDescripcion.getText());
                }
                if (!ftfLatitud.getText().isEmpty()) {
                    cliente.getUbicacionEnvio().setLatitud(Double.valueOf(ftfLatitud.getText()));
                }
                if (!ftfLongitud.getText().isEmpty()) {
                    cliente.getUbicacionEnvio().setLongitud(Double.valueOf(ftfLongitud.getText()));
                }
                if (cliente.getUbicacionEnvio().getIdUbicacion() == 0L) {
                    RestClient.getRestTemplate().postForObject("/ubicaciones/clientes/" + cliente.getId_Cliente() + "/envio", cliente.getUbicacionEnvio(), Ubicacion.class);
                } else {
                    RestClient.getRestTemplate().put("/ubicaciones", cliente.getUbicacionEnvio());
                }
                try {
                    if (nuevoPedido != null) {
                        Pedido p = RestClient.getRestTemplate().postForObject("/pedidos?idEmpresa="
                                + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                                + "&idUsuario=" + UsuarioActivo.getInstance().getUsuario().getId_Usuario()
                                + "&idCliente=" + cliente.getId_Cliente()
                                + "&usarUbicacionDeFacturacion=" + chkEnviarUbicacionFacturacion.isSelected(), nuevoPedido, Pedido.class);
                        int reply = JOptionPane.showConfirmDialog(this,
                                ResourceBundle.getBundle("Mensajes").getString("mensaje_reporte"),
                                "Aviso", JOptionPane.YES_NO_OPTION);
                        if (reply == JOptionPane.YES_OPTION) {
                            this.lanzarReportePedido(p);
                        }
                    } else {
                        RestClient.getRestTemplate().put("/pedidos?idEmpresa="
                                + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                                + "&idUsuario=" + UsuarioActivo.getInstance().getUsuario().getId_Usuario()
                                + "&idCliente=" + cliente.getId_Cliente()
                                + "&usarUbicacionDeFacturacion=" + chkEnviarUbicacionFacturacion.isSelected(), pedido);
                        this.actualizacionExitosa = true;
                        JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_pedido_actualizado"),
                                "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (RestClientResponseException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (ResourceAccessException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                this.dispose();
            }
        }
    }//GEN-LAST:event_btnCerrarPedidoActionPerformed

    private void chkEnviarUbicacionFacturacionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkEnviarUbicacionFacturacionItemStateChanged
        lblCalle.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        lblDepartamento.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        lblDescripcion.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        lblLatitud.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        lblLocalidad.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        lblLongitud.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        lblNumero.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        lblPiso.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        txtCalle.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        txtDepartamento.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        txtDescripcion.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        txtLocalidad.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        txtPiso.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        ftfLatitud.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        ftfLongitud.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
        ftfNumero.setEnabled(!chkEnviarUbicacionFacturacion.isSelected());
    }//GEN-LAST:event_chkEnviarUbicacionFacturacionItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelCerrarPedido;
    private javax.swing.JButton btnCerrarPedido;
    private javax.swing.JButton btnModificarLocalidad;
    private javax.swing.JCheckBox chkEnviarUbicacionFacturacion;
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
    private javax.swing.JTextField txtCalle;
    private javax.swing.JTextField txtDepartamento;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtLocalidad;
    private javax.swing.JFormattedTextField txtPiso;
    // End of variables declaration//GEN-END:variables
}
