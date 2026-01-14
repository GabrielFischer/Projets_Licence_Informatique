BEGIN;
DROP TABLE IF EXISTS utilisateur_tag CASCADE;
DROP TABLE IF EXISTS utilisateur_orientation CASCADE;
DROP TABLE IF EXISTS utilisateur_genre CASCADE;
DROP TABLE IF EXISTS profil CASCADE;
DROP TABLE IF EXISTS tag CASCADE;
DROP TABLE IF EXISTS orientation CASCADE;
DROP TABLE IF EXISTS genre CASCADE;
DROP TABLE IF EXISTS utilisateur CASCADE;
DROP TABLE IF EXISTS compatibilite_genre_orientation CASCADE;
DROP TABLE IF EXISTS dates CASCADE;
DROP TABLE IF EXISTS historique_activite CASCADE;
DROP TABLE IF EXISTS evenement CASCADE;
DROP TABLE IF EXISTS lieu CASCADE;
DROP TABLE IF EXISTS evenement_lieu CASCADE;
DROP TABLE IF EXISTS interaction_utilisateur CASCADE; 
DROP TABLE IF EXISTS evenement_tag;

--*********************** CREATION DES TABLES ****************************

CREATE TABLE utilisateur(
    user_id BIGSERIAL PRIMARY KEY, --on met BIGSERIAL au lieu de SERIAL pour avoir une marge au niveau de la valeur maximale de l'auto-incrément. user_id sera la clé primaire pour la relation utilisateur
    pseudo VARCHAR(50) UNIQUE NOT NULL, --NOT NULL pour que tous les utilisateurs aient un pseudo, et on met UNIQUE pour faire commes tous les réseaux, genre on empêche à 2 users d'avoir le même pseudo sinon ça devient chaud à gérer
    mdp_hash TEXT, --mot de passe pour chaque user, je en sais pas s'il faut que le mot de passe soit stocké sous forme illisible mais pour l'instant j'ai fait ça
    email VARCHAR(255) UNIQUE NOT NULL, --apparement la validation de si la chaine entrée correspond à une adresse mail ne se fait pas au niveau de la BDD mais au niveau de l'application
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL
);

CREATE TABLE profil(
    user_id BIGINT PRIMARY KEY REFERENCES utilisateur(user_id) ON DELETE CASCADE,
    bio TEXT NULL,
    date_naissance DATE NOT NULL,
    CONSTRAINT est_majeur CHECK (date_naissance <= CURRENT_DATE - INTERVAL '18 years'),
    pays VARCHAR(100) NOT NULL,
    ville VARCHAR(100) NOT NULL,
    taille NUMERIC(5, 2) NOT NULL CHECK (taille > 0), -- taille en cm
    poids NUMERIC(5, 2) NOT NULL CHECK (poids > 0),
    nbLike INT DEFAULT 0,
    nbNope INT DEFAULT 0
     /*On n'a pas besoin de ces compteurs car ils peuvent être calculés via interaction_utilisateur
     mais il faut les remettre pour une des requêtes avec la sous-requête dans le WHERE*/
);

-- SECTION GENRE & ORIENTATION (Le sujet demande à ce que ces attributs ne soient pas "binaires", donc on doit permettre tous types de genre et d'orientation)

CREATE TABLE genre(
    genre_id SERIAL PRIMARY KEY,
    nom_du_genre VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE utilisateur_genre(
    user_id BIGINT REFERENCES utilisateur(user_id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genre(genre_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, genre_id)
);

CREATE TABLE orientation(
    orientation_id SERIAL PRIMARY KEY,
    nom_orientation VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE utilisateur_orientation(
    user_id BIGINT REFERENCES utilisateur(user_id) ON DELETE CASCADE,
    orientation_id INTEGER REFERENCES orientation(orientation_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, orientation_id)
);

-- Section Hobbies, et donc TAGS
CREATE TABLE tag(
    tag_id SERIAL PRIMARY KEY,
    nom_tag VARCHAR(100) UNIQUE NOT NULL --on met des tags uniques vu qu'on veut pas plus d'une fois le même tag
);

CREATE TABLE utilisateur_tag(
    user_id BIGINT REFERENCES utilisateur(user_id) ON DELETE CASCADE,
    tag_id INTEGER REFERENCES tag(tag_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, tag_id)
);

CREATE TABLE compatibilite_genre_orientation (
    genre_id INTEGER NOT NULL,
    orientation_id INTEGER NOT NULL,
    target_genre_id INTEGER NOT NULL,
    PRIMARY KEY (genre_id, orientation_id, target_genre_id),
    FOREIGN KEY (genre_id) REFERENCES genre(genre_id),
    FOREIGN KEY (orientation_id) REFERENCES orientation(orientation_id),
    FOREIGN KEY (target_genre_id) REFERENCES genre(genre_id)
);


-- Tables liées aux événements
CREATE TABLE evenement(
    id SERIAL PRIMARY KEY,
    nom_evenement VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE evenement_tag (
    evenement_id INT NOT NULL REFERENCES evenement(id) ON DELETE CASCADE,
    tag_id INT NOT NULL REFERENCES tag(tag_id) ON DELETE CASCADE,
    PRIMARY KEY (evenement_id, tag_id)
);

CREATE TABLE lieu(
    id SERIAL PRIMARY KEY,
    nom_lieu VARCHAR(255) NOT NULL,
    adresse VARCHAR(255),
    ville VARCHAR(100) NOT NULL,
    capacite INT CHECK(capacite >0)
);

CREATE TABLE evenement_lieu(
    evenement_id INT NOT NULL REFERENCES evenement(id) ON DELETE CASCADE,
    lieu_id INT NOT NULL REFERENCES lieu(id) ON DELETE CASCADE,
    date_debut TIMESTAMP NOT NULL,
    date_fin TIMESTAMP NOT NULL,
    PRIMARY KEY (evenement_id, lieu_id, date_debut),
    CONSTRAINT fk_evenement_lieu_unique UNIQUE (lieu_id, date_debut, date_fin) -- cette contrainte empêche le lieu d'être réservé deux fois au même moment
);

CREATE TABLE historique_activite (
    historique_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES utilisateur(user_id) ON DELETE CASCADE,
    evenement_id INT NOT NULL REFERENCES evenement(id) ON DELETE CASCADE,
    date_participation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Quand l'utilisateur a participé
    UNIQUE (user_id, evenement_id, date_participation) -- Pour éviter les doublons exacts si un utilisateur participe plusieurs fois au même événement à la même heure exacte
);


CREATE TABLE interaction_utilisateur (
    user_id_source BIGINT NOT NULL REFERENCES utilisateur(user_id) ON DELETE CASCADE,
    user_id_cible BIGINT NOT NULL REFERENCES utilisateur(user_id) ON DELETE CASCADE,
    type_interaction VARCHAR(10) NOT NULL, -- 'like' ou 'nope'
    date_interaction TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Date et heure de l'interaction
    PRIMARY KEY (user_id_source, user_id_cible, type_interaction),
    CONSTRAINT chk_type_interaction CHECK (type_interaction IN ('like', 'nope')),
    CONSTRAINT chk_self_interaction CHECK (user_id_source <> user_id_cible) -- Un utilisateur ne peut pas interagir avec lui-même
);

CREATE TABLE dates (
    id SERIAL PRIMARY KEY,
    utilisateur_1_id BIGINT NOT NULL,
    utilisateur_2_id BIGINT NOT NULL,
    --idee_date_id INT NOT NULL,
    FOREIGN KEY (utilisateur_1_id) REFERENCES utilisateur(user_id),
    FOREIGN KEY (utilisateur_2_id) REFERENCES utilisateur(user_id)
    -- FOREIGN KEY (idee_date_id) REFERENCES idees_rencart(id)
);

COMMIT;