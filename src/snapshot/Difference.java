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
