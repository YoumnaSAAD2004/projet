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
