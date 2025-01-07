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
