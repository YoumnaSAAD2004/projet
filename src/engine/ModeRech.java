package engine;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import data.ImageFile;

/**
 * Classe pour effectuer des recherches sur des fichiers image.
 * 
 * Cette classe fournit des méthodes pour rechercher des fichiers image par
 * leur nom ou par l'année de leur dernière modification. Elle peut également
 * afficher les résultats de recherche à l'écran.
 * 
 * @author Youmna Saad et Seyda Ann
 */
public class ModeRech {

    private List<ImageFile> imageFiles; // Liste des fichiers image disponibles

    /**
     * Constructeur pour initialiser la liste des fichiers image à analyser.
     *
     * @param imageFiles La liste des fichiers image.
     * @throws IllegalArgumentException Si la liste est nulle.
     */
    public ModeRech(List<ImageFile> imageFiles) {
        if (imageFiles == null) {
            throw new IllegalArgumentException("La liste des fichiers image ne peut pas être nulle.");
        }
        this.imageFiles = imageFiles;
    }

    /**
     * Recherche des images par nom ou partie du nom.
     *
     * @param partialName Nom complet ou fragment de nom à rechercher.
     * @return Une liste de fichiers correspondant aux critères.
     */
    public List<ImageFile> findByName(String partialName) {
        return imageFiles.stream()
                .filter(file -> {
                    String name = file.getFileName().toLowerCase();
                    return name.contains(partialName.toLowerCase()) ||
                           (name.contains(partialName.toLowerCase()) &&
                           (name.endsWith(".jpeg") || name.endsWith(".jpg")));
                })
                .collect(Collectors.toList());
    }

    /**
     * Recherche des images par année de dernière modification.
     *
     * @param year Année de dernière modification.
     * @return Une liste de fichiers correspondant à l'année donnée.
     */
    public List<ImageFile> findByYear(int year) {
        return imageFiles.stream()
                .filter(file -> {
                    Date lastModified = file.getLastModified();
                    return lastModified.getYear() + 1900 == year; // Ajout de 1900 pour une année correcte
                })
                .collect(Collectors.toList());
    }

    /**
     * Affiche les résultats de la recherche.
     *
     * @param results Liste des fichiers correspondant aux critères de recherche.
     */
    public void displayResults(List<ImageFile> results) {
        if (results.isEmpty()) {
            System.out.println("Aucun fichier ne correspond aux critères de recherche.");
        } else {
            System.out.println("Fichiers trouvés :");
            results.forEach(file -> 
                System.out.println("- " + file.getFileName() + " (Chemin : " + file.getFilePath() + ")"));
        }
    }
}
