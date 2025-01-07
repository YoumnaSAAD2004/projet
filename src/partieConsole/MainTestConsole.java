package partieConsole;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import data.Repertoire;
import engine.StatR;
import engine.ModeRech;
import data.ImageFile;
import data.ImageMetadata;

/**
 * Classe principale pour tester les fonctionnalités de l'application en mode console.
 *
 * Cette classe permet d'exécuter des fonctionnalités comme l'exploration de répertoires,
 * l'affichage de statistiques, l'extraction de métadonnées, et la recherche interactive.
 * 
 * @author Youmna Saad et Seyda Ann
 */
public class MainTestConsole {

    /**
     * Point d'entrée principal pour exécuter les tests en mode console.
     *
     * @param args Arguments de la ligne de commande.
     * @throws IOException En cas d'erreur d'accès au répertoire ou aux fichiers.
     */
    public static void main(String[] args) throws IOException {
        // Obtenir le répertoire de travail actuel
        String currentDirectory = System.getProperty("user.dir");
        File targetDirectory = new File(currentDirectory);

        // Utiliser ImageDirectoryManager pour explorer les fichiers image
        Repertoire directoryManager = new Repertoire();
        directoryManager.scanDirectory(targetDirectory);

        // Récupérer la liste des fichiers image trouvés sous forme d'objets ImageFile
        List<ImageFile> imageFiles = directoryManager.getImageFiles();

        // LISTER LES FICHIERS DU RÉPERTOIRE
        System.out.println("==> Liste des fichiers image trouvés :");
        directoryManager.getImageFiles().forEach(image -> System.out.println(image.getFileName()));

        // AFFICHER LES STATISTIQUES GÉNÉRALES DU RÉPERTOIRE
        System.out.println("\n==> Statistiques générales du répertoire :");
        StatR analytics = new StatR(imageFiles);
        analytics.generateReport();

        // TRAITEMENT D'UN FICHIER SPÉCIFIQUE
        System.out.println("\n==> Statistiques pour un fichier spécifique :");
        String specificFileName = "imagetest.jpg"; // Remplacez par le nom réel d'un fichier
        ImageFile specificFile = directoryManager.findFileByName(specificFileName);

        if (specificFile != null) {
            System.out.println("Nom du fichier : " + specificFile.getFileName());
            System.out.println("Taille : " + specificFile.getFileSize() + " octets");
            System.out.println("Dernière modification : " + specificFile.getLastModified());
        } else {
            System.out.println("Erreur : Le fichier spécifié n'a pas été trouvé.");
        }

        // EXTRACTION DES MÉTADONNÉES
        System.out.println("\n==> Métadonnées pour un fichier spécifié :");
        try {
            if (specificFile != null) {
                ImageMetadata metadata = new ImageMetadata(new File(specificFile.getFilePath()));
                System.out.println(metadata); // Affiche les métadonnées extraites
            } else {
                System.out.println("Erreur : Le fichier spécifié n'a pas été trouvé.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'extraction des métadonnées : " + e.getMessage());
        }

        // Initialiser la classe de recherche
        ModeRech imageSearch = new ModeRech(imageFiles);

        // Mode interactif pour les recherches
        Scanner scanner = new Scanner(System.in);
        boolean keepRunning = true;

        while (keepRunning) {
            System.out.println("\n=== Mode Recherche ===");
            System.out.println("1. Rechercher par nom ou partie du nom");
            System.out.println("2. Rechercher par année de création");
            System.out.println("3. Quitter");
            System.out.print("Votre choix : ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consommer la ligne restante

            switch (choice) {
                case 1: // Recherche par nom
                    System.out.print("Entrez le nom ou partie du nom à rechercher : ");
                    String partialName = scanner.nextLine();
                    List<ImageFile> nameResults = imageSearch.findByName(partialName);
                    imageSearch.displayResults(nameResults);
                    break;

                case 2: // Recherche par année
                    System.out.print("Entrez l'année de création ou modification (ex. 2024) : ");
                    int year = scanner.nextInt();
                    List<ImageFile> yearResults = imageSearch.findByYear(year);
                    imageSearch.displayResults(yearResults);
                    break;

                case 3: // Quitter
                    keepRunning = false;
                    System.out.println("Fin de la recherche.");
                    break;

                default:
                    System.out.println("Choix invalide. Veuillez réessayer.");
            }
        }

        scanner.close();
    }
}
