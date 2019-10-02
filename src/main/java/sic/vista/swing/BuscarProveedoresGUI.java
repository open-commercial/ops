package sic.vista.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
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
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.Proveedor;
import sic.modelo.criteria.BusquedaProveedorCriteria;
import sic.util.Utilidades;

public class BuscarProveedoresGUI extends JDialog {
    
    private ModeloTabla modeloTablaResultados = new ModeloTabla();    
    private List<Proveedor> proveedoresTotal = new ArrayList<>();
    private List<Proveedor> proveedoresParcial = new ArrayList<>();
    private Proveedor proveedorSeleccionado;
    private final HotKeysHandler keyHandler = new HotKeysHandler();
    private int NUMERO_PAGINA = 0;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeDialog = new Dimension(1000, 600);

    public BuscarProveedoresGUI() {
        this.initComponents();
        this.setIcon();        
        this.setColumnas();        
        txtCriteriaBusqueda.addKeyListener(keyHandler);
        tblResultados.addKeyListener(keyHandler);
        sp_Resultados.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int va = scrollBar.getVisibleAmount() + 10;
            if (scrollBar.getValue() >= (scrollBar.getMaximum() - va)) {
                if (proveedoresTotal.size() >= 10) {
                    NUMERO_PAGINA += 1;
                    buscar();
                }
            }
        });
    }
   
    public Proveedor getProveedorSeleccionado() {
        return proveedorSeleccionado;
    }

    public void setProveedorSeleccionado(Proveedor proveedorSeleccionado) {
        this.proveedorSeleccionado = proveedorSeleccionado;
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(BuscarProveedoresGUI.class.getResource("/sic/icons/ProviderBag_16x16.png")); 
        this.setIconImage(iconoVentana.getImage());
    }    
    
    private void buscar() {
        try {
            if (txtCriteriaBusqueda.getText().equals("")) {
                this.resetScroll();
                this.limpiarJTable();
            } else {
                BusquedaProveedorCriteria criteria = BusquedaProveedorCriteria.builder().build();
                criteria.setNroProveedor(txtCriteriaBusqueda.getText().trim());
                criteria.setRazonSocial(txtCriteriaBusqueda.getText().trim());
                criteria.setPagina(NUMERO_PAGINA);
                HttpEntity<BusquedaProveedorCriteria> requestEntity = new HttpEntity<>(criteria);
                PaginaRespuestaRest<Proveedor> response = RestClient.getRestTemplate()
                        .exchange("/proveedores/busqueda/criteria", HttpMethod.POST, requestEntity, 
                                new ParameterizedTypeReference<PaginaRespuestaRest<Proveedor>>() {})
                        .getBody();
                proveedoresParcial = response.getContent();
                proveedoresTotal.addAll(proveedoresParcial);
                proveedorSeleccionado = null;
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

    private void setColumnas() {
        String[] encabezados = new String[4];
        encabezados[0] = "Nº Proveedor";
        encabezados[1] = "CUIT o DNI";
        encabezados[2] = "Razon Social";
        encabezados[3] = "Ubicación";               
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tblResultados.setModel(modeloTablaResultados);        
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        tipos[2] = String.class;
        tipos[3] = String.class;       
        modeloTablaResultados.setClaseColumnas(tipos);
        tblResultados.getTableHeader().setReorderingAllowed(false);
        tblResultados.getTableHeader().setResizingAllowed(true);     
        tblResultados.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblResultados.getColumnModel().getColumn(0).setMinWidth(100);
        tblResultados.getColumnModel().getColumn(0).setMaxWidth(100);
        tblResultados.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblResultados.getColumnModel().getColumn(1).setMinWidth(100);
        tblResultados.getColumnModel().getColumn(1).setMaxWidth(100);
        tblResultados.getColumnModel().getColumn(2).setPreferredWidth(250);
        tblResultados.getColumnModel().getColumn(3).setPreferredWidth(250);      
    }

    private void cargarResultadosAlTable() {
        proveedoresParcial.stream().map(proveedor -> {
            Object[] fila = new Object[4];
            fila[0] = proveedor.getNroProveedor();
            fila[1] = proveedor.getIdFiscal();
            fila[2] = proveedor.getRazonSocial();
            fila[3] = proveedor.getUbicacion();            
            return fila;
        }).forEachOrdered(fila -> {
            modeloTablaResultados.addRow(fila);
        });
        tblResultados.setModel(modeloTablaResultados);
    }

    private void resetScroll() {
        NUMERO_PAGINA = 0;
        proveedoresTotal.clear();
        proveedoresParcial.clear();
        Point p = new Point(0, 0);
        sp_Resultados.getViewport().setViewPosition(p);
    }
    
    private void limpiarJTable() {
        modeloTablaResultados = new ModeloTabla();
        tblResultados.setModel(modeloTablaResultados);
        this.setColumnas();
    }

    private void seleccionarProveedor() {
        if (tblResultados.getSelectedRow() != -1) {
            int filaSeleccionada = tblResultados.getSelectedRow();
            proveedorSeleccionado = proveedoresTotal.get(filaSeleccionada);
        } else {
            proveedorSeleccionado = null;
        }
        this.dispose();
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
        btnAceptar = new javax.swing.JButton();
        sp_Resultados = new javax.swing.JScrollPane();
        tblResultados = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
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
        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Refresh_16x16.png"))); // NOI18N
        btnBuscar.setFocusable(false);
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        btnAceptar.setForeground(java.awt.Color.blue);
        btnAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/22x22_FlechaGO.png"))); // NOI18N
        btnAceptar.setFocusable(false);
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        tblResultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblResultados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblResultados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblResultados.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblResultadosFocusGained(evt);
            }
        });
        tblResultados.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblResultadosKeyPressed(evt);
            }
        });
        sp_Resultados.setViewportView(tblResultados);

        javax.swing.GroupLayout panelFondoLayout = new javax.swing.GroupLayout(panelFondo);
        panelFondo.setLayout(panelFondoLayout);
        panelFondoLayout.setHorizontalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(sp_Resultados, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 797, Short.MAX_VALUE)
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnAceptar))
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addComponent(txtCriteriaBusqueda)
                        .addGap(0, 0, 0)
                        .addComponent(btnBuscar)))
                .addContainerGap())
        );
        panelFondoLayout.setVerticalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCriteriaBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAceptar)
                .addContainerGap())
        );

        panelFondoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBuscar, txtCriteriaBusqueda});

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
        this.seleccionarProveedor();
}//GEN-LAST:event_btnAceptarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        this.resetScroll();
        this.limpiarJTable();
        this.buscar();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void tblResultadosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblResultadosKeyPressed
        if (evt.getKeyCode() == 10) {
            this.seleccionarProveedor();
        }
        if (evt.getKeyCode() == 9) {
            txtCriteriaBusqueda.requestFocus();
        }
    }//GEN-LAST:event_tblResultadosKeyPressed

    private void txtCriteriaBusquedaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCriteriaBusquedaKeyTyped
        evt.setKeyChar(Utilidades.convertirAMayusculas(evt.getKeyChar()));
    }//GEN-LAST:event_txtCriteriaBusquedaKeyTyped

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setSize(sizeDialog);
        this.setTitle("Buscar Proveedor");
        this.setColumnas();
    }//GEN-LAST:event_formWindowOpened

    private void tblResultadosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblResultadosFocusGained
        //Si no hay nada seleccionado y NO esta vacio el table, selecciona la primer fila
        if ((tblResultados.getSelectedRow() == -1) && (tblResultados.getRowCount() != 0)) {
            tblResultados.setRowSelectionInterval(0, 0);
        }
    }//GEN-LAST:event_tblResultadosFocusGained

    private void txtCriteriaBusquedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCriteriaBusquedaActionPerformed
        this.resetScroll();
        this.limpiarJTable();
        this.buscar();
    }//GEN-LAST:event_txtCriteriaBusquedaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JPanel panelFondo;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JTable tblResultados;
    private javax.swing.JTextField txtCriteriaBusqueda;
    // End of variables declaration//GEN-END:variables
}
