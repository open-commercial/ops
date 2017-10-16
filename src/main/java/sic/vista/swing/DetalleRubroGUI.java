package sic.vista.swing;

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
import sic.modelo.Rubro;
import sic.util.Utilidades;

public class DetalleRubroGUI extends JDialog {

    private final DefaultListModel modeloList = new DefaultListModel();
    private Rubro rubroSeleccionado;  
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleRubroGUI() {
        this.initComponents();
        this.setIcon();
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetalleRubroGUI.class.getResource("/sic/icons/Block.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel1 = new javax.swing.JPanel();
        sp_ListaRubros = new javax.swing.JScrollPane();
        lst_Rubros = new javax.swing.JList();
        lbl_Flechas1 = new javax.swing.JLabel();
        txt_Nuevo = new javax.swing.JTextField();
        separador1 = new javax.swing.JSeparator();
        lbl_Flechas2 = new javax.swing.JLabel();
        txt_ModicaElimina = new javax.swing.JTextField();
        btn_Actualizar = new javax.swing.JButton();
        btn_Agregar = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Rubros");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lst_Rubros.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lst_Rubros.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lst_RubrosValueChanged(evt);
            }
        });
        sp_ListaRubros.setViewportView(lst_Rubros);

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
        btn_Actualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditBlock.png"))); // NOI18N
        btn_Actualizar.setText("Actualizar");
        btn_Actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ActualizarActionPerformed(evt);
            }
        });

        btn_Agregar.setForeground(java.awt.Color.blue);
        btn_Agregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddBlock.png"))); // NOI18N
        btn_Agregar.setText("Agregar");
        btn_Agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AgregarActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/DeleteBlock.png"))); // NOI18N
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
            .addGroup(panel1Layout.createSequentialGroup()
                .addComponent(sp_ListaRubros, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(lbl_Flechas1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_Nuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_Agregar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(separador1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panel1Layout.createSequentialGroup()
                            .addComponent(lbl_Flechas2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt_ModicaElimina, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(btn_Eliminar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btn_Actualizar, javax.swing.GroupLayout.Alignment.LEADING)))))
                .addContainerGap())
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_ListaRubros)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Nuevo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Flechas1)
                    .addComponent(btn_Agregar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separador1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_ModicaElimina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Flechas2)
                    .addComponent(btn_Actualizar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Eliminar)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_AgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AgregarActionPerformed
        try {
            Rubro rubro = new Rubro();
            rubro.setNombre(txt_Nuevo.getText().trim());
            rubro.setEmpresa(EmpresaActiva.getInstance().getEmpresa());
            RestClient.getRestTemplate().postForObject("/rubros", rubro, Rubro.class);
            txt_Nuevo.setText("");
            this.cargarListRubros();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                "Error", JOptionPane.ERROR_MESSAGE);
        } 
    }//GEN-LAST:event_btn_AgregarActionPerformed

    private void lst_RubrosValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lst_RubrosValueChanged
        if (lst_Rubros.getModel().getSize() != 0) {
            if (lst_Rubros.getSelectedValue() != null) {
                rubroSeleccionado = (Rubro) lst_Rubros.getSelectedValue();
                txt_ModicaElimina.setText(rubroSeleccionado.getNombre());
            }
        }
    }//GEN-LAST:event_lst_RubrosValueChanged

    private void btn_ActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ActualizarActionPerformed
        try {
            if (rubroSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un rubro de la lista para poder continuar.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                Rubro rubroModificado = new Rubro();
                rubroModificado.setId_Rubro(rubroSeleccionado.getId_Rubro());
                rubroModificado.setNombre(txt_ModicaElimina.getText().trim());
                rubroModificado.setEmpresa(EmpresaActiva.getInstance().getEmpresa());
                RestClient.getRestTemplate().put("/rubros", rubroModificado);
                txt_ModicaElimina.setText("");
                rubroSeleccionado = null;
                this.cargarListRubros();
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
            if (rubroSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un rubro de la lista para poder continuar.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                RestClient.getRestTemplate().delete("/rubros/" + rubroSeleccionado.getId_Rubro());
                txt_ModicaElimina.setText("");
                rubroSeleccionado = null;
                cargarListRubros();
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
        this.cargarListRubros();
    }//GEN-LAST:event_formWindowOpened

    private void cargarListRubros() {
        modeloList.clear();
        List<Rubro> rubros = Arrays.asList(RestClient.getRestTemplate()
                .getForObject("/rubros/empresas/"  + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                Rubro[].class));
        rubros.stream().forEach((r) -> {
            modeloList.addElement(r);
        });
        lst_Rubros.setModel(modeloList);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Actualizar;
    private javax.swing.JButton btn_Agregar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JLabel lbl_Flechas1;
    private javax.swing.JLabel lbl_Flechas2;
    private javax.swing.JList lst_Rubros;
    private javax.swing.JPanel panel1;
    private javax.swing.JSeparator separador1;
    private javax.swing.JScrollPane sp_ListaRubros;
    private javax.swing.JTextField txt_ModicaElimina;
    private javax.swing.JTextField txt_Nuevo;
    // End of variables declaration//GEN-END:variables
}
