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
import sic.modelo.Caja;
import sic.modelo.EmpresaActiva;
import sic.modelo.Usuario;
import sic.modelo.EstadoCaja;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.Rol;
import sic.util.ColoresEstadosRenderer;
import sic.util.DecimalesRenderer;
import sic.util.FechasRenderer;
import sic.util.FormatosFechaHora;
import sic.util.FormatterFechaHora;
import sic.util.Utilidades;

public class CajasGUI extends JInternalFrame {

    private ModeloTabla modeloTablaCajas = new ModeloTabla();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private List<Caja> cajasTotal = new ArrayList<>();
    private List<Caja> cajasParcial = new ArrayList<>();
    private static int totalElementosBusqueda;
    private static int NUMERO_PAGINA = 0;
    private static final int TAMANIO_PAGINA = 50;
    private final Dimension sizeInternalFrame = new Dimension(880, 600);

    public CajasGUI() {
        this.initComponents();        
        sp_Cajas.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int va = scrollBar.getVisibleAmount() + 50;
            if (scrollBar.getValue() >= (scrollBar.getMaximum() - va)) {
                if (cajasTotal.size() >= TAMANIO_PAGINA) {
                    NUMERO_PAGINA += 1;
                    buscar();
                }
            }
        });
    }

    private void setColumnasCaja() {       
        //nombres de columnas
        String[] encabezados = new String[8];
        encabezados[0] = "Estado";
        encabezados[1] = "Fecha Apertura";
        encabezados[2] = "Hora Control";
        encabezados[3] = "Fecha Cierre";
        encabezados[4] = "Usuario de Cierre";
        encabezados[5] = "Apertura";
        encabezados[6] = "Cierre Final";
        encabezados[7] = "Cierre Real";
        modeloTablaCajas.setColumnIdentifiers(encabezados);
        tbl_Cajas.setModel(modeloTablaCajas);
        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaCajas.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = Date.class;
        tipos[2] = String.class;
        tipos[3] = Date.class;
        tipos[4] = String.class;
        tipos[5] = BigDecimal.class;
        tipos[6] = BigDecimal.class;
        tipos[7] = BigDecimal.class;
        modeloTablaCajas.setClaseColumnas(tipos);
        tbl_Cajas.getTableHeader().setReorderingAllowed(false);
        tbl_Cajas.getTableHeader().setResizingAllowed(true);
        //tamanios de columnas
        tbl_Cajas.getColumnModel().getColumn(0).setPreferredWidth(0);
        tbl_Cajas.getColumnModel().getColumn(1).setPreferredWidth(80);
        tbl_Cajas.getColumnModel().getColumn(2).setPreferredWidth(30);
        tbl_Cajas.getColumnModel().getColumn(3).setPreferredWidth(80);
        tbl_Cajas.getColumnModel().getColumn(4).setPreferredWidth(40);
        tbl_Cajas.getColumnModel().getColumn(5).setPreferredWidth(25);
        tbl_Cajas.getColumnModel().getColumn(6).setPreferredWidth(20);
        tbl_Cajas.getColumnModel().getColumn(7).setPreferredWidth(20);
        //renderers
        tbl_Cajas.setDefaultRenderer(BigDecimal.class, new DecimalesRenderer());
        tbl_Cajas.getColumnModel().getColumn(1).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHAHORA_HISPANO));
        tbl_Cajas.getColumnModel().getColumn(2).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHAHORA_HISPANO));
        tbl_Cajas.getColumnModel().getColumn(3).setCellRenderer(new FechasRenderer(FormatosFechaHora.FORMATO_FECHAHORA_HISPANO));
    }

    private void buscar() {
        this.cambiarEstadoEnabledComponentes(false);
        String criteriaBusqueda = "/cajas/busqueda/criteria?";
        if (chk_Fecha.isSelected()) {
            criteriaBusqueda += "desde=" + dc_FechaDesde.getDate().getTime() + "&hasta=" + dc_FechaHasta.getDate().getTime();
        }
        if (chk_Usuario.isSelected()) {
            criteriaBusqueda += "&idUsuario=" + ((Usuario) cmb_Usuarios.getSelectedItem()).getId_Usuario();
        }
        criteriaBusqueda += "&idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa();
        criteriaBusqueda += "&pagina=" + NUMERO_PAGINA + "&tamanio=" + TAMANIO_PAGINA;
        try {
            PaginaRespuestaRest<Caja> response = RestClient.getRestTemplate()
                    .exchange(criteriaBusqueda, HttpMethod.GET, null,
                            new ParameterizedTypeReference<PaginaRespuestaRest<Caja>>() {
                    })
                    .getBody();
            totalElementosBusqueda = response.getTotalElements();
            cajasParcial = response.getContent();
            cajasTotal.addAll(cajasParcial);
            this.cargarResultadosAlTable();
            criteriaBusqueda = "";
            if (chk_Fecha.isSelected()) {
                criteriaBusqueda += "desde=" + dc_FechaDesde.getDate().getTime() + "&hasta=" + dc_FechaHasta.getDate().getTime();
            }
            if (chk_Usuario.isSelected()) {
                criteriaBusqueda += "&idUsuario=" + ((Usuario) cmb_Usuarios.getSelectedItem()).getId_Usuario();
            }
//            ftxt_TotalFinal.setValue(RestClient.getRestTemplate()
//                    .getForObject("/cajas/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
//                            + "/saldo-final?" + criteriaBusqueda, BigDecimal.class));
//            ftxt_TotalCierre.setValue(RestClient.getRestTemplate()
//                    .getForObject("/cajas/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
//                            + "/saldo-real?" + criteriaBusqueda, BigDecimal.class));
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
        chk_Usuario.setEnabled(status);
        if (status == true && chk_Usuario.isSelected() == true) {
            cmb_Usuarios.setEnabled(true);
        } else {
            cmb_Usuarios.setEnabled(false);
        }
        btn_buscar.setEnabled(status);        
        tbl_Cajas.setEnabled(status);
        sp_Cajas.setEnabled(status);
        btn_AbrirCaja.setEnabled(status);
        btn_eliminarCaja.setEnabled(status);
        btn_verDetalle.setEnabled(status);
        tbl_Cajas.requestFocus();
    }

    private void cargarResultadosAlTable() {
        for (Caja caja : cajasParcial) {
            Object[] fila = new Object[8];
            fila[0] = caja.getEstado();
            fila[1] = caja.getFechaApertura();
            fila[2] = (new FormatterFechaHora(FormatosFechaHora.FORMATO_HORA_INTERNACIONAL)).format(caja.getFechaCorteInforme());
            if (caja.getFechaCierre() != null) {
                fila[3] = caja.getFechaCierre();
            }
            fila[4] = (caja.getUsuarioCierraCaja() != null ? caja.getUsuarioCierraCaja() : "");
            fila[5] = caja.getSaldoInicial();
            fila[6] = caja.getSaldoFinal();
            fila[7] = (caja.getEstado().equals(EstadoCaja.CERRADA) ? caja.getSaldoReal() : 0.0);
            modeloTablaCajas.addRow(fila);
        }
        tbl_Cajas.setModel(modeloTablaCajas);
        tbl_Cajas.getColumnModel().getColumn(0).setCellRenderer(new ColoresEstadosRenderer());
        lbl_cantidadMostrar.setText(totalElementosBusqueda + " Cajas encontradas");
    }

    private void limpiarResultados() {
        NUMERO_PAGINA = 0;
        cajasTotal.clear();
        cajasParcial.clear();
        Point p = new Point(0, 0);
        sp_Cajas.getViewport().setViewPosition(p);
        modeloTablaCajas = new ModeloTabla();
        tbl_Cajas.setModel(modeloTablaCajas);
        this.setColumnasCaja();
    }

    private void abrirNuevaCaja() {
        AbrirCajaGUI abrirCaja = new AbrirCajaGUI();
        abrirCaja.setModal(true);
        abrirCaja.setLocationRelativeTo(this);
        abrirCaja.setVisible(true);
        this.limpiarResultados();
        this.buscar();        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_Filtros = new javax.swing.JPanel();
        chk_Fecha = new javax.swing.JCheckBox();
        dc_FechaDesde = new com.toedter.calendar.JDateChooser();
        dc_FechaHasta = new com.toedter.calendar.JDateChooser();
        lbl_Hasta = new javax.swing.JLabel();
        lbl_Desde = new javax.swing.JLabel();
        btn_buscar = new javax.swing.JButton();
        chk_Usuario = new javax.swing.JCheckBox();
        cmb_Usuarios = new javax.swing.JComboBox<>();
        lbl_cantidadMostrar = new javax.swing.JLabel();
        pnl_Cajas = new javax.swing.JPanel();
        sp_Cajas = new javax.swing.JScrollPane();
        tbl_Cajas = new javax.swing.JTable();
        btn_AbrirCaja = new javax.swing.JButton();
        btn_verDetalle = new javax.swing.JButton();
        btn_eliminarCaja = new javax.swing.JButton();
        lbl_TotalFinal = new javax.swing.JLabel();
        ftxt_TotalFinal = new javax.swing.JFormattedTextField();
        lbl_TotalCierre = new javax.swing.JLabel();
        ftxt_TotalCierre = new javax.swing.JFormattedTextField();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Cajas");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Caja_16x16.png"))); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                CajasGUI.this.internalFrameOpened(evt);
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

        pnl_Filtros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        chk_Fecha.setText("Fecha Caja:");
        chk_Fecha.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FechaItemStateChanged(evt);
            }
        });

        dc_FechaDesde.setDateFormatString("dd/MM/yyyy");
        dc_FechaDesde.setEnabled(false);

        dc_FechaHasta.setDateFormatString("dd/MM/yyyy");
        dc_FechaHasta.setEnabled(false);

        lbl_Hasta.setText("Hasta:");
        lbl_Hasta.setEnabled(false);

        lbl_Desde.setText("Desde:");
        lbl_Desde.setEnabled(false);

        btn_buscar.setForeground(java.awt.Color.blue);
        btn_buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btn_buscar.setText("Buscar");
        btn_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscarActionPerformed(evt);
            }
        });

        chk_Usuario.setText("Usuario:");
        chk_Usuario.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_UsuarioItemStateChanged(evt);
            }
        });

        lbl_cantidadMostrar.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout pnl_FiltrosLayout = new javax.swing.GroupLayout(pnl_Filtros);
        pnl_Filtros.setLayout(pnl_FiltrosLayout);
        pnl_FiltrosLayout.setHorizontalGroup(
            pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_FiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_FiltrosLayout.createSequentialGroup()
                        .addComponent(btn_buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_cantidadMostrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnl_FiltrosLayout.createSequentialGroup()
                        .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chk_Usuario)
                            .addComponent(chk_Fecha))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(pnl_FiltrosLayout.createSequentialGroup()
                                .addComponent(lbl_Desde)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbl_Hasta)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cmb_Usuarios, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pnl_FiltrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_buscar, chk_Fecha, chk_Usuario});

        pnl_FiltrosLayout.setVerticalGroup(
            pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_FiltrosLayout.createSequentialGroup()
                .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Fecha)
                    .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Desde)
                    .addComponent(lbl_Hasta))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmb_Usuarios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_Usuario))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_buscar)
                    .addComponent(lbl_cantidadMostrar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnl_FiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_buscar, lbl_cantidadMostrar});

        pnl_Cajas.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        tbl_Cajas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbl_Cajas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sp_Cajas.setViewportView(tbl_Cajas);

        btn_AbrirCaja.setForeground(java.awt.Color.blue);
        btn_AbrirCaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AbrirCaja_16x16.png"))); // NOI18N
        btn_AbrirCaja.setText("Abrir Nueva");
        btn_AbrirCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AbrirCajaActionPerformed(evt);
            }
        });

        btn_verDetalle.setForeground(java.awt.Color.blue);
        btn_verDetalle.setText("Ver Detalle");
        btn_verDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_verDetalleActionPerformed(evt);
            }
        });

        btn_eliminarCaja.setForeground(java.awt.Color.blue);
        btn_eliminarCaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Cancel_16x16.png"))); // NOI18N
        btn_eliminarCaja.setText("Eliminar");
        btn_eliminarCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_eliminarCajaActionPerformed(evt);
            }
        });

        lbl_TotalFinal.setText("Total Final:");

        ftxt_TotalFinal.setEditable(false);
        ftxt_TotalFinal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        ftxt_TotalFinal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        lbl_TotalCierre.setText("Total Real:");

        ftxt_TotalCierre.setEditable(false);
        ftxt_TotalCierre.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        ftxt_TotalCierre.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout pnl_CajasLayout = new javax.swing.GroupLayout(pnl_Cajas);
        pnl_Cajas.setLayout(pnl_CajasLayout);
        pnl_CajasLayout.setHorizontalGroup(
            pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_CajasLayout.createSequentialGroup()
                .addComponent(btn_AbrirCaja)
                .addGap(0, 0, 0)
                .addComponent(btn_verDetalle)
                .addGap(0, 0, 0)
                .addComponent(btn_eliminarCaja)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_CajasLayout.createSequentialGroup()
                        .addComponent(lbl_TotalFinal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxt_TotalFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_CajasLayout.createSequentialGroup()
                        .addComponent(lbl_TotalCierre)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxt_TotalCierre, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addComponent(sp_Cajas, javax.swing.GroupLayout.DEFAULT_SIZE, 709, Short.MAX_VALUE)
        );

        pnl_CajasLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_AbrirCaja, btn_eliminarCaja, btn_verDetalle});

        pnl_CajasLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbl_TotalCierre, lbl_TotalFinal});

        pnl_CajasLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ftxt_TotalCierre, ftxt_TotalFinal});

        pnl_CajasLayout.setVerticalGroup(
            pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_CajasLayout.createSequentialGroup()
                .addComponent(sp_Cajas, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_verDetalle)
                        .addComponent(btn_eliminarCaja)
                        .addComponent(btn_AbrirCaja))
                    .addGroup(pnl_CajasLayout.createSequentialGroup()
                        .addGroup(pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbl_TotalFinal)
                            .addComponent(ftxt_TotalFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbl_TotalCierre)
                            .addComponent(ftxt_TotalCierre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );

        pnl_CajasLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_eliminarCaja, btn_verDetalle});

        lbl_TotalFinal.getAccessibleContext().setAccessibleName("Total Final:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_Cajas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_Filtros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_Filtros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_Cajas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chk_FechaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FechaItemStateChanged
        //Pregunta el estado actual del checkBox
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

    private void btn_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscarActionPerformed
        this.limpiarResultados();
        this.buscar();        
    }//GEN-LAST:event_btn_buscarActionPerformed
    
    private void btn_verDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_verDetalleActionPerformed
        if (tbl_Cajas.getSelectedRow() != -1) {
            int indice = Utilidades.getSelectedRowModelIndice(tbl_Cajas);
            try {
                Caja caja = RestClient.getRestTemplate()
                        .getForObject("/cajas/ " + this.cajasTotal.get(indice).getId_Caja(), Caja.class);
                JInternalFrame iFrameCaja = new CajaGUI(caja);
                iFrameCaja.setLocation(getDesktopPane().getWidth() / 2 - iFrameCaja.getWidth() / 2,
                        getDesktopPane().getHeight() / 2 - iFrameCaja.getHeight() / 2);
                getDesktopPane().add(iFrameCaja);
                iFrameCaja.setVisible(true);            
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_verDetalleActionPerformed

    private void btn_eliminarCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_eliminarCajaActionPerformed
        if (tbl_Cajas.getSelectedRow() != -1) {
            int confirmacionEliminacion = JOptionPane.showConfirmDialog(this,
                    "¿Esta seguro que desea eliminar la caja seleccionada?",
                    "Eliminar", JOptionPane.YES_NO_OPTION);
            try {
                if (confirmacionEliminacion == JOptionPane.YES_OPTION) {
                    int indiceDelModel = Utilidades.getSelectedRowModelIndice(tbl_Cajas);
                    RestClient.getRestTemplate().delete("/cajas/" + this.cajasTotal.get(indiceDelModel).getId_Caja());
                }
                this.limpiarResultados();
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
    }//GEN-LAST:event_btn_eliminarCajaActionPerformed

    private void chk_UsuarioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_UsuarioItemStateChanged
        try {
            if (chk_Usuario.isSelected() == true) {
                cmb_Usuarios.setEnabled(true);
                List<Usuario> usuarios = Arrays.asList(RestClient.getRestTemplate()
                        .getForObject("/usuarios/roles?rol=" + Rol.ADMINISTRADOR, Usuario[].class));
                usuarios.stream().forEach((usuario) -> {
                    cmb_Usuarios.addItem(usuario);
                });
            } else {
                cmb_Usuarios.removeAllItems();
                cmb_Usuarios.setEnabled(false);
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_chk_UsuarioItemStateChanged

    private void btn_AbrirCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AbrirCajaActionPerformed
        this.abrirNuevaCaja();
    }//GEN-LAST:event_btn_AbrirCajaActionPerformed

    private void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_internalFrameOpened
        this.setSize(sizeInternalFrame);
        this.setColumnasCaja();
        cmb_Usuarios.setEnabled(false);
        dc_FechaDesde.setDate(new Date());
        dc_FechaHasta.setDate(new Date());
        try {
            this.setMaximum(true);            
        } catch (PropertyVetoException ex) {
            String mensaje = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_internalFrameOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_AbrirCaja;
    private javax.swing.JButton btn_buscar;
    private javax.swing.JButton btn_eliminarCaja;
    private javax.swing.JButton btn_verDetalle;
    private javax.swing.JCheckBox chk_Fecha;
    private javax.swing.JCheckBox chk_Usuario;
    private javax.swing.JComboBox<Usuario> cmb_Usuarios;
    private com.toedter.calendar.JDateChooser dc_FechaDesde;
    private com.toedter.calendar.JDateChooser dc_FechaHasta;
    private javax.swing.JFormattedTextField ftxt_TotalCierre;
    private javax.swing.JFormattedTextField ftxt_TotalFinal;
    private javax.swing.JLabel lbl_Desde;
    private javax.swing.JLabel lbl_Hasta;
    private javax.swing.JLabel lbl_TotalCierre;
    private javax.swing.JLabel lbl_TotalFinal;
    private javax.swing.JLabel lbl_cantidadMostrar;
    private javax.swing.JPanel pnl_Cajas;
    private javax.swing.JPanel pnl_Filtros;
    private javax.swing.JScrollPane sp_Cajas;
    private javax.swing.JTable tbl_Cajas;
    // End of variables declaration//GEN-END:variables

}
