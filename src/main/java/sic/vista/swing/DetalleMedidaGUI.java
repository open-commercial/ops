package sic.vista.swing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.EmpresaActiva;
import sic.modelo.Medida;
import sic.util.Utilidades;

public class DetalleMedidaGUI extends JDialog {

    private final DefaultListModel modeloList = new DefaultListModel();
    private Medida medidaSeleccionada;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleMedidaGUI() {
        this.initComponents();
        this.setIcon();
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetalleMedidaGUI.class.getResource("/sic/icons/Ruler_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void cargarListMedidas() {
        modeloList.clear();
        try {
            List<Medida> medidas = new ArrayList(Arrays.asList(RestClient.getRestTemplate().getForObject("/medidas/empresas/"
                    + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(), Medida[].class)));
            medidas.stream().forEach((medida) -> {
                modeloList.addElement(medida);
            });
            lst_Medidas.setModel(modeloList);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel1 = new javax.swing.JPanel();
        sp_ListaMedidas = new javax.swing.JScrollPane();
        lst_Medidas = new javax.swing.JList();
        lbl_Flechas1 = new javax.swing.JLabel();
        txt_Nuevo = new javax.swing.JTextField();
        separador1 = new javax.swing.JSeparator();
        lbl_Flechas2 = new javax.swing.JLabel();
        txt_ModicaElimina = new javax.swing.JTextField();
        btn_Actualizar = new javax.swing.JButton();
        btn_Agregar = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Unidades de Medida");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lst_Medidas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lst_Medidas.setVisibleRowCount(9);
        lst_Medidas.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lst_MedidasValueChanged(evt);
            }
        });
        sp_ListaMedidas.setViewportView(lst_Medidas);

        lbl_Flechas1.setText("<<");

        txt_Nuevo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_NuevoKeyTyped(evt);
            }
        });

        lbl_Flechas2.setText(">>");

        txt_ModicaElimina.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_ModicaEliminaKeyTyped(evt);
            }
        });

        btn_Actualizar.setForeground(java.awt.Color.blue);
        btn_Actualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditRuler_16x16.png"))); // NOI18N
        btn_Actualizar.setText("Actualizar");
        btn_Actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ActualizarActionPerformed(evt);
            }
        });

        btn_Agregar.setForeground(java.awt.Color.blue);
        btn_Agregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddRuler_16x16.png"))); // NOI18N
        btn_Agregar.setText("Agregar");
        btn_Agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AgregarActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/RemoveRuler_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                .addComponent(sp_ListaMedidas, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(lbl_Flechas1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_Nuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(lbl_Flechas2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_ModicaElimina, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btn_Agregar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_Eliminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_Actualizar)))
                    .addComponent(separador1, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Nuevo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Flechas1)
                    .addComponent(btn_Agregar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separador1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_ModicaElimina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbl_Flechas2))
                    .addComponent(btn_Actualizar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Eliminar)
                .addContainerGap())
            .addComponent(sp_ListaMedidas, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_AgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AgregarActionPerformed
        try {
            Medida medida = new Medida();
            medida.setNombre(txt_Nuevo.getText().trim());
            medida.setEmpresa(EmpresaActiva.getInstance().getEmpresa());
            RestClient.getRestTemplate().postForObject("/medidas", medida, Medida.class);
            txt_Nuevo.setText("");
            this.cargarListMedidas();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_AgregarActionPerformed

    private void lst_MedidasValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lst_MedidasValueChanged
        if (lst_Medidas.getModel().getSize() != 0) {
            if (lst_Medidas.getSelectedValue() != null) {
                medidaSeleccionada = (Medida) lst_Medidas.getSelectedValue();
                txt_ModicaElimina.setText(medidaSeleccionada.getNombre());
            }
        }
    }//GEN-LAST:event_lst_MedidasValueChanged

    private void btn_ActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ActualizarActionPerformed
        try {
            if (medidaSeleccionada == null) {
                JOptionPane.showMessageDialog(this, "Seleccione una medida de la lista para poder continuar",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                Medida medidaModificada = new Medida();
                medidaModificada.setId_Medida(medidaSeleccionada.getId_Medida());
                medidaModificada.setNombre(txt_ModicaElimina.getText().trim());
                medidaModificada.setEmpresa(EmpresaActiva.getInstance().getEmpresa());
                RestClient.getRestTemplate().put("/medidas", medidaModificada);
                txt_ModicaElimina.setText("");
                medidaSeleccionada = null;
                this.cargarListMedidas();
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_ActualizarActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        try {
            if (medidaSeleccionada == null) {
                JOptionPane.showMessageDialog(this, "Seleccione una Medida de la lista para poder continuar.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                RestClient.getRestTemplate().delete("/medidas/" + medidaSeleccionada.getId_Medida());
                txt_ModicaElimina.setText("");
                medidaSeleccionada = null;
                this.cargarListMedidas();
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_EliminarActionPerformed

    private void txt_NuevoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_NuevoKeyTyped
        evt.setKeyChar(Utilidades.convertirAMayusculas(evt.getKeyChar()));
    }//GEN-LAST:event_txt_NuevoKeyTyped

    private void txt_ModicaEliminaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_ModicaEliminaKeyTyped
        evt.setKeyChar(Utilidades.convertirAMayusculas(evt.getKeyChar()));
    }//GEN-LAST:event_txt_ModicaEliminaKeyTyped

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.cargarListMedidas();
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Actualizar;
    private javax.swing.JButton btn_Agregar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JLabel lbl_Flechas1;
    private javax.swing.JLabel lbl_Flechas2;
    private javax.swing.JList lst_Medidas;
    private javax.swing.JPanel panel1;
    private javax.swing.JSeparator separador1;
    private javax.swing.JScrollPane sp_ListaMedidas;
    private javax.swing.JTextField txt_ModicaElimina;
    private javax.swing.JTextField txt_Nuevo;
    // End of variables declaration//GEN-END:variables
}
