package Old_source;

/*

 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JDialog;
import javax.swing.JProgressBar;

/**
 *
 * @author Silvio
 */
public class Progress extends JDialog {
    public Progress() {
        this.setTitle("Aguarde...");
        setModal(false);
        JProgressBar progress = new JProgressBar();
        progress.setIndeterminate(true);
        progress.setSize(200,30);
        getContentPane().add(progress);
        pack();
        Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((tela.width - this.getSize().width) / 2,
             (tela.height - this.getSize().height) / 2);
    }
}
