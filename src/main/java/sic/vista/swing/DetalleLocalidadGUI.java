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
import sic.modelo.Localidad;
import sic.modelo.Pais;
import sic.modelo.Provincia;

public class DetalleLocalidadGUI extends JDialog {

    private final DefaultListModel modeloList = new DefaultListModel();
    private final DefaultComboBoxModel modeloComboPaises = new DefaultComboBoxModel();
    private final DefaultComboBoxModel modeloComboProvincias = new DefaultComboBoxModel();
    private final DefaultComboBoxModel modeloComboProvinciasBusqueda = new DefaultComboBoxModel();
    private Localidad localidadSeleccionada;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleLocalidadGUI() {
        this.initComponents();
        this.setIcon();
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetalleLocalidadGUI.class.getResource("/sic/icons/Map_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void cargarPaises() {
        try {
            List<Pais> paises = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/paises",
                    Pais[].class)));
            modeloComboPaises.removeAllElements();
            paises.stream().forEach((pais) -> {
                modeloComboPaises.addElement(pais);
            });
            cmb_Paises.setModel(modeloComboPaises);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarProvinciasDelPais(JComboBox comboBox, DefaultComboBoxModel modelo, Pais pais) {
        try {
            List<Provincia> provincias = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/provincias/paises/" + pais.getId_Pais(),
                    Provincia[].class)));
            modelo.removeAllElements();
            provincias.stream().forEach((p) -> {
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
    
    private void cargarLocalidadesDeLaProvincia(Provincia provincia) {
        try {
            List<Localidad> localidades = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/localidades/provincias/" + provincia.getId_Provincia(),
                    Localidad[].class)));
            modeloList.clear();
            localidades.stream().forEach((l) -> {
                modeloList.addElement(l);
            });
            lst_Localidades.setModel(modeloList);
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
        lbl_Pais = new javax.swing.JLabel();
        cmb_Paises = new javax.swing.JComboBox();
        lbl_Prov = new javax.swing.JLabel();
        cmb_ProvinciasBusqueda = new javax.swing.JComboBox();
        lbl_Localidades = new javax.swing.JLabel();
        sp_ListaMedidas = new javax.swing.JScrollPane();
        lst_Localidades = new javax.swing.JList();
        panel2 = new javax.swing.JPanel();
        lbl_Nombre = new javax.swing.JLabel();
        txt_Nombre = new javax.swing.JTextField();
        lbl_Provincia = new javax.swing.JLabel();
        cmb_Provincias = new javax.swing.JComboBox();
        txt_CodigoPostal = new javax.swing.JTextField();
        lbl_CP = new javax.swing.JLabel();
        btn_Agregar = new javax.swing.JButton();
        btn_Actualizar = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Localidades");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Pais.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Pais.setText("Pais:");

        cmb_Paises.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_PaisesItemStateChanged(evt);
            }
        });

        lbl_Prov.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Prov.setText("Provincia:");

        cmb_ProvinciasBusqueda.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_ProvinciasBusquedaItemStateChanged(evt);
            }
        });

        lbl_Localidades.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Localidades.setText("Localidades:");

        lst_Localidades.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lst_Localidades.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lst_LocalidadesValueChanged(evt);
            }
        });
        sp_ListaMedidas.setViewportView(lst_Localidades);

        panel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Nombre.setForeground(java.awt.Color.red);
        lbl_Nombre.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Nombre.setText("* Nombre:");

        lbl_Provincia.setForeground(java.awt.Color.red);
        lbl_Provincia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Provincia.setText("* Provincia:");

        lbl_CP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_CP.setText("C.P.:");

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_Provincia, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_CP, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_Nombre, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_CodigoPostal, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txt_Nombre, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cmb_Provincias, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Nombre))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_CodigoPostal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_CP))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmb_Provincias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Provincia)))
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
            .addGroup(panel1Layout.createSequentialGroup()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_Pais, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Prov, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Localidades, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmb_ProvinciasBusqueda, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sp_ListaMedidas, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                    .addComponent(cmb_Paises, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(btn_Agregar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_Actualizar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_Eliminar))
                    .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_Pais)
                            .addComponent(cmb_Paises, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbl_Prov)
                            .addComponent(cmb_ProvinciasBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(lbl_Localidades)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE))
                            .addComponent(sp_ListaMedidas, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panel1Layout.createSequentialGroup()
                        .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_Agregar)
                            .addComponent(btn_Actualizar)
                            .addComponent(btn_Eliminar))))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_AgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AgregarActionPerformed
        try {
            Localidad localidad = new Localidad();
            localidad.setNombre(txt_Nombre.getText().trim());
            localidad.setCodigoPostal(txt_CodigoPostal.getText().trim());
            localidad.setProvincia((Provincia) cmb_Provincias.getSelectedItem());
            RestClient.getRestTemplate().postForObject("/localidades", localidad, Localidad.class);
            txt_Nombre.setText("");
            txt_CodigoPostal.setText("");
            this.cargarLocalidadesDeLaProvincia((Provincia) cmb_ProvinciasBusqueda.getSelectedItem());
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
       }
    }//GEN-LAST:event_btn_AgregarActionPerformed

    private void lst_LocalidadesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lst_LocalidadesValueChanged
        if (lst_Localidades.getModel().getSize() != 0) {
            if (lst_Localidades.getSelectedValue() != null) {
                localidadSeleccionada = (Localidad) lst_Localidades.getSelectedValue();
                txt_Nombre.setText(localidadSeleccionada.getNombre());
                txt_CodigoPostal.setText(localidadSeleccionada.getCodigoPostal());
                cmb_Provincias.setSelectedIndex(cmb_ProvinciasBusqueda.getSelectedIndex());
            }
        }
    }//GEN-LAST:event_lst_LocalidadesValueChanged

    private void btn_ActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ActualizarActionPerformed
        try {
            if (localidadSeleccionada == null) {
                JOptionPane.showMessageDialog(this,
                        "Seleccione una localidad de la lista para poder continuar.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                Localidad localidadModificada = new Localidad();
                localidadModificada.setId_Localidad(localidadSeleccionada.getId_Localidad());
                localidadModificada.setNombre(txt_Nombre.getText().trim());
                localidadModificada.setCodigoPostal(txt_CodigoPostal.getText().trim());
                localidadModificada.setProvincia((Provincia) cmb_Provincias.getSelectedItem());
                RestClient.getRestTemplate().put("/localidades", localidadModificada);
                txt_Nombre.setText("");
                txt_CodigoPostal.setText("");
                localidadSeleccionada = null;
                this.cargarLocalidadesDeLaProvincia((Provincia) cmb_ProvinciasBusqueda.getSelectedItem());
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
            if (localidadSeleccionada == null) {
                JOptionPane.showMessageDialog(this,
                        "Seleccione una localidad de la lista para poder continuar.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                RestClient.getRestTemplate().delete("/localidades/" + localidadSeleccionada.getId_Localidad());
                txt_Nombre.setText("");
                txt_CodigoPostal.setText("");
                localidadSeleccionada = null;
                cargarLocalidadesDeLaProvincia((Provincia) cmb_ProvinciasBusqueda.getSelectedItem());
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

    private void cmb_ProvinciasBusquedaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_ProvinciasBusquedaItemStateChanged
        if (modeloComboProvinciasBusqueda.getSize() > 0) {
            this.cargarLocalidadesDeLaProvincia((Provincia) cmb_ProvinciasBusqueda.getSelectedItem());
        }
    }//GEN-LAST:event_cmb_ProvinciasBusquedaItemStateChanged

    private void cmb_PaisesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_PaisesItemStateChanged
        if (modeloComboPaises.getSize() > 0) {
            this.cargarProvinciasDelPais(cmb_ProvinciasBusqueda,
                    modeloComboProvinciasBusqueda,
                    (Pais) cmb_Paises.getSelectedItem());
            this.cargarProvinciasDelPais(cmb_Provincias,
                    modeloComboProvincias,
                    (Pais) cmb_Paises.getSelectedItem());
        }
    }//GEN-LAST:event_cmb_PaisesItemStateChanged

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.cargarPaises();
        cmb_PaisesItemStateChanged(null);
        cmb_ProvinciasBusquedaItemStateChanged(null);
    }//GEN-LAST:event_formWindowOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Actualizar;
    private javax.swing.JButton btn_Agregar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JComboBox cmb_Paises;
    private javax.swing.JComboBox cmb_Provincias;
    private javax.swing.JComboBox cmb_ProvinciasBusqueda;
    private javax.swing.JLabel lbl_CP;
    private javax.swing.JLabel lbl_Localidades;
    private javax.swing.JLabel lbl_Nombre;
    private javax.swing.JLabel lbl_Pais;
    private javax.swing.JLabel lbl_Prov;
    private javax.swing.JLabel lbl_Provincia;
    private javax.swing.JList lst_Localidades;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JScrollPane sp_ListaMedidas;
    private javax.swing.JTextField txt_CodigoPostal;
    private javax.swing.JTextField txt_Nombre;
    // End of variables declaration//GEN-END:variables
}
