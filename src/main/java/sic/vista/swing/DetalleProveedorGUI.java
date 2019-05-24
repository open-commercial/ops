package sic.vista.swing;

import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.CategoriaIVA;
import sic.modelo.EmpresaActiva;
import sic.modelo.Proveedor;
import sic.modelo.Rol;
import sic.modelo.TipoDeOperacion;
import sic.modelo.Ubicacion;
import sic.modelo.UsuarioActivo;

public class DetalleProveedorGUI extends JDialog {

    private Proveedor proveedorModificar;
    private Proveedor proveedorNuevo;
    private Ubicacion ubicacion;
    private final TipoDeOperacion operacion;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public DetalleProveedorGUI() {
        this.initComponents();
        this.setIcon();
        operacion = TipoDeOperacion.ALTA;
    }

    public DetalleProveedorGUI(Proveedor prov) {
        this.initComponents();
        this.setIcon();
        operacion = TipoDeOperacion.ACTUALIZACION;
        proveedorModificar = prov;
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(DetalleProveedorGUI.class.getResource("/sic/icons/ProviderBag_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void cargarProveedorParaModificar() {
        txtRazonSocial.setText(proveedorModificar.getRazonSocial());
        txtIdFiscal.setValue(proveedorModificar.getIdFiscal());
        cmbCategoriaIVA.setSelectedItem(proveedorModificar.getCategoriaIVA());
        if (proveedorModificar.getUbicacion() != null) {
            lblDetalleUbicacionProveedor.setText(proveedorModificar.getUbicacion().toString());
            this.ubicacion = proveedorModificar.getUbicacion();
        }
        txtTelPrimario.setText(proveedorModificar.getTelPrimario());
        txtTelSecundario.setText(proveedorModificar.getTelSecundario());
        txtContacto.setText(proveedorModificar.getContacto());
        txtEmail.setText(proveedorModificar.getEmail());
        txtWeb.setText(proveedorModificar.getWeb());

    }

    private void limpiarYRecargarComponentes() {
        txtRazonSocial.setText("");
        txtIdFiscal.setText("");
        txtTelPrimario.setText("");
        txtTelSecundario.setText("");
        txtContacto.setText("");
        txtEmail.setText("");
        txtWeb.setText("");
        this.cargarComboBoxCondicionesIVA();
    }

    private void cargarComboBoxCondicionesIVA() {
        cmbCategoriaIVA.removeAllItems();
        for (CategoriaIVA c : CategoriaIVA.values()) {
            cmbCategoriaIVA.addItem(c);
        }
    }

    private void cambiarEstadoDeComponentesSegunRolUsuario() {
        List<Rol> rolesDeUsuarioActivo = UsuarioActivo.getInstance().getUsuario().getRoles();
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)
                || rolesDeUsuarioActivo.contains(Rol.ENCARGADO)) {
            btnUbicacion.setEnabled(true);
            btnUbicacion.setEnabled(true);
        } else {
            btnUbicacion.setEnabled(false);
            btnUbicacion.setEnabled(false);
        }
    }

    public Proveedor getProveedorCreado() {
        return proveedorNuevo;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_Guardar = new javax.swing.JButton();
        panelPrincipal = new javax.swing.JPanel();
        lblRazonSocial = new javax.swing.JLabel();
        txtRazonSocial = new javax.swing.JTextField();
        lblIdFiscal = new javax.swing.JLabel();
        lblCondicionIVA = new javax.swing.JLabel();
        cmbCategoriaIVA = new javax.swing.JComboBox();
        lblTelPrimario = new javax.swing.JLabel();
        txtTelSecundario = new javax.swing.JTextField();
        lblTelSecundario = new javax.swing.JLabel();
        lblContacto = new javax.swing.JLabel();
        txtContacto = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblWeb = new javax.swing.JLabel();
        txtWeb = new javax.swing.JTextField();
        txtIdFiscal = new javax.swing.JFormattedTextField();
        lblUbicacion = new javax.swing.JLabel();
        btnUbicacion = new javax.swing.JButton();
        lblDetalleUbicacionProveedor = new javax.swing.JLabel();
        txtTelPrimario = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nuevo Proveedor");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        btn_Guardar.setForeground(java.awt.Color.blue);
        btn_Guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btn_Guardar.setText("Guardar");
        btn_Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GuardarActionPerformed(evt);
            }
        });

        panelPrincipal.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblRazonSocial.setForeground(java.awt.Color.red);
        lblRazonSocial.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRazonSocial.setText("* Razón Social:");

        lblIdFiscal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblIdFiscal.setText("ID Fiscal:");

        lblCondicionIVA.setForeground(java.awt.Color.red);
        lblCondicionIVA.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCondicionIVA.setText("* Condición IVA:");

        cmbCategoriaIVA.setMaximumRowCount(5);

        lblTelPrimario.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTelPrimario.setText("Teléfono #1:");

        lblTelSecundario.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTelSecundario.setText("Teléfono #2:");

        lblContacto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblContacto.setText("Contacto:");

        lblEmail.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblEmail.setText("Email:");

        lblWeb.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblWeb.setText("Página Web:");

        txtIdFiscal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#"))));
        txtIdFiscal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtIdFiscalFocusLost(evt);
            }
        });

        lblUbicacion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUbicacion.setText("Ubicación:");

        btnUbicacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditMap_16x16.png"))); // NOI18N
        btnUbicacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbicacionActionPerformed(evt);
            }
        });

        lblDetalleUbicacionProveedor.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDetalleUbicacionProveedor.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lblIdFiscal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblRazonSocial, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTelPrimario, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblUbicacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTelSecundario, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblContacto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblEmail, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblWeb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCondicionIVA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRazonSocial, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtIdFiscal, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cmbCategoriaIVA, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                        .addComponent(lblDetalleUbicacionProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btnUbicacion))
                    .addComponent(txtTelPrimario, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtTelSecundario, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtContacto, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtEmail)
                    .addComponent(txtWeb, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblRazonSocial)
                    .addComponent(txtRazonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblIdFiscal)
                    .addComponent(txtIdFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbCategoriaIVA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCondicionIVA))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnUbicacion)
                    .addComponent(lblUbicacion)
                    .addComponent(lblDetalleUbicacionProveedor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTelPrimario)
                    .addComponent(txtTelPrimario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblTelSecundario)
                    .addComponent(txtTelSecundario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblContacto)
                    .addComponent(txtContacto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblEmail)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblWeb)
                    .addComponent(txtWeb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelPrincipalLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnUbicacion, lblDetalleUbicacionProveedor});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btn_Guardar)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Guardar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
        try {
            if (operacion == TipoDeOperacion.ALTA) {
                Proveedor proveedor = new Proveedor();
                proveedor.setRazonSocial(txtRazonSocial.getText().trim());
                proveedor.setIdFiscal((Long) txtIdFiscal.getValue());
                proveedor.setCategoriaIVA((CategoriaIVA) cmbCategoriaIVA.getSelectedItem());
                proveedor.setTelPrimario(txtTelPrimario.getText().trim());
                proveedor.setTelSecundario(txtTelSecundario.getText().trim());
                proveedor.setContacto(txtContacto.getText().trim());
                proveedor.setEmail(txtEmail.getText().trim());
                proveedor.setWeb(txtWeb.getText().trim());
                proveedor.setIdEmpresa((EmpresaActiva.getInstance().getEmpresa()).getId_Empresa());
                if (this.ubicacion != null) {
                    proveedor.setUbicacion(this.ubicacion);
                }
                RestClient.getRestTemplate().postForObject("/proveedores", proveedor, Proveedor.class);
                int respuesta = JOptionPane.showConfirmDialog(this,
                        "El proveedor se guardó correctamente.\n¿Desea dar de alta otro proveedor?",
                        "Aviso", JOptionPane.YES_NO_OPTION);
                this.limpiarYRecargarComponentes();
                if (respuesta == JOptionPane.NO_OPTION) {
                    this.dispose();
                }
            }
            if (operacion == TipoDeOperacion.ACTUALIZACION) {
                proveedorModificar.setRazonSocial(txtRazonSocial.getText().trim());
                proveedorModificar.setIdFiscal((Long) txtIdFiscal.getValue());
                proveedorModificar.setCategoriaIVA((CategoriaIVA) cmbCategoriaIVA.getSelectedItem());
                proveedorModificar.setTelPrimario(txtTelPrimario.getText().trim());
                proveedorModificar.setTelSecundario(txtTelSecundario.getText().trim());
                proveedorModificar.setContacto(txtContacto.getText().trim());
                proveedorModificar.setEmail(txtEmail.getText().trim());
                proveedorModificar.setWeb(txtWeb.getText().trim());
                if (this.ubicacion != null) {
                    proveedorModificar.setUbicacion(this.ubicacion);
                }
                proveedorModificar.setIdEmpresa((EmpresaActiva.getInstance().getEmpresa()).getId_Empresa());
                RestClient.getRestTemplate().put("/proveedores", proveedorModificar);
                JOptionPane.showMessageDialog(this, "El proveedor se modificó correctamente.",
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
        this.cargarComboBoxCondicionesIVA();
        this.cambiarEstadoDeComponentesSegunRolUsuario();
        if (operacion == TipoDeOperacion.ACTUALIZACION) {
            this.setTitle("Modificar Proveedor");
            this.cargarProveedorParaModificar();
        } else if (operacion == TipoDeOperacion.ALTA) {
            this.setTitle("Nuevo Proveedor");
        }
    }//GEN-LAST:event_formWindowOpened

    private void txtIdFiscalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIdFiscalFocusLost
        if (txtIdFiscal.getText().equals("")) {
            txtIdFiscal.setValue(null);
        }
    }//GEN-LAST:event_txtIdFiscalFocusLost

    private void btnUbicacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbicacionActionPerformed
        DetalleUbicacionGUI guiDetalleUbicacion = new DetalleUbicacionGUI(this.ubicacion, "Ubicación Proveedor");
        guiDetalleUbicacion.setModal(true);
        guiDetalleUbicacion.setLocationRelativeTo(this);
        guiDetalleUbicacion.setVisible(true);
        if (guiDetalleUbicacion.getUbicacionModificada() != null) {
            this.ubicacion = guiDetalleUbicacion.getUbicacionModificada();
            lblDetalleUbicacionProveedor.setText(this.ubicacion.toString());
        }
    }//GEN-LAST:event_btnUbicacionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnUbicacion;
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JComboBox cmbCategoriaIVA;
    private javax.swing.JLabel lblCondicionIVA;
    private javax.swing.JLabel lblContacto;
    private javax.swing.JLabel lblDetalleUbicacionProveedor;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblIdFiscal;
    private javax.swing.JLabel lblRazonSocial;
    private javax.swing.JLabel lblTelPrimario;
    private javax.swing.JLabel lblTelSecundario;
    private javax.swing.JLabel lblUbicacion;
    private javax.swing.JLabel lblWeb;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JTextField txtContacto;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JFormattedTextField txtIdFiscal;
    private javax.swing.JTextField txtRazonSocial;
    private javax.swing.JTextField txtTelPrimario;
    private javax.swing.JTextField txtTelSecundario;
    private javax.swing.JTextField txtWeb;
    // End of variables declaration//GEN-END:variables
}
