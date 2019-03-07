package sic.vista.swing;

import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.EmpresaActiva;
import sic.modelo.Transportista;
import sic.modelo.TipoDeOperacion;
import sic.modelo.Ubicacion;

public class DetalleTransportistaGUI extends JDialog {

    private Transportista transportistaModificar;
    private final TipoDeOperacion operacion;
    private Ubicacion ubicacion;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleTransportistaGUI() {
        this.initComponents();
        this.setIcon();        
        operacion = TipoDeOperacion.ALTA;
    }

    public DetalleTransportistaGUI(Transportista transportista) {
        this.initComponents();
        this.setIcon();        
        operacion = TipoDeOperacion.ACTUALIZACION;
        transportistaModificar = transportista;
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetalleTransportistaGUI.class.getResource("/sic/icons/Truck_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void cargarTransportistaParaModificar() {
        txt_Nombre.setText(transportistaModificar.getNombre());
                if (transportistaModificar.getUbicacion() != null) {
            lblDetalleUbicacionTransportista.setText(transportistaModificar.getUbicacion().getCalle()
                    + " "
                    + transportistaModificar.getUbicacion().getNumero()
                    + (transportistaModificar.getUbicacion().getPiso() != null
                    ? ", " + transportistaModificar.getUbicacion().getPiso() + " "
                    : " ")
                    + (transportistaModificar.getUbicacion().getDepartamento() != null
                    ? transportistaModificar.getUbicacion().getDepartamento()
                    : "")
                    + (transportistaModificar.getUbicacion().getNombreLocalidad() != null
                    ? ", " + transportistaModificar.getUbicacion().getNombreLocalidad()
                    : " ")
                    + " "
                    + (transportistaModificar.getUbicacion().getNombreProvincia() != null
                    ? transportistaModificar.getUbicacion().getNombreProvincia()
                    : " "));
            this.ubicacion = transportistaModificar.getUbicacion();
        }
        txt_Telefono.setText(transportistaModificar.getTelefono());
        txt_Web.setText(transportistaModificar.getWeb());
    }

    private void limpiarYRecargarComponentes() {
        txt_Nombre.setText("");
        txt_Telefono.setText("");
        txt_Web.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPrincipal = new javax.swing.JPanel();
        lbl_Nombre = new javax.swing.JLabel();
        txt_Nombre = new javax.swing.JTextField();
        lbl_Web = new javax.swing.JLabel();
        txt_Web = new javax.swing.JTextField();
        lbl_Telefono = new javax.swing.JLabel();
        txt_Telefono = new javax.swing.JTextField();
        btnUbicacion = new javax.swing.JButton();
        lblUbicacion = new javax.swing.JLabel();
        lblDetalleUbicacionTransportista = new javax.swing.JLabel();
        btn_Guardar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panelPrincipal.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_Nombre.setForeground(java.awt.Color.red);
        lbl_Nombre.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Nombre.setText("* Nombre:");

        lbl_Web.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Web.setText("Página Web:");

        lbl_Telefono.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Telefono.setText("Teléfono:");

        btnUbicacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditMap_16x16.png"))); // NOI18N
        btnUbicacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbicacionActionPerformed(evt);
            }
        });

        lblUbicacion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUbicacion.setText("Ubicación:");

        lblDetalleUbicacionTransportista.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDetalleUbicacionTransportista.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_Nombre, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Web, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(lbl_Telefono, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblUbicacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_Web)
                    .addComponent(txt_Telefono)
                    .addComponent(txt_Nombre)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addComponent(lblDetalleUbicacionTransportista, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(btnUbicacion)))
                .addContainerGap())
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Nombre))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblUbicacion)
                    .addComponent(btnUbicacion)
                    .addComponent(lblDetalleUbicacionTransportista))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Telefono)
                    .addComponent(txt_Telefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Web)
                    .addComponent(txt_Web, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblDetalleUbicacionTransportista, txt_Telefono});

        btn_Guardar.setForeground(java.awt.Color.blue);
        btn_Guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btn_Guardar.setText("Guardar");
        btn_Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GuardarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_Guardar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Guardar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
            try {
                if (operacion == TipoDeOperacion.ALTA) {
                    Transportista transportista = new Transportista();
                    transportista.setNombre(txt_Nombre.getText().trim());
                    transportista.setTelefono(txt_Telefono.getText().trim());
                    transportista.setWeb(txt_Web.getText().trim());
                    transportista = RestClient.getRestTemplate().postForObject("/transportistas?idEmpresa="
                            + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(), transportista, Transportista.class);
                    if (this.ubicacion != null) {
                        RestClient.getRestTemplate().postForObject("/ubicaciones/transportistas/" 
                                + transportista.getIdEmpresa(), this.ubicacion, Ubicacion.class);
                    }
                    int respuesta = JOptionPane.showConfirmDialog(this,
                            "El transportista se guardó correctamente.\n¿Desea dar de alta otro transportista?",
                            "Aviso", JOptionPane.YES_NO_OPTION);
                    this.limpiarYRecargarComponentes();
                    if (respuesta == JOptionPane.NO_OPTION) {
                        this.dispose();
                    }
                }

                if (operacion == TipoDeOperacion.ACTUALIZACION) {
                    transportistaModificar.setNombre(txt_Nombre.getText().trim());
                    transportistaModificar.setTelefono(txt_Telefono.getText().trim());
                    transportistaModificar.setWeb(txt_Web.getText().trim());
                    transportistaModificar.setIdEmpresa(EmpresaActiva.getInstance().getEmpresa().getId_Empresa());
                    RestClient.getRestTemplate().put("/transportistas", transportistaModificar);
                    if (transportistaModificar.getUbicacion() == null && this.ubicacion != null) {
                        RestClient.getRestTemplate().postForObject("/ubicaciones/transportistas/" + transportistaModificar.getId_Transportista(), this.ubicacion, Ubicacion.class);
                    } else if (transportistaModificar.getUbicacion() != null && this.ubicacion != null) {
                        RestClient.getRestTemplate().put("/ubicaciones", this.ubicacion);
                    }
                    JOptionPane.showMessageDialog(this, "El transportista se modificó correctamente!",
                            "Aviso", JOptionPane.INFORMATION_MESSAGE);
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
	}//GEN-LAST:event_btn_GuardarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if (operacion == TipoDeOperacion.ACTUALIZACION) {
            this.setTitle("Modificar Transportista");
            this.cargarTransportistaParaModificar();
        } else if (operacion == TipoDeOperacion.ALTA) {
            this.setTitle("Nuevo Transportista");
        }
    }//GEN-LAST:event_formWindowOpened

    private void btnUbicacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbicacionActionPerformed
        DetalleUbicacionGUI guiDetalleUbicacion = new DetalleUbicacionGUI(this.ubicacion);
        guiDetalleUbicacion.setModal(true);
        guiDetalleUbicacion.setLocationRelativeTo(this);
        guiDetalleUbicacion.setVisible(true);
        if (guiDetalleUbicacion.getUbicacionModificada() != null) {
            this.ubicacion = guiDetalleUbicacion.getUbicacionModificada();
            lblDetalleUbicacionTransportista.setText(this.ubicacion.getCalle()
                + " "
                + this.ubicacion.getNumero()
                + (this.ubicacion.getPiso() != null
                    ? ", " + this.ubicacion.getPiso() + " "
                    : " ")
                + (this.ubicacion.getDepartamento() != null
                    ? this.ubicacion.getDepartamento()
                    : "")
                + (this.ubicacion.getNombreLocalidad() != null
                    ? ", " + this.ubicacion.getNombreLocalidad()
                    : " ")
                + " "
                + (this.ubicacion.getNombreProvincia() != null
                    ? this.ubicacion.getNombreProvincia()
                    : " "));
        }
    }//GEN-LAST:event_btnUbicacionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnUbicacion;
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JLabel lblDetalleUbicacionTransportista;
    private javax.swing.JLabel lblUbicacion;
    private javax.swing.JLabel lbl_Nombre;
    private javax.swing.JLabel lbl_Telefono;
    private javax.swing.JLabel lbl_Web;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JTextField txt_Nombre;
    private javax.swing.JTextField txt_Telefono;
    private javax.swing.JTextField txt_Web;
    // End of variables declaration//GEN-END:variables
}
