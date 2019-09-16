package sic.vista.swing;

import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Sucursal;
import sic.modelo.SucursalActiva;
import sic.modelo.Movimiento;
import sic.modelo.Rol;
import sic.modelo.UsuarioActivo;
import sic.util.Utilidades;

public class PrincipalGUI extends JFrame {
    
    private boolean tienePermisoSegunRoles;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeFrame = new Dimension(1200, 800);
    
    public PrincipalGUI() {
        this.initComponents();
        this.setIcon();
    }

    public JDesktopPane getDesktopPane() {
        return dp_Escritorio;
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(PuntoDeVentaGUI.class.getResource("/sic/icons/SIC_24_square.png"));
        this.setIconImage(iconoVentana.getImage());
    }
    
    private void setInfoEnTituloDeVentana() {
        this.setTitle("S.I.C. OPS"
                + " - Sucursal: " + SucursalActiva.getInstance().getSucursal().getNombre()
                + " - Usuario: " + UsuarioActivo.getInstance().getUsuario().getUsername());
    }
    
    private void llamarSeleccionSucursalGUI() {
        SeleccionSucursalGUI seleccionSucursalGUI = new SeleccionSucursalGUI();
        seleccionSucursalGUI.setModal(true);
        seleccionSucursalGUI.setLocationRelativeTo(this);
        seleccionSucursalGUI.setVisible(true);
        this.setInfoEnTituloDeVentana();
    }
    
    private void cambiarEstadoDeComponentesSegunRolUsuario() {
        List<Rol> rolesDeUsuarioActivo = UsuarioActivo.getInstance().getUsuario().getRoles();
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)) {
            mnuItm_Sucursales.setVisible(true);
            mnuItm_Configuracion.setVisible(true);
            mnuItm_Usuarios.setVisible(true);
        } else {
            mnuItm_Sucursales.setVisible(false);
            mnuItm_Configuracion.setVisible(false);
            mnuItm_Usuarios.setVisible(false);
        }
        if (rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)
                || rolesDeUsuarioActivo.contains(Rol.ENCARGADO)) {
            mnu_Compras.setVisible(true);
            mnu_Stock.setVisible(true);
            mnu_Administracion.setVisible(true);
        } else {
            mnu_Compras.setVisible(false);
            mnu_Stock.setVisible(false);
            mnu_Administracion.setVisible(false);
        }
        tienePermisoSegunRoles = rolesDeUsuarioActivo.contains(Rol.ADMINISTRADOR)
                || rolesDeUsuarioActivo.contains(Rol.ENCARGADO)
                || rolesDeUsuarioActivo.contains(Rol.VENDEDOR);
    }

    private void checkCajaAbierta() {
        if (tienePermisoSegunRoles) {
            boolean existeCajaAbierta = RestClient.getRestTemplate()
                    .getForObject("/cajas/sucursales/" + SucursalActiva.getInstance().getSucursal().getIdSucursal() + "/ultima-caja-abierta",
                            boolean.class);
            if (!existeCajaAbierta) {
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_caja_ninguna_abierta"),
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dp_Escritorio = new javax.swing.JDesktopPane();
        mb_BarraMenues = new javax.swing.JMenuBar();
        mnu_Sistema = new javax.swing.JMenu();
        mnuItm_IrTPV = new javax.swing.JMenuItem();
        mnuItm_Sucursales = new javax.swing.JMenuItem();
        mnuItm_CambiarSucursal = new javax.swing.JMenuItem();
        mnuItm_Usuarios = new javax.swing.JMenuItem();
        mnuItm_CambiarUser = new javax.swing.JMenuItem();
        mnuItm_Configuracion = new javax.swing.JMenuItem();
        mnuItm_Salir = new javax.swing.JMenuItem();
        mnu_Compras = new javax.swing.JMenu();
        mnuItm_FacturasCompra = new javax.swing.JMenuItem();
        mnuItm_RecibosCompra = new javax.swing.JMenuItem();
        mnuItm_Proveedores = new javax.swing.JMenuItem();
        NotasCompras = new javax.swing.JMenu();
        NotasCreditoCompra = new javax.swing.JMenuItem();
        NotasDebitoCompra = new javax.swing.JMenuItem();
        mnu_Ventas = new javax.swing.JMenu();
        mnuItm_FacturasVenta = new javax.swing.JMenuItem();
        mnuItm_RecibosVenta = new javax.swing.JMenuItem();
        mnuItm_Pedidos = new javax.swing.JMenuItem();
        mnuItm_Clientes = new javax.swing.JMenuItem();
        NotasVenta = new javax.swing.JMenu();
        NotasCreditoVenta = new javax.swing.JMenuItem();
        NotasDebitoVenta = new javax.swing.JMenuItem();
        mnu_Administracion = new javax.swing.JMenu();
        mnuItm_Transportistas = new javax.swing.JMenuItem();
        mnuItm_FormasDePago = new javax.swing.JMenuItem();
        mnu_Cajas = new javax.swing.JMenuItem();
        mnuItmGastos = new javax.swing.JMenuItem();
        mnuItmRubros = new javax.swing.JMenuItem();
        mnuItmMedidas = new javax.swing.JMenuItem();
        mnuItmLocalidades = new javax.swing.JMenuItem();
        mnu_Stock = new javax.swing.JMenu();
        mnuItm_Productos = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        dp_Escritorio.setBackground(new java.awt.Color(204, 204, 204));
        dp_Escritorio.setToolTipText("");

        mnu_Sistema.setText("Sistema");

        mnuItm_IrTPV.setText("Ir a Punto de Venta");
        mnuItm_IrTPV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_IrTPVActionPerformed(evt);
            }
        });
        mnu_Sistema.add(mnuItm_IrTPV);

        mnuItm_Sucursales.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Sucursal_16x16.png"))); // NOI18N
        mnuItm_Sucursales.setText("Sucursales");
        mnuItm_Sucursales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_SucursalesActionPerformed(evt);
            }
        });
        mnu_Sistema.add(mnuItm_Sucursales);

        mnuItm_CambiarSucursal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/SucursalGo_16x16.png"))); // NOI18N
        mnuItm_CambiarSucursal.setText("Cambiar Sucursal");
        mnuItm_CambiarSucursal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_CambiarSucursalActionPerformed(evt);
            }
        });
        mnu_Sistema.add(mnuItm_CambiarSucursal);

        mnuItm_Usuarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Group_16x16.png"))); // NOI18N
        mnuItm_Usuarios.setText("Usuarios");
        mnuItm_Usuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_UsuariosActionPerformed(evt);
            }
        });
        mnu_Sistema.add(mnuItm_Usuarios);

        mnuItm_CambiarUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/GroupArrow_16x16.png"))); // NOI18N
        mnuItm_CambiarUser.setText("Cambiar Usuario");
        mnuItm_CambiarUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_CambiarUserActionPerformed(evt);
            }
        });
        mnu_Sistema.add(mnuItm_CambiarUser);

        mnuItm_Configuracion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Gears_16x16.png"))); // NOI18N
        mnuItm_Configuracion.setText("Configuración");
        mnuItm_Configuracion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_ConfiguracionActionPerformed(evt);
            }
        });
        mnu_Sistema.add(mnuItm_Configuracion);

        mnuItm_Salir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/LogOut_16x16.png"))); // NOI18N
        mnuItm_Salir.setText("Salir");
        mnuItm_Salir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_SalirActionPerformed(evt);
            }
        });
        mnu_Sistema.add(mnuItm_Salir);

        mb_BarraMenues.add(mnu_Sistema);

        mnu_Compras.setText("Compras");

        mnuItm_FacturasCompra.setText("Facturas");
        mnuItm_FacturasCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_FacturasCompraActionPerformed(evt);
            }
        });
        mnu_Compras.add(mnuItm_FacturasCompra);

        mnuItm_RecibosCompra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Stamp_16x16.png"))); // NOI18N
        mnuItm_RecibosCompra.setText("Recibos");
        mnuItm_RecibosCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_RecibosCompraActionPerformed(evt);
            }
        });
        mnu_Compras.add(mnuItm_RecibosCompra);

        mnuItm_Proveedores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/ProviderBag_16x16.png"))); // NOI18N
        mnuItm_Proveedores.setText("Proveedores");
        mnuItm_Proveedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_ProveedoresActionPerformed(evt);
            }
        });
        mnu_Compras.add(mnuItm_Proveedores);

        NotasCompras.setText("Notas");

        NotasCreditoCompra.setText("Credito");
        NotasCreditoCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NotasCreditoCompraActionPerformed(evt);
            }
        });
        NotasCompras.add(NotasCreditoCompra);

        NotasDebitoCompra.setText("Debito");
        NotasDebitoCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NotasDebitoCompraActionPerformed(evt);
            }
        });
        NotasCompras.add(NotasDebitoCompra);

        mnu_Compras.add(NotasCompras);

        mb_BarraMenues.add(mnu_Compras);

        mnu_Ventas.setText("Ventas");

        mnuItm_FacturasVenta.setText("Facturas");
        mnuItm_FacturasVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_FacturasVentaActionPerformed(evt);
            }
        });
        mnu_Ventas.add(mnuItm_FacturasVenta);

        mnuItm_RecibosVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Stamp_16x16.png"))); // NOI18N
        mnuItm_RecibosVenta.setText("Recibos");
        mnuItm_RecibosVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_RecibosVentaActionPerformed(evt);
            }
        });
        mnu_Ventas.add(mnuItm_RecibosVenta);

        mnuItm_Pedidos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/PedidoFacturar_16x16.png"))); // NOI18N
        mnuItm_Pedidos.setText("Pedidos");
        mnuItm_Pedidos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_PedidosActionPerformed(evt);
            }
        });
        mnu_Ventas.add(mnuItm_Pedidos);

        mnuItm_Clientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Client_16x16.png"))); // NOI18N
        mnuItm_Clientes.setText("Clientes");
        mnuItm_Clientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_ClientesActionPerformed(evt);
            }
        });
        mnu_Ventas.add(mnuItm_Clientes);

        NotasVenta.setText("Notas");

        NotasCreditoVenta.setText("Credito");
        NotasCreditoVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NotasCreditoVentaActionPerformed(evt);
            }
        });
        NotasVenta.add(NotasCreditoVenta);

        NotasDebitoVenta.setText("Debito");
        NotasDebitoVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NotasDebitoVentaActionPerformed(evt);
            }
        });
        NotasVenta.add(NotasDebitoVenta);

        mnu_Ventas.add(NotasVenta);

        mb_BarraMenues.add(mnu_Ventas);

        mnu_Administracion.setText("Administración");

        mnuItm_Transportistas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Truck_16x16.png"))); // NOI18N
        mnuItm_Transportistas.setText("Transportistas");
        mnuItm_Transportistas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_TransportistasActionPerformed(evt);
            }
        });
        mnu_Administracion.add(mnuItm_Transportistas);

        mnuItm_FormasDePago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Wallet_16x16.png"))); // NOI18N
        mnuItm_FormasDePago.setText("Formas de Pago");
        mnuItm_FormasDePago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_FormasDePagoActionPerformed(evt);
            }
        });
        mnu_Administracion.add(mnuItm_FormasDePago);

        mnu_Cajas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Caja_16x16.png"))); // NOI18N
        mnu_Cajas.setText("Cajas");
        mnu_Cajas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnu_CajasActionPerformed(evt);
            }
        });
        mnu_Administracion.add(mnu_Cajas);

        mnuItmGastos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Coins_16x16.png"))); // NOI18N
        mnuItmGastos.setText("Gastos");
        mnuItmGastos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItmGastosActionPerformed(evt);
            }
        });
        mnu_Administracion.add(mnuItmGastos);

        mnuItmRubros.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Block.png"))); // NOI18N
        mnuItmRubros.setText("Rubros");
        mnuItmRubros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItmRubrosActionPerformed(evt);
            }
        });
        mnu_Administracion.add(mnuItmRubros);

        mnuItmMedidas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Ruler_16x16.png"))); // NOI18N
        mnuItmMedidas.setText("Medidas");
        mnuItmMedidas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItmMedidasActionPerformed(evt);
            }
        });
        mnu_Administracion.add(mnuItmMedidas);

        mnuItmLocalidades.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Map_16x16.png"))); // NOI18N
        mnuItmLocalidades.setText("Localidades");
        mnuItmLocalidades.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItmLocalidadesActionPerformed(evt);
            }
        });
        mnu_Administracion.add(mnuItmLocalidades);

        mb_BarraMenues.add(mnu_Administracion);

        mnu_Stock.setText("Stock");

        mnuItm_Productos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Product_16x16.png"))); // NOI18N
        mnuItm_Productos.setText("Productos");
        mnuItm_Productos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_ProductosActionPerformed(evt);
            }
        });
        mnu_Stock.add(mnuItm_Productos);

        mb_BarraMenues.add(mnu_Stock);

        setJMenuBar(mb_BarraMenues);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dp_Escritorio, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dp_Escritorio, javax.swing.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuItm_SalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_SalirActionPerformed
        this.formWindowClosing(null);
    }//GEN-LAST:event_mnuItm_SalirActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int respuesta = JOptionPane.showConfirmDialog(this,
                ResourceBundle.getBundle("Mensajes").getString("mensaje_confirmacion_salir_sistema"),
                ResourceBundle.getBundle("Mensajes").getString("mensaje_salir"), JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                RestClient.getRestTemplate().put("/logout", null);
                this.dispose();
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosing

    private void mnuItm_SucursalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_SucursalesActionPerformed
        SucursalesGUI sucursalesGUI = new SucursalesGUI();
        sucursalesGUI.setModal(true);
        sucursalesGUI.setLocationRelativeTo(this);
        sucursalesGUI.setVisible(true);
        Utilidades.cerrarTodasVentanas(dp_Escritorio);
        this.llamarSeleccionSucursalGUI();
    }//GEN-LAST:event_mnuItm_SucursalesActionPerformed

    private void mnuItm_CambiarUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_CambiarUserActionPerformed
        try {
            RestClient.getRestTemplate().put("/logout", null);
            this.dispose();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        UsuarioActivo.getInstance().setToken("");
        UsuarioActivo.getInstance().setUsuario(null);
        this.dispose();
        new LoginGUI().setVisible(true);
    }//GEN-LAST:event_mnuItm_CambiarUserActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setLocationRelativeTo(null);
        this.setSize(sizeFrame);
        this.setExtendedState(MAXIMIZED_BOTH);
        if (UsuarioActivo.getInstance().getUsuario().getIdSucursalPredeterminada()== 0) {
            this.llamarSeleccionSucursalGUI();
        } else {
            try {
                Sucursal sucursal = RestClient.getRestTemplate()
                        .getForObject("/sucursales/" + UsuarioActivo.getInstance().getUsuario().getIdSucursalPredeterminada(),
                                Sucursal.class);
                SucursalActiva.getInstance().setSucursal(sucursal);
                this.setInfoEnTituloDeVentana();
            } catch (RestClientResponseException ex) {
                this.llamarSeleccionSucursalGUI();
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        this.cambiarEstadoDeComponentesSegunRolUsuario();                
        this.checkCajaAbierta();        
    }//GEN-LAST:event_formWindowOpened
        
    private void mnuItm_UsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_UsuariosActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), UsuariosGUI.class);
        if (gui == null) {
            UsuariosGUI usuariosGUI = new UsuariosGUI();
            usuariosGUI.setLocation(getDesktopPane().getWidth() / 2 - usuariosGUI.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - usuariosGUI.getHeight() / 2);
            getDesktopPane().add(usuariosGUI);
            usuariosGUI.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_UsuariosActionPerformed

    private void mnuItm_ProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_ProveedoresActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), ProveedoresGUI.class);
        if (gui == null) {
            gui = new ProveedoresGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_ProveedoresActionPerformed

    private void mnuItm_FacturasCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_FacturasCompraActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), FacturasCompraGUI.class);
        if (gui == null) {
            gui = new FacturasCompraGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_FacturasCompraActionPerformed

    private void mnuItm_TransportistasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_TransportistasActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), TransportistasGUI.class);
        if (gui == null) {
            gui = new TransportistasGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_TransportistasActionPerformed

    private void mnuItm_ProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_ProductosActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), ProductosGUI.class);
        if (gui == null) {
            ProductosGUI productosGUI = new ProductosGUI();
            productosGUI.setLocation(getDesktopPane().getWidth() / 2 - productosGUI.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - productosGUI.getHeight() / 2);
            getDesktopPane().add(productosGUI);
            productosGUI.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_ProductosActionPerformed

    private void mnuItm_ClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_ClientesActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), ClientesGUI.class);
        if (gui == null) {
            gui = new ClientesGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_ClientesActionPerformed

    private void mnuItm_CambiarSucursalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_CambiarSucursalActionPerformed
        Utilidades.cerrarTodasVentanas(dp_Escritorio);
        this.llamarSeleccionSucursalGUI();
    }//GEN-LAST:event_mnuItm_CambiarSucursalActionPerformed

    private void mnuItm_FormasDePagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_FormasDePagoActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), FormasDePagoGUI.class);
        if (gui == null) {
            gui = new FormasDePagoGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_FormasDePagoActionPerformed

    private void mnuItm_IrTPVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_IrTPVActionPerformed
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
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_IrTPVActionPerformed

    private void mnuItm_FacturasVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_FacturasVentaActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), FacturasVentaGUI.class);
        if (gui == null) {
            gui = new FacturasVentaGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_FacturasVentaActionPerformed

    private void mnuItm_ConfiguracionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_ConfiguracionActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), ConfiguracionSucursalGUI.class);
        if (gui == null) {
            gui = new ConfiguracionSucursalGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_ConfiguracionActionPerformed

    private void mnuItm_PedidosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_PedidosActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), PedidosGUI.class);
        if (gui == null) {
            gui = new PedidosGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_PedidosActionPerformed

    private void mnu_CajasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnu_CajasActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), CajasGUI.class);
        if (gui == null) {
            gui = new CajasGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnu_CajasActionPerformed

    private void mnuItm_RecibosVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_RecibosVentaActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), RecibosVentaGUI.class);
        if (gui == null) {
            gui = new RecibosVentaGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_RecibosVentaActionPerformed

    private void mnuItm_RecibosCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_RecibosCompraActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), RecibosCompraGUI.class);
        if (gui == null) {
            gui = new RecibosCompraGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_RecibosCompraActionPerformed

    private void mnuItmRubrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItmRubrosActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), DetalleRubroGUI.class);
        if (gui == null) {
            gui = new DetalleRubroGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItmRubrosActionPerformed

    private void mnuItmMedidasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItmMedidasActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), DetalleMedidaGUI.class);
        if (gui == null) {
            gui = new DetalleMedidaGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItmMedidasActionPerformed

    private void mnuItmLocalidadesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItmLocalidadesActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), LocalidadesGUI.class);
        if (gui == null) {
            gui = new LocalidadesGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItmLocalidadesActionPerformed

    private void mnuItmGastosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItmGastosActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), GastosGUI.class);
        if (gui == null) {
            gui = new GastosGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItmGastosActionPerformed

    private void NotasCreditoVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NotasCreditoVentaActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), NotasCreditoVentaGUI.class);
        if (gui == null) {
            gui = new NotasCreditoVentaGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_NotasCreditoVentaActionPerformed

    private void NotasDebitoVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NotasDebitoVentaActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), NotasDebitoVentaGUI.class);
        if (gui == null) {
            gui = new NotasDebitoVentaGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_NotasDebitoVentaActionPerformed

    private void NotasCreditoCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NotasCreditoCompraActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), NotasCreditoCompraGUI.class);
        if (gui == null) {
            gui = new NotasCreditoCompraGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_NotasCreditoCompraActionPerformed

    private void NotasDebitoCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NotasDebitoCompraActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), NotasDebitoCompraGUI.class);
        if (gui == null) {
            gui = new NotasDebitoCompraGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            try {
                gui.setSelected(true);
            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_NotasDebitoCompraActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu NotasCompras;
    private javax.swing.JMenuItem NotasCreditoCompra;
    private javax.swing.JMenuItem NotasCreditoVenta;
    private javax.swing.JMenuItem NotasDebitoCompra;
    private javax.swing.JMenuItem NotasDebitoVenta;
    private javax.swing.JMenu NotasVenta;
    private javax.swing.JDesktopPane dp_Escritorio;
    private javax.swing.JMenuBar mb_BarraMenues;
    private javax.swing.JMenuItem mnuItmGastos;
    private javax.swing.JMenuItem mnuItmLocalidades;
    private javax.swing.JMenuItem mnuItmMedidas;
    private javax.swing.JMenuItem mnuItmRubros;
    private javax.swing.JMenuItem mnuItm_CambiarSucursal;
    private javax.swing.JMenuItem mnuItm_CambiarUser;
    private javax.swing.JMenuItem mnuItm_Clientes;
    private javax.swing.JMenuItem mnuItm_Configuracion;
    private javax.swing.JMenuItem mnuItm_FacturasCompra;
    private javax.swing.JMenuItem mnuItm_FacturasVenta;
    private javax.swing.JMenuItem mnuItm_FormasDePago;
    private javax.swing.JMenuItem mnuItm_IrTPV;
    private javax.swing.JMenuItem mnuItm_Pedidos;
    private javax.swing.JMenuItem mnuItm_Productos;
    private javax.swing.JMenuItem mnuItm_Proveedores;
    private javax.swing.JMenuItem mnuItm_RecibosCompra;
    private javax.swing.JMenuItem mnuItm_RecibosVenta;
    private javax.swing.JMenuItem mnuItm_Salir;
    private javax.swing.JMenuItem mnuItm_Sucursales;
    private javax.swing.JMenuItem mnuItm_Transportistas;
    private javax.swing.JMenuItem mnuItm_Usuarios;
    private javax.swing.JMenu mnu_Administracion;
    private javax.swing.JMenuItem mnu_Cajas;
    private javax.swing.JMenu mnu_Compras;
    private javax.swing.JMenu mnu_Sistema;
    private javax.swing.JMenu mnu_Stock;
    private javax.swing.JMenu mnu_Ventas;
    // End of variables declaration//GEN-END:variables
}
