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
