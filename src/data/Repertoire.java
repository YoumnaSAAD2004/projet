package data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import engine.StatR;
import engine.StatF;

/**
 * Classe pour gérer et explorer les fichiers image dans des répertoires.
 * 
 * Cette classe permet de rechercher des fichiers image dans un répertoire donné,
 * d'obtenir leurs détails et de générer des statistiques sur leur contenu.
 * 
 * @author Youmna Saad et Seyda Ann
 */
public class Repertoire {

    /**
     * Liste des fichiers image détectés.
     */
    private List<ImageFile> imageFiles;

    /**
     * Constructeur par défaut qui initialise une liste vide.
     */
    public Repertoire() {
        this.imageFiles = new ArrayList<>();
    }

    /**
     * Parcourt un répertoire et ses sous-répertoires pour collecter les fichiers image.
     *
     * Cette méthode filtre les fichiers valides basés sur leur extension et leur type MIME.
     *
     * @param directory Le répertoire à analyser.
     * @throws IOException Si le répertoire est invalide ou inaccessible.
     */
    public void scanDirectory(File directory) throws IOException {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            throw new IOException("Répertoire invalide ou inexistant.");
        }

        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanDirectory(file); // Récursion pour les sous-dossiers
                } else if (file.isFile() && isValidImage(file)) {
                    imageFiles.add(new ImageFile(file)); // Ajout à la liste des fichiers image
                }
            }
        }
    }

    /**
     * Vérifie si un fichier est une image valide (PNG, JPEG, JPG, WEBP).
     *
     * @param file Le fichier à analyser.
     * @return true si le fichier est une image valide, sinon false.
     */
    private boolean isValidImage(File file) {
        try {
            String fileName = file.getName().toLowerCase();
            boolean validExtension = fileName.endsWith(".png") || fileName.endsWith(".jpeg") || 
                                      fileName.endsWith(".jpg") || fileName.endsWith(".webp");

            String mimeType = Files.probeContentType(file.toPath());
            boolean validMimeType = "image/png".equals(mimeType) || "image/jpeg".equals(mimeType) || "image/webp".equals(mimeType);

            return validExtension && validMimeType;
        } catch (IOException e) {
            System.err.println("Erreur lors de la vérification : " + file.getName() + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupère un fichier image par son nom dans la liste des fichiers détectés.
     *
     * @param fileName Le nom du fichier recherché.
     * @return L'objet ImageFile correspondant, ou null si non trouvé.
     */
    public ImageFile findFileByName(String fileName) {
        for (ImageFile imageFile : imageFiles) {
            if (imageFile.getFileName().equals(fileName)) {
                return imageFile;
            }
        }
        return null;
    }


    /**
     * Retourne la liste des fichiers image détectés.
     *
     * @return La liste des fichiers image.
     */
    public List<ImageFile> getImageFiles() {
        return imageFiles;
    }

    /**
     * Affiche les statistiques globales sur les fichiers image trouvés.
     *
     * Utilise {@link StatR} pour calculer et afficher les informations.
     */
    public void displayDirectoryStats() {
        StatR stats = new StatR(imageFiles);
        stats.generateReport();
    }

    /**
     * Affiche les statistiques d'un fichier image spécifique.
     *
     * @param fileName Le nom du fichier dont les statistiques doivent être affichées.
     */
    public void displayFileStats(String fileName) {
        ImageFile imageFile = findFileByName(fileName);

        if (imageFile != null) {
            StatF fileStats = new StatF(imageFile);
            fileStats.displayDetails();
        } else {
            System.out.println("Fichier introuvable : " + fileName);
        }
    }
}
