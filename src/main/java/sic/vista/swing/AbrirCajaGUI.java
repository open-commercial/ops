package sic.vista.swing;

import java.math.BigDecimal;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Caja;
import sic.modelo.EmpresaActiva;
import sic.modelo.UsuarioActivo;
import sic.modelo.EstadoCaja;

public class AbrirCajaGUI extends JDialog {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
    public AbrirCajaGUI() {
        this.initComponents();        
    }
    
    private Caja construirCaja(BigDecimal monto) {
        Caja caja = new Caja();
        caja.setEstado(EstadoCaja.ABIERTA);
        caja.setObservacion("Apertura De Caja");
        caja.setEmpresa(EmpresaActiva.getInstance().getEmpresa());
        caja.setSaldoInicial(monto);
        caja.setSaldoSistema(monto);
        caja.setSaldoReal(BigDecimal.ZERO);
        caja.setUsuarioAbreCaja(UsuarioActivo.getInstance().getUsuario());
        return caja;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        p_container = new javax.swing.JPanel();
        lbl_monto = new javax.swing.JLabel();
        ftxt_Monto = new javax.swing.JFormattedTextField();
        btn_AbrirCaja = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Abrir Caja");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        p_container.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_monto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_monto.setText("Monto Apertura:");

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

        javax.swing.GroupLayout p_containerLayout = new javax.swing.GroupLayout(p_container);
        p_container.setLayout(p_containerLayout);
        p_containerLayout.setHorizontalGroup(
            p_containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p_containerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_monto, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ftxt_Monto, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        p_containerLayout.setVerticalGroup(
            p_containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p_containerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(p_containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_monto)
                    .addComponent(ftxt_Monto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        p_containerLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ftxt_Monto, lbl_monto});

        btn_AbrirCaja.setForeground(java.awt.Color.blue);
        btn_AbrirCaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AbrirCaja_16x16.png"))); // NOI18N
        btn_AbrirCaja.setText("Abrir Caja");
        btn_AbrirCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AbrirCajaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(p_container, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_AbrirCaja))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(p_container, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_AbrirCaja)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ftxt_MontoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ftxt_MontoKeyTyped
        char tecla = evt.getKeyChar();
        if (Character.isLetter(tecla)) {
            evt.consume();
        }
    }//GEN-LAST:event_ftxt_MontoKeyTyped

    private void btn_AbrirCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AbrirCajaActionPerformed
        if (ftxt_Monto.getValue() == null) {
            ftxt_Monto.setValue(0);
        }
        try {
            RestClient.getRestTemplate().postForObject("/cajas",
                    this.construirCaja(new BigDecimal(ftxt_Monto.getValue().toString())),
                    Caja.class);
            this.dispose();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_AbrirCajaActionPerformed

    private void ftxt_MontoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxt_MontoFocusGained
        SwingUtilities.invokeLater(() -> {
            ftxt_Monto.selectAll();
        });
    }//GEN-LAST:event_ftxt_MontoFocusGained

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setModal(true);
        ImageIcon iconoVentana = new ImageIcon(AbrirCajaGUI.class.getResource("/sic/icons/Caja_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_AbrirCaja;
    private javax.swing.JFormattedTextField ftxt_Monto;
    private javax.swing.JLabel lbl_monto;
    private javax.swing.JPanel p_container;
    // End of variables declaration//GEN-END:variables

}
