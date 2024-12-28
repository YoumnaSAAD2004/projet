import os

# Répertoire contenant les fichiers Java
source_folder = "./src"  # <== Indiquez ici le chemin vers "src"

# Nom du fichier fusionné
output_file = "merged_code.java"

with open(output_file, "w") as outfile:
    for root, dirs, files in os.walk(source_folder):
        for file in files:
            if file.endswith(".java"):
                filepath = os.path.join(root, file)
                with open(filepath, "r") as infile:
                    # Ajoutez un commentaire pour indiquer le fichier source
                    outfile.write(f"\n// File: {filepath}\n")
                    outfile.write(infile.read())
                    outfile.write("\n")
print(f"All Java files have been merged into {output_file}")
