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
import sic.modelo.Cliente;
import sic.modelo.EmpresaActiva;
import sic.modelo.Localidad;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.Pais;
import sic.modelo.Provincia;
import sic.modelo.Rol;
import sic.modelo.Usuario;
import sic.util.ColoresNumerosRenderer;
import sic.util.FechasRenderer;
import sic.util.FormatosFechaHora;
import sic.util.Utilidades;

public class ClientesGUI extends JInternalFrame {

    private List<Cliente> clientesTotal = new ArrayList<>();
    private List<Cliente> clientesParcial = new ArrayList<>();
    private ModeloTabla modeloTablaDeResultados = new ModeloTabla();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame =  new Dimension(880, 600);
    private static int totalElementosBusqueda;
    private static int NUMERO_PAGINA = 0;
    private static final int TAMANIO_PAGINA = 50;

    public ClientesGUI() {
        this.initComponents();        
        sp_Resultados.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int va = scrollBar.getVisibleAmount() + 50;
            if (scrollBar.getValue() >= (scrollBar.getMaximum() - va)) {
                if (clientesTotal.size() >= TAMANIO_PAGINA) {
                    NUMERO_PAGINA += 1;
                    buscar();
                }
            }
        });
    }

    private void cargarComboBoxPaises() {
        cmbPais.removeAllItems();
        try {
            List<Pais> paises = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/paises", Pais[].class)));
            Pais paisTodos = new Pais();
            paisTodos.setNombre("Todos");
            cmbPais.addItem(paisTodos);
            paises.stream().forEach(p -> {
                cmbPais.addItem(p);
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
    
    private void cargarComboBoxViajantes() {
        cmbViajante.removeAllItems();
        try {
            PaginaRespuestaRest<Usuario> response = RestClient.getRestTemplate()
                    .exchange("/usuarios/busqueda/criteria?"
                            + "roles=" + Rol.VIAJANTE, HttpMethod.GET, null,
                            new ParameterizedTypeReference<PaginaRespuestaRest<Usuario>>() {
                    })
                    .getBody();
            response.getContent().stream().forEach(v -> {
                cmbViajante.addItem(v);
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

    private void cargarComboBoxProvinciasDelPais() {
        cmbProvincia.removeAllItems();
        try {
            List<Provincia> provincias = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/provincias/paises/" + ((Pais) cmbPais.getSelectedItem()).getId_Pais(),
                    Provincia[].class)));
            Provincia provinciaTodas = new Provincia();
            provinciaTodas.setNombre("Todas");
            cmbProvincia.addItem(provinciaTodas);
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

    private void cargarComboBoxLocalidadesDeLaProvincia() {
        cmbLocalidad.removeAllItems();
        try {
            List<Localidad> Localidades = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/localidades/provincias/" + ((Provincia) cmbProvincia.getSelectedItem()).getId_Provincia(),
                    Localidad[].class)));
            Localidad localidadTodas = new Localidad();
            localidadTodas.setNombre("Todas");
            cmbLocalidad.addItem(localidadTodas);
            Localidades.stream().forEach((l) -> {
                cmbLocalidad.addItem(l);
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
        // Momentaneamente desactivado hasta terminar la paginacion.
        // sorting
        // tbl_Resultados.setAutoCreateRowSorter(true);
        //nombres de columnas
        String[] encabezados = new String[17];
        encabezados[0] = "Predeterminado";
        encabezados[1] = "ID Fiscal";
        encabezados[2] = "Razon Social";
        encabezados[3] = "Nombre Fantasia";
        encabezados[4] = "Saldo C/C";
        encabezados[5] = "Ultimo Movimiento C/C";
        encabezados[6] = "Viajante";
        encabezados[7] = "Direccion";
        encabezados[8] = "Condicion IVA";
        encabezados[9] = "Tel. Primario";
        encabezados[10] = "Tel. Secundario";
        encabezados[11] = "Contacto";
        encabezados[12] = "Email";
        encabezados[13] = "Fecha Alta";
        encabezados[14] = "Localidad";
        encabezados[15] = "Provincia";
        encabezados[16] = "Pais";
        modeloTablaDeResultados.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaDeResultados);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaDeResultados.getColumnCount()];
        tipos[0] = Boolean.class;
        tipos[1] = String.class;
        tipos[2] = String.class;
        tipos[3] = String.class;
        tipos[4] = BigDecimal.class;
        tipos[5] = Date.class;
        tipos[6] = String.class;
        tipos[7] = String.class;
        tipos[8] = String.class;
        tipos[9] = String.class;
        tipos[10] = String.class;
        tipos[11] = String.class;
        tipos[12] = String.class;
        tipos[13] = Date.class;
        tipos[14] = String.class;
        tipos[15] = String.class;
        tipos[16] = String.class;
        modeloTablaDeResultados.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);        
        //tamanios de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(110);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(110);
        tbl_Resultados.getColumnModel().getColumn(5).setPreferredWidth(150);
        tbl_Resultados.getColumnModel().getColumn(6).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(7).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(8).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(9).setPreferredWidth(150);
        tbl_Resultados.getColumnModel().getColumn(10).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(11).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(12).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(13).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(14).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(15).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(16).setPreferredWidth(200);        
        //renderers
        tbl_Resultados.getColumnModel().getColumn(4).setCellRenderer(new ColoresNumerosRenderer());
        tbl_Resultados.getColumnModel().getColumn(5).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHAHORA_HISPANO));
        tbl_Resultados.getColumnModel().getColumn(13).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHA_HISPANO));
    }

    private void cargarResultadosAlTable() {
        clientesParcial.stream().map(cliente -> {
            Object[] fila = new Object[17];
            fila[0] = cliente.isPredeterminado();
            fila[1] = cliente.getIdFiscal();
            fila[2] = cliente.getRazonSocial();
            fila[3] = cliente.getNombreFantasia(); 
            fila[4] = cliente.getSaldoCuentaCorriente();
            fila[5] = cliente.getFechaUltimoMovimiento();
            if (cliente.getViajante() != null) fila[6] = cliente.getViajante().toString();
            fila[7] = cliente.getDireccion();
            fila[8] = cliente.getCondicionIVA().getNombre();
            fila[9] = cliente.getTelPrimario();
            fila[10] = cliente.getTelSecundario();            
            fila[11] = cliente.getContacto();
            fila[12] = cliente.getEmail();
            fila[13] = cliente.getFechaAlta();
            fila[14] = cliente.getLocalidad().getNombre();
            fila[15] = cliente.getLocalidad().getProvincia().getNombre();
            fila[16] = cliente.getLocalidad().getProvincia().getPais().getNombre();
            return fila;
        }).forEach(fila -> {
            modeloTablaDeResultados.addRow(fila);
        });        
        tbl_Resultados.setModel(modeloTablaDeResultados);                
        lbl_cantResultados.setText(totalElementosBusqueda + " clientes encontrados");
    }
    
    private void resetScroll() {
        NUMERO_PAGINA = 0;
        clientesTotal.clear();
        clientesParcial.clear();
        Point p = new Point(0, 0);
        sp_Resultados.getViewport().setViewPosition(p);
    }

    private void limpiarJTable() {
        modeloTablaDeResultados = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaDeResultados);
        this.setColumnas();
    }

    private void cambiarEstadoEnabledComponentes(boolean status) {
        chkCriteria.setEnabled(status);
        if (status == true && chkCriteria.isSelected() == true) {
            txtCriteria.setEnabled(true);
        } else {
            txtCriteria.setEnabled(false);
        }        
        chk_Ubicacion.setEnabled(status);
        if (status == true && chk_Ubicacion.isSelected() == true) {
            cmbPais.setEnabled(true);
            cmbProvincia.setEnabled(true);
            cmbLocalidad.setEnabled(true);
        } else {
            cmbPais.setEnabled(false);
            cmbProvincia.setEnabled(false);
            cmbLocalidad.setEnabled(false);
        }
        btn_Buscar.setEnabled(status);  
        btnCuentaCorriente.setEnabled(status);
        btn_Nuevo.setEnabled(status);
        btn_Eliminar.setEnabled(status);
        btn_Modificar.setEnabled(status);
        btn_setPredeterminado.setEnabled(status);
        tbl_Resultados.setEnabled(status);
        sp_Resultados.setEnabled(status);
        tbl_Resultados.requestFocus();
    }
    
    private void buscar() {
        this.cambiarEstadoEnabledComponentes(false);
        String criteriaBusqueda = "/clientes/busqueda/criteria?";
        if (chkCriteria.isSelected()) {
            criteriaBusqueda += "razonSocial=" + txtCriteria.getText().trim() + "&";
            criteriaBusqueda += "nombreFantasia=" + txtCriteria.getText().trim() + "&";
            criteriaBusqueda += "idFiscal=" + txtCriteria.getText().trim() + "&";
        }
        if (chkViajante.isSelected()) {
            criteriaBusqueda += "idViajante=" + ((Usuario) cmbViajante.getSelectedItem()).getId_Usuario() + "&";
        }
        if (chk_Ubicacion.isSelected()) {
            if (!((Pais) cmbPais.getSelectedItem()).getNombre().equals("Todos")) {
                criteriaBusqueda += "idPais=" + String.valueOf(((Pais) cmbPais.getSelectedItem()).getId_Pais()) + "&";
            }
            if (!((Provincia) (cmbProvincia.getSelectedItem())).getNombre().equals("Todas")) {
                criteriaBusqueda += "idProvincia=" + String.valueOf(((Provincia) (cmbProvincia.getSelectedItem())).getId_Provincia()) + "&";
            }
            if (!((Localidad) cmbLocalidad.getSelectedItem()).getNombre().equals("Todas")) {
                criteriaBusqueda += "idLocalidad=" + String.valueOf((((Localidad) cmbLocalidad.getSelectedItem()).getId_Localidad())) + "&";
            }
        }
        criteriaBusqueda += "idEmpresa=" + String.valueOf(EmpresaActiva.getInstance().getEmpresa().getId_Empresa());
        criteriaBusqueda += "&pagina=" + NUMERO_PAGINA + "&tamanio=" + TAMANIO_PAGINA;
        try {
            PaginaRespuestaRest<Cliente> response = RestClient.getRestTemplate()
                    .exchange(criteriaBusqueda, HttpMethod.GET, null,
                            new ParameterizedTypeReference<PaginaRespuestaRest<Cliente>>() {})
                    .getBody();
            totalElementosBusqueda = response.getTotalElements();
            clientesParcial = response.getContent();
            clientesTotal.addAll(clientesParcial);
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
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFiltros = new javax.swing.JPanel();
        chkCriteria = new javax.swing.JCheckBox();
        txtCriteria = new javax.swing.JTextField();
        chk_Ubicacion = new javax.swing.JCheckBox();
        cmbProvincia = new javax.swing.JComboBox();
        cmbPais = new javax.swing.JComboBox();
        cmbLocalidad = new javax.swing.JComboBox();
        btn_Buscar = new javax.swing.JButton();
        lbl_cantResultados = new javax.swing.JLabel();
        chkViajante = new javax.swing.JCheckBox();
        cmbViajante = new javax.swing.JComboBox();
        panelResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btn_Nuevo = new javax.swing.JButton();
        btn_Modificar = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();
        btn_setPredeterminado = new javax.swing.JButton();
        btnCuentaCorriente = new javax.swing.JButton();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Clientes");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Client_16x16.png"))); // NOI18N
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

        chkCriteria.setText("ID Fiscal, Razon Social, Nombre Fantasia:");
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

        chk_Ubicacion.setText("Ubicación:");
        chk_Ubicacion.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_UbicacionItemStateChanged(evt);
            }
        });

        cmbProvincia.setEnabled(false);
        cmbProvincia.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbProvinciaItemStateChanged(evt);
            }
        });

        cmbPais.setEnabled(false);
        cmbPais.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbPaisItemStateChanged(evt);
            }
        });

        cmbLocalidad.setEnabled(false);

        btn_Buscar.setForeground(java.awt.Color.blue);
        btn_Buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btn_Buscar.setText("Buscar");
        btn_Buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarActionPerformed(evt);
            }
        });

        lbl_cantResultados.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        chkViajante.setText("Viajante:");
        chkViajante.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkViajanteItemStateChanged(evt);
            }
        });

        cmbViajante.setEnabled(false);

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
                        .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(chkViajante, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chkCriteria, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_Ubicacion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbLocalidad, 0, 302, Short.MAX_VALUE)
                            .addComponent(cmbProvincia, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbPais, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtCriteria)
                            .addComponent(cmbViajante, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkCriteria)
                    .addComponent(txtCriteria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkViajante)
                    .addComponent(cmbViajante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Ubicacion)
                    .addComponent(cmbPais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbLocalidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Buscar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        btn_Nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddClient_16x16.png"))); // NOI18N
        btn_Nuevo.setText("Nuevo");
        btn_Nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoActionPerformed(evt);
            }
        });

        btn_Modificar.setForeground(java.awt.Color.blue);
        btn_Modificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditClient_16x16.png"))); // NOI18N
        btn_Modificar.setText("Modificar");
        btn_Modificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ModificarActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/DeleteClient_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
            }
        });

        btn_setPredeterminado.setForeground(java.awt.Color.blue);
        btn_setPredeterminado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/ClientArrow_16x16.png"))); // NOI18N
        btn_setPredeterminado.setText("Establecer como Predeterminado");
        btn_setPredeterminado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_setPredeterminadoActionPerformed(evt);
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
            .addComponent(sp_Resultados)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(btn_Nuevo)
                .addGap(0, 0, 0)
                .addComponent(btn_Modificar)
                .addGap(0, 0, 0)
                .addComponent(btn_Eliminar)
                .addGap(0, 0, 0)
                .addComponent(btn_setPredeterminado)
                .addGap(0, 0, 0)
                .addComponent(btnCuentaCorriente)
                .addGap(0, 148, Short.MAX_VALUE))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Eliminar, btn_Modificar, btn_Nuevo});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btn_setPredeterminado)
                    .addComponent(btnCuentaCorriente)
                    .addComponent(btn_Eliminar)
                    .addComponent(btn_Modificar)
                    .addComponent(btn_Nuevo)))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCuentaCorriente, btn_Eliminar, btn_Modificar, btn_Nuevo, btn_setPredeterminado});

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

    private void chkCriteriaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkCriteriaItemStateChanged
        if (chkCriteria.isSelected() == true) {
            txtCriteria.setEnabled(true);
            txtCriteria.requestFocus();
        } else {
            txtCriteria.setEnabled(false);
        }
    }//GEN-LAST:event_chkCriteriaItemStateChanged

    private void chk_UbicacionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_UbicacionItemStateChanged
        if (chk_Ubicacion.isSelected()) {
            this.cargarComboBoxPaises();
            cmbPais.setEnabled(true);
            cmbProvincia.setEnabled(true);
            cmbLocalidad.setEnabled(true);
            cmbPais.requestFocus();
        } else {
            cmbPais.removeAllItems();
            cmbPais.setEnabled(false);
            cmbProvincia.removeAllItems();
            cmbProvincia.setEnabled(false);
            cmbLocalidad.setEnabled(false);
            cmbLocalidad.removeAllItems();
        }
    }//GEN-LAST:event_chk_UbicacionItemStateChanged

    private void cmbPaisItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbPaisItemStateChanged
        if (cmbPais.getItemCount() > 0) {
            if (!cmbPais.getSelectedItem().toString().equals("Todos")) {
                this.cargarComboBoxProvinciasDelPais();
            } else {
                cmbProvincia.removeAllItems();
                Provincia provinciaTodas = new Provincia();
                provinciaTodas.setNombre("Todas");
                cmbProvincia.addItem(provinciaTodas);
                cmbLocalidad.removeAllItems();
                Localidad localidadTodas = new Localidad();
                localidadTodas.setNombre("Todas");
                cmbLocalidad.addItem(localidadTodas);
            }
        }
    }//GEN-LAST:event_cmbPaisItemStateChanged

    private void cmbProvinciaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbProvinciaItemStateChanged
        if (cmbProvincia.getItemCount() > 0) {
            if (!cmbProvincia.getSelectedItem().toString().equals("Todas")) {
                cargarComboBoxLocalidadesDeLaProvincia();
            } else {
                cmbLocalidad.removeAllItems();
                Localidad localidadTodas = new Localidad();
                localidadTodas.setNombre("Todas");
                cmbLocalidad.addItem(localidadTodas);
            }
        } else {
            cmbLocalidad.removeAllItems();
        }
    }//GEN-LAST:event_cmbProvinciaItemStateChanged

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.resetScroll();
        this.limpiarJTable();
        this.buscar();
    }//GEN-LAST:event_btn_BuscarActionPerformed

    private void btn_NuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoActionPerformed
        DetalleClienteGUI gui_DetalleCliente = new DetalleClienteGUI();
        gui_DetalleCliente.setModal(true);
        gui_DetalleCliente.setLocationRelativeTo(this);
        gui_DetalleCliente.setVisible(true);
        this.resetScroll();
        this.limpiarJTable();
        this.buscar();
    }//GEN-LAST:event_btn_NuevoActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Esta seguro que desea eliminar el cliente: "
                    + clientesTotal.get(indexFilaSeleccionada) + "?",
                    "Eliminar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    RestClient.getRestTemplate().delete("/clientes/" + clientesTotal.get(indexFilaSeleccionada).getId_Cliente());
                    this.resetScroll();
                    this.limpiarJTable();
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
            DetalleClienteGUI gui_DetalleCliente = new DetalleClienteGUI(clientesTotal.get(indexFilaSeleccionada));
            gui_DetalleCliente.setModal(true);
            gui_DetalleCliente.setLocationRelativeTo(this);
            gui_DetalleCliente.setVisible(true);
            this.resetScroll();
            this.limpiarJTable();
            this.buscar();
        }
    }//GEN-LAST:event_btn_ModificarActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            this.setSize(sizeInternalFrame);
            this.setColumnas();
            this.setMaximum(true);
            RestClient.getRestTemplate().getForObject("/clientes/predeterminado/empresas/"
                    + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(), Cliente.class);
            if ((RestClient.getRestTemplate().getForObject("/clientes/predeterminado/empresas/"
                    + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(), Cliente.class)) == null) {
                JOptionPane.showInternalMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_no_existe_cliente_predeterminado"),
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            }
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

    private void btn_setPredeterminadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_setPredeterminadoActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            try {
                int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);                
                Cliente cliente = RestClient.getRestTemplate()
                        .getForObject("/clientes/" + clientesTotal.get(indexFilaSeleccionada).getId_Cliente(), Cliente.class);
                if (cliente != null) {
                    RestClient.getRestTemplate().put("/clientes/" + cliente.getId_Cliente() + "/predeterminado", null);
                    btn_BuscarActionPerformed(evt);
                } else {
                    JOptionPane.showInternalMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_no_se_encontro_cliente_predeterminado"),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showInternalMessageDialog(this,
                ResourceBundle.getBundle("Mensajes").getString("mensaje_seleccionar_cliente"),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_setPredeterminadoActionPerformed

    private void btnCuentaCorrienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCuentaCorrienteActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            Cliente cliente = RestClient.getRestTemplate()
                    .getForObject("/clientes/" + clientesTotal.get(indexFilaSeleccionada).getId_Cliente(), Cliente.class);
            JInternalFrame gui;
            if (cliente != null) {
                gui = new CuentaCorrienteGUI(cliente);
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

    private void chkViajanteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkViajanteItemStateChanged
        if (chkViajante.isSelected()) {
            this.cargarComboBoxViajantes();
            cmbViajante.setEnabled(true);            
            cmbViajante.requestFocus();
        } else {
            cmbViajante.removeAllItems();
            cmbViajante.setEnabled(false);            
        }
    }//GEN-LAST:event_chkViajanteItemStateChanged

    private void txtCriteriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCriteriaActionPerformed
        btn_BuscarActionPerformed(null);
    }//GEN-LAST:event_txtCriteriaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCuentaCorriente;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_Modificar;
    private javax.swing.JButton btn_Nuevo;
    private javax.swing.JButton btn_setPredeterminado;
    private javax.swing.JCheckBox chkCriteria;
    private javax.swing.JCheckBox chkViajante;
    private javax.swing.JCheckBox chk_Ubicacion;
    private javax.swing.JComboBox cmbLocalidad;
    private javax.swing.JComboBox cmbPais;
    private javax.swing.JComboBox cmbProvincia;
    private javax.swing.JComboBox cmbViajante;
    private javax.swing.JLabel lbl_cantResultados;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JTextField txtCriteria;
    // End of variables declaration//GEN-END:variables
}
