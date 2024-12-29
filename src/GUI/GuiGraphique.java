package GUI;
import data.Fichier;
import data.MetaDonnees;
import data.Repertoire;
import data.StatistiquesFichier;
import data.StatistiquesRepertoire;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Classe pour gérer l'interface graphique de l'application.
 * 
 * Fournit une interface avec une disposition où les boutons sont alignés horizontalement en haut.
 * 
 * @author Gaetan et Yanis
 */
public class GuiGraphique extends JFrame {

    private JTable tableFiles;
    private DefaultTableModel tableModel;
    private JLabel lblImagePreview;
    private GUIOrganisation manageGUI;

    public GuiGraphique() {
        setTitle("Projet POO Image et metadonnee");
        setSize(1900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"Nom", "Taille (Ko)", "Chemin", "Dernière modification"}, 0);
        manageGUI = new GUIOrganisation(tableModel);

        setLayout(new BorderLayout(10, 10));

        createTopPanel();
        createCenterPanel();
        createBottomPanel();
    }

    /**
     * Crée le panneau supérieur avec les boutons alignés horizontalement et la barre de recherche.
     */
    /**
     * Creates the top panel with horizontally aligned buttons.
     */
    private void createTopPanel() {
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Section for horizontally aligned buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton btnSelectDirectory = new JButton("Choisir un répertoire");
        JButton btnViewStatistics = new JButton("Statistiques globales");
        JButton btnViewFileStatistics = new JButton("Statistiques du fichier");
        JButton btnViewMetadata = new JButton("Métadonnées");
        JButton btnSnapshotSave = new JButton("Sauvegarder Snapshot");
        JButton btnSnapshotCompare = new JButton("Comparer Snapshot");

        btnSelectDirectory.addActionListener(e -> chargerRepertoire());
        btnViewStatistics.addActionListener(e -> manageGUI.viewStatistics());
        btnViewFileStatistics.addActionListener(e -> afficherStatsFichier());
        btnViewMetadata.addActionListener(e -> montrerMetadata());
        btnSnapshotSave.addActionListener(e -> onSnapshotSave());
        btnSnapshotCompare.addActionListener(e -> onSnapshotCompare());

        buttonPanel.add(btnSelectDirectory);
        buttonPanel.add(btnViewStatistics);
        buttonPanel.add(btnViewFileStatistics);
        buttonPanel.add(btnViewMetadata);
        buttonPanel.add(btnSnapshotSave);
        buttonPanel.add(btnSnapshotCompare);

        panelTop.add(buttonPanel, BorderLayout.CENTER);

        add(panelTop, BorderLayout.NORTH);
    }


    /**
     * Crée le panneau central pour le tableau des images.
     */
    private void createCenterPanel() {
        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableFiles = new JTable(tableModel);
        tableFiles.setFillsViewportHeight(true);

        tableFiles.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableFiles.getSelectedRow();
                if (selectedRow != -1) {
                    String filePath = (String) tableModel.getValueAt(selectedRow, 2);
                    displaySelectedImage(new File(filePath));
                }
            }
        });

        JScrollPane scrollPaneTable = new JScrollPane(tableFiles);
        panelCenter.add(scrollPaneTable, BorderLayout.CENTER);

        add(panelCenter, BorderLayout.CENTER);
    }

    /**
     * Crée le panneau inférieur pour la prévisualisation de l'image.
     */
    private void createBottomPanel() {
        JPanel panelBottom = new JPanel(new BorderLayout());
        panelBottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblImagePreview = new JLabel("Aucune image sélectionnée", JLabel.CENTER);
        lblImagePreview.setVerticalAlignment(JLabel.CENTER);
        lblImagePreview.setHorizontalAlignment(JLabel.CENTER);
        lblImagePreview.setBorder(BorderFactory.createTitledBorder("Image sélectionnée"));

        panelBottom.add(lblImagePreview, BorderLayout.CENTER);

        add(panelBottom, BorderLayout.SOUTH);
    }

    /**
     * Affiche l'image sélectionnée.
     *
     * @param file Le fichier image sélectionné.
     */
    private void displaySelectedImage(File file) {
        try {
            if (file.exists() && (file.getName().toLowerCase().endsWith(".png") ||
                    file.getName().toLowerCase().endsWith(".jpeg") ||
                    file.getName().toLowerCase().endsWith(".jpg") ||
                    file.getName().toLowerCase().endsWith(".webp"))) {
                BufferedImage image = ImageIO.read(file);
                if (image != null) {
                    Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    lblImagePreview.setIcon(new ImageIcon(scaledImage));
                    lblImagePreview.setText("");
                } else {
                    lblImagePreview.setIcon(null);
                    lblImagePreview.setText("Format non pris en charge.");
                }
            } else {
                lblImagePreview.setIcon(null);
                lblImagePreview.setText("Fichier non pris en charge ou inexistant.");
            }
        } catch (IOException ex) {
            lblImagePreview.setIcon(null);
            lblImagePreview.setText("Erreur lors du chargement de l'image.");
            ex.printStackTrace();
        }
    }

    private void chargerRepertoire() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            manageGUI.loadDirectory(selectedDirectory);
        }
    }

    private void afficherStatsFichier() {
        int selectedRow = tableFiles.getSelectedRow();
        if (selectedRow != -1) {
            String filePath = (String) tableModel.getValueAt(selectedRow, 2);
            manageGUI.viewFileStatistics(new File(filePath));
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un fichier dans le tableau.", "Alerte", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void montrerMetadata() {
        int selectedRow = tableFiles.getSelectedRow();
        if (selectedRow != -1) {
            String filePath = (String) tableModel.getValueAt(selectedRow, 2);
            manageGUI.viewMetadata(new File(filePath));
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un fichier dans le tableau.", "Alerte", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onSnapshotSave() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sauvegarder Snapshot");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
				manageGUI.saveSnapshot(selectedFile.getAbsolutePath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    private void onSnapshotCompare() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Charger Snapshot pour Comparaison");

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
				manageGUI.compareSnapshot(selectedFile.getAbsolutePath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}

