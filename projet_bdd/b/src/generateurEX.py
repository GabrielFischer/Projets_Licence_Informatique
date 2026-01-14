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
    return correspondance.get(pays, "fr_FR")

pays = ["France", "Allemagne", "Espagne", "Italie", "Royaume-Uni", "États-Unis", "Canada"]
poids_pays = [0.9, 0.02, 0.02, 0.02, 0.02, 0.01, 0.01]

orientation = ["Hétérosexuel.le","Homosexuel.le","Autre"]
poids_orientation = [0.7, 0.2, 0.1]

liste_hobbies = [
    "cinéma", "lecture", "sport", "voyage", "musique", "cuisine",
    "jeux vidéo", "randonnée", "photographie", "dessin", "yoga", "danse"
]

def generer_hobbies():
    nb_hobbies = random.randint(1, 3)
    hobbies = random.sample(liste_hobbies, nb_hobbies)
    while len(hobbies) < 3:
        hobbies.append("")
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

# Initialize a single Faker instance outside the function
# This Faker instance will manage unique values across all profiles
# We choose a default locale here as it's not specific to a single user anymore
global_fake_instance = Faker('fr_FR') # You can pick any default locale here

def generate_user_profile(id): # Removed 'fake' argument here, but will use global_fake_instance below
    
    pays_choisi = random.choices(pays, weights=poids_pays, k=1)[0]
    orientation_choisie = random.choices(orientation, weights=poids_orientation, k=1)[0]
    
    # We don't re-initialize Faker here. Instead, we use the global_fake_instance for unique values.
    # For locale-specific data (names, cities) that change based on pays_choisi, you'll need a separate Faker instance for that,
    # or ensure your global instance covers all necessary locales, or use defFaker to get country-specific data
    # (which might still lead to non-unique usernames/emails across locales if not managed carefully by Faker's unique provider).
    # For now, let's keep unique for username and email on the global instance, and use a separate Faker for locale-specific names/cities if needed.
    
    # To maintain locale-specific names/cities while ensuring unique username/email:
    # Option 1: Use the global unique faker for username/email, and a new locale-specific faker for names/cities.
    # This is the most robust approach for unique fields.
    locale_fake = Faker(defFaker(pays_choisi)) 

    genre = random.choice(["Homme", "Femme"])
    if genre == "Femme":
        prenom = locale_fake.first_name_female() # Use locale_fake for names
    else:
        prenom = locale_fake.first_name_male()   # Use locale_fake for names
        
    nonBinaireOrOther=random.randint(1,100)
    if nonBinaireOrOther<10 :
        if nonBinaireOrOther<3:
            genre="Autre"
        else:
            genre="Non-binaire"
    
    nom = locale_fake.last_name() # Use locale_fake for names
    age = random.randint(18, 65)
    email = global_fake_instance.unique.email() # <-- Use global_fake_instance for unique email
    ville = locale_fake.city() # Use locale_fake for cities
    username = global_fake_instance.unique.user_name() # <-- Use global_fake_instance for unique username
    password = global_fake_instance.password(length=12, special_chars=True, digits=True, upper_case=True, lower_case=True) # Could use global or locale
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
        "password": password_hashed,
        "ville": ville,
        "pays": pays_choisi,
        "hobby1": hobby1,
        "hobby2": hobby2,
        "hobby3": hobby3,
        "taille" : taille,
        "poids" : poids
    }

# Génération de 1000 profils
profils = [generate_user_profile(i+1) for i in range(1000)]

# Export vers un fichier CSV
df = pd.DataFrame(profils)
df.to_csv("CSV/profils_utilisateursEX.csv", index=False)