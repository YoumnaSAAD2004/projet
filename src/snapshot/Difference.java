package snapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente les différences entre deux snapshots.
 */
public class Difference {

    private List<String> fichiersAjoutes;
    private List<String> fichiersSupprimes;
    private List<String> fichiersModifies;

    /**
     * Constructeur : initialise des listes vides.
     */
    public Difference() {
        this.fichiersAjoutes = new ArrayList<>();
        this.fichiersSupprimes = new ArrayList<>();
        this.fichiersModifies = new ArrayList<>();
    }

    /**
     * Ajoute un fichier à la liste des fichiers ajoutés.
     * @param fichier Le nom du fichier ajouté.
     */
    public void ajouterFichierAjoute(String fichier) {
        fichiersAjoutes.add(fichier);
    }

    /**
     * Ajoute un fichier à la liste des fichiers supprimés.
     * @param fichier Le nom du fichier supprimé.
     */
    public void ajouterFichierSupprime(String fichier) {
        fichiersSupprimes.add(fichier);
    }

    /**
     * Ajoute un fichier à la liste des fichiers modifiés.
     * @param fichier Le nom du fichier modifié.
     */
    public void ajouterFichierModifie(String fichier) {
        fichiersModifies.add(fichier);
    }

    /**
     * Retourne une représentation textuelle des différences.
     * @return String décrivant les différences.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== Différences détectées ===\n");

        // Fichiers ajoutés
        sb.append("Fichiers ajoutés :\n");
        if (fichiersAjoutes.isEmpty()) {
            sb.append("  Aucun fichier ajouté.\n");
        } else {
            fichiersAjoutes.forEach(f -> sb.append("  - ").append(f).append("\n"));
        }

        // Fichiers supprimés
        sb.append("Fichiers supprimés :\n");
        if (fichiersSupprimes.isEmpty()) {
            sb.append("  Aucun fichier supprimé.\n");
        } else {
            fichiersSupprimes.forEach(f -> sb.append("  - ").append(f).append("\n"));
        }

        // Fichiers modifiés
        sb.append("Fichiers modifiés :\n");
        if (fichiersModifies.isEmpty()) {
            sb.append("  Aucun fichier modifié.\n");
        } else {
            fichiersModifies.forEach(f -> sb.append("  - ").append(f).append("\n"));
        }

        return sb.toString();
    }
}