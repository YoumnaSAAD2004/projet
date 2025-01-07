package data;

import java.util.List;

/**
 * Classe permettant d'examiner et de présenter les fichiers image trouvés.
 * 
 * Cette classe propose des fonctionnalités pour :
 * - Lister les fichiers image.
 * - Afficher les informations détaillées sur chaque fichier image.
 * 
 * @author Youmna Saad et Seyda Ann
 */
public class ControleurR {

    /**
     * Collection des fichiers image à examiner.
     */
    private List<ImageFile> listeImages;

    /**
     * Constructeur pour configurer la liste des fichiers image.
     *
     * @param listeImages La liste des fichiers image à examiner.
     * @throws IllegalArgumentException Si la liste des fichiers est nulle.
     */
    public ControleurR(List<ImageFile> listeImages) {
        if (listeImages == null) {
            throw new IllegalArgumentException("La liste des fichiers ne peut pas être nulle.");
        }
        this.listeImages = listeImages;
    }

    /**
     * Liste les noms des fichiers image dans la console.
     * 
     * Affiche un message si aucun fichier n'est présent dans la liste.
     */
    public void listerNomsImages() {
        if (listeImages.isEmpty()) {
            System.out.println("Aucun fichier image détecté.");
        } else {
            System.out.println("Noms des fichiers image :");
            for (ImageFile image : listeImages) {
                System.out.println(image.getFileName());
            }
        }
    }

    /**
     * Affiche les informations détaillées pour chaque fichier image.
     * 
     * Les informations incluent : le nom, le chemin absolu, la taille en octets,
     * et la date de dernière modification.
     * Affiche un message si aucun fichier n'est présent.
     */
    public void afficherInformationsCompletes() {
        if (listeImages.isEmpty()) {
            System.out.println("Aucun fichier image détecté.");
        } else {
            System.out.println("Détails des fichiers image :");
            for (ImageFile image : listeImages) {
                System.out.println("Nom du fichier : " + image.getFileName());
                System.out.println("Chemin complet : " + image.getFilePath());
                System.out.println("Taille du fichier : " + image.getFileSize() + " octets");
                System.out.println("Date de modification : " + image.getLastModified());
                System.out.println("--------------------------------------------------");
            }
        }
    }
}
