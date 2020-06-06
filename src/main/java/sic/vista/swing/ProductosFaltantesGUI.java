package sic.vista.swing;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.modelo.ProductoFaltante;
import sic.util.DecimalesRenderer;

public class ProductosFaltantesGUI extends JDialog {

    private ModeloTabla modeloTablaFaltantes = new ModeloTabla();
    private final List<ProductoFaltante> faltantes;
    private final Dimension sizeDialog = new Dimension(920, 500);
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ProductosFaltantesGUI(List<ProductoFaltante> faltantes) {
        super.setModal(true);
        this.initComponents();
        this.setIcon();
        this.faltantes = faltantes;
    }
    
    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(ProductosFaltantesGUI.class.getResource("/sic/icons/SIC_24_square.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void setColumnas() {
        //nombres de columnas
        String[] encabezados = new String[4];
        encabezados[0] = "Codigo";
        encabezados[1] = "Descripcion";
        encabezados[2] = "Cant. Solicitada";
        encabezados[3] = "Cant. Disponible";
        modeloTablaFaltantes.setColumnIdentifiers(encabezados);
        tbl_Faltantes.setModel(modeloTablaFaltantes);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaFaltantes.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        tipos[2] = BigDecimal.class;
        tipos[3] = BigDecimal.class;
        modeloTablaFaltantes.setClaseColumnas(tipos);
        tbl_Faltantes.getTableHeader().setReorderingAllowed(false);
        tbl_Faltantes.getTableHeader().setResizingAllowed(true);                
        //tamanios de columnas
        tbl_Faltantes.getColumnModel().getColumn(0).setPreferredWidth(130);
        tbl_Faltantes.getColumnModel().getColumn(0).setMaxWidth(130);
        tbl_Faltantes.getColumnModel().getColumn(1).setPreferredWidth(300);
        tbl_Faltantes.getColumnModel().getColumn(2).setPreferredWidth(120);
        tbl_Faltantes.getColumnModel().getColumn(2).setMaxWidth(120);
        tbl_Faltantes.getColumnModel().getColumn(3).setPreferredWidth(120);
        tbl_Faltantes.getColumnModel().getColumn(3).setMaxWidth(120);
        //renderers
        tbl_Faltantes.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());
    }

    private void limpiarJTables() {
        modeloTablaFaltantes = new ModeloTabla();
        tbl_Faltantes.setModel(modeloTablaFaltantes);
        this.setColumnas();
    }

    private void cargarResultadosAlTable() {
        this.limpiarJTables();
        Object[] fila = new Object[4];
        faltantes.forEach(faltante -> {
            fila[0] = faltante.getCodigo();
            fila[1] = faltante.getDescripcion();
            fila[2] = faltante.getCantidadSolicitada();
            fila[3] = faltante.getCantidadDisponible();
            modeloTablaFaltantes.addRow(fila);
        });
        tbl_Faltantes.setModel(modeloTablaFaltantes);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl_faltantes = new javax.swing.JLabel();
        sp_faltantes = new javax.swing.JScrollPane();
        tbl_Faltantes = new javax.swing.JTable();
        btn_volver = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Productos Faltantes");
        setIconImage(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        lbl_faltantes.setText("Los siguientes productos no poseen stock suficiente:");

        tbl_Faltantes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        sp_faltantes.setViewportView(tbl_Faltantes);

        btn_volver.setForeground(java.awt.Color.blue);
        btn_volver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/ArrowLeft_16x16.png"))); // NOI18N
        btn_volver.setText("Volver");
        btn_volver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_volverActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_faltantes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 726, Short.MAX_VALUE)
                    .addComponent(sp_faltantes)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_volver)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_faltantes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_faltantes, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_volver)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_volverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_volverActionPerformed
        this.dispose();
    }//GEN-LAST:event_btn_volverActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setSize(sizeDialog);        
        this.setLocationRelativeTo(null);
        this.setColumnas();
        try {
            this.cargarResultadosAlTable();
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
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_volver;
    private javax.swing.JLabel lbl_faltantes;
    private javax.swing.JScrollPane sp_faltantes;
    private javax.swing.JTable tbl_Faltantes;
    // End of variables declaration//GEN-END:variables
}
