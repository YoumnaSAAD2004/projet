package partieConsole;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import Snapshot.Snapshot;
import data.ImageFile;
import data.ImageMetadata;
import data.ControleurR;
import data.Repertoire;
import engine.StatR;
import engine.StatF;
import engine.ModeRech;

/**
 * Classe principale pour l'exécution de l'application en mode console.
 * 
 * Fournit une interface en ligne de commande pour parcourir, rechercher, gérer 
 * les fichiers image, et effectuer des opérations comme la gestion des snapshots 
 * et l'affichage des statistiques.
 * 
 * @author Youmna Saad et Seyda Ann
 */
public class MainConsole {

    /**
     * Point d'entrée principal de l'application console.
     *
     * Gère les arguments de la ligne de commande et exécute les actions
     * correspondantes, comme l'affichage des statistiques ou la gestion des snapshots.
     *
     * @param args Les arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        if (args.length == 0 || args[0].equals("--help") || args[0].equals("-h")) {
            afficherAide();
            return;
        }

        try {
            String directoryPath = System.getProperty("user.dir");
            String fileName = null;
            String snapshotFile = null;
            boolean listFiles = false;
            boolean showStats = false;
            boolean enableSearch = false;
            boolean metadataForFile = false;
            boolean snapshotSave = false;
            boolean snapshotCompare = false;

            // Analyser les arguments
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-d":
                    case "--directory":
                        if (i + 1 < args.length) {
                            directoryPath = args[++i];
                        } else {
                            System.out.println("Erreur : Spécifiez un répertoire après -d ou --directory.");
                            return;
                        }
                        break;

                    case "-f":
                    case "--file":
                        if (i + 1 < args.length) {
                            fileName = args[++i];
                        } else {
                            System.out.println("Erreur : Spécifiez un fichier après -f ou --file.");
                            return;
                        }
                        break;

                    case "--list":
                        listFiles = true;
                        break;

                    case "--stat":
                        showStats = true;
                        break;

                    case "--recherche":
                        enableSearch = true;
                        break;

                    case "--info":
                    case "-i":
                        metadataForFile = true;
                        break;

                    case "--snapshotsave":
                        snapshotSave = true;
                        if (i + 1 < args.length) {
                            snapshotFile = args[++i];
                        } else {
                            System.out.println("Erreur : Spécifiez un fichier pour --snapshotsave.");
                            return;
                        }
                        break;

                    case "--snapshotcompare":
                        snapshotCompare = true;
                        if (i + 1 < args.length) {
                            snapshotFile = args[++i];
                        } else {
                            System.out.println("Erreur : Spécifiez un fichier pour --snapshotcompare.");
                            return;
                        }
                        break;

                    default:
                        System.out.println("Erreur : Option inconnue " + args[i]);
                        return;
                }
            }

            // Créer le gestionnaire de répertoires
            File directory = new File(directoryPath);
            Repertoire manager = new Repertoire();
            manager.scanDirectory(directory);

            // Récupérer la liste des fichiers image
            List<ImageFile> imageFiles = manager.getImageFiles();

            if (listFiles) {
                System.out.println("==> Liste des fichiers image trouvés :");
                ControleurR inspector = new ControleurR(imageFiles);
                inspector.listerNomsImages();
            }

            if (showStats) {
                if (fileName == null) {
                    System.out.println("\n==> Statistiques générales du répertoire :");
                    StatR analytics = new StatR(imageFiles);
                    analytics.generateReport();
                } else {
                    System.out.println("\n==> Statistiques pour le fichier spécifié :");
                    ImageFile file = manager.findFileByName(fileName);
                    if (file != null) {
                        StatF fileDetails = new StatF(file);
                        fileDetails.displayDetails();
                    } else {
                        System.out.println("Erreur : Fichier non trouvé.");
                    }
                }
            }

            if (metadataForFile && fileName != null) {
                ImageFile file = manager.findFileByName(fileName);
                if (file != null) {
                    System.out.println("\n==> Métadonnées pour le fichier spécifié :");
                    ImageMetadata metadata = new ImageMetadata(new File(file.getFilePath()));
                    System.out.println(metadata);
                } else {
                    System.out.println("Erreur : Fichier non trouvé.");
                }
            }

            if (enableSearch) {
                lancerModeRecherche(imageFiles);
            }

            if (snapshotSave && snapshotFile != null) {
                Snapshot.saveSnapshot(imageFiles, snapshotFile);
            }

            if (snapshotCompare && snapshotFile != null) {
                List<ImageFile> previousSnapshot = Snapshot.loadSnapshot(snapshotFile);
                Snapshot.compareSnapshots(previousSnapshot, imageFiles);
            }

        } catch (IOException e) {
            System.err.println("Erreur d'accès au répertoire ou au fichier : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue : " + e.getMessage());
        }
    }

    /**
     * Affiche les options disponibles pour le mode CLI.
     */
    private static void afficherAide() {
        System.out.println("Usage : java -jar projet.jar [options]");
        System.out.println("Options disponibles :");
        System.out.println("-d, --directory <path>   : Spécifier le répertoire à analyser.");
        System.out.println("-f, --file <filename>    : Spécifier un fichier pour en extraire les métadonnées.");
        System.out.println("--list                   : Lister les fichiers image dans le répertoire.");
        System.out.println("--stat                   : Afficher les statistiques globales du répertoire.");
        System.out.println("--recherche              : Activer le mode recherche interactif.");
        System.out.println("--info, -i               : Extraire les métadonnées pour le fichier spécifié avec -f.");
        System.out.println("--snapshotsave <file>    : Sauvegarder un snapshot du répertoire dans un fichier.");
        System.out.println("--snapshotcompare <file> : Comparer l'état actuel avec un snapshot.");
        System.out.println("--help, -h               : Afficher ce message d'aide.");
    }

    /**
     * Lance le mode interactif de recherche.
     *
     * @param imageFiles Liste des fichiers image à analyser.
     */
    private static void lancerModeRecherche(List<ImageFile> imageFiles) {
        ModeRech search = new ModeRech(imageFiles);
        Scanner scanner = new Scanner(System.in);
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n=== Mode Recherche ===");
            System.out.println("1. Rechercher par nom ou partie du nom");
            System.out.println("2. Rechercher par année de création");
            System.out.println("3. Quitter");
            System.out.print("Votre choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    System.out.print("Entrez le nom ou partie du nom à rechercher : ");
                    String partialName = scanner.nextLine();
                    List<ImageFile> resultsByName = search.findByName(partialName);
                    search.displayResults(resultsByName);
                    break;

                case 2:
                    System.out.print("Entrez l'année de création ou modification (ex. 2024) : ");
                    int year = scanner.nextInt();
                    List<ImageFile> resultsByYear = search.findByYear(year);
                    search.displayResults(resultsByYear);
                    break;

                case 3:
                    continuer = false;
                    System.out.println("Fin de la recherche.");
                    break;

                default:
                    System.out.println("Choix invalide. Veuillez réessayer.");
            }
        }

        scanner.close();
    }
}
