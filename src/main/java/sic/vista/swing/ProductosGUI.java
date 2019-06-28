package sic.vista.swing;

import java.awt.Desktop;
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
import sic.modelo.EmpresaActiva;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.Rol;
import sic.modelo.Rubro;
import sic.modelo.UsuarioActivo;
import sic.util.DecimalesRenderer;
import sic.util.FechasRenderer;
import sic.util.FormatosFechaHora;
import sic.util.Utilidades;

public class ProductosGUI extends JInternalFrame {

    private ModeloTabla modeloTablaResultados = new ModeloTabla();
    private List<Producto> productosTotal = new ArrayList<>();
    private List<Producto> productosParcial = new ArrayList<>();
    private Proveedor proveedorSeleccionado;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame = new Dimension(880, 600);
    private static int totalElementosBusqueda;
    private static int NUMERO_PAGINA = 0;    
    private List<Rubro> rubros;

    public ProductosGUI() {
        this.initComponents();        
        sp_Resultados.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int va = scrollBar.getVisibleAmount() + 10;
            if (scrollBar.getValue() >= (scrollBar.getMaximum() - va)) {
                if (productosTotal.size() >= 10) {
                    NUMERO_PAGINA += 1;
                    buscar();
                }
            }
        });
    }

    private void cargarRubros() {
        try {
            rubros = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/rubros/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                            Rubro[].class)));
            cmb_Rubro.removeAllItems();
            rubros.stream().forEach(r -> {
                cmb_Rubro.addItem(r.getNombre());
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
        String[] encabezados = new String[24];
        encabezados[0] = "Público";
        encabezados[1] = "Destacado";
        encabezados[2] = "Codigo";
        encabezados[3] = "Descripcion";
        encabezados[4] = "Cant. Disponible";
        encabezados[5] = "Cant. Minima";
        encabezados[6] = "Cant. por Bulto";
        encabezados[7] = "Sin Límite";
        encabezados[8] = "Medida";
        encabezados[9] = "Precio Costo";
        encabezados[10] = "% Ganancia";
        encabezados[11] = "Ganancia";
        encabezados[12] = "PVP";
        encabezados[13] = "% IVA";
        encabezados[14] = "IVA";
        encabezados[15] = "Precio Lista";
        encabezados[16] = "Rubro";
        encabezados[17] = "Fecha U. Modificacion";
        encabezados[18] = "Estanteria";
        encabezados[19] = "Estante";
        encabezados[20] = "Proveedor";
        encabezados[21] = "Fecha Alta";
        encabezados[22] = "Fecha Vencimiento";
        encabezados[23] = "Nota";
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaResultados);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = Boolean.class;
        tipos[1] = Boolean.class;
        tipos[2] = String.class;
        tipos[3] = String.class;
        tipos[4] = BigDecimal.class;
        tipos[5] = BigDecimal.class;
        tipos[6] = BigDecimal.class;
        tipos[7] = Boolean.class;
        tipos[8] = String.class;
        tipos[9] = BigDecimal.class;
        tipos[10] = BigDecimal.class;
        tipos[11] = BigDecimal.class;
        tipos[12] = BigDecimal.class;
        tipos[13] = BigDecimal.class;
        tipos[14] = BigDecimal.class;
        tipos[15] = BigDecimal.class;
        tipos[16] = String.class;
        tipos[17] = Date.class;
        tipos[18] = String.class;
        tipos[19] = String.class;
        tipos[20] = String.class;
        tipos[21] = Date.class;
        tipos[22] = Date.class;
        tipos[23] = String.class;
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);
        //render para los tipos de datos
        tbl_Resultados.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());
        //tamanios de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(70);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(75);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(150);
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(400);
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(110);
        tbl_Resultados.getColumnModel().getColumn(5).setPreferredWidth(110);
        tbl_Resultados.getColumnModel().getColumn(6).setPreferredWidth(110);
        tbl_Resultados.getColumnModel().getColumn(7).setPreferredWidth(80);
        tbl_Resultados.getColumnModel().getColumn(8).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(9).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(10).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(11).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(12).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(13).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(14).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(15).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(16).setPreferredWidth(180);
        tbl_Resultados.getColumnModel().getColumn(17).setPreferredWidth(150);
        tbl_Resultados.getColumnModel().getColumn(18).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(19).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(20).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(21).setPreferredWidth(125);
        tbl_Resultados.getColumnModel().getColumn(22).setPreferredWidth(125);
        tbl_Resultados.getColumnModel().getColumn(23).setPreferredWidth(400);
        //renderers
        tbl_Resultados.getColumnModel().getColumn(16).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHAHORA_HISPANO));
        tbl_Resultados.getColumnModel().getColumn(20).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHA_HISPANO));
        tbl_Resultados.getColumnModel().getColumn(21).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHA_HISPANO));
    }

    private void cargarResultadosAlTable() {
        productosParcial.stream().map(producto -> {
            Object[] fila = new Object[24];
            fila[0] = producto.isPublico();
            fila[1] =  producto.isDestacado();
            fila[2] = producto.getCodigo();
            fila[3] = producto.getDescripcion();
            fila[4] = producto.getCantidad();
            fila[5] = producto.getCantMinima();
            fila[6] = producto.getBulto();
            fila[7] = producto.isIlimitado();
            fila[8] = producto.getNombreMedida();
            fila[9] = producto.getPrecioCosto();
            fila[10] = producto.getGananciaPorcentaje();
            fila[11] = producto.getGananciaNeto();
            fila[12] = producto.getPrecioVentaPublico();
            fila[13] = producto.getIvaPorcentaje();
            fila[14] = producto.getIvaNeto();
            fila[15] = producto.getPrecioLista();
            fila[16] = producto.getNombreRubro();
            fila[17] = producto.getFechaUltimaModificacion();
            fila[18] = producto.getEstanteria();
            fila[19] = producto.getEstante();
            fila[20] = producto.getRazonSocialProveedor();
            fila[21] = producto.getFechaAlta();
            fila[22] = producto.getFechaVencimiento();
            fila[23] = producto.getNota();
            return fila;
        }).forEach(fila -> {
            modeloTablaResultados.addRow(fila);
        });
        tbl_Resultados.setModel(modeloTablaResultados);
        lbl_cantResultados.setText(totalElementosBusqueda + " productos encontrados");
    }

    private void resetScroll() {
        NUMERO_PAGINA = 0;
        productosTotal.clear();
        productosParcial.clear();
        Point p = new Point(0, 0);
        sp_Resultados.getViewport().setViewPosition(p);
    }

    private void limpiarJTable() {
        modeloTablaResultados = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaResultados);
        this.setColumnas();
    }

    private void cambiarEstadoEnabledComponentes(boolean status) {
        chkCodigoODescripcion.setEnabled(status);
        if (status == true && chkCodigoODescripcion.isSelected() == true) {
            txtCodigoODescripcion.setEnabled(true);
        } else {
            txtCodigoODescripcion.setEnabled(false);
        }
        chk_Rubro.setEnabled(status);
        if (status == true && chk_Rubro.isSelected() == true) {
            cmb_Rubro.setEnabled(true);
        } else {
            cmb_Rubro.setEnabled(false);
        }
        chk_Proveedor.setEnabled(status);
        if (status == true && chk_Proveedor.isSelected() == true) {
            btnBuscarProveedor.setEnabled(true);
        } else {
            btnBuscarProveedor.setEnabled(false);
        }
        chk_Disponibilidad.setEnabled(status);
        if (status == true && chk_Disponibilidad.isSelected() == true) {
            rbEnStock.setEnabled(true);
            rb_Faltantes.setEnabled(true);
        } else {
            rbEnStock.setEnabled(false);
            rb_Faltantes.setEnabled(false);
        }
        btn_Buscar.setEnabled(status);        
        btn_Nuevo.setEnabled(status);
        btn_Modificar.setEnabled(status);
        btn_Eliminar.setEnabled(status);
        btnExportar.setEnabled(status);
        tbl_Resultados.setEnabled(status);
        sp_Resultados.setEnabled(status);
        tbl_Resultados.requestFocus();
    }

    private long getIdRubroSeleccionado() {
        long idRubro = 0;
        for (Rubro r : rubros) {
            if (r.getNombre().equals(cmb_Rubro.getSelectedItem())) idRubro = r.getId_Rubro();
        }
        return idRubro;
    }
    
    private long getIdProveedorSeleccionado() {
        return ((proveedorSeleccionado != null) ? proveedorSeleccionado.getId_Proveedor() : 0);
    }

    private void exportar() {
        String uriReporte = "/productos/reporte/criteria?"
                + "&idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa();
        if (chkCodigoODescripcion.isSelected()) {
            uriReporte += "&codigo=" + txtCodigoODescripcion.getText().trim();
            uriReporte += "&descripcion=" + txtCodigoODescripcion.getText().trim();
        }
        if (chk_Rubro.isSelected()) {
            uriReporte += "&idRubro=" + this.getIdRubroSeleccionado();
        }
        if (chk_Proveedor.isSelected()) {
            uriReporte += "&idProveedor=" + this.getIdProveedorSeleccionado();
        }
        if (chk_Disponibilidad.isSelected()) {
            if (rb_Faltantes.isSelected()) {
                uriReporte += "&soloFantantes=true";
            }
            if (rbEnStock.isSelected()) {
                uriReporte += "&soloEnStock=true";
            }
        }
        if (chk_visibilidad.isSelected()) {
            if (rb_publico.isSelected()) {
                uriReporte += "&publicos=true";
            } else if (rb_privado.isSelected()) {
                uriReporte += "&publicos=false";                        
            }
        }
        int seleccionOrden = cmbOrden.getSelectedIndex();
        switch (seleccionOrden) {
            case 0: uriReporte += "&ordenarPor=descripcion"; break;
            case 1: uriReporte += "&ordenarPor=codigo"; break;
            case 2: uriReporte += "&ordenarPor=cantidad"; break;
            case 3: uriReporte += "&ordenarPor=precioCosto"; break;
            case 4: uriReporte += "&ordenarPor=gananciaPorcentaje"; break;
            case 5: uriReporte += "&ordenarPor=precioLista"; break;
            case 6: uriReporte += "&ordenarPor=fechaAlta"; break;
            case 7: uriReporte += "&ordenarPor=fechaUltimaModificacion"; break;
        }
        int seleccionDireccion = cmbSentido.getSelectedIndex();
        switch (seleccionDireccion) {
            case 0: uriReporte += "&sentido=ASC"; break;
            case 1: uriReporte += "&sentido=DESC"; break;
        }
        ExportGUI exportGUI = new ExportGUI(uriReporte + "&formato=xlsx", "ListaPrecios.xlsx",
                uriReporte + "&formato=pdf", "ListaPrecios.pdf");
        exportGUI.setModal(true);
        exportGUI.setLocationRelativeTo(this);
        exportGUI.setVisible(true);
    }
    
    private void buscar() {
        this.cambiarEstadoEnabledComponentes(false);
        long idEmpresa = EmpresaActiva.getInstance().getEmpresa().getId_Empresa();
        String criteriaBusqueda = "/productos/busqueda/criteria?idEmpresa=" + idEmpresa;
        String criteriaCosto = "/productos/valor-stock/criteria?idEmpresa=" + idEmpresa;
        if (chkCodigoODescripcion.isSelected()) {
            criteriaBusqueda += "&codigo=" + txtCodigoODescripcion.getText().trim();
            criteriaCosto += "&codigo=" + txtCodigoODescripcion.getText().trim();
            criteriaBusqueda += "&descripcion=" + txtCodigoODescripcion.getText().trim();
            criteriaCosto += "&descripcion=" + txtCodigoODescripcion.getText().trim();
        }
        if (chk_Rubro.isSelected()) {
            criteriaBusqueda += "&idRubro=" + this.getIdRubroSeleccionado();
            criteriaCosto += "&idRubro=" + this.getIdRubroSeleccionado();
        }
        if (chk_Proveedor.isSelected() &&  this.getIdProveedorSeleccionado() != 0L) {
            criteriaBusqueda += "&idProveedor=" + this.getIdProveedorSeleccionado();
            criteriaCosto += "&idProveedor=" + this.getIdProveedorSeleccionado();
        }
        if (chk_Disponibilidad.isSelected()) {
            if (rb_Faltantes.isSelected()) {
                criteriaBusqueda += "&soloFantantes=true";
                criteriaCosto += "&soloFantantes=true";
            }
            if (rbEnStock.isSelected()) {
                criteriaBusqueda += "&soloEnStock=true";
                criteriaCosto += "&soloEnStock=true";
            }
        }
        if (chk_visibilidad.isSelected()) {
            if (rb_publico.isSelected()) {
                criteriaBusqueda += "&publicos=true";
                criteriaCosto += "&publicos=true";
            } else if (rb_privado.isSelected()) {
                criteriaBusqueda += "&publicos=false";
                criteriaCosto += "&publicos=false";
            }
        }
        if (chkDestacados.isSelected()) {
            criteriaBusqueda += "&destacados=true";
            criteriaCosto += "&destacados=true";
        }
        int seleccionOrden = cmbOrden.getSelectedIndex();
        switch (seleccionOrden) {
            case 0: criteriaBusqueda += "&ordenarPor=descripcion"; break;
            case 1: criteriaBusqueda += "&ordenarPor=codigo"; break;
            case 2: criteriaBusqueda += "&ordenarPor=cantidad"; break;
            case 3: criteriaBusqueda += "&ordenarPor=precioCosto"; break;
            case 4: criteriaBusqueda += "&ordenarPor=gananciaPorcentaje"; break;
            case 5: criteriaBusqueda += "&ordenarPor=precioLista"; break;
            case 6: criteriaBusqueda += "&ordenarPor=fechaAlta"; break;
            case 7: criteriaBusqueda += "&ordenarPor=fechaUltimaModificacion"; break;
        }
        int seleccionDireccion = cmbSentido.getSelectedIndex();
        switch (seleccionDireccion) {
            case 0: criteriaBusqueda += "&sentido=ASC"; break;
            case 1: criteriaBusqueda += "&sentido=DESC"; break;
        }
        criteriaBusqueda += "&pagina=" + NUMERO_PAGINA;
        try {
            PaginaRespuestaRest<Producto> response = RestClient.getRestTemplate()
                    .exchange(criteriaBusqueda, HttpMethod.GET, null,
                            new ParameterizedTypeReference<PaginaRespuestaRest<Producto>>() {
                    })
                    .getBody();
            txt_ValorStock.setValue(RestClient.getRestTemplate().getForObject(criteriaCosto, BigDecimal.class));
            totalElementosBusqueda = response.getTotalElements();
            productosParcial = response.getContent();
            productosTotal.addAll(productosParcial);
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

    private List<Producto> getSeleccionMultipleDeProductos(int[] indices) {
        List<Producto> productosSeleccionados = new ArrayList<>();
        for (int i = 0; i < indices.length; i++) {
            productosSeleccionados.add(productosTotal.get(indices[i]));
        }
        return productosSeleccionados;
    }
    
    private void limpiarYBuscar() {
        resetScroll();
        limpiarJTable();
        this.buscar();
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

        bgDisponibilidad = new javax.swing.ButtonGroup();
        bgVisibilidad = new javax.swing.ButtonGroup();
        panelFiltros = new javax.swing.JPanel();
        chkCodigoODescripcion = new javax.swing.JCheckBox();
        chk_Proveedor = new javax.swing.JCheckBox();
        btn_Buscar = new javax.swing.JButton();
        chk_Rubro = new javax.swing.JCheckBox();
        cmb_Rubro = new javax.swing.JComboBox();
        lbl_cantResultados = new javax.swing.JLabel();
        chk_Disponibilidad = new javax.swing.JCheckBox();
        rbEnStock = new javax.swing.JRadioButton();
        rb_Faltantes = new javax.swing.JRadioButton();
        chk_visibilidad = new javax.swing.JCheckBox();
        rb_publico = new javax.swing.JRadioButton();
        rb_privado = new javax.swing.JRadioButton();
        txtProveedor = new javax.swing.JTextField();
        btnBuscarProveedor = new javax.swing.JButton();
        chkDestacados = new javax.swing.JCheckBox();
        txtCodigoODescripcion = new javax.swing.JTextField();
        panelResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btn_Nuevo = new javax.swing.JButton();
        btn_Modificar = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();
        txt_ValorStock = new javax.swing.JFormattedTextField();
        lbl_ValorStock = new javax.swing.JLabel();
        btnExportar = new javax.swing.JButton();
        panelOrden = new javax.swing.JPanel();
        cmbOrden = new javax.swing.JComboBox<>();
        cmbSentido = new javax.swing.JComboBox<>();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Productos");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Product_16x16.png"))); // NOI18N
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

        chkCodigoODescripcion.setText("Código o Descripción:");
        chkCodigoODescripcion.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkCodigoODescripcionItemStateChanged(evt);
            }
        });

        chk_Proveedor.setText("Proveedor:");
        chk_Proveedor.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ProveedorItemStateChanged(evt);
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

        chk_Rubro.setText("Rubro:");
        chk_Rubro.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_RubroItemStateChanged(evt);
            }
        });

        cmb_Rubro.setEnabled(false);

        lbl_cantResultados.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        chk_Disponibilidad.setText("Disponibilidad:");
        chk_Disponibilidad.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_DisponibilidadItemStateChanged(evt);
            }
        });

        bgDisponibilidad.add(rbEnStock);
        rbEnStock.setText("En Stock");
        rbEnStock.setEnabled(false);

        bgDisponibilidad.add(rb_Faltantes);
        rb_Faltantes.setText("Faltantes");
        rb_Faltantes.setEnabled(false);

        chk_visibilidad.setText("Visibilidad:");
        chk_visibilidad.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_visibilidadItemStateChanged(evt);
            }
        });

        bgVisibilidad.add(rb_publico);
        rb_publico.setText("Públicos");
        rb_publico.setEnabled(false);

        bgVisibilidad.add(rb_privado);
        rb_privado.setText("Privados");
        rb_privado.setEnabled(false);

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

        chkDestacados.setText("Destacados");

        txtCodigoODescripcion.setEnabled(false);
        txtCodigoODescripcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoODescripcionActionPerformed(evt);
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
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(chkDestacados)
                            .addComponent(chk_Proveedor)
                            .addComponent(chk_Rubro)
                            .addComponent(chkCodigoODescripcion))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmb_Rubro, 0, 319, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFiltrosLayout.createSequentialGroup()
                                .addComponent(txtProveedor)
                                .addGap(0, 0, 0)
                                .addComponent(btnBuscarProveedor))
                            .addComponent(txtCodigoODescripcion))
                        .addGap(25, 25, 25)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(chk_Disponibilidad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_visibilidad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rb_privado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rbEnStock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rb_Faltantes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rb_publico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(6, 6, 6))
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addComponent(btn_Buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {chkCodigoODescripcion, chkDestacados, chk_Proveedor, chk_Rubro});

        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkCodigoODescripcion)
                    .addComponent(txtCodigoODescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_Disponibilidad)
                    .addComponent(rbEnStock))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Rubro)
                    .addComponent(cmb_Rubro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rb_Faltantes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Proveedor)
                    .addComponent(txtProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarProveedor)
                    .addComponent(chk_visibilidad)
                    .addComponent(rb_publico))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkDestacados)
                    .addComponent(rb_privado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btn_Buscar)
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscarProveedor, cmb_Rubro, txtCodigoODescripcion, txtProveedor});

        panelFiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Buscar, lbl_cantResultados});

        panelResultados.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        tbl_Resultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resultados.setToolTipText("Mantener presionado Ctrl ó Shift para seleccionar varios productos");
        tbl_Resultados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        sp_Resultados.setViewportView(tbl_Resultados);

        btn_Nuevo.setForeground(java.awt.Color.blue);
        btn_Nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddProduct_16x16.png"))); // NOI18N
        btn_Nuevo.setText("Nuevo");
        btn_Nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoActionPerformed(evt);
            }
        });

        btn_Modificar.setForeground(java.awt.Color.blue);
        btn_Modificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditProduct_16x16.png"))); // NOI18N
        btn_Modificar.setText("Modificar");
        btn_Modificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ModificarActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/DeleteProduct_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
            }
        });

        txt_ValorStock.setEditable(false);
        txt_ValorStock.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_ValorStock.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        lbl_ValorStock.setText("Valor del Stock:");

        btnExportar.setForeground(java.awt.Color.blue);
        btnExportar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Export_16x16.png"))); // NOI18N
        btnExportar.setText("Exportar");
        btnExportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarActionPerformed(evt);
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
                .addComponent(btnExportar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_ValorStock)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_ValorStock, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(sp_Resultados)
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnExportar, btn_Eliminar, btn_Modificar, btn_Nuevo});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_ValorStock)
                    .addComponent(txt_ValorStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Nuevo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_Modificar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_Eliminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnExportar)))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnExportar, btn_Eliminar, btn_Modificar, btn_Nuevo});

        panelOrden.setBorder(javax.swing.BorderFactory.createTitledBorder("Ordenar por"));

        cmbOrden.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Descripción", "Código", "Cantidad", "Precio Costo", "% Ganancia", "Precio Lista", "Fecha Alta", "Fecha U. Modificación" }));
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
            .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkCodigoODescripcionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkCodigoODescripcionItemStateChanged
        if (chkCodigoODescripcion.isSelected() == true) {
            txtCodigoODescripcion.setEnabled(true);
            txtCodigoODescripcion.requestFocus();
        } else {
            txtCodigoODescripcion.setEnabled(false);
        }
    }//GEN-LAST:event_chkCodigoODescripcionItemStateChanged

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

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        this.limpiarYBuscar();
    }//GEN-LAST:event_btn_BuscarActionPerformed

    private void btn_NuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoActionPerformed
        DetalleProductoGUI gui_DetalleProducto = new DetalleProductoGUI();
        gui_DetalleProducto.setModal(true);
        gui_DetalleProducto.setLocationRelativeTo(this);
        gui_DetalleProducto.setVisible(true);
        this.limpiarYBuscar();
    }//GEN-LAST:event_btn_NuevoActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            int respuesta = JOptionPane.showInternalConfirmDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_pregunta_eliminar_productos"),
                    "Eliminar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    int[] indicesProductos = Utilidades.getSelectedRowsModelIndices(tbl_Resultados);
                    long[] idsProductos = new long[indicesProductos.length];
                    for (int i = 0; i < indicesProductos.length; i++) {
                        idsProductos[i] = this.productosTotal.get(indicesProductos[i]).getIdProducto();
                    }
                    RestClient.getRestTemplate().delete("/productos?idProducto="
                            + Arrays.toString(idsProductos).substring(1, Arrays.toString(idsProductos).length() - 1));
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
            if (tbl_Resultados.getSelectedRowCount() > 1) {
                //seleccion multiple
                ModificacionMultipleProductosGUI guiModificacionMultipleProductos = new ModificacionMultipleProductosGUI(
                        this.getSeleccionMultipleDeProductos(Utilidades.getSelectedRowsModelIndices(tbl_Resultados)));
                guiModificacionMultipleProductos.setModal(true);
                guiModificacionMultipleProductos.setLocationRelativeTo(this);
                guiModificacionMultipleProductos.setVisible(true);
            } else {
                //seleccion unica
                DetalleProductoGUI gui_DetalleProducto = new DetalleProductoGUI(
                        productosTotal.get(Utilidades.getSelectedRowModelIndice(tbl_Resultados)));
                gui_DetalleProducto.setModal(true);
                gui_DetalleProducto.setLocationRelativeTo(this);
                gui_DetalleProducto.setVisible(true);
            }
            this.limpiarYBuscar();
        }
    }//GEN-LAST:event_btn_ModificarActionPerformed

    private void chk_RubroItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_RubroItemStateChanged
        if (chk_Rubro.isSelected() == true) {
            cmb_Rubro.setEnabled(true);
            this.cargarRubros();
            cmb_Rubro.requestFocus();
        } else {
            cmb_Rubro.removeAllItems();
            cmb_Rubro.setEnabled(false);
        }
    }//GEN-LAST:event_chk_RubroItemStateChanged

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        this.setSize(sizeInternalFrame);
        rbEnStock.setSelected(true);
        rb_publico.setSelected(true);
        this.setColumnas();
        this.cambiarEstadoDeComponentesSegunRolUsuario();
        try {
            this.setMaximum(true);
        } catch (PropertyVetoException ex) {
            String mensaje = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }        
    }//GEN-LAST:event_formInternalFrameOpened

    private void chk_DisponibilidadItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_DisponibilidadItemStateChanged
        if (chk_Disponibilidad.isSelected() == true) {
            rbEnStock.setEnabled(true);
            rb_Faltantes.setEnabled(true);
        } else {
            rbEnStock.setEnabled(false);
            rb_Faltantes.setEnabled(false);
        }
    }//GEN-LAST:event_chk_DisponibilidadItemStateChanged

    private void btnExportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarActionPerformed
        if (!productosTotal.isEmpty()) {
            if (Desktop.isDesktopSupported()) {
                this.exportar();
            } else {
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_plataforma_no_soportada"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnExportarActionPerformed

    private void txtCodigoODescripcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoODescripcionActionPerformed
        btn_BuscarActionPerformed(null);
    }//GEN-LAST:event_txtCodigoODescripcionActionPerformed

    private void chk_visibilidadItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_visibilidadItemStateChanged
        if (chk_visibilidad.isSelected() == true) {
            rb_privado.setEnabled(true);
            rb_publico.setEnabled(true);
        } else {
            rb_privado.setEnabled(false);
            rb_publico.setEnabled(false);
        }
    }//GEN-LAST:event_chk_visibilidadItemStateChanged

    private void cmbOrdenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbOrdenItemStateChanged
        this.limpiarYBuscar();
    }//GEN-LAST:event_cmbOrdenItemStateChanged

    private void cmbSentidoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSentidoItemStateChanged
        this.limpiarYBuscar();
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgDisponibilidad;
    private javax.swing.ButtonGroup bgVisibilidad;
    private javax.swing.JButton btnBuscarProveedor;
    private javax.swing.JButton btnExportar;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_Modificar;
    private javax.swing.JButton btn_Nuevo;
    private javax.swing.JCheckBox chkCodigoODescripcion;
    private javax.swing.JCheckBox chkDestacados;
    private javax.swing.JCheckBox chk_Disponibilidad;
    private javax.swing.JCheckBox chk_Proveedor;
    private javax.swing.JCheckBox chk_Rubro;
    private javax.swing.JCheckBox chk_visibilidad;
    private javax.swing.JComboBox<String> cmbOrden;
    private javax.swing.JComboBox<String> cmbSentido;
    private javax.swing.JComboBox cmb_Rubro;
    private javax.swing.JLabel lbl_ValorStock;
    private javax.swing.JLabel lbl_cantResultados;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelOrden;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JRadioButton rbEnStock;
    private javax.swing.JRadioButton rb_Faltantes;
    private javax.swing.JRadioButton rb_privado;
    private javax.swing.JRadioButton rb_publico;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JTextField txtCodigoODescripcion;
    private javax.swing.JTextField txtProveedor;
    private javax.swing.JFormattedTextField txt_ValorStock;
    // End of variables declaration//GEN-END:variables
}
