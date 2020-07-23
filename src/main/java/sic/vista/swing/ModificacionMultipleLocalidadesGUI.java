package sic.vista.swing;

import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Localidad;
import sic.modelo.LocalidadesParaActualizarDTO;

public class ModificacionMultipleLocalidadesGUI extends javax.swing.JDialog {

    private List<Localidad> localidadesParaModificar;
    private ModeloTabla modeloTablaLocalidades = new ModeloTabla();
    private LocalidadesParaActualizarDTO localidadesParaActualizarDTO;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ModificacionMultipleLocalidadesGUI(LocalidadesParaActualizarDTO localidadesParaActualizarDTO) {
        initComponents();
        this.localidadesParaActualizarDTO = localidadesParaActualizarDTO;
    }

    private void cargarLocalidades() {
        try {
            this.localidadesParaModificar = new ArrayList();
            for (int i = 0; i < localidadesParaActualizarDTO.getIdLocalidad().length; ++i) {
                localidadesParaModificar.add(RestClient.getRestTemplate().getForObject("/ubicaciones/localidades/" + localidadesParaActualizarDTO.getIdLocalidad()[i], Localidad.class));
            }
            this.cargarResultadosAlTable();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void prepararComponentes() {
        ftfCostoDeEnvio.setValue(BigDecimal.ZERO);
        this.setColumnas();
    }

    private void setColumnas() {
        //nombres de columnas
        String[] encabezados = new String[3];
        encabezados[0] = "Codigo Postal";
        encabezados[1] = "Nombre";
        encabezados[2] = "Provincia";
        modeloTablaLocalidades.setColumnIdentifiers(encabezados);
        tblLocalidades.setModel(modeloTablaLocalidades);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaLocalidades.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        tipos[2] = String.class;
        modeloTablaLocalidades.setClaseColumnas(tipos);
        tblLocalidades.getTableHeader().setReorderingAllowed(false);
        tblLocalidades.getTableHeader().setResizingAllowed(true);
        //Tamanios de columnas
        tblLocalidades.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblLocalidades.getColumnModel().getColumn(0).setMaxWidth(100);
        tblLocalidades.getColumnModel().getColumn(0).setMinWidth(100);
        tblLocalidades.getColumnModel().getColumn(1).setPreferredWidth(320);
        tblLocalidades.getColumnModel().getColumn(1).setMaxWidth(320);
        tblLocalidades.getColumnModel().getColumn(1).setMinWidth(320);
        tblLocalidades.getColumnModel().getColumn(2).setPreferredWidth(220);
        tblLocalidades.getColumnModel().getColumn(2).setMaxWidth(220);
        tblLocalidades.getColumnModel().getColumn(2).setMinWidth(220);
    }

    private void cargarResultadosAlTable() {
        localidadesParaModificar.stream().map(l -> {
            Object[] fila = new Object[3];
            fila[0] = l.getCodigoPostal();
            fila[1] = l.getNombre();
            fila[2] = l.getNombreProvincia();
            return fila;
        }).forEach(f -> {
            modeloTablaLocalidades.addRow(f);
        });
        tblLocalidades.setModel(modeloTablaLocalidades);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel1 = new javax.swing.JPanel();
        jScrollPanel1 = new javax.swing.JScrollPane();
        tblLocalidades = new javax.swing.JTable();
        chkCostoDeEnvio = new javax.swing.JCheckBox();
        chkEnvioGratuito = new javax.swing.JCheckBox();
        ftfCostoDeEnvio = new javax.swing.JFormattedTextField();
        btnAceptar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Modificar multiples Localidades");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        tblLocalidades.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPanel1.setViewportView(tblLocalidades);

        chkCostoDeEnvio.setText("Costo de Envío: ");
        chkCostoDeEnvio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkCostoDeEnvioItemStateChanged(evt);
            }
        });

        chkEnvioGratuito.setText("Envio Gratuito");

        ftfCostoDeEnvio.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftfCostoDeEnvio.setEnabled(false);

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkEnvioGratuito)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(chkCostoDeEnvio)
                        .addGap(0, 0, 0)
                        .addComponent(ftfCostoDeEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(392, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jScrollPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE))
        );

        panel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {chkCostoDeEnvio, chkEnvioGratuito});

        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addComponent(jScrollPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkCostoDeEnvio)
                    .addComponent(ftfCostoDeEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkEnvioGratuito))
        );

        btnAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btnAceptar.setText("Guardar");
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnAceptar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAceptar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.prepararComponentes();
        this.cargarLocalidades();
    }//GEN-LAST:event_formWindowOpened

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        this.localidadesParaActualizarDTO.setCostoDeEnvio(new BigDecimal(ftfCostoDeEnvio.getValue().toString()));
        this.localidadesParaActualizarDTO.setEnvioGratuito(chkEnvioGratuito.isSelected());
        try {
            RestClient.getRestTemplate().put("/ubicaciones/multiples", this.localidadesParaActualizarDTO);
            JOptionPane.showMessageDialog(this, "Las localidades se modificaron correctamente.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAceptarActionPerformed

    private void chkCostoDeEnvioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkCostoDeEnvioItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            ftfCostoDeEnvio.setEnabled(true);
        } else {
            ftfCostoDeEnvio.setEnabled(false);
        }
    }//GEN-LAST:event_chkCostoDeEnvioItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JCheckBox chkCostoDeEnvio;
    private javax.swing.JCheckBox chkEnvioGratuito;
    private javax.swing.JFormattedTextField ftfCostoDeEnvio;
    private javax.swing.JScrollPane jScrollPanel1;
    private javax.swing.JPanel panel1;
    private javax.swing.JTable tblLocalidades;
    // End of variables declaration//GEN-END:variables
}
