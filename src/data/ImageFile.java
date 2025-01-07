package data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

/**
 * Classe représentant un élément de fichier dans un dossier.
 * 
 * Cette classe encapsule les attributs et opérations permettant d'interagir
 * et de récupérer des informations sur un fichier, comme son nom, son chemin,
 * sa taille, son type MIME et sa date de modification.
 * 
 * @author Youmna Saad et Seyda Ann
 */
public class ImageFile {
    private File fichierPhysique; // Instance du fichier physique sur le système

    /**
     * Constructeur pour initialiser un objet ImageFile à partir d'un fichier existant.
     * 
     * @param fichier Le fichier sur le disque.
     */
    public ImageFile(File fichier) {
        this.fichierPhysique = fichier;
    }

    /**
     * Récupère le nom du fichier.
     * 
     * @return Le nom de base du fichier.
     */
    public String getFileName() {
        return fichierPhysique.getName();
    }

    /**
     * Récupère le chemin absolu du fichier.
     * 
     * @return Le chemin complet du fichier.
     */
    public String getFilePath() {
        return fichierPhysique.getAbsolutePath();
    }

    /**
     * Récupère la taille en octets du fichier.
     * 
     * @return La taille en octets.
     */
    public long getFileSize() {
        return fichierPhysique.length();
    }

    /**
     * Récupère la date de dernière modification du fichier.
     * 
     * @return La date de modification sous forme d'objet Date.
     */
    public Date getLastModified() {
        return new Date(fichierPhysique.lastModified());
    }

    /**
     * Identifie le type MIME du fichier.
     * 
     * Utilise les fonctionnalités de Java NIO pour déterminer le type MIME.
     * 
     * @return Le type MIME sous forme de chaîne de caractères.
     * @throws IOException En cas d'erreur d'accès au fichier.
     */
    public String getMimeType() throws IOException {
        return Files.probeContentType(fichierPhysique.toPath());
    }
}
