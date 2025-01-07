package partieGui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Snapshot.Snapshot;
import data.ImageFile;
import data.Repertoire;
import engine.StatR;
import engine.ModeRech;
import data.ImageMetadata;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe pour gérer les fonctionnalités principales de l'application via l'interface graphique.
 *
 * Fournit des méthodes pour charger, rechercher, afficher des statistiques,
 * afficher des métadonnées, sauvegarder et comparer des snapshots des fichiers image.
 *
 * @author Youmna Saad et Seyda Ann
 */
public class GUIOrganisation {
    private DefaultTableModel tableModel;
    private List<ImageFile> imageFiles;

    /**
     * Initialise le gestionnaire avec le modèle de table.
     *
     * @param tableModel Modèle pour afficher les fichiers dans le tableau.
     */
    public GUIOrganisation(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }

    /**
     * Charge les fichiers image d'un répertoire dans la table.
     *
     * @param directory Répertoire à explorer.
     */
    public void loadDirectory(File directory) {
        try {
            Repertoire manager = new Repertoire();
            manager.scanDirectory(directory);
            imageFiles = manager.getImageFiles();
            refreshTable(imageFiles);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de l'accès au répertoire : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Recherche des fichiers par nom ou année, et met à jour la table.
     *
     * @param query Texte de recherche (nom ou année).
     */
    public void searchFiles(String query) {
        if (imageFiles == null || imageFiles.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Aucun fichier n'est chargé. Veuillez sélectionner un répertoire.",
                    "Alerte", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ModeRech search = new ModeRech(imageFiles);
        List<ImageFile> filteredFiles;

        try {
            int year = Integer.parseInt(query);
            filteredFiles = search.findByYear(year);
        } catch (NumberFormatException ex) {
            filteredFiles = search.findByName(query);
        }

        refreshTable(filteredFiles);

        if (filteredFiles.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Aucun fichier correspondant à la recherche.",
                    "Résultats de recherche", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Affiche les statistiques globales des fichiers.
     */
    public void viewStatistics() {
        if (imageFiles == null || imageFiles.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Aucun fichier n'est chargé. Veuillez sélectionner un répertoire.",
                    "Alerte", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StatR stats = new StatR(imageFiles);
        StringBuilder statsMessage = new StringBuilder();
        statsMessage.append("Statistiques globales :\n")
                .append("Nombre total de fichiers : ").append(stats.countAllFiles()).append("\n")
                .append("Nombre d'images PNG : ").append(stats.countImagesByFormat(".png")).append("\n")
                .append("Nombre d'images JPEG : ").append(stats.countImagesByFormat(".jpeg")).append("\n")
                .append("Nombre d'images WEBP : ").append(stats.countImagesByFormat(".webp"));

        JOptionPane.showMessageDialog(null, statsMessage.toString(), "Statistiques Globales", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Affiche les métadonnées d'un fichier sélectionné.
     *
     * @param file Fichier à analyser.
     */
    public void viewMetadata(File file) {
        ImageFile imageFile = imageFiles.stream()
                .filter(img -> img.getFilePath().equals(file.getAbsolutePath()))
                .findFirst()
                .orElse(null);

        if (imageFile != null) {
            try {
                ImageMetadata metadata = new ImageMetadata(file);
                JTextArea textArea = new JTextArea(20, 50);
                textArea.setText(metadata.toString());
                textArea.setCaretPosition(0);
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                JOptionPane.showMessageDialog(null, scrollPane, "Métadonnées du fichier", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Erreur lors de l'extraction des métadonnées : " + e.getMessage(),
                        "Erreur Métadonnées", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Le fichier sélectionné n'est pas disponible.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Affiche les statistiques d'un fichier sélectionné.
     *
     * @param file Fichier à analyser.
     */
    public void viewFileStatistics(File file) {
        ImageFile fichier = imageFiles.stream()
                .filter(f -> f.getFilePath().equals(file.getAbsolutePath()))
                .findFirst()
                .orElse(null);

        if (fichier != null) {
            try {
                JOptionPane.showMessageDialog(null,
                        "Statistiques du fichier :\n" +
                                "Nom : " + fichier.getFileName() + "\n" +
                                "Chemin : " + fichier.getFilePath() + "\n" +
                                "Type MIME : " + fichier.getMimeType() + "\n" +
                                "Taille : " + fichier.getFileSize() + " octets\n" +
                                "Dernière modification : " + fichier.getLastModified(),
                        "Statistiques Fichier", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Erreur lors de la récupération des statistiques : " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Le fichier sélectionné n'est pas disponible.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Sauvegarde un snapshot de l'état actuel.
     *
     * @param path Chemin du fichier snapshot.
     */
    public void saveSnapshot(String path) {
        if (imageFiles == null || imageFiles.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Aucun fichier n'est chargé. Veuillez sélectionner un répertoire.",
                    "Alerte", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Snapshot.saveSnapshot(imageFiles, path);
            JOptionPane.showMessageDialog(null, "Snapshot sauvegardé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de la sauvegarde du snapshot : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Compare l'état actuel avec un snapshot et affiche les résultats.
     *
     * @param snapshotFilePath Chemin du fichier snapshot.
     */
    public void compareSnapshot(String snapshotFilePath) {
        try {
            List<ImageFile> snapshot = Snapshot.loadSnapshot(snapshotFilePath);
            StringBuilder comparisonResult = new StringBuilder();

            Set<String> currentNames = imageFiles.stream().map(ImageFile::getFileName).collect(Collectors.toSet());
            Set<String> snapshotNames = snapshot.stream().map(ImageFile::getFileName).collect(Collectors.toSet());

            Set<String> addedFiles = new HashSet<>(currentNames);
            addedFiles.removeAll(snapshotNames);

            Set<String> removedFiles = new HashSet<>(snapshotNames);
            removedFiles.removeAll(currentNames);

            if (!addedFiles.isEmpty()) {
                comparisonResult.append("Fichiers ajoutés :\n");
                addedFiles.forEach(name -> comparisonResult.append("- ").append(name).append("\n"));
            } else {
                comparisonResult.append("Aucun fichier ajouté.\n");
            }

            if (!removedFiles.isEmpty()) {
                comparisonResult.append("Fichiers supprimés :\n");
                removedFiles.forEach(name -> comparisonResult.append("- ").append(name).append("\n"));
            } else {
                comparisonResult.append("Aucun fichier supprimé.\n");
            }

            JTextArea textArea = new JTextArea(comparisonResult.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            JOptionPane.showMessageDialog(null, scrollPane, "Résultats de la comparaison", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors du chargement du snapshot : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Met à jour la table avec une liste de fichiers.
     *
     * @param files Liste des fichiers à afficher.
     */
    private void refreshTable(List<ImageFile> files) {
        tableModel.setRowCount(0);
        files.forEach(file -> tableModel.addRow(new Object[]{
                file.getFileName(),
                file.getFileSize() / 1024, // Taille en Ko
                file.getFilePath(),
                file.getLastModified()
        }));
    }
}
