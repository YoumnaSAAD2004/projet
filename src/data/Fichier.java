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
