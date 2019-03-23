package sic.vista.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
import sic.modelo.Localidad;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.Provincia;
import sic.modelo.Rol;
import sic.modelo.UsuarioActivo;
import sic.util.DecimalesRenderer;
import sic.util.Utilidades;

public class LocalidadesGUI extends JInternalFrame {

    private List<Localidad> localidadesTotal = new ArrayList<>();
    private List<Localidad> localidadesParcial = new ArrayList<>();
    private ModeloTabla modeloTablaDeResultados = new ModeloTabla();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame = new Dimension(880, 600);
    private static int totalElementosBusqueda;
    private static int NUMERO_PAGINA = 0;

    public LocalidadesGUI() {
        this.initComponents();
        sp_Resultados.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int va = scrollBar.getVisibleAmount() + 10;
            if (scrollBar.getValue() >= (scrollBar.getMaximum() - va)) {
                if (localidadesTotal.size() >= 10) {
                    NUMERO_PAGINA += 1;
                    buscar();
                }
            }
        });
    }

    private void cargarComboBoxProvincias() {
        cmbProvincia.removeAllItems();
        try {
            List<Provincia> provincias = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/ubicaciones/provincias",
                            Provincia[].class)));
            provincias.stream().forEach((p) -> {
                cmbProvincia.addItem(p);
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
        //nombres de columnas
        String[] encabezados = new String[5];
        encabezados[0] = "Nombre";
        encabezados[1] = "Codigo Postal";
        encabezados[2] = "Provincia";
        encabezados[3] = "Envío Gratuito";
        encabezados[4] = "Costo Envío";
        modeloTablaDeResultados.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaDeResultados);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaDeResultados.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        tipos[2] = String.class;
        tipos[3] = Boolean.class;
        tipos[4] = BigDecimal.class;
        modeloTablaDeResultados.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);
        //tamanios de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(4).setCellRenderer(new DecimalesRenderer());
    }

    private void cargarResultadosAlTable() {
        localidadesParcial.stream().map(l -> {
            Object[] fila = new Object[5];
            fila[0] = l.getNombre();
            fila[1] = l.getCodigoPostal();
            fila[2] = l.getNombreProvincia();
            fila[3] = l.isEnvioGratuito();
            fila[4] = l.getCostoEnvio();
            return fila;
        }).forEach(fila -> {
            modeloTablaDeResultados.addRow(fila);
        });
        tbl_Resultados.setModel(modeloTablaDeResultados);
        lbl_cantResultados.setText(totalElementosBusqueda + " localidades encontradas");
    }

    private void resetScroll() {
        NUMERO_PAGINA = 0;
        localidadesTotal.clear();
        localidadesParcial.clear();
        Point p = new Point(0, 0);
        sp_Resultados.getViewport().setViewPosition(p);
    }

    private void limpiarJTable() {
        modeloTablaDeResultados = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaDeResultados);
        this.setColumnas();
    }

    private void cambiarEstadoEnabledComponentes(boolean status) {
        chkNombreLocalidad.setEnabled(status);
        if (status == true && chkNombreLocalidad.isSelected() == true) {
            txtCriteria.setEnabled(true);
        } else {
            txtCriteria.setEnabled(false);
        }
        chk_Provincia.setEnabled(status);
        if (status == true && chk_Provincia.isSelected() == true) {
            cmbProvincia.setEnabled(true);
        } else {
            cmbProvincia.setEnabled(false);
        }
        if (status == true && chkEnvio.isSelected() == true) {
            rbGratuito.setEnabled(true);
            rbNoGratuito.setEnabled(true);
        } else {
            rbGratuito.setEnabled(false);
            rbNoGratuito.setEnabled(false);
        }
        btn_Buscar.setEnabled(status);
        btnModificarLocalidad.setEnabled(status);
        tbl_Resultados.setEnabled(status);
        sp_Resultados.setEnabled(status);
        tbl_Resultados.requestFocus();
    }

    private void limpiarYBuscar() {
        this.resetScroll();
        this.limpiarJTable();
        this.buscar();
    }

    private void buscar() {
        this.cambiarEstadoEnabledComponentes(false);
        String criteriaBusqueda = "/ubicaciones/localidades/busqueda/criteria?";
        if (chk_Provincia.isSelected()) {
            criteriaBusqueda += "nombreProvincia=" + ((Provincia) cmbProvincia.getSelectedItem()).getNombre() + "&";
        }
        if (chkNombreLocalidad.isSelected()) {
            criteriaBusqueda += "nombreLocalidad=" + txtCriteria.getText().trim() + "&";
        }
        if (chkEnvio.isSelected()) {
            if (rbGratuito.isSelected()) {
                criteriaBusqueda += "envioGratuito=true&";
            } else if (rbNoGratuito.isSelected()) {
                criteriaBusqueda += "envioGratuito=false&";
            }
        }
        int seleccionOrden = cmbOrden.getSelectedIndex();
        switch (seleccionOrden) {
            case 0:
                criteriaBusqueda += "ordenarPor=nombre&";
                break;
            case 1:
                criteriaBusqueda += "ordenarPor=provincia.nombre&";
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
        criteriaBusqueda += "pagina=" + NUMERO_PAGINA;
        try {
            PaginaRespuestaRest<Localidad> response = RestClient.getRestTemplate()
                    .exchange(criteriaBusqueda, HttpMethod.GET, null,
                            new ParameterizedTypeReference<PaginaRespuestaRest<Localidad>>() {
                    })
                    .getBody();
            totalElementosBusqueda = response.getTotalElements();
            localidadesParcial = response.getContent();
            localidadesTotal.addAll(localidadesParcial);
            this.cargarResultadosAlTable();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        this.cambiarEstadoEnabledComponentes(true);
        this.cambiarEstadoDeComponentesSegunRolUsuario();
    }

    private void cambiarEstadoDeComponentesSegunRolUsuario() {
        List<Rol> rolesDeUsuarioActivo = UsuarioActivo.getInstance().getUsuario().getRoles();
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)
                || rolesDeUsuarioActivo.contains(Rol.ENCARGADO)) {
            btnModificarLocalidad.setEnabled(true);
        } else {
            btnModificarLocalidad.setEnabled(false);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgEnvio = new javax.swing.ButtonGroup();
        panelFiltros = new javax.swing.JPanel();
        chkNombreLocalidad = new javax.swing.JCheckBox();
        txtCriteria = new javax.swing.JTextField();
        chk_Provincia = new javax.swing.JCheckBox();
        cmbProvincia = new javax.swing.JComboBox<>();
        btn_Buscar = new javax.swing.JButton();
        lbl_cantResultados = new javax.swing.JLabel();
        rbGratuito = new javax.swing.JRadioButton();
        rbNoGratuito = new javax.swing.JRadioButton();
        chkEnvio = new javax.swing.JCheckBox();
        panelResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btnModificarLocalidad = new javax.swing.JButton();
        panelOrden = new javax.swing.JPanel();
        cmbOrden = new javax.swing.JComboBox<>();
        cmbSentido = new javax.swing.JComboBox<>();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Localidades");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Map_16x16.png"))); // NOI18N
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

        chkNombreLocalidad.setText("Nombre Localidad:");
        chkNombreLocalidad.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkNombreLocalidadItemStateChanged(evt);
            }
        });

        txtCriteria.setEnabled(false);
        txtCriteria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCriteriaActionPerformed(evt);
            }
        });

        chk_Provincia.setText("Provincia:");
        chk_Provincia.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ProvinciaItemStateChanged(evt);
            }
        });

        cmbProvincia.setEnabled(false);

        btn_Buscar.setForeground(java.awt.Color.blue);
        btn_Buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btn_Buscar.setText("Buscar");
        btn_Buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarActionPerformed(evt);
            }
        });

        lbl_cantResultados.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        bgEnvio.add(rbGratuito);
        rbGratuito.setText("Gratuito");
        rbGratuito.setEnabled(false);

        bgEnvio.add(rbNoGratuito);
        rbNoGratuito.setText("No Gratuito");
        rbNoGratuito.setEnabled(false);

        chkEnvio.setText("Envío:");
        chkEnvio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkEnvioItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panelFiltrosLayout = new javax.swing.GroupLayout(panelFiltros);
        panelFiltros.setLayout(panelFiltrosLayout);
        panelFiltrosLayout.setHorizontalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(chkNombreLocalidad, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_Provincia, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCriteria)
                            .addComponent(cmbProvincia, 0, 292, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkEnvio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbGratuito, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rbNoGratuito, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addComponent(btn_Buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmbProvincia, txtCriteria});

        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkNombreLocalidad)
                    .addComponent(txtCriteria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEnvio)
                    .addComponent(rbGratuito))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Provincia)
                    .addComponent(cmbProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbNoGratuito))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Buscar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmbProvincia, txtCriteria});

        panelResultados.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        tbl_Resultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resultados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sp_Resultados.setViewportView(tbl_Resultados);

        btnModificarLocalidad.setForeground(java.awt.Color.blue);
        btnModificarLocalidad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditMap_16x16.png"))); // NOI18N
        btnModificarLocalidad.setText("Modificar");
        btnModificarLocalidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarLocalidadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_Resultados)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(btnModificarLocalidad)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnModificarLocalidad))
        );

        panelOrden.setBorder(javax.swing.BorderFactory.createTitledBorder("Ordenar Por"));

        cmbOrden.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nombre", "Provincia" }));
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
                    .addComponent(cmbOrden, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbSentido, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelOrdenLayout.setVerticalGroup(
            panelOrdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOrdenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmbOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cmbSentido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkNombreLocalidadItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkNombreLocalidadItemStateChanged
        if (chkNombreLocalidad.isSelected() == true) {
            txtCriteria.setEnabled(true);
            txtCriteria.requestFocus();
        } else {
            txtCriteria.setEnabled(false);
        }
    }//GEN-LAST:event_chkNombreLocalidadItemStateChanged

    private void chk_ProvinciaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ProvinciaItemStateChanged
        if (chk_Provincia.isSelected()) {
            this.cargarComboBoxProvincias();
            cmbProvincia.setEnabled(true);
            cmbProvincia.requestFocus();
        } else {
            cmbProvincia.removeAllItems();
            cmbProvincia.setEnabled(false);
        }
    }//GEN-LAST:event_chk_ProvinciaItemStateChanged

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.limpiarYBuscar();
    }//GEN-LAST:event_btn_BuscarActionPerformed

    private void btnModificarLocalidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarLocalidadActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            ModificarLocalidadesGUI modificarLocalidadGUI = new ModificarLocalidadesGUI(localidadesTotal.get(indexFilaSeleccionada));
            modificarLocalidadGUI.setModal(true);
            modificarLocalidadGUI.setLocationRelativeTo(this);
            modificarLocalidadGUI.setVisible(true);
            this.limpiarYBuscar();
        }
    }//GEN-LAST:event_btnModificarLocalidadActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            this.setSize(sizeInternalFrame);
            this.setColumnas();
            this.setMaximum(true);
            this.cambiarEstadoDeComponentesSegunRolUsuario();
            rbGratuito.setSelected(true);
        } catch (PropertyVetoException ex) {
            String msjError = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(msjError + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, msjError, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void txtCriteriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCriteriaActionPerformed
        btn_BuscarActionPerformed(null);
    }//GEN-LAST:event_txtCriteriaActionPerformed

    private void cmbSentidoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSentidoItemStateChanged
        this.limpiarYBuscar();
    }//GEN-LAST:event_cmbSentidoItemStateChanged

    private void cmbOrdenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbOrdenItemStateChanged
        this.limpiarYBuscar();
    }//GEN-LAST:event_cmbOrdenItemStateChanged

    private void chkEnvioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkEnvioItemStateChanged
        if (chkEnvio.isSelected() == true) {
            rbGratuito.setEnabled(true);
            rbNoGratuito.setEnabled(true);
        } else {
            rbGratuito.setEnabled(false);
            rbNoGratuito.setEnabled(false);
        }
    }//GEN-LAST:event_chkEnvioItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgEnvio;
    private javax.swing.JButton btnModificarLocalidad;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JCheckBox chkEnvio;
    private javax.swing.JCheckBox chkNombreLocalidad;
    private javax.swing.JCheckBox chk_Provincia;
    private javax.swing.JComboBox<String> cmbOrden;
    private javax.swing.JComboBox<Provincia> cmbProvincia;
    private javax.swing.JComboBox<String> cmbSentido;
    private javax.swing.JLabel lbl_cantResultados;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelOrden;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JRadioButton rbGratuito;
    private javax.swing.JRadioButton rbNoGratuito;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JTextField txtCriteria;
    // End of variables declaration//GEN-END:variables
}
