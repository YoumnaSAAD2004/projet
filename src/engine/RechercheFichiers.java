package engine;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour effectuer une recherche dans un répertoire selon des critères.
 */
public class RechercheFichiers  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Recherche des fichiers selon les critères spécifiés.
     *
     * @param repertoire   Le répertoire à parcourir.
     * @param nomPartiel   Nom ou partie du nom à rechercher (peut être null).
     * @param anneeCreation Année de création à rechercher (peut être null).
     * @return Liste des fichiers correspondant aux critères.
     * @throws IOException En cas d'erreur de lecture des fichiers.
     */
    public static List<File> rechercherFichiers(File repertoire, String nomPartiel, Integer anneeCreation) throws IOException {
        List<File> resultats = new ArrayList<>();

        if (!repertoire.exists() || !repertoire.isDirectory()) {
            throw new IllegalArgumentException("Le chemin spécifié n'est pas un répertoire valide.");
        }

        // Parcourt récursivement le répertoire
        for (File fichier : repertoire.listFiles()) {
            if (fichier.isDirectory()) {
                // Recherche dans les sous-dossiers
                resultats.addAll(rechercherFichiers(fichier, nomPartiel, anneeCreation));
            } else if (fichier.isFile()) {
                boolean correspond = true;

                // Vérifie le critère du nom (contient la chaîne)
                if (nomPartiel != null && !fichier.getName().toLowerCase().contains(nomPartiel.toLowerCase())) {
                    correspond = false;
                }

                // Vérifie le critère de l'année de création
                if (anneeCreation != null) {
                    BasicFileAttributes attrs = Files.readAttributes(fichier.toPath(), BasicFileAttributes.class);
                    long creationTime = attrs.creationTime().toMillis();
                    String anneeFichier = new SimpleDateFormat("yyyy").format(creationTime);
                    if (!anneeFichier.equals(anneeCreation.toString())) {
                        correspond = false;
                    }
                }

                if (correspond) {
                    resultats.add(fichier);
                }
            }
        }

        return resultats;
    }
}

