
// File: ./src/module-info.java
/**
 * 
 */
/**
 * 
 */
module projectt {
	requires metadata.extractor;

}

// File: ./src/data/Fichier.java
package data;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.Directory;
import com.drew.metadata.Tag;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Date;

/**
 * Classe représentant un fichier image avec ses métadonnées et ses statistiques.
 */
public class Fichier implements Serializable {
    private static final long serialVersionUID = 1L;
    private File file; // Instance de File représentant le fichier
    private String nom; // Nom du fichier
    private StatistiquesFichier statistiques; // Statistiques associées au fichier
    private MetaDonnees metaDonnees; // Métadonnées associées au fichier

    /**
     * Constructeur.
     * Initialise le fichier, son nom, ses métadonnées et ses statistiques.
     * @param file Le fichier à manipuler.
     */
    public Fichier(File file) {
        this.file = file;
        this.nom = file.getName(); // Initialise le nom du fichier
        this.metaDonnees = extraireMetaDonnees(); // Extrait les métadonnées du fichier
        this.statistiques = calculerStatistiques(); // Calcule les statistiques du fichier
    }

    /**
     * Calcule les statistiques du fichier.
     * @return Un objet StatistiquesFichier contenant les statistiques.
     */
    private StatistiquesFichier calculerStatistiques() {
        try {
            String typeMime = Files.probeContentType(file.toPath());
            long taille = file.length();
            String dateModification = new Date(file.lastModified()).toString();
            return new StatistiquesFichier((int) taille, typeMime, dateModification);
        } catch (IOException e) {
            System.err.println("Erreur lors du calcul des statistiques : " + e.getMessage());
            return new StatistiquesFichier(0, "inconnu", "inconnue");
        }
    }

    /**
     * Extrait les métadonnées du fichier.
     * @return Un objet MetaDonnees contenant les informations extraites.
     */
    private MetaDonnees extraireMetaDonnees() {
        MetaDonnees metaDonnees = new MetaDonnees();
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);

            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    String tagName = tag.getTagName();
                    String tagValue = tag.getDescription();

                    // Extraction des métadonnées pertinentes
                    switch (tagName) {
                        case "Image Width":
                            metaDonnees.getDimensions()[0] = Integer.parseInt(tagValue.replaceAll("\\D+", ""));
                            break;
                        case "Image Height":
                            metaDonnees.getDimensions()[1] = Integer.parseInt(tagValue.replaceAll("\\D+", ""));
                            break;
                        case "X Resolution":
                        case "Y Resolution":
                            metaDonnees.setResolution(tagValue + " dpi");
                            break;
                        case "GPS Latitude":
                        case "GPS Longitude":
                            if (metaDonnees.getPositionGPS() == null) {
                                metaDonnees.setPositionGPS(tagValue);
                            } else {
                                metaDonnees.setPositionGPS(metaDonnees.getPositionGPS() + ", " + tagValue);
                            }
                            break;
                        case "Image Description":
                        case "Caption/Abstract":
                            metaDonnees.setDescription(tagValue);
                            break;
                        case "Thumbnail Offset":
                            metaDonnees.setMiniatureExistance(true);
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'extraction des métadonnées : " + e.getMessage());
        }
        return metaDonnees;
    }

    /**
     * Vérifie si le type MIME du fichier est valide.
     * @return true si le type MIME est valide, false sinon.
     */
    public boolean verifierTypeMIME() {
        String type = statistiques.getTypeMime();
        return type.equals("image/jpeg") || type.equals("image/png") || type.equals("image/webp");
    }

    // Getters
    public String getNom() {
        return nom;
    }

    public StatistiquesFichier getStatistiques() {
        if (this.statistiques == null) {
            this.statistiques = calculerStatistiques();
        }
        return this.statistiques;
    }


    public MetaDonnees getMetaDonnees() {
        return metaDonnees;
    }

    // toString
    @Override
    public String toString() {
        return "Fichier{" +
                "nom='" + nom + '\'' +
                ", statistiques=" + statistiques +
                ", metaDonnees=" + metaDonnees +
                '}';
    }
}




// File: ./src/data/StatistiquesFichier.java
package data;



import java.io.Serializable;

public class StatistiquesFichier implements Serializable {
    private static final long serialVersionUID = 1L;

    // Attributs principaux
    private int taille; // Taille du fichier en octets
    private String typeMime; // Type MIME du fichier
    private String dateModification; // Date de dernière modification du fichier

    /**
     * Constructeur pour initialiser les statistiques d'un fichier.
     * @param taille Taille en octets.
     * @param typeMime Type MIME.
     * @param dateModification Date de dernière modification.
     */
    public StatistiquesFichier(int taille, String typeMime, String dateModification) {
        this.taille = taille;
        this.typeMime = typeMime;
        this.dateModification = dateModification;
    }

    /**
     * Retourne la taille du fichier.
     * @return Taille en octets.
     */
    public int getTaille() {
        return taille;
    }

    /**
     * Retourne le type MIME du fichier.
     * @return Type MIME du fichier.
     */
    public String getTypeMime() {
        return typeMime;
    }

    /**
     * Retourne la date de dernière modification du fichier.
     * @return Date de dernière modification.
     */
    public String getDateModification() {
        return dateModification;
    }

    /**
     * Retourne une représentation textuelle des statistiques du fichier.
     * @return Les statistiques formatées sous forme de chaîne.
     */
    @Override
    public String toString() {
        return "StatistiquesFichier { " +
                "taille=" + taille + " octets, " +
                "typeMime='" + typeMime + '\'' + ", " +
                "dateModification='" + dateModification + '\'' +
                " }";
    }
}




// File: ./src/data/MetaDonnees.java
package data;

import java.io.Serializable;

/**
 * Représente les métadonnées associées à un fichier.
 */
public class MetaDonnees  implements Serializable {
    private static final long serialVersionUID = 1L;
    private int[] dimensions; // Dimensions de l'image [largeur, hauteur]
    private String description; // Description ou légende de l'image
    private String positionGPS; // Coordonnées GPS (latitude et longitude)
    private String resolution; // Résolution de l'image (ex. 300 dpi)
    private boolean miniatureExistance; // Indique si une miniature existe

    /**
     * Constructeur complet pour initialiser toutes les métadonnées.
     * @param dimensions Dimensions de l'image [largeur, hauteur].
     * @param description Description de l'image.
     * @param positionGPS Coordonnées GPS.
     * @param resolution Résolution de l'image.
     * @param miniatureExistance Indique si une miniature est disponible.
     */
    public MetaDonnees(int[] dimensions, String description, String positionGPS, String resolution, boolean miniatureExistance) {
        this.dimensions = dimensions;
        this.description = description;
        this.positionGPS = positionGPS;
        this.resolution = resolution;
        this.miniatureExistance = miniatureExistance;
    }

    /**
     * Constructeur par défaut pour initialiser les valeurs par défaut.
     */
    public MetaDonnees() {
        this.dimensions = new int[]{0, 0}; // Par défaut : largeur et hauteur à 0
        this.description = "";
        this.positionGPS = null; // Pas de coordonnées GPS par défaut
        this.resolution = "";
        this.miniatureExistance = false; // Pas de miniature par défaut
    }

    // Getters pour accéder aux champs

    public int[] getDimensions() {
        return dimensions;
    }

    public String getDescription() {
        return description;
    }

    public String getPositionGPS() {
        return positionGPS;
    }

    public String getResolution() {
        return resolution;
    }

    public boolean isMiniatureExist() {
        return miniatureExistance;
    }

    // Setters pour modifier les champs après l'initialisation

    public void setDimensions(int[] dimensions) {
        this.dimensions = dimensions;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPositionGPS(String positionGPS) {
        this.positionGPS = positionGPS;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setMiniatureExistance(boolean miniatureExistance) {
        this.miniatureExistance = miniatureExistance;
    }

    /**
     * Représentation textuelle des métadonnées.
     * @return Une chaîne de caractères représentant les métadonnées.
     */
    @Override
    public String toString() {
        // Protection contre les dimensions nulles
        String dimensionsStr = (dimensions != null && dimensions.length == 2)
                ? dimensions[0] + "x" + dimensions[1]
                : "Inconnues";

        return "MetaDonnees{" +
                "dimensions=[" + dimensionsStr + "], " +
                "description='" + description + '\'' +
                ", positionGPS='" + (positionGPS != null ? positionGPS : "Non disponible") + '\'' +
                ", resolution='" + resolution + '\'' +
                ", miniatureExistance=" + miniatureExistance +
                '}';
    }
}



// File: ./src/data/Repertoire.java
package data;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un répertoire contenant des fichiers image et des sous-répertoires.
 * Cette classe parcourt récursivement un répertoire et collecte les fichiers image valides
 * ainsi que les sous-répertoires.
 */
public class Repertoire implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nom; // Nom du répertoire
    private List<Fichier> fichiers; // Liste des fichiers image dans le répertoire
    private List<Repertoire> sousRepertoires; // Liste des sous-répertoires
    private StatistiquesRepertoire statistiques; // Statistiques associées au répertoire

    /**
     * Constructeur complet.
     * @param nom Nom du répertoire.
     * @param fichiers Liste des fichiers image.
     * @param sousRepertoires Liste des sous-répertoires.
     * @param statistiques Statistiques associées au répertoire.
     */
    public Repertoire(String nom, List<Fichier> fichiers, List<Repertoire> sousRepertoires, StatistiquesRepertoire statistiques) {
        this.nom = nom;
        this.fichiers = fichiers;
        this.sousRepertoires = sousRepertoires;
        this.statistiques = statistiques;
    }

    /**
     * Constructeur simplifié qui initialise les listes et le nom.
     * @param nom Nom du répertoire.
     */
    public Repertoire(String nom) {
        this.nom = nom;
        this.fichiers = new ArrayList<>();
        this.sousRepertoires = new ArrayList<>();
        this.statistiques = new StatistiquesRepertoire(fichiers);
    }

    // Getters
    public String getNom() {
        return nom;
    }

    public List<Fichier> getFichiers() {
        return fichiers;
    }

    public List<Repertoire> getSousRepertoires() {
        return sousRepertoires;
    }

    public StatistiquesRepertoire getStatistiques() {
        return statistiques;
    }

    /**
     * Parcourt un répertoire et ses sous-répertoires pour récupérer les fichiers image valides.
     * Cette méthode utilise une approche récursive pour explorer toute l'arborescence.
     * 
     * @param directory Le répertoire à parcourir.
     * @throws IOException Si le répertoire n'existe pas ou n'est pas accessible.
     */
    public void parcourirRepertoire(File directory) throws IOException {
        // Vérifie si le répertoire est valide
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            throw new IOException("Le répertoire n'existe pas ou n'est pas valide : " + directory);
        }

        // Met à jour le nom du répertoire courant
        this.nom = directory.getName();

        // Liste tous les fichiers et sous-dossiers dans le répertoire courant
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Si l'élément est un sous-répertoire, crée un objet Repertoire et appelle récursivement la méthode
                    Repertoire sousRepertoire = new Repertoire(file.getName());
                    sousRepertoire.parcourirRepertoire(file); // Appel récursif
                    sousRepertoires.add(sousRepertoire); // Ajoute le sous-répertoire à la liste
                    System.out.println("les sous-répertoire : " + file.getName());
                } else if (file.isFile()) {
                    // Si l'élément est un fichier, vérifie s'il s'agit d'une image valide
                    Fichier fichier = new Fichier(file);
                    if (fichier.verifierTypeMIME()) {
                        fichiers.add(fichier); // Ajoute le fichier valide à la liste
                        System.out.println("Fichier valide : " + file.getName());
                    } else {
                        System.out.println("Fichier ignoré (non valide) : " + file.getName());
                    }
                }
            }
        }
    }


    /**
     * Recherche un fichier par son nom dans le répertoire courant.
     * @param fileName Le nom du fichier recherché.
     * @return Une instance de Fichier si trouvée, null sinon.
     */
    public Fichier getFichierByName(String fileName) {
        for (Fichier fichier : fichiers) {
            if (fichier.getNom().equalsIgnoreCase(fileName)) {
                return fichier; // Retourne le fichier si le nom correspond
            }
        }
        return null; // Retourne null si aucun fichier ne correspond
    }

    /**
     * Retourne une représentation textuelle du répertoire, incluant le nombre de fichiers et de sous-répertoires.
     * @return Une chaîne de caractères décrivant le répertoire.
     */
    @Override
    public String toString() {
        return "Repertoire{" +
                "nom='" + nom + '\'' +
                ", fichiers=" + fichiers.size() +
                ", sousRepertoires=" + sousRepertoires.size() +
                '}';
    }
}


// File: ./src/data/StatistiquesRepertoire.java
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


// File: ./src/cli/CLI.java
package cli;

import data.*;
import engine.*;
import snapshot.Snapshot;
import snapshot.Difference;
import java.io.File;
import java.io.IOException;

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


                    default:
                        System.out.println("Option invalide pour un répertoire : " + args[i]);
                        afficherAide();
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'analyse du répertoire : " + e.getMessage());
        }
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


// File: ./src/engine/RechercheFichiers.java
package engine;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour effectuer une recherche dans un répertoire selon des critères.
 */
public class RechercheFichiers  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Recherche des fichiers selon les critères spécifiés.
     *
     * @param repertoire   Le répertoire à parcourir.
     * @param nomPartiel   Nom ou partie du nom à rechercher (peut être null).
     * @param anneeCreation Année de création à rechercher (peut être null).
     * @return Liste des fichiers correspondant aux critères.
     * @throws IOException En cas d'erreur de lecture des fichiers.
     */
    public static List<File> rechercherFichiers(File repertoire, String nomPartiel, Integer anneeCreation) throws IOException {
        List<File> resultats = new ArrayList<>();

        if (!repertoire.exists() || !repertoire.isDirectory()) {
            throw new IllegalArgumentException("Le chemin spécifié n'est pas un répertoire valide.");
        }

        // Parcourt récursivement le répertoire
        for (File fichier : repertoire.listFiles()) {
            if (fichier.isDirectory()) {
                // Recherche dans les sous-dossiers
                resultats.addAll(rechercherFichiers(fichier, nomPartiel, anneeCreation));
            } else if (fichier.isFile()) {
                boolean correspond = true;

                // Vérifie le critère du nom (contient la chaîne)
                if (nomPartiel != null && !fichier.getName().toLowerCase().contains(nomPartiel.toLowerCase())) {
                    correspond = false;
                }

                // Vérifie le critère de l'année de création
                if (anneeCreation != null) {
                    BasicFileAttributes attrs = Files.readAttributes(fichier.toPath(), BasicFileAttributes.class);
                    long creationTime = attrs.creationTime().toMillis();
                    String anneeFichier = new SimpleDateFormat("yyyy").format(creationTime);
                    if (!anneeFichier.equals(anneeCreation.toString())) {
                        correspond = false;
                    }
                }

                if (correspond) {
                    resultats.add(fichier);
                }
            }
        }

        return resultats;
    }
}



// File: ./src/engine/ControleurR.java
package engine;

import data.Repertoire;
import data.StatistiquesRepertoire;
import data.Fichier;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe de contrôle pour les opérations liées aux répertoires.
 */
public class ControleurR  implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Retourne tous les fichiers d'un répertoire, y compris ceux des sous-répertoires.
     *
     * @param repertoire Le répertoire à explorer.
     * @return Liste des fichiers dans le répertoire et ses sous-répertoires.
     */
    public ArrayList<Fichier> listerFichiers(Repertoire repertoire) {
        if (repertoire == null) {
            throw new IllegalArgumentException("Le répertoire ne peut pas être null.");
        }

        ArrayList<Fichier> tousFichiers = new ArrayList<>(repertoire.getFichiers());

        // Parcourt récursivement les sous-répertoires
        for (Repertoire sousRepertoire : repertoire.getSousRepertoires()) {
            tousFichiers.addAll(listerFichiers(sousRepertoire));
        }

        return tousFichiers;
    }

    /**
     * Calcule les statistiques globales d'un répertoire, y compris les fichiers des sous-répertoires.
     *
     * @param repertoire Le répertoire pour lequel calculer les statistiques.
     * @return Les statistiques du répertoire et de ses sous-répertoires.
     */
    public StatistiquesRepertoire calculerStatsRepertoire(Repertoire repertoire) {
        if (repertoire == null) {
            throw new IllegalArgumentException("Le répertoire ne peut pas être null.");
        }

        // Collecte tous les fichiers du répertoire principal et des sous-répertoires
        ArrayList<Fichier> tousFichiers = listerFichiers(repertoire);

        // Crée un objet StatistiquesRepertoire basé sur la liste complète des fichiers
        return new StatistiquesRepertoire(tousFichiers);
    }

    /**
     * Affiche les statistiques d'un répertoire et de ses sous-répertoires.
     *
     * @param repertoire Le répertoire pour lequel afficher les statistiques.
     */
    public void afficherStatistiques(Repertoire repertoire) {
        if (repertoire == null) {
            throw new IllegalArgumentException("Le répertoire ne peut pas être null.");
        }

        // Calcule les statistiques globales
        StatistiquesRepertoire stats = calculerStatsRepertoire(repertoire);

        // Affiche uniquement les statistiques calculées
        stats.afficherStatistiques();
    }
}


// File: ./src/engine/ControleurF.java
package engine;

import data.Fichier;
import data.MetaDonnees;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe de contrôle pour les opérations liées aux fichiers.
 */
public class ControleurF  implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Extrait les métadonnées d'un fichier.
     *
     * @param fichier Le fichier pour lequel extraire les métadonnées.
     * @return Les métadonnées du fichier.
     */
    public MetaDonnees extraireMeta(Fichier fichier) {
        if (fichier == null) {
            throw new IllegalArgumentException("Le fichier ne peut pas être null.");
        }
        return fichier.getMetaDonnees();
    }

    /**
     * Cherche des fichiers dans un répertoire par nom partiel.
     *
     * @param fichiers Liste de fichiers à chercher.
     * @param nom Nom partiel à chercher.
     * @return Liste des fichiers correspondant.
     */
    public ArrayList<Fichier> chercherFichiersParNom(ArrayList<Fichier> fichiers, String nom) {
        if (fichiers == null || nom == null) {
            throw new IllegalArgumentException("La liste des fichiers ou le nom ne peuvent pas être null.");
        }

        ArrayList<Fichier> resultats = new ArrayList<>();
        for (Fichier fichier : fichiers) {
            if (fichier.getNom().contains(nom)) {
                resultats.add(fichier);
            }
        }
        return resultats;
    }

    /**
     * Cherche des fichiers par taille minimale.
     *
     * @param fichiers Liste de fichiers à chercher.
     * @param tailleMin Taille minimale (en octets).
     * @return Liste des fichiers correspondant.
     */
    public ArrayList<Fichier> chercherFichiersParTaille(ArrayList<Fichier> fichiers, int tailleMin) {
        if (fichiers == null) {
            throw new IllegalArgumentException("La liste des fichiers ne peut pas être null.");
        }
        if (tailleMin < 0) {
            throw new IllegalArgumentException("La taille minimale ne peut pas être négative.");
        }

        ArrayList<Fichier> resultats = new ArrayList<>();
        for (Fichier fichier : fichiers) {
            if (fichier.getStatistiques().getTaille() >= tailleMin) {
                resultats.add(fichier);
            }
        }
        return resultats;
    }
}


// File: ./src/snapshot/Difference.java
package snapshot;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Difference implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<String> fichiersAjoutes;
    private List<String> fichiersSupprimes;

    // Constructeur
    public Difference() {
        this.fichiersAjoutes = new ArrayList<>();
        this.fichiersSupprimes = new ArrayList<>();
    }

    // Ajoute un fichier à la liste des fichiers ajoutés
    public void ajouterFichierAjoute(String fichier) {
        fichiersAjoutes.add(fichier);
    }

    // Ajoute un fichier à la liste des fichiers supprimés
    public void ajouterFichierSupprime(String fichier) {
        fichiersSupprimes.add(fichier);
    }

    // Afficher les différences
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== Différences détectées ===\n");

        sb.append("Fichiers ajoutés :\n");
        for (String fichier : fichiersAjoutes) {
            sb.append("  - ").append(fichier).append("\n");
        }

        sb.append("Fichiers supprimés :\n");
        for (String fichier : fichiersSupprimes) {
            sb.append("  - ").append(fichier).append("\n");
        }

        return sb.toString();
    }
}


// File: ./src/snapshot/Snapshot.java
package snapshot;


import data.Repertoire;
import data.Fichier;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Snapshot implements Serializable {

    private static final long serialVersionUID = 1L;
    private Repertoire repertoire;
    private String dateSnapshot;

    // Constructeur
    public Snapshot(Repertoire repertoire, String dateSnapshot) {
        this.repertoire = repertoire;
        this.dateSnapshot = dateSnapshot;
    }

    // Sauvegarder le snapshot dans un fichier binaire
    public void sauvegarder(String cheminFichier) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cheminFichier))) {
            oos.writeObject(this);
            System.out.println("Snapshot sauvegardé dans : " + cheminFichier);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde du snapshot : " + e.getMessage());
        }
    }

    // Charger un snapshot depuis un fichier binaire
    public static Snapshot charger(String cheminFichier) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cheminFichier))) {
            return (Snapshot) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement du snapshot : " + e.getMessage());
        }
        return null;
    }

    // Comparer deux snapshots pour détecter les différences
    public Difference comparer(Snapshot ancienSnapshot) {
        Difference difference = new Difference();

        List<Fichier> anciensFichiers = ancienSnapshot.getRepertoire().getFichiers();
        List<Fichier> actuelsFichiers = this.repertoire.getFichiers();

        // Fichiers ajoutés
        for (Fichier actuel : actuelsFichiers) {
            boolean existe = anciensFichiers.stream()
                    .anyMatch(ancien -> ancien.getNom().equals(actuel.getNom()));
            if (!existe) {
                difference.ajouterFichierAjoute(actuel.getNom());
            }
        }

        // Fichiers supprimés
        for (Fichier ancien : anciensFichiers) {
            boolean existe = actuelsFichiers.stream()
                    .anyMatch(actuel -> actuel.getNom().equals(ancien.getNom()));
            if (!existe) {
                difference.ajouterFichierSupprime(ancien.getNom());
            }
        }

        return difference;
    }

    // Getters
    public Repertoire getRepertoire() {
        return repertoire;
    }

    public String getDateSnapshot() {
        return dateSnapshot;
    }
}

