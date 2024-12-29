package cli;

import data.*;
import engine.*;
import snapshot.Snapshot;
import snapshot.Difference;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class CLI {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Erreur : Aucun argument fourni.");
            afficherAide();
            return;
        }

        String option = args[0];

        if (option.equals("-d") || option.equals("--directory")) {
            try {
				traiterRepertoire(args);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else if (option.equals("-f") || option.equals("--file")) {
            traiterFichier(args);
        } else if (option.equals("-h") || option.equals("--help")) {
            afficherAide();
        } else {
            System.out.println("Option invalide : " + option);
            afficherAide();
        }
    }
    String cheminRepertoire = "."; // Répertoire courant par défaut

    private static void traiterRepertoire(String[] args) throws IOException, ClassNotFoundException {
        if (args.length < 2) {
            System.out.println("Erreur : Vous devez spécifier un répertoire après -d ou --directory.");
            return;
        }

        String cheminRepertoire = args[1];
        File repertoireFile = new File(cheminRepertoire);

        if (!repertoireFile.exists() || !repertoireFile.isDirectory()) {
            System.out.println("Erreur : Le chemin spécifié n'est pas un répertoire valide : " + repertoireFile.getAbsolutePath());
            return;
        }

        Repertoire repertoire = new Repertoire(cheminRepertoire);
     // Déclaration des filtres par défaut
        String filtreNom = null;
        Integer filtreAnnee = null;
        int[] filtreDimensions = null;

        // Parcourir les arguments pour détecter les filtres
        for (int i = 2; i < args.length; i++) {
            if (args[i].startsWith("--year=")) {
                filtreAnnee = Integer.parseInt(args[i].substring("--year=".length()));
            } else if (args[i].startsWith("--dimension=")) {
                String[] dims = args[i].substring("--dimension=".length()).split("x");
                filtreDimensions = new int[]{Integer.parseInt(dims[0]), Integer.parseInt(dims[1])};
            } else if (args[i].startsWith("--name=")) {
                filtreNom = args[i].substring("--name=".length());
            }
        }


        try {
            repertoire.parcourirRepertoire(repertoireFile);
            ControleurR controleurR = new ControleurR();

            for (int i = 2; i < args.length; i++) {
                switch (args[i]) {
                    case "--list":
                        System.out.println("Fichiers d'images dans le répertoire :");
                        for (Fichier fichier : controleurR.listerFichiers(repertoire)) {
                            System.out.println("- " + fichier.getNom());
                        }
                        break;

                    case "--stat":
                        controleurR.afficherStatistiques(repertoire);
                        break;

                        // Commande pour sauvegarder un snapshot
                    case "--snapshotsave":
                        String nomSnapshotSave;
                        if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                            // Utiliser le nom fourni par l'utilisateur
                            nomSnapshotSave = args[++i];
                        } else {
                            // Générer un nom de fichier par défaut
                            nomSnapshotSave = "snapshot.ser";
                            System.out.println("Aucun nom spécifié, le snapshot sera sauvegardé avec le nom : " + nomSnapshotSave);
                        }
                        try {
                            Snapshot snapshot = creerSnapshot(cheminRepertoire); // Capture l'état du répertoire
                            snapshot.sauvegarder(nomSnapshotSave); // Sauvegarde dans un fichier
                            System.out.println("Snapshot sauvegardé avec succès dans : " + new File(nomSnapshotSave).getAbsolutePath());
                        } catch (IOException e) {
                            System.err.println("Erreur lors de la sauvegarde du snapshot : " + e.getMessage());
                        }
                        break;



                        // Commande pour comparer deux snapshots
                    case "--snapshotcompare":
                        if (i + 1 >= args.length) {
                            System.out.println("Erreur : Vous devez spécifier un fichier snapshot à comparer.");
                            return;
                        }
                        String fichierSnapshot = args[++i];

                        // Charger le snapshot sauvegardé
                        Snapshot snapshotSauvegarde = Snapshot.charger(fichierSnapshot);

                        if (snapshotSauvegarde == null) {
                            System.out.println("Erreur : Impossible de charger le snapshot spécifié.");
                            return;
                        }

                        // Créer un snapshot de l'état actuel du répertoire
                        Snapshot snapshotActuel = creerSnapshot(cheminRepertoire);

                        // Comparer les snapshots
                        Difference differences = snapshotActuel.comparer(snapshotSauvegarde);
                        System.out.println(differences);
                        break;
                    case "--search":
                        String nomRecherche = null;
                        Integer anneeRecherche = null;
                        int[] dimensionsRecherche = null;

                        // Récupérer les arguments (par exemple, `--name`, `--year`, `--dim`)
                        for (int j = i + 1; j < args.length && !args[j].startsWith("--"); j++) {
                            if (args[j].startsWith("--name=")) {
                                nomRecherche = args[j].substring(7); // Extrait la valeur après `--name=`
                            } else if (args[j].startsWith("--year=")) {
                                anneeRecherche = Integer.parseInt(args[j].substring(7)); // Extrait l'année
                            } else if (args[j].startsWith("--dim=")) {
                                String[] dim = args[j].substring(6).split("x");
                                dimensionsRecherche = new int[]{Integer.parseInt(dim[0]), Integer.parseInt(dim[1])};
                            }
                        }

                        // Appeler la méthode rechercherFichiers
                        List<Fichier> resultatsRecherche = repertoire.rechercherFichiers(nomRecherche, anneeRecherche, dimensionsRecherche);

                        // Afficher les résultats
                        if (resultatsRecherche.isEmpty()) {
                            System.out.println("Aucun fichier ne correspond aux critères spécifiés.");
                        } else {
                            System.out.println("Fichiers correspondants :");
                            for (Fichier fichier : resultatsRecherche) {
                                System.out.println(fichier);
                            }
                        }
                        break;



                    default:
                        System.out.println("Option invalide pour un répertoire : " + args[i]);
                        afficherAide();
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'analyse du répertoire : " + e.getMessage());
        }
        ControleurR controleurR = new ControleurR();
        List<Fichier> fichiersFiltres = controleurR.rechercherFichiers(repertoire, filtreNom, filtreAnnee, filtreDimensions);

        System.out.println("Fichiers filtrés :");
        fichiersFiltres.forEach(System.out::println);

    }
    
    private static Snapshot creerSnapshot(String cheminRepertoire) throws IOException {
        File repertoireFile = new File(cheminRepertoire);

        if (!repertoireFile.exists() || !repertoireFile.isDirectory()) {
            throw new IOException("Le chemin spécifié n'est pas un répertoire valide : " + cheminRepertoire);
        }

        // Crée un objet Repertoire pour capturer les fichiers et sous-répertoires
        Repertoire repertoire = new Repertoire(cheminRepertoire);
        repertoire.parcourirRepertoire(repertoireFile); // Parcourt le répertoire pour collecter les fichiers

        // Capture la date actuelle pour le snapshot
        String dateSnapshot = java.time.LocalDateTime.now().toString();

        // Crée un snapshot à partir du répertoire
        return new Snapshot(repertoire, dateSnapshot);
    }




    private static void traiterFichier(String[] args) {
        if (args.length < 2) {
            System.out.println("Erreur : Vous devez spécifier un fichier après -f ou --file.");
            return;
        }

        String cheminFichier = args[1];
        File fichierFile = new File(cheminFichier);

        if (!fichierFile.exists() || !fichierFile.isFile()) {
            System.out.println("Erreur : Le chemin spécifié n'est pas un fichier valide : " + fichierFile.getAbsolutePath());
            return;
        }

        // Crée une instance de Fichier
        Fichier fichier = new Fichier(fichierFile);
        ControleurF controleurF = new ControleurF();

        for (int i = 2; i < args.length; i++) {
            switch (args[i]) {
                case "--stat":
                    System.out.println("Statistiques du fichier :");
                    System.out.println(fichier.getStatistiques().toString());
                    break;
                 

                case "-i":
                case "--info":
                    System.out.println("Métadonnées du fichier :");
                    System.out.println(controleurF.extraireMeta(fichier).toString());
                    break;

                default:
                    System.out.println("Option invalide pour un fichier : " + args[i]);
                    afficherAide();
                    break;
            }
        }
    }


    private static void afficherAide() {
        System.out.println("Options possibles :");
        System.out.println("  -d, --directory <directory>   Analyser un répertoire");
        System.out.println("      --list                    Lister tous les fichiers d'images dans le répertoire");
        System.out.println("      --stat                    Afficher les statistiques du répertoire");
        System.out.println("      --snapshotsave [file]     Sauvegarder l'état du répertoire dans un fichier snapshot");
        System.out.println("      --snapshotcompare <file>  Comparer l'état actuel du répertoire avec un fichier snapshot");
        System.out.println("  -f, --file <file>             Analyser un fichier");
        System.out.println("      --stat                    Afficher les statistiques du fichier");
        System.out.println("      -i, --info                Extraire les métadonnées d'un fichier");
        System.out.println("  -h, --help                    Afficher cette aide");
    }
}
