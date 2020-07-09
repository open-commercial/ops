package sic.vista.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Cliente;
import sic.modelo.CuentaCorrienteCliente;
import sic.modelo.SucursalActiva;
import sic.modelo.RenglonFactura;
import sic.modelo.UsuarioActivo;
import sic.modelo.NuevoRenglonFactura;
import sic.modelo.FormaDePago;
import sic.modelo.NuevaFacturaVenta;
import sic.modelo.NuevosResultadosComprobante;
import sic.modelo.Pedido;
import sic.modelo.ProductoFaltante;
import sic.modelo.ProductosParaVerificarStock;
import sic.modelo.Resultados;
import sic.modelo.Rol;
import sic.modelo.TipoDeComprobante;
import sic.modelo.Transportista;
import sic.util.DecimalesRenderer;

public class NuevaFacturaVentaGUI extends JInternalFrame {

    private TipoDeComprobante tipoDeComprobante;
    private Cliente cliente;
    private final Pedido pedido;
    private List<RenglonFactura> renglonesFactura = new ArrayList<>();
    private ModeloTabla modeloTablaResultados = new ModeloTabla();
    private final HotKeysHandler keyHandler = new HotKeysHandler();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame = new Dimension(1200, 700);    
    private final List<Rol> rolesDeUsuario = UsuarioActivo.getInstance().getUsuario().getRoles();
    private final static BigDecimal CIEN = new BigDecimal("100");
    
    public NuevaFacturaVentaGUI(Pedido pedido) {
        this.initComponents();        
        this.pedido = pedido;
        ImageIcon iconoNoMarcado = new ImageIcon(getClass().getResource("/sic/icons/chkNoMarcado_16x16.png"));
        this.tbtn_marcarDesmarcar.setIcon(iconoNoMarcado);        
        this.setListeners();
    }
    
    private NuevaFacturaVenta construirNuevaFactura() {
        NuevaFacturaVenta nuevaFacturaVenta = NuevaFacturaVenta.builder()
                .idSucursal(SucursalActiva.getInstance().getSucursal().getIdSucursal())
                .idCliente(this.cliente.getIdCliente())
                .tipoDeComprobante(this.tipoDeComprobante)
                .observaciones(this.txt_Observaciones.getText().trim())
                .recargoPorcentaje(new BigDecimal(txt_Recargo_porcentaje.getValue().toString()))
                .descuentoPorcentaje(new BigDecimal(txt_Descuento_porcentaje.getValue().toString()))
                .build();
        List<NuevoRenglonFactura> nuevosRenglones = new ArrayList<>();
        this.renglonesFactura.forEach(renglon -> {
            NuevoRenglonFactura renglonNuevo = NuevoRenglonFactura.builder()
                    .cantidad(renglon.getCantidad())
                    .idProducto(renglon.getIdProductoItem())
                    .build();
            nuevosRenglones.add(renglonNuevo);
        });
        return nuevaFacturaVenta;
    } 
    
    private void setListeners() {
        cmb_TipoComprobante.addKeyListener(keyHandler);
        tbl_Resultado.addKeyListener(keyHandler);
        txt_Descuento_porcentaje.addKeyListener(keyHandler);
        txt_Recargo_porcentaje.addKeyListener(keyHandler);
        btn_Continuar.addKeyListener(keyHandler);
        tbtn_marcarDesmarcar.addKeyListener(keyHandler);        
    }
    
    private void cargarPedidoParaFacturar() {
        try {
            this.cargarCliente(RestClient.getRestTemplate().getForObject("/cuentas-corriente/clientes/" + pedido.getCliente().getIdCliente(), CuentaCorrienteCliente.class));
            this.cargarTiposDeComprobantesDisponibles();
            this.tipoDeComprobante = (TipoDeComprobante) cmb_TipoComprobante.getSelectedItem();
            this.renglonesFactura = new LinkedList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/facturas/ventas/renglones/pedidos/" + pedido.getIdPedido()
                            + "?tipoDeComprobante=" + this.tipoDeComprobante.name(),
                            RenglonFactura[].class)));
            EstadoRenglon[] marcaDeRenglonesDelPedido = new EstadoRenglon[renglonesFactura.size()];
            for (int i = 0; i < renglonesFactura.size(); i++) {
                marcaDeRenglonesDelPedido[i] = EstadoRenglon.DESMARCADO;
            }
            this.cargarRenglonesAlTable(marcaDeRenglonesDelPedido);
            txt_Descuento_porcentaje.setValue(pedido.getDescuentoPorcentaje());
            txt_Recargo_porcentaje.setValue(pedido.getRecargoPorcentaje());
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarEstadoDeLosChkEnTabla(JTable tbl_Resultado, EstadoRenglon[] estadosDeLosRenglones) {
        for (int i = 0; i < tbl_Resultado.getRowCount(); i++) {
            if ((boolean) tbl_Resultado.getValueAt(i, 0)) {
                estadosDeLosRenglones[i] = EstadoRenglon.MARCADO;
            } else {
                estadosDeLosRenglones[i] = EstadoRenglon.DESMARCADO;
            }
        }
    }

    private boolean existeClientePredeterminado() {
        return RestClient.getRestTemplate().getForObject("/clientes/existe-predeterminado", boolean.class);
    }

    private boolean existeFormaDePagoPredeterminada() {
        FormaDePago formaDePago = RestClient.getRestTemplate()
                .getForObject("/formas-de-pago/predeterminada", FormaDePago.class);
        return (formaDePago != null);
    }

    private boolean existeTransportistaCargado() {
        if (Arrays.asList(RestClient.getRestTemplate().
                getForObject("/transportistas",
                        Transportista[].class)).isEmpty()) {
            JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_transportista_ninguno_cargado"), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            return true;
        }
    }

    private void cargarCliente(CuentaCorrienteCliente cuentaCorrienteCliente) {
        this.cliente = cuentaCorrienteCliente.getCliente();
        txtNombreCliente.setText(cliente.getNombreFiscal() + " (" + cliente.getNroCliente() + ")");
        ftxtSaldoFinal.setValue(cuentaCorrienteCliente.getSaldo().setScale(2, RoundingMode.HALF_UP));
        if (cuentaCorrienteCliente.getSaldo().setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) < 0) {
            ftxtSaldoFinal.setBackground(Color.PINK);
        } else if (cuentaCorrienteCliente.getSaldo().setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) >= 0) {
            ftxtSaldoFinal.setBackground(Color.GREEN);
        }
        ftxtCompraMinima.setValue(cliente.getMontoCompraMinima().setScale(2, RoundingMode.HALF_UP));
        txtUbicacionCliente.setText(cliente.getUbicacionFacturacion() != null ? cliente.getUbicacionFacturacion().toString() : "");
        txt_CondicionIVACliente.setText(cliente.getCategoriaIVA().toString());
        txtIdFiscalCliente.setText(cliente.getIdFiscal() != null ? cliente.getIdFiscal().toString() : "");
    }

    private void setColumnas() {
        //nombres de columnas
        String[] encabezados = new String[8];
        encabezados[0] = " ";
        encabezados[1] = "Codigo";
        encabezados[2] = "Descripcion";
        encabezados[3] = "Unidad";
        encabezados[4] = "Cantidad";
        encabezados[5] = "P. Unitario";
        encabezados[6] = "% Bonificacion";
        encabezados[7] = "Importe";
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultado.setModel(modeloTablaResultados);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = Boolean.class;
        tipos[1] = String.class;
        tipos[2] = String.class;
        tipos[3] = String.class;
        tipos[4] = BigDecimal.class;
        tipos[5] = BigDecimal.class;
        tipos[6] = BigDecimal.class;
        tipos[7] = BigDecimal.class;
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_Resultado.getTableHeader().setReorderingAllowed(false);
        tbl_Resultado.getTableHeader().setResizingAllowed(true);
        //render para los tipos de datos
        tbl_Resultado.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());
        //tamanios de columnas
        tbl_Resultado.getColumnModel().getColumn(0).setPreferredWidth(25);
        tbl_Resultado.getColumnModel().getColumn(1).setPreferredWidth(170);
        tbl_Resultado.getColumnModel().getColumn(2).setPreferredWidth(580);
        tbl_Resultado.getColumnModel().getColumn(3).setPreferredWidth(120);
        tbl_Resultado.getColumnModel().getColumn(4).setPreferredWidth(120);
        tbl_Resultado.getColumnModel().getColumn(5).setPreferredWidth(120);
        tbl_Resultado.getColumnModel().getColumn(6).setPreferredWidth(120);
        tbl_Resultado.getColumnModel().getColumn(7).setPreferredWidth(120);
    }

    private void cargarRenglonesAlTable(EstadoRenglon[] estadosDeLosRenglones) {
        modeloTablaResultados = new ModeloTabla();
        this.setColumnas();
        int i = 0;
        boolean corte;
        for (RenglonFactura renglon : renglonesFactura) {
            Object[] fila = new Object[8];
            corte = false;
            /*Dentro de este While, el case según el valor leido en el array de la enumeración,
             (modelo tabla) asigna el valor correspondiente al checkbox del renglon.*/
            while (corte == false) {
                switch (estadosDeLosRenglones[i]) {
                    case MARCADO: {
                        fila[0] = true;
                        corte = true;
                        break;
                    }
                    case DESMARCADO: {
                        fila[0] = false;
                        corte = true;
                        break;
                    }
                    /* En caso de que no sea un marcado o desmarcado, se considera que fue de un
                     renglon eliminado, entonces la estructura while continua iterando.*/
                    case ELIMINADO: {
                        i++;
                        break;
                    }
                    /* El caso por defecto, se da cuando el método es ejecutado
                     desde otras partes que no sea eliminar, ya que la colección
                     contendrá valores vacíos ''.*/
                    default: {
                        fila[0] = false;
                        corte = true;
                    }
                }
            }
            i++;
            fila[1] = renglon.getCodigoItem();
            fila[2] = renglon.getDescripcionItem();
            fila[3] = renglon.getMedidaItem();
            fila[4] = renglon.getCantidad();
            fila[5] = renglon.getPrecioUnitario();
            fila[6] = renglon.getBonificacionPorcentaje();
            fila[7] = renglon.getImporte();
            modeloTablaResultados.addRow(fila);
        }
        tbl_Resultado.setModel(modeloTablaResultados);
    }
    
    private void validarComponentesDeResultados() {
        if (txt_Descuento_porcentaje.isEditValid()) {
            try {
                txt_Descuento_porcentaje.commitEdit();
            } catch (ParseException ex) {
                String mensaje = "Se produjo un error analizando los campos.";
                LOGGER.error(mensaje + " - " + ex.getMessage());
            }
        }
         if (txt_Recargo_porcentaje.isEditValid()) {
            try {
                txt_Recargo_porcentaje.commitEdit();
            } catch (ParseException ex) {
                String mensaje = "Se produjo un error analizando los campos.";
                LOGGER.error(mensaje + " - " + ex.getMessage());
            }
        }
    }

    private void calcularResultados() {
        this.validarComponentesDeResultados();
        BigDecimal[] importe = new BigDecimal[renglonesFactura.size()];
        BigDecimal[] ivaPorcentajes = new BigDecimal[renglonesFactura.size()];
        BigDecimal[] ivaNetos = new BigDecimal[renglonesFactura.size()];
        BigDecimal[] cantidades = new BigDecimal[renglonesFactura.size()];
        int indice = 0;
        for (RenglonFactura renglon : renglonesFactura) {
            importe[indice] = renglon.getImporte();
            ivaPorcentajes[indice] = renglon.getIvaPorcentaje();
            ivaNetos[indice] = renglon.getIvaNeto();
            cantidades[indice] = renglon.getCantidad();
            indice++;
        }
        NuevosResultadosComprobante nuevosResultadosComprobante
                = NuevosResultadosComprobante.builder()
                        .importe(importe)
                        .ivaPorcentajes(ivaPorcentajes)
                        .ivaNetos(ivaNetos)
                        .cantidades(cantidades)
                        .tipoDeComprobante(tipoDeComprobante)
                        .descuentoPorcentaje(txt_Descuento_porcentaje.getValue() != null ? new BigDecimal(txt_Descuento_porcentaje.getValue().toString()) : BigDecimal.ZERO)
                        .recargoPorcentaje(txt_Recargo_porcentaje.getValue() != null ? new BigDecimal(txt_Recargo_porcentaje.getValue().toString()) : BigDecimal.ZERO)
                        .build();
        Resultados resultadosComprobante = RestClient.getRestTemplate().postForObject("/facturas/calculo-factura", nuevosResultadosComprobante, Resultados.class);
        txt_Subtotal.setValue(resultadosComprobante.getSubTotal());
        txt_Descuento_neto.setValue(resultadosComprobante.getDescuentoNeto());
        txt_Recargo_neto.setValue(resultadosComprobante.getRecargoNeto());
        txt_SubTotalBruto.setValue(resultadosComprobante.getSubTotalBruto());
        txt_IVA105_neto.setValue(resultadosComprobante.getIva105Neto());
        txt_IVA21_neto.setValue(resultadosComprobante.getIva21Neto());
        txt_Total.setValue(resultadosComprobante.getTotal());
    }

    private void cargarTiposDeComprobantesDisponibles() {
        cmb_TipoComprobante.removeAllItems();
        TipoDeComprobante[] tiposDeComprobante = new TipoDeComprobante[0];
        if (cliente != null) {
            if (rolesDeUsuario.contains(Rol.ADMINISTRADOR)
                    || rolesDeUsuario.contains(Rol.ENCARGADO)
                    || rolesDeUsuario.contains(Rol.VENDEDOR)) {
                try {
                    cmb_TipoComprobante.removeAllItems();
                    tiposDeComprobante = RestClient.getRestTemplate()
                            .getForObject("/facturas/ventas/tipos/sucursales/" + SucursalActiva.getInstance().getSucursal().getIdSucursal()
                                    + "/clientes/" + cliente.getIdCliente(), TipoDeComprobante[].class);
                } catch (RestClientResponseException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (ResourceAccessException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                for (int i = 0; tiposDeComprobante.length > i; i++) {
                    cmb_TipoComprobante.addItem(tiposDeComprobante[i]);                    
                }
            }
        }
    }

    private void recargarRenglones() {
        try {
            List<NuevoRenglonFactura> nuevosRenglones = new ArrayList<>();
            this.renglonesFactura.forEach(renglon -> {
                NuevoRenglonFactura nuevoRenglon = NuevoRenglonFactura.builder()
                        .cantidad(renglon.getCantidad())
                        .idProducto(renglon.getIdProductoItem())
                        .build();
                nuevosRenglones.add(nuevoRenglon);
            });
            this.renglonesFactura = new LinkedList(Arrays.asList(RestClient.getRestTemplate().
                    postForObject("/facturas/ventas/renglones?tipoDeComprobante=" + this.tipoDeComprobante.name(), nuevosRenglones, RenglonFactura[].class)));
            EstadoRenglon[] estadosRenglones = new EstadoRenglon[renglonesFactura.size()];
            if (!renglonesFactura.isEmpty()) {
                if (tbl_Resultado.getRowCount() == 0) {
                    estadosRenglones[0] = EstadoRenglon.DESMARCADO;
                } else {
                    this.cargarEstadoDeLosChkEnTabla(tbl_Resultado, estadosRenglones);
                    if (tbl_Resultado.getRowCount() > renglonesFactura.size()) {
                        estadosRenglones[tbl_Resultado.getRowCount()] = EstadoRenglon.DESMARCADO;
                    }
                }
            }
            this.cargarRenglonesAlTable(estadosRenglones);
            this.calcularResultados();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Clase interna para manejar las hotkeys del TPV     
    class HotKeysHandler extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent evt) {
            if (evt.getSource() == tbl_Resultado && evt.getKeyCode() == KeyEvent.VK_TAB) {
                txt_Descuento_porcentaje.requestFocus();
            }
            if (evt.getKeyCode() == KeyEvent.VK_F9) {
                btn_ContinuarActionPerformed(null);
            }
        }
    };

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelGeneral = new javax.swing.JPanel();
        panelRenglones = new javax.swing.JPanel();
        sp_Resultado = new javax.swing.JScrollPane();
        tbl_Resultado = new javax.swing.JTable();
        panelObservaciones = new javax.swing.JPanel();
        lbl_Observaciones = new javax.swing.JLabel();
        btn_AddComment = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_Observaciones = new javax.swing.JTextArea();
        panelResultados = new javax.swing.JPanel();
        lbl_SubTotal = new javax.swing.JLabel();
        txt_Subtotal = new javax.swing.JFormattedTextField();
        lbl_IVA21 = new javax.swing.JLabel();
        txt_IVA21_neto = new javax.swing.JFormattedTextField();
        lbl_Total = new javax.swing.JLabel();
        txt_Total = new javax.swing.JFormattedTextField();
        txt_Descuento_porcentaje = new javax.swing.JFormattedTextField();
        txt_Descuento_neto = new javax.swing.JFormattedTextField();
        lbl_DescuentoRecargo = new javax.swing.JLabel();
        txt_SubTotalBruto = new javax.swing.JFormattedTextField();
        lbl_SubTotalBruto = new javax.swing.JLabel();
        txt_IVA105_neto = new javax.swing.JFormattedTextField();
        lbl_IVA105 = new javax.swing.JLabel();
        lbl_105 = new javax.swing.JLabel();
        lbl_21 = new javax.swing.JLabel();
        lbl_recargoPorcentaje = new javax.swing.JLabel();
        txt_Recargo_neto = new javax.swing.JFormattedTextField();
        txt_Recargo_porcentaje = new javax.swing.JFormattedTextField();
        btn_Continuar = new javax.swing.JButton();
        panelEncabezado = new javax.swing.JPanel();
        lbl_TipoDeComprobante = new javax.swing.JLabel();
        cmb_TipoComprobante = new javax.swing.JComboBox();
        lblSeparadorIzquierdo = new javax.swing.JLabel();
        lblSeparadorDerecho = new javax.swing.JLabel();
        panelCliente = new javax.swing.JPanel();
        lblNombreCliente = new javax.swing.JLabel();
        lblUbicacionCliente = new javax.swing.JLabel();
        lbl_IDFiscalCliente = new javax.swing.JLabel();
        lbl_CondicionIVACliente = new javax.swing.JLabel();
        txt_CondicionIVACliente = new javax.swing.JTextField();
        txtUbicacionCliente = new javax.swing.JTextField();
        txtNombreCliente = new javax.swing.JTextField();
        txtIdFiscalCliente = new javax.swing.JTextField();
        lblSaldoCC = new javax.swing.JLabel();
        ftxtSaldoFinal = new javax.swing.JFormattedTextField();
        lblMontoCompraMinima = new javax.swing.JLabel();
        ftxtCompraMinima = new javax.swing.JFormattedTextField();
        tbtn_marcarDesmarcar = new javax.swing.JToggleButton();

        setResizable(true);
        setTitle("Nueva Factura de Venta");
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

        panelGeneral.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tbl_Resultado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resultado.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbl_Resultado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbl_ResultadoFocusGained(evt);
            }
        });
        tbl_Resultado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_ResultadoMouseClicked(evt);
            }
        });
        sp_Resultado.setViewportView(tbl_Resultado);

        javax.swing.GroupLayout panelRenglonesLayout = new javax.swing.GroupLayout(panelRenglones);
        panelRenglones.setLayout(panelRenglonesLayout);
        panelRenglonesLayout.setHorizontalGroup(
            panelRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_Resultado)
        );
        panelRenglonesLayout.setVerticalGroup(
            panelRenglonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_Resultado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
        );

        lbl_Observaciones.setText("Observaciones:");

        btn_AddComment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Comment_16x16.png"))); // NOI18N
        btn_AddComment.setFocusable(false);
        btn_AddComment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AddCommentActionPerformed(evt);
            }
        });

        txt_Observaciones.setEditable(false);
        txt_Observaciones.setBackground(new java.awt.Color(220, 215, 215));
        txt_Observaciones.setColumns(20);
        txt_Observaciones.setRows(5);
        txt_Observaciones.setFocusable(false);
        jScrollPane1.setViewportView(txt_Observaciones);

        javax.swing.GroupLayout panelObservacionesLayout = new javax.swing.GroupLayout(panelObservaciones);
        panelObservaciones.setLayout(panelObservacionesLayout);
        panelObservacionesLayout.setHorizontalGroup(
            panelObservacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelObservacionesLayout.createSequentialGroup()
                .addGroup(panelObservacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelObservacionesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lbl_Observaciones))
                    .addGroup(panelObservacionesLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 469, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_AddComment)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelObservacionesLayout.setVerticalGroup(
            panelObservacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelObservacionesLayout.createSequentialGroup()
                .addComponent(lbl_Observaciones)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelObservacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_AddComment)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lbl_SubTotal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_SubTotal.setText("SubTotal");

        txt_Subtotal.setEditable(false);
        txt_Subtotal.setForeground(new java.awt.Color(29, 156, 37));
        txt_Subtotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_Subtotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Subtotal.setText("0");
        txt_Subtotal.setFocusable(false);
        txt_Subtotal.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N

        lbl_IVA21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_IVA21.setText("I.V.A.");

        txt_IVA21_neto.setEditable(false);
        txt_IVA21_neto.setForeground(new java.awt.Color(29, 156, 37));
        txt_IVA21_neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_IVA21_neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_IVA21_neto.setText("0");
        txt_IVA21_neto.setFocusable(false);
        txt_IVA21_neto.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N

        lbl_Total.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_Total.setText("TOTAL");

        txt_Total.setEditable(false);
        txt_Total.setForeground(new java.awt.Color(29, 156, 37));
        txt_Total.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_Total.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Total.setText("0");
        txt_Total.setFocusable(false);
        txt_Total.setFont(new java.awt.Font("DejaVu Sans", 1, 36)); // NOI18N

        txt_Descuento_porcentaje.setForeground(new java.awt.Color(29, 156, 37));
        txt_Descuento_porcentaje.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_Descuento_porcentaje.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Descuento_porcentaje.setText("0");
        txt_Descuento_porcentaje.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N
        txt_Descuento_porcentaje.setNextFocusableComponent(txt_Recargo_porcentaje);
        txt_Descuento_porcentaje.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_Descuento_porcentajeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_Descuento_porcentajeFocusLost(evt);
            }
        });
        txt_Descuento_porcentaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_Descuento_porcentajeActionPerformed(evt);
            }
        });
        txt_Descuento_porcentaje.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_Descuento_porcentajeKeyTyped(evt);
            }
        });

        txt_Descuento_neto.setEditable(false);
        txt_Descuento_neto.setForeground(new java.awt.Color(29, 156, 37));
        txt_Descuento_neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_Descuento_neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Descuento_neto.setText("0");
        txt_Descuento_neto.setFocusable(false);
        txt_Descuento_neto.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N

        lbl_DescuentoRecargo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_DescuentoRecargo.setText("Descuento (%)");

        txt_SubTotalBruto.setEditable(false);
        txt_SubTotalBruto.setForeground(new java.awt.Color(29, 156, 37));
        txt_SubTotalBruto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_SubTotalBruto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_SubTotalBruto.setText("0");
        txt_SubTotalBruto.setFocusable(false);
        txt_SubTotalBruto.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N

        lbl_SubTotalBruto.setText("SubTotal Bruto");

        txt_IVA105_neto.setEditable(false);
        txt_IVA105_neto.setForeground(new java.awt.Color(29, 156, 37));
        txt_IVA105_neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_IVA105_neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_IVA105_neto.setText("0");
        txt_IVA105_neto.setFocusable(false);
        txt_IVA105_neto.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N

        lbl_IVA105.setText("I.V.A.");

        lbl_105.setText("10.5 %");

        lbl_21.setText("21 %");

        lbl_recargoPorcentaje.setText("Recargo (%)");

        txt_Recargo_neto.setEditable(false);
        txt_Recargo_neto.setForeground(new java.awt.Color(29, 156, 37));
        txt_Recargo_neto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_Recargo_neto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Recargo_neto.setText("0");
        txt_Recargo_neto.setFocusable(false);
        txt_Recargo_neto.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N

        txt_Recargo_porcentaje.setForeground(new java.awt.Color(29, 156, 37));
        txt_Recargo_porcentaje.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_Recargo_porcentaje.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Recargo_porcentaje.setText("0");
        txt_Recargo_porcentaje.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N
        txt_Recargo_porcentaje.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_Recargo_porcentajeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_Recargo_porcentajeFocusLost(evt);
            }
        });
        txt_Recargo_porcentaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_Recargo_porcentajeActionPerformed(evt);
            }
        });
        txt_Recargo_porcentaje.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_Recargo_porcentajeKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_Subtotal)
                    .addComponent(lbl_SubTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_Descuento_porcentaje, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(txt_Descuento_neto)
                    .addComponent(lbl_DescuentoRecargo, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_Recargo_neto)
                    .addComponent(txt_Recargo_porcentaje)
                    .addComponent(lbl_recargoPorcentaje, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_SubTotalBruto)
                    .addComponent(lbl_SubTotalBruto, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_IVA105_neto)
                    .addComponent(lbl_105, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(lbl_IVA105, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_IVA21_neto)
                    .addComponent(lbl_21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_IVA21, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_Total)
                    .addComponent(lbl_Total, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_SubTotal)
                    .addComponent(lbl_DescuentoRecargo)
                    .addComponent(lbl_recargoPorcentaje)
                    .addComponent(lbl_SubTotalBruto)
                    .addComponent(lbl_IVA105)
                    .addComponent(lbl_IVA21)
                    .addComponent(lbl_Total, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelResultadosLayout.createSequentialGroup()
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txt_Descuento_porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_Recargo_porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_105, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_21, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txt_Subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_Descuento_neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_Recargo_neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_SubTotalBruto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_IVA105_neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_IVA21_neto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txt_Total, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lbl_105, lbl_21, txt_Descuento_neto, txt_Descuento_porcentaje, txt_IVA105_neto, txt_IVA21_neto, txt_Recargo_neto, txt_Recargo_porcentaje, txt_SubTotalBruto, txt_Subtotal});

        btn_Continuar.setForeground(java.awt.Color.blue);
        btn_Continuar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/22x22_FlechaGO.png"))); // NOI18N
        btn_Continuar.setText("Continuar (F9)");
        btn_Continuar.setFocusable(false);
        btn_Continuar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ContinuarActionPerformed(evt);
            }
        });

        lbl_TipoDeComprobante.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_TipoDeComprobante.setText("Tipo de Comprobante:");

        cmb_TipoComprobante.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_TipoComprobanteItemStateChanged(evt);
            }
        });
        cmb_TipoComprobante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_TipoComprobanteActionPerformed(evt);
            }
        });

        lblSeparadorDerecho.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        lblNombreCliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNombreCliente.setText("Nombre:");

        lblUbicacionCliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblUbicacionCliente.setText("Ubicación:");

        lbl_IDFiscalCliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_IDFiscalCliente.setText("CUIT o DNI:");

        lbl_CondicionIVACliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_CondicionIVACliente.setText("Categoria IVA:");

        txt_CondicionIVACliente.setEditable(false);
        txt_CondicionIVACliente.setFocusable(false);

        txtUbicacionCliente.setEditable(false);
        txtUbicacionCliente.setFocusable(false);

        txtNombreCliente.setEditable(false);
        txtNombreCliente.setFocusable(false);

        txtIdFiscalCliente.setEditable(false);
        txtIdFiscalCliente.setFocusable(false);

        lblSaldoCC.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblSaldoCC.setText("Saldo CC $:");

        ftxtSaldoFinal.setEditable(false);
        ftxtSaldoFinal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxtSaldoFinal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ftxtSaldoFinal.setFocusable(false);
        ftxtSaldoFinal.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        lblMontoCompraMinima.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMontoCompraMinima.setText("Compra Mín. $:");

        ftxtCompraMinima.setEditable(false);
        ftxtCompraMinima.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxtCompraMinima.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ftxtCompraMinima.setFocusable(false);
        ftxtCompraMinima.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        javax.swing.GroupLayout panelClienteLayout = new javax.swing.GroupLayout(panelCliente);
        panelCliente.setLayout(panelClienteLayout);
        panelClienteLayout.setHorizontalGroup(
            panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClienteLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblNombreCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblUbicacionCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_CondicionIVACliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_CondicionIVACliente)
                    .addComponent(txtUbicacionCliente)
                    .addComponent(txtNombreCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelClienteLayout.createSequentialGroup()
                        .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblSaldoCC)
                            .addComponent(lbl_IDFiscalCliente))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtIdFiscalCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ftxtSaldoFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelClienteLayout.createSequentialGroup()
                        .addComponent(lblMontoCompraMinima)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxtCompraMinima))))
        );

        panelClienteLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblMontoCompraMinima, lblSaldoCC, lbl_IDFiscalCliente});

        panelClienteLayout.setVerticalGroup(
            panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClienteLayout.createSequentialGroup()
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNombreCliente)
                    .addComponent(lbl_IDFiscalCliente)
                    .addComponent(txtIdFiscalCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblUbicacionCliente)
                    .addComponent(txtUbicacionCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSaldoCC)
                    .addComponent(ftxtSaldoFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_CondicionIVACliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_CondicionIVACliente)
                    .addComponent(lblMontoCompraMinima)
                    .addComponent(ftxtCompraMinima, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        panelClienteLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtIdFiscalCliente, txtNombreCliente, txtUbicacionCliente, txt_CondicionIVACliente});

        panelClienteLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblMontoCompraMinima, lbl_IDFiscalCliente});

        javax.swing.GroupLayout panelEncabezadoLayout = new javax.swing.GroupLayout(panelEncabezado);
        panelEncabezado.setLayout(panelEncabezadoLayout);
        panelEncabezadoLayout.setHorizontalGroup(
            panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEncabezadoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelEncabezadoLayout.createSequentialGroup()
                        .addComponent(lblSeparadorIzquierdo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_TipoDeComprobante)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmb_TipoComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSeparadorDerecho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(panelCliente, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelEncabezadoLayout.setVerticalGroup(
            panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEncabezadoLayout.createSequentialGroup()
                .addGroup(panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelEncabezadoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lbl_TipoDeComprobante)
                            .addComponent(cmb_TipoComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelEncabezadoLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(panelEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSeparadorDerecho, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSeparadorIzquierdo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tbtn_marcarDesmarcar.setFocusable(false);
        tbtn_marcarDesmarcar.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tbtn_marcarDesmarcarStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panelGeneralLayout = new javax.swing.GroupLayout(panelGeneral);
        panelGeneral.setLayout(panelGeneralLayout);
        panelGeneralLayout.setHorizontalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelRenglones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelEncabezado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addComponent(panelObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_Continuar))
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelResultados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbtn_marcarDesmarcar, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelGeneralLayout.setVerticalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addComponent(panelEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbtn_marcarDesmarcar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelRenglones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Continuar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmb_TipoComprobanteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_TipoComprobanteItemStateChanged
        //para evitar que pase null cuando esta recargando el comboBox
        if (cmb_TipoComprobante.getSelectedItem() != null) {            
            this.tipoDeComprobante = (TipoDeComprobante) cmb_TipoComprobante.getSelectedItem();
            this.recargarRenglones();
        }
    }//GEN-LAST:event_cmb_TipoComprobanteItemStateChanged

    private void btn_AddCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AddCommentActionPerformed
        ObservacionesGUI observacionesGUI = new ObservacionesGUI(txt_Observaciones.getText());
        observacionesGUI.setModal(true);
        observacionesGUI.setLocationRelativeTo(this);
        observacionesGUI.setVisible(true);
        txt_Observaciones.setText(observacionesGUI.getTxta_Observaciones().getText());
    }//GEN-LAST:event_btn_AddCommentActionPerformed

    private void txt_Descuento_porcentajeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Descuento_porcentajeFocusLost
        this.calcularResultados();
    }//GEN-LAST:event_txt_Descuento_porcentajeFocusLost

    private void txt_Descuento_porcentajeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Descuento_porcentajeFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_Descuento_porcentaje.selectAll();
        });
    }//GEN-LAST:event_txt_Descuento_porcentajeFocusGained

    private void txt_Descuento_porcentajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_Descuento_porcentajeActionPerformed
        this.calcularResultados();
    }//GEN-LAST:event_txt_Descuento_porcentajeActionPerformed

    private void btn_ContinuarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ContinuarActionPerformed
        if (cliente != null) {
            if (renglonesFactura.isEmpty()) {
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_factura_sin_renglones"), "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                if (new BigDecimal(txt_Descuento_porcentaje.getValue().toString()).compareTo(CIEN) > 0) {
                    JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                            .getString("mensaje_factura_descuento_mayor_cien"), "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    this.calcularResultados();
                    try {
                        cliente = RestClient.getRestTemplate().getForObject("/clientes/" + this.cliente.getIdCliente(), Cliente.class);
                        List<NuevoRenglonFactura> nuevosRenglones = new ArrayList<>();
                        this.renglonesFactura.forEach(renglon -> {
                            NuevoRenglonFactura nuevoRenglon = NuevoRenglonFactura.builder()
                                    .cantidad(renglon.getCantidad())
                                    .idProducto(renglon.getIdProductoItem())
                                    .build();
                            nuevosRenglones.add(nuevoRenglon);
                        });
                        CerrarOperacionGUI cerrarOperacionGUI = new CerrarOperacionGUI(this.construirNuevaFactura(), this.pedido, new BigDecimal(txt_Total.getValue().toString()), this.modeloTablaResultados);
                        cerrarOperacionGUI.setLocationRelativeTo(this);
                        cerrarOperacionGUI.setVisible(true);
                        if (cerrarOperacionGUI.isExito()) {
                            this.dispose();
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
        } else {
            JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_seleccionar_cliente"),
                    "Aviso", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_ContinuarActionPerformed

    private void tbl_ResultadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbl_ResultadoFocusGained
        //Si no hay nada seleccionado y NO esta vacio el table, selecciona la primer fila
        if ((tbl_Resultado.getSelectedRow() == -1) && (tbl_Resultado.getRowCount() != 0)) {
            tbl_Resultado.setRowSelectionInterval(0, 0);
        }
    }//GEN-LAST:event_tbl_ResultadoFocusGained

    private void txt_Descuento_porcentajeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_Descuento_porcentajeKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_Descuento_porcentajeKeyTyped

    private void cmb_TipoComprobanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_TipoComprobanteActionPerformed
        for (int i = 0; i < tbl_Resultado.getRowCount(); i++) {
            tbl_Resultado.setValueAt((boolean) tbl_Resultado.getValueAt(i, 0), i, 0);
        }
    }//GEN-LAST:event_cmb_TipoComprobanteActionPerformed

    private void tbl_ResultadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_ResultadoMouseClicked
        int fila = tbl_Resultado.getSelectedRow();
        int columna = tbl_Resultado.getSelectedColumn();
        if (columna == 0) {
            tbl_Resultado.setValueAt(!(boolean) tbl_Resultado.getValueAt(fila, columna), fila, columna);
        }
    }//GEN-LAST:event_tbl_ResultadoMouseClicked

    private void tbtn_marcarDesmarcarStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tbtn_marcarDesmarcarStateChanged
        int cantidadDeFilas = tbl_Resultado.getRowCount();
        if (this.tbtn_marcarDesmarcar.isSelected()) {
            ImageIcon iconoMarcado = new ImageIcon(getClass().getResource("/sic/icons/chkMarca_16x16.png"));
            this.tbtn_marcarDesmarcar.setIcon(iconoMarcado);
            for (int i = 0; i < cantidadDeFilas; i++) {
                tbl_Resultado.setValueAt(true, i, 0);
            }
        } else {
            ImageIcon iconoNoMarcado = new ImageIcon(getClass().getResource("/sic/icons/chkNoMarcado_16x16.png"));
            this.tbtn_marcarDesmarcar.setIcon(iconoNoMarcado);
            for (int i = 0; i < cantidadDeFilas; i++) {
                tbl_Resultado.setValueAt(false, i, 0);
            }
        }
    }//GEN-LAST:event_tbtn_marcarDesmarcarStateChanged

    private void txt_Recargo_porcentajeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Recargo_porcentajeFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_Recargo_porcentaje.selectAll();
        });
    }//GEN-LAST:event_txt_Recargo_porcentajeFocusGained

    private void txt_Recargo_porcentajeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_Recargo_porcentajeFocusLost
        this.calcularResultados();
    }//GEN-LAST:event_txt_Recargo_porcentajeFocusLost

    private void txt_Recargo_porcentajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_Recargo_porcentajeActionPerformed
        this.calcularResultados();
    }//GEN-LAST:event_txt_Recargo_porcentajeActionPerformed

    private void txt_Recargo_porcentajeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_Recargo_porcentajeKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_Recargo_porcentajeKeyTyped

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            this.setSize(sizeInternalFrame);
            this.setColumnas();
            this.setMaximum(true);
            if (!this.existeFormaDePagoPredeterminada() || !this.existeTransportistaCargado()) {
                this.dispose();
            }
            this.cargarTiposDeComprobantesDisponibles();
            if (this.pedido != null && this.pedido.getIdPedido() != 0) {
                this.cargarPedidoParaFacturar();
                this.calcularResultados();
            } else {
                txt_Descuento_porcentaje.setValue(BigDecimal.ZERO);
                txt_Recargo_porcentaje.setValue(BigDecimal.ZERO);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_AddComment;
    private javax.swing.JButton btn_Continuar;
    private javax.swing.JComboBox cmb_TipoComprobante;
    private javax.swing.JFormattedTextField ftxtCompraMinima;
    private javax.swing.JFormattedTextField ftxtSaldoFinal;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMontoCompraMinima;
    private javax.swing.JLabel lblNombreCliente;
    private javax.swing.JLabel lblSaldoCC;
    private javax.swing.JLabel lblSeparadorDerecho;
    private javax.swing.JLabel lblSeparadorIzquierdo;
    private javax.swing.JLabel lblUbicacionCliente;
    private javax.swing.JLabel lbl_105;
    private javax.swing.JLabel lbl_21;
    private javax.swing.JLabel lbl_CondicionIVACliente;
    private javax.swing.JLabel lbl_DescuentoRecargo;
    private javax.swing.JLabel lbl_IDFiscalCliente;
    private javax.swing.JLabel lbl_IVA105;
    private javax.swing.JLabel lbl_IVA21;
    private javax.swing.JLabel lbl_Observaciones;
    private javax.swing.JLabel lbl_SubTotal;
    private javax.swing.JLabel lbl_SubTotalBruto;
    private javax.swing.JLabel lbl_TipoDeComprobante;
    private javax.swing.JLabel lbl_Total;
    private javax.swing.JLabel lbl_recargoPorcentaje;
    private javax.swing.JPanel panelCliente;
    private javax.swing.JPanel panelEncabezado;
    private javax.swing.JPanel panelGeneral;
    private javax.swing.JPanel panelObservaciones;
    private javax.swing.JPanel panelRenglones;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JScrollPane sp_Resultado;
    private javax.swing.JTable tbl_Resultado;
    private javax.swing.JToggleButton tbtn_marcarDesmarcar;
    private javax.swing.JTextField txtIdFiscalCliente;
    private javax.swing.JTextField txtNombreCliente;
    private javax.swing.JTextField txtUbicacionCliente;
    private javax.swing.JTextField txt_CondicionIVACliente;
    private javax.swing.JFormattedTextField txt_Descuento_neto;
    private javax.swing.JFormattedTextField txt_Descuento_porcentaje;
    private javax.swing.JFormattedTextField txt_IVA105_neto;
    private javax.swing.JFormattedTextField txt_IVA21_neto;
    private javax.swing.JTextArea txt_Observaciones;
    private javax.swing.JFormattedTextField txt_Recargo_neto;
    private javax.swing.JFormattedTextField txt_Recargo_porcentaje;
    private javax.swing.JFormattedTextField txt_SubTotalBruto;
    private javax.swing.JFormattedTextField txt_Subtotal;
    private javax.swing.JFormattedTextField txt_Total;
    // End of variables declaration//GEN-END:variables
}
