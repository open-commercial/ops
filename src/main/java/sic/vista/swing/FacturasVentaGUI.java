package sic.vista.swing;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
import sic.modelo.criteria.BusquedaFacturaVentaCriteria;
import sic.modelo.Cliente;
import sic.modelo.SucursalActiva;
import sic.modelo.FacturaVenta;
import sic.modelo.Usuario;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.Producto;
import sic.modelo.Rol;
import sic.modelo.TipoDeComprobante;
import sic.modelo.UsuarioActivo;
import sic.util.DecimalesRenderer;
import sic.util.FechasRenderer;
import sic.util.FormatosFechaHora;
import sic.util.Utilidades;

public class FacturasVentaGUI extends JInternalFrame {

    private ModeloTabla modeloTablaFacturas = new ModeloTabla();
    private List<FacturaVenta> facturasTotal = new ArrayList<>();
    private List<FacturaVenta> facturasParcial = new ArrayList<>();
    private Cliente clienteSeleccionado;
    private Usuario usuarioSeleccionado;
    private Producto productoSeleccionado;
    private Usuario viajanteSeleccionado;
    private boolean tienePermisoSegunRoles;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame = new Dimension(970, 600);
    private static int totalElementosBusqueda;
    private static int NUMERO_PAGINA = 0;

    public FacturasVentaGUI() {
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

    public void buscarPorNroPedido(long nroPedido) {
        chk_NumeroPedido.setSelected(true);
        txt_NumeroPedido.setEnabled(true);
        txt_NumeroPedido.setText(String.valueOf(nroPedido));
        this.limpiarYBuscar(true);
    }

    public void buscarPorSerieNroTipo(long nroSerie, long nroFactura,
            TipoDeComprobante tipoDeComprobante, long idCliente) {
        chk_NumFactura.setSelected(true);
        txt_SerieFactura.setEnabled(true);
        txt_NroFactura.setEnabled(true);
        txt_SerieFactura.setText(String.valueOf(nroSerie));
        txt_NroFactura.setText(String.valueOf(nroFactura));
        chk_TipoFactura.setSelected(true);
        cmb_TipoFactura.setSelectedItem(tipoDeComprobante);
        clienteSeleccionado = RestClient.getRestTemplate()
                .getForObject("/clientes/" + idCliente,
                        Cliente.class);
        txtCliente.setText(clienteSeleccionado.getNombreFiscal());
        chk_Cliente.setSelected(true);
        this.resetScroll();
        this.limpiarJTable();
        this.buscar(true);
    }

    private BusquedaFacturaVentaCriteria getCriteria() {
        BusquedaFacturaVentaCriteria criteria = new BusquedaFacturaVentaCriteria();
        criteria.setIdSucursal(SucursalActiva.getInstance().getSucursal().getIdSucursal());
        if (chk_Cliente.isSelected() && clienteSeleccionado != null) {
            criteria.setIdCliente(clienteSeleccionado.getIdCliente());
        }
        if (chk_Fecha.isSelected()) {
            criteria.setFechaDesde((dc_FechaDesde.getDate() != null)
                    ? LocalDateTime.ofInstant(dc_FechaDesde.getDate().toInstant(), ZoneId.systemDefault())
                    : null);
            criteria.setFechaHasta((dc_FechaHasta.getDate() != null)
                    ? LocalDateTime.ofInstant(dc_FechaHasta.getDate().toInstant(), ZoneId.systemDefault())
                    : null);
        }
        if (chk_NumFactura.isSelected()) {
            criteria.setNumSerie(Long.valueOf(txt_SerieFactura.getText()));
            criteria.setNumFactura(Long.valueOf(txt_NroFactura.getText()));
        }
        if (chk_TipoFactura.isSelected()) {
            criteria.setTipoComprobante(((TipoDeComprobante) cmb_TipoFactura.getSelectedItem()));
        }
        if (chk_Usuario.isSelected() && usuarioSeleccionado != null) {
            criteria.setIdUsuario(usuarioSeleccionado.getIdUsuario());
        }
        if (chk_Producto.isSelected() && productoSeleccionado != null) {
            criteria.setIdProducto(productoSeleccionado.getIdProducto());
        }
        if (chk_Viajante.isSelected() && viajanteSeleccionado != null) {
            criteria.setIdViajante(viajanteSeleccionado.getIdUsuario());
        }
        if (chk_NumeroPedido.isSelected()) {
            criteria.setNroPedido(Long.parseLong(txt_NumeroPedido.getText()));
        }
        return criteria;
    }

    private void setColumnas() {
        //nombres de columnas
        String[] encabezados = new String[20];
        encabezados[0] = "CAE";
        encabezados[1] = "Fecha Factura";
        encabezados[2] = "Tipo";
        encabezados[3] = "Nº Factura";
        encabezados[4] = "Nº Pedido";
        encabezados[5] = "Cliente";
        encabezados[6] = "Usuario";
        encabezados[7] = "Viajante";
        encabezados[8] = "Transportista";
        encabezados[9] = "Total";
        encabezados[10] = "SubTotal";
        encabezados[11] = "% Descuento";
        encabezados[12] = "Descuento neto";
        encabezados[13] = "% Recargo";
        encabezados[14] = "Recargo neto";
        encabezados[15] = "SubTotal bruto";
        encabezados[16] = "IVA 10.5% neto";
        encabezados[17] = "IVA 21% neto";
        encabezados[18] = "Nº Factura Afip";
        encabezados[19] = "Vencimiento CAE";
        modeloTablaFacturas.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaFacturas);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaFacturas.getColumnCount()];
        tipos[0] = Object.class;
        tipos[1] = LocalDateTime.class;
        tipos[2] = TipoDeComprobante.class;
        tipos[3] = String.class;
        tipos[4] = Long.class;
        tipos[5] = String.class;
        tipos[6] = String.class;
        tipos[7] = String.class;
        tipos[8] = String.class;
        tipos[9] = BigDecimal.class;
        tipos[10] = BigDecimal.class;
        tipos[11] = BigDecimal.class;
        tipos[12] = BigDecimal.class;
        tipos[13] = BigDecimal.class;
        tipos[14] = BigDecimal.class;
        tipos[15] = BigDecimal.class;
        tipos[16] = BigDecimal.class;
        tipos[17] = BigDecimal.class;
        tipos[18] = String.class;
        tipos[19] = LocalDateTime.class;
        modeloTablaFacturas.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);
        //tamanios de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(140);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(90);
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(4).setMaxWidth(100);
        tbl_Resultados.getColumnModel().getColumn(5).setPreferredWidth(220);
        tbl_Resultados.getColumnModel().getColumn(5).setMaxWidth(220);
        tbl_Resultados.getColumnModel().getColumn(5).setMinWidth(220);
        tbl_Resultados.getColumnModel().getColumn(6).setPreferredWidth(220);
        tbl_Resultados.getColumnModel().getColumn(6).setMaxWidth(220);
        tbl_Resultados.getColumnModel().getColumn(6).setMinWidth(220);
        tbl_Resultados.getColumnModel().getColumn(7).setPreferredWidth(220);
        tbl_Resultados.getColumnModel().getColumn(7).setMaxWidth(220);
        tbl_Resultados.getColumnModel().getColumn(7).setMinWidth(220);
        tbl_Resultados.getColumnModel().getColumn(8).setPreferredWidth(190);
        tbl_Resultados.getColumnModel().getColumn(9).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(10).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(11).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(12).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(13).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(14).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(15).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(16).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(17).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(18).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(19).setPreferredWidth(120);
        //render para los tipos de datos
        tbl_Resultados.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());
        tbl_Resultados.getColumnModel().getColumn(1).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHAHORA_HISPANO));
        tbl_Resultados.getColumnModel().getColumn(19).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHA_HISPANO));
    }

    private void calcularResultados(BusquedaFacturaVentaCriteria criteria) {
        txt_ResultTotalFacturado.setValue(RestClient.getRestTemplate()
                .postForObject("/facturas/ventas/total-facturado/criteria", criteria, BigDecimal.class));
        if (UsuarioActivo.getInstance().getUsuario().getRoles().contains(Rol.ADMINISTRADOR)
                || UsuarioActivo.getInstance().getUsuario().getRoles().contains(Rol.ENCARGADO)) {
            txt_ResultGananciaTotal.setValue(RestClient.getRestTemplate()
                    .postForObject("/facturas/ventas/ganancia-total/criteria", criteria, BigDecimal.class));
            txt_ResultTotalIVAVenta.setValue(RestClient.getRestTemplate()
                    .postForObject("/facturas/ventas/total-iva/criteria", criteria, BigDecimal.class));
        }
    }

    private void buscar(boolean calcularResultados) {
        this.cambiarEstadoEnabledComponentes(false);
        BusquedaFacturaVentaCriteria criteria = this.getCriteria();
        int seleccionOrden = cmbOrden.getSelectedIndex();
        switch (seleccionOrden) {
            case 0:
                criteria.setOrdenarPor("fecha");
                break;
            case 1:
                criteria.setOrdenarPor("cliente.nombreFiscal");
                break;
            case 2:
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
        try {
            HttpEntity<BusquedaFacturaVentaCriteria> requestEntity = new HttpEntity<>(criteria);
            PaginaRespuestaRest<FacturaVenta> response = RestClient.getRestTemplate()
                    .exchange("/facturas/ventas/busqueda/criteria", HttpMethod.POST, requestEntity,
                            new ParameterizedTypeReference<PaginaRespuestaRest<FacturaVenta>>() {
                    })
                    .getBody();
            totalElementosBusqueda = response.getTotalElements();
            facturasParcial = response.getContent();
            facturasTotal.addAll(facturasParcial);
            if (calcularResultados && tienePermisoSegunRoles) {
                this.calcularResultados(getCriteria());
            }
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

    private void cambiarEstadoEnabledComponentes(boolean status) {
        chk_Fecha.setEnabled(status);
        if (status == true && chk_Fecha.isSelected() == true) {
            dc_FechaDesde.setEnabled(true);
            dc_FechaHasta.setEnabled(true);
        } else {
            dc_FechaDesde.setEnabled(false);
            dc_FechaHasta.setEnabled(false);
        }
        chk_Cliente.setEnabled(status);
        if (status == true && chk_Cliente.isSelected() == true) {
            btnBuscarCliente.setEnabled(true);
        } else {
            btnBuscarCliente.setEnabled(false);
        }
        chk_Usuario.setEnabled(status);
        if (status == true && chk_Usuario.isSelected() == true) {
            btnBuscarUsuarios.setEnabled(true);
        } else {
            btnBuscarUsuarios.setEnabled(false);
        }
        chk_Usuario.setEnabled(status);
        if (status == true && chk_Usuario.isSelected() == true) {
            btnBuscarUsuarios.setEnabled(true);
        } else {
            btnBuscarUsuarios.setEnabled(false);
        }
        chk_NumFactura.setEnabled(status);
        if (status == true && chk_NumFactura.isSelected() == true) {
            txt_SerieFactura.setEnabled(true);
            txt_NroFactura.setEnabled(true);
        } else {
            txt_SerieFactura.setEnabled(false);
            txt_NroFactura.setEnabled(false);
        }
        chk_TipoFactura.setEnabled(status);
        if (status == true && chk_TipoFactura.isSelected() == true) {
            cmb_TipoFactura.setEnabled(true);
        } else {
            cmb_TipoFactura.setEnabled(false);
        }
        chk_NumeroPedido.setEnabled(status);
        if (status == true && chk_NumeroPedido.isSelected() == true) {
            txt_NumeroPedido.setEnabled(true);
        } else {
            txt_NumeroPedido.setEnabled(false);
        }
        btn_Buscar.setEnabled(status);
        btn_VerDetalle.setEnabled(status);
        btn_Autorizar.setEnabled(status);
        tbl_Resultados.setEnabled(status);
        sp_Resultados.setEnabled(status);
        tbl_Resultados.requestFocus();
    }

    private void cargarResultadosAlTable() {
        facturasParcial.stream().map(factura -> {
            Object[] fila = new Object[20];
            fila[0] = factura.getCae() == 0 ? "" : factura.getCae();
            fila[1] = factura.getFecha();
            fila[2] = factura.getTipoComprobante();
            fila[3] = factura.getNumSerie() + " - " + factura.getNumFactura();
            fila[4] = factura.getNroPedido();
            fila[5] = factura.getNombreFiscalCliente();
            fila[6] = factura.getNombreUsuario();
            fila[7] = factura.getNombreViajanteCliente();
            fila[8] = factura.getNombreTransportista();
            fila[9] = factura.getTotal();
            fila[10] = factura.getSubTotal();
            fila[11] = factura.getDescuentoPorcentaje();
            fila[12] = factura.getDescuentoNeto();
            fila[13] = factura.getRecargoPorcentaje();
            fila[14] = factura.getRecargoNeto();
            fila[15] = factura.getSubTotalBruto();
            fila[16] = factura.getIva105Neto();
            fila[17] = factura.getIva21Neto();
            if (factura.getNumSerieAfip() == 0 && factura.getNumFacturaAfip() == 0) {
                fila[18] = "";
            } else {
                fila[18] = factura.getNumSerieAfip() + " - " + factura.getNumFacturaAfip();
            }
            fila[19] = factura.getVencimientoCae();
            return fila;
        }).forEach(fila -> {
            modeloTablaFacturas.addRow(fila);
        });
        tbl_Resultados.setModel(modeloTablaFacturas);
        lbl_cantResultados.setText(totalElementosBusqueda + " facturas encontradas");
    }

    private void resetScroll() {
        NUMERO_PAGINA = 0;
        facturasTotal.clear();
        facturasParcial.clear();
        Point p = new Point(0, 0);
        sp_Resultados.getViewport().setViewPosition(p);
    }

    private void limpiarJTable() {
        modeloTablaFacturas = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaFacturas);
        this.setColumnas();
    }

    private void limpiarYBuscar(boolean calcularResultados) {
        this.resetScroll();
        this.limpiarJTable();
        this.buscar(calcularResultados);
    }

    private void cargarTiposDeFactura() {
        try {
            TipoDeComprobante[] tiposDeComprobantes = RestClient.getRestTemplate()
                    .getForObject("/facturas/tipos/sucursales/" + SucursalActiva.getInstance().getSucursal().getIdSucursal(),
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

    private void lanzarReporteFactura() {
        if (Desktop.isDesktopSupported()) {
            try {
                int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
                byte[] reporte = RestClient.getRestTemplate()
                        .getForObject("/facturas/ventas/" + facturasTotal.get(indexFilaSeleccionada).getIdFactura() + "/reporte",
                                byte[].class);
                File f = new File(System.getProperty("user.home") + "/Factura.pdf");
                Files.write(f.toPath(), reporte);
                Desktop.getDesktop().open(f);
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_IOException"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_plataforma_no_soportada"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarEstadoDeComponentesSegunRolUsuario() {
        List<Rol> rolesDeUsuarioActivo = UsuarioActivo.getInstance().getUsuario().getRoles();
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)
                || rolesDeUsuarioActivo.contains(Rol.ENCARGADO)) {
            txt_ResultGananciaTotal.setVisible(true);
            lbl_GananciaTotal.setVisible(true);
            lbl_TotalIVAVenta.setVisible(true);
            txt_ResultTotalIVAVenta.setVisible(true);
        } else {
            txt_ResultGananciaTotal.setVisible(false);
            lbl_GananciaTotal.setVisible(false);
            lbl_TotalIVAVenta.setVisible(false);
            txt_ResultTotalIVAVenta.setVisible(false);
        }
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)
                || rolesDeUsuarioActivo.contains(Rol.ENCARGADO)
                || rolesDeUsuarioActivo.contains(Rol.VENDEDOR)) {
            tienePermisoSegunRoles = true;
            btn_Autorizar.setEnabled(true);
            chk_Usuario.setEnabled(true);
        } else {
            tienePermisoSegunRoles = false;
            btn_Autorizar.setEnabled(false);
            chk_Usuario.setEnabled(false);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btn_VerDetalle = new javax.swing.JButton();
        panelNumeros = new javax.swing.JPanel();
        lbl_TotalFacturado = new javax.swing.JLabel();
        lbl_GananciaTotal = new javax.swing.JLabel();
        lbl_TotalIVAVenta = new javax.swing.JLabel();
        txt_ResultTotalFacturado = new javax.swing.JFormattedTextField();
        txt_ResultGananciaTotal = new javax.swing.JFormattedTextField();
        txt_ResultTotalIVAVenta = new javax.swing.JFormattedTextField();
        btn_Autorizar = new javax.swing.JButton();
        btnCrearNotaCredito = new javax.swing.JButton();
        btnEnviarEmail = new javax.swing.JButton();
        panelFiltros = new javax.swing.JPanel();
        subPanelFiltros1 = new javax.swing.JPanel();
        chk_Cliente = new javax.swing.JCheckBox();
        chk_Usuario = new javax.swing.JCheckBox();
        txtCliente = new javax.swing.JTextField();
        btnBuscarCliente = new javax.swing.JButton();
        txtUsuario = new javax.swing.JTextField();
        btnBuscarUsuarios = new javax.swing.JButton();
        txtProducto = new javax.swing.JTextField();
        btnBuscarProductos = new javax.swing.JButton();
        chk_Producto = new javax.swing.JCheckBox();
        txtViajante = new javax.swing.JTextField();
        chk_Viajante = new javax.swing.JCheckBox();
        btnBuscarViajantes = new javax.swing.JButton();
        subPanelFiltros2 = new javax.swing.JPanel();
        chk_TipoFactura = new javax.swing.JCheckBox();
        cmb_TipoFactura = new javax.swing.JComboBox();
        chk_NumeroPedido = new javax.swing.JCheckBox();
        txt_NumeroPedido = new javax.swing.JFormattedTextField();
        chk_NumFactura = new javax.swing.JCheckBox();
        txt_SerieFactura = new javax.swing.JFormattedTextField();
        separador = new javax.swing.JLabel();
        txt_NroFactura = new javax.swing.JFormattedTextField();
        dc_FechaDesde = new com.toedter.calendar.JDateChooser();
        dc_FechaHasta = new com.toedter.calendar.JDateChooser();
        chk_Fecha = new javax.swing.JCheckBox();
        btn_Buscar = new javax.swing.JButton();
        lbl_cantResultados = new javax.swing.JLabel();
        panelOrden = new javax.swing.JPanel();
        cmbOrden = new javax.swing.JComboBox<>();
        cmbSentido = new javax.swing.JComboBox<>();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Facturas de Venta");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/SIC_16_square.png"))); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

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

        btn_VerDetalle.setForeground(java.awt.Color.blue);
        btn_VerDetalle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/target_16x16.png"))); // NOI18N
        btn_VerDetalle.setText("Ver Detalle");
        btn_VerDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_VerDetalleActionPerformed(evt);
            }
        });

        lbl_TotalFacturado.setText("Total Facturado:");

        lbl_GananciaTotal.setText("Ganancia Total:");

        lbl_TotalIVAVenta.setText("Total IVA Venta:");

        txt_ResultTotalFacturado.setEditable(false);
        txt_ResultTotalFacturado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_ResultTotalFacturado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txt_ResultGananciaTotal.setEditable(false);
        txt_ResultGananciaTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_ResultGananciaTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txt_ResultTotalIVAVenta.setEditable(false);
        txt_ResultTotalIVAVenta.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_ResultTotalIVAVenta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout panelNumerosLayout = new javax.swing.GroupLayout(panelNumeros);
        panelNumeros.setLayout(panelNumerosLayout);
        panelNumerosLayout.setHorizontalGroup(
            panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNumerosLayout.createSequentialGroup()
                .addGroup(panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lbl_TotalFacturado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_TotalIVAVenta, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_GananciaTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_ResultTotalIVAVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_ResultGananciaTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_ResultTotalFacturado, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        panelNumerosLayout.setVerticalGroup(
            panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNumerosLayout.createSequentialGroup()
                .addGroup(panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_GananciaTotal)
                    .addComponent(txt_ResultGananciaTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_TotalIVAVenta)
                    .addComponent(txt_ResultTotalIVAVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_TotalFacturado)
                    .addComponent(txt_ResultTotalFacturado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        btn_Autorizar.setForeground(java.awt.Color.blue);
        btn_Autorizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Certificate_16x16.png"))); // NOI18N
        btn_Autorizar.setText("Autorizar");
        btn_Autorizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AutorizarActionPerformed(evt);
            }
        });

        btnCrearNotaCredito.setForeground(java.awt.Color.blue);
        btnCrearNotaCredito.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Add_16x16.png"))); // NOI18N
        btnCrearNotaCredito.setText("Nueva Nota Credito");
        btnCrearNotaCredito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearNotaCreditoActionPerformed(evt);
            }
        });

        btnEnviarEmail.setForeground(java.awt.Color.blue);
        btnEnviarEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/email_16x16.png"))); // NOI18N
        btnEnviarEmail.setText("Enviar por email");
        btnEnviarEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarEmailActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(btnCrearNotaCredito)
                .addGap(0, 0, 0)
                .addComponent(btn_Autorizar)
                .addGap(0, 0, 0)
                .addComponent(btn_VerDetalle)
                .addGap(0, 0, 0)
                .addComponent(btnEnviarEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelNumeros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(sp_Resultados)
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCrearNotaCredito, btnEnviarEmail, btn_Autorizar, btn_VerDetalle});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createSequentialGroup()
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelNumeros, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(btnCrearNotaCredito)
                        .addComponent(btn_Autorizar)
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(btnEnviarEmail)
                            .addComponent(btn_VerDetalle)))))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCrearNotaCredito, btn_Autorizar, btn_VerDetalle});

        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        chk_Cliente.setText("Cliente:");
        chk_Cliente.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ClienteItemStateChanged(evt);
            }
        });

        chk_Usuario.setText("Usuario:");
        chk_Usuario.setToolTipText("");
        chk_Usuario.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_UsuarioItemStateChanged(evt);
            }
        });

        txtCliente.setEditable(false);
        txtCliente.setEnabled(false);
        txtCliente.setOpaque(false);

        btnBuscarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btnBuscarCliente.setEnabled(false);
        btnBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarClienteActionPerformed(evt);
            }
        });

        txtUsuario.setEditable(false);
        txtUsuario.setEnabled(false);
        txtUsuario.setOpaque(false);

        btnBuscarUsuarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btnBuscarUsuarios.setEnabled(false);
        btnBuscarUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarUsuariosActionPerformed(evt);
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

        txtViajante.setEditable(false);
        txtViajante.setEnabled(false);
        txtViajante.setOpaque(false);

        chk_Viajante.setText("Viajante:");
        chk_Viajante.setToolTipText("");
        chk_Viajante.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ViajanteItemStateChanged(evt);
            }
        });

        btnBuscarViajantes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btnBuscarViajantes.setEnabled(false);
        btnBuscarViajantes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarViajantesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout subPanelFiltros1Layout = new javax.swing.GroupLayout(subPanelFiltros1);
        subPanelFiltros1.setLayout(subPanelFiltros1Layout);
        subPanelFiltros1Layout.setHorizontalGroup(
            subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(chk_Usuario, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Viajante, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Producto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Cliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                        .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                            .addComponent(txtUsuario)
                            .addComponent(txtViajante))
                        .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnBuscarUsuarios)
                            .addComponent(btnBuscarProductos)
                            .addComponent(btnBuscarViajantes)))
                    .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                        .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btnBuscarCliente))))
        );
        subPanelFiltros1Layout.setVerticalGroup(
            subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnBuscarCliente)
                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_Cliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnBuscarUsuarios)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_Usuario))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnBuscarProductos)
                    .addComponent(chk_Producto)
                    .addComponent(txtProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnBuscarViajantes)
                    .addComponent(txtViajante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_Viajante)))
        );

        subPanelFiltros1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscarUsuarios, txtProducto, txtUsuario, txtViajante});

        subPanelFiltros1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscarCliente, txtCliente});

        chk_TipoFactura.setText("Tipo de Factura:");
        chk_TipoFactura.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_TipoFacturaItemStateChanged(evt);
            }
        });

        cmb_TipoFactura.setEnabled(false);

        chk_NumeroPedido.setText("Nº Pedido:");
        chk_NumeroPedido.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_NumeroPedidoItemStateChanged(evt);
            }
        });

        txt_NumeroPedido.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_NumeroPedido.setText("0");
        txt_NumeroPedido.setEnabled(false);
        txt_NumeroPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_NumeroPedidoActionPerformed(evt);
            }
        });
        txt_NumeroPedido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_NumeroPedidoKeyTyped(evt);
            }
        });

        chk_NumFactura.setText("Nº Factura:");
        chk_NumFactura.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_NumFacturaItemStateChanged(evt);
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

        separador.setFont(new java.awt.Font("DejaVu Sans", 0, 15)); // NOI18N
        separador.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        separador.setText("-");

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

        dc_FechaDesde.setDateFormatString("dd/MM/yyyy");
        dc_FechaDesde.setEnabled(false);

        dc_FechaHasta.setDateFormatString("dd/MM/yyyy");
        dc_FechaHasta.setEnabled(false);

        chk_Fecha.setText("Fecha Factura:");
        chk_Fecha.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FechaItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout subPanelFiltros2Layout = new javax.swing.GroupLayout(subPanelFiltros2);
        subPanelFiltros2.setLayout(subPanelFiltros2Layout);
        subPanelFiltros2Layout.setHorizontalGroup(
            subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, subPanelFiltros2Layout.createSequentialGroup()
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(chk_NumFactura, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_NumeroPedido, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_TipoFactura, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Fecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmb_TipoFactura, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, subPanelFiltros2Layout.createSequentialGroup()
                        .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txt_NumeroPedido, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(subPanelFiltros2Layout.createSequentialGroup()
                        .addComponent(txt_SerieFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(separador, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_NroFactura))))
        );

        subPanelFiltros2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {dc_FechaDesde, dc_FechaHasta});

        subPanelFiltros2Layout.setVerticalGroup(
            subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros2Layout.createSequentialGroup()
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Fecha)
                    .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_TipoFactura)
                    .addComponent(cmb_TipoFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_NumeroPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_NumeroPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_SerieFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(separador)
                    .addComponent(txt_NroFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_NumFactura)))
        );

        subPanelFiltros2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {chk_Fecha, chk_NumFactura, chk_NumeroPedido, chk_TipoFactura});

        subPanelFiltros2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {dc_FechaDesde, dc_FechaHasta});

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
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(subPanelFiltros1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(subPanelFiltros2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btn_Buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(subPanelFiltros2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(subPanelFiltros1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Buscar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelOrden.setBorder(javax.swing.BorderFactory.createTitledBorder("Ordenar por"));

        cmbOrden.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Fecha Factura", "Cliente", "Total" }));
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cmbOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
    private void chk_FechaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FechaItemStateChanged
        if (chk_Fecha.isSelected() == true) {
            dc_FechaDesde.setEnabled(true);
            dc_FechaHasta.setEnabled(true);            
            dc_FechaDesde.requestFocus();
        } else {
            dc_FechaDesde.setEnabled(false);
            dc_FechaHasta.setEnabled(false);            
        }
}//GEN-LAST:event_chk_FechaItemStateChanged

    private void chk_ClienteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ClienteItemStateChanged
        if (chk_Cliente.isSelected() == true) {
            btnBuscarCliente.setEnabled(true);
            btnBuscarCliente.requestFocus();
            txtCliente.setEnabled(true);
        } else {
            btnBuscarCliente.setEnabled(false);
            txtCliente.setEnabled(false);
        }
}//GEN-LAST:event_chk_ClienteItemStateChanged

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.limpiarYBuscar(true);
}//GEN-LAST:event_btn_BuscarActionPerformed

    private void btn_VerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_VerDetalleActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            this.lanzarReporteFactura();
        }
}//GEN-LAST:event_btn_VerDetalleActionPerformed

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

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            this.setSize(sizeInternalFrame);
            this.setColumnas();
            this.setMaximum(true);
            dc_FechaDesde.setDate(new Date());
            dc_FechaHasta.setDate(new Date());
            this.cambiarEstadoDeComponentesSegunRolUsuario();
        } catch (PropertyVetoException ex) {
            String mensaje = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void chk_UsuarioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_UsuarioItemStateChanged
        if (chk_Usuario.isSelected() == true) {
            btnBuscarUsuarios.setEnabled(true);
            btnBuscarUsuarios.requestFocus();
            txtUsuario.setEnabled(true);
        } else {
            btnBuscarUsuarios.setEnabled(false);
            txtUsuario.setEnabled(false);
        }
    }//GEN-LAST:event_chk_UsuarioItemStateChanged

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

    private void txt_SerieFacturaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_SerieFacturaKeyTyped
        Utilidades.controlarEntradaSoloNumerico(evt);
    }//GEN-LAST:event_txt_SerieFacturaKeyTyped

    private void txt_NroFacturaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_NroFacturaKeyTyped
        Utilidades.controlarEntradaSoloNumerico(evt);
    }//GEN-LAST:event_txt_NroFacturaKeyTyped

    private void txt_NumeroPedidoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_NumeroPedidoKeyTyped
        Utilidades.controlarEntradaSoloNumerico(evt);
    }//GEN-LAST:event_txt_NumeroPedidoKeyTyped
  
    private void chk_NumeroPedidoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_NumeroPedidoItemStateChanged
        txt_NumeroPedido.setEnabled(chk_NumeroPedido.isSelected());
    }//GEN-LAST:event_chk_NumeroPedidoItemStateChanged

    private void btn_AutorizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AutorizarActionPerformed
        try {
            boolean FEHabilitada = RestClient.getRestTemplate().getForObject("/configuraciones-sucursal/"
                    + SucursalActiva.getInstance().getSucursal().getIdSucursal()
                    + "/factura-electronica-habilitada", Boolean.class);
            if (FEHabilitada) {
                if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
                    int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
                    long idFacturaSeleccionada = facturasTotal.get(indexFilaSeleccionada).getIdFactura();
                    FacturaVenta facturaVenta = RestClient.getRestTemplate().postForObject("/facturas/ventas/" + idFacturaSeleccionada + "/autorizacion",
                            null, FacturaVenta.class);
                    if (facturaVenta.getCae() != 0) {
                        JOptionPane.showMessageDialog(this,
                                ResourceBundle.getBundle("Mensajes").getString("mensaje_factura_autorizada"),
                                "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_factura_no_autorizada"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    this.limpiarYBuscar(false);
                }
            } else {
                JOptionPane.showInternalMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_sucursal_fe_habilitada"),
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
    }//GEN-LAST:event_btn_AutorizarActionPerformed

    private void txt_SerieFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_SerieFacturaActionPerformed
        btn_BuscarActionPerformed(null);
    }//GEN-LAST:event_txt_SerieFacturaActionPerformed

    private void txt_NroFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_NroFacturaActionPerformed
        btn_BuscarActionPerformed(null);
    }//GEN-LAST:event_txt_NroFacturaActionPerformed

    private void txt_NumeroPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_NumeroPedidoActionPerformed
        btn_BuscarActionPerformed(null);
    }//GEN-LAST:event_txt_NumeroPedidoActionPerformed

    private void btnBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarClienteActionPerformed
        BuscarClientesGUI buscarClientesGUI = new BuscarClientesGUI();
        buscarClientesGUI.setModal(true);
        buscarClientesGUI.setLocationRelativeTo(this);
        buscarClientesGUI.setVisible(true);
        if (buscarClientesGUI.getClienteSeleccionado() != null) {
            clienteSeleccionado = buscarClientesGUI.getClienteSeleccionado();
            txtCliente.setText(clienteSeleccionado.getNombreFiscal());
        }
    }//GEN-LAST:event_btnBuscarClienteActionPerformed

    private void btnBuscarUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarUsuariosActionPerformed
        Rol[] rolesParaFiltrar = new Rol[]{Rol.ADMINISTRADOR, Rol.ENCARGADO, Rol.VENDEDOR};
        BuscarUsuariosGUI buscarUsuariosGUI = new BuscarUsuariosGUI(rolesParaFiltrar, "Buscar Usuario");
        buscarUsuariosGUI.setModal(true);
        buscarUsuariosGUI.setLocationRelativeTo(this);
        buscarUsuariosGUI.setVisible(true);
        if (buscarUsuariosGUI.getUsuarioSeleccionado() != null) {
            usuarioSeleccionado = buscarUsuariosGUI.getUsuarioSeleccionado();
            txtUsuario.setText(usuarioSeleccionado.toString());
        }
    }//GEN-LAST:event_btnBuscarUsuariosActionPerformed

    private void cmbOrdenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbOrdenItemStateChanged
        this.limpiarYBuscar(true);
    }//GEN-LAST:event_cmbOrdenItemStateChanged

    private void cmbSentidoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSentidoItemStateChanged
        this.limpiarYBuscar(true);
    }//GEN-LAST:event_cmbSentidoItemStateChanged

    private void btnCrearNotaCreditoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearNotaCreditoActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            FacturaVenta factura = facturasTotal.get(indexFilaSeleccionada);
            if (factura.getTipoComprobante() == TipoDeComprobante.FACTURA_A
                    || factura.getTipoComprobante() == TipoDeComprobante.FACTURA_B
                    || factura.getTipoComprobante() == TipoDeComprobante.FACTURA_C
                    || factura.getTipoComprobante() == TipoDeComprobante.FACTURA_X
                    || factura.getTipoComprobante() == TipoDeComprobante.FACTURA_Y
                    || factura.getTipoComprobante() == TipoDeComprobante.PRESUPUESTO) {
                SeleccionDeProductosGUI seleccionDeProductosGUI = new SeleccionDeProductosGUI(
                        factura.getIdFactura());
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

    private void chk_ViajanteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ViajanteItemStateChanged
        if (chk_Viajante.isSelected() == true) {
            btnBuscarViajantes.setEnabled(true);
            btnBuscarViajantes.requestFocus();
            txtViajante.setEnabled(true);
        } else {
            btnBuscarViajantes.setEnabled(false);
            txtViajante.setEnabled(false);
        }
    }//GEN-LAST:event_chk_ViajanteItemStateChanged

    private void btnBuscarViajantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarViajantesActionPerformed
        Rol[] rolesParaFiltrar = new Rol[]{Rol.VIAJANTE};
        BuscarUsuariosGUI buscarUsuariosGUI = new BuscarUsuariosGUI(rolesParaFiltrar, "Buscar Viajante");
        buscarUsuariosGUI.setModal(true);
        buscarUsuariosGUI.setLocationRelativeTo(this);
        buscarUsuariosGUI.setVisible(true);
        if (buscarUsuariosGUI.getUsuarioSeleccionado() != null) {
            viajanteSeleccionado = buscarUsuariosGUI.getUsuarioSeleccionado();
            txtViajante.setText(viajanteSeleccionado.toString());
        }
    }//GEN-LAST:event_btnBuscarViajantesActionPerformed

    private void btnEnviarEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarEmailActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            int respuesta = JOptionPane.showConfirmDialog(this, ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_email"),
                    "Aviso", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    RestClient.getRestTemplate().getForObject("/facturas/ventas/email/" + facturasTotal.get(indexFilaSeleccionada).getIdFactura(), Object.class);
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_factura_email_aviso"),
                            "Aviso", JOptionPane.INFORMATION_MESSAGE);
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
    }//GEN-LAST:event_btnEnviarEmailActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarCliente;
    private javax.swing.JButton btnBuscarProductos;
    private javax.swing.JButton btnBuscarUsuarios;
    private javax.swing.JButton btnBuscarViajantes;
    private javax.swing.JButton btnCrearNotaCredito;
    private javax.swing.JButton btnEnviarEmail;
    private javax.swing.JButton btn_Autorizar;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JButton btn_VerDetalle;
    private javax.swing.JCheckBox chk_Cliente;
    private javax.swing.JCheckBox chk_Fecha;
    private javax.swing.JCheckBox chk_NumFactura;
    private javax.swing.JCheckBox chk_NumeroPedido;
    private javax.swing.JCheckBox chk_Producto;
    private javax.swing.JCheckBox chk_TipoFactura;
    private javax.swing.JCheckBox chk_Usuario;
    private javax.swing.JCheckBox chk_Viajante;
    private javax.swing.JComboBox<String> cmbOrden;
    private javax.swing.JComboBox<String> cmbSentido;
    private javax.swing.JComboBox cmb_TipoFactura;
    private com.toedter.calendar.JDateChooser dc_FechaDesde;
    private com.toedter.calendar.JDateChooser dc_FechaHasta;
    private javax.swing.JLabel lbl_GananciaTotal;
    private javax.swing.JLabel lbl_TotalFacturado;
    private javax.swing.JLabel lbl_TotalIVAVenta;
    private javax.swing.JLabel lbl_cantResultados;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelNumeros;
    private javax.swing.JPanel panelOrden;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JLabel separador;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JPanel subPanelFiltros1;
    private javax.swing.JPanel subPanelFiltros2;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtProducto;
    private javax.swing.JTextField txtUsuario;
    private javax.swing.JTextField txtViajante;
    private javax.swing.JFormattedTextField txt_NroFactura;
    private javax.swing.JFormattedTextField txt_NumeroPedido;
    private javax.swing.JFormattedTextField txt_ResultGananciaTotal;
    private javax.swing.JFormattedTextField txt_ResultTotalFacturado;
    private javax.swing.JFormattedTextField txt_ResultTotalIVAVenta;
    private javax.swing.JFormattedTextField txt_SerieFactura;
    // End of variables declaration//GEN-END:variables
}
