package data;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un répertoire contenant des fichiers image et des sous-répertoires.
 * Cette classe parcourt récursivement un répertoire et collecte les fichiers image valides
 * ainsi que les sous-répertoires.
 */
public class Repertoire implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nom; // Nom du répertoire
    private List<Fichier> fichiers; // Liste des fichiers image dans le répertoire
    private List<Repertoire> sousRepertoires; // Liste des sous-répertoires
    private StatistiquesRepertoire statistiques; // Statistiques associées au répertoire

    /**
     * Constructeur complet.
     * @param nom Nom du répertoire.
     * @param fichiers Liste des fichiers image.
     * @param sousRepertoires Liste des sous-répertoires.
     * @param statistiques Statistiques associées au répertoire.
     */
    public Repertoire(String nom, List<Fichier> fichiers, List<Repertoire> sousRepertoires, StatistiquesRepertoire statistiques) {
        this.nom = nom;
        this.fichiers = fichiers;
        this.sousRepertoires = sousRepertoires;
        this.statistiques = statistiques;
    }

    /**
     * Constructeur simplifié qui initialise les listes et le nom.
     * @param nom Nom du répertoire.
     */
    public Repertoire(String nom) {
        this.nom = nom;
        this.fichiers = new ArrayList<>();
        this.sousRepertoires = new ArrayList<>();
        this.statistiques = new StatistiquesRepertoire(fichiers);
    }

    // Getters
    public String getNom() {
        return nom;
    }

    public List<Fichier> getFichiers() {
        return fichiers;
    }

    public List<Repertoire> getSousRepertoires() {
        return sousRepertoires;
    }

    public StatistiquesRepertoire getStatistiques() {
        return statistiques;
    }

    /**
     * Parcourt un répertoire et ses sous-répertoires pour récupérer les fichiers image valides.
     * Cette méthode utilise une approche récursive pour explorer toute l'arborescence.
     * 
     * @param directory Le répertoire à parcourir.
     * @throws IOException Si le répertoire n'existe pas ou n'est pas accessible.
     */
    public void parcourirRepertoire(File directory) throws IOException {
        // Vérifie si le répertoire est valide
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            throw new IOException("Le répertoire n'existe pas ou n'est pas valide : " + directory);
        }

        // Met à jour le nom du répertoire courant
        this.nom = directory.getName();

        // Liste tous les fichiers et sous-dossiers dans le répertoire courant
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Si l'élément est un sous-répertoire, crée un objet Repertoire et appelle récursivement la méthode
                    Repertoire sousRepertoire = new Repertoire(file.getName());
                    sousRepertoire.parcourirRepertoire(file); // Appel récursif
                    sousRepertoires.add(sousRepertoire); // Ajoute le sous-répertoire à la liste
                    System.out.println("les sous-répertoire : " + file.getName());
                } else if (file.isFile()) {
                    // Si l'élément est un fichier, vérifie s'il s'agit d'une image valide
                    Fichier fichier = new Fichier(file);
                    if (fichier.verifierTypeMIME()) {
                        fichiers.add(fichier); // Ajoute le fichier valide à la liste
                        System.out.println("Fichier valide : " + file.getName());
                    } else {
                        System.out.println("Fichier ignoré (non valide) : " + file.getName());
                    }
                }
            }
        }
    }


    /**
     * Recherche un fichier par son nom dans le répertoire courant.
     * @param fileName Le nom du fichier recherché.
     * @return Une instance de Fichier si trouvée, null sinon.
     */
    public Fichier getFichierByName(String fileName) {
        for (Fichier fichier : fichiers) {
            if (fichier.getNom().equalsIgnoreCase(fileName)) {
                return fichier; // Retourne le fichier si le nom correspond
            }
        }
        return null; // Retourne null si aucun fichier ne correspond
    }
    
    public List<Fichier> rechercherFichiers(String nomPartiel, Integer annee, int[] dimensions) {
        List<Fichier> resultats = new ArrayList<>();

        // Parcourir les fichiers dans le répertoire courant
        for (Fichier fichier : fichiers) {
            boolean correspond = true;

            // Filtrer par nom
            if (nomPartiel != null && !fichier.getNom().contains(nomPartiel)) {
                correspond = false;
            }

            // Filtrer par année
            if (annee != null) {
                String anneeFichier = fichier.getStatistiques().getDateModification().substring(24); // Vérifiez si cela fonctionne
                if (!anneeFichier.equals(annee.toString())) {
                    correspond = false;
                }
            }

            // Filtrer par dimensions
            if (dimensions != null) {
                int[] dimsFichier = fichier.getMetaDonnees().getDimensions();
                if (dimsFichier[0] != dimensions[0] || dimsFichier[1] != dimensions[1]) {
                    correspond = false;
                }
            }

            if (correspond) {
                resultats.add(fichier);
            }
        }

        // Chercher dans les sous-répertoires
        for (Repertoire sousRepertoire : sousRepertoires) {
            resultats.addAll(sousRepertoire.rechercherFichiers(nomPartiel, annee, dimensions));
        }

        return resultats;
    }



    /**
     * Retourne une représentation textuelle du répertoire, incluant le nombre de fichiers et de sous-répertoires.
     * @return Une chaîne de caractères décrivant le répertoire.
     */
    @Override
    public String toString() {
        return "Repertoire{" +
                "nom='" + nom + '\'' +
                ", fichiers=" + fichiers.size() +
                ", sousRepertoires=" + sousRepertoires.size() +
                '}';
    }
}
