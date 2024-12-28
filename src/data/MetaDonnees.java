package data;

import java.io.Serializable;

/**
 * Représentee les métadonnées associées à un fichier.
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

