
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
import java.util.Objects;

public class Fichier implements Serializable {
    private static final long serialVersionUID = 1L;
    private File file; // Instance de File représentant le fichier
    private String nom; // Nom du fichier
    private StatistiquesFichier statistiques; // Statistiques associées au fichier
    private MetaDonnees metaDonnees; // Métadonnées associées au fichier

    public Fichier(File file) {
        this.file = file;
        this.nom = file.getName();
        this.metaDonnees = extraireMetaDonnees();
        this.statistiques = calculerStatistiques();
    }

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

    private MetaDonnees extraireMetaDonnees() {
        MetaDonnees metaDonnees = new MetaDonnees();
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);

            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    String tagName = tag.getTagName();
                    String tagValue = tag.getDescription();

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

    public boolean verifierTypeMIME() {
        String type = statistiques.getTypeMime();
        return type.equals("image/jpeg") || type.equals("image/png") || type.equals("image/webp");
    }

    public String getNom() {
        return nom;
    }

    public StatistiquesFichier getStatistiques() {
        return statistiques;
    }

    public MetaDonnees getMetaDonnees() {
        return metaDonnees;
    }

    // Nouvelle méthode : Retourne le chemin relatif du fichier
    public String getCheminRelatif() {
        return file.getPath();
    }

    // Méthode equals pour comparaison dans Difference
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fichier fichier = (Fichier) o;
        return Objects.equals(getCheminRelatif(), fichier.getCheminRelatif());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCheminRelatif());
    }
    public StatistiquesFichier getStatFichier() {
        return this.statistiques; // Si `statistiques` est l'attribut de type `StatistiquesFichier`.
    }


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

    public String getType() {
        return this.typeMime; // Assuming `typeMime` stores the MIME type
    }
    public String getAnneeModification() {
        // Utilise directement la dernière partie de la date pour extraire l'année
        try {
            String[] parts = dateModification.split(" ");
            return parts[parts.length - 1]; // Retourne le dernier élément (l'année)
        } catch (Exception e) {
            System.err.println("Erreur lors de l'extraction de l'année : " + e.getMessage());
            return "inconnue";
        }
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
    
    public List<Fichier> rechercherFichiers(String nomPartiel, Integer annee, int[] dimensions) {
        List<Fichier> resultats = new ArrayList<>();

        // Parcourir les fichiers dans le répertoire courant
        for (Fichier fichier : fichiers) {
            boolean correspond = true;

            // Filtrer par nom
            if (nomPartiel != null && !fichier.getNom().contains(nomPartiel)) {
                correspond = false;
            }

            // Filtrer par année
            if (annee != null) {
                String anneeFichier = fichier.getStatistiques().getDateModification().substring(24); // Vérifiez si cela fonctionne
                if (!anneeFichier.equals(annee.toString())) {
                    correspond = false;
                }
            }

            // Filtrer par dimensions
            if (dimensions != null) {
                int[] dimsFichier = fichier.getMetaDonnees().getDimensions();
                if (dimsFichier[0] != dimensions[0] || dimsFichier[1] != dimensions[1]) {
                    correspond = false;
                }
            }

            if (correspond) {
                resultats.add(fichier);
            }
        }

        // Chercher dans les sous-répertoires
        for (Repertoire sousRepertoire : sousRepertoires) {
            resultats.addAll(sousRepertoire.rechercherFichiers(nomPartiel, annee, dimensions));
        }

        return resultats;
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

        // Récupération du chemin du répertoire
        String cheminRepertoire = args[1];
        File repertoireFile = new File(cheminRepertoire);

        if (!repertoireFile.exists() || !repertoireFile.isDirectory()) {
            System.out.println("Erreur : Le chemin spécifié n'est pas un répertoire valide : " + repertoireFile.getAbsolutePath());
            return;
        }

        Repertoire repertoire = new Repertoire(cheminRepertoire);

        // Initialisation des filtres
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
            // Parcourir le répertoire pour collecter tous les fichiers
            repertoire.parcourirRepertoire(repertoireFile);

            // Utiliser le contrôleur pour rechercher les fichiers filtrés
            ControleurR controleurR = new ControleurR();
            List<Fichier> fichiersFiltres = controleurR.rechercherFichiers(repertoire, filtreNom, filtreAnnee, filtreDimensions);

            // Affichage des fichiers filtrés
            if (fichiersFiltres.isEmpty()) {
                System.out.println("Aucun fichier ne correspond aux critères spécifiés.");
            } else {
                System.out.println("Fichiers correspondants :");
                fichiersFiltres.forEach(System.out::println);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'analyse du répertoire : " + e.getMessage());
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
import java.util.List;

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
    
    public List<Fichier> rechercherFichiers(Repertoire repertoire, String nomPartiel, Integer annee, int[] dimensions) {
        return repertoire.rechercherFichiers(nomPartiel, annee, dimensions);
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


// File: ./src/GUI/GUIOrganisation.java
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


// File: ./src/GUI/GuiGraphique.java
package GUI;
import data.Fichier;
import data.MetaDonnees;
import data.Repertoire;
import data.StatistiquesFichier;
import data.StatistiquesRepertoire;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Classe pour gérer l'interface graphique de l'application.
 * 
 * Fournit une interface avec une disposition où les boutons sont alignés horizontalement en haut.
 * 
 * @author Gaetan et Yanis
 */
public class GuiGraphique extends JFrame {

    private JTable tableFiles;
    private DefaultTableModel tableModel;
    private JLabel lblImagePreview;
    private GUIOrganisation manageGUI;

    public GuiGraphique() {
        setTitle("Projet POO Image et metadonnee");
        setSize(1900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"Nom", "Taille (Ko)", "Chemin", "Dernière modification"}, 0);
        manageGUI = new GUIOrganisation(tableModel);

        setLayout(new BorderLayout(10, 10));

        createTopPanel();
        createCenterPanel();
        createBottomPanel();
    }

    /**
     * Crée le panneau supérieur avec les boutons alignés horizontalement et la barre de recherche.
     */
    /**
     * Creates the top panel with horizontally aligned buttons.
     */
    private void createTopPanel() {
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Section for horizontally aligned buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton btnSelectDirectory = new JButton("Choisir un répertoire");
        JButton btnViewStatistics = new JButton("Statistiques globales");
        JButton btnViewFileStatistics = new JButton("Statistiques du fichier");
        JButton btnViewMetadata = new JButton("Métadonnées");
        JButton btnSnapshotSave = new JButton("Sauvegarder Snapshot");
        JButton btnSnapshotCompare = new JButton("Comparer Snapshot");

        btnSelectDirectory.addActionListener(e -> chargerRepertoire());
        btnViewStatistics.addActionListener(e -> manageGUI.viewStatistics());
        btnViewFileStatistics.addActionListener(e -> afficherStatsFichier());
        btnViewMetadata.addActionListener(e -> montrerMetadata());
        btnSnapshotSave.addActionListener(e -> onSnapshotSave());
        btnSnapshotCompare.addActionListener(e -> onSnapshotCompare());

        buttonPanel.add(btnSelectDirectory);
        buttonPanel.add(btnViewStatistics);
        buttonPanel.add(btnViewFileStatistics);
        buttonPanel.add(btnViewMetadata);
        buttonPanel.add(btnSnapshotSave);
        buttonPanel.add(btnSnapshotCompare);

        panelTop.add(buttonPanel, BorderLayout.CENTER);

        add(panelTop, BorderLayout.NORTH);
    }


    /**
     * Crée le panneau central pour le tableau des images.
     */
    private void createCenterPanel() {
        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableFiles = new JTable(tableModel);
        tableFiles.setFillsViewportHeight(true);

        tableFiles.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableFiles.getSelectedRow();
                if (selectedRow != -1) {
                    String filePath = (String) tableModel.getValueAt(selectedRow, 2);
                    displaySelectedImage(new File(filePath));
                }
            }
        });

        JScrollPane scrollPaneTable = new JScrollPane(tableFiles);
        panelCenter.add(scrollPaneTable, BorderLayout.CENTER);

        add(panelCenter, BorderLayout.CENTER);
    }

    /**
     * Crée le panneau inférieur pour la prévisualisation de l'image.
     */
    private void createBottomPanel() {
        JPanel panelBottom = new JPanel(new BorderLayout());
        panelBottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblImagePreview = new JLabel("Aucune image sélectionnée", JLabel.CENTER);
        lblImagePreview.setVerticalAlignment(JLabel.CENTER);
        lblImagePreview.setHorizontalAlignment(JLabel.CENTER);
        lblImagePreview.setBorder(BorderFactory.createTitledBorder("Image sélectionnée"));

        panelBottom.add(lblImagePreview, BorderLayout.CENTER);

        add(panelBottom, BorderLayout.SOUTH);
    }

    /**
     * Affiche l'image sélectionnée.
     *
     * @param file Le fichier image sélectionné.
     */
    private void displaySelectedImage(File file) {
        try {
            if (file.exists() && (file.getName().toLowerCase().endsWith(".png") ||
                    file.getName().toLowerCase().endsWith(".jpeg") ||
                    file.getName().toLowerCase().endsWith(".jpg") ||
                    file.getName().toLowerCase().endsWith(".webp"))) {
                BufferedImage image = ImageIO.read(file);
                if (image != null) {
                    Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    lblImagePreview.setIcon(new ImageIcon(scaledImage));
                    lblImagePreview.setText("");
                } else {
                    lblImagePreview.setIcon(null);
                    lblImagePreview.setText("Format non pris en charge.");
                }
            } else {
                lblImagePreview.setIcon(null);
                lblImagePreview.setText("Fichier non pris en charge ou inexistant.");
            }
        } catch (IOException ex) {
            lblImagePreview.setIcon(null);
            lblImagePreview.setText("Erreur lors du chargement de l'image.");
            ex.printStackTrace();
        }
    }

    private void chargerRepertoire() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            manageGUI.loadDirectory(selectedDirectory);
        }
    }

    private void afficherStatsFichier() {
        int selectedRow = tableFiles.getSelectedRow();
        if (selectedRow != -1) {
            String filePath = (String) tableModel.getValueAt(selectedRow, 2);
            manageGUI.viewFileStatistics(new File(filePath));
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un fichier dans le tableau.", "Alerte", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void montrerMetadata() {
        int selectedRow = tableFiles.getSelectedRow();
        if (selectedRow != -1) {
            String filePath = (String) tableModel.getValueAt(selectedRow, 2);
            manageGUI.viewMetadata(new File(filePath));
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un fichier dans le tableau.", "Alerte", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onSnapshotSave() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sauvegarder Snapshot");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
				manageGUI.saveSnapshot(selectedFile.getAbsolutePath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    private void onSnapshotCompare() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Charger Snapshot pour Comparaison");

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
				manageGUI.compareSnapshot(selectedFile.getAbsolutePath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}



// File: ./src/GUI/MainGui.java
package GUI;


import javax.swing.SwingUtilities;

/**
 * Classe principale pour lancer l'interface graphique de l'application.
 *
 * Cette classe contient le point d'entrée principal pour exécuter l'application
 * avec une interface utilisateur basée sur Swing.
 *
 * @author Gaetan et Yanis
 */
public class MainGui {

    /**
     * Point d'entrée principal de l'application.
     *
     * Cette méthode initialise et lance l'interface graphique de l'application.
     *
     * @param args Les arguments de ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GuiGraphique gui = new GuiGraphique();
            gui.setVisible(true);
        });
    }
}



// File: ./src/snapshot/Difference.java
package snapshot;

import java.io.Serializable;
import java.util.ArrayList;

import data.Fichier;


public class Difference implements Serializable {

    private static final long serialVersionUID = 1L; // Identifiant pour la sérialisation
    private ArrayList<Fichier> fichiersAjoutes;
    private ArrayList<Fichier> fichiersSupprimes;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	// Constructeur
    public Difference() {
        this.fichiersAjoutes = new ArrayList<>();
        this.fichiersSupprimes = new ArrayList<>();
    }

    // Ajouts
    public void ajouterFichierAjoute(Fichier fichier) {
        fichiersAjoutes.add(fichier);
    }

    public void ajouterFichierSupprime(Fichier fichier) {
        fichiersSupprimes.add(fichier);
    }

    // Affichage des différences
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Vérifier si aucune différence n'est détectée
        if (fichiersAjoutes.isEmpty() && fichiersSupprimes.isEmpty()) {
            sb.append("Aucune différence détectée.");
            return sb.toString();
        }

        sb.append("Différences détectées :\n");

        // Afficher les fichiers ajoutés avec leur nombre
        sb.append(String.format("Fichiers ajoutés (%d) :\n", fichiersAjoutes.size()));
        if (fichiersAjoutes.isEmpty()) {
            sb.append("  Aucun fichier ajouté.\n");
        } else {
            for (Fichier fichier : fichiersAjoutes) {
                sb.append(String.format("  - Nom : %-20s Type : %-15s Chemin : %s\n",
                        fichier.getNom(),
                        fichier.getStatFichier().getType(),
                        fichier.getCheminRelatif()));
            }
        }

        // Afficher les fichiers supprimés avec leur nombre
        sb.append(String.format("Fichiers supprimés (%d) :\n", fichiersSupprimes.size()));
        if (fichiersSupprimes.isEmpty()) {
            sb.append("  Aucun fichier supprimé.\n");
        } else {
            for (Fichier fichier : fichiersSupprimes) {
                sb.append(String.format("  - Nom : %-20s Type : %-15s Chemin : %s\n",
                        fichier.getNom(),
                        fichier.getStatFichier().getType(),
                        fichier.getCheminRelatif()));
            }
        }

        return sb.toString();
    }

    public ArrayList<Fichier> getFichiersAjoutes() {
		return fichiersAjoutes;
	}

	public void setFichiersAjoutes(ArrayList<Fichier> fichiersAjoutes) {
		this.fichiersAjoutes = fichiersAjoutes;
	}

	public ArrayList<Fichier> getFichiersSupprimes() {
		return fichiersSupprimes;
	}

	public void setFichiersSupprimes(ArrayList<Fichier> fichiersSupprimes) {
		this.fichiersSupprimes = fichiersSupprimes;
	}
}


// File: ./src/snapshot/Snapshot.java
package snapshot;

import java.io.*;
import data.*;
import java.util.ArrayList;
import java.util.List;

public class Snapshot implements Serializable {

    private static final long serialVersionUID = 1L; // Identifiant pour la sérialisation
    private Repertoire repertoire; // Répertoire capturé
    private String dateSnapshot;   // Date de création du snapshot

    // Constructeur
    public Snapshot(Repertoire repertoire, String dateSnapshot) {
        this.repertoire = repertoire;
        this.dateSnapshot = dateSnapshot;
    }

    // Sauvegarder le snapshot dans un fichier binaire
    public void sauvegarder(String cheminFichier) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cheminFichier))) {
            oos.writeObject(this); // Sérialisation de l'objet Snapshot
            System.out.println("Snapshot sauvegardé avec succès dans : " + cheminFichier);
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde du snapshot : " + e.getMessage());
        }
    }

    // Charger un snapshot à partir d'un fichier binaire
    public static Snapshot charger(String cheminFichier) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cheminFichier))) {
            return (Snapshot) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Erreur : Fichier snapshot introuvable : " + cheminFichier);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erreur lors du chargement du snapshot : " + e.getMessage());
        }
        return null;
    }

    // Comparer deux snapshots pour générer une différence
    public Difference comparer(Snapshot ancienSnapshot) {
        Difference difference = new Difference();

        // Récupérer les fichiers des deux snapshots
        List<Fichier> anciensFichiers = ancienSnapshot.repertoire.getFichiers();
        List<Fichier> actuelsFichiers = this.repertoire.getFichiers();

        // Identifier les fichiers ajoutés
        for (Fichier actuel : actuelsFichiers) {
            boolean existe = anciensFichiers.stream()
                    .anyMatch(ancien -> ancien.getCheminRelatif().equals(actuel.getCheminRelatif()));
            if (!existe) {
                difference.ajouterFichierAjoute(actuel);
            }
        }

        // Identifier les fichiers supprimés
        for (Fichier ancien : anciensFichiers) {
            boolean existe = actuelsFichiers.stream()
                    .anyMatch(actuel -> actuel.getCheminRelatif().equals(ancien.getCheminRelatif()));
            if (!existe) {
                difference.ajouterFichierSupprime(ancien);
            }
        }

        return difference;
    }

    // Getters et Setters
    public Repertoire getRepertoire() {
        return repertoire;
    }

    public void setRepertoire(Repertoire repertoire) {
        this.repertoire = repertoire;
    }

    public String getDateSnapshot() {
        return dateSnapshot;
    }

    public void setDateSnapshot(String dateSnapshot) {
        this.dateSnapshot = dateSnapshot;
    }
}

