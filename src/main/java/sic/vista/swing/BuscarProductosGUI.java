package sic.vista.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
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
import sic.util.RenderTabla;
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
    private static final int TAMANIO_PAGINA = 50;
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
        ta_ObservacionesProducto.addKeyListener(keyHandler);
        txtCantidad.addKeyListener(keyHandler);
        txt_UnidadMedida.addKeyListener(keyHandler);
        txtPorcentajeDescuento.addKeyListener(keyHandler);
        btnAceptar.addKeyListener(keyHandler);        
        // desactivado momentaneamente
        /*Timer timer = new Timer(false);
        txtCriteriaBusqueda.addKeyListener(new KeyAdapter() {
            private TimerTask task;
            @Override
            public void keyTyped(KeyEvent e) {
                if (task != null) {
                    task.cancel();
                }
                task = new TimerTask() {
                    @Override
                    public void run() {
                        resetScroll();
                        limpiarJTable();
                        buscar();
                    }
                };
                timer.schedule(task, 450);
            }
        });*/
        sp_Resultados.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int va = scrollBar.getVisibleAmount() + 50;
            if (scrollBar.getValue() >= (scrollBar.getMaximum() - va)) {
                if (productosTotal.size() >= TAMANIO_PAGINA) {
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
                        + "&pagina=" + NUMERO_PAGINA + "&tamanio=" + TAMANIO_PAGINA;
                PaginaRespuestaRest<Producto> response = RestClient.getRestTemplate()
                        .exchange("/productos/busqueda/criteria?" + uri, HttpMethod.GET, null,
                                new ParameterizedTypeReference<PaginaRespuestaRest<Producto>>() {
                        })
                        .getBody();
                productosParcial = response.getContent();
                productosTotal.addAll(productosParcial);
                productoSeleccionado = null;
                txt_UnidadMedida.setText("");
                this.restarCantidadesSegunProductosYaCargados();
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
        if (productoSeleccionado == null) {
            debeCargarRenglon = false;
            this.dispose();
        } else {
            if (movimiento == Movimiento.PEDIDO) {
                if (this.sumarCantidadesSegunProductosYaCargados() > productoSeleccionado.getCantidad()) {
                    JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                            .getString("mensaje_producto_sin_stock_suficiente"), "Error", JOptionPane.ERROR_MESSAGE);
                    esValido = false;
                } else if (Double.valueOf(txtCantidad.getValue().toString()) < productoSeleccionado.getVentaMinima()) {
                    JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                            .getString("mensaje_producto_cantidad_menor_a_minima"), "Error", JOptionPane.ERROR_MESSAGE);
                    esValido = false;
                }
            }
            if (movimiento == Movimiento.VENTA) {
                if (this.sumarCantidadesSegunProductosYaCargados() > productoSeleccionado.getCantidad()) {
                    JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                            .getString("mensaje_producto_sin_stock_suficiente"), "Error", JOptionPane.ERROR_MESSAGE);
                    esValido = false;
                }
            }            
            if (esValido) {
                try {
                    renglon = RestClient.getRestTemplate().getForObject("/facturas/renglon?"
                            + "idProducto=" + productoSeleccionado.getId_Producto()
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
    
    private double sumarCantidadesSegunProductosYaCargados() {
        double cantidad = Double.parseDouble(txtCantidad.getValue().toString());
        return renglones.stream()
                        .filter(r -> (r.getId_ProductoItem() == productoSeleccionado.getId_Producto()))
                        .map(r -> r.getCantidad())
                        .reduce(cantidad, (accumulator, item) -> accumulator + item);
    }
    
    private void restarCantidadesSegunProductosYaCargados() {
        if (!(movimiento == Movimiento.PEDIDO || movimiento == Movimiento.COMPRA)) {
            renglones.stream().forEach(r -> {
                productosTotal.stream()
                        .filter(p -> (r.getDescripcionItem().equals(p.getDescripcion()) && p.isIlimitado() == false))
                        .forEach(p -> {
                            p.setCantidad(p.getCantidad() - r.getCantidad());
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
            txt_UnidadMedida.setText(productoSeleccionado.getNombreMedida());
            ta_ObservacionesProducto.setText(productoSeleccionado.getNota());
            this.actualizarEstadoSeleccion();
        }
    }

    private void cargarResultadosAlTable() {
        productosParcial.stream().map(p -> {
            Object[] fila = new Object[6];
            fila[0] = p.getCodigo();
            fila[1] = p.getDescripcion();
            fila[2] = p.getCantidad();
            fila[3] = p.getVentaMinima();
            fila[4] = p.getNombreMedida();
            double precio = (movimiento == Movimiento.VENTA) ? p.getPrecioLista()
                    : (movimiento == Movimiento.PEDIDO) ? p.getPrecioLista()
                    : (movimiento == Movimiento.COMPRA) ? p.getPrecioCosto() : 0.0;
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
        //nombres de columnas
        String[] encabezados = new String[6];
        encabezados[0] = "Codigo";
        encabezados[1] = "Descripción";
        encabezados[2] = "Cantidad";
        encabezados[3] = "Venta Min.";
        encabezados[4] = "Unidad";
        String encabezadoPrecio = (movimiento == Movimiento.VENTA) ? "P. Lista"
                : (movimiento == Movimiento.PEDIDO) ? "P. Lista"
                : (movimiento == Movimiento.COMPRA) ? "P.Costo" : "";
        encabezados[5] = encabezadoPrecio;
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaResultados);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        tipos[2] = Double.class;
        tipos[3] = Double.class;
        tipos[4] = String.class;
        tipos[5] = Double.class;
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);

        //render para los tipos de datos
        tbl_Resultados.setDefaultRenderer(Double.class, new RenderTabla());

        //Size de columnas        
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(130);
        tbl_Resultados.getColumnModel().getColumn(0).setMaxWidth(130);        
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(380);        
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(70);
        tbl_Resultados.getColumnModel().getColumn(2).setMaxWidth(70);        
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(70);
        tbl_Resultados.getColumnModel().getColumn(3).setMaxWidth(70);        
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
        txt_UnidadMedida = new javax.swing.JTextField();
        lbl_Cantidad = new javax.swing.JLabel();
        lbl_Descuento = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JFormattedTextField();
        txtPorcentajeDescuento = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        ta_ObservacionesProducto = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
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
                "Title 1", "Title 2", "Title 3", "Title 4"
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

        txt_UnidadMedida.setEditable(false);
        txt_UnidadMedida.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N
        txt_UnidadMedida.setFocusable(false);

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

        ta_ObservacionesProducto.setEditable(false);
        ta_ObservacionesProducto.setColumns(20);
        ta_ObservacionesProducto.setLineWrap(true);
        ta_ObservacionesProducto.setRows(4);
        ta_ObservacionesProducto.setFocusable(false);
        jScrollPane1.setViewportView(ta_ObservacionesProducto);

        javax.swing.GroupLayout panelFondoLayout = new javax.swing.GroupLayout(panelFondo);
        panelFondo.setLayout(panelFondoLayout);
        panelFondoLayout.setHorizontalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sp_Resultados)
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addComponent(txtCriteriaBusqueda)
                        .addGap(0, 0, 0)
                        .addComponent(btnBuscar))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFondoLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbl_Descuento)
                            .addComponent(lbl_Cantidad))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtPorcentajeDescuento)
                            .addComponent(txtCantidad, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_UnidadMedida, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
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
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lbl_Cantidad)
                            .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_UnidadMedida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lbl_Descuento)
                            .addComponent(txtPorcentajeDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelFondoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscar, txtCriteriaBusqueda});

        panelFondoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtCantidad, txtPorcentajeDescuento, txt_UnidadMedida});

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

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        this.aceptarProducto();
    }//GEN-LAST:event_btnAceptarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        this.resetScroll();
        this.limpiarJTable();
        this.buscar();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void tbl_ResultadosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_ResultadosMouseClicked
        this.seleccionarProductoEnTable();
    }//GEN-LAST:event_tbl_ResultadosMouseClicked

    private void txtCantidadFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCantidadFocusLost
        this.actualizarEstadoSeleccion();
    }//GEN-LAST:event_txtCantidadFocusLost

    private void txtPorcentajeDescuentoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPorcentajeDescuentoFocusLost
        this.actualizarEstadoSeleccion();
    }//GEN-LAST:event_txtPorcentajeDescuentoFocusLost

    private void tbl_ResultadosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_ResultadosKeyPressed
        if (evt.getKeyCode() == 10) {
            this.aceptarProducto();
        }
        if (evt.getKeyCode() == 9) {
            txtCantidad.requestFocus();
        }
    }//GEN-LAST:event_tbl_ResultadosKeyPressed

    private void tbl_ResultadosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_ResultadosKeyReleased
        this.seleccionarProductoEnTable();
    }//GEN-LAST:event_tbl_ResultadosKeyReleased

    private void txtPorcentajeDescuentoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPorcentajeDescuentoKeyReleased
        if (evt.getKeyCode() == 10) {
            this.aceptarProducto();
        } else {
            this.actualizarEstadoSeleccion();
        }
    }//GEN-LAST:event_txtPorcentajeDescuentoKeyReleased

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        productoSeleccionado = null;
        NUMERO_PAGINA = 0;
    }//GEN-LAST:event_formWindowClosing

    private void txtCriteriaBusquedaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCriteriaBusquedaKeyTyped
        evt.setKeyChar(Utilidades.convertirAMayusculas(evt.getKeyChar()));
    }//GEN-LAST:event_txtCriteriaBusquedaKeyTyped

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setSize(sizeDialog);
        this.setTitle("Buscar Producto");
        this.prepararComponentes();
    }//GEN-LAST:event_formWindowOpened

    private void txtCantidadFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCantidadFocusGained
        this.seleccionarProductoEnTable();
        SwingUtilities.invokeLater(() -> {
            txtCantidad.selectAll();
        });
    }//GEN-LAST:event_txtCantidadFocusGained

    private void txtCantidadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadKeyPressed
        if (evt.getKeyCode() == 10) {
            this.aceptarProducto();
        } else {
            this.actualizarEstadoSeleccion();
        }
    }//GEN-LAST:event_txtCantidadKeyPressed

    private void txtPorcentajeDescuentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPorcentajeDescuentoFocusGained
        SwingUtilities.invokeLater(() -> {
            txtPorcentajeDescuento.selectAll();
        });
    }//GEN-LAST:event_txtPorcentajeDescuentoFocusGained

    private void tbl_ResultadosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbl_ResultadosFocusGained
        if ((tbl_Resultados.getSelectedRow() == -1) && (tbl_Resultados.getRowCount() != 0)) {
            tbl_Resultados.setRowSelectionInterval(0, 0);
        }
    }//GEN-LAST:event_tbl_ResultadosFocusGained

    private void txtCriteriaBusquedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCriteriaBusquedaActionPerformed
        this.resetScroll();
        this.limpiarJTable();
        this.buscar();
    }//GEN-LAST:event_txtCriteriaBusquedaActionPerformed

    private void txtPorcentajeDescuentoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPorcentajeDescuentoKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_MINUS) {
            evt.consume();
        }
    }//GEN-LAST:event_txtPorcentajeDescuentoKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_Cantidad;
    private javax.swing.JLabel lbl_Descuento;
    private javax.swing.JPanel panelFondo;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JTextArea ta_ObservacionesProducto;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JFormattedTextField txtCantidad;
    private javax.swing.JTextField txtCriteriaBusqueda;
    private javax.swing.JFormattedTextField txtPorcentajeDescuento;
    private javax.swing.JTextField txt_UnidadMedida;
    // End of variables declaration//GEN-END:variables
}
