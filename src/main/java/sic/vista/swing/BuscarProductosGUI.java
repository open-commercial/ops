package sic.vista.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.EmpresaActiva;
import sic.modelo.Movimiento;
import sic.modelo.Producto;
import sic.modelo.RenglonFactura;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.TipoDeComprobante;
import sic.util.DecimalesRenderer;
import sic.util.Utilidades;

public class BuscarProductosGUI extends JDialog {

    private final TipoDeComprobante tipoDeComprobante;
    private ModeloTabla modeloTablaResultados = new ModeloTabla();
    private List<Producto> productosTotal = new ArrayList<>();
    private List<Producto> productosParcial = new ArrayList<>();
    private final List<RenglonFactura> renglones;
    private Producto productoSeleccionado;
    private RenglonFactura renglon;
    private boolean debeCargarRenglon;    
    private final Movimiento movimiento;
    private final HotKeysHandler keyHandler = new HotKeysHandler();
    private int NUMERO_PAGINA = 0;    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeDialog = new Dimension(1000, 600);
    
    public BuscarProductosGUI(List<RenglonFactura> renglones, TipoDeComprobante tipoDeComprobante, Movimiento movimiento) {
        this.initComponents();
        this.setIcon();
        this.renglones = renglones;
        this.movimiento = movimiento;
        this.tipoDeComprobante = tipoDeComprobante;
        this.setColumnas();
        txtCriteriaBusqueda.addKeyListener(keyHandler);
        btnBuscar.addKeyListener(keyHandler);
        tbl_Resultados.addKeyListener(keyHandler);
        txtaNotaProducto.addKeyListener(keyHandler);
        txtCantidad.addKeyListener(keyHandler);        
        txtPorcentajeDescuento.addKeyListener(keyHandler);
        btnAceptar.addKeyListener(keyHandler);
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

    public boolean debeCargarRenglon() {
        return debeCargarRenglon;
    }

    public RenglonFactura getRenglon() {
        return renglon;
    }
    
    public Producto getProductoSeleccionado(){
        return this.productoSeleccionado;
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(BuscarProductosGUI.class.getResource("/sic/icons/Product_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }
    
    private void prepararComponentes() {
        txtCantidad.setValue(1.00);
        txtPorcentajeDescuento.setValue(0.0);
        if (renglones == null && movimiento == null && tipoDeComprobante == null) {
            lbl_Cantidad.setVisible(false);
            lbl_Descuento.setVisible(false);
            txtCantidad.setVisible(false);
            txtPorcentajeDescuento.setVisible(false);
        }
    }

    private void buscar() {
        try {
            if (txtCriteriaBusqueda.getText().equals("")) {
                this.resetScroll();
                this.limpiarJTable();
            } else {
                String uri = "descripcion=" + txtCriteriaBusqueda.getText().trim()
                        + "&codigo=" + txtCriteriaBusqueda.getText().trim()
                        + "&idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                        + "&pagina=" + NUMERO_PAGINA;
                PaginaRespuestaRest<Producto> response = RestClient.getRestTemplate()
                        .exchange("/productos/busqueda/criteria?" + uri, HttpMethod.GET, null,
                                new ParameterizedTypeReference<PaginaRespuestaRest<Producto>>() {
                        })
                        .getBody();
                productosParcial = response.getContent();
                productosTotal.addAll(productosParcial);
                productoSeleccionado = null;       
                if (renglones != null) {
                    this.restarCantidadesSegunProductosYaCargados();
                }
                this.cargarResultadosAlTable();
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }
    
    private void aceptarProducto() {
        boolean esValido = true;
        this.actualizarEstadoSeleccion();
        if (productoSeleccionado == null || (renglones == null && movimiento == null && tipoDeComprobante == null)) {
            debeCargarRenglon = false;
            this.dispose();
        } else {            
            if (movimiento == Movimiento.VENTA) {
                String uri = "/productos/disponibilidad-stock?"
                        + "idProducto=" + productoSeleccionado.getIdProducto()
                        + "&cantidad=" + this.sumarCantidadesSegunProductosYaCargados();
                boolean existeStockSuficiente = RestClient.getRestTemplate()
                        .exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<Map<Long, BigDecimal>>() {})
                        .getBody().isEmpty();
                if (!existeStockSuficiente) {
                    JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                            .getString("mensaje_producto_sin_stock_suficiente"), "Error", JOptionPane.ERROR_MESSAGE);
                    esValido = false;
                }
            }
            if (esValido) {
                try {
                    renglon = RestClient.getRestTemplate().getForObject("/facturas/renglon?"
                            + "idProducto=" + productoSeleccionado.getIdProducto()
                            + "&tipoDeComprobante=" + this.tipoDeComprobante.name()
                            + "&movimiento=" + movimiento
                            + "&cantidad=" + txtCantidad.getValue().toString()
                            + "&descuentoPorcentaje=" + txtPorcentajeDescuento.getValue().toString(),
                            RenglonFactura.class);
                    debeCargarRenglon = true;
                    this.dispose();
                } catch (RestClientResponseException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (ResourceAccessException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }            
        }
    }
    
    private BigDecimal sumarCantidadesSegunProductosYaCargados() {
        BigDecimal cantidad = new BigDecimal(txtCantidad.getValue().toString());
        for (RenglonFactura r : renglones) {
            if (r.getIdProductoItem() == productoSeleccionado.getIdProducto()) {
                cantidad = cantidad.add(r.getCantidad());
            }
        }
        return cantidad;
    }
    
    private void restarCantidadesSegunProductosYaCargados() {
        if (!(movimiento == Movimiento.PEDIDO || movimiento == Movimiento.COMPRA)) {
            renglones.forEach((r) -> {
                productosTotal.stream().filter((p) -> (r.getDescripcionItem().equals(p.getDescripcion()) && p.isIlimitado() == false))
                        .forEachOrdered((p) -> {
                            p.setCantidad(p.getCantidad().subtract(r.getCantidad()));
                        });
            });
        }
    }

    private void actualizarEstadoSeleccion() {
        if (txtCantidad.isEditValid() && txtPorcentajeDescuento.isEditValid()) {
            try {
                txtCantidad.commitEdit();
                txtPorcentajeDescuento.commitEdit();
            } catch (ParseException ex) {
                String msjError = "Se produjo un error analizando los campos.";
                LOGGER.error(msjError + " - " + ex.getMessage());
            }
        }
    }

    private void seleccionarProductoEnTable() {
        int fila = tbl_Resultados.getSelectedRow();
        if (fila != -1) {
            productoSeleccionado = productosTotal.get(fila);                                                
            txtaNotaProducto.setText(productoSeleccionado.getNota());
            this.actualizarEstadoSeleccion();
        }
    }

    private void cargarResultadosAlTable() {
        productosParcial.stream().map(p -> {
            Object[] fila = new Object[6];
            fila[0] = p.getCodigo();
            fila[1] = p.getDescripcion();
            fila[2] = p.getCantidad();
            fila[3] = p.getBulto();
            fila[4] = p.getNombreMedida();
            BigDecimal precio = (movimiento == Movimiento.VENTA) ? p.getPrecioLista()
                    : (movimiento == Movimiento.PEDIDO) ? p.getPrecioLista()
                    : (movimiento == Movimiento.COMPRA) ? p.getPrecioCosto() : BigDecimal.ZERO;
            fila[5] = precio;
            return fila;
        }).forEach(fila -> {
            modeloTablaResultados.addRow(fila);
        });
        tbl_Resultados.setModel(modeloTablaResultados);
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

    private void setColumnas() {        
        String[] encabezados = new String[6];
        encabezados[0] = "Codigo";
        encabezados[1] = "Descripción";
        encabezados[2] = "Cant. Disponible";
        encabezados[3] = "Cant. por Bulto";
        encabezados[4] = "Unidad";
        String encabezadoPrecio = (movimiento == Movimiento.VENTA) ? "P. Lista"
                : (movimiento == Movimiento.PEDIDO) ? "P. Lista"
                : (movimiento == Movimiento.COMPRA) ? "P.Costo" : "";
        encabezados[5] = encabezadoPrecio;
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaResultados);        
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        tipos[2] = BigDecimal.class;        
        tipos[3] = BigDecimal.class;        
        tipos[4] = String.class;
        tipos[5] = BigDecimal.class;
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);        
        tbl_Resultados.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());        
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(130);
        tbl_Resultados.getColumnModel().getColumn(0).setMaxWidth(130);        
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(380);        
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(110);
        tbl_Resultados.getColumnModel().getColumn(2).setMaxWidth(110);                
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(110);
        tbl_Resultados.getColumnModel().getColumn(3).setMaxWidth(110);                
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(70);
        tbl_Resultados.getColumnModel().getColumn(4).setMaxWidth(70);        
        tbl_Resultados.getColumnModel().getColumn(5).setPreferredWidth(80);
        tbl_Resultados.getColumnModel().getColumn(5).setMaxWidth(80);
    }

    /**
     * Clase interna para manejar las hotkeys
     */
    class HotKeysHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                dispose();
            }
        }
    };

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFondo = new javax.swing.JPanel();
        txtCriteriaBusqueda = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btnAceptar = new javax.swing.JButton();
        lbl_Cantidad = new javax.swing.JLabel();
        lbl_Descuento = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JFormattedTextField();
        txtPorcentajeDescuento = new javax.swing.JFormattedTextField();
        spNotaProducto = new javax.swing.JScrollPane();
        txtaNotaProducto = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        txtCriteriaBusqueda.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N
        txtCriteriaBusqueda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCriteriaBusquedaActionPerformed(evt);
            }
        });
        txtCriteriaBusqueda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCriteriaBusquedaKeyTyped(evt);
            }
        });

        btnBuscar.setForeground(java.awt.Color.blue);
        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/22x22_LupaBuscar.png"))); // NOI18N
        btnBuscar.setFocusable(false);
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        tbl_Resultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Resultados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbl_Resultados.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl_Resultados.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbl_ResultadosFocusGained(evt);
            }
        });
        tbl_Resultados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_ResultadosMouseClicked(evt);
            }
        });
        tbl_Resultados.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbl_ResultadosKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbl_ResultadosKeyReleased(evt);
            }
        });
        sp_Resultados.setViewportView(tbl_Resultados);

        btnAceptar.setForeground(java.awt.Color.blue);
        btnAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/22x22_FlechaGO.png"))); // NOI18N
        btnAceptar.setFocusable(false);
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        lbl_Cantidad.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Cantidad.setText("Cantidad:");

        lbl_Descuento.setText("Descuento (%):");

        txtCantidad.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtCantidad.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N
        txtCantidad.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCantidadFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCantidadFocusLost(evt);
            }
        });
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCantidadKeyPressed(evt);
            }
        });

        txtPorcentajeDescuento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txtPorcentajeDescuento.setText("0");
        txtPorcentajeDescuento.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N
        txtPorcentajeDescuento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPorcentajeDescuentoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPorcentajeDescuentoFocusLost(evt);
            }
        });
        txtPorcentajeDescuento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPorcentajeDescuentoKeyTyped(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPorcentajeDescuentoKeyReleased(evt);
            }
        });

        txtaNotaProducto.setEditable(false);
        txtaNotaProducto.setColumns(20);
        txtaNotaProducto.setLineWrap(true);
        txtaNotaProducto.setRows(5);
        txtaNotaProducto.setFocusable(false);
        spNotaProducto.setViewportView(txtaNotaProducto);

        javax.swing.GroupLayout panelFondoLayout = new javax.swing.GroupLayout(panelFondo);
        panelFondo.setLayout(panelFondoLayout);
        panelFondoLayout.setHorizontalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addComponent(txtCriteriaBusqueda)
                        .addGap(0, 0, 0)
                        .addComponent(btnBuscar))
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addComponent(spNotaProducto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbl_Descuento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_Cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtPorcentajeDescuento)
                            .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelFondoLayout.setVerticalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBuscar)
                    .addComponent(txtCriteriaBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_Resultados)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spNotaProducto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(panelFondoLayout.createSequentialGroup()
                            .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(lbl_Cantidad)
                                .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(lbl_Descuento)
                                .addComponent(txtPorcentajeDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        panelFondoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscar, txtCriteriaBusqueda});

        panelFondoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtCantidad, txtPorcentajeDescuento});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        productoSeleccionado = null;
        NUMERO_PAGINA = 0;
    }//GEN-LAST:event_formWindowClosing

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setSize(sizeDialog);
        this.setTitle("Buscar Producto");
        this.prepararComponentes();
    }//GEN-LAST:event_formWindowOpened

    private void txtPorcentajeDescuentoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPorcentajeDescuentoKeyReleased
        if (evt.getKeyCode() == 10) {
            this.aceptarProducto();
        } else {
            this.actualizarEstadoSeleccion();
        }
    }//GEN-LAST:event_txtPorcentajeDescuentoKeyReleased

    private void txtPorcentajeDescuentoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPorcentajeDescuentoKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_txtPorcentajeDescuentoKeyTyped

    private void txtPorcentajeDescuentoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPorcentajeDescuentoFocusLost
        this.actualizarEstadoSeleccion();
    }//GEN-LAST:event_txtPorcentajeDescuentoFocusLost

    private void txtPorcentajeDescuentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPorcentajeDescuentoFocusGained
        SwingUtilities.invokeLater(() -> {
            txtPorcentajeDescuento.selectAll();
        });
    }//GEN-LAST:event_txtPorcentajeDescuentoFocusGained

    private void txtCantidadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadKeyPressed
        if (evt.getKeyCode() == 10) {
            this.aceptarProducto();
        } else {
            this.actualizarEstadoSeleccion();
        }
    }//GEN-LAST:event_txtCantidadKeyPressed

    private void txtCantidadFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCantidadFocusLost
        this.actualizarEstadoSeleccion();
    }//GEN-LAST:event_txtCantidadFocusLost

    private void txtCantidadFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCantidadFocusGained
        this.seleccionarProductoEnTable();
        SwingUtilities.invokeLater(() -> {
            txtCantidad.selectAll();
        });
    }//GEN-LAST:event_txtCantidadFocusGained

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        this.aceptarProducto();
    }//GEN-LAST:event_btnAceptarActionPerformed

    private void tbl_ResultadosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_ResultadosKeyReleased
        this.seleccionarProductoEnTable();
    }//GEN-LAST:event_tbl_ResultadosKeyReleased

    private void tbl_ResultadosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_ResultadosKeyPressed
        if (evt.getKeyCode() == 10) {
            this.aceptarProducto();
        }
        if (evt.getKeyCode() == 9) {
            txtCantidad.requestFocus();
        }
    }//GEN-LAST:event_tbl_ResultadosKeyPressed

    private void tbl_ResultadosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_ResultadosMouseClicked
        this.seleccionarProductoEnTable();
    }//GEN-LAST:event_tbl_ResultadosMouseClicked

    private void tbl_ResultadosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbl_ResultadosFocusGained
        if ((tbl_Resultados.getSelectedRow() == -1) && (tbl_Resultados.getRowCount() != 0)) {
            tbl_Resultados.setRowSelectionInterval(0, 0);
        }
    }//GEN-LAST:event_tbl_ResultadosFocusGained

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        this.resetScroll();
        this.limpiarJTable();
        this.buscar();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void txtCriteriaBusquedaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCriteriaBusquedaKeyTyped
        evt.setKeyChar(Utilidades.convertirAMayusculas(evt.getKeyChar()));
    }//GEN-LAST:event_txtCriteriaBusquedaKeyTyped

    private void txtCriteriaBusquedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCriteriaBusquedaActionPerformed
        this.resetScroll();
        this.limpiarJTable();
        this.buscar();
    }//GEN-LAST:event_txtCriteriaBusquedaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JLabel lbl_Cantidad;
    private javax.swing.JLabel lbl_Descuento;
    private javax.swing.JPanel panelFondo;
    private javax.swing.JScrollPane spNotaProducto;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JFormattedTextField txtCantidad;
    private javax.swing.JTextField txtCriteriaBusqueda;
    private javax.swing.JFormattedTextField txtPorcentajeDescuento;
    private javax.swing.JTextArea txtaNotaProducto;
    // End of variables declaration//GEN-END:variables
}
