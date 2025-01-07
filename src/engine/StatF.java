package engine;

import java.io.IOException;
import data.ImageFile;

/**
 * Classe pour générer et afficher des détails spécifiques sur un fichier.
 * 
 * Cette classe fournit des méthodes pour afficher des informations comme :
 * - Le type MIME.
 * - La taille du fichier.
 * - La date de modification.
 * 
 * @author Youmna Saad et Seyda Ann
 */
public class StatF {

    private ImageFile imageFile;

    /**
     * Constructeur pour initialiser l'objet avec un fichier.
     * 
     * @param imageFile Le fichier dont les détails seront affichés.
     */
    public StatF(ImageFile imageFile) {
        this.imageFile = imageFile;
    }

    /**
     * Affiche les détails du fichier, notamment :
     * - Nom.
     * - Chemin absolu.
     * - Type MIME.
     * - Taille en octets.
     * - Dernière date de modification.
     */
    public void displayDetails() {
        try {
            System.out.println("Détails du fichier :");
            System.out.println("Nom du fichier : " + imageFile.getFileName());
            System.out.println("Chemin complet : " + imageFile.getFilePath());
            System.out.println("Type MIME : " + imageFile.getMimeType());
            System.out.println("Taille : " + imageFile.getFileSize() + " octets");
            System.out.println("Dernière modification : " + imageFile.getLastModified());
            System.out.println("--------------------------------------------------");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'affichage des détails du fichier : " + e.getMessage());
        }
    }
}
