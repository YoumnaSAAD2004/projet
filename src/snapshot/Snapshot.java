package snapshot;


import data.Repertoire;
import data.Fichier;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Représente un état (snapshot) d'un répertoire à un instant donné.
 */
public class Snapshot implements Serializable {

    private static final long serialVersionUID = 1L; // Identifiant pour la sérialisation
    private Repertoire repertoire; // Répertoire capturé
    private String dateSnapshot; // Date de création du snapshot

    /**
     * Constructeur pour capturer un snapshot d'un répertoire.
     *
     * @param repertoire Le répertoire capturé.
     * @param dateSnapshot La date de création du snapshot.
     */
    public Snapshot(Repertoire repertoire, String dateSnapshot) {
        this.repertoire = repertoire;
        this.dateSnapshot = dateSnapshot;
    }

    /**
     * Sauvegarder le snapshot dans un fichier binaire.
     *
     * @param cheminFichier Le chemin du fichier où sauvegarder le snapshot.
     */
    public void sauvegarder(String cheminFichier) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cheminFichier))) {
            oos.writeObject(this);
            System.out.println("Snapshot sauvegardé avec succès dans : " + cheminFichier);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde du snapshot : " + e.getMessage());
        }
    }

    /**
     * Charger un snapshot à partir d'un fichier binaire.
     *
     * @param cheminFichier Le chemin du fichier à charger.
     * @return L'objet Snapshot chargé ou null en cas d'erreur.
     */
    public static Snapshot charger(String cheminFichier) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cheminFichier))) {
            return (Snapshot) ois.readObject();
        } catch (FileNotFoundException e) {
            System.err.println("Erreur : Fichier snapshot introuvable : " + cheminFichier);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement du snapshot : " + e.getMessage());
        }
        return null;
    }

    /**
     * Comparer deux snapshots pour détecter les différences.
     *
     * @param ancienSnapshot Le snapshot précédent à comparer.
     * @return Un objet Difference contenant les ajouts, suppressions et modifications.
     */
    public Difference comparer(Snapshot ancienSnapshot) {
        Difference difference = new Difference();

        // Conversion des fichiers actuels et anciens en ArrayList
        ArrayList<Fichier> actuelsFichiers = new ArrayList<>(this.repertoire.getFichiers());
        ArrayList<Fichier> anciensFichiers = new ArrayList<>(ancienSnapshot.repertoire.getFichiers());

        // Identifier les fichiers ajoutés
        for (Fichier actuel : actuelsFichiers) {
            boolean existe = anciensFichiers.stream()
                    .anyMatch(ancien -> ancien.getNom().equals(actuel.getNom()) && ancien.getStatistiques().getTaille() == actuel.getStatistiques().getTaille());
            if (!existe) {
                difference.ajouterFichierAjoute(actuel.getNom());
            }
        }

        // Identifier les fichiers supprimés
        for (Fichier ancien : anciensFichiers) {
            boolean existe = actuelsFichiers.stream()
                    .anyMatch(actuel -> actuel.getNom().equals(ancien.getNom()) && actuel.getStatistiques().getTaille() == ancien.getStatistiques().getTaille());
            if (!existe) {
                difference.ajouterFichierSupprime(ancien.getNom());
            }
        }

        // Identifier les fichiers modifiés
        for (Fichier actuel : actuelsFichiers) {
            anciensFichiers.stream()
                    .filter(ancien -> ancien.getNom().equals(actuel.getNom()) && ancien.getStatistiques().getTaille() != actuel.getStatistiques().getTaille())
                    .forEach(ancien -> difference.ajouterFichierModifie(actuel.getNom()));
        }

        return difference;
    }

    // Getters et Setters

    public Repertoire getRepertoire() {
        return repertoire;
    }

    public void setRepertoire(Repertoire repertoire) {
        this.repertoire = repertoire;
    }

    public String getDateSnapshot() {
        return dateSnapshot;
    }

    public void setDateSnapshot(String dateSnapshot) {
        this.dateSnapshot = dateSnapshot;
    }
}
