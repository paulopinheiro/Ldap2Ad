
import br.jus.trt12.paulopinheiro.ldap2ad.view.LoginFrame;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
    public static void main (String[] args) {
        lookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFrame frame = new LoginFrame();
        frame.setVisible(true);
        frame.pack();
    }

    private static void lookAndFeel(String classe) {
        try {
            UIManager.setLookAndFeel(classe);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
