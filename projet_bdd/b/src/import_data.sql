-- Script d'importation des données depuis le fichier CSV

-- Début de la transaction pour s'assurer que l'import est atomique
BEGIN;
TRUNCATE TABLE interaction_utilisateur RESTART IDENTITY;
TRUNCATE TABLE utilisateur RESTART IDENTITY CASCADE;
TRUNCATE TABLE genre RESTART IDENTITY CASCADE;
TRUNCATE TABLE orientation RESTART IDENTITY CASCADE;
TRUNCATE TABLE tag RESTART IDENTITY CASCADE;
TRUNCATE TABLE evenement RESTART IDENTITY CASCADE;
TRUNCATE TABLE lieu RESTART IDENTITY CASCADE;
TRUNCATE TABLE dates RESTART IDENTITY;

DROP TABLE IF EXISTS temp_import_users;
DROP TABLE IF EXISTS temp_import_evenements;
--On met TEMPORARY pour s'assurer que cette table disapraisse une fois la session terminée.
-- Cette table est là pour transformer certaines données qui ne sont pas exactement correspondantes aux attributs de nos tables pincipales. Par exemple,
-- dans le fichier csv, on a age, mais dans nos tables principales on a date_naissance (on garde dat_naissance pour de potentielles requêtes sur les signes astrologiques etc.)
CREATE TEMPORARY TABLE temp_import_users (
    id INTEGER,
    prenom VARCHAR(255),
    nom VARCHAR(255),
    genre VARCHAR(255),
    orientation VARCHAR(255),
    age INTEGER,
    email VARCHAR(255),
    username VARCHAR(255),
    password TEXT, 
    ville VARCHAR(255),
    pays VARCHAR(255),
    hobby1 VARCHAR(255),
    hobby2 VARCHAR(255),
    hobby3 VARCHAR(255),
    taille NUMERIC(10,2),
    poids NUMERIC(10,2)
);

--On copie csv dans la table temporaire
\copy temp_import_users FROM 'CSV/profils_utilisateursEX.csv' CSV HEADER ENCODING 'UTF8';

CREATE TEMPORARY TABLE temp_import_evenements (
    id INTEGER,
    nom_evenement VARCHAR(255),
    tags VARCHAR(255)
);
\copy temp_import_evenements FROM 'CSV/evenements.csv' CSV HEADER ENCODING 'UTF8';

INSERT INTO evenement(id, nom_evenement)
SELECT id, nom_evenement
FROM temp_import_evenements
ON CONFLICT (id) DO NOTHING;
-- On utilise ON CONFLICT DO NOTHING pour ignorer les genres qui existeraient déjà (si jamais on lance plusieurs dois l'importation sur les mêmes données, jsp pk mais ça posait problème jusque là)
INSERT INTO genre (genre_id,nom_du_genre) VALUES
    (1,'Homme'),
    (2,'Femme'),
    (3,'Non-binaire'),
    (4,'Autre')
ON CONFLICT (genre_id) DO NOTHING;

INSERT INTO orientation (orientation_id,nom_orientation) VALUES
    (1,'Hétérosexuel.le'),
    (2,'Homosexuel.le'),
    (3,'Autre')
ON CONFLICT (orientation_id) DO NOTHING;


-- Insérer les tags avec IDs explicites
INSERT INTO tag (tag_id, nom_tag) VALUES
    (1, 'voyage'),
    (2, 'musique'),
    (3, 'jeu de société'),
    (4, 'soirées'),
    (5, 'randonnée'),
    (6, 'lecture'),
    (7, 'dessin'),
    (8, 'jeux vidéo'),
    (9, 'cinéma'),
    (10, 'yoga'),
    (11, 'sport'),
    (12, 'photographie'),
    (13, 'animaux'),
    (14, 'théâtre'),
    (15, 'cuisine'),
    (16, 'danse')
ON CONFLICT (nom_tag) DO NOTHING;


INSERT INTO utilisateur (pseudo, mdp_hash, email, nom, prenom)
SELECT username, password, email, nom, prenom
FROM temp_import_users;
-- Ici, on ne récupère pas directement l'user_id généré par BIGSERIAL.
-- On joindra plus tard sur un champ unique (comme l'email) pour récupérer l'user_id.



-- On joint la table temporaire avec la table 'utilisateur' via l'email pour récupérer l'user_id
-- On calcule la date_naissance et on cast les types pour taille et poids
INSERT INTO profil (user_id, bio, date_naissance, pays, ville, taille, poids)
SELECT
    u.user_id,
    NULL, -- bio est NULL car non présente dans le CSV
    (CURRENT_DATE - INTERVAL '1 year' * t.age)::DATE, -- Calcul approximatif de la date de naissance, à voir si on donne des dates différentes plutôt que la date d'aujourd'hui
    t.pays,
    t.ville,
    t.taille::NUMERIC(5,2), -- Casting vers le type de la table profil
    t.poids::NUMERIC(5,2)   -- Casting vers le type de la table profil
FROM temp_import_users t
JOIN utilisateur u ON t.email = u.email; -- Jointure pour lier le profil au bon utilisateur via l'email


-- On joint temp_import_users, utilisateur et genre
INSERT INTO utilisateur_genre (user_id, genre_id)
SELECT u.user_id, g.genre_id
FROM temp_import_users t
JOIN utilisateur u ON t.email = u.email
JOIN genre g ON t.genre = g.nom_du_genre
WHERE t.genre IS NOT NULL AND t.genre <> ''; -- Inclure seulement les lignes où un genre est renseigné


-- On joint temp_import_users, utilisateur et orientation
INSERT INTO utilisateur_orientation (user_id, orientation_id)
SELECT u.user_id, o.orientation_id
FROM temp_import_users t
JOIN utilisateur u ON t.email = u.email
JOIN orientation o ON t.orientation = o.nom_orientation
WHERE t.orientation IS NOT NULL AND t.orientation <> ''; -- Inclure seulement les lignes où une orientation est renseignée

--Insertion des compatibilités pré définies en ce qui concerne les genres et les orientations 
--Pour Homme hétéro cherche donc une femme
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (1, 1, 2) ON CONFLICT DO NOTHING;
-- Femme hétéro cherche Homme
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (2, 1, 1) ON CONFLICT DO NOTHING;
-- homme homo donc homme
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (1, 2, 1) ON CONFLICT DO NOTHING;
-- Femme homo donc femme
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (2, 2, 2) ON CONFLICT DO NOTHING;
-- Homme orientation autre 
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (1, 3, 1) ON CONFLICT DO NOTHING;
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (1, 3, 2) ON CONFLICT DO NOTHING;
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (1, 3, 3) ON CONFLICT DO NOTHING;
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (1, 3, 4) ON CONFLICT DO NOTHING;

--Femme orientation autre
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (2, 3, 1) ON CONFLICT DO NOTHING;
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (2, 3, 2) ON CONFLICT DO NOTHING;
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (2, 3, 3) ON CONFLICT DO NOTHING;
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (2, 3, 4) ON CONFLICT DO NOTHING;

--Non-bianire (orientation autre forcément parce que : Par quel genre est attiré une personne non-bianire homosexuelle ? Et alors, par quel genre est attirée une personne non-binaire hétérosexuelle ?)
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (3, 3, 1) ON CONFLICT DO NOTHING;
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (3, 3, 2) ON CONFLICT DO NOTHING;
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (3, 3, 3) ON CONFLICT DO NOTHING;
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (3, 3, 4) ON CONFLICT DO NOTHING; -- Assuming 4 for Autre
-- pour ceux qui possèdent le genre Autre, puisque nous ne savons pas précisément leur orientation ni leur genre, il est plus sécurisé de mettre toutes les combinaisons possibles
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (4, 3, 1) ON CONFLICT DO NOTHING;
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (4, 3, 2) ON CONFLICT DO NOTHING;
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (4, 3, 3) ON CONFLICT DO NOTHING;
INSERT INTO compatibilite_genre_orientation (genre_id, orientation_id, target_genre_id) VALUES (4, 3, 4) ON CONFLICT DO NOTHING; -- Assuming 4 for Autre


-- On doit faire une insertion pour chaque colonne de hobby (hobby1, hobby2, hobby3)
-- Utilise ON CONFLICT DO NOTHING pour gérer les cas où un utilisateur aurait le même hobby listé plusieurs fois dans le CSV
-- Insertion pour hobby1
INSERT INTO utilisateur_tag (user_id, tag_id)
SELECT u.user_id, tg.tag_id
FROM temp_import_users t
JOIN utilisateur u ON t.email = u.email
JOIN tag tg ON t.hobby1 = tg.nom_tag
WHERE t.hobby1 IS NOT NULL AND t.hobby1 <> ''
ON CONFLICT (user_id, tag_id) DO NOTHING;

-- Insertion pour hobby2
INSERT INTO utilisateur_tag (user_id, tag_id)
SELECT u.user_id, tg.tag_id
FROM temp_import_users t
JOIN utilisateur u ON t.email = u.email
JOIN tag tg ON t.hobby2 = tg.nom_tag
WHERE t.hobby2 IS NOT NULL AND t.hobby2 <> ''
ON CONFLICT (user_id, tag_id) DO NOTHING;

-- Insertion pour hobby3
INSERT INTO utilisateur_tag (user_id, tag_id)
SELECT u.user_id, tg.tag_id
FROM temp_import_users t
JOIN utilisateur u ON t.email = u.email
JOIN tag tg ON t.hobby3 = tg.nom_tag
WHERE t.hobby3 IS NOT NULL AND t.hobby3 <> ''
ON CONFLICT (user_id, tag_id) DO NOTHING;

INSERT INTO evenement_tag (evenement_id, tag_id)
SELECT
    e.id,
    t.tag_id
FROM
    temp_import_evenements et
JOIN
    evenement e ON et.id = e.id,
    unnest(string_to_array(et.tags, ',')) as tag_name
JOIN
    tag t ON tag_name = t.nom_tag;


-- Insertion dans les tables pour les évènements
INSERT INTO lieu (id, nom_lieu, adresse, capacite, ville) VALUES
(1, 'Salle Polyvalente Paris', '10 Rue de la Paix, 75001 Paris', 200, 'Paris'),
(2, 'Théâtre des Lumières Lyon', '25 Avenue Jean Jaurès, 69007 Lyon', 150, 'Lyon'),
(3, 'Espace Conférence Marseille', '5 Boulevard de la Corderie, 13007 Marseille', 100, 'Marseille'),
(4, 'Le Club Secret', '1 Rue des Mystères, 75010 Paris', 50, 'Paris'),
(5, 'Galerie d''Art Moderne', '12 Rue des Beaux-Arts, 69002 Lyon', 80, 'Lyon'),
(6, 'Centre Communautaire Ottawa', '150 Main St, Ottawa, ON K1S 5A1', 120, 'Ottawa'),
(7, 'Salle de Conférence Tech Houston', '450 Tech Blvd, Houston, TX 77002', 90, 'Houston'),
(8, 'Club de Musique Live Manchester', '78 Music Lane, Manchester M1 1AA', 300, 'Manchester'),
(9, 'Espace Créatif Nuremberg', '20 Kunstweg, 90403 Nürnberg', 70, 'Nuremberg'),
(10, 'Centre Culturel Phocéen', '30 Quai du Port, 13002 Marseille', 180, 'Marseille'),
(11, 'Studio Yoga Valance', '8 Rue du Zen, 26000 Valence', 40, 'Valence'),
(12, 'Bibliothèque Municipale Cologne', '5 Bücherei Str., 50667 Köln', 60, 'Cologne'),
(13, 'Salle de Bowling Palerme', '10 Via Palla, 90133 Palermo', 100, 'Palerme'),
(14, 'Parc Aventure Winnipeg', '200 Forest Rd, Winnipeg, MB R3C 0J8', 250, 'Winnipeg'),
(15, 'Atelier d''Art Strasbourg', '5 Rue des Pinceaux, 67000 Strasbourg', 30, 'Strasbourg')
ON CONFLICT (id) DO NOTHING;

INSERT INTO evenement_lieu (evenement_id, lieu_id, date_debut, date_fin) VALUES
(1, 1, '2025-06-01 19:00:00', '2025-06-01 22:00:00'), -- Concert à Paris
(2, 1, '2025-06-02 18:00:00', '2025-06-02 20:00:00'), -- Spectacle à Paris
(7, 2, '2025-06-05 20:00:00', '2025-06-05 22:30:00'), -- Théâtre à Lyon
(4, 3, '2025-06-10 09:00:00', '2025-06-10 17:00:00'), -- Conférence à Marseille
(5, 4, '2025-06-15 21:00:00', '2025-06-16 02:00:00'), -- Soirée à Paris
(8, 5, '2025-06-20 10:00:00', '2025-06-20 18:00:00'), -- Exposition à Lyon
(1, 1, '2025-06-03 19:00:00', '2025-06-03 22:00:00'), -- Autre concert à Paris (même lieu)
(7, 2, '2025-06-06 20:00:00', '2025-06-06 22:30:00'), -- Autre théâtre à Lyon (même lieu)
(1, 1, '2025-06-04 19:00:00', '2025-06-04 22:00:00'), -- Troisième concert à Paris (même lieu)
(21, 6, '2025-06-25 10:00:00', '2025-06-25 11:30:00'), -- Cours de Yoga à Ottawa
(22, 6, '2025-07-01 14:00:00', '2025-07-01 17:00:00'), -- Atelier Photo à Ottawa
(4, 7, '2025-07-05 09:00:00', '2025-07-05 16:00:00'), -- Conférence à Houston
(1, 8, '2025-07-10 20:00:00', '2025-07-10 23:00:00'), -- Concert à Manchester
(28, 8, '2025-07-12 19:00:00', '2025-07-12 23:00:00'), -- Soirée Jeux de Société à Manchester
(17, 9, '2025-07-15 14:00:00', '2025-07-15 17:00:00'), -- Atelier peinture à Nuremberg
(8, 9, '2025-07-18 10:00:00', '2025-07-18 17:00:00'), -- Exposition à Nuremberg
(19, 10, '2025-07-20 18:00:00', '2025-07-20 21:00:00'), -- Atelier cuisine à Marseille
(3, 10, '2025-07-25 16:00:00', '2025-07-25 23:00:00'), -- Festival à Marseille
(15, 11, '2025-06-28 17:00:00', '2025-06-28 19:00:00'), -- Cours de danse à Valence
(23, 11, '2025-07-03 19:00:00', '2025-07-03 21:00:00'), -- Dégustation de Vins à Valence
(12, 12, '2025-07-07 10:00:00', '2025-07-07 12:00:00'), -- Club de lecture à Cologne
(14, 13, '2025-07-11 19:00:00', '2025-07-11 21:00:00'), -- Bowling à Palerme
(20, 13, '2025-07-19 20:00:00', '2025-07-19 23:00:00'), -- Karaoké à Palerme
(16, 14, '2025-07-22 14:00:00', '2025-07-22 16:00:00'), -- Escape Game à Winnipeg
(29, 14, '2025-07-23 09:00:00', '2025-07-23 12:00:00'), -- Balade à Vélo à Winnipeg
(17, 15, '2025-07-26 15:00:00', '2025-07-26 18:00:00'), -- Atelier peinture à Strasbourg
(24, 1, '2025-07-08 18:00:00', '2025-07-08 21:00:00'), -- Cours de Cuisine Thématique à Paris (Salle Polyvalente)
(25, 2, '2025-07-14 14:00:00', '2025-07-14 17:00:00'), -- Tournoi d'Échecs à Lyon (Théâtre des Lumières)
(26, 3, '2025-07-28 10:00:00', '2025-07-28 11:00:00')  -- Session de Méditation à Marseille (Espace Conférence)
ON CONFLICT (evenement_id, lieu_id, date_debut) DO NOTHING;

INSERT INTO historique_activite (user_id, evenement_id, date_participation)
SELECT
    u.user_id,
    el.evenement_id,
    el.date_debut + (random() * INTERVAL '1 hour')
FROM
    utilisateur u
CROSS JOIN LATERAL (
    SELECT el_inner.evenement_id, el_inner.date_debut
    FROM evenement_lieu el_inner
    -- Ajouter une condition pour tenter de varier la sélection par utilisateur.
    -- Par exemple, une petite variation basée sur user_id et random()
    WHERE el_inner.evenement_id % (SELECT COUNT(*) FROM evenement_lieu) = (u.user_id % (SELECT COUNT(*) FROM evenement_lieu))
    ORDER BY random()
    LIMIT 10 -- Tenter de sélectionner plus d'événements pour que le `ON CONFLICT` ne bloque pas tout
) AS el
ON CONFLICT (user_id, evenement_id, date_participation) DO NOTHING;


--ID d'utilisateur récupérées grâce à 
INSERT INTO dates (utilisateur_1_id, utilisateur_2_id) VALUES
(1,40), --jeux vidéos en commun 
(5,328), -- animaux en commun
(55,288); --aucun


-- Étape 1 : Récupérer tous les utilisateurs avec leur genre et orientation principale
-- Nous partons du principe que l'utilisateur n'a qu'un seul genre et une seule orientation pour cette génération simple
-- Ici, on joint utilisateur, utilisateur_genre et utilisateur_orientation pour obtenir les IDs des genres et orientations.
CREATE TEMPORARY TABLE users_with_details AS
SELECT
    u.user_id,
    ug.genre_id,
    uo.orientation_id
FROM
    utilisateur u
JOIN
    utilisateur_genre ug ON u.user_id = ug.user_id
JOIN
    utilisateur_orientation uo ON u.user_id = uo.user_id;

-- On génère des interactions de type 'like' (5% seulement)
INSERT INTO interaction_utilisateur (user_id_source, user_id_cible, type_interaction, date_interaction)
SELECT DISTINCT
    u_source.user_id,
    u_cible.user_id,
    'like',
    (CURRENT_TIMESTAMP - (random() * (365 * 24 * 60 * 60)) * INTERVAL '1 second')
FROM
    users_with_details u_source
JOIN
    compatibilite_genre_orientation cgo
    ON u_source.genre_id = cgo.genre_id AND u_source.orientation_id = cgo.orientation_id
JOIN
    utilisateur_genre ug_cible ON cgo.target_genre_id = ug_cible.genre_id
JOIN
    users_with_details u_cible
    ON ug_cible.user_id = u_cible.user_id
    AND u_source.user_id <> u_cible.user_id
WHERE random() < 0.01  -- ⇐ 5 % de chance d'insérer
;

--Et des interactions de type 'nope' (5% seulement, et pas de doublons avec les 'like')
INSERT INTO interaction_utilisateur (user_id_source, user_id_cible, type_interaction, date_interaction)
SELECT DISTINCT
    u_source.user_id,
    u_cible.user_id,
    'nope',
    (CURRENT_TIMESTAMP - (random() * (365 * 24 * 60 * 60)) * INTERVAL '1 second')
FROM
    users_with_details u_source
JOIN
    compatibilite_genre_orientation cgo
    ON u_source.genre_id = cgo.genre_id AND u_source.orientation_id = cgo.orientation_id
JOIN
    utilisateur_genre ug_cible ON cgo.target_genre_id = ug_cible.genre_id
JOIN
    users_with_details u_cible
    ON ug_cible.user_id = u_cible.user_id
    AND u_source.user_id <> u_cible.user_id
WHERE NOT EXISTS ( -- On évite d'insérer des nopes si la paire source/cible a déjà un like
    SELECT 1
    FROM interaction_utilisateur
    WHERE user_id_source = u_source.user_id
      AND user_id_cible = u_cible.user_id
      AND type_interaction = 'like'
)
AND random() < 0.01  -- ⇐ 5 % de chance d'insérer
;

DROP TABLE users_with_details;

--Supprimer la table temporaire
DROP TABLE temp_import_users;
DROP TABLE temp_import_evenements;


UPDATE profil
SET nbLike = COALESCE(likes_count.nbLike, 0)
FROM (
    SELECT
        user_id_cible AS user_id,
        COUNT(*) AS nbLike
    FROM
        interaction_utilisateur
    WHERE
        type_interaction = 'like'
    GROUP BY
        user_id_cible
) AS likes_count
WHERE profil.user_id = likes_count.user_id;


UPDATE profil
SET nbNope = COALESCE(nopes_count.nbNope, 0)
FROM (
    SELECT
        user_id_cible AS user_id,
        COUNT(*) AS nbNope
    FROM
        interaction_utilisateur
    WHERE
        type_interaction = 'nope'
    GROUP BY
        user_id_cible
) AS nopes_count
WHERE profil.user_id = nopes_count.user_id;



-- Valider la transaction si tout s'est bien passé
COMMIT;