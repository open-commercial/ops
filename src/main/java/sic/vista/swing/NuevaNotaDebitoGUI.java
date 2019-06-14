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
import sic.modelo.NotaDebito;
import sic.modelo.NuevaNotaDebitoDeRecibo;
import sic.modelo.NuevaNotaDebitoSinRecibo;
import sic.modelo.Proveedor;
import sic.modelo.TipoDeComprobante;

public class NuevaNotaDebitoGUI extends JDialog {
    
    private final Cliente cliente;
    private final Proveedor proveedor;
    private NotaDebito notaDebitoCalculada;
    private Long idRecibo;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public NuevaNotaDebitoGUI(Cliente cliente) {
        this.initComponents();
        this.cliente = cliente;
        this.proveedor = null;
    }

    public NuevaNotaDebitoGUI(Proveedor proveedor) {
        this.initComponents();
        this.cliente = null;
        this.proveedor = proveedor;
    }

    public NuevaNotaDebitoGUI(Cliente cliente, Long idRecibo) {
        this.initComponents();
        this.cliente = cliente;
        this.proveedor = null;
        this.idRecibo = idRecibo;
    }

    public NuevaNotaDebitoGUI(Proveedor proveedor, Long idRecibo) {
        this.initComponents();
        this.cliente = null;
        this.proveedor = proveedor;
        this.idRecibo = idRecibo;
    }
    
    public NotaDebito getNotaDebitoCalculada() {
        return notaDebitoCalculada;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lbl_TipoDeComprobante = new javax.swing.JLabel();
        cmbTipoDeComprobante = new javax.swing.JComboBox();
        lblGastoAdministrativo = new javax.swing.JLabel();
        ftxt_Monto = new javax.swing.JFormattedTextField();
        lbl_Aceptar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nueva Nota de Debito");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_TipoDeComprobante.setForeground(java.awt.Color.red);
        lbl_TipoDeComprobante.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_TipoDeComprobante.setText("* Tipo de Nota:");

        lblGastoAdministrativo.setForeground(java.awt.Color.red);
        lblGastoAdministrativo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblGastoAdministrativo.setText("* Gasto Administrativo:");

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lbl_TipoDeComprobante)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(lblGastoAdministrativo, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmbTipoDeComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ftxt_Monto, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblGastoAdministrativo, lbl_TipoDeComprobante});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_TipoDeComprobante)
                    .addComponent(cmbTipoDeComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ftxt_Monto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblGastoAdministrativo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lbl_Aceptar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_Aceptar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void lbl_AceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lbl_AceptarActionPerformed
        if (ftxt_Monto.getValue() == null) {
            ftxt_Monto.setValue(0.00);
        }
        try {
            if (idRecibo == null) {
                NuevaNotaDebitoSinRecibo nuevaNotaCreditoSinRecibo = NuevaNotaDebitoSinRecibo
                        .builder()
                        .idCliente(cliente != null ? cliente.getId_Cliente() : null)
                        .idProveedor(proveedor != null ? proveedor.getId_Proveedor() : null)
                        .gastoAdministrativo(new BigDecimal(ftxt_Monto.getValue().toString()))
                        .tipoDeComprobante(((TipoDeComprobante) cmbTipoDeComprobante.getSelectedItem()))
                        .build();
                notaDebitoCalculada = RestClient.getRestTemplate().postForObject("/notas/debito/calculos-sin-recibo", nuevaNotaCreditoSinRecibo, NotaDebito.class);
            } else {
                NuevaNotaDebitoDeRecibo nuevaNotaDebitoDeRecibo = NuevaNotaDebitoDeRecibo
                        .builder()
                        .idRecibo(idRecibo)
                        .gastoAdministrativo(new BigDecimal(ftxt_Monto.getValue().toString()))
                        //.motivo("Tiene una deuda muy vieja que no paga.")
                        .tipoDeComprobante(((TipoDeComprobante) cmbTipoDeComprobante.getSelectedItem()))
                        .build();
                notaDebitoCalculada = RestClient.getRestTemplate().postForObject("/notas/debito/calculos", nuevaNotaDebitoDeRecibo, NotaDebito.class);
            } 
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

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setModal(true);
        try {
            cmbTipoDeComprobante.removeAllItems();
            TipoDeComprobante[] tiposDeComprobante = null;
            if (cliente != null) {
                tiposDeComprobante = RestClient.getRestTemplate()
                        .getForObject("/notas/clientes/tipos/debito?idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                                + "&idCliente=" + this.cliente.getId_Cliente(), TipoDeComprobante[].class);
            }
            if (proveedor != null) {
                tiposDeComprobante = RestClient.getRestTemplate()
                        .getForObject("/notas/proveedores/tipos/debito?idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                                + "&idProveedor=" + this.proveedor.getId_Proveedor(), TipoDeComprobante[].class);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbTipoDeComprobante;
    private javax.swing.JFormattedTextField ftxt_Monto;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblGastoAdministrativo;
    private javax.swing.JButton lbl_Aceptar;
    private javax.swing.JLabel lbl_TipoDeComprobante;
    // End of variables declaration//GEN-END:variables
}
