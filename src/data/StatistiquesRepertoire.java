package data;

import java.util.List;
import java.io.Serializable;

public class StatistiquesRepertoire implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Fichier> fichiersImage; // Liste des fichiers image dans le répertoire

    /**
     * Constructeur pour initialiser la liste des fichiers.
     * @param fichiersImage Liste de fichiers du répertoire.
     */
    public StatistiquesRepertoire(List<Fichier> fichiersImage) {
        if (fichiersImage == null) {
            throw new IllegalArgumentException("La liste des fichiers ne peut pas être null.");
        }
        this.fichiersImage = fichiersImage;
    }

    /**
     * Retourne le nombre total de fichiers dans le répertoire.
     * @return Nombre total de fichiers.
     */
    public int getNombreTotalFichiers() {
        return fichiersImage.size();
    }

    /**
     * Retourne le nombre total d'images valides dans le répertoire.
     * @return Nombre d'images détectées.
     */
    public int getNombreImagesValides() {
        return (int) fichiersImage.stream()
                .filter(Fichier::verifierTypeMIME) // Utilise directement la méthode de Fichier
                .count();
    }

    /**
     * Retourne le nombre d'images correspondant à un format donné (extension).
     * @param extension Extension du format (ex. ".png").
     * @return Nombre d'images correspondant.
     */
    public int getNombreImagesParFormat(String extension) {
        if (extension == null || extension.isEmpty()) {
            throw new IllegalArgumentException("L'extension ne peut pas être null ou vide.");
        }
        return (int) fichiersImage.stream()
                .filter(f -> f.getNom().toLowerCase().endsWith(extension.toLowerCase()))
                .count();
    }

    /**
     * Retourne la taille totale des fichiers dans le répertoire.
     * @return Taille totale en octets.
     */
    public long getTailleTotale() {
        return fichiersImage.stream()
                .mapToLong(f -> f.getStatistiques().getTaille())
                .sum();
    }

    /**
     * Affiche les statistiques globales pour le répertoire.
     */
    public void afficherStatistiques() {
        System.out.println("Statistiques du répertoire :");
        System.out.println("Nombre total de fichiers : " + getNombreTotalFichiers());
        System.out.println("Nombre d'images valides : " + getNombreImagesValides());
        System.out.println("Nombre d'images PNG : " + getNombreImagesParFormat(".png"));
        System.out.println("Nombre d'images JPEG : " + (getNombreImagesParFormat(".jpeg") + getNombreImagesParFormat(".jpg")));
        System.out.println("Nombre d'images WEBP : " + getNombreImagesParFormat(".webp"));
        System.out.println("Taille totale des fichiers : " + getTailleTotale() + " octets");
    }

    /**
     * Retourne une représentation textuelle des statistiques du répertoire.
     * @return Chaîne contenant les statistiques globales.
     */
    @Override
    public String toString() {
        return "StatistiquesRepertoire {" +
                "Nombre total de fichiers=" + getNombreTotalFichiers() +
                ", Nombre d'images valides=" + getNombreImagesValides() +
                ", Images PNG=" + getNombreImagesParFormat(".png") +
                ", Images JPEG=" + (getNombreImagesParFormat(".jpeg") + getNombreImagesParFormat(".jpg")) +
                ", Images WEBP=" + getNombreImagesParFormat(".webp") +
                ", Taille totale=" + getTailleTotale() + " octets" +
                '}';
    }
}
