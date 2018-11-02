package sic.vista.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.beans.PropertyVetoException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Cliente;
import sic.modelo.EmpresaActiva;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.Rol;
import sic.modelo.UsuarioActivo;
import sic.modelo.Usuario;
import sic.util.Utilidades;

public class UsuariosGUI extends JInternalFrame {

    private Usuario usuarioSeleccionado;    
    private ModeloTabla modeloTablaResultados = new ModeloTabla();    
    private List<Usuario> usuariosTotal = new ArrayList<>();
    private List<Usuario> usuariosParcial = new ArrayList<>();
    private static int totalElementosBusqueda;
    private static int NUMERO_PAGINA = 0;
    private static final int TAMANIO_PAGINA = 50;
    private final Dimension sizeInternalFrame =  new Dimension(880, 600);
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public UsuariosGUI() {
        this.initComponents();
        sp_resultados.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int va = scrollBar.getVisibleAmount() + 50;
            if (scrollBar.getValue() >= (scrollBar.getMaximum() - va)) {
                if (usuariosTotal.size() >= TAMANIO_PAGINA) {
                    NUMERO_PAGINA += 1;
                    buscar();
                }
            }
        });
    }
    
    private void resetScroll() {
        NUMERO_PAGINA = 0;
        usuariosTotal.clear();
        usuariosParcial.clear();
        Point p = new Point(0, 0);
        sp_resultados.getViewport().setViewPosition(p);
    }    

    private void buscar() {
        this.cambiarEstadoDeComponentes(false);
        String criteriaBusqueda = "/usuarios/busqueda/criteria?";
        if (chkCriteria.isSelected()) {
            criteriaBusqueda += "username=" + txtCriteria.getText().trim() + "&";
            criteriaBusqueda += "nombre=" + txtCriteria.getText().trim() + "&";
            criteriaBusqueda += "apellido=" + txtCriteria.getText().trim() + "&";
            criteriaBusqueda += "email=" + txtCriteria.getText().trim() + "&";
        }
        int seleccionOrden = cmbOrden.getSelectedIndex();
        switch (seleccionOrden) {
            case 0:
                criteriaBusqueda += "ordenarPor=nombre&";
                break;
            case 1:
                criteriaBusqueda += "ordenarPor=apellido&";
                break;
            case 2:
                criteriaBusqueda += "ordenarPor=username&";
                break;
        }
        int seleccionDireccion = cmbSentido.getSelectedIndex();
        switch (seleccionDireccion) {
            case 0:
                criteriaBusqueda += "sentido=ASC&";
                break;
            case 1:
                criteriaBusqueda += "sentido=DESC&";
                break;
        }
        criteriaBusqueda += "&pagina=" + NUMERO_PAGINA + "&tamanio=" + TAMANIO_PAGINA;
        try {
            PaginaRespuestaRest<Usuario> response = RestClient.getRestTemplate()
                    .exchange(criteriaBusqueda, HttpMethod.GET, null,
                            new ParameterizedTypeReference<PaginaRespuestaRest<Usuario>>() {})
                    .getBody();
            totalElementosBusqueda = response.getTotalElements();
            usuariosParcial = response.getContent();
            usuariosTotal.addAll(usuariosParcial);
            this.cargarRenglonesAlTable();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        this.cambiarEstadoDeComponentes(true);
    }
    
    private void cambiarEstadoDeComponentes(boolean status) {
        btn_Eliminar.setEnabled(status);
        btn_Modificar.setEnabled(status);
        btn_Agregar.setEnabled(status);
    }
    
    private void setColumnas() {               
        //nombres de columnas
        String[] encabezados = new String[10];
        encabezados[0] = "Habilitado";
        encabezados[1] = "Usuario";
        encabezados[2] = "Nombre";
        encabezados[3] = "Apellido";        
        encabezados[4] = "Email";
        encabezados[5] = "Administrador";
        encabezados[6] = "Encargado";
        encabezados[7] = "Vendedor";
        encabezados[8] = "Viajante";
        encabezados[9] = "Comprador";
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultado.setModel(modeloTablaResultados);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = Boolean.class;
        tipos[1] = String.class;
        tipos[2] = String.class;
        tipos[3] = String.class;
        tipos[4] = String.class;
        tipos[5] = Boolean.class;
        tipos[6] = Boolean.class;
        tipos[7] = Boolean.class;
        tipos[8] = Boolean.class;
        tipos[9] = Boolean.class;
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_Resultado.getTableHeader().setReorderingAllowed(false);
        tbl_Resultado.getTableHeader().setResizingAllowed(true);        
        //tamanios de columnas
        tbl_Resultado.getColumnModel().getColumn(0).setMinWidth(90);
        tbl_Resultado.getColumnModel().getColumn(0).setMaxWidth(90);
        tbl_Resultado.getColumnModel().getColumn(1).setMinWidth(130);
        tbl_Resultado.getColumnModel().getColumn(2).setMinWidth(130);
        tbl_Resultado.getColumnModel().getColumn(3).setMinWidth(130);
        tbl_Resultado.getColumnModel().getColumn(4).setPreferredWidth(350);
        tbl_Resultado.getColumnModel().getColumn(5).setPreferredWidth(150);
        tbl_Resultado.getColumnModel().getColumn(5).setMaxWidth(150);
        tbl_Resultado.getColumnModel().getColumn(6).setPreferredWidth(130);
        tbl_Resultado.getColumnModel().getColumn(6).setMaxWidth(130);        
        tbl_Resultado.getColumnModel().getColumn(7).setPreferredWidth(130);
        tbl_Resultado.getColumnModel().getColumn(7).setMaxWidth(130);
        tbl_Resultado.getColumnModel().getColumn(8).setPreferredWidth(130);
        tbl_Resultado.getColumnModel().getColumn(8).setMaxWidth(130);
        tbl_Resultado.getColumnModel().getColumn(9).setPreferredWidth(130);
        tbl_Resultado.getColumnModel().getColumn(9).setMaxWidth(130);
    }
    
    private void cargarRenglonesAlTable() {
        usuariosParcial.stream().map(u -> {
            Object[] fila = new Object[10];
            fila[0] = u.isHabilitado();
            fila[1] = u.getUsername();
            fila[2] = u.getNombre();
            fila[3] = u.getApellido();
            fila[4] = u.getEmail();
            u.getRoles().forEach(rol -> {
                if (Rol.ADMINISTRADOR.equals(rol)) {
                    fila[5] = true;
                }
                if (Rol.ENCARGADO.equals(rol)) {
                    fila[6] = true;
                }
                if (Rol.VENDEDOR.equals(rol)) {
                    fila[7] = true;
                }
                if (Rol.VIAJANTE.equals(rol)) {
                    fila[8] = true;
                }
                if (Rol.COMPRADOR.equals(rol)) {
                    fila[9] = true;
                }
            });
            return fila;
        }).forEach(fila -> {
            modeloTablaResultados.addRow(fila);
        });
        tbl_Resultado.setModel(modeloTablaResultados);
        lblCantResultados.setText(totalElementosBusqueda + " usuarios encontrados");
    }
    
    private void limpiarJTable() {
        modeloTablaResultados = new ModeloTabla();
        tbl_Resultado.setModel(modeloTablaResultados);
        this.setColumnas();
    }
    
    private boolean existeUsuarioSeleccionado() {
        if (tbl_Resultado.getSelectedRow() != -1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultado);
            usuarioSeleccionado = usuariosTotal.get(indexFilaSeleccionada);
            return true;
        }
        return false;
    }
    
    private void limpiarYBuscar() {
        this.resetScroll();
        this.limpiarJTable();
        this.buscar();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPrincipal = new javax.swing.JPanel();
        sp_resultados = new javax.swing.JScrollPane();
        tbl_Resultado = new javax.swing.JTable();
        btn_Agregar = new javax.swing.JButton();
        btn_Modificar = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();
        panelFiltros = new javax.swing.JPanel();
        chkCriteria = new javax.swing.JCheckBox();
        txtCriteria = new javax.swing.JTextField();
        btn_Buscar = new javax.swing.JButton();
        lblCantResultados = new javax.swing.JLabel();
        panelOrden = new javax.swing.JPanel();
        cmbOrden = new javax.swing.JComboBox<>();
        cmbSentido = new javax.swing.JComboBox<>();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Usuarios");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Group_16x16.png"))); // NOI18N
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

        panelPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        tbl_Resultado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resultado.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sp_resultados.setViewportView(tbl_Resultado);

        btn_Agregar.setForeground(java.awt.Color.blue);
        btn_Agregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddGroup_16x16.png"))); // NOI18N
        btn_Agregar.setText("Agregar");
        btn_Agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AgregarActionPerformed(evt);
            }
        });

        btn_Modificar.setForeground(java.awt.Color.blue);
        btn_Modificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditGroup_16x16.png"))); // NOI18N
        btn_Modificar.setText("Modificar");
        btn_Modificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ModificarActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/DeleteGroup_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_resultados)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addComponent(btn_Agregar, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btn_Modificar, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btn_Eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Agregar, btn_Eliminar, btn_Modificar});

        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addComponent(sp_resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Agregar)
                    .addComponent(btn_Modificar)
                    .addComponent(btn_Eliminar)))
        );

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Agregar, btn_Eliminar, btn_Modificar});

        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        chkCriteria.setText("Usuario, Nombre, Apellido, Email:");
        chkCriteria.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkCriteriaItemStateChanged(evt);
            }
        });

        txtCriteria.setEnabled(false);
        txtCriteria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCriteriaActionPerformed(evt);
            }
        });

        btn_Buscar.setForeground(java.awt.Color.blue);
        btn_Buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btn_Buscar.setText("Buscar");
        btn_Buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarActionPerformed(evt);
            }
        });

        lblCantResultados.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout panelFiltrosLayout = new javax.swing.GroupLayout(panelFiltros);
        panelFiltros.setLayout(panelFiltrosLayout);
        panelFiltrosLayout.setHorizontalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addComponent(btn_Buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addComponent(chkCriteria)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCriteria, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkCriteria)
                    .addComponent(txtCriteria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCantResultados, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Buscar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelOrden.setBorder(javax.swing.BorderFactory.createTitledBorder("Ordenar Por"));

        cmbOrden.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nombre", "Apellido", "Usuario" }));
        cmbOrden.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbOrdenItemStateChanged(evt);
            }
        });

        cmbSentido.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ascendente", "Descendente" }));
        cmbSentido.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbSentidoItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panelOrdenLayout = new javax.swing.GroupLayout(panelOrden);
        panelOrden.setLayout(panelOrdenLayout);
        panelOrdenLayout.setHorizontalGroup(
            panelOrdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOrdenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelOrdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbOrden, 0, 158, Short.MAX_VALUE)
                    .addComponent(cmbSentido, javax.swing.GroupLayout.Alignment.TRAILING, 0, 158, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelOrdenLayout.setVerticalGroup(
            panelOrdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOrdenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmbOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbSentido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelFiltros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelOrden, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        if (this.existeUsuarioSeleccionado()) {
            if (usuarioSeleccionado != null) {
                int respuesta;
                if (UsuarioActivo.getInstance().getUsuario().getId_Usuario() == usuarioSeleccionado.getId_Usuario()) {
                    respuesta = JOptionPane.showConfirmDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_eliminar_usuario_propio"),
                            "Eliminar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                } else {
                    respuesta = JOptionPane.showConfirmDialog(this,
                            MessageFormat.format(ResourceBundle.getBundle("Mensajes")
                                    .getString("mensaje_eliminar_usuario"),
                                    usuarioSeleccionado.getUsername()),
                            "Eliminar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                }
                if (respuesta == JOptionPane.YES_OPTION) {
                    try {
                        Cliente clienteRelacionado = RestClient.getRestTemplate()
                                .getForObject("/clientes/usuarios/"
                                        + usuarioSeleccionado.getId_Usuario()
                                        + "/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                                        Cliente.class);
                        if (clienteRelacionado != null) {
                            respuesta = JOptionPane.showConfirmDialog(this,
                                    MessageFormat.format(ResourceBundle.getBundle("Mensajes")
                                            .getString("mensaje_eliminar_usuario_con_cliente_asignado"),
                                            clienteRelacionado.getRazonSocial()),
                                    "Eliminar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        }
                        if (respuesta == JOptionPane.YES_OPTION) {
                            RestClient.getRestTemplate().delete("/usuarios/" + usuarioSeleccionado.getId_Usuario());
                            LOGGER.warn("El usuario " + usuarioSeleccionado.getUsername() + " se eliminó correctamente.");
                            usuarioSeleccionado = null;
                            this.resetScroll();
                            this.limpiarJTable();
                            this.buscar();
                        }
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
        }
    }//GEN-LAST:event_btn_EliminarActionPerformed

    private void btn_AgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AgregarActionPerformed
        DetalleUsuarioGUI gui_DetalleUsuario = new DetalleUsuarioGUI();
        gui_DetalleUsuario.setModal(true);
        gui_DetalleUsuario.setLocationRelativeTo(this);
        gui_DetalleUsuario.setVisible(true);
        this.resetScroll();
        this.limpiarJTable();
        this.buscar();
    }//GEN-LAST:event_btn_AgregarActionPerformed

    private void btn_ModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ModificarActionPerformed
        if (this.existeUsuarioSeleccionado()) {
            if (usuarioSeleccionado != null) {
                //Si el usuario activo corresponde con el usuario seleccionado para modificar
                int respuesta = JOptionPane.YES_OPTION;
                boolean mismoUsuarioActivo = false;
                if (UsuarioActivo.getInstance().getUsuario().getId_Usuario() == usuarioSeleccionado.getId_Usuario()) {
                    mismoUsuarioActivo = true;
                    respuesta = JOptionPane.showConfirmDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_modificar_el_usuario_propio"),
                            "Atención", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                }
                if (respuesta == JOptionPane.YES_OPTION) {
                    DetalleUsuarioGUI gui_DetalleUsuario = new DetalleUsuarioGUI(usuarioSeleccionado);
                    gui_DetalleUsuario.setModal(true);
                    gui_DetalleUsuario.setLocationRelativeTo(this);
                    gui_DetalleUsuario.setVisible(true);
                    if (mismoUsuarioActivo == true) {
                        UsuarioActivo.getInstance().setUsuario(usuarioSeleccionado);
                    }
                    this.resetScroll();
                    this.limpiarJTable();
                    this.buscar();
                }
            }
        }
    }//GEN-LAST:event_btn_ModificarActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        this.setSize(sizeInternalFrame);
        this.setColumnas();
        try {
            this.setMaximum(true);
        } catch (PropertyVetoException ex) {
            String mensaje = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }        
    }//GEN-LAST:event_formInternalFrameOpened

    private void chkCriteriaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkCriteriaItemStateChanged
        if (chkCriteria.isSelected() == true) {
            txtCriteria.setEnabled(true);
            txtCriteria.requestFocus();
        } else {
            txtCriteria.setEnabled(false);
        }
    }//GEN-LAST:event_chkCriteriaItemStateChanged

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.limpiarYBuscar();
    }//GEN-LAST:event_btn_BuscarActionPerformed

    private void txtCriteriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCriteriaActionPerformed
        btn_BuscarActionPerformed(null);
    }//GEN-LAST:event_txtCriteriaActionPerformed

    private void cmbOrdenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbOrdenItemStateChanged
        this.limpiarYBuscar();
    }//GEN-LAST:event_cmbOrdenItemStateChanged

    private void cmbSentidoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSentidoItemStateChanged
        this.limpiarYBuscar();
    }//GEN-LAST:event_cmbSentidoItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Agregar;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_Modificar;
    private javax.swing.JCheckBox chkCriteria;
    private javax.swing.JComboBox<String> cmbOrden;
    private javax.swing.JComboBox<String> cmbSentido;
    private javax.swing.JLabel lblCantResultados;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelOrden;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JScrollPane sp_resultados;
    private javax.swing.JTable tbl_Resultado;
    private javax.swing.JTextField txtCriteria;
    // End of variables declaration//GEN-END:variables
}
