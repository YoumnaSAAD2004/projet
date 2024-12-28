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


