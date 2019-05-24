package sic.vista.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import sic.modelo.CuentaCorrienteProveedor;
import sic.modelo.EmpresaActiva;
import sic.modelo.Localidad;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.Proveedor;
import sic.modelo.Provincia;
import sic.modelo.Rol;
import sic.modelo.UsuarioActivo;
import sic.util.ColoresNumerosRenderer;
import sic.util.FechasRenderer;
import sic.util.FormatosFechaHora;
import sic.util.Utilidades;

public class ProveedoresGUI extends JInternalFrame {

    private ModeloTabla modeloTablaResultados = new ModeloTabla();
    private List<CuentaCorrienteProveedor> cuentasCorrienteProveedoresTotal = new ArrayList<>();
    private List<CuentaCorrienteProveedor> cuentasCorrienteProveedoresParcial = new ArrayList<>();
    private Proveedor proveedorSeleccionado;
    private static int totalElementosBusqueda;
    private static int NUMERO_PAGINA = 0;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame = new Dimension(880, 600);

    public ProveedoresGUI() {
        this.initComponents();
        sp_Resultados.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int va = scrollBar.getVisibleAmount() + 10;
            if (scrollBar.getValue() >= (scrollBar.getMaximum() - va)) {
                if (cuentasCorrienteProveedoresTotal.size() >= 10) {
                    NUMERO_PAGINA += 1;
                    buscar();
                }
            }
        });
    }

    public Proveedor getProvSeleccionado() {
        return proveedorSeleccionado;
    }

    public void setProvSeleccionado(Proveedor provSeleccionado) {
        this.proveedorSeleccionado = provSeleccionado;
    }

    private void cargarComboBoxProvincias() {
        cmb_Provincia.removeAllItems();
        try {
            List<Provincia> provincias = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/ubicaciones/provincias",
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
                    .getForObject("/ubicaciones/localidades/provincias/" + provSeleccionada.getIdProvincia(), Localidad[].class)));
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
        //nombres de columnas
        String[] encabezados = new String[12];
        encabezados[0] = "Nro Proveedor";
        encabezados[1] = "ID Fiscal";
        encabezados[2] = "Razon Social";
        encabezados[3] = "Saldo C/C";
        encabezados[4] = "Ultimo Movimiento C/C";
        encabezados[5] = "Condicion IVA";
        encabezados[6] = "Tel. Primario";
        encabezados[7] = "Tel. Secundario";
        encabezados[8] = "Contacto";
        encabezados[9] = "Email";
        encabezados[10] = "Web";
        encabezados[11] = "Ubicacion";
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaResultados);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        tipos[2] = String.class;
        tipos[3] = BigDecimal.class;
        tipos[4] = Date.class;
        tipos[5] = String.class;
        tipos[6] = String.class;
        tipos[7] = String.class;
        tipos[8] = String.class;
        tipos[9] = String.class;
        tipos[10] = String.class;
        tipos[11] = String.class;
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);
        //tamanios de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(300);
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(110);
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(150);
        tbl_Resultados.getColumnModel().getColumn(5).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(6).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(7).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(8).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(9).setPreferredWidth(240);
        tbl_Resultados.getColumnModel().getColumn(10).setPreferredWidth(240);
        tbl_Resultados.getColumnModel().getColumn(11).setPreferredWidth(440);
        //renderers
        tbl_Resultados.getColumnModel().getColumn(3).setCellRenderer(new ColoresNumerosRenderer());
        tbl_Resultados.setDefaultRenderer(Date.class, new FechasRenderer(FormatosFechaHora.FORMATO_FECHAHORA_HISPANO));
    }

    private void cargarResultadosAlTable() {
        cuentasCorrienteProveedoresParcial.stream().map(p -> {
            Object[] fila = new Object[12];
            fila[0] = p.getProveedor().getNroProveedor();
            fila[1] = p.getProveedor().getIdFiscal();
            fila[2] = p.getProveedor().getRazonSocial();
            fila[3] = p.getSaldo();
            fila[4] = p.getFechaUltimoMovimiento();
            fila[5] = p.getProveedor().getCategoriaIVA();
            fila[6] = p.getProveedor().getTelPrimario();
            fila[7] = p.getProveedor().getTelSecundario();
            fila[8] = p.getProveedor().getContacto();
            fila[9] = p.getProveedor().getEmail();
            fila[10] = p.getProveedor().getWeb();
            fila[11] = p.getProveedor().getUbicacion();
            return fila;
        }).forEach(f -> {
            modeloTablaResultados.addRow(f);
        });
        tbl_Resultados.setModel(modeloTablaResultados);
        String mensaje = totalElementosBusqueda + " proveedores encontrados";
        lbl_cantResultados.setText(mensaje);
    }

    private void resetScroll() {
        NUMERO_PAGINA = 0;
        cuentasCorrienteProveedoresTotal.clear();
        cuentasCorrienteProveedoresParcial.clear();
        Point p = new Point(0, 0);
        sp_Resultados.getViewport().setViewPosition(p);
    }

    private void limpiarJTable() {
        modeloTablaResultados = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaResultados);
        this.setColumnas();
    }

    private void cambiarEstadoEnabled(boolean status) {
        chkNroProveedorORazonSocial.setEnabled(status);
        if (status == true && chkNroProveedorORazonSocial.isSelected() == true) {
            txtNroProveedorORazonSocial.setEnabled(true);
        } else {
            txtNroProveedorORazonSocial.setEnabled(false);
        }
        chk_Ubicacion.setEnabled(status);
        if (status == true && chk_Ubicacion.isSelected() == true) {
            cmb_Provincia.setEnabled(true);
            cmb_Localidad.setEnabled(true);
        } else {
            cmb_Provincia.setEnabled(false);
            cmb_Localidad.setEnabled(false);
        }
        btn_Buscar.setEnabled(status);
        tbl_Resultados.setEnabled(status);
        btn_Nuevo.setEnabled(status);
        btn_Modificar.setEnabled(status);
        btn_Eliminar.setEnabled(status);
        tbl_Resultados.requestFocus();
    }

    private void limpiarYBuscar() {
        this.resetScroll();
        this.limpiarJTable();
        this.buscar();
    }

    private void buscar() {
        this.cambiarEstadoEnabled(false);
        String criteria = "/cuentas-corriente/proveedores/busqueda/criteria?";
        if (chkNroProveedorORazonSocial.isSelected()) {
            criteria += "nroProveedor=" + txtNroProveedorORazonSocial.getText().trim()
                    + "&razonSocial=" + txtNroProveedorORazonSocial.getText().trim() + "&";
        }
        if (chk_Ubicacion.isSelected()) {
            criteria += "idProvincia=" + String.valueOf(((Provincia) (cmb_Provincia.getSelectedItem())).getIdProvincia()) + "&";
            if (!((Localidad) cmb_Localidad.getSelectedItem()).getNombre().equals("Todas")) {
                criteria += "idLocalidad=" + String.valueOf((((Localidad) cmb_Localidad.getSelectedItem()).getIdLocalidad())) + "&";
            }
        }
        int seleccionOrden = cmbOrden.getSelectedIndex();
        switch (seleccionOrden) {
            case 0:
                criteria += "ordenarPor=proveedor.razonSocial&";
                break;
            case 1:
                criteria += "ordenarPor=saldo&";
                break;
            case 2:
                criteria += "ordenarPor=fechaUltimoMovimiento&";
                break;
        }
        int seleccionDireccion = cmbSentido.getSelectedIndex();
        switch (seleccionDireccion) {
            case 0:
                criteria += "sentido=ASC&";
                break;
            case 1:
                criteria += "sentido=DESC&";
                break;
        }
        criteria += "idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa();
        criteria += "&pagina=" + NUMERO_PAGINA;
        try {
            PaginaRespuestaRest<CuentaCorrienteProveedor> response = RestClient.getRestTemplate()
                    .exchange(criteria, HttpMethod.GET, null, new ParameterizedTypeReference<PaginaRespuestaRest<CuentaCorrienteProveedor>>() {
                    })
                    .getBody();
            totalElementosBusqueda = response.getTotalElements();
            cuentasCorrienteProveedoresParcial = response.getContent();
            cuentasCorrienteProveedoresTotal.addAll(cuentasCorrienteProveedoresParcial);
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
        this.cambiarEstadoDeComponentesSegunRolUsuario();
    }

    private void cambiarEstadoDeComponentesSegunRolUsuario() {
        List<Rol> rolesDeUsuarioActivo = UsuarioActivo.getInstance().getUsuario().getRoles();
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)) {
            btn_Eliminar.setEnabled(true);
        } else {
            btn_Eliminar.setEnabled(false);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFiltros = new javax.swing.JPanel();
        chkNroProveedorORazonSocial = new javax.swing.JCheckBox();
        txtNroProveedorORazonSocial = new javax.swing.JTextField();
        chk_Ubicacion = new javax.swing.JCheckBox();
        cmb_Provincia = new javax.swing.JComboBox();
        cmb_Localidad = new javax.swing.JComboBox();
        btn_Buscar = new javax.swing.JButton();
        lbl_cantResultados = new javax.swing.JLabel();
        panelResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btn_Nuevo = new javax.swing.JButton();
        btn_Modificar = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();
        btnCuentaCorriente = new javax.swing.JButton();
        panelOrden = new javax.swing.JPanel();
        cmbOrden = new javax.swing.JComboBox<>();
        cmbSentido = new javax.swing.JComboBox<>();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Proveedores");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/ProviderBag_16x16.png"))); // NOI18N
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

        chkNroProveedorORazonSocial.setText("Nº Proveedor o Razón Social:");
        chkNroProveedorORazonSocial.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkNroProveedorORazonSocialItemStateChanged(evt);
            }
        });

        txtNroProveedorORazonSocial.setEnabled(false);
        txtNroProveedorORazonSocial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNroProveedorORazonSocialActionPerformed(evt);
            }
        });

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
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkNroProveedorORazonSocial)
                            .addComponent(txtNroProveedorORazonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                        .addComponent(chk_Ubicacion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmb_Localidad, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmb_Provincia, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addComponent(cmb_Provincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_Ubicacion)
                    .addComponent(chkNroProveedorORazonSocial))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmb_Localidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNroProveedorORazonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_Buscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

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

        btn_Nuevo.setForeground(java.awt.Color.blue);
        btn_Nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddProviderBag_16x16.png"))); // NOI18N
        btn_Nuevo.setText("Nuevo");
        btn_Nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoActionPerformed(evt);
            }
        });

        btn_Modificar.setForeground(java.awt.Color.blue);
        btn_Modificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditProviderBag_16x16.png"))); // NOI18N
        btn_Modificar.setText("Modificar");
        btn_Modificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ModificarActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/DeleteProviderBag_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
            }
        });

        btnCuentaCorriente.setForeground(java.awt.Color.blue);
        btnCuentaCorriente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/CC_16x16.png"))); // NOI18N
        btnCuentaCorriente.setText("Cuenta Corriente");
        btnCuentaCorriente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCuentaCorrienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(btn_Nuevo)
                .addGap(0, 0, 0)
                .addComponent(btn_Modificar)
                .addGap(0, 0, 0)
                .addComponent(btn_Eliminar)
                .addGap(0, 0, 0)
                .addComponent(btnCuentaCorriente)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(sp_Resultados)
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCuentaCorriente, btn_Eliminar, btn_Modificar, btn_Nuevo});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCuentaCorriente, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_Eliminar, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_Modificar, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_Nuevo, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        panelOrden.setBorder(javax.swing.BorderFactory.createTitledBorder("Ordenar Por"));

        cmbOrden.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Razón Social", "Saldo C/C", "Ultimo Movimiento C/C" }));
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
                .addComponent(cmbOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbSentido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void chkNroProveedorORazonSocialItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkNroProveedorORazonSocialItemStateChanged
        //Pregunta el estado actual del checkBox
        if (chkNroProveedorORazonSocial.isSelected() == true) {
            txtNroProveedorORazonSocial.setEnabled(true);
            txtNroProveedorORazonSocial.requestFocus();
        } else {
            txtNroProveedorORazonSocial.setEnabled(false);
        }
    }//GEN-LAST:event_chkNroProveedorORazonSocialItemStateChanged

    private void chk_UbicacionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_UbicacionItemStateChanged
        //Pregunta el estado actual del checkBox
        if (chk_Ubicacion.isSelected() == true) {
            this.cargarComboBoxProvincias();
            cmb_Provincia.setEnabled(true);
            cmb_Localidad.setEnabled(true);
            cmb_Provincia.requestFocus();
        } else {
            cmb_Provincia.removeAllItems();
            cmb_Provincia.setEnabled(false);
            cmb_Localidad.removeAllItems();
            cmb_Localidad.setEnabled(false);
        }
    }//GEN-LAST:event_chk_UbicacionItemStateChanged

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
        this.limpiarYBuscar();
    }//GEN-LAST:event_btn_BuscarActionPerformed

    private void btn_NuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoActionPerformed
        DetalleProveedorGUI gui_DetalleProveedor = new DetalleProveedorGUI();
        gui_DetalleProveedor.setModal(true);
        gui_DetalleProveedor.setLocationRelativeTo(this);
        gui_DetalleProveedor.setVisible(true);
        this.limpiarYBuscar();
    }//GEN-LAST:event_btn_NuevoActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Esta seguro que desea eliminar el proveedor: "
                    + cuentasCorrienteProveedoresTotal.get(indexFilaSeleccionada).getProveedor() + "?", "Eliminar",
                    JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    RestClient.getRestTemplate().delete("/proveedores/" + cuentasCorrienteProveedoresTotal.get(indexFilaSeleccionada).getProveedor().getId_Proveedor());
                    this.limpiarYBuscar();
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
            DetalleProveedorGUI gui_DetalleProveedor = new DetalleProveedorGUI(cuentasCorrienteProveedoresTotal.get(indexFilaSeleccionada).getProveedor());
            gui_DetalleProveedor.setModal(true);
            gui_DetalleProveedor.setLocationRelativeTo(this);
            gui_DetalleProveedor.setVisible(true);
            this.limpiarYBuscar();
        }
    }//GEN-LAST:event_btn_ModificarActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        this.setSize(sizeInternalFrame);
        this.setColumnas();
        try {
            this.setMaximum(true);
            this.cambiarEstadoDeComponentesSegunRolUsuario();
        } catch (PropertyVetoException ex) {
            String msjError = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(msjError + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, msjError, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnCuentaCorrienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCuentaCorrienteActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            Proveedor proveedor = RestClient.getRestTemplate()
                    .getForObject("/proveedores/" + cuentasCorrienteProveedoresTotal.get(indexFilaSeleccionada).getProveedor().getId_Proveedor(), Proveedor.class);
            JInternalFrame gui;
            if (proveedor != null) {
                gui = new CuentaCorrienteGUI(proveedor);
                gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                        getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
                getDesktopPane().add(gui);
                gui.setVisible(true);
                try {
                    gui.setSelected(true);
                } catch (PropertyVetoException ex) {
                    String msjError = "No se pudo seleccionar la ventana requerida.";
                    LOGGER.error(msjError + " - " + ex.getMessage());
                    JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_btnCuentaCorrienteActionPerformed

    private void txtNroProveedorORazonSocialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNroProveedorORazonSocialActionPerformed
        btn_BuscarActionPerformed(null);
    }//GEN-LAST:event_txtNroProveedorORazonSocialActionPerformed

    private void cmbOrdenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbOrdenItemStateChanged
        this.limpiarYBuscar();
    }//GEN-LAST:event_cmbOrdenItemStateChanged

    private void cmbSentidoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSentidoItemStateChanged
        this.limpiarYBuscar();
    }//GEN-LAST:event_cmbSentidoItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCuentaCorriente;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_Modificar;
    private javax.swing.JButton btn_Nuevo;
    private javax.swing.JCheckBox chkNroProveedorORazonSocial;
    private javax.swing.JCheckBox chk_Ubicacion;
    private javax.swing.JComboBox<String> cmbOrden;
    private javax.swing.JComboBox<String> cmbSentido;
    private javax.swing.JComboBox cmb_Localidad;
    private javax.swing.JComboBox cmb_Provincia;
    private javax.swing.JLabel lbl_cantResultados;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelOrden;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JTextField txtNroProveedorORazonSocial;
    // End of variables declaration//GEN-END:variables
}
