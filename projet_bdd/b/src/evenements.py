import random
from faker import Faker
import pandas as pd

fake = Faker("fr_FR")

types_evenement = ["Concert", "Spectacle", "Festival", "Conférence", "Soirée", "Projection",
              "Théâtre", "Exposition", "Séance cinéma", "Shooting photo", "Bar à chats", "Club de lecture"
              ,"Compétition de jeux-vidéos", "Bowling", "Cours de danse", "Escape Game",
              "Atelier peinture", "Stand-up", "Atelier cuisine", "Karaoké"]

def generer_evenement(id):
    return {
        "id": id,
        "evenement": types_evenement[id-1],
    }
    
evenements = [generer_evenement(i+1) for i in range(len(types_evenement))]
df = pd.DataFrame(evenements)
df.to_csv("evenements.csv", index=False)