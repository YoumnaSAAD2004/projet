
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



// File: ./src/data/ImageMetadata.java
package data;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.*;
import com.drew.metadata.exif.ExifThumbnailDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.xmp.XmpDirectory;

import java.io.File;
import java.util.Optional;

/**
 * Classe pour analyser et récupérer les informations clés d'une image.
 * 
 * Cette classe explore les métadonnées EXIF, XMP et GPS à partir d'un fichier image
 * à l'aide de la bibliothèque `drewnoakes/metadata-extractor`.
 * 
 * @author Youmna Saad et Seyda Ann
 */
public class ImageMetadata {

    private File imageFile;
    private int width = 0;
    private int height = 0;
    private String dpiResolution = "Indisponible";
    private String title = "Non spécifié";
    private String description = "Non spécifiée";
    private String gpsCoordinates = "Non spécifiées";
    private boolean thumbnailAvailable = false;

    /**
     * Constructeur pour analyser un fichier image et en extraire les métadonnées.
     *
     * @param imageFile Le fichier image à analyser.
     * @throws Exception Si une erreur survient pendant l'extraction des métadonnées.
     */
    public ImageMetadata(File imageFile) throws Exception {
        this.imageFile = imageFile;
        extractMetadata();
    }

    /**
     * Méthode privée pour extraire les métadonnées de l'image.
     *
     * Cette méthode parcourt les différents répertoires pour extraire des
     * informations telles que les dimensions, les titres, les coordonnées GPS, etc.
     *
     * @throws Exception Si une erreur survient pendant l'extraction.
     */
    private void extractMetadata() throws Exception {
        Metadata metadata = ImageMetadataReader.readMetadata(imageFile);

        boolean dimensionsFound = false;

        // Parcours des répertoires pour récupérer les données
        for (Directory directory : metadata.getDirectories()) {
            if (directory instanceof XmpDirectory xmpDir) {
                title = xmpDir.getXmpProperties().getOrDefault("dc:title[1]", "Non spécifié");
                description = xmpDir.getXmpProperties().getOrDefault("dc:description[1]", "Non spécifiée");
            }

            for (Tag tag : directory.getTags()) {
                String tagName = tag.getTagName();
                String tagValue = tag.getDescription();

                if ("Image Width".equals(tagName)) {
                    width = Integer.parseInt(tagValue.replace(" pixels", "").trim());
                    dimensionsFound = true;
                }
                if ("Image Height".equals(tagName)) {
                    height = Integer.parseInt(tagValue.replace(" pixels", "").trim());
                    dimensionsFound = true;
                }
            }

            if (directory instanceof GpsDirectory gpsDir) {
                gpsCoordinates = Optional.ofNullable(gpsDir.getGeoLocation())
                        .map(location -> "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude())
                        .orElse("Non spécifiées");
            }

            if (directory instanceof ExifThumbnailDirectory) {
                thumbnailAvailable = true;
            }
        }

        if (!dimensionsFound) {
            width = height = 0;
        }
    }

    /**
     * Retourne si une miniature est disponible pour l'image.
     *
     * @return true si une miniature est présente, sinon false.
     */
    public boolean isThumbnailAvailable() {
        return thumbnailAvailable;
    }

    /**
     * Fournit une vue lisible des métadonnées de l'image.
     *
     * @return Une chaîne décrivant les métadonnées essentielles.
     */
    @Override
    public String toString() {
        return "Informations sur l'image :\n" +
                "Nom du fichier : " + imageFile.getName() + "\n" +
                "Dimensions : " + (width > 0 && height > 0 ? width + "x" + height + " px" : "Indisponibles") + "\n" +
                "Résolution DPI : " + dpiResolution + "\n" +
                "Titre : " + title + "\n" +
                "Description : " + description + "\n" +
                "Coordonnées GPS : " + gpsCoordinates + "\n" +
                "Miniature : " + (thumbnailAvailable ? "Oui" : "Non");
    }
}


// File: ./src/data/Repertoire.java
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


// File: ./src/data/ControleurR.java
package data;

import java.util.List;

/**
 * Classe permettant d'examiner et de présenter les fichiers image trouvés.
 * 
 * Cette classe propose des fonctionnalités pour :
 * - Lister les fichiers image.
 * - Afficher les informations détaillées sur chaque fichier image.
 * 
 * @author Youmna Saad et Seyda Ann
 */
public class ControleurR {

    /**
     * Collection des fichiers image à examiner.
     */
    private List<ImageFile> listeImages;

    /**
     * Constructeur pour configurer la liste des fichiers image.
     *
     * @param listeImages La liste des fichiers image à examiner.
     * @throws IllegalArgumentException Si la liste des fichiers est nulle.
     */
    public ControleurR(List<ImageFile> listeImages) {
        if (listeImages == null) {
            throw new IllegalArgumentException("La liste des fichiers ne peut pas être nulle.");
        }
        this.listeImages = listeImages;
    }

    /**
     * Liste les noms des fichiers image dans la console.
     * 
     * Affiche un message si aucun fichier n'est présent dans la liste.
     */
    public void listerNomsImages() {
        if (listeImages.isEmpty()) {
            System.out.println("Aucun fichier image détecté.");
        } else {
            System.out.println("Noms des fichiers image :");
            for (ImageFile image : listeImages) {
                System.out.println(image.getFileName());
            }
        }
    }

    /**
     * Affiche les informations détaillées pour chaque fichier image.
     * 
     * Les informations incluent : le nom, le chemin absolu, la taille en octets,
     * et la date de dernière modification.
     * Affiche un message si aucun fichier n'est présent.
     */
    public void afficherInformationsCompletes() {
        if (listeImages.isEmpty()) {
            System.out.println("Aucun fichier image détecté.");
        } else {
            System.out.println("Détails des fichiers image :");
            for (ImageFile image : listeImages) {
                System.out.println("Nom du fichier : " + image.getFileName());
                System.out.println("Chemin complet : " + image.getFilePath());
                System.out.println("Taille du fichier : " + image.getFileSize() + " octets");
                System.out.println("Date de modification : " + image.getLastModified());
                System.out.println("--------------------------------------------------");
            }
        }
    }
}


// File: ./src/data/ImageFile.java
package data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

/**
 * Classe représentant un élément de fichier dans un dossier.
 * 
 * Cette classe encapsule les attributs et opérations permettant d'interagir
 * et de récupérer des informations sur un fichier, comme son nom, son chemin,
 * sa taille, son type MIME et sa date de modification.
 * 
 * @author Youmna Saad et Seyda Ann
 */
public class ImageFile {
    private File fichierPhysique; // Instance du fichier physique sur le système

    /**
     * Constructeur pour initialiser un objet ImageFile à partir d'un fichier existant.
     * 
     * @param fichier Le fichier sur le disque.
     */
    public ImageFile(File fichier) {
        this.fichierPhysique = fichier;
    }

    /**
     * Récupère le nom du fichier.
     * 
     * @return Le nom de base du fichier.
     */
    public String getFileName() {
        return fichierPhysique.getName();
    }

    /**
     * Récupère le chemin absolu du fichier.
     * 
     * @return Le chemin complet du fichier.
     */
    public String getFilePath() {
        return fichierPhysique.getAbsolutePath();
    }

    /**
     * Récupère la taille en octets du fichier.
     * 
     * @return La taille en octets.
     */
    public long getFileSize() {
        return fichierPhysique.length();
    }

    /**
     * Récupère la date de dernière modification du fichier.
     * 
     * @return La date de modification sous forme d'objet Date.
     */
    public Date getLastModified() {
        return new Date(fichierPhysique.lastModified());
    }

    /**
     * Identifie le type MIME du fichier.
     * 
     * Utilise les fonctionnalités de Java NIO pour déterminer le type MIME.
     * 
     * @return Le type MIME sous forme de chaîne de caractères.
     * @throws IOException En cas d'erreur d'accès au fichier.
     */
    public String getMimeType() throws IOException {
        return Files.probeContentType(fichierPhysique.toPath());
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


// File: ./src/engine/StatF.java
package engine;

import java.io.IOException;
import data.ImageFile;

/**
 * Classe pour générer et afficher des détails spécifiques sur un fichier.
 * 
 * Cette classe fournit des méthodes pour afficher des informations comme :
 * - Le type MIME.
 * - La taille du fichier.
 * - La date de modification.
 * 
 * @author Youmna Saad et Seyda Ann
 */
public class StatF {

    private ImageFile imageFile;

    /**
     * Constructeur pour initialiser l'objet avec un fichier.
     * 
     * @param imageFile Le fichier dont les détails seront affichés.
     */
    public StatF(ImageFile imageFile) {
        this.imageFile = imageFile;
    }

    /**
     * Affiche les détails du fichier, notamment :
     * - Nom.
     * - Chemin absolu.
     * - Type MIME.
     * - Taille en octets.
     * - Dernière date de modification.
     */
    public void displayDetails() {
        try {
            System.out.println("Détails du fichier :");
            System.out.println("Nom du fichier : " + imageFile.getFileName());
            System.out.println("Chemin complet : " + imageFile.getFilePath());
            System.out.println("Type MIME : " + imageFile.getMimeType());
            System.out.println("Taille : " + imageFile.getFileSize() + " octets");
            System.out.println("Dernière modification : " + imageFile.getLastModified());
            System.out.println("--------------------------------------------------");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'affichage des détails du fichier : " + e.getMessage());
        }
    }
}


// File: ./src/engine/ModeRech.java
package engine;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import data.ImageFile;

/**
 * Classe pour effectuer des recherches sur des fichiers image.
 * 
 * Cette classe fournit des méthodes pour rechercher des fichiers image par
 * leur nom ou par l'année de leur dernière modification. Elle peut également
 * afficher les résultats de recherche à l'écran.
 * 
 * @author Youmna Saad et Seyda Ann
 */
public class ModeRech {

    private List<ImageFile> imageFiles; // Liste des fichiers image disponibles

    /**
     * Constructeur pour initialiser la liste des fichiers image à analyser.
     *
     * @param imageFiles La liste des fichiers image.
     * @throws IllegalArgumentException Si la liste est nulle.
     */
    public ModeRech(List<ImageFile> imageFiles) {
        if (imageFiles == null) {
            throw new IllegalArgumentException("La liste des fichiers image ne peut pas être nulle.");
        }
        this.imageFiles = imageFiles;
    }

    /**
     * Recherche des images par nom ou partie du nom.
     *
     * @param partialName Nom complet ou fragment de nom à rechercher.
     * @return Une liste de fichiers correspondant aux critères.
     */
    public List<ImageFile> findByName(String partialName) {
        return imageFiles.stream()
                .filter(file -> {
                    String name = file.getFileName().toLowerCase();
                    return name.contains(partialName.toLowerCase()) ||
                           (name.contains(partialName.toLowerCase()) &&
                           (name.endsWith(".jpeg") || name.endsWith(".jpg")));
                })
                .collect(Collectors.toList());
    }

    /**
     * Recherche des images par année de dernière modification.
     *
     * @param year Année de dernière modification.
     * @return Une liste de fichiers correspondant à l'année donnée.
     */
    public List<ImageFile> findByYear(int year) {
        return imageFiles.stream()
                .filter(file -> {
                    Date lastModified = file.getLastModified();
                    return lastModified.getYear() + 1900 == year; // Ajout de 1900 pour une année correcte
                })
                .collect(Collectors.toList());
    }

    /**
     * Affiche les résultats de la recherche.
     *
     * @param results Liste des fichiers correspondant aux critères de recherche.
     */
    public void displayResults(List<ImageFile> results) {
        if (results.isEmpty()) {
            System.out.println("Aucun fichier ne correspond aux critères de recherche.");
        } else {
            System.out.println("Fichiers trouvés :");
            results.forEach(file -> 
                System.out.println("- " + file.getFileName() + " (Chemin : " + file.getFilePath() + ")"));
        }
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


// File: ./src/engine/StatR.java
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


// File: ./src/partieGui/GUIOrganisation.java
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


// File: ./src/partieGui/GuiGraphique.java
package partieGui;

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
 * @author Youmna Saad et Seyda Ann
 */
public class GuiGraphique extends JFrame {

    private JTable tableFiles;
    private DefaultTableModel tableModel;
    private JLabel lblImagePreview;
    private GUIOrganisation manageGUI;

    public GuiGraphique() {
        setTitle("Projet POO Image et metadonnee");
        setSize(1920, 800);
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
    private void createTopPanel() {
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Section boutons horizontaux
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton btnSelectDirectory = new JButton("choisir un répertoire");
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

        // Section barre de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JTextField tfSearch = new JTextField(15);
        JButton btnSearch = new JButton("Rechercher");
        btnSearch.addActionListener(e -> manageGUI.searchFiles(tfSearch.getText().trim()));
        tfSearch.addActionListener(e -> btnSearch.doClick());
        searchPanel.add(new JLabel("Rechercher :"));
        searchPanel.add(tfSearch);
        searchPanel.add(btnSearch);

        panelTop.add(buttonPanel, BorderLayout.CENTER);
        panelTop.add(searchPanel, BorderLayout.EAST);

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
            manageGUI.saveSnapshot(selectedFile.getAbsolutePath());
        }
    }

    private void onSnapshotCompare() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Charger Snapshot pour Comparaison");

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            manageGUI.compareSnapshot(selectedFile.getAbsolutePath());
        }
    }
}


// File: ./src/partieGui/MainGui.java
package partieGui;

import javax.swing.SwingUtilities;

/**
 * Classe principale pour lancer l'interface graphique de l'application.
 *
 * Cette classe contient le point d'entrée principal pour exécuter l'application
 * avec une interface utilisateur basée sur Swing.
 *
 * @author Youmna Saad et Seyda Ann
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


// File: ./src/partieConsole/MainTestConsole.java
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


// File: ./src/partieConsole/MainConsole.java
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


// File: ./src/Snapshot/Snapshot.java
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

