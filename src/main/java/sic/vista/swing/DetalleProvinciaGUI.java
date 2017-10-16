package sic.vista.swing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Pais;
import sic.modelo.Provincia;

public class DetalleProvinciaGUI extends JDialog {

    private final DefaultListModel modeloList = new DefaultListModel();
    private final DefaultComboBoxModel modeloComboPaises = new DefaultComboBoxModel();
    private final DefaultComboBoxModel modeloComboPaisesBusqueda = new DefaultComboBoxModel();
    private Provincia provinciaSeleccionada;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleProvinciaGUI() {
        this.initComponents();
        this.setIcon();
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetalleProvinciaGUI.class.getResource("/sic/icons/Map_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void cargarPaises(JComboBox comboBox, DefaultComboBoxModel modelo) {   
        try {
            modelo.removeAllElements();
            List<Pais> paises = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/paises",
                    Pais[].class)));
            paises.stream().forEach((p) -> {
                modelo.addElement(p);
            });
            comboBox.setModel(modelo);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarProvinciasDelPais(Pais pais) {
        try {
            List<Provincia> provincias = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/provincias/paises/" + pais.getId_Pais(),
                    Provincia[].class)));
            modeloList.clear();
            provincias.stream().forEach((p) -> {
                modeloList.addElement(p);
            });
            lst_Provincias.setModel(modeloList);
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
        lbl_Pa = new javax.swing.JLabel();
        cmb_PaisesBusqueda = new javax.swing.JComboBox();
        lbl_Provincias = new javax.swing.JLabel();
        sp_ListaMedidas = new javax.swing.JScrollPane();
        lst_Provincias = new javax.swing.JList();
        panel2 = new javax.swing.JPanel();
        lbl_Pais = new javax.swing.JLabel();
        lbl_Nombre = new javax.swing.JLabel();
        txt_Nombre = new javax.swing.JTextField();
        cmb_Paises = new javax.swing.JComboBox();
        btn_Agregar = new javax.swing.JButton();
        btn_Actualizar = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Provincias");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Pa.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Pa.setText("Pais:");

        cmb_PaisesBusqueda.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_PaisesBusquedaItemStateChanged(evt);
            }
        });

        lbl_Provincias.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Provincias.setText("Provincias:");

        lst_Provincias.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lst_Provincias.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lst_ProvinciasValueChanged(evt);
            }
        });
        sp_ListaMedidas.setViewportView(lst_Provincias);

        panel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Pais.setForeground(java.awt.Color.red);
        lbl_Pais.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Pais.setText("* Pais:");

        lbl_Nombre.setForeground(java.awt.Color.red);
        lbl_Nombre.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Nombre.setText("* Nombre:");

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_Nombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Pais, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmb_Paises, 0, 208, Short.MAX_VALUE)
                    .addComponent(txt_Nombre, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)))
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Nombre))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmb_Paises, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Pais)))
        );

        btn_Agregar.setForeground(java.awt.Color.blue);
        btn_Agregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btn_Agregar.setText("Agregar");
        btn_Agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AgregarActionPerformed(evt);
            }
        });

        btn_Actualizar.setForeground(java.awt.Color.blue);
        btn_Actualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditMap_16x16.png"))); // NOI18N
        btn_Actualizar.setText("Actualizar");
        btn_Actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ActualizarActionPerformed(evt);
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

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_Pa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Provincias, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sp_ListaMedidas, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                    .addComponent(cmb_PaisesBusqueda, 0, 179, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(btn_Agregar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_Actualizar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_Eliminar, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Pa)
                    .addComponent(cmb_PaisesBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_Provincias)
                            .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_Agregar)
                            .addComponent(btn_Actualizar)
                            .addComponent(btn_Eliminar)))
                    .addComponent(sp_ListaMedidas, 0, 0, Short.MAX_VALUE)))
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
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_AgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AgregarActionPerformed
        try {
            Provincia provincia = new Provincia();
            provincia.setNombre(txt_Nombre.getText().trim());
            provincia.setPais((Pais) cmb_Paises.getSelectedItem());
            RestClient.getRestTemplate().postForObject("/provincias", provincia, Provincia.class);
            txt_Nombre.setText("");
            this.cargarProvinciasDelPais((Pais) cmb_PaisesBusqueda.getSelectedItem());
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_AgregarActionPerformed

    private void lst_ProvinciasValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lst_ProvinciasValueChanged
        if (lst_Provincias.getModel().getSize() != 0) {
            if (lst_Provincias.getSelectedValue() != null) {
                provinciaSeleccionada = (Provincia) lst_Provincias.getSelectedValue();
                txt_Nombre.setText(provinciaSeleccionada.getNombre());
                cmb_Paises.setSelectedIndex(cmb_PaisesBusqueda.getSelectedIndex());
            }
        }
    }//GEN-LAST:event_lst_ProvinciasValueChanged

    private void btn_ActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ActualizarActionPerformed
        try {
            if (provinciaSeleccionada == null) {
                JOptionPane.showMessageDialog(this,
                        "Seleccione una provincia de la lista para poder continuar.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                Provincia provinciaModificada = new Provincia();
                provinciaModificada.setId_Provincia(provinciaSeleccionada.getId_Provincia());
                provinciaModificada.setNombre(txt_Nombre.getText().trim());
                provinciaModificada.setPais((Pais) cmb_Paises.getSelectedItem());
                RestClient.getRestTemplate().put("/provincias", provinciaModificada);
                txt_Nombre.setText("");
                provinciaSeleccionada = null;
                cargarProvinciasDelPais((Pais) cmb_PaisesBusqueda.getSelectedItem());
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
            if (provinciaSeleccionada == null) {
                JOptionPane.showMessageDialog(this,
                        "Seleccione una provincia de la lista para poder continuar.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                RestClient.getRestTemplate().delete("/provincias/" + provinciaSeleccionada.getId_Provincia());
                txt_Nombre.setText("");
                provinciaSeleccionada = null;
                cargarProvinciasDelPais((Pais) cmb_PaisesBusqueda.getSelectedItem());
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

    private void cmb_PaisesBusquedaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_PaisesBusquedaItemStateChanged
        if (modeloComboPaisesBusqueda.getSize() > 0) {
            cargarProvinciasDelPais((Pais) cmb_PaisesBusqueda.getSelectedItem());
        }
    }//GEN-LAST:event_cmb_PaisesBusquedaItemStateChanged

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        cargarPaises(cmb_Paises, modeloComboPaises);
        cargarPaises(cmb_PaisesBusqueda, modeloComboPaisesBusqueda);
        cmb_PaisesBusquedaItemStateChanged(null);
    }//GEN-LAST:event_formWindowOpened
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Actualizar;
    private javax.swing.JButton btn_Agregar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JComboBox cmb_Paises;
    private javax.swing.JComboBox cmb_PaisesBusqueda;
    private javax.swing.JLabel lbl_Nombre;
    private javax.swing.JLabel lbl_Pa;
    private javax.swing.JLabel lbl_Pais;
    private javax.swing.JLabel lbl_Provincias;
    private javax.swing.JList lst_Provincias;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JScrollPane sp_ListaMedidas;
    private javax.swing.JTextField txt_Nombre;
    // End of variables declaration//GEN-END:variables
}
