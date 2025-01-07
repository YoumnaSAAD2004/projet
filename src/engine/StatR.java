package engine;

import java.util.List;
import data.ImageFile;

/**
 * Classe pour analyser et générer des rapports statistiques sur les fichiers image d'un dossier.
 * 
 * Cette classe permet de :
 * - Calculer le total des fichiers présents.
 * - Identifier et compter les différents formats d'images.
 * - Lister les fichiers qui ne sont pas des images.
 * 
 * @author Youmna Saad et Seyda Ann
 */
public class StatR {

    /**
     * Liste des fichiers image analysés dans le répertoire.
     */
    private List<ImageFile> imageFiles;

    /**
     * Constructeur pour initialiser la liste des fichiers image à analyser.
     *
     * @param imageFiles La liste des fichiers image à analyser.
     * @throws IllegalArgumentException Si la liste est vide ou null.
     */
    public StatR(List<ImageFile> imageFiles) {
        if (imageFiles == null || imageFiles.isEmpty()) {
            throw new IllegalArgumentException("La liste des fichiers ne peut pas être vide ou nulle.");
        }
        this.imageFiles = imageFiles;
    }

    /**
     * Retourne le nombre total de fichiers dans le répertoire.
     *
     * @return Le nombre total de fichiers.
     */
    public int countAllFiles() {
        return imageFiles.size();
    }

    /**
     * Retourne le nombre total de fichiers image.
     *
     * @return Le nombre total de fichiers image.
     */
    public int countImages() {
        return (int) imageFiles.stream()
                .filter(f -> f.getFileName().endsWith(".png") || f.getFileName().endsWith(".jpeg") || f.getFileName().endsWith(".webp"))
                .count();
    }

    /**
     * Compte les fichiers d'un format d'image spécifique, avec gestion des extensions synchronisées (.jpeg et .jpg).
     *
     * @param extension L'extension du format d'image (par exemple : ".png").
     * @return Le nombre de fichiers du format donné.
     */
    public int countImagesByFormat(String extension) {
        return (int) imageFiles.stream()
                .filter(f -> {
                    String fileName = f.getFileName().toLowerCase();
                    if (extension.equals(".jpeg") || extension.equals(".jpg")) {
                        return fileName.endsWith(".jpeg") || fileName.endsWith(".jpg");
                    }
                    return fileName.endsWith(extension);
                })
                .count();
    }

    /**
     * Génère un rapport statistique global sur les fichiers du répertoire.
     */
    public void generateReport() {
        System.out.println("Rapport statistique du dossier :");
        System.out.println("Total de fichiers : " + countAllFiles());
        System.out.println("Total d'images : " + countImages());
        System.out.println("Images PNG : " + countImagesByFormat(".png"));
        System.out.println("Images JPEG : " + countImagesByFormat(".jpeg"));
        System.out.println("Images WEBP : " + countImagesByFormat(".webp"));
    }

    /**
     * Liste les fichiers non reconnus comme images.
     */
    public void listNonImageFiles() {
        System.out.println("Fichiers non reconnus comme images :");
        imageFiles.stream()
                .filter(f -> !f.getFileName().endsWith(".png") && !f.getFileName().endsWith(".jpeg") && !f.getFileName().endsWith(".webp"))
                .forEach(f -> System.out.println("Nom : " + f.getFileName() + " - Format non supporté"));
    }
}
