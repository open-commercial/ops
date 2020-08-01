package sic.vista.swing;

import java.awt.event.ItemEvent;
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
import sic.RestClient;
import sic.modelo.Localidad;
import sic.modelo.LocalidadesParaActualizarDTO;

public class ModificacionMultipleLocalidadesGUI extends JDialog {

    private final List<Localidad> localidadesParaModificar;
    private final ModeloTabla modeloTablaLocalidades = new ModeloTabla();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ModificacionMultipleLocalidadesGUI(List<Localidad> localidadesParaModificar) {
        this.initComponents();
        this.setIcon();
        this.localidadesParaModificar = localidadesParaModificar;
    }
    
    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(ModificacionMultipleProductosGUI.class.getResource("/sic/icons/Map_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void prepararComponentes() {
        rbNoGratuito.setSelected(true);
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
    
    private long[] getIdsLocalidades(List<Localidad> localidades) {
        long[] idsLocalidades = new long[localidades.size()];
        for (int i = 0; i < localidades.size(); ++i) {
            idsLocalidades[i] = localidades.get(i).getIdLocalidad();
        }
        return idsLocalidades;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgTipoEnvio = new javax.swing.ButtonGroup();
        panel1 = new javax.swing.JPanel();
        chkCostoDeEnvio = new javax.swing.JCheckBox();
        ftfCostoDeEnvio = new javax.swing.JFormattedTextField();
        chkEnvio = new javax.swing.JCheckBox();
        rbGratuito = new javax.swing.JRadioButton();
        rbNoGratuito = new javax.swing.JRadioButton();
        btnAceptar = new javax.swing.JButton();
        lblIndicaciones = new javax.swing.JLabel();
        spResultados = new javax.swing.JScrollPane();
        tblLocalidades = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Modificar multiples Localidades");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        chkCostoDeEnvio.setText("Costo de Envío: ");
        chkCostoDeEnvio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkCostoDeEnvioItemStateChanged(evt);
            }
        });

        ftfCostoDeEnvio.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftfCostoDeEnvio.setEnabled(false);

        chkEnvio.setText("Envío:");
        chkEnvio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkEnvioItemStateChanged(evt);
            }
        });

        bgTipoEnvio.add(rbGratuito);
        rbGratuito.setText("Gratuito");
        rbGratuito.setEnabled(false);

        bgTipoEnvio.add(rbNoGratuito);
        rbNoGratuito.setText("No Gratuito");
        rbNoGratuito.setEnabled(false);

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(chkCostoDeEnvio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkEnvio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rbNoGratuito, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rbGratuito, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ftfCostoDeEnvio))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkCostoDeEnvio)
                    .addComponent(ftfCostoDeEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkEnvio)
                    .addComponent(rbGratuito))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbNoGratuito)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnAceptar.setForeground(java.awt.Color.blue);
        btnAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btnAceptar.setText("Guardar");
        btnAceptar.setEnabled(false);
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        lblIndicaciones.setText("Localidades que se van a modificar:");

        tblLocalidades.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        spResultados.setViewportView(tblLocalidades);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spResultados, javax.swing.GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnAceptar))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblIndicaciones)
                            .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblIndicaciones)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spResultados, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAceptar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.prepararComponentes();
        this.cargarResultadosAlTable();
    }//GEN-LAST:event_formWindowOpened

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        LocalidadesParaActualizarDTO localidadesParaActualizarDTO = LocalidadesParaActualizarDTO.builder()
                .idLocalidad(this.getIdsLocalidades(this.localidadesParaModificar))
                .build();
        if (chkCostoDeEnvio.isSelected()) {
            localidadesParaActualizarDTO.setCostoDeEnvio(new BigDecimal(ftfCostoDeEnvio.getValue().toString()));
        }
        if (chkEnvio.isSelected()) {
            localidadesParaActualizarDTO.setEnvioGratuito(rbGratuito.isSelected());
        }
        try {
            RestClient.getRestTemplate().put("/ubicaciones/multiples", localidadesParaActualizarDTO);
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
            btnAceptar.setEnabled(true);
        } else {
            ftfCostoDeEnvio.setEnabled(false);
            if (!chkEnvio.isSelected()) {
                btnAceptar.setEnabled(false);
            }
        }
    }//GEN-LAST:event_chkCostoDeEnvioItemStateChanged

    private void chkEnvioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkEnvioItemStateChanged
        if (chkEnvio.isSelected() == true) {
            rbGratuito.setEnabled(true);
            rbNoGratuito.setEnabled(true);
            btnAceptar.setEnabled(true);
        } else {
            rbGratuito.setEnabled(false);
            rbNoGratuito.setEnabled(false);
            if (!chkCostoDeEnvio.isSelected()) {
                btnAceptar.setEnabled(false);
            }
        }
    }//GEN-LAST:event_chkEnvioItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgTipoEnvio;
    private javax.swing.JButton btnAceptar;
    private javax.swing.JCheckBox chkCostoDeEnvio;
    private javax.swing.JCheckBox chkEnvio;
    private javax.swing.JFormattedTextField ftfCostoDeEnvio;
    private javax.swing.JLabel lblIndicaciones;
    private javax.swing.JPanel panel1;
    private javax.swing.JRadioButton rbGratuito;
    private javax.swing.JRadioButton rbNoGratuito;
    private javax.swing.JScrollPane spResultados;
    private javax.swing.JTable tblLocalidades;
    // End of variables declaration//GEN-END:variables
}
