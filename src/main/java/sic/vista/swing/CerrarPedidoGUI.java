package sic.vista.swing;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
import sic.modelo.Cliente;
import sic.modelo.Sucursal;
import sic.modelo.PedidoDTO;
import sic.modelo.NuevoRenglonPedido;
import sic.modelo.Pedido;
import sic.modelo.SucursalActiva;
import sic.modelo.TipoDeEnvio;

public class CerrarPedidoGUI extends JDialog {

    private PedidoDTO nuevoPedido;
    private final Cliente cliente;
    private Pedido pedido;
    private boolean operacionExitosa = false;
    private List<Sucursal> sucursales;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public CerrarPedidoGUI(PedidoDTO nuevoPedido, Cliente cliente) {
        this.nuevoPedido = nuevoPedido;
        this.cliente = cliente;
        this.initComponents();
        this.setIcon(); 
    }

    public CerrarPedidoGUI(Pedido pedido, Cliente cliente) {
        this.nuevoPedido = null;
        this.cliente = cliente;
        this.pedido = pedido;
        this.initComponents();
        this.setIcon(); 
    }
    
    public boolean isOperacionExitosa() {
        return this.operacionExitosa;
    }
    
    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(CerrarVentaGUI.class.getResource("/sic/icons/SIC_24_square.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void lanzarReportePedido(Pedido pedido) {
        if (Desktop.isDesktopSupported()) {
            try {
                byte[] reporte = RestClient.getRestTemplate()
                        .getForObject("/pedidos/" + pedido.getIdPedido() + "/reporte", byte[].class);
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

    private void cargarSucursalesConPuntoDeRetiro() {
        try {
            sucursales = Arrays.asList(RestClient.getRestTemplate().getForObject("/sucursales?puntoDeRetiro=true", Sucursal[].class));
            sucursales.stream().forEach(e -> {
                cmbSucursales.addItem(e.getNombre() + ((e.getUbicacion() != null) ? (" (" + e.getUbicacion() + ")") : ""));
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
        cmbSucursales = new javax.swing.JComboBox<>();
        lblDetalleUbicacionFacturacion = new javax.swing.JLabel();
        lblDetalleUbicacionEnvio = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cerrar Pedido");
        setResizable(false);
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
                    .addComponent(cmbSucursales, javax.swing.GroupLayout.Alignment.TRAILING, 0, 439, Short.MAX_VALUE)
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
                    .addComponent(cmbSucursales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            this.cargarSucursalesConPuntoDeRetiro();
            if (this.cliente.getUbicacionEnvio() != null) {
                lblDetalleUbicacionEnvio.setText(this.cliente.getUbicacionEnvio().toString());
                if (!rbDireccionFacturacion.isSelected()) {
                    rbDireccionEnvio.setSelected(true);
                }
            } else {
                rbDireccionEnvio.setEnabled(false);
                lblDetalleUbicacionEnvio.setEnabled(false);
            }
            if (this.cliente.getUbicacionFacturacion() != null) {
                lblDetalleUbicacionFacturacion.setText(this.cliente.getUbicacionFacturacion().toString());
                if (!rbRetiroEnSucursal.isSelected()) {
                    rbDireccionFacturacion.setSelected(true);
                }
            } else {
                rbDireccionFacturacion.setEnabled(false);
                lblDetalleUbicacionFacturacion.setEnabled(false);
            }
            if (sucursales.isEmpty()) {
                cmbSucursales.setEnabled(false);
                rbRetiroEnSucursal.setEnabled(false);
            } else {
                rbRetiroEnSucursal.setSelected(true);
            }
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
            TipoDeEnvio tipoDeEnvio = null;
            if (rbRetiroEnSucursal.isSelected()) {
                tipoDeEnvio = TipoDeEnvio.RETIRO_EN_SUCURSAL;
            }
            if (rbDireccionFacturacion.isSelected()) {
                tipoDeEnvio = TipoDeEnvio.USAR_UBICACION_FACTURACION;
            }
            if (rbDireccionEnvio.isSelected()) {
                tipoDeEnvio = TipoDeEnvio.USAR_UBICACION_ENVIO;
            }
            if (tipoDeEnvio != null) {
                if (nuevoPedido != null) {
                    nuevoPedido.setIdSucursal(rbRetiroEnSucursal.isSelected() ? 
                            sucursales.get(cmbSucursales.getSelectedIndex()).getIdSucursal() : SucursalActiva.getInstance().getSucursal().getIdSucursal());
                    nuevoPedido.setIdCliente(cliente.getIdCliente());
                    nuevoPedido.setTipoDeEnvio(tipoDeEnvio);
                    Pedido p = RestClient.getRestTemplate().postForObject("/pedidos", nuevoPedido, Pedido.class);
                    this.operacionExitosa = true;
                    int reply = JOptionPane.showConfirmDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_reporte"),
                            "Aviso", JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        this.lanzarReportePedido(p);
                    }
                } else {
                    nuevoPedido = new PedidoDTO();
                    nuevoPedido.setIdPedido(pedido.getIdPedido());
                    nuevoPedido.setIdSucursal(rbRetiroEnSucursal.isSelected() ? 
                            sucursales.get(cmbSucursales.getSelectedIndex()).getIdSucursal() : null);
                    nuevoPedido.setObservaciones(pedido.getObservaciones());
                    nuevoPedido.setRecargoPorcentaje(pedido.getRecargoPorcentaje());
                    nuevoPedido.setDescuentoPorcentaje(pedido.getDescuentoPorcentaje());
                    List<NuevoRenglonPedido> nuevosRenglonesPedido = new ArrayList();
                    pedido.getRenglones().forEach(r -> nuevosRenglonesPedido.add(
                            new NuevoRenglonPedido(r.getIdProductoItem(), r.getCantidad())));
                    nuevoPedido.setRenglones(nuevosRenglonesPedido);
                    nuevoPedido.setTipoDeEnvio(tipoDeEnvio);                    
                    RestClient.getRestTemplate().put("/pedidos", nuevoPedido);
                    this.operacionExitosa = true;
                    JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_pedido_actualizado"),
                            "Aviso", JOptionPane.INFORMATION_MESSAGE);
                }
                this.dispose();
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCerrarPedidoActionPerformed

    private void rbDireccionFacturacionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbDireccionFacturacionItemStateChanged
        cmbSucursales.setEnabled(!rbDireccionFacturacion.isSelected());
        lblDetalleUbicacionEnvio.setEnabled(!rbDireccionFacturacion.isSelected());
        lblDetalleUbicacionFacturacion.setEnabled(rbDireccionFacturacion.isSelected());
        if (rbDireccionFacturacion.isSelected()) {
            if (this.cliente.getUbicacionFacturacion() != null) {
                lblDetalleUbicacionFacturacion.setText(this.cliente.getUbicacionFacturacion().toString());
            }
        }
    }//GEN-LAST:event_rbDireccionFacturacionItemStateChanged

    private void rbRetiroEnSucursalItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbRetiroEnSucursalItemStateChanged
        cmbSucursales.setEnabled(rbRetiroEnSucursal.isSelected());
        lblDetalleUbicacionEnvio.setEnabled(!rbRetiroEnSucursal.isSelected());
        lblDetalleUbicacionFacturacion.setEnabled(!rbRetiroEnSucursal.isSelected());
    }//GEN-LAST:event_rbRetiroEnSucursalItemStateChanged

    private void rbDireccionEnvioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbDireccionEnvioItemStateChanged
        cmbSucursales.setEnabled(!rbDireccionEnvio.isSelected());
        lblDetalleUbicacionEnvio.setEnabled(rbDireccionEnvio.isSelected());
        lblDetalleUbicacionFacturacion.setEnabled(!rbDireccionEnvio.isSelected());
        if (this.cliente.getUbicacionEnvio() != null) {
            lblDetalleUbicacionEnvio.setText(this.cliente.getUbicacionEnvio().toString());
        }
    }//GEN-LAST:event_rbDireccionEnvioItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarPedido;
    private javax.swing.JComboBox<String> cmbSucursales;
    private javax.swing.ButtonGroup grupoOpcionesEnvio;
    private javax.swing.JLabel lblDetalleUbicacionEnvio;
    private javax.swing.JLabel lblDetalleUbicacionFacturacion;
    private javax.swing.JPanel pnlCerrarPedido;
    private javax.swing.JRadioButton rbDireccionEnvio;
    private javax.swing.JRadioButton rbDireccionFacturacion;
    private javax.swing.JRadioButton rbRetiroEnSucursal;
    // End of variables declaration//GEN-END:variables
}
