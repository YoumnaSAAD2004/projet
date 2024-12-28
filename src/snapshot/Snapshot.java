package snapshot;


import data.Repertoire;
import data.Fichier;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Snapshot implements Serializable {

    private static final long serialVersionUID = 1L;
    private Repertoire repertoire;
    private String dateSnapshot;

    // Constructeur
    public Snapshot(Repertoire repertoire, String dateSnapshot) {
        this.repertoire = repertoire;
        this.dateSnapshot = dateSnapshot;
    }

    // Sauvegarder le snapshot dans un fichier binaire
    public void sauvegarder(String cheminFichier) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cheminFichier))) {
            oos.writeObject(this);
            System.out.println("Snapshot sauvegardé dans : " + cheminFichier);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde du snapshot : " + e.getMessage());
        }
    }

    // Charger un snapshot depuis un fichier binaire
    public static Snapshot charger(String cheminFichier) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cheminFichier))) {
            return (Snapshot) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement du snapshot : " + e.getMessage());
        }
        return null;
    }

    // Comparer deux snapshots pour détecter les différences
    public Difference comparer(Snapshot ancienSnapshot) {
        Difference difference = new Difference();

        List<Fichier> anciensFichiers = ancienSnapshot.getRepertoire().getFichiers();
        List<Fichier> actuelsFichiers = this.repertoire.getFichiers();

        // Fichiers ajoutés
        for (Fichier actuel : actuelsFichiers) {
            boolean existe = anciensFichiers.stream()
                    .anyMatch(ancien -> ancien.getNom().equals(actuel.getNom()));
            if (!existe) {
                difference.ajouterFichierAjoute(actuel.getNom());
            }
        }

        // Fichiers supprimés
        for (Fichier ancien : anciensFichiers) {
            boolean existe = actuelsFichiers.stream()
                    .anyMatch(actuel -> actuel.getNom().equals(ancien.getNom()));
            if (!existe) {
                difference.ajouterFichierSupprime(ancien.getNom());
            }
        }

        return difference;
    }

    // Getters
    public Repertoire getRepertoire() {
        return repertoire;
    }

    public String getDateSnapshot() {
        return dateSnapshot;
    }
}
