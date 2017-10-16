package sic.vista.swing;

import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
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
import sic.modelo.PaginaRespuestaRest;
import sic.util.Utilidades;

public class BuscarClientesGUI extends JDialog {
    
    private ModeloTabla modeloTablaResultados = new ModeloTabla();    
    private List<Cliente> clientesTotal = new ArrayList<>();
    private List<Cliente> clientesParcial = new ArrayList<>();
    private Cliente clienteSeleccionado;
    private final HotKeysHandler keyHandler = new HotKeysHandler();
    private int NUMERO_PAGINA = 0;
    private static final int TAMANIO_PAGINA = 100;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public BuscarClientesGUI(JDialog parent, boolean modal) {
        super(parent, modal);
        this.initComponents();
        this.setIcon();        
        this.setColumnas();        
        txtCriteriaBusqueda.addKeyListener(keyHandler);
        tbl_Resultados.addKeyListener(keyHandler);
        Timer timer = new Timer(false);
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
        });
        sp_Resultado.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
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
   
    public Cliente getClienteSeleccionado() {
        return clienteSeleccionado;
    }

    public void setClienteSeleccionado(Cliente clienteSeleccionado) {
        this.clienteSeleccionado = clienteSeleccionado;
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(BuscarClientesGUI.class.getResource("/sic/icons/Client_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void cambiarEstadoEnabledComponentes(boolean status) {
        txtCriteriaBusqueda.setEnabled(status);
        btnBuscar.setEnabled(status);
        tbl_Resultados.setEnabled(status);
        btnAceptar.setEnabled(status);
        txtCriteriaBusqueda.requestFocus();
    }
    
    private void buscar() {
        try {
            if (txtCriteriaBusqueda.getText().equals("")) {
                this.resetScroll();
                this.limpiarJTable();
            } else {
                this.cambiarEstadoEnabledComponentes(false);
                String uri = "/clientes/busqueda/criteria?"
                        + "&razonSocial=" + txtCriteriaBusqueda.getText().trim()
                        + "&nombreFantasia=" + txtCriteriaBusqueda.getText().trim()
                        + "&idFiscal=" + txtCriteriaBusqueda.getText().trim()
                        + "&idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                        + "&pagina=" + NUMERO_PAGINA
                        + "&tamanio=" + TAMANIO_PAGINA;
                PaginaRespuestaRest<Cliente> response = RestClient.getRestTemplate()
                        .exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<PaginaRespuestaRest<Cliente>>() {
                        })
                        .getBody();
                clientesParcial = response.getContent();
                clientesTotal.addAll(clientesParcial);
                clienteSeleccionado = null;
                this.cargarResultadosAlTable();
                this.cambiarEstadoEnabledComponentes(true);
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.cambiarEstadoEnabledComponentes(true);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }

    private void setColumnas() {
        //nombres de columnas
        String[] encabezados = new String[5];
        encabezados[0] = "ID Fiscal";
        encabezados[1] = "Razon Social";
        encabezados[2] = "Nombre Fantasia";
        encabezados[3] = "Direccion";
        encabezados[4] = "Condicion IVA";
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaResultados);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        tipos[2] = String.class;
        tipos[3] = String.class;
        tipos[4] = String.class;        
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);

        //Tamanios de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(110);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(250);
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(400);
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(300);
    }

    private void cargarResultadosAlTable() {
        clientesParcial.stream().map(cliente -> {
            Object[] fila = new Object[5];
            fila[0] = cliente.getIdFiscal();
            fila[1] = cliente.getRazonSocial();
            fila[2] = cliente.getNombreFantasia();
            fila[3] = cliente.getDireccion();
            fila[4] = cliente.getCondicionIVA();
            return fila;
        }).forEachOrdered(fila -> {
            modeloTablaResultados.addRow(fila);
        });
        tbl_Resultados.setModel(modeloTablaResultados);
    }

    private void resetScroll() {
        NUMERO_PAGINA = 0;
        clientesTotal.clear();
        clientesParcial.clear();
        Point p = new Point(0, 0);
        sp_Resultado.getViewport().setViewPosition(p);
    }
    
    private void limpiarJTable() {
        modeloTablaResultados = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaResultados);
        this.setColumnas();
    }

    private void seleccionarCliente() {
        if (tbl_Resultados.getSelectedRow() != -1) {
            int filaSeleccionada = tbl_Resultados.getSelectedRow();
            clienteSeleccionado = clientesTotal.get(filaSeleccionada);
        } else {
            clienteSeleccionado = null;
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
        sp_Resultado = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        txtCriteriaBusqueda.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N
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

        btnAceptar.setForeground(java.awt.Color.blue);
        btnAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/22x22_FlechaGO.png"))); // NOI18N
        btnAceptar.setFocusable(false);
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        tbl_Resultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_Resultados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl_Resultados.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbl_ResultadosFocusGained(evt);
            }
        });
        tbl_Resultados.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbl_ResultadosKeyPressed(evt);
            }
        });
        sp_Resultado.setViewportView(tbl_Resultados);

        javax.swing.GroupLayout panelFondoLayout = new javax.swing.GroupLayout(panelFondo);
        panelFondo.setLayout(panelFondoLayout);
        panelFondoLayout.setHorizontalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(sp_Resultado, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 797, Short.MAX_VALUE)
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addComponent(txtCriteriaBusqueda)
                        .addGap(55, 55, 55))
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnBuscar)
                            .addComponent(btnAceptar))))
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
                .addComponent(sp_Resultado, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
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
        this.seleccionarCliente();
}//GEN-LAST:event_btnAceptarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        this.resetScroll();
        this.limpiarJTable();
        this.buscar();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void tbl_ResultadosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_ResultadosKeyPressed
        if (evt.getKeyCode() == 10) {
            this.seleccionarCliente();
        }
        if (evt.getKeyCode() == 9) {
            txtCriteriaBusqueda.requestFocus();
        }
    }//GEN-LAST:event_tbl_ResultadosKeyPressed

    private void txtCriteriaBusquedaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCriteriaBusquedaKeyTyped
        evt.setKeyChar(Utilidades.convertirAMayusculas(evt.getKeyChar()));
    }//GEN-LAST:event_txtCriteriaBusquedaKeyTyped

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setSize(this.getParent().getWidth() - 100, this.getParent().getHeight() - 200);
        this.setLocationRelativeTo(this.getParent());
        this.setTitle("Buscar Cliente");
        this.setColumnas();
    }//GEN-LAST:event_formWindowOpened

    private void tbl_ResultadosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbl_ResultadosFocusGained
        //Si no hay nada seleccionado y NO esta vacio el table, selecciona la primer fila
        if ((tbl_Resultados.getSelectedRow() == -1) && (tbl_Resultados.getRowCount() != 0)) {
            tbl_Resultados.setRowSelectionInterval(0, 0);
        }
    }//GEN-LAST:event_tbl_ResultadosFocusGained
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JPanel panelFondo;
    private javax.swing.JScrollPane sp_Resultado;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JTextField txtCriteriaBusqueda;
    // End of variables declaration//GEN-END:variables
}
