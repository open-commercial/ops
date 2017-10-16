package sic.vista.swing;

import java.util.ArrayList;
import java.util.Arrays;
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
import sic.modelo.EmpresaActiva;
import sic.modelo.Localidad;
import sic.modelo.Pais;
import sic.modelo.Provincia;
import sic.modelo.Transportista;
import sic.modelo.TipoDeOperacion;

public class DetalleTransportistaGUI extends JDialog {

    private Transportista transportistaModificar;
    private final TipoDeOperacion operacion;   
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleTransportistaGUI() {
        this.initComponents();
        this.setIcon();
        this.setTitle("Nuevo Transportista");
        operacion = TipoDeOperacion.ALTA;
    }

    public DetalleTransportistaGUI(Transportista transportista) {
        this.initComponents();
        this.setIcon();
        this.setTitle("Modificar Transportista");
        operacion = TipoDeOperacion.ACTUALIZACION;
        transportistaModificar = transportista;
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetalleTransportistaGUI.class.getResource("/sic/icons/Truck_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void cargarTransportistaParaModificar() {
        txt_Nombre.setText(transportistaModificar.getNombre());
        txt_Direccion.setText(transportistaModificar.getDireccion());
        cmb_Pais.setSelectedItem(transportistaModificar.getLocalidad().getProvincia().getPais());
        cmb_Provincia.setSelectedItem(transportistaModificar.getLocalidad().getProvincia());
        cmb_Localidad.setSelectedItem(transportistaModificar.getLocalidad());
        txt_Telefono.setText(transportistaModificar.getTelefono());
        txt_Web.setText(transportistaModificar.getWeb());
    }

    private void limpiarYRecargarComponentes() {
        txt_Nombre.setText("");
        txt_Direccion.setText("");
        txt_Telefono.setText("");
        txt_Web.setText("");
        cargarComboBoxPaises();
    }

    private void cargarComboBoxPaises() {
        cmb_Pais.removeAllItems();
        try {
            List<Pais> paises = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/paises", Pais[].class)));
            paises.stream().forEach((p) -> {
                cmb_Pais.addItem(p);
            });
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarComboBoxProvinciasDelPais(Pais paisSeleccionado) {        
        cmb_Provincia.removeAllItems();
        try {
            List<Provincia> provincias = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/provincias/paises/" + paisSeleccionado.getId_Pais(),
                    Provincia[].class)));
            provincias.stream().forEach((p) -> {
                cmb_Provincia.addItem(p);
            });
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarComboBoxLocalidadesDeLaProvincia(Provincia provSeleccionada) {        
        cmb_Localidad.removeAllItems();
        try {
            List<Localidad> Localidades = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/localidades/provincias/" + provSeleccionada.getId_Provincia(),
                    Localidad[].class)));
            Localidades.stream().forEach((l) -> {
                cmb_Localidad.addItem(l);
            });
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
        lbl_Direccion = new javax.swing.JLabel();
        txt_Direccion = new javax.swing.JTextField();
        lbl_Provincia = new javax.swing.JLabel();
        cmb_Provincia = new javax.swing.JComboBox();
        lbl_Localidad = new javax.swing.JLabel();
        cmb_Pais = new javax.swing.JComboBox();
        lbl_Pais = new javax.swing.JLabel();
        btn_NuevoPais = new javax.swing.JButton();
        btn_NuevaProvincia = new javax.swing.JButton();
        btn_NuevaLocalidad = new javax.swing.JButton();
        cmb_Localidad = new javax.swing.JComboBox();
        lbl_Nombre = new javax.swing.JLabel();
        txt_Nombre = new javax.swing.JTextField();
        lbl_Web = new javax.swing.JLabel();
        txt_Web = new javax.swing.JTextField();
        lbl_Telefono = new javax.swing.JLabel();
        txt_Telefono = new javax.swing.JTextField();
        btn_Guardar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Direccion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Direccion.setText("Dirección:");

        lbl_Provincia.setForeground(java.awt.Color.red);
        lbl_Provincia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Provincia.setText("* Provincia:");

        cmb_Provincia.setMaximumRowCount(5);
        cmb_Provincia.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_ProvinciaItemStateChanged(evt);
            }
        });

        lbl_Localidad.setForeground(java.awt.Color.red);
        lbl_Localidad.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Localidad.setText("* Localidad:");

        cmb_Pais.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_PaisItemStateChanged(evt);
            }
        });

        lbl_Pais.setForeground(java.awt.Color.red);
        lbl_Pais.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Pais.setText("* Pais:");

        btn_NuevoPais.setForeground(java.awt.Color.blue);
        btn_NuevoPais.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btn_NuevoPais.setText("Nuevo");
        btn_NuevoPais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoPaisActionPerformed(evt);
            }
        });

        btn_NuevaProvincia.setForeground(java.awt.Color.blue);
        btn_NuevaProvincia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btn_NuevaProvincia.setText("Nueva");
        btn_NuevaProvincia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevaProvinciaActionPerformed(evt);
            }
        });

        btn_NuevaLocalidad.setForeground(java.awt.Color.blue);
        btn_NuevaLocalidad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btn_NuevaLocalidad.setText("Nueva");
        btn_NuevaLocalidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevaLocalidadActionPerformed(evt);
            }
        });

        lbl_Nombre.setForeground(java.awt.Color.red);
        lbl_Nombre.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Nombre.setText("* Nombre:");

        lbl_Web.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Web.setText("Página Web:");

        lbl_Telefono.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Telefono.setText("Teléfono:");

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_Localidad, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(lbl_Provincia, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Pais, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Direccion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Nombre, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Web, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Telefono, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_Web)
                    .addComponent(txt_Telefono)
                    .addComponent(txt_Direccion, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmb_Localidad, 0, 304, Short.MAX_VALUE)
                            .addComponent(cmb_Provincia, 0, 304, Short.MAX_VALUE)
                            .addComponent(cmb_Pais, 0, 304, Short.MAX_VALUE))
                        .addGap(0, 0, 0)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btn_NuevaProvincia)
                            .addComponent(btn_NuevoPais)
                            .addComponent(btn_NuevaLocalidad)))
                    .addComponent(txt_Nombre))
                .addContainerGap())
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Nombre))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Direccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Direccion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Pais)
                    .addComponent(cmb_Pais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevoPais))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Provincia)
                    .addComponent(cmb_Provincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevaProvincia))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Localidad)
                    .addComponent(cmb_Localidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevaLocalidad))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Telefono)
                    .addComponent(txt_Telefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Web)
                    .addComponent(txt_Web, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevoPais, cmb_Pais});

        panel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevaProvincia, cmb_Provincia});

        panel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevaLocalidad, cmb_Localidad});

        btn_Guardar.setForeground(java.awt.Color.blue);
        btn_Guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btn_Guardar.setText("Guardar");
        btn_Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GuardarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_Guardar)
                    .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Guardar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void btn_NuevoPaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoPaisActionPerformed
            DetallePaisGUI gui_DetallePais = new DetallePaisGUI();
            gui_DetallePais.setModal(true);
            gui_DetallePais.setLocationRelativeTo(this);
            gui_DetallePais.setVisible(true);
            cargarComboBoxPaises();
            cargarComboBoxProvinciasDelPais((Pais) cmb_Pais.getSelectedItem());
	}//GEN-LAST:event_btn_NuevoPaisActionPerformed

	private void btn_NuevaProvinciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevaProvinciaActionPerformed
            DetalleProvinciaGUI gui_DetalleProvincia = new DetalleProvinciaGUI();
            gui_DetalleProvincia.setModal(true);
            gui_DetalleProvincia.setLocationRelativeTo(this);
            gui_DetalleProvincia.setVisible(true);
            cargarComboBoxProvinciasDelPais((Pais) cmb_Pais.getSelectedItem());
	}//GEN-LAST:event_btn_NuevaProvinciaActionPerformed

	private void cmb_PaisItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_PaisItemStateChanged
            if (cmb_Pais.getItemCount() > 0) {
                cargarComboBoxProvinciasDelPais((Pais) cmb_Pais.getSelectedItem());
            }
	}//GEN-LAST:event_cmb_PaisItemStateChanged

	private void btn_NuevaLocalidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevaLocalidadActionPerformed
            DetalleLocalidadGUI gui_DetalleLocalidad = new DetalleLocalidadGUI();
            gui_DetalleLocalidad.setModal(true);
            gui_DetalleLocalidad.setLocationRelativeTo(this);
            gui_DetalleLocalidad.setVisible(true);
            cargarComboBoxLocalidadesDeLaProvincia((Provincia) cmb_Provincia.getSelectedItem());
	}//GEN-LAST:event_btn_NuevaLocalidadActionPerformed

	private void cmb_ProvinciaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_ProvinciaItemStateChanged
            if (cmb_Provincia.getItemCount() > 0) {
                cargarComboBoxLocalidadesDeLaProvincia((Provincia) cmb_Provincia.getSelectedItem());
            } else {
                cmb_Localidad.removeAllItems();
            }
	}//GEN-LAST:event_cmb_ProvinciaItemStateChanged

	private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
            try {
                if (operacion == TipoDeOperacion.ALTA) {
                    Transportista transportista = new Transportista();
                    transportista.setNombre(txt_Nombre.getText().trim());
                    transportista.setDireccion(txt_Direccion.getText().trim());
                    transportista.setLocalidad((Localidad) cmb_Localidad.getSelectedItem());                    
                    transportista.setTelefono(txt_Telefono.getText().trim());
                    transportista.setWeb(txt_Web.getText().trim());
                    transportista.setEmpresa(EmpresaActiva.getInstance().getEmpresa());
                    RestClient.getRestTemplate().postForObject("/transportistas", transportista, Transportista.class);
                    int respuesta = JOptionPane.showConfirmDialog(this,
                            "El transportista se guardó correctamente.\n¿Desea dar de alta otro transportista?",
                            "Aviso", JOptionPane.YES_NO_OPTION);
                    this.limpiarYRecargarComponentes();
                    if (respuesta == JOptionPane.NO_OPTION) {
                        this.dispose();
                    }
                }

                if (operacion == TipoDeOperacion.ACTUALIZACION) {
                    transportistaModificar.setNombre(txt_Nombre.getText().trim());
                    transportistaModificar.setDireccion(txt_Direccion.getText().trim());
                    transportistaModificar.setLocalidad((Localidad) cmb_Localidad.getSelectedItem());
                    transportistaModificar.setTelefono(txt_Telefono.getText().trim());
                    transportistaModificar.setWeb(txt_Web.getText().trim());
                    transportistaModificar.setEmpresa(EmpresaActiva.getInstance().getEmpresa());
                    RestClient.getRestTemplate().put("/transportistas", transportistaModificar);
                    JOptionPane.showMessageDialog(this, "El transportista se modificó correctamente!",
                            "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                }
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
	}//GEN-LAST:event_btn_GuardarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.cargarComboBoxPaises();
        if (operacion == TipoDeOperacion.ACTUALIZACION) {
            this.cargarTransportistaParaModificar();
        }
    }//GEN-LAST:event_formWindowOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JButton btn_NuevaLocalidad;
    private javax.swing.JButton btn_NuevaProvincia;
    private javax.swing.JButton btn_NuevoPais;
    private javax.swing.JComboBox cmb_Localidad;
    private javax.swing.JComboBox cmb_Pais;
    private javax.swing.JComboBox cmb_Provincia;
    private javax.swing.JLabel lbl_Direccion;
    private javax.swing.JLabel lbl_Localidad;
    private javax.swing.JLabel lbl_Nombre;
    private javax.swing.JLabel lbl_Pais;
    private javax.swing.JLabel lbl_Provincia;
    private javax.swing.JLabel lbl_Telefono;
    private javax.swing.JLabel lbl_Web;
    private javax.swing.JPanel panel1;
    private javax.swing.JTextField txt_Direccion;
    private javax.swing.JTextField txt_Nombre;
    private javax.swing.JTextField txt_Telefono;
    private javax.swing.JTextField txt_Web;
    // End of variables declaration//GEN-END:variables
}
