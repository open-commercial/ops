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
import sic.modelo.Pais;

public class DetallePaisGUI extends JDialog {

    private final DefaultListModel modeloList = new DefaultListModel();
    private Pais paisSeleccionado;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetallePaisGUI() {
        this.initComponents();
        this.setIcon();
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetallePaisGUI.class.getResource("/sic/icons/Map_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void cargarPaises() {
        try {
            modeloList.clear();
            List<Pais> paises = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/paises",
                    Pais[].class)));
            paises.stream().forEach((p) -> {
                modeloList.addElement(p);
            });
            lst_Paises.setModel(modeloList);
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

        jPanel1 = new javax.swing.JPanel();
        separador1 = new javax.swing.JSeparator();
        sp_ListaRubros = new javax.swing.JScrollPane();
        lst_Paises = new javax.swing.JList();
        lbl_Paises = new javax.swing.JLabel();
        lbl_Flechas1 = new javax.swing.JLabel();
        btn_Actualizar = new javax.swing.JButton();
        lbl_Flechas2 = new javax.swing.JLabel();
        btn_Agregar = new javax.swing.JButton();
        txt_Nuevo = new javax.swing.JTextField();
        txt_ModicaElimina = new javax.swing.JTextField();
        btn_Eliminar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Paises");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setPreferredSize(new java.awt.Dimension(530, 152));

        lst_Paises.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lst_Paises.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lst_PaisesValueChanged(evt);
            }
        });
        sp_ListaRubros.setViewportView(lst_Paises);

        lbl_Paises.setText("Paises:");

        lbl_Flechas1.setText("<<");

        btn_Actualizar.setForeground(java.awt.Color.blue);
        btn_Actualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditMap_16x16.png"))); // NOI18N
        btn_Actualizar.setText("Actualizar");
        btn_Actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ActualizarActionPerformed(evt);
            }
        });

        lbl_Flechas2.setText(">>");

        btn_Agregar.setForeground(java.awt.Color.blue);
        btn_Agregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btn_Agregar.setText("Agregar");
        btn_Agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AgregarActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/RemoveMap_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lbl_Paises)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_ListaRubros, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lbl_Flechas1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_Nuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_Agregar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(separador1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lbl_Flechas2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_ModicaElimina, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btn_Eliminar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_Actualizar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sp_ListaRubros, 0, 142, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txt_Nuevo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lbl_Flechas1)
                                    .addComponent(btn_Agregar))
                                .addGap(5, 5, 5)
                                .addComponent(separador1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txt_ModicaElimina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lbl_Flechas2)
                                    .addComponent(btn_Actualizar)))
                            .addComponent(lbl_Paises))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_Eliminar)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 512, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_AgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AgregarActionPerformed
        try {
            Pais pais = new Pais();
            pais.setNombre(txt_Nuevo.getText().trim());
            RestClient.getRestTemplate().postForObject("/paises", pais, Pais.class);
            txt_Nuevo.setText("");
            this.cargarPaises();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
       }
    }//GEN-LAST:event_btn_AgregarActionPerformed

    private void lst_PaisesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lst_PaisesValueChanged
        if (lst_Paises.getModel().getSize() != 0) {
            if (lst_Paises.getSelectedValue() != null) {
                paisSeleccionado = (Pais) lst_Paises.getSelectedValue();
                txt_ModicaElimina.setText(lst_Paises.getSelectedValue().toString());
            }
        }
    }//GEN-LAST:event_lst_PaisesValueChanged

    private void btn_ActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ActualizarActionPerformed
        try {
            if (paisSeleccionado == null) {
                JOptionPane.showMessageDialog(this,
                        "Seleccione un pais de la lista para poder continuar.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                Pais paisModificado = new Pais();
                paisModificado.setId_Pais(paisSeleccionado.getId_Pais());
                paisModificado.setNombre(txt_ModicaElimina.getText().trim());
                RestClient.getRestTemplate().put("/paises", paisModificado);
                txt_ModicaElimina.setText("");
                paisSeleccionado = null;
                this.cargarPaises();
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
            if (paisSeleccionado == null) {
                JOptionPane.showMessageDialog(this,
                        "Seleccione un Pais de la lista para poder continuar.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                RestClient.getRestTemplate().delete("/paises/" + paisSeleccionado.getId_Pais());
                txt_ModicaElimina.setText("");
                this.cargarPaises();
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

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
            this.cargarPaises();
    }//GEN-LAST:event_formWindowOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Actualizar;
    private javax.swing.JButton btn_Agregar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbl_Flechas1;
    private javax.swing.JLabel lbl_Flechas2;
    private javax.swing.JLabel lbl_Paises;
    private javax.swing.JList lst_Paises;
    private javax.swing.JSeparator separador1;
    private javax.swing.JScrollPane sp_ListaRubros;
    private javax.swing.JTextField txt_ModicaElimina;
    private javax.swing.JTextField txt_Nuevo;
    // End of variables declaration//GEN-END:variables
}
