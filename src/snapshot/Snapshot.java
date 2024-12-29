package snapshot;

import java.io.*;
import data.*;
import java.util.ArrayList;
import java.util.List;

public class Snapshot implements Serializable {

    private static final long serialVersionUID = 1L; // Identifiant pour la sérialisation
    private Repertoire repertoire; // Répertoire capturé
    private String dateSnapshot;   // Date de création du snapshot

    // Constructeur
    public Snapshot(Repertoire repertoire, String dateSnapshot) {
        this.repertoire = repertoire;
        this.dateSnapshot = dateSnapshot;
    }

    // Sauvegarder le snapshot dans un fichier binaire
    public void sauvegarder(String cheminFichier) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cheminFichier))) {
            oos.writeObject(this); // Sérialisation de l'objet Snapshot
            System.out.println("Snapshot sauvegardé avec succès dans : " + cheminFichier);
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde du snapshot : " + e.getMessage());
        }
    }

    // Charger un snapshot à partir d'un fichier binaire
    public static Snapshot charger(String cheminFichier) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cheminFichier))) {
            return (Snapshot) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Erreur : Fichier snapshot introuvable : " + cheminFichier);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erreur lors du chargement du snapshot : " + e.getMessage());
        }
        return null;
    }

    // Comparer deux snapshots pour générer une différence
    public Difference comparer(Snapshot ancienSnapshot) {
        Difference difference = new Difference();

        // Récupérer les fichiers des deux snapshots
        List<Fichier> anciensFichiers = ancienSnapshot.repertoire.getFichiers();
        List<Fichier> actuelsFichiers = this.repertoire.getFichiers();

        // Identifier les fichiers ajoutés
        for (Fichier actuel : actuelsFichiers) {
            boolean existe = anciensFichiers.stream()
                    .anyMatch(ancien -> ancien.getCheminRelatif().equals(actuel.getCheminRelatif()));
            if (!existe) {
                difference.ajouterFichierAjoute(actuel);
            }
        }

        // Identifier les fichiers supprimés
        for (Fichier ancien : anciensFichiers) {
            boolean existe = actuelsFichiers.stream()
                    .anyMatch(actuel -> actuel.getCheminRelatif().equals(ancien.getCheminRelatif()));
            if (!existe) {
                difference.ajouterFichierSupprime(ancien);
            }
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
