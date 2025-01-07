package partieGui;

import javax.swing.SwingUtilities;

/**
 * Classe principale pour lancer l'interface graphique de l'application.
 *
 * Cette classe contient le point d'entrée principal pour exécuter l'application
 * avec une interface utilisateur basée sur Swing.
 *
 * @author Youmna Saad et Seyda Ann
 */
public class MainGui {

    /**
     * Point d'entrée principal de l'application.
     *
     * Cette méthode initialise et lance l'interface graphique de l'application.
     *
     * @param args Les arguments de ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GuiGraphique gui = new GuiGraphique();
            gui.setVisible(true);
        });
    }
}
