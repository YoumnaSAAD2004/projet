package GUI;

import data.Fichier;
import data.MetaDonnees;
import data.Repertoire;
import data.StatistiquesRepertoire;
import snapshot.Snapshot;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class to manage the core functionalities of the application via the GUI.
 */
public class GUIOrganisation {
    private DefaultTableModel tableModel;
    private List<Fichier> fichiersImage;

    /**
     * Initialize the manager with the table model.
     *
     * @param tableModel Model for displaying files in the table.
     */
    public GUIOrganisation(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }

    /**
     * Load image files from a directory into the table.
     *
     * @param directory Directory to explore.
     */
    public void loadDirectory(File directory) {
        try {
            Repertoire repertoire = new Repertoire(directory.getName());
            repertoire.parcourirRepertoire(directory);
            fichiersImage = repertoire.getFichiers();
            refreshTable(fichiersImage);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error accessing directory: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Search for files by name or year and update the table.
     *
     * @param query Search text (name or year).
     */
    public void searchFiles(String query) {
        if (fichiersImage == null || fichiersImage.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "No files loaded. Please select a directory.",
                    "Alert", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Fichier> filteredFiles;
        try {
            int year = Integer.parseInt(query);
            filteredFiles = fichiersImage.stream()
                    .filter(f -> f.getStatistiques().getAnneeModification().equals(String.valueOf(year)))
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            filteredFiles = fichiersImage.stream()
                    .filter(f -> f.getNom().contains(query))
                    .collect(Collectors.toList());
        }

        refreshTable(filteredFiles);

        if (filteredFiles.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "No files matching the search.",
                    "Search Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Display global statistics of files.
     */
    public void viewStatistics() {
        if (fichiersImage == null || fichiersImage.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "No files loaded. Please select a directory.",
                    "Alert", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StatistiquesRepertoire stats = new StatistiquesRepertoire(fichiersImage);
        JOptionPane.showMessageDialog(null,
                "Global Statistics:\n" +
                        "Total files: " + stats.getNombreTotalFichiers() + "\n" +
                        "Valid images: " + stats.getNombreImagesValides() + "\n" +
                        "PNG images: " + stats.getNombreImagesParFormat(".png") + "\n" +
                        "JPEG images: " + stats.getNombreImagesParFormat(".jpeg") + "\n" +
                        "WEBP images: " + stats.getNombreImagesParFormat(".webp"),
                "Global Statistics", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Display statistics of a selected file.
     *
     * @param file File to analyze.
     */
    public void viewFileStatistics(File file) {
        Fichier fichier = findFile(file);

        if (fichier != null) {
            JOptionPane.showMessageDialog(null,
                    "File Statistics:\n" +
                            "Name: " + fichier.getNom() + "\n" +
                            "Path: " + fichier.getCheminRelatif() + "\n" +
                            "MIME Type: " + fichier.getStatistiques().getTypeMime() + "\n" +
                            "Size: " + fichier.getStatistiques().getTaille() + " bytes\n" +
                            "Last Modified: " + fichier.getStatistiques().getDateModification(),
                    "File Statistics", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Selected file not available.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Display metadata of a selected file.
     *
     * @param file File to analyze.
     */
    public void viewMetadata(File file) {
        Fichier fichier = findFile(file);

        if (fichier != null) {
            MetaDonnees metaDonnees = fichier.getMetaDonnees();
            JTextArea textArea = new JTextArea(20, 50);
            textArea.setText(metaDonnees.toString());
            textArea.setCaretPosition(0);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            JOptionPane.showMessageDialog(null, scrollPane, "File Metadata", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Selected file not available.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Save a snapshot of the current state.
     *
     * @param path Path of the snapshot file.
     */
    public void saveSnapshot(String path) throws IOException {
        if (fichiersImage == null || fichiersImage.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "No files loaded. Please select a directory.",
                    "Alert", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Snapshot snapshot = new Snapshot(new Repertoire("Snapshot"), path);
		snapshot.sauvegarder(path);
		JOptionPane.showMessageDialog(null,
		        "Snapshot saved successfully.",
		        "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Compare the current state with a snapshot and display the results.
     *
     * @param snapshotFilePath Path of the snapshot file.
     */
    public void compareSnapshot(String snapshotFilePath) throws IOException {
        Snapshot snapshot = Snapshot.charger(snapshotFilePath);
		if (snapshot == null) {
		    JOptionPane.showMessageDialog(null,
		            "Unable to load snapshot.",
		            "Error", JOptionPane.ERROR_MESSAGE);
		    return;
		}

		List<Fichier> snapshotFiles = snapshot.getRepertoire().getFichiers();
		StringBuilder differences = new StringBuilder();

		// Compare files
		for (Fichier file : fichiersImage) {
		    if (!snapshotFiles.contains(file)) {
		        differences.append("Added: ").append(file.getNom()).append("\n");
		    }
		}
		for (Fichier file : snapshotFiles) {
		    if (!fichiersImage.contains(file)) {
		        differences.append("Removed: ").append(file.getNom()).append("\n");
		    }
		}

		JTextArea textArea = new JTextArea(differences.toString());
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		JOptionPane.showMessageDialog(null, scrollPane, "Snapshot Comparison", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Refresh the table with a list of files.
     *
     * @param fichiers List of files to display.
     */
    private void refreshTable(List<Fichier> fichiers) {
        tableModel.setRowCount(0);
        fichiers.forEach(f -> tableModel.addRow(new Object[]{
                f.getNom(),
                f.getStatistiques().getTaille() / 1024, // Size in KB
                f.getCheminRelatif(),
                f.getStatistiques().getDateModification()
        }));
    }

    /**
     * Find a file in the current list by its path.
     *
     * @param file File to find.
     * @return The corresponding Fichier object, or null if not found.
     */
    private Fichier findFile(File file) {
        return fichiersImage.stream()
                .filter(f -> f.getCheminRelatif().equals(file.getAbsolutePath()))
                .findFirst()
                .orElse(null);
    }
}
