package sic.vista.swing;

import java.awt.Dimension;
import java.beans.PropertyVetoException;
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
import sic.modelo.Rol;
import sic.modelo.UsuarioActivo;
import sic.modelo.Usuario;
import sic.util.RenderTabla;
import sic.util.Utilidades;

public class UsuariosGUI extends JInternalFrame {

    private Usuario usuarioSeleccionado;
    private boolean mismoUsuarioActivo = false;
    private ModeloTabla modeloTablaResultados = new ModeloTabla();
    private List<Usuario> usuarios;
    private final Dimension sizeInternalFrame =  new Dimension(880, 600);
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public UsuariosGUI() {
        this.initComponents();       
    }

    private void comprobarPrivilegiosUsuarioActivo() {
        //Comprueba si el usuario es Administrador
        if (UsuarioActivo.getInstance().getUsuario().getRoles().contains(Rol.ADMINISTRADOR) == true) {
            this.cargarUsuarios();            
            this.cargarRenglonesAlTable();
        } else {
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_privilegios_usuario"),
                    "Error", JOptionPane.ERROR_MESSAGE);            
            this.dispose();
        }
    }

    private void cargarUsuarios() {
        try {
            usuarios = Arrays.asList(RestClient.getRestTemplate().getForObject("/usuarios", Usuario[].class));
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
        //sort
        tbl_Resultado.setAutoCreateRowSorter(true);
        
        //nombres de columnas
        String[] encabezados = new String[8];
        encabezados[0] = "Habilitado";
        encabezados[1] = "Nombre";
        encabezados[2] = "Apellido";
        encabezados[3] = "Usuario";
        encabezados[4] = "Email";
        encabezados[5] = "Administrador";
        encabezados[6] = "Viajante";
        encabezados[7] = "Vendedor";
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
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_Resultado.getTableHeader().setReorderingAllowed(false);
        tbl_Resultado.getTableHeader().setResizingAllowed(true);

        //render para los tipos de datos
        tbl_Resultado.setDefaultRenderer(Double.class, new RenderTabla());

        //tamanios de columnas
        tbl_Resultado.getColumnModel().getColumn(0).setMinWidth(100);
        tbl_Resultado.getColumnModel().getColumn(0).setMaxWidth(100);
        tbl_Resultado.getColumnModel().getColumn(1).setMinWidth(130);
        tbl_Resultado.getColumnModel().getColumn(2).setMinWidth(130);
        tbl_Resultado.getColumnModel().getColumn(3).setMinWidth(130);
        tbl_Resultado.getColumnModel().getColumn(4).setPreferredWidth(350);
        tbl_Resultado.getColumnModel().getColumn(5).setPreferredWidth(130);
        tbl_Resultado.getColumnModel().getColumn(5).setMaxWidth(130);
        tbl_Resultado.getColumnModel().getColumn(6).setPreferredWidth(130);
        tbl_Resultado.getColumnModel().getColumn(6).setMaxWidth(130);
        tbl_Resultado.getColumnModel().getColumn(7).setPreferredWidth(130);
        tbl_Resultado.getColumnModel().getColumn(7).setMaxWidth(130);
    }
    
    private void cargarRenglonesAlTable() {
        this.limpiarJTable();
        usuarios.stream().map(u -> {
            Object[] fila = new Object[8];
            fila[0] = u.isHabilitado();
            fila[1] = u.getNombre();
            fila[2] = u.getApellido();
            fila[3] = u.getUsername();
            fila[4] = u.getEmail();
            List<Rol> roles = u.getRoles();
            for (Rol rol : roles) {
                if (Rol.ADMINISTRADOR.equals(rol)) {
                    fila[5] = true;
                }
                if (Rol.VIAJANTE.equals(rol)) {
                    fila[6] = true;
                }
                if (Rol.VENDEDOR.equals(rol)) {
                    fila[7] = true;
                }
            }
            return fila;
        }).forEach(fila -> {
            modeloTablaResultados.addRow(fila);
        });
        tbl_Resultado.setModel(modeloTablaResultados);
    }
    
    private void limpiarJTable() {
        modeloTablaResultados = new ModeloTabla();
        tbl_Resultado.setModel(modeloTablaResultados);
        this.setColumnas();
    }
    
    private boolean existeUsuarioSeleccionado() {
        if (tbl_Resultado.getSelectedRow() != -1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultado);
            usuarioSeleccionado = usuarios.get(indexFilaSeleccionada);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPrincipal = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_Resultado = new javax.swing.JTable();
        btn_Agregar = new javax.swing.JButton();
        btn_Modificar = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();

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

        panelPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder("Usuarios"));

        tbl_Resultado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resultado.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(tbl_Resultado);

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
        );

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_Agregar, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_Modificar, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_Eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Agregar, btn_Eliminar, btn_Modificar});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Agregar)
                    .addComponent(btn_Modificar)
                    .addComponent(btn_Eliminar))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Agregar, btn_Eliminar, btn_Modificar});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        if (this.existeUsuarioSeleccionado()) {
            try {
                if (usuarioSeleccionado != null) {
                    //Si el usuario activo corresponde con el usuario seleccionado para modificar
                    int respuesta;
                    if (UsuarioActivo.getInstance().getUsuario().getId_Usuario() == usuarioSeleccionado.getId_Usuario()) {
                        respuesta = JOptionPane.showConfirmDialog(this,
                                ResourceBundle.getBundle("Mensajes").getString("mensaje_eliminar_usuario_propio"),
                                "Eliminar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    } else {
                        respuesta = JOptionPane.showConfirmDialog(this,
                                ResourceBundle.getBundle("Mensajes").getString("mensaje_eliminar_usuario")
                                + usuarioSeleccionado.getNombre() + "?",
                                "Eliminar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    }

                    if (respuesta == JOptionPane.YES_OPTION) {
                        RestClient.getRestTemplate().delete("/usuarios/" + usuarioSeleccionado.getId_Usuario());
                        LOGGER.warn("El usuario " + usuarioSeleccionado.getNombre() + " se elimino correctamente.");
                        usuarioSeleccionado = null;
                        this.cargarUsuarios();
                        this.cargarRenglonesAlTable();
                    }
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
    }//GEN-LAST:event_btn_EliminarActionPerformed

    private void btn_AgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AgregarActionPerformed
        DetalleUsuarioGUI gui_DetalleUsuario = new DetalleUsuarioGUI();
        gui_DetalleUsuario.setModal(true);
        gui_DetalleUsuario.setLocationRelativeTo(this);
        gui_DetalleUsuario.setVisible(true);
        this.cargarUsuarios();
        this.cargarRenglonesAlTable();
    }//GEN-LAST:event_btn_AgregarActionPerformed

    private void btn_ModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ModificarActionPerformed
        if (existeUsuarioSeleccionado()) {        
            if (usuarioSeleccionado != null) {
                //Si el usuario activo corresponde con el usuario seleccionado para modificar
                int respuesta = JOptionPane.YES_OPTION;            
                if (UsuarioActivo.getInstance().getUsuario().getNombre().equals(usuarioSeleccionado.getNombre())) {
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
                    this.cargarUsuarios();
                    this.cargarRenglonesAlTable();
                }
            }
        }
    }//GEN-LAST:event_btn_ModificarActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        this.setSize(sizeInternalFrame);
        try {
            this.setMaximum(true);
        } catch (PropertyVetoException ex) {
            String mensaje = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
        this.comprobarPrivilegiosUsuarioActivo(); 
    }//GEN-LAST:event_formInternalFrameOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Agregar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_Modificar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JTable tbl_Resultado;
    // End of variables declaration//GEN-END:variables
}
