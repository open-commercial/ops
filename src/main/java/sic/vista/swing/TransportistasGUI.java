package sic.vista.swing;

import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JInternalFrame;
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
import sic.util.Utilidades;

public class TransportistasGUI extends JInternalFrame {

    private ModeloTabla modeloTablaResultados = new ModeloTabla();
    private List<Transportista> transportistas;
    private Transportista transSeleccionado; 
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame = new Dimension(880, 600);

    public TransportistasGUI() {
        this.initComponents();                
    }

    public Transportista getTransSeleccionado() {
        return transSeleccionado;
    }

    public void setTransSeleccionado(Transportista transSeleccionado) {
        this.transSeleccionado = transSeleccionado;
    }

    private void cargarComboBoxPaises() {
        cmb_Pais.removeAllItems();
        try {
            List<Pais> paises = new ArrayList(Arrays.asList(RestClient.getRestTemplate().getForObject("/paises", Pais[].class)));
            Pais paisTodos = new Pais();
            paisTodos.setNombre("Todos");
            cmb_Pais.addItem(paisTodos);
            paises.stream().forEach((pais) -> {
                cmb_Pais.addItem(pais);
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
            Provincia provinciaTodas = new Provincia();
            provinciaTodas.setNombre("Todas");
            cmb_Provincia.addItem(provinciaTodas);
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
            Localidad localidadTodas = new Localidad();
            localidadTodas.setNombre("Todas");
            cmb_Localidad.addItem(localidadTodas);
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

    private void setColumnas() {
        //sorting
        tbl_Resultados.setAutoCreateRowSorter(true);

        //nombres de columnas
        String[] encabezados = new String[7];
        encabezados[0] = "Nombre";
        encabezados[1] = "Direccion";
        encabezados[2] = "Telefono";
        encabezados[3] = "Web";
        encabezados[4] = "Localidad";
        encabezados[5] = "Provincia";
        encabezados[6] = "Pais";
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaResultados);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        tipos[2] = String.class;
        tipos[3] = String.class;
        tipos[4] = String.class;
        tipos[5] = String.class;
        tipos[6] = String.class;
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);

        //Tamanios de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(300);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(300);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(5).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(6).setPreferredWidth(200);
    }

    private void cargarResultadosAlTable() {
        this.limpiarJTable();
        transportistas.stream().map((transportista) -> {
            Object[] fila = new Object[7];
            fila[0] = transportista.getNombre();
            fila[1] = transportista.getDireccion();
            fila[2] = transportista.getTelefono();
            fila[3] = transportista.getWeb();
            fila[4] = transportista.getLocalidad().getNombre();
            fila[5] = transportista.getLocalidad().getProvincia().getNombre();
            fila[6] = transportista.getLocalidad().getProvincia().getPais().getNombre();
            return fila;
        }).forEach((fila) -> {
            modeloTablaResultados.addRow(fila);
        });
        tbl_Resultados.setModel(modeloTablaResultados);
        String mensaje = transportistas.size() + " transportistas encontrados";
        lbl_cantResultados.setText(mensaje);
    }

    private void limpiarJTable() {
        modeloTablaResultados = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaResultados);
        setColumnas();
    }
    
    private void cambiarEstadoEnabled(boolean status) {
        chk_Nombre.setEnabled(status);
        if (status == true && chk_Nombre.isSelected() == true) {
            txt_Nombre.setEnabled(true);
        } else {
            txt_Nombre.setEnabled(false);
        }
        chk_Ubicacion.setEnabled(status);
        if (status == true && chk_Ubicacion.isSelected() == true) {
            cmb_Pais.setEnabled(true);
            cmb_Provincia.setEnabled(true);
            cmb_Localidad.setEnabled(true);
        } else {
            cmb_Pais.setEnabled(false);
            cmb_Provincia.setEnabled(false);
            cmb_Localidad.setEnabled(false);
        }
        btn_Buscar.setEnabled(status);        
        tbl_Resultados.setEnabled(status);
        btn_Nuevo.setEnabled(status);
        btn_Modificar.setEnabled(status);
        btn_Eliminar.setEnabled(status);
    }

    private void buscar() {
        this.cambiarEstadoEnabled(false);
        String criteria = "/transportistas/busqueda/criteria?";
        if (chk_Nombre.isSelected()) {
            criteria += "nombre=" + txt_Nombre.getText().trim() + "&";
        }
        if (chk_Ubicacion.isSelected()) {
            if (!((Pais) cmb_Pais.getSelectedItem()).getNombre().equals("Todos")) {
                criteria += "idPais=" + String.valueOf(((Pais) cmb_Pais.getSelectedItem()).getId_Pais()) + "&";
            }
            if (!((Provincia) (cmb_Provincia.getSelectedItem())).getNombre().equals("Todas")) {
                criteria += "idProvincia=" + String.valueOf(((Provincia) (cmb_Provincia.getSelectedItem())).getId_Provincia()) + "&";
            }
            if (!((Localidad) cmb_Localidad.getSelectedItem()).getNombre().equals("Todas")) {
                criteria += "idLocalidad=" + String.valueOf((((Localidad) cmb_Localidad.getSelectedItem()).getId_Localidad())) + "&";
            }
        }
        criteria += "idEmpresa=" + String.valueOf(EmpresaActiva.getInstance().getEmpresa().getId_Empresa());
        try {
            transportistas = new ArrayList(Arrays.asList(RestClient.getRestTemplate().getForObject(criteria, Transportista[].class)));
            this.cargarResultadosAlTable();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.cambiarEstadoEnabled(true);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                "Error", JOptionPane.ERROR_MESSAGE);
            this.cambiarEstadoEnabled(true);
        } 
        this.cambiarEstadoEnabled(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFiltros = new javax.swing.JPanel();
        chk_Nombre = new javax.swing.JCheckBox();
        txt_Nombre = new javax.swing.JTextField();
        chk_Ubicacion = new javax.swing.JCheckBox();
        cmb_Provincia = new javax.swing.JComboBox();
        cmb_Pais = new javax.swing.JComboBox();
        cmb_Localidad = new javax.swing.JComboBox();
        btn_Buscar = new javax.swing.JButton();
        lbl_cantResultados = new javax.swing.JLabel();
        panelResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btn_Nuevo = new javax.swing.JButton();
        btn_Modificar = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Transportistas");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Truck_16x16.png"))); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        chk_Nombre.setText("Nombre:");
        chk_Nombre.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_NombreItemStateChanged(evt);
            }
        });

        txt_Nombre.setEnabled(false);

        chk_Ubicacion.setText("Ubicación:");
        chk_Ubicacion.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_UbicacionItemStateChanged(evt);
            }
        });

        cmb_Provincia.setEnabled(false);
        cmb_Provincia.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_ProvinciaItemStateChanged(evt);
            }
        });

        cmb_Pais.setEnabled(false);
        cmb_Pais.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_PaisItemStateChanged(evt);
            }
        });

        cmb_Localidad.setEnabled(false);

        btn_Buscar.setForeground(java.awt.Color.blue);
        btn_Buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btn_Buscar.setText("Buscar");
        btn_Buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarActionPerformed(evt);
            }
        });

        lbl_cantResultados.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout panelFiltrosLayout = new javax.swing.GroupLayout(panelFiltros);
        panelFiltros.setLayout(panelFiltrosLayout);
        panelFiltrosLayout.setHorizontalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(chk_Ubicacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_Nombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmb_Provincia, 0, 351, Short.MAX_VALUE)
                            .addComponent(cmb_Pais, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmb_Localidad, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_Nombre)))
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addComponent(btn_Buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Nombre)
                    .addComponent(txt_Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Ubicacion)
                    .addComponent(cmb_Pais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmb_Provincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmb_Localidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_Buscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelResultados.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        tbl_Resultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_Resultados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sp_Resultados.setViewportView(tbl_Resultados);

        btn_Nuevo.setForeground(java.awt.Color.blue);
        btn_Nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddTruck_16x16.png"))); // NOI18N
        btn_Nuevo.setText("Nuevo");
        btn_Nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoActionPerformed(evt);
            }
        });

        btn_Modificar.setForeground(java.awt.Color.blue);
        btn_Modificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditTruck_16x16.png"))); // NOI18N
        btn_Modificar.setText("Modificar");
        btn_Modificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ModificarActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/RemoveTruck_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(btn_Nuevo)
                .addGap(0, 0, 0)
                .addComponent(btn_Modificar)
                .addGap(0, 0, 0)
                .addComponent(btn_Eliminar)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Eliminar, btn_Modificar, btn_Nuevo});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Nuevo)
                    .addComponent(btn_Modificar)
                    .addComponent(btn_Eliminar)))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Eliminar, btn_Modificar, btn_Nuevo});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chk_NombreItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_NombreItemStateChanged
        if (chk_Nombre.isSelected() == true) {
            txt_Nombre.setEnabled(true);
            txt_Nombre.requestFocus();
        } else {
            txt_Nombre.setEnabled(false);
        }
    }//GEN-LAST:event_chk_NombreItemStateChanged

    private void chk_UbicacionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_UbicacionItemStateChanged
        if (chk_Ubicacion.isSelected() == true) {
            cmb_Pais.setEnabled(true);
            cmb_Provincia.setEnabled(true);
            cmb_Localidad.setEnabled(true);
            cmb_Pais.requestFocus();
        } else {
            cmb_Pais.setEnabled(false);
            cmb_Provincia.setEnabled(false);
            cmb_Localidad.setEnabled(false);
        }
    }//GEN-LAST:event_chk_UbicacionItemStateChanged

    private void cmb_PaisItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_PaisItemStateChanged
        if (cmb_Pais.getItemCount() > 0) {
            if (!cmb_Pais.getSelectedItem().toString().equals("Todos")) {
                cargarComboBoxProvinciasDelPais((Pais) cmb_Pais.getSelectedItem());
            } else {
                cmb_Provincia.removeAllItems();
                Provincia provinciaTodas = new Provincia();
                provinciaTodas.setNombre("Todas");
                cmb_Provincia.addItem(provinciaTodas);
                cmb_Localidad.removeAllItems();
                Localidad localidadTodas = new Localidad();
                localidadTodas.setNombre("Todas");
                cmb_Localidad.addItem(localidadTodas);
            }
        }
    }//GEN-LAST:event_cmb_PaisItemStateChanged

    private void cmb_ProvinciaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_ProvinciaItemStateChanged
        if (cmb_Provincia.getItemCount() > 0) {
            if (!cmb_Provincia.getSelectedItem().toString().equals("Todas")) {
                cargarComboBoxLocalidadesDeLaProvincia((Provincia) cmb_Provincia.getSelectedItem());
            } else {
                cmb_Localidad.removeAllItems();
                Localidad localidadTodas = new Localidad();
                localidadTodas.setNombre("Todas");
                cmb_Localidad.addItem(localidadTodas);
            }
        } else {
            cmb_Localidad.removeAllItems();
        }
    }//GEN-LAST:event_cmb_ProvinciaItemStateChanged

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.buscar();
        if (transportistas.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                ResourceBundle.getBundle("Mensajes").getString("mensaje_busqueda_sin_resultados"),
                "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btn_BuscarActionPerformed

    private void btn_NuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoActionPerformed
        DetalleTransportistaGUI gui_DetalleTransportista = new DetalleTransportistaGUI();
        gui_DetalleTransportista.setModal(true);
        gui_DetalleTransportista.setLocationRelativeTo(this);
        gui_DetalleTransportista.setVisible(true);
        this.cargarComboBoxPaises();
    }//GEN-LAST:event_btn_NuevoActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Esta seguro que desea eliminar el transportista: "
                    + transportistas.get(indexFilaSeleccionada) + "?", "Eliminar",
                    JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    RestClient.getRestTemplate().delete("/transportistas/" + transportistas.get(indexFilaSeleccionada).getId_Transportista());
                    this.buscar();
                } catch (RestClientResponseException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (ResourceAccessException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_btn_EliminarActionPerformed

    private void btn_ModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ModificarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            DetalleTransportistaGUI gui_DetalleTransportista = new DetalleTransportistaGUI(transportistas.get(indexFilaSeleccionada));
            gui_DetalleTransportista.setModal(true);
            gui_DetalleTransportista.setLocationRelativeTo(this);
            gui_DetalleTransportista.setVisible(true);
            this.buscar();
            this.cargarComboBoxPaises();
        }
    }//GEN-LAST:event_btn_ModificarActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        this.setSize(sizeInternalFrame);
        this.cargarComboBoxPaises();
        this.setColumnas();
        try {
            this.setMaximum(true);
        } catch (PropertyVetoException ex) {
            String msjError = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(msjError + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, msjError, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formInternalFrameOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_Modificar;
    private javax.swing.JButton btn_Nuevo;
    private javax.swing.JCheckBox chk_Nombre;
    private javax.swing.JCheckBox chk_Ubicacion;
    private javax.swing.JComboBox cmb_Localidad;
    private javax.swing.JComboBox cmb_Pais;
    private javax.swing.JComboBox cmb_Provincia;
    private javax.swing.JLabel lbl_cantResultados;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JTextField txt_Nombre;
    // End of variables declaration//GEN-END:variables
}
