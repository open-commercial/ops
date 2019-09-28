package sic.vista.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.ParseException;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.criteria.BusquedaFacturaCompraCriteria;
import sic.modelo.EmpresaActiva;
import sic.modelo.FacturaCompra;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.TipoDeComprobante;
import sic.modelo.TipoDeOperacion;
import sic.util.DecimalesRenderer;
import sic.util.FechasRenderer;
import sic.util.FormatosFechaHora;
import sic.util.Utilidades;

public class FacturasCompraGUI extends JInternalFrame {

    private ModeloTabla modeloTablaFacturas = new ModeloTabla();
    private List<FacturaCompra> facturasTotal = new ArrayList<>();
    private List<FacturaCompra> facturasParcial =  new ArrayList<>();
    private Proveedor proveedorSeleccionado;
    private Producto productoSeleccionado;
    private static int totalElementosBusqueda;
    private static int NUMERO_PAGINA = 0;    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame =  new Dimension(970, 600);

    public FacturasCompraGUI() {
        this.initComponents();        
        sp_Resultados.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int va = scrollBar.getVisibleAmount() + 10;
            if (scrollBar.getValue() >= (scrollBar.getMaximum() - va)) {
                if (facturasTotal.size() >= 10) {
                    NUMERO_PAGINA += 1;
                    buscar(false);
                }
            }
        });
    }
    
    public void buscarPorSerieYNroFactura(long nroSerie, long nroFactura,
            TipoDeComprobante tipoDeComprobante, long idProveedor) {
        chk_NumFactura.setSelected(true);
        txt_SerieFactura.setEnabled(true);
        txt_NroFactura.setEnabled(true);
        txt_SerieFactura.setText(String.valueOf(nroSerie));
        txt_NroFactura.setText(String.valueOf(nroFactura));
        chk_TipoFactura.setSelected(true);
        cmb_TipoFactura.setSelectedItem(tipoDeComprobante);
        proveedorSeleccionado = RestClient.getRestTemplate()
                .getForObject("/proveedores/" + idProveedor,
                        Proveedor.class);
        txtProveedor.setText(proveedorSeleccionado.getRazonSocial());
        chk_Proveedor.setSelected(true);
        this.limpiarJTable();
        this.buscar(true);
    }

    private void setColumnas() {
        //nombres de columnas
        String[] encabezados = new String[16];
        encabezados[0] = "Fecha Factura";
        encabezados[1] = "Tipo";
        encabezados[2] = "Nº Factura";
        encabezados[3] = "Fecha Vencimiento";
        encabezados[4] = "Proveedor";
        encabezados[5] = "Usuario";
        encabezados[6] = "Transportista";
        encabezados[7] = "Total";
        encabezados[8] = "SubTotal";
        encabezados[9] = "% Descuento";
        encabezados[10] = "Descuento neto";
        encabezados[11] = "% Recargo";
        encabezados[12] = "Recargo neto";
        encabezados[13] = "SubTotal bruto";
        encabezados[14] = "IVA 10.5% neto";
        encabezados[15] = "IVA 21% neto";
        modeloTablaFacturas.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaFacturas);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaFacturas.getColumnCount()];
        tipos[0] = Date.class;
        tipos[1] = TipoDeOperacion.class;
        tipos[2] = String.class;
        tipos[3] = Date.class;
        tipos[4] = String.class;
        tipos[5] = String.class;
        tipos[6] = String.class;
        tipos[7] = BigDecimal.class;
        tipos[8] = BigDecimal.class;
        tipos[9] = BigDecimal.class;
        tipos[10] = BigDecimal.class;
        tipos[11] = BigDecimal.class;
        tipos[12] = BigDecimal.class;
        tipos[13] = BigDecimal.class;
        tipos[14] = BigDecimal.class;
        tipos[15] = BigDecimal.class;
        modeloTablaFacturas.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);        
        //Tamanios de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(130);
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(5).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(6).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(7).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(8).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(9).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(10).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(11).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(12).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(13).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(14).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(15).setPreferredWidth(120);
        //renderers        
        tbl_Resultados.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());
        tbl_Resultados.setDefaultRenderer(Date.class, new FechasRenderer(FormatosFechaHora.FORMATO_FECHA_HISPANO));
    }

    private void cambiarEstadoEnabledComponentes(boolean status) {
        chk_Fecha.setEnabled(status);
        if (status == true && chk_Fecha.isSelected() == true) {
            dc_FechaDesde.setEnabled(true);
            dc_FechaHasta.setEnabled(true);
        } else {
            dc_FechaDesde.setEnabled(false);
            dc_FechaHasta.setEnabled(false);
        }
        chk_Proveedor.setEnabled(status);
        if (status == true && chk_Proveedor.isSelected() == true) {
            btnBuscarProveedor.setEnabled(true);
        } else {
            btnBuscarProveedor.setEnabled(false);
        }
        chk_NumFactura.setEnabled(status);
        if (status == true && chk_NumFactura.isSelected() == true) {
            txt_SerieFactura.setEnabled(true);
            txt_NroFactura.setEnabled(true);
        } else {
            txt_SerieFactura.setEnabled(false);
            txt_NroFactura.setEnabled(false);
        }
        btn_Buscar.setEnabled(status);
        tbl_Resultados.setEnabled(status);
        sp_Resultados.setEnabled(status);
        btnNuevaFactura.setEnabled(status);
        btn_VerDetalle.setEnabled(status);
        tbl_Resultados.requestFocus();
    }
    
    private void buscar(boolean calcularResultados) {
        this.cambiarEstadoEnabledComponentes(false);
        BusquedaFacturaCompraCriteria criteria = new BusquedaFacturaCompraCriteria();
        criteria.setIdEmpresa(EmpresaActiva.getInstance().getEmpresa().getId_Empresa());
        if (chk_Fecha.isSelected()) {
            criteria.setFechaDesde((dc_FechaDesde.getDate() != null) ? dc_FechaDesde.getDate() : null);
            criteria.setFechaHasta((dc_FechaHasta.getDate() != null) ? dc_FechaHasta.getDate() : null);
        }
        if (chk_Proveedor.isSelected() && proveedorSeleccionado != null) {
            criteria.setIdProveedor(proveedorSeleccionado.getId_Proveedor());
        }
        if (chk_TipoFactura.isSelected()) {
            criteria.setTipoComprobante(((TipoDeComprobante) cmb_TipoFactura.getSelectedItem()));
        }
        if (chk_NumFactura.isSelected()) {
            try {
                txt_SerieFactura.commitEdit();
                txt_NroFactura.commitEdit();
            } catch (ParseException ex) {
                LOGGER.error(ex.getMessage());
            }
            criteria.setNumSerie(Long.valueOf(txt_SerieFactura.getValue().toString()));
            criteria.setNumFactura(Long.valueOf(txt_NroFactura.getValue().toString()));
        }
        if (chk_Producto.isSelected() && productoSeleccionado != null) {
            criteria.setIdProducto(productoSeleccionado.getIdProducto());
        }
        int seleccionOrden = cmbOrden.getSelectedIndex();
        switch (seleccionOrden) {
            case 0:
                criteria.setOrdenarPor("fecha");
                break;
            case 1:
                criteria.setOrdenarPor("total");
                break;
        }
        int seleccionDireccion = cmbSentido.getSelectedIndex();
        switch (seleccionDireccion) {
            case 0:
                criteria.setSentido("DESC");
                break;
            case 1:
                criteria.setSentido("ASC");
                break;
        }
        criteria.setPagina(NUMERO_PAGINA);
        String criteriaBusqueda = "/facturas/compra/busqueda/criteria";
        try {
            if (calcularResultados) {
               this.calcularResultados(criteria);
            }
            HttpEntity<BusquedaFacturaCompraCriteria> requestEntity = new HttpEntity<>(criteria);
            PaginaRespuestaRest<FacturaCompra> response = RestClient.getRestTemplate()
                    .exchange(criteriaBusqueda, HttpMethod.POST, requestEntity,
                            new ParameterizedTypeReference<PaginaRespuestaRest<FacturaCompra>>() {
                    })
                    .getBody();
            totalElementosBusqueda = response.getTotalElements();
            facturasParcial = response.getContent();
            facturasTotal.addAll(facturasParcial);
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

    private void calcularResultados(BusquedaFacturaCompraCriteria criteria) {
        txt_ResultGastoTotal.setValue(RestClient.getRestTemplate()
                .postForObject("/facturas/total-facturado-compra/criteria",criteria, BigDecimal.class));
        txt_ResultTotalIVACompra.setValue(RestClient.getRestTemplate()
                .postForObject("/facturas/total-iva-compra/criteria", criteria, BigDecimal.class));
    }

    private void cargarResultadosAlTable() {
        facturasParcial.stream().map(factura -> {
            Object[] fila = new Object[16];
            fila[0] = factura.getFecha();
            fila[1] = factura.getTipoComprobante();
            fila[2] = factura.getNumSerie() + " - " + factura.getNumFactura();
            fila[3] = factura.getFechaVencimiento();
            fila[4] = factura.getRazonSocialProveedor();
            fila[5] = factura.getNombreUsuario();
            fila[6] = factura.getNombreTransportista();
            fila[7] = factura.getTotal();
            fila[8] = factura.getSubTotal();
            fila[9] = factura.getDescuentoPorcentaje();
            fila[10] = factura.getDescuentoNeto();
            fila[11] = factura.getRecargoPorcentaje();
            fila[12] = factura.getRecargoNeto();
            fila[13] = factura.getSubTotalBruto();
            fila[14] = factura.getIva105Neto();
            fila[15] = factura.getIva21Neto();
            return fila;
        }).forEach(fila -> {
            modeloTablaFacturas.addRow(fila);
        });
        tbl_Resultados.setModel(modeloTablaFacturas);
        String mensaje = totalElementosBusqueda + " facturas encontradas";
        lbl_CantRegistrosEncontrados.setText(mensaje);
    }

    private void limpiarJTable() {
        NUMERO_PAGINA = 0;
        facturasTotal.clear();
        facturasParcial.clear();
        Point p = new Point(0, 0);
        sp_Resultados.getViewport().setViewPosition(p);
        modeloTablaFacturas = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaFacturas);
        this.setColumnas();
    }

    private void limpiarYBuscar(boolean calcularResultados) {
        this.limpiarJTable();
        this.buscar(calcularResultados);
    }

    private boolean existeProveedorDisponible() {
        return !Arrays.asList(RestClient.getRestTemplate()
            .getForObject("/proveedores/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
            Proveedor[].class)).isEmpty();
    }
       
    private void cargarTiposDeFactura() {
        try {
            TipoDeComprobante[] tiposDeComprobantes = RestClient.getRestTemplate()
                    .getForObject("/facturas/tipos/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                            TipoDeComprobante[].class);
            for (int i = 0; tiposDeComprobantes.length > i; i++) {
                cmb_TipoFactura.addItem(tiposDeComprobantes[i]);
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
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFiltros = new javax.swing.JPanel();
        btn_Buscar = new javax.swing.JButton();
        lbl_CantRegistrosEncontrados = new javax.swing.JLabel();
        panelFiltros1 = new javax.swing.JPanel();
        dc_FechaDesde = new com.toedter.calendar.JDateChooser();
        lbl_Hasta = new javax.swing.JLabel();
        lbl_Desde = new javax.swing.JLabel();
        chk_Fecha = new javax.swing.JCheckBox();
        dc_FechaHasta = new com.toedter.calendar.JDateChooser();
        chk_Proveedor = new javax.swing.JCheckBox();
        txtProveedor = new javax.swing.JTextField();
        btnBuscarProveedor = new javax.swing.JButton();
        txtProducto = new javax.swing.JTextField();
        btnBuscarProductos = new javax.swing.JButton();
        chk_Producto = new javax.swing.JCheckBox();
        panelFiltros2 = new javax.swing.JPanel();
        chk_TipoFactura = new javax.swing.JCheckBox();
        cmb_TipoFactura = new javax.swing.JComboBox();
        chk_NumFactura = new javax.swing.JCheckBox();
        txt_NroFactura = new javax.swing.JFormattedTextField();
        txt_SerieFactura = new javax.swing.JFormattedTextField();
        lblSeparador = new javax.swing.JLabel();
        panelResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btnNuevaFactura = new javax.swing.JButton();
        btn_VerDetalle = new javax.swing.JButton();
        lbl_TotalIVACompra = new javax.swing.JLabel();
        txt_ResultTotalIVACompra = new javax.swing.JFormattedTextField();
        txt_ResultGastoTotal = new javax.swing.JFormattedTextField();
        lbl_TotalFacturado = new javax.swing.JLabel();
        btnCrearNotaCredito = new javax.swing.JButton();
        panelOrden = new javax.swing.JPanel();
        cmbOrden = new javax.swing.JComboBox<>();
        cmbSentido = new javax.swing.JComboBox<>();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Facturas de Compra");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/SIC_16_square.png"))); // NOI18N
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

        btn_Buscar.setForeground(java.awt.Color.blue);
        btn_Buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btn_Buscar.setText("Buscar");
        btn_Buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarActionPerformed(evt);
            }
        });

        lbl_CantRegistrosEncontrados.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        dc_FechaDesde.setDateFormatString("dd/MM/yyyy");
        dc_FechaDesde.setEnabled(false);

        lbl_Hasta.setText("Hasta:");
        lbl_Hasta.setEnabled(false);

        lbl_Desde.setText("Desde:");
        lbl_Desde.setEnabled(false);

        chk_Fecha.setText("Fecha Factura:");
        chk_Fecha.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FechaItemStateChanged(evt);
            }
        });

        dc_FechaHasta.setDateFormatString("dd/MM/yyyy");
        dc_FechaHasta.setEnabled(false);

        chk_Proveedor.setText("Proveedor:");
        chk_Proveedor.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ProveedorItemStateChanged(evt);
            }
        });

        txtProveedor.setEditable(false);
        txtProveedor.setEnabled(false);
        txtProveedor.setOpaque(false);

        btnBuscarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btnBuscarProveedor.setEnabled(false);
        btnBuscarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarProveedorActionPerformed(evt);
            }
        });

        txtProducto.setEditable(false);
        txtProducto.setEnabled(false);
        txtProducto.setOpaque(false);

        btnBuscarProductos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btnBuscarProductos.setEnabled(false);
        btnBuscarProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarProductosActionPerformed(evt);
            }
        });

        chk_Producto.setText("Producto:");
        chk_Producto.setToolTipText("");
        chk_Producto.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ProductoItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panelFiltros1Layout = new javax.swing.GroupLayout(panelFiltros1);
        panelFiltros1.setLayout(panelFiltros1Layout);
        panelFiltros1Layout.setHorizontalGroup(
            panelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltros1Layout.createSequentialGroup()
                .addGroup(panelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chk_Fecha, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_Proveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_Producto, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFiltros1Layout.createSequentialGroup()
                        .addComponent(lbl_Desde)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_Hasta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelFiltros1Layout.createSequentialGroup()
                        .addGroup(panelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtProducto)
                            .addComponent(txtProveedor))
                        .addGap(0, 0, 0)
                        .addGroup(panelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnBuscarProveedor, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnBuscarProductos, javax.swing.GroupLayout.Alignment.TRAILING)))))
        );

        panelFiltros1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {dc_FechaDesde, dc_FechaHasta});

        panelFiltros1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {chk_Fecha, chk_Producto, chk_Proveedor});

        panelFiltros1Layout.setVerticalGroup(
            panelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltros1Layout.createSequentialGroup()
                .addGroup(panelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Hasta)
                    .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Desde)
                    .addComponent(chk_Fecha))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Proveedor)
                    .addComponent(txtProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarProveedor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Producto)
                    .addComponent(txtProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarProductos)))
        );

        panelFiltros1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {chk_Fecha, chk_Producto, chk_Proveedor});

        panelFiltros1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscarProveedor, txtProveedor});

        panelFiltros1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscarProductos, txtProducto});

        chk_TipoFactura.setText("Tipo de Factura:");
        chk_TipoFactura.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_TipoFacturaItemStateChanged(evt);
            }
        });

        cmb_TipoFactura.setEnabled(false);

        chk_NumFactura.setText("Nº Factura:");
        chk_NumFactura.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_NumFacturaItemStateChanged(evt);
            }
        });

        txt_NroFactura.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_NroFactura.setText("0");
        txt_NroFactura.setEnabled(false);
        txt_NroFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_NroFacturaActionPerformed(evt);
            }
        });
        txt_NroFactura.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_NroFacturaKeyTyped(evt);
            }
        });

        txt_SerieFactura.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_SerieFactura.setText("0");
        txt_SerieFactura.setEnabled(false);
        txt_SerieFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_SerieFacturaActionPerformed(evt);
            }
        });
        txt_SerieFactura.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_SerieFacturaKeyTyped(evt);
            }
        });

        lblSeparador.setFont(new java.awt.Font("DejaVu Sans", 0, 15)); // NOI18N
        lblSeparador.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSeparador.setText("-");

        javax.swing.GroupLayout panelFiltros2Layout = new javax.swing.GroupLayout(panelFiltros2);
        panelFiltros2.setLayout(panelFiltros2Layout);
        panelFiltros2Layout.setHorizontalGroup(
            panelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltros2Layout.createSequentialGroup()
                .addComponent(chk_TipoFactura)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmb_TipoFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(panelFiltros2Layout.createSequentialGroup()
                .addComponent(chk_NumFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_SerieFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSeparador, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_NroFactura))
        );

        panelFiltros2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {chk_NumFactura, chk_TipoFactura});

        panelFiltros2Layout.setVerticalGroup(
            panelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltros2Layout.createSequentialGroup()
                .addGroup(panelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmb_TipoFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_TipoFactura))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_SerieFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSeparador)
                    .addComponent(txt_NroFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_NumFactura))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelFiltros2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txt_NroFactura, txt_SerieFactura});

        javax.swing.GroupLayout panelFiltrosLayout = new javax.swing.GroupLayout(panelFiltros);
        panelFiltros.setLayout(panelFiltrosLayout);
        panelFiltrosLayout.setHorizontalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addComponent(panelFiltros1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelFiltros2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 3, Short.MAX_VALUE))
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addComponent(btn_Buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_CantRegistrosEncontrados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(6, 6, 6))
        );
        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelFiltros1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelFiltros2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btn_Buscar)
                    .addComponent(lbl_CantRegistrosEncontrados, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        btnNuevaFactura.setForeground(java.awt.Color.blue);
        btnNuevaFactura.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Add_16x16.png"))); // NOI18N
        btnNuevaFactura.setText("Nueva Factura");
        btnNuevaFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaFacturaActionPerformed(evt);
            }
        });

        btn_VerDetalle.setForeground(java.awt.Color.blue);
        btn_VerDetalle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/target_16x16.png"))); // NOI18N
        btn_VerDetalle.setText("Ver Detalle");
        btn_VerDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_VerDetalleActionPerformed(evt);
            }
        });

        lbl_TotalIVACompra.setText("Total IVA Compra:");

        txt_ResultTotalIVACompra.setEditable(false);
        txt_ResultTotalIVACompra.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_ResultTotalIVACompra.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txt_ResultGastoTotal.setEditable(false);
        txt_ResultGastoTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_ResultGastoTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        lbl_TotalFacturado.setText("Total Facturado:");

        btnCrearNotaCredito.setForeground(java.awt.Color.blue);
        btnCrearNotaCredito.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Add_16x16.png"))); // NOI18N
        btnCrearNotaCredito.setText("Nueva Nota Credito");
        btnCrearNotaCredito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearNotaCreditoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(btnNuevaFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btn_VerDetalle)
                .addGap(0, 0, 0)
                .addComponent(btnCrearNotaCredito)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_TotalFacturado, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_TotalIVACompra, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_ResultGastoTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_ResultTotalIVACompra, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addComponent(sp_Resultados)
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCrearNotaCredito, btnNuevaFactura, btn_VerDetalle});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createSequentialGroup()
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_ResultGastoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_TotalFacturado))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_ResultTotalIVACompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_TotalIVACompra)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_VerDetalle)
                            .addComponent(btnCrearNotaCredito))
                        .addComponent(btnNuevaFactura))))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCrearNotaCredito, btnNuevaFactura, btn_VerDetalle});

        panelOrden.setBorder(javax.swing.BorderFactory.createTitledBorder("Ordenar por"));

        cmbOrden.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Fecha Factura", "Total" }));
        cmbOrden.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbOrdenItemStateChanged(evt);
            }
        });

        cmbSentido.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Descendente", "Ascendente" }));
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
                .addComponent(panelOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        this.setSize(sizeInternalFrame);
        this.setColumnas();
        dc_FechaDesde.setDate(new Date());
        dc_FechaHasta.setDate(new Date());
        try {
            this.setMaximum(true);            
        } catch (PropertyVetoException ex) {
            LOGGER.error(ex.getMessage());
        }        
    }//GEN-LAST:event_formInternalFrameOpened
        
    private void chk_NumFacturaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_NumFacturaItemStateChanged
        if (chk_NumFactura.isSelected() == true) {
            txt_NroFactura.setEnabled(true);
            txt_SerieFactura.setEnabled(true);
            txt_SerieFactura.requestFocus();
        } else {
            txt_NroFactura.setEnabled(false);
            txt_SerieFactura.setEnabled(false);
        }
    }//GEN-LAST:event_chk_NumFacturaItemStateChanged

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.limpiarYBuscar(true);
    }//GEN-LAST:event_btn_BuscarActionPerformed

    private void chk_ProveedorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ProveedorItemStateChanged
        if (chk_Proveedor.isSelected() == true) {
            btnBuscarProveedor.setEnabled(true);
            btnBuscarProveedor.requestFocus();
            txtProveedor.setEnabled(true);
        } else {
            btnBuscarProveedor.setEnabled(false);
            txtProveedor.setEnabled(false);
        }
    }//GEN-LAST:event_chk_ProveedorItemStateChanged

    private void chk_FechaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FechaItemStateChanged
        if (chk_Fecha.isSelected() == true) {
            dc_FechaDesde.setEnabled(true);
            dc_FechaHasta.setEnabled(true);
            lbl_Desde.setEnabled(true);
            lbl_Hasta.setEnabled(true);
            dc_FechaDesde.requestFocus();
        } else {
            dc_FechaDesde.setEnabled(false);
            dc_FechaHasta.setEnabled(false);
            lbl_Desde.setEnabled(false);
            lbl_Hasta.setEnabled(false);
        }
    }//GEN-LAST:event_chk_FechaItemStateChanged

    private void txt_SerieFacturaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_SerieFacturaKeyTyped
        Utilidades.controlarEntradaSoloNumerico(evt);
    }//GEN-LAST:event_txt_SerieFacturaKeyTyped

    private void txt_NroFacturaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_NroFacturaKeyTyped
        Utilidades.controlarEntradaSoloNumerico(evt);
    }//GEN-LAST:event_txt_NroFacturaKeyTyped

    private void btn_VerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_VerDetalleActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            JInternalFrame gui = new DetalleFacturaCompraGUI(facturasTotal.get(indexFilaSeleccionada));
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        }
    }//GEN-LAST:event_btn_VerDetalleActionPerformed

    private void btnNuevaFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaFacturaActionPerformed
        if (this.existeProveedorDisponible()) {
            JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), DetalleFacturaCompraGUI.class);
            if (gui == null) {
                gui = new DetalleFacturaCompraGUI();
                gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                        getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
                getDesktopPane().add(gui);
                gui.setVisible(true);
            } else {
                try {
                    gui.setSelected(true);
                } catch (PropertyVetoException ex) {
                    String msjError = "No se pudo seleccionar la ventana requerida.";
                    LOGGER.error(msjError + " - " + ex.getMessage());
                    JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            String mensaje = ResourceBundle.getBundle("Mensajes").getString("mensaje_sin_proveedor");
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnNuevaFacturaActionPerformed

    private void chk_TipoFacturaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_TipoFacturaItemStateChanged
        if (chk_TipoFactura.isSelected() == true) {
            cmb_TipoFactura.setEnabled(true);
            this.cargarTiposDeFactura();
            cmb_TipoFactura.requestFocus();
        } else {
            cmb_TipoFactura.setEnabled(false);
            cmb_TipoFactura.removeAllItems();
        }
    }//GEN-LAST:event_chk_TipoFacturaItemStateChanged

    private void txt_SerieFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_SerieFacturaActionPerformed
        btn_BuscarActionPerformed(null);
    }//GEN-LAST:event_txt_SerieFacturaActionPerformed

    private void txt_NroFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_NroFacturaActionPerformed
        btn_BuscarActionPerformed(null);
    }//GEN-LAST:event_txt_NroFacturaActionPerformed

    private void cmbOrdenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbOrdenItemStateChanged
        this.limpiarYBuscar(true);
    }//GEN-LAST:event_cmbOrdenItemStateChanged

    private void cmbSentidoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSentidoItemStateChanged
        this.limpiarYBuscar(true);
    }//GEN-LAST:event_cmbSentidoItemStateChanged

    private void btnBuscarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarProveedorActionPerformed
        BuscarProveedoresGUI buscarProveedoresGUI = new BuscarProveedoresGUI();
        buscarProveedoresGUI.setModal(true);
        buscarProveedoresGUI.setLocationRelativeTo(this);
        buscarProveedoresGUI.setVisible(true);
        if (buscarProveedoresGUI.getProveedorSeleccionado() != null) {
            proveedorSeleccionado = buscarProveedoresGUI.getProveedorSeleccionado();
            txtProveedor.setText(proveedorSeleccionado.getRazonSocial());
        }
    }//GEN-LAST:event_btnBuscarProveedorActionPerformed

    private void btnCrearNotaCreditoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearNotaCreditoActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            FacturaCompra factura = facturasTotal.get(indexFilaSeleccionada);
            if (factura.getTipoComprobante() == TipoDeComprobante.FACTURA_A
                    || factura.getTipoComprobante() == TipoDeComprobante.FACTURA_B
                    || factura.getTipoComprobante() == TipoDeComprobante.FACTURA_C
                    || factura.getTipoComprobante() == TipoDeComprobante.FACTURA_X
                    || factura.getTipoComprobante() == TipoDeComprobante.FACTURA_Y
                    || factura.getTipoComprobante() == TipoDeComprobante.PRESUPUESTO) {
                SeleccionDeProductosGUI seleccionDeProductosGUI = new SeleccionDeProductosGUI(
                        factura.getId_Factura());
                seleccionDeProductosGUI.setModal(true);
                seleccionDeProductosGUI.setLocationRelativeTo(this);
                seleccionDeProductosGUI.setVisible(true);
                try {
                    if (seleccionDeProductosGUI.getNotaCreditoCalculada() != null) {
                        DetalleNotaCreditoGUI detalleNotaCredito = new DetalleNotaCreditoGUI(seleccionDeProductosGUI.getNotaCreditoCalculada());
                        detalleNotaCredito.setModal(true);
                        detalleNotaCredito.setLocationRelativeTo(this);
                        detalleNotaCredito.setVisible(true);
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
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_tipoDeMovimiento_incorrecto"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnCrearNotaCreditoActionPerformed

    private void chk_ProductoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ProductoItemStateChanged
        if (chk_Producto.isSelected() == true) {
            btnBuscarProductos.setEnabled(true);
            btnBuscarProductos.requestFocus();
            txtProducto.setEnabled(true);
        } else {
            btnBuscarProductos.setEnabled(false);
            txtProducto.setEnabled(false);
        }
    }//GEN-LAST:event_chk_ProductoItemStateChanged

    private void btnBuscarProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarProductosActionPerformed
        BuscarProductosGUI buscarProductosGUI = new BuscarProductosGUI();
        buscarProductosGUI.setModal(true);
        buscarProductosGUI.setLocationRelativeTo(this);
        buscarProductosGUI.setVisible(true);
        productoSeleccionado = buscarProductosGUI.getProductoSeleccionado();
        if (productoSeleccionado != null) {
            txtProducto.setText(productoSeleccionado.getDescripcion());
        }
    }//GEN-LAST:event_btnBuscarProductosActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarProductos;
    private javax.swing.JButton btnBuscarProveedor;
    private javax.swing.JButton btnCrearNotaCredito;
    private javax.swing.JButton btnNuevaFactura;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JButton btn_VerDetalle;
    private javax.swing.JCheckBox chk_Fecha;
    private javax.swing.JCheckBox chk_NumFactura;
    private javax.swing.JCheckBox chk_Producto;
    private javax.swing.JCheckBox chk_Proveedor;
    private javax.swing.JCheckBox chk_TipoFactura;
    private javax.swing.JComboBox<String> cmbOrden;
    private javax.swing.JComboBox<String> cmbSentido;
    private javax.swing.JComboBox cmb_TipoFactura;
    private com.toedter.calendar.JDateChooser dc_FechaDesde;
    private com.toedter.calendar.JDateChooser dc_FechaHasta;
    private javax.swing.JLabel lblSeparador;
    private javax.swing.JLabel lbl_CantRegistrosEncontrados;
    private javax.swing.JLabel lbl_Desde;
    private javax.swing.JLabel lbl_Hasta;
    private javax.swing.JLabel lbl_TotalFacturado;
    private javax.swing.JLabel lbl_TotalIVACompra;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelFiltros1;
    private javax.swing.JPanel panelFiltros2;
    private javax.swing.JPanel panelOrden;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JTextField txtProducto;
    private javax.swing.JTextField txtProveedor;
    private javax.swing.JFormattedTextField txt_NroFactura;
    private javax.swing.JFormattedTextField txt_ResultGastoTotal;
    private javax.swing.JFormattedTextField txt_ResultTotalIVACompra;
    private javax.swing.JFormattedTextField txt_SerieFactura;
    // End of variables declaration//GEN-END:variables
}
