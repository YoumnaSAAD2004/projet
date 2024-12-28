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
 * Classee représentant un fichier image avec ses métadonnées et ses statistiques.
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


