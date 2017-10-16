package sic.vista.swing;

import javax.swing.JDialog;
import javax.swing.JTextArea;

public class ObservacionesGUI extends JDialog {

    public ObservacionesGUI(JDialog parent, boolean modal, String texto) {
        super(parent, modal);
        this.initComponents();
        this.setLocationRelativeTo(parent);
        this.txta_Observaciones.setText(texto);
    }

    public JTextArea getTxta_Observaciones() {
        return txta_Observaciones;
    }    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_Aceptar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txta_Observaciones = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Observaciones");

        btn_Aceptar.setForeground(java.awt.Color.blue);
        btn_Aceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btn_Aceptar.setText("Aceptar");
        btn_Aceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AceptarActionPerformed(evt);
            }
        });

        txta_Observaciones.setColumns(20);
        txta_Observaciones.setLineWrap(true);
        txta_Observaciones.setRows(5);
        jScrollPane1.setViewportView(txta_Observaciones);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_Aceptar, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Aceptar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_AceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AceptarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btn_AceptarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Aceptar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txta_Observaciones;
    // End of variables declaration//GEN-END:variables
}
