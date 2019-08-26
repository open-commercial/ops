package sic.vista.swing;

import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.ResourceBundle;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Cliente;
import sic.modelo.EmpresaActiva;
import sic.modelo.NotaCredito;
import sic.modelo.NuevaNotaCreditoSinFactura;
import sic.modelo.Proveedor;
import sic.modelo.TipoDeComprobante;

public class NuevaNotaCreditoSinFacturaGUI extends JDialog {
    
    private final Long idCliente;
    private final Long idProveedor;
    private NotaCredito notaCreditoCalculada;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public NuevaNotaCreditoSinFacturaGUI(Cliente cliente) {
        this.initComponents();
        this.idCliente = cliente.getId_Cliente();
        this.idProveedor = null;
    }

    public NuevaNotaCreditoSinFacturaGUI(Proveedor proveedor) {
        this.initComponents();
        this.idCliente = null;
        this.idProveedor = proveedor.getId_Proveedor();
    }

    public NotaCredito getNotaCreditoCalculadaSinFactura() {
        return notaCreditoCalculada;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_Parametros = new javax.swing.JPanel();
        lblDescripcion = new javax.swing.JLabel();
        ftxtDetalle = new javax.swing.JTextField();
        lbl_Monto = new javax.swing.JLabel();
        ftxt_Monto = new javax.swing.JFormattedTextField();
        cmbTipoDeComprobante = new javax.swing.JComboBox();
        lbl_TipoDeComprobante = new javax.swing.JLabel();
        lbl_Aceptar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nueva Nota de Credito");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        pnl_Parametros.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblDescripcion.setForeground(java.awt.Color.red);
        lblDescripcion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDescripcion.setText("* Descripcion:");

        lbl_Monto.setForeground(java.awt.Color.red);
        lbl_Monto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Monto.setText("* Monto:");

        ftxt_Monto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxt_Monto.setText("0");
        ftxt_Monto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ftxt_MontoFocusGained(evt);
            }
        });
        ftxt_Monto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ftxt_MontoKeyTyped(evt);
            }
        });

        lbl_TipoDeComprobante.setForeground(java.awt.Color.red);
        lbl_TipoDeComprobante.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_TipoDeComprobante.setText("* Tipo de Nota:");

        javax.swing.GroupLayout pnl_ParametrosLayout = new javax.swing.GroupLayout(pnl_Parametros);
        pnl_Parametros.setLayout(pnl_ParametrosLayout);
        pnl_ParametrosLayout.setHorizontalGroup(
            pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDescripcion)
                    .addComponent(lbl_TipoDeComprobante)
                    .addComponent(lbl_Monto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ftxtDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbTipoDeComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ftxt_Monto, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnl_ParametrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmbTipoDeComprobante, ftxtDetalle, ftxt_Monto});

        pnl_ParametrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblDescripcion, lbl_Monto, lbl_TipoDeComprobante});

        pnl_ParametrosLayout.setVerticalGroup(
            pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_TipoDeComprobante)
                    .addComponent(cmbTipoDeComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDescripcion)
                    .addComponent(ftxtDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Monto)
                    .addComponent(ftxt_Monto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnl_ParametrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmbTipoDeComprobante, ftxtDetalle, ftxt_Monto});

        pnl_ParametrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblDescripcion, lbl_Monto, lbl_TipoDeComprobante});

        lbl_Aceptar.setForeground(java.awt.Color.blue);
        lbl_Aceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        lbl_Aceptar.setText("Aceptar");
        lbl_Aceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lbl_AceptarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_Aceptar)
                    .addComponent(pnl_Parametros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_Parametros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_Aceptar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setModal(true);
        try {
            cmbTipoDeComprobante.removeAllItems();
            TipoDeComprobante[] tiposDeComprobante = null;
            if (this.idCliente != null) {
                tiposDeComprobante = RestClient.getRestTemplate()
                        .getForObject("/notas/clientes/tipos/credito?idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                                + "&idCliente=" + this.idCliente, TipoDeComprobante[].class);
            }
            if (this.idProveedor != null) {
                tiposDeComprobante = RestClient.getRestTemplate()
                        .getForObject("/notas/proveedores/tipos/credito?idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                                + "&idProveedor=" + this.idProveedor, TipoDeComprobante[].class);
            }
            if (tiposDeComprobante != null) {
                for (int i = 0; tiposDeComprobante.length > i; i++) {
                    cmbTipoDeComprobante.addItem(tiposDeComprobante[i]);
                }
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

    private void lbl_AceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lbl_AceptarActionPerformed
        if (ftxt_Monto.getValue() == null) {
            ftxt_Monto.setValue(0.00);
        }
        if (ftxtDetalle.getText() == null) {
            ftxtDetalle.setText("");
        }
        try {
            NuevaNotaCreditoSinFactura nuevaNotaCreditoSinFactura = NuevaNotaCreditoSinFactura
                    .builder()
                    .idCliente(idCliente)
                    .idProveedor(idProveedor)
                    .idEmpresa(EmpresaActiva.getInstance().getEmpresa().getId_Empresa())
                    .monto(new BigDecimal(ftxt_Monto.getValue().toString()))
                    .tipo(((TipoDeComprobante) cmbTipoDeComprobante.getSelectedItem()))
                    .detalle(ftxtDetalle.getText().trim())
                    .build();
            notaCreditoCalculada = RestClient.getRestTemplate().postForObject("/notas/credito/calculos-sin-factura", nuevaNotaCreditoSinFactura, NotaCredito.class);
            this.dispose();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_lbl_AceptarActionPerformed

    private void ftxt_MontoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxt_MontoFocusGained
        SwingUtilities.invokeLater(() -> {
            ftxt_Monto.selectAll();
        });
    }//GEN-LAST:event_ftxt_MontoFocusGained

    private void ftxt_MontoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ftxt_MontoKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_ftxt_MontoKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbTipoDeComprobante;
    private javax.swing.JTextField ftxtDetalle;
    private javax.swing.JFormattedTextField ftxt_Monto;
    private javax.swing.JLabel lblDescripcion;
    private javax.swing.JButton lbl_Aceptar;
    private javax.swing.JLabel lbl_Monto;
    private javax.swing.JLabel lbl_TipoDeComprobante;
    private javax.swing.JPanel pnl_Parametros;
    // End of variables declaration//GEN-END:variables
}
