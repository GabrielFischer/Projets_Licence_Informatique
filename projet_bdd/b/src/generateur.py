from faker import Faker
import pandas as pd
import random
import hashlib

def defFaker(pays):
    correspondance = {
        "France": "fr_FR",
        "Allemagne": "de_DE",
        "Espagne": "es_ES",
        "Italie": "it_IT",
        "Royaume-Uni": "en_GB",
        "États-Unis": "en_US",
        "Canada": "fr_CA"
    }
    # Valeur par défaut : en_US si le pays n'est pas dans le dictionnaire
    return correspondance.get(pays, "fr_FR")

pays = ["France", "Allemagne", "Espagne", "Italie", "Royaume-Uni", "États-Unis", "Canada"]
poids_pays = [0.9, 0.02, 0.02, 0.02, 0.02, 0.01, 0.01]

orientation = ["Hétérosexuel.le","Homosexuel.le","Autre"]
poids_orientation = [0.7, 0.2, 0.1]

liste_hobbies = [
    "cinéma", "lecture", "sport", "voyage", "musique", "cuisine", "jeu de société", "théâtre",
    "jeux vidéo", "randonnée", "photographie", "dessin", "yoga", "danse", "animaux", "soirées"
]

villes_par_pays = {
    "France": ["Paris", "Marseille", "Lyon", "Toulouse", "Nice", "Nantes", "Strasbourg", "Montpellier", "Bordeaux", "Lille"],
    "Allemagne": ["Berlin", "Munich", "Hambourg", "Francfort", "Cologne", "Stuttgart", "Düsseldorf", "Dresde", "Hanovre", "Nuremberg"],
    "Espagne": ["Madrid", "Barcelone", "Valence", "Séville", "Saragosse", "Málaga", "Murcie", "Palma", "Bilbao", "Salamanque"],
    "Italie": ["Rome", "Milan", "Naples", "Turin", "Florence", "Venise", "Bologne", "Gênes", "Palerme", "Vérone"],
    "Royaume-Uni": ["Londres", "Manchester", "Birmingham", "Édimbourg", "Glasgow", "Liverpool", "Bristol", "Leeds", "Oxford", "Cambridge"],
    "États-Unis": ["New York", "Los Angeles", "Chicago", "Houston", "San Francisco", "Miami", "Las Vegas", "Boston", "Seattle", "Washington"],
    "Canada": ["Toronto", "Montréal", "Vancouver", "Calgary", "Ottawa", "Edmonton", "Québec", "Winnipeg", "Halifax", "Victoria"]
}


def generer_hobbies():
    nb_hobbies = random.randint(1, 3)
    hobbies = random.sample(liste_hobbies, nb_hobbies)
    while len(hobbies) < 3:
        hobbies.append("") #Pour compléter avec "" quand vide
    return hobbies

def generer_taille_poids(genre):
    if genre == "Homme":
        taille = round(random.uniform(165, 200), 1)
        poids = round(random.uniform(60, 110), 1)
    elif genre == "Femme":
        taille = round(random.uniform(150, 185), 1)
        poids = round(random.uniform(45, 90), 1)
    else:  # Non-binaire ou autre
        taille = round(random.uniform(150, 195), 1)
        poids = round(random.uniform(50, 100), 1)
    return taille, poids


def generate_user_profile(id):
    
    pays_choisi = random.choices(pays, weights=poids_pays, k=1)[0]
    
    orientation_choisie = random.choices(orientation, weights=poids_orientation, k=1)[0]
    
    fake=Faker(defFaker(pays_choisi))
    
    genre = random.choice(["Homme", "Femme"])
    if genre == "Femme":
        prenom = fake.first_name_female()
    else:
        prenom = fake.first_name_male()
        
    nonBinaireOrOther=random.randint(1,100)
    if nonBinaireOrOther<10 :
        if nonBinaireOrOther<3:
            genre="Autre"
        else:
            genre="Non-binaire"
    
    nom = fake.last_name()
    age = random.randint(18, 65)
    num1 = random.randint(1,10000)
    num2 = random.randint(1,10000)
    email = f"{prenom.lower()}.{nom.lower()}.{num1}.{num2}@{fake.free_email_domain()}"
    #email = fake.unique.email()
    #ville = fake.city() #Donne des villes qui n'existe pas... Voir si meilleure solution
    if pays_choisi in villes_par_pays:
        ville = random.choice(villes_par_pays[pays_choisi])
    else:
        print("Pays non reconnu.")
    #username=fake.unique.user_name()
    username=f"{prenom}{nom}{num1*num2}"
    password = fake.password(length=12, special_chars=True, digits=True, upper_case=True, lower_case=True)
    password_hashed = hashlib.sha256(password.encode('utf-8')).hexdigest()
    hobby1, hobby2, hobby3 = generer_hobbies()
    taille, poids = generer_taille_poids(genre)

    return {
        "id": id,
        "prenom": prenom,
        "nom": nom,
        "genre": genre,
        "orientation": orientation_choisie,
        "age": age,
        "email": email,
        "username":username,
        "password": password_hashed, #A voir
        "ville": ville,
        "pays": pays_choisi,
        "hobby1": hobby1,
        "hobby2": hobby2, #Facultatif donc
        "hobby3": hobby3, #Egalement
        "taille" : taille,
        "poids" : poids
        #Voir pour photos, description
        
    }

# Génération de 100 profils
profils = [generate_user_profile(i+1) for i in range(1000)]

# Export vers un fichier CSV
df = pd.DataFrame(profils)
df.to_csv("../CSV/profils_utilisateursEX.csv", index=False)
