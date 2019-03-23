package sic.vista.swing;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
import sic.modelo.Cliente;
import sic.modelo.Empresa;
import sic.modelo.EmpresaActiva;
import sic.modelo.NuevoPedido;
import sic.modelo.Pedido;
import sic.modelo.TipoDeEnvio;
import sic.modelo.UsuarioActivo;

public class CerrarPedidoGUI extends JDialog {

    private final NuevoPedido nuevoPedido;
    private final Cliente cliente;
    private Pedido pedido;
    private boolean operacionExitosa = false;
    private List<Empresa> empresas;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public CerrarPedidoGUI(NuevoPedido nuevoPedido, Cliente cliente) {
        this.nuevoPedido = nuevoPedido;
        this.cliente = cliente;
        initComponents();
        this.setIcon(); 
    }

    public CerrarPedidoGUI(Pedido pedido, Cliente cliente) {
        this.nuevoPedido = null;
        this.cliente = cliente;
        this.pedido = pedido;
        initComponents();
        this.setIcon(); 
    }
    
    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(CerrarVentaGUI.class.getResource("/sic/icons/SIC_24_square.png"));
        this.setIconImage(iconoVentana.getImage());
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

    public boolean isOperacionExitosa() {
        return this.operacionExitosa;
    }

    private void cargarEmpresas() {
        try {
            empresas = Arrays.asList(RestClient.getRestTemplate().getForObject("/empresas", Empresa[].class));
            empresas.stream().forEach(e -> {
                cmbEmpresas.addItem(e.getNombre() + " (" + e.getDetalleUbicacion() + ")");
            });
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
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grupoOpcionesEnvio = new javax.swing.ButtonGroup();
        btnCerrarPedido = new javax.swing.JButton();
        pnlCerrarPedido = new javax.swing.JPanel();
        rbRetiroEnSucursal = new javax.swing.JRadioButton();
        rbDireccionFacturacion = new javax.swing.JRadioButton();
        rbDireccionEnvio = new javax.swing.JRadioButton();
        cmbEmpresas = new javax.swing.JComboBox<>();
        lblDetalleUbicacionFacturacion = new javax.swing.JLabel();
        lblDetalleUbicacionEnvio = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cerrar Pedido");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        btnCerrarPedido.setForeground(java.awt.Color.blue);
        btnCerrarPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btnCerrarPedido.setText("Finalizar");
        btnCerrarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarPedidoActionPerformed(evt);
            }
        });

        pnlCerrarPedido.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        grupoOpcionesEnvio.add(rbRetiroEnSucursal);
        rbRetiroEnSucursal.setText("Retiro en sucursal:");
        rbRetiroEnSucursal.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbRetiroEnSucursalItemStateChanged(evt);
            }
        });

        grupoOpcionesEnvio.add(rbDireccionFacturacion);
        rbDireccionFacturacion.setText("Usar ubicación de facturación:");
        rbDireccionFacturacion.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbDireccionFacturacionItemStateChanged(evt);
            }
        });

        grupoOpcionesEnvio.add(rbDireccionEnvio);
        rbDireccionEnvio.setText("Usar ubicación de envío:");
        rbDireccionEnvio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbDireccionEnvioItemStateChanged(evt);
            }
        });

        lblDetalleUbicacionFacturacion.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDetalleUbicacionFacturacion.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        lblDetalleUbicacionFacturacion.setEnabled(false);

        lblDetalleUbicacionEnvio.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDetalleUbicacionEnvio.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        lblDetalleUbicacionEnvio.setEnabled(false);

        javax.swing.GroupLayout pnlCerrarPedidoLayout = new javax.swing.GroupLayout(pnlCerrarPedido);
        pnlCerrarPedido.setLayout(pnlCerrarPedidoLayout);
        pnlCerrarPedidoLayout.setHorizontalGroup(
            pnlCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCerrarPedidoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(rbDireccionFacturacion)
                    .addComponent(rbDireccionEnvio)
                    .addComponent(rbRetiroEnSucursal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbEmpresas, javax.swing.GroupLayout.Alignment.TRAILING, 0, 439, Short.MAX_VALUE)
                    .addComponent(lblDetalleUbicacionFacturacion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDetalleUbicacionEnvio, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlCerrarPedidoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {rbDireccionEnvio, rbDireccionFacturacion, rbRetiroEnSucursal});

        pnlCerrarPedidoLayout.setVerticalGroup(
            pnlCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCerrarPedidoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(rbRetiroEnSucursal)
                    .addComponent(cmbEmpresas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDetalleUbicacionFacturacion)
                    .addComponent(rbDireccionFacturacion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCerrarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(rbDireccionEnvio)
                    .addComponent(lblDetalleUbicacionEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlCerrarPedidoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {rbDireccionEnvio, rbDireccionFacturacion, rbRetiroEnSucursal});

        pnlCerrarPedidoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblDetalleUbicacionEnvio, lblDetalleUbicacionFacturacion});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlCerrarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCerrarPedido)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlCerrarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCerrarPedido)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
            if (this.cliente.getIdUbicacionEnvio() != null) {
                lblDetalleUbicacionEnvio.setText(this.cliente.getDetalleUbicacionEnvio());
            } else {
                rbDireccionEnvio.setEnabled(false);
            }
            if (this.cliente.getIdUbicacionFacturacion() != null) {
                lblDetalleUbicacionFacturacion.setText(this.cliente.getDetalleUbicacionFacturacion());
            } else {
                rbDireccionFacturacion.setEnabled(false);
            }
            this.cargarEmpresas();
            rbRetiroEnSucursal.setSelected(true);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_formWindowOpened

    private void btnCerrarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarPedidoActionPerformed
        try {
//            if (this.ubicacionDeFacturacion != null && this.ubicacionDeFacturacion.getIdUbicacion() == 0L) {
//                RestClient.getRestTemplate().postForObject("/ubicaciones/clientes/" + cliente.getId_Cliente() + "/facturacion", this.ubicacionDeFacturacion, Ubicacion.class);
//            } else {
//                RestClient.getRestTemplate().put("/ubicaciones", this.ubicacionDeFacturacion);
//            }
//            if (this.ubicacionDeEnvio != null && this.ubicacionDeEnvio.getIdUbicacion() == 0L) {
//                RestClient.getRestTemplate().postForObject("/ubicaciones/clientes/" + cliente.getId_Cliente() + "/envio", this.ubicacionDeEnvio, Ubicacion.class);
//            } else {
//                RestClient.getRestTemplate().put("/ubicaciones", this.ubicacionDeEnvio);
//            }
            TipoDeEnvio tipoDeEnvio;
            Long idEmpresa = null;
            if (rbDireccionFacturacion.isSelected()) {
                tipoDeEnvio = TipoDeEnvio.USAR_UBICACION_FACTURACION;
            } else if (rbDireccionEnvio.isSelected()) {
                tipoDeEnvio = TipoDeEnvio.USAR_UBICACION_ENVIO;
            } else {
                tipoDeEnvio = TipoDeEnvio.RETIRO_EN_SUCURSAL;
                idEmpresa = empresas.get(cmbEmpresas.getSelectedIndex()).getId_Empresa();
            }
            if (nuevoPedido != null) {
                Pedido p = RestClient.getRestTemplate().postForObject("/pedidos?idEmpresa="
                        + (idEmpresa != null ? idEmpresa : EmpresaActiva.getInstance().getEmpresa().getId_Empresa())
                        + "&idUsuario=" + UsuarioActivo.getInstance().getUsuario().getId_Usuario()
                        + "&idCliente=" + cliente.getId_Cliente()
                        + "&tipoDeEnvio=" + tipoDeEnvio, nuevoPedido, Pedido.class);
                this.operacionExitosa = true;
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
                        + "&tipoDeEnvio=" + tipoDeEnvio, pedido);
                this.operacionExitosa = true;
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
    }//GEN-LAST:event_btnCerrarPedidoActionPerformed

    private void rbDireccionFacturacionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbDireccionFacturacionItemStateChanged
        cmbEmpresas.setEnabled(!rbDireccionFacturacion.isSelected());
        lblDetalleUbicacionEnvio.setEnabled(!rbDireccionFacturacion.isSelected());
        lblDetalleUbicacionFacturacion.setEnabled(rbDireccionFacturacion.isSelected());
        if (rbDireccionFacturacion.isSelected()) {
            if (this.cliente.getDetalleUbicacionFacturacion() != null) {
                lblDetalleUbicacionFacturacion.setText(this.cliente.getDetalleUbicacionFacturacion());
            }
        }
    }//GEN-LAST:event_rbDireccionFacturacionItemStateChanged

    private void rbRetiroEnSucursalItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbRetiroEnSucursalItemStateChanged
        cmbEmpresas.setEnabled(rbRetiroEnSucursal.isSelected());
        lblDetalleUbicacionEnvio.setEnabled(!rbRetiroEnSucursal.isSelected());
        lblDetalleUbicacionFacturacion.setEnabled(!rbRetiroEnSucursal.isSelected());
    }//GEN-LAST:event_rbRetiroEnSucursalItemStateChanged

    private void rbDireccionEnvioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbDireccionEnvioItemStateChanged
        cmbEmpresas.setEnabled(!rbDireccionEnvio.isSelected());
        lblDetalleUbicacionEnvio.setEnabled(rbDireccionEnvio.isSelected());
        lblDetalleUbicacionFacturacion.setEnabled(!rbDireccionEnvio.isSelected());
        if (this.cliente.getDetalleUbicacionEnvio() != null) {
            lblDetalleUbicacionEnvio.setText(this.cliente.getDetalleUbicacionEnvio());
        }
    }//GEN-LAST:event_rbDireccionEnvioItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarPedido;
    private javax.swing.JComboBox<String> cmbEmpresas;
    private javax.swing.ButtonGroup grupoOpcionesEnvio;
    private javax.swing.JLabel lblDetalleUbicacionEnvio;
    private javax.swing.JLabel lblDetalleUbicacionFacturacion;
    private javax.swing.JPanel pnlCerrarPedido;
    private javax.swing.JRadioButton rbDireccionEnvio;
    private javax.swing.JRadioButton rbDireccionFacturacion;
    private javax.swing.JRadioButton rbRetiroEnSucursal;
    // End of variables declaration//GEN-END:variables
}
