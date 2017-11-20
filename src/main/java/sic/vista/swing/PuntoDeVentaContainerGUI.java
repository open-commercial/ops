package sic.vista.swing;

import java.awt.Toolkit;
import java.beans.PropertyVetoException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sic.modelo.EmpresaActiva;
import sic.modelo.UsuarioActivo;

public class PuntoDeVentaContainerGUI extends JDialog {
    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public PuntoDeVentaContainerGUI() {        
        this.initComponents();
        ImageIcon iconoVentana = new ImageIcon(PuntoDeVentaGUI.class.getResource("/sic/icons/SIC_24_square.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        desktopPaneContainer = new javax.swing.JDesktopPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("S.I.C. Punto de Venta "
            + ResourceBundle.getBundle("Mensajes").getString("version"));
        setMinimumSize(new java.awt.Dimension(1000, 700));
        setPreferredSize(new java.awt.Dimension(1000, 700));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        desktopPaneContainer.setPreferredSize(new java.awt.Dimension(880, 600));

        javax.swing.GroupLayout desktopPaneContainerLayout = new javax.swing.GroupLayout(desktopPaneContainer);
        desktopPaneContainer.setLayout(desktopPaneContainerLayout);
        desktopPaneContainerLayout.setHorizontalGroup(
            desktopPaneContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 784, Short.MAX_VALUE)
        );
        desktopPaneContainerLayout.setVerticalGroup(
            desktopPaneContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 465, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desktopPaneContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 784, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desktopPaneContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        PuntoDeVentaGUI gui_puntoDeVenta = new PuntoDeVentaGUI();
        gui_puntoDeVenta.setLocation(desktopPaneContainer.getWidth() / 2 - gui_puntoDeVenta.getWidth() / 2,
                desktopPaneContainer.getHeight() / 2 - gui_puntoDeVenta.getHeight() / 2);
        desktopPaneContainer.add(gui_puntoDeVenta);
        gui_puntoDeVenta.setVisible(true);
        try {
            gui_puntoDeVenta.setSelected(true);
            gui_puntoDeVenta.setMaximum(true);
        } catch (PropertyVetoException ex) {
            String msjError = "No se pudo seleccionar la ventana requerida.";
            LOGGER.error(msjError + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(desktopPaneContainer, msjError, "Error", JOptionPane.ERROR_MESSAGE);
        }
        this.setTitle("Empresa : " + EmpresaActiva.getInstance().getEmpresa().getNombre() + " - Usuario: " + UsuarioActivo.getInstance().getUsuario().getNombre());
    }//GEN-LAST:event_formWindowOpened

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane desktopPaneContainer;
    // End of variables declaration//GEN-END:variables
}
