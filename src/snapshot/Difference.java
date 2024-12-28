package snapshot;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Difference implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<String> fichiersAjoutes;
    private List<String> fichiersSupprimes;

    // Constructeur
    public Difference() {
        this.fichiersAjoutes = new ArrayList<>();
        this.fichiersSupprimes = new ArrayList<>();
    }

    // Ajoute un fichier à la liste des fichiers ajoutés
    public void ajouterFichierAjoute(String fichier) {
        fichiersAjoutes.add(fichier);
    }

    // Ajoute un fichier à la liste des fichiers supprimés
    public void ajouterFichierSupprime(String fichier) {
        fichiersSupprimes.add(fichier);
    }

    // Afficher les différences
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== Différences détectées ===\n");

        sb.append("Fichiers ajoutés :\n");
        for (String fichier : fichiersAjoutes) {
            sb.append("  - ").append(fichier).append("\n");
        }

        sb.append("Fichiers supprimés :\n");
        for (String fichier : fichiersSupprimes) {
            sb.append("  - ").append(fichier).append("\n");
        }

        return sb.toString();
    }
}
