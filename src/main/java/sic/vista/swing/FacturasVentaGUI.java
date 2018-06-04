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
import sic.modelo.FacturaVenta;
import sic.modelo.Usuario;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.Rol;
import sic.modelo.TipoDeComprobante;
import sic.util.DecimalesRenderer;
import sic.util.FechasRenderer;
import sic.util.FormatosFechaHora;
import sic.util.Utilidades;

public class FacturasVentaGUI extends JInternalFrame {

    private ModeloTabla modeloTablaFacturas = new ModeloTabla();
    private List<FacturaVenta> facturasTotal = new ArrayList<>();
    private List<FacturaVenta> facturasParcial = new ArrayList<>();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame = new Dimension(970, 600);
    private static int totalElementosBusqueda;
    private static int NUMERO_PAGINA = 0;
    private static final int TAMANIO_PAGINA = 50;

    public FacturasVentaGUI() {
        this.initComponents();
        sp_Resultados.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int va = scrollBar.getVisibleAmount() + 50;
            if (scrollBar.getValue() >= (scrollBar.getMaximum() - va)) {
                if (facturasTotal.size() >= TAMANIO_PAGINA) {
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
        this.resetScroll();
        this.limpiarJTable();
        this.buscar(true); 
    }

    private String getUriCriteria() {
        String uriCriteria = "idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa();
        if (chk_Cliente.isSelected()) {
            uriCriteria += "&idCliente=" + ((Cliente) cmb_Cliente.getSelectedItem()).getId_Cliente();
        }
        if (chk_Fecha.isSelected()) {
            uriCriteria += "&desde=" + dc_FechaDesde.getDate().getTime()
                    + "&hasta=" + dc_FechaHasta.getDate().getTime();
        }
        if (chk_NumFactura.isSelected()) {
            uriCriteria += "&nroFactura=" + Long.valueOf(txt_NroFactura.getText())
                    + "&nroSerie=" + Long.valueOf(txt_SerieFactura.getText());
        }
        if (chk_TipoFactura.isSelected()) {
            uriCriteria += "&tipoDeComprobante=" + ((TipoDeComprobante) cmb_TipoFactura.getSelectedItem()).name();
        }
        if (chk_Viajante.isSelected()) {
            uriCriteria += "&idViajante=" + ((Usuario) cmb_Viajante.getSelectedItem()).getId_Usuario();
        }
        if (chk_Vendedor.isSelected()) {
            uriCriteria += "&idUsuario=" + ((Usuario) cmb_Vendedor.getSelectedItem()).getId_Usuario();
        }
        if (chk_NumeroPedido.isSelected()) {
            uriCriteria += "&nroPedido=" + Long.parseLong(txt_NumeroPedido.getText());
        }
        uriCriteria += "&pagina=" + NUMERO_PAGINA + "&tamanio=" + TAMANIO_PAGINA;
        return uriCriteria;
    }

    private void setColumnas() {
        // Momentaneamente desactivado hasta terminar la paginacion.        
        //sorting
        // tbl_Resultados.setAutoCreateRowSorter(true);        
        //nombres de columnas
        String[] encabezados = new String[19];
        encabezados[0] = "CAE";
        encabezados[1] = "Fecha Factura";
        encabezados[2] = "Tipo";
        encabezados[3] = "Nº Factura";
        encabezados[4] = "Fecha Vencimiento";
        encabezados[5] = "Cliente";
        encabezados[6] = "Vendedor";
        encabezados[7] = "Transportista";
        encabezados[8] = "Total";
        encabezados[9] = "SubTotal";
        encabezados[10] = "% Descuento";
        encabezados[11] = "Descuento neto";
        encabezados[12] = "% Recargo";
        encabezados[13] = "Recargo neto";
        encabezados[14] = "SubTotal bruto";
        encabezados[15] = "IVA 10.5% neto";
        encabezados[16] = "IVA 21% neto";
        encabezados[17] = "Nº Factura Afip";
        encabezados[18] = "Vencimiento CAE";
        modeloTablaFacturas.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaFacturas);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaFacturas.getColumnCount()];
        tipos[0] = Object.class;
        tipos[1] = Date.class;
        tipos[2] = TipoDeComprobante.class;
        tipos[3] = String.class;
        tipos[4] = Date.class;
        tipos[5] = String.class;
        tipos[6] = String.class;
        tipos[7] = String.class;
        tipos[8] = BigDecimal.class;
        tipos[9] = BigDecimal.class;
        tipos[10] = BigDecimal.class;
        tipos[11] = BigDecimal.class;
        tipos[12] = BigDecimal.class;
        tipos[13] = BigDecimal.class;
        tipos[14] = BigDecimal.class;
        tipos[15] = BigDecimal.class;
        tipos[16] = BigDecimal.class;
        tipos[17] = String.class;
        tipos[18] = Date.class;
        modeloTablaFacturas.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);        
        //tamanios de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(120);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(140);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(90);
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(130);
        tbl_Resultados.getColumnModel().getColumn(5).setPreferredWidth(280);
        tbl_Resultados.getColumnModel().getColumn(6).setPreferredWidth(190);
        tbl_Resultados.getColumnModel().getColumn(7).setPreferredWidth(190);
        tbl_Resultados.getColumnModel().getColumn(8).setPreferredWidth(120);
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
        //render para los tipos de datos
        tbl_Resultados.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());
        tbl_Resultados.getColumnModel().getColumn(1).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHAHORA_HISPANO));
        tbl_Resultados.getColumnModel().getColumn(4).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHA_HISPANO));
        tbl_Resultados.getColumnModel().getColumn(18).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHA_HISPANO));
    }

    private void calcularResultados(String uriCriteria) {
        txt_ResultTotalFacturado.setValue(RestClient.getRestTemplate()
                .getForObject("/facturas/total-facturado-venta/criteria?" + uriCriteria, BigDecimal.class));
        txt_ResultGananciaTotal.setValue(RestClient.getRestTemplate()
                .getForObject("/facturas/ganancia-total/criteria?" + uriCriteria, BigDecimal.class));
        txt_ResultTotalIVAVenta.setValue(RestClient.getRestTemplate()
                .getForObject("/facturas/total-iva-venta/criteria?" + uriCriteria, BigDecimal.class));        
    }

    private void buscar(boolean calcularResultados) {
        this.cambiarEstadoEnabledComponentes(false);
        String uriCriteria = getUriCriteria();
        try {
            PaginaRespuestaRest<FacturaVenta> response = RestClient.getRestTemplate()
                    .exchange("/facturas/venta/busqueda/criteria?" + uriCriteria, HttpMethod.GET, null,
                            new ParameterizedTypeReference<PaginaRespuestaRest<FacturaVenta>>() {
                    })
                    .getBody();
            totalElementosBusqueda = response.getTotalElements();
            facturasParcial = response.getContent();
            facturasTotal.addAll(facturasParcial);
            if (calcularResultados) {
                this.calcularResultados(getUriCriteria());
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
            cmb_Cliente.setEnabled(true);
        } else {
            cmb_Cliente.setEnabled(false);
        }
        chk_Viajante.setEnabled(status);
        if (status == true && chk_Viajante.isSelected() == true) {
            cmb_Viajante.setEnabled(true);
        } else {
            cmb_Viajante.setEnabled(false);
        }
        chk_Vendedor.setEnabled(status);
        if (status == true && chk_Vendedor.isSelected() == true) {
            cmb_Vendedor.setEnabled(true);
        } else {
            cmb_Vendedor.setEnabled(false);
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
        btn_Nueva.setEnabled(status);
        btn_Eliminar.setEnabled(status);
        btn_VerDetalle.setEnabled(status);
        btn_Autorizar.setEnabled(status);
        tbl_Resultados.setEnabled(status);
        sp_Resultados.setEnabled(status);
        tbl_Resultados.requestFocus();
    }

    private void cargarResultadosAlTable() {
        facturasParcial.stream().map(factura -> {
            Object[] fila = new Object[20];
            fila[0] = factura.getCAE() == 0 ? "" : factura.getCAE();
            fila[1] = factura.getFecha();
            fila[2] = factura.getTipoComprobante();
            fila[3] = factura.getNumSerie() + " - " + factura.getNumFactura();
            fila[4] = factura.getFechaVencimiento();
            fila[5] = factura.getRazonSocialCliente();
            fila[6] = factura.getNombreUsuario();
            fila[7] = factura.getNombreTransportista();
            fila[8] = factura.getTotal();
            fila[9] = factura.getSubTotal();
            fila[10] = factura.getDescuento_porcentaje();
            fila[11] = factura.getDescuento_neto();
            fila[12] = factura.getRecargo_porcentaje();
            fila[13] = factura.getRecargo_neto();
            fila[14] = factura.getSubTotal_bruto();
            fila[15] = factura.getIva_105_neto();
            fila[16] = factura.getIva_21_neto();
            if (factura.getNumSerieAfip() == 0 && factura.getNumFacturaAfip() == 0) {
                fila[17] = "";
            } else {
                fila[17] = factura.getNumSerieAfip() + " - " + factura.getNumFacturaAfip();
            }
            fila[18] = factura.getVencimientoCAE();
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

    private void cargarClientes() {
        if (chk_Cliente.isSelected() == true) {
            cmb_Cliente.setEnabled(true);
            cmb_Cliente.removeAllItems();
            try {
                String criteriaBusqueda = "/clientes/busqueda/criteria?idEmpresa="
                        + String.valueOf(EmpresaActiva.getInstance().getEmpresa().getId_Empresa())
                        + "&pagina=0&tamanio=" + Integer.MAX_VALUE
                        + "&conSaldo=false";
                PaginaRespuestaRest<Cliente> response = RestClient.getRestTemplate()
                        .exchange(criteriaBusqueda, HttpMethod.GET, null,
                                new ParameterizedTypeReference<PaginaRespuestaRest<Cliente>>() {
                        })
                        .getBody();
                response.getContent().stream().forEach((c) -> {
                    cmb_Cliente.addItem(c);
                });
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            cmb_Cliente.requestFocus();
        } else {
            cmb_Cliente.removeAllItems();
            cmb_Cliente.setEnabled(false);
        }
    }

    private void cargarUsuarios() {
        if (chk_Vendedor.isSelected() == true) {
            cmb_Vendedor.setEnabled(true);
            cmb_Vendedor.removeAllItems();
            try {
                PaginaRespuestaRest<Usuario> response = RestClient.getRestTemplate()
                        .exchange("/usuarios/busqueda/criteria?idEmpresa="
                                + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                                + "&roles=" + Rol.VENDEDOR
                                + "&pagina=0&tamanio=" + Integer.MAX_VALUE, HttpMethod.GET, null,
                                new ParameterizedTypeReference<PaginaRespuestaRest<Usuario>>() {
                        })
                        .getBody();
                response.getContent().stream().forEach(u -> {
                    cmb_Vendedor.addItem(u);
                });
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            cmb_Vendedor.requestFocus();
        } else {
            cmb_Vendedor.removeAllItems();
            cmb_Vendedor.setEnabled(false);
        }
    }

    private void cargarUsuariosViajante() {
        if (chk_Viajante.isSelected() == true) {
            cmb_Viajante.setEnabled(true);
            cmb_Viajante.removeAllItems();
            try {
                PaginaRespuestaRest<Usuario> response = RestClient.getRestTemplate()
                        .exchange("/usuarios/busqueda/criteria?idEmpresa="
                                + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                                + "&roles=" + Rol.VIAJANTE
                                + "&pagina=0&tamanio=" + Integer.MAX_VALUE, HttpMethod.GET, null,
                                new ParameterizedTypeReference<PaginaRespuestaRest<Usuario>>() {
                        })
                        .getBody();
                response.getContent().stream().forEach(u -> {
                    cmb_Viajante.addItem(u);
                });
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            cmb_Viajante.requestFocus();
        } else {
            cmb_Viajante.removeAllItems();
            cmb_Viajante.setEnabled(false);
        }
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

    private void lanzarReporteFactura() {
        if (Desktop.isDesktopSupported()) {
            try {
                int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
                byte[] reporte = RestClient.getRestTemplate()
                        .getForObject("/facturas/" + facturasTotal.get(indexFilaSeleccionada).getId_Factura() + "/reporte",
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

    private boolean existeClienteDisponible() {
        String criteriaBusqueda = "/clientes/busqueda/criteria?idEmpresa="
                + String.valueOf(EmpresaActiva.getInstance().getEmpresa().getId_Empresa())
                + "&pagina=0&tamanio=" + 1 + "&conSaldo=false";
        PaginaRespuestaRest<Cliente> response = RestClient.getRestTemplate()
                .exchange(criteriaBusqueda, HttpMethod.GET, null,
                        new ParameterizedTypeReference<PaginaRespuestaRest<Cliente>>() {
                })
                .getBody();
        return !response.getContent().isEmpty();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btn_VerDetalle = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();
        panelNumeros = new javax.swing.JPanel();
        lbl_TotalFacturado = new javax.swing.JLabel();
        lbl_GananciaTotal = new javax.swing.JLabel();
        lbl_TotalIVAVenta = new javax.swing.JLabel();
        txt_ResultTotalFacturado = new javax.swing.JFormattedTextField();
        txt_ResultGananciaTotal = new javax.swing.JFormattedTextField();
        txt_ResultTotalIVAVenta = new javax.swing.JFormattedTextField();
        btn_Nueva = new javax.swing.JButton();
        btn_Autorizar = new javax.swing.JButton();
        panelFiltros = new javax.swing.JPanel();
        subPanelFiltros1 = new javax.swing.JPanel();
        chk_Fecha = new javax.swing.JCheckBox();
        chk_Cliente = new javax.swing.JCheckBox();
        cmb_Cliente = new javax.swing.JComboBox();
        lbl_Hasta = new javax.swing.JLabel();
        lbl_Desde = new javax.swing.JLabel();
        dc_FechaDesde = new com.toedter.calendar.JDateChooser();
        dc_FechaHasta = new com.toedter.calendar.JDateChooser();
        chk_Viajante = new javax.swing.JCheckBox();
        cmb_Viajante = new javax.swing.JComboBox();
        chk_Vendedor = new javax.swing.JCheckBox();
        cmb_Vendedor = new javax.swing.JComboBox();
        chk_NumFactura = new javax.swing.JCheckBox();
        txt_SerieFactura = new javax.swing.JFormattedTextField();
        separador = new javax.swing.JLabel();
        txt_NroFactura = new javax.swing.JFormattedTextField();
        subPanelFiltros2 = new javax.swing.JPanel();
        chk_TipoFactura = new javax.swing.JCheckBox();
        cmb_TipoFactura = new javax.swing.JComboBox();
        chk_NumeroPedido = new javax.swing.JCheckBox();
        txt_NumeroPedido = new javax.swing.JFormattedTextField();
        btn_Buscar = new javax.swing.JButton();
        lbl_cantResultados = new javax.swing.JLabel();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Facturas de Venta");
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

        panelResultados.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        tbl_Resultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resultados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        sp_Resultados.setViewportView(tbl_Resultados);

        btn_VerDetalle.setForeground(java.awt.Color.blue);
        btn_VerDetalle.setText("Ver Detalle");
        btn_VerDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_VerDetalleActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Cancel_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar ");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
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
                    .addComponent(lbl_TotalIVAVenta, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_GananciaTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_TotalFacturado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_ResultTotalIVAVenta, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                    .addComponent(txt_ResultGananciaTotal)
                    .addComponent(txt_ResultTotalFacturado, javax.swing.GroupLayout.Alignment.TRAILING)))
        );
        panelNumerosLayout.setVerticalGroup(
            panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNumerosLayout.createSequentialGroup()
                .addGroup(panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_TotalFacturado)
                    .addComponent(txt_ResultTotalFacturado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_GananciaTotal)
                    .addComponent(txt_ResultGananciaTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelNumerosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_TotalIVAVenta)
                    .addComponent(txt_ResultTotalIVAVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        btn_Nueva.setForeground(java.awt.Color.blue);
        btn_Nueva.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Add_16x16.png"))); // NOI18N
        btn_Nueva.setText("Nueva");
        btn_Nueva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevaActionPerformed(evt);
            }
        });

        btn_Autorizar.setForeground(java.awt.Color.blue);
        btn_Autorizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Certificate_16x16.png"))); // NOI18N
        btn_Autorizar.setText("Autorizar");
        btn_Autorizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AutorizarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(btn_Nueva, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(btn_Eliminar)
                .addGap(0, 0, 0)
                .addComponent(btn_Autorizar)
                .addGap(0, 0, 0)
                .addComponent(btn_VerDetalle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelNumeros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(sp_Resultados)
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Autorizar, btn_Eliminar, btn_Nueva, btn_VerDetalle});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createSequentialGroup()
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                .addGap(7, 7, 7)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelNumeros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btn_Nueva)
                        .addComponent(btn_Eliminar)
                        .addComponent(btn_Autorizar)
                        .addComponent(btn_VerDetalle))))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Autorizar, btn_Eliminar, btn_Nueva, btn_VerDetalle});

        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        chk_Fecha.setText("Fecha Factura:");
        chk_Fecha.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FechaItemStateChanged(evt);
            }
        });

        chk_Cliente.setText("Cliente:");
        chk_Cliente.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ClienteItemStateChanged(evt);
            }
        });

        cmb_Cliente.setEnabled(false);

        lbl_Hasta.setText("Hasta:");
        lbl_Hasta.setEnabled(false);

        lbl_Desde.setText("Desde:");
        lbl_Desde.setEnabled(false);

        dc_FechaDesde.setDateFormatString("dd/MM/yyyy");
        dc_FechaDesde.setEnabled(false);

        dc_FechaHasta.setDateFormatString("dd/MM/yyyy");
        dc_FechaHasta.setEnabled(false);

        chk_Viajante.setText("Viajante:");
        chk_Viajante.setToolTipText("");
        chk_Viajante.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ViajanteItemStateChanged(evt);
            }
        });

        cmb_Viajante.setEnabled(false);

        chk_Vendedor.setText("Vendedor:");
        chk_Vendedor.setToolTipText("");
        chk_Vendedor.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_VendedorItemStateChanged(evt);
            }
        });

        cmb_Vendedor.setEnabled(false);

        chk_NumFactura.setText("Nº de Factura:");
        chk_NumFactura.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_NumFacturaItemStateChanged(evt);
            }
        });

        txt_SerieFactura.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_SerieFactura.setText("0");
        txt_SerieFactura.setEnabled(false);
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
        txt_NroFactura.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_NroFacturaKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout subPanelFiltros1Layout = new javax.swing.GroupLayout(subPanelFiltros1);
        subPanelFiltros1.setLayout(subPanelFiltros1Layout);
        subPanelFiltros1Layout.setHorizontalGroup(
            subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(chk_NumFactura, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Viajante, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Fecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Cliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_Vendedor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                        .addComponent(txt_SerieFactura)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(separador, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_NroFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cmb_Viajante, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                        .addComponent(lbl_Desde)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dc_FechaDesde, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_Hasta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dc_FechaHasta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                        .addComponent(cmb_Cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(cmb_Vendedor, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        subPanelFiltros1Layout.setVerticalGroup(
            subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros1Layout.createSequentialGroup()
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Hasta)
                    .addComponent(lbl_Desde)
                    .addComponent(chk_Fecha))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Cliente)
                    .addComponent(cmb_Cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Viajante)
                    .addComponent(cmb_Viajante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Vendedor)
                    .addComponent(cmb_Vendedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chk_NumFactura)
                    .addComponent(txt_SerieFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(separador)
                    .addComponent(txt_NroFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        chk_TipoFactura.setText("Tipo de Factura:");
        chk_TipoFactura.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_TipoFacturaItemStateChanged(evt);
            }
        });

        cmb_TipoFactura.setEnabled(false);

        chk_NumeroPedido.setText("Nº de Pedido:");
        chk_NumeroPedido.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_NumeroPedidoItemStateChanged(evt);
            }
        });

        txt_NumeroPedido.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt_NumeroPedido.setText("0");
        txt_NumeroPedido.setEnabled(false);
        txt_NumeroPedido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_NumeroPedidoKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout subPanelFiltros2Layout = new javax.swing.GroupLayout(subPanelFiltros2);
        subPanelFiltros2.setLayout(subPanelFiltros2Layout);
        subPanelFiltros2Layout.setHorizontalGroup(
            subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros2Layout.createSequentialGroup()
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(chk_NumeroPedido, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chk_TipoFactura, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_NumeroPedido, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cmb_TipoFactura, 0, 129, Short.MAX_VALUE)))
        );
        subPanelFiltros2Layout.setVerticalGroup(
            subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subPanelFiltros2Layout.createSequentialGroup()
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_TipoFactura)
                    .addComponent(cmb_TipoFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(subPanelFiltros2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_NumeroPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_NumeroPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

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
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelFiltrosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btn_Buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(subPanelFiltros1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(subPanelFiltros2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(subPanelFiltros1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(subPanelFiltros2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btn_Buscar)
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 87, Short.MAX_VALUE))
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

    private void chk_ClienteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ClienteItemStateChanged
        this.cargarClientes();
}//GEN-LAST:event_chk_ClienteItemStateChanged

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.resetScroll();
        this.limpiarJTable();
        this.buscar(true);
}//GEN-LAST:event_btn_BuscarActionPerformed

    private void btn_VerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_VerDetalleActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            this.lanzarReporteFactura();
        }
}//GEN-LAST:event_btn_VerDetalleActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            int respuesta = JOptionPane.showConfirmDialog(this, ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_eliminar_multiples_facturas"),
                    "Eliminar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                int[] indexFilasSeleccionadas = Utilidades.getSelectedRowsModelIndices(tbl_Resultados);
                long[] idsFacturas = new long[indexFilasSeleccionadas.length];
                int i = 0;
                for (int indice : indexFilasSeleccionadas) {
                    idsFacturas[i] = facturasTotal.get(indice).getId_Factura();
                    i++;
                }
                try {
                    RestClient.getRestTemplate().delete("/facturas?idFactura="
                            + Arrays.toString(idsFacturas).substring(1, Arrays.toString(idsFacturas).length() - 1));
                    this.resetScroll();
                    this.limpiarJTable();
                    this.buscar(true);
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
        } catch (PropertyVetoException ex) {
            String mensaje = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void chk_ViajanteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ViajanteItemStateChanged
        this.cargarUsuariosViajante();
    }//GEN-LAST:event_chk_ViajanteItemStateChanged

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

    private void btn_NuevaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevaActionPerformed
        if (this.existeClienteDisponible()) {
            JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), PuntoDeVentaGUI.class);
            if (gui == null) {
                PuntoDeVentaGUI puntoDeVentaGUI = new PuntoDeVentaGUI();
                puntoDeVentaGUI.setLocation(getDesktopPane().getWidth() / 2 - puntoDeVentaGUI.getWidth() / 2,
                        getDesktopPane().getHeight() / 2 - puntoDeVentaGUI.getHeight() / 2);
                getDesktopPane().add(puntoDeVentaGUI);
                puntoDeVentaGUI.setMaximizable(true);
                puntoDeVentaGUI.setClosable(true);
                puntoDeVentaGUI.setVisible(true);
            } else {
                //selecciona y trae al frente el internalframe
                try {
                    gui.setSelected(true);
                } catch (PropertyVetoException ex) {
                    String msjError = "No se pudo seleccionar la ventana requerida.";
                    LOGGER.error(msjError + " - " + ex.getMessage());
                    JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showInternalMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_sin_cliente"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_NuevaActionPerformed

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

    private void chk_VendedorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_VendedorItemStateChanged
        this.cargarUsuarios();
    }//GEN-LAST:event_chk_VendedorItemStateChanged

    private void btn_AutorizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AutorizarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1 && tbl_Resultados.getSelectedRowCount() == 1) {
            int indexFilaSeleccionada = Utilidades.getSelectedRowModelIndice(tbl_Resultados);
            long idFacturaSeleccionada = facturasTotal.get(indexFilaSeleccionada).getId_Factura();
            try {
                RestClient.getRestTemplate().postForObject("/facturas/" + idFacturaSeleccionada + "/autorizacion",
                        null, FacturaVenta.class);
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_factura_autorizada"),
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
                this.resetScroll();
                this.limpiarJTable();
                this.buscar(false);
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_AutorizarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Autorizar;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_Nueva;
    private javax.swing.JButton btn_VerDetalle;
    private javax.swing.JCheckBox chk_Cliente;
    private javax.swing.JCheckBox chk_Fecha;
    private javax.swing.JCheckBox chk_NumFactura;
    private javax.swing.JCheckBox chk_NumeroPedido;
    private javax.swing.JCheckBox chk_TipoFactura;
    private javax.swing.JCheckBox chk_Vendedor;
    private javax.swing.JCheckBox chk_Viajante;
    private javax.swing.JComboBox cmb_Cliente;
    private javax.swing.JComboBox cmb_TipoFactura;
    private javax.swing.JComboBox cmb_Vendedor;
    private javax.swing.JComboBox cmb_Viajante;
    private com.toedter.calendar.JDateChooser dc_FechaDesde;
    private com.toedter.calendar.JDateChooser dc_FechaHasta;
    private javax.swing.JLabel lbl_Desde;
    private javax.swing.JLabel lbl_GananciaTotal;
    private javax.swing.JLabel lbl_Hasta;
    private javax.swing.JLabel lbl_TotalFacturado;
    private javax.swing.JLabel lbl_TotalIVAVenta;
    private javax.swing.JLabel lbl_cantResultados;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelNumeros;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JLabel separador;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JPanel subPanelFiltros1;
    private javax.swing.JPanel subPanelFiltros2;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JFormattedTextField txt_NroFactura;
    private javax.swing.JFormattedTextField txt_NumeroPedido;
    private javax.swing.JFormattedTextField txt_ResultGananciaTotal;
    private javax.swing.JFormattedTextField txt_ResultTotalFacturado;
    private javax.swing.JFormattedTextField txt_ResultTotalIVAVenta;
    private javax.swing.JFormattedTextField txt_SerieFactura;
    // End of variables declaration//GEN-END:variables
}
