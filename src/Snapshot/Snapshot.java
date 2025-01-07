package Snapshot;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import data.ImageFile;

/**
 * Gestionnaire de snapshots pour surveiller les changements dans un répertoire.
 * 
 * Cette classe propose des fonctionnalités pour :
 * - Enregistrer un état de répertoire dans un fichier snapshot.
 * - Charger un état précédent à partir d'un fichier snapshot.
 * - Comparer deux états pour identifier les changements (ajouts, suppressions, modifications).
 * 
 * @author Youmna Saad et Seyda Ann
 */
public class Snapshot {

    /**
     * Enregistre l'état actuel des fichiers dans un fichier snapshot.
     *
     * @param imageFiles Liste des fichiers à inclure dans le snapshot.
     * @param snapshotPath Chemin du fichier snapshot.
     * @throws IOException En cas d'erreur lors de l'écriture du fichier.
     */
    public static void saveSnapshot(List<ImageFile> imageFiles, String snapshotPath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(snapshotPath))) {
            for (ImageFile file : imageFiles) {
                writer.write(String.join(";", 
                        file.getFileName(),
                        file.getFilePath(),
                        String.valueOf(file.getFileSize()),
                        String.valueOf(file.getLastModified().getTime())));
                writer.newLine();
            }
            System.out.println("Snapshot enregistré dans le fichier : " + snapshotPath);
        }
    }

    /**
     * Charge un snapshot à partir d'un fichier.
     *
     * @param snapshotPath Chemin du fichier snapshot.
     * @return Liste des fichiers contenus dans le snapshot.
     * @throws IOException En cas d'erreur lors de la lecture du fichier.
     */
    public static List<ImageFile> loadSnapshot(String snapshotPath) throws IOException {
        List<ImageFile> snapshotFiles = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(snapshotPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(";");
                if (details.length == 4) {
                    String name = details[0];
                    String path = details[1];
                    long size = Long.parseLong(details[2]);
                    Date lastModified = new Date(Long.parseLong(details[3]));

                    snapshotFiles.add(new ImageFile(new File(path)) {
                        @Override
                        public String getFileName() {
                            return name;
                        }

                        @Override
                        public long getFileSize() {
                            return size;
                        }

                        @Override
                        public Date getLastModified() {
                            return lastModified;
                        }
                    });
                }
            }
        }
        return snapshotFiles;
    }

    /**
     * Compare un snapshot précédent avec une liste actuelle de fichiers.
     *
     * @param previousSnapshot Liste des fichiers issus du snapshot précédent.
     * @param currentFiles Liste actuelle des fichiers.
     */
    public static void compareSnapshots(List<ImageFile> previousSnapshot, List<ImageFile> currentFiles) {
        // Extraction des noms de fichiers pour la comparaison
        Set<String> previousFileNames = previousSnapshot.stream().map(ImageFile::getFileName).collect(Collectors.toSet());
        Set<String> currentFileNames = currentFiles.stream().map(ImageFile::getFileName).collect(Collectors.toSet());

        // Fichiers ajoutés
        Set<String> addedFiles = new HashSet<>(currentFileNames);
        addedFiles.removeAll(previousFileNames);
        if (!addedFiles.isEmpty()) {
            System.out.println("Fichiers ajoutés :");
            addedFiles.forEach(file -> System.out.println("- " + file));
        } else {
            System.out.println("Aucun fichier ajouté.");
        }

        // Fichiers supprimés
        Set<String> removedFiles = new HashSet<>(previousFileNames);
        removedFiles.removeAll(currentFileNames);
        if (!removedFiles.isEmpty()) {
            System.out.println("Fichiers supprimés :");
            removedFiles.forEach(file -> System.out.println("- " + file));
        } else {
            System.out.println("Aucun fichier supprimé.");
        }

        // Fichiers modifiés
        System.out.println("Fichiers modifiés :");
        for (ImageFile previousFile : previousSnapshot) {
            currentFiles.stream()
                .filter(currentFile -> currentFile.getFileName().equals(previousFile.getFileName()))
                .filter(currentFile -> currentFile.getFileSize() != previousFile.getFileSize() ||
                        !currentFile.getLastModified().equals(previousFile.getLastModified()))
                .forEach(modifiedFile -> System.out.println("- " + modifiedFile.getFileName()));
        }
    }
}
