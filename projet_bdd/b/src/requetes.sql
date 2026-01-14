BEGIN;
-- REQUÊTE QUI PORTE SUR AU MOINS 3 TABLES 
-- Soit un utilisateur dont on connait le pseudo, quels sont les autres utilisateurs qui habitent dans la même ville que 
-- cet utilisateur et qui ont les mêmes hobbies que lui ?
--On peut utiliser cette requête pour assez facilement faire des matchs entre des personnes qui cherchent des utilisateurs habitant la même ville
--s'intéressant aux mêmes loisirs mais également pour proposer des évènements à ces personnes puisqu'ils habitent dans la même ville
/*
SELECT
    userCible.pseudo,
    profilCible.date_naissance,
    profilCible.ville, --table résultat
    STRING_AGG(DISTINCT t.nom_tag, ', ') AS hobbiesCommuns
FROM 
    utilisateur userDeBase -- utilisateur dont on connait le pseudo
JOIN 
    profil profilDeBase ON userDeBase.user_id = profilDeBase.user_id -- on lie cet utilisateur à son profil car c'est la relation qui contient la ville
JOIN 
    utilisateur_tag utDeBase ON userDeBase.user_id = utDeBase.user_id -- on récupère les tags(hobbies) associés à l'utilisateur connu
JOIN 
    utilisateur_tag utCible ON utDeBase.tag_id = utCible.tag_id AND utDeBase.user_id <> utCible.user_id -- on cherche le même tag chez deux utilisateurs différents
JOIN 
    utilisateur userCible ON utCible.user_id = userCible.user_id -- association de l'user_id du tag commun trouvé avec l'utilisateur cible
JOIN 
    profil profilCible ON userCible.user_id = profilCible.user_id -- association de l'utilisateur trouvé à son profil
JOIN 
    tag t ON utCible.tag_id = t.tag_id -- association du tag trouvé chez l'utilisateur à son id dans la table tag
WHERE 
    userDeBase.pseudo = 'JosetteLegros43511781' AND profilCible.ville = profilDeBase.ville -- on cherche pour JosetteLegros43511781 et on vérifie si les 2 proviennet de la même ville
GROUP BY 
    userCible.pseudo, profilCible.date_naissance, profilCible.ville; -- on groupe pour un meilleur rendu
*/




-- REQUÊTE AVEC AUTO JOINTURE/JOINTURE REFLEXIVE (JOINTURE DE DEUX COPIES D'UNE MÊME TABLE)
--On pourra utiliser cette requête pour faire des évènements où des gens qui ont le même prénom peuvent se rencontrer
/*
SELECT DISTINCT 
    u1.pseudo AS pseudo1, 
    u1.prenom AS prenom_commun, 
    u2.pseudo AS pseudo2
FROM 
    utilisateur u1, utilisateur u2
WHERE 
    u1.user_id < u2.user_id AND u1.prenom = u2.prenom
ORDER BY 
    u1.prenom, u1.pseudo, u2.pseudo;
*/



/*REQUÊTE AVEc SOUS-REQUÊTE CORRELEE*/
/*Reqête qu donne les utilisateurs dont les combinaisons de genre et orientation ne sont pas disponibles sur l'application*/
/*
SELECT
    u.user_id,
    u.pseudo,
    g.nom_du_genre AS genre_utilisateur,
    o.nom_orientation AS orientation_utilisateur
FROM
    utilisateur u
JOIN
    utilisateur_genre ug ON u.user_id = ug.user_id
JOIN
    genre g ON ug.genre_id = g.genre_id
JOIN
    utilisateur_orientation uo ON u.user_id = uo.user_id
JOIN
    orientation o ON uo.orientation_id = o.orientation_id
WHERE
    NOT EXISTS (SELECT 1
                FROM
                    compatibilite_genre_orientation cgo
                WHERE
                    -- Corrélation : Utilise le genre de l'utilisateur principal (ug.genre_id)
                    cgo.genre_id = ug.genre_id
                    -- Corrélation : Utilise l'orientation de l'utilisateur principal (uo.orientation_id)
                    AND cgo.orientation_id = uo.orientation_id
               )
ORDER BY
    u.pseudo;
*/





/*REQUÊTE AVEC SOUS REQUÊTE DANS LE FROM*/
/*Cette requête permet aux développeurs de distinguer les hobbies qui ont assez d'utilisateurs dans une ville donnée pour organiser des évènements
autour de cet hobbie dans cette ville*/
/*
SELECT
    p.ville,
    t.nom_tag AS hobby_commun_dense,
    STRING_AGG(u.pseudo, ', ' ORDER BY u.pseudo) AS utilisateurs_concernes
FROM
    utilisateur u
JOIN
    profil p ON u.user_id = p.user_id
JOIN
    utilisateur_tag ut ON u.user_id = ut.user_id
JOIN
    tag t ON ut.tag_id = t.tag_id
JOIN (
    -- Début de la sous-requête dans le FROM (table dérivée)
    -- Cette sous-requête identifie les combinaisons (ville, hobby) qui ont au moins 5 utilisateurs.
    SELECT
        p_sub.ville,
        ut_sub.tag_id
    FROM
        profil p_sub
    JOIN
        utilisateur_tag ut_sub ON p_sub.user_id = ut_sub.user_id
    GROUP BY
        p_sub.ville, ut_sub.tag_id
    HAVING
        COUNT(p_sub.user_id) >= 20 -- Critère de densité : au moins 20 utilisateurs pour ce hobby dans cette ville afin de proposer l'évènement
) AS villes_hobbies_densite ON p.ville = villes_hobbies_densite.ville AND ut.tag_id = villes_hobbies_densite.tag_id
GROUP BY
    p.ville, t.nom_tag
ORDER BY
    p.ville, t.nom_tag;
*/




/*REQUÊTE AVEC SOUS REQUÊTE DANS LE WHERE*/
/*Les utilisateurs peuvent voir tous les profils dont le nombre de like est supérieur à la moyenne par pays,
et peuvent donc comparer la popularité des profils entre eux*/
/*
SELECT
    u.pseudo,
    p.pays,
    p.ville,
    p.nbLike
FROM
    utilisateur u
JOIN
    profil p ON u.user_id = p.user_id
WHERE
    -- On calcule la moyenne des likes pour le pays de l'utilisateur principal (p.pays)
    -- et on va filtrer les utilisateurs dont le nbLike est supérieur à cette moyenne.
    p.nbLike > (SELECT AVG(p_sub.nbLike)
                FROM
                    profil p_sub
                WHERE
                    p_sub.pays = p.pays -- Corrélation : la sous-requête dépend du pays de la ligne courante de la requête externe
               )
ORDER BY
    p.pays, p.nbLike DESC;
*/







/*Requête qui permet de voir les utilisateurs qui sont plus jeunes que la moyenne d'âge des utilisateurs de leurs pays*/
/*
SELECT
    u.pseudo,
    p.pays,
    p.ville,
    EXTRACT(YEAR FROM AGE(CURRENT_DATE, p.date_naissance)) AS age_utilisateur
FROM
    utilisateur u
JOIN
    profil p ON u.user_id = p.user_id
WHERE
    -- L'âge de l'utilisateur courant doit être inférieur à la moyenne des âges dans son pays
    EXTRACT(YEAR FROM AGE(CURRENT_DATE, p.date_naissance)) < (
        -- Sous-requête pour calculer la moyenne des âges des utilisateurs du même pays
        SELECT AVG(EXTRACT(YEAR FROM AGE(CURRENT_DATE, p_sub.date_naissance)))
        FROM profil p_sub
        WHERE p_sub.pays = p.pays -- Corrélation par pays
    )
ORDER BY
    p.pays, age_utilisateur ASC;
*/



/*REQUÊTE AVEC DEUX AGREGATS NECESSITANT GROUP BY ET HAVING*/
/*Cette requête permet aux utilisateurs de voir quelles sont les potentielles communautés qui pratiquent un hobbie dans une ville donnée,
dont les membres ont entre 18 et 28 ans, et dont le nombre de like moyen est supérieur à 2*/
/*
SELECT
    p.ville,
    t.nom_tag AS hobby_de_la_communaute,
    COUNT(p.user_id) AS nombre_de_membres,
    ROUND(AVG(p.nbLike), 2) AS moyenne_de_likes_des_membres
FROM
    profil p
JOIN
    utilisateur_tag ut ON p.user_id = ut.user_id
JOIN
    tag t ON ut.tag_id = t.tag_id
WHERE
    AGE(CURRENT_DATE, p.date_naissance) >= INTERVAL '18 years' AND
    AGE(CURRENT_DATE, p.date_naissance) < INTERVAL '28 years'
GROUP BY
    p.ville,
    t.nom_tag
HAVING
    -- Condition 1 sur le premier agrégat : Au moins 2 membres dans la communauté
    COUNT(p.user_id) >= 2
    AND
    -- Condition 2 sur le second agrégat : La moyenne des likes reçus est supérieure à 2
    AVG(p.nbLike) > 2
ORDER BY
    p.ville,
    nombre_de_membres DESC,
    moyenne_de_likes_des_membres DESC;
*/



/*REQUÊTE IMPLIQUANT LE CALCUL DE DEUX AGREGATS*/
/*L'utilisateur peut savoir grâce à cette requête quelle est la moyenne du nb de likes maximum par ville pour chaque pays*/
/*
SELECT
    pays,
    ROUND(AVG(max_likes_par_ville), 2) AS moyenne_des_likes_max_par_ville_dans_ce_pays
FROM (
    -- Première agrégation : Trouver le maximum de likes (nbLike) pour chaque ville
    SELECT
        p.pays,
        p.ville,
        MAX(p.nbLike) AS max_likes_par_ville -- Calcul du score de likes le plus élevé par ville
    FROM
        profil p
    GROUP BY
        p.pays, p.ville -- Regroupe par pays et par ville pour trouver le MAX(nbLike) dans chaque ville
) AS likes_max_par_ville -- C'est la "table" des maximums de likes par ville
GROUP BY
    pays -- Deuxième agrégation : Faire la moyenne de ces maximums, regroupé par pays
ORDER BY
    pays;
*/



/*Moyenne du nombre de likes maximum par ville pour chaque pays*/
/*
SELECT
    max_likes_par_ville.pays,
    ROUND(AVG(max_likes_par_ville.max_likes_in_city), 2) AS moyenne_du_max_likes_par_ville_par_pays
FROM (
    -- Première agrégation : Trouver le nombre maximum de 'likes' pour un utilisateur dans chaque ville
    SELECT
        p_inner.pays,
        p_inner.ville,
        MAX(
            -- Sous-requête corrélée pour compter les likes d'un utilisateur spécifique
            (SELECT COUNT(*) FROM interaction_utilisateur WHERE user_id_cible = u_inner.user_id AND type_interaction = 'like')
        ) AS max_likes_in_city
    FROM
        utilisateur u_inner
    JOIN
        profil p_inner ON u_inner.user_id = p_inner.user_id
    GROUP BY
        p_inner.pays, p_inner.ville
) AS max_likes_par_ville
GROUP BY
    max_likes_par_ville.pays
ORDER BY
    max_likes_par_ville.pays;
*/



/*REQUÊTE QUI INCLUT UNE JOINTURE EXTERNE*/
/*Requête qui permet de savoir le nombre de personnes pratiquant chacun des hobbies présents sur l'application*/
/*
SELECT
    t.nom_tag AS hobby,
    COUNT(ut.user_id) AS nombre_utilisateurs_qui_le_pratiquent
FROM
    tag t
LEFT JOIN
    utilisateur_tag ut ON t.tag_id = ut.tag_id
GROUP BY
    t.nom_tag
ORDER BY
    nombre_utilisateurs_qui_le_pratiquent DESC, hobby;
*/




/*2 REQUÊTES EQUIVALENTES EXPRIMANT UNE CONDITION DE TOTALITE*/
/*On a choisi de faire des requêtes qui donnent les utilisateurs susceptibles d'être intérressés par des évènements de voyage
grâce aux hobbies*/
/*VERSION AVEC SOUS REQUÊTE CORRELEE*/
/*
SELECT
    u.pseudo,
    u.email --On prend le mail parce qu'on part du principe que l'app envoie des mails aux utilisateurs concernés pour les prévenir d'un event
FROM
    utilisateur u
WHERE
    NOT EXISTS (
        -- Cette sous-requête cherche un hobby de voyage ou de randonnée que l'utilisateur actuel (u) ne pratique pas
        SELECT 1
        FROM
            tag AS travel_hobbies
        WHERE
            travel_hobbies.nom_tag IN ('voyage', 'randonnée')
            AND NOT EXISTS (
                --sous-sous-requête qui vérifie si l'utilisateur (u) a le hobby (travel_hobbies)
                SELECT 1
                FROM
                    utilisateur_tag ut
                WHERE
                    ut.user_id = u.user_id -- Corrélation avec l'utilisateur principal
                    AND ut.tag_id = travel_hobbies.tag_id -- Corrélation avec le hobby de référence
            )
    );
*/
/*VERSION AVEC AGGREGATION*/
/*
SELECT
    u.pseudo,
    u.email
FROM
    utilisateur u
JOIN
    utilisateur_tag ut ON u.user_id = ut.user_id
JOIN
    tag t ON ut.tag_id = t.tag_id
WHERE
    t.nom_tag IN ('voyage', 'randonnée')
GROUP BY
    u.user_id, u.pseudo, u.email -- Regroupe par utilisateur
HAVING
    COUNT(DISTINCT t.nom_tag) = (
        -- Cette sous-requête calcule la taille de notre ensemble de référence (nombre total de hobbies de voyage)
        SELECT COUNT(DISTINCT nom_tag)
        FROM tag
        WHERE nom_tag IN ('voyage', 'randonnée')
    );
*/



/*2 REQUÊTES QUI RETOURNENT LE MÊME RESULTAT SANS NULL, MAIS DIFFERENTS AVEC NULL */
/*Les requêtes peuvent servir à identifier les profils parmi ceux qui ne pratiquent pas de sport,
ou ceux qui ont moins de 50 likes, ou encore ceux qui n'ont pas de bio pour leur proposer des activités sportives*/

/*
--On doit d'abord mettre à jour la colonne bio qui était NULL jusqu'à présent
UPDATE profil
SET bio = CASE
    WHEN RANDOM() < 0.2 THEN 'Passioné de voyages et de découvertes'
    WHEN RANDOM() < 0.4 THEN 'Aime la lecture et les sports en plein air'
    WHEN RANDOM() < 0.6 THEN 'Fan de cuisine et de musique'
    ELSE NULL
END
WHERE BIO IS NULL; --en réalité, puisqu'aucune bio n'est remplie à ce moment là, cette clause ne sert pas à l'initialisation, mais seulement après.
*/

/*Version qui utilise NOT LIKE (renvoie NULL si la colonne comparée est NULL*/
/*
SELECT
    u.pseudo,
    u.email,
    p.bio
FROM
    utilisateur u
JOIN
    profil p ON u.user_id = p.user_id
WHERE
    p.bio NOT LIKE '%sport%'; -- Si p.bio est NULL, cette comparaison est NULL (inconnu)
*/

/*Version qui inclut explicitement les bio NULL, ainsi
cette version inclut les utilisateurs qui n'ont pas de bio, contrairement à la première requête*/
/*
SELECT
    u.pseudo,
    u.email,
    p.bio
FROM
    utilisateur u
JOIN
    profil p ON u.user_id = p.user_id
WHERE
    p.bio IS NULL -- Inclut explicitement les profils sans bio
    OR p.bio NOT LIKE '%sport%'; -- Inclut les profils avec une bio qui ne contient pas 'sport'
*/


/*Version correcte de la requête 1*/
/*
SELECT
    u.pseudo,
    u.email,
    p.bio
FROM
    utilisateur u
JOIN
    profil p ON u.user_id = p.user_id
WHERE
    (p.bio NOT LIKE '%sport%' AND p.bio IS NOT NULL) -- La bio ne contient pas 'sport' ET n'est pas NULL
    OR p.bio IS NULL; -- OU la bio est NULL (ce qui inclut les cas précédemment manqués)
*/



/*REQUÊTE RECURSIVE*/
/*Cette requête permet à un utilisateur de trouver la prochaine date à laquelle la Salle polyvalente de Paris est disponible
pour un évènement de 3h*/
/*
WITH RECURSIVE generated_slots AS (
    -- Membre d'ancrage: Commence à générer des créneaux à partir de la date et heure actuelles.
    -- Nous assumons une durée d'événement de 3 heures pour cette "Soirée".
    SELECT
        CURRENT_TIMESTAMP AS slot_start,
        (CURRENT_TIMESTAMP + INTERVAL '3 hours') AS slot_end,
        1 AS iteration_num -- Compteur pour limiter la récursion
    UNION ALL
    -- Membre récursif: Génère le créneau suivant en ajoutant la durée de l'événement au créneau précédent.
    SELECT
        (gs.slot_start + INTERVAL '3 hours'), -- Le nouveau créneau commence juste après la fin du précédent
        (gs.slot_end + INTERVAL '3 hours'),
        gs.iteration_num + 1
    FROM
        generated_slots gs
    WHERE
        gs.iteration_num < 100 -- Limite la récursion pour éviter une boucle infinie (ex: 100 créneaux)
        AND (gs.slot_start < (CURRENT_TIMESTAMP + INTERVAL '3 months')) -- Limite la recherche à 3 mois dans le futur pour la performance
),
-- Récupère tous les créneaux déjà réservés pour le lieu spécifique
booked_slots AS (
    SELECT
        date_debut,
        date_fin
    FROM
        evenement_lieu
    WHERE
        lieu_id = 1 -- ID de la Salle Polyvalente Paris
)
-- Sélectionne le premier créneau généré qui ne chevauche aucun créneau réservé
SELECT
    'Salle Polyvalente Paris' AS nom_du_lieu,
    gs.slot_start AS prochaine_date_disponible,
    gs.slot_end AS fin_du_creneau_disponible
FROM
    generated_slots gs
WHERE
    -- Vérifie qu'il n'y a AUCUN chevauchement avec un créneau déjà réservé
    NOT EXISTS (
        SELECT 1
        FROM booked_slots bs
        WHERE
            (gs.slot_start, gs.slot_end) OVERLAPS (bs.date_debut, bs.date_fin)
    )
ORDER BY
    gs.slot_start-- S'assure de prendre le premier créneau disponible
LIMIT 1; -- Ne renvoie que le tout premier créneau trouvé
*/



/*REQUÊTE AVEC FENETRAGE*/
/*Requête qui permet de savoir le top 10 des personnes les plus nopées par mois*/
/*
WITH MonthlyNopeCounts AS (
    SELECT
        EXTRACT(YEAR FROM date_interaction) AS year,
        EXTRACT(MONTH FROM date_interaction) AS month,
        user_id_cible,
        COUNT(*) AS nope_count
    FROM
        interaction_utilisateur
    WHERE
        type_interaction = 'nope'
    GROUP BY
        1, 2, user_id_cible
),
RankedNopes AS (
    SELECT
        year,
        month,
        user_id_cible,
        nope_count,
        -- On utilise RANK() pour classer les utilisateurs par nombre de nope dans chaque mois
        RANK() OVER (PARTITION BY year, month ORDER BY nope_count DESC) as rnk
    FROM
        MonthlyNopeCounts
),
Top10NopedUsers AS (
    SELECT
        year,
        month,
        user_id_cible,
        nope_count
    FROM
        RankedNopes
    WHERE
        rnk <= 10 -- on sélectionne uniquement le top 10 des utilisateurs les plus nopés pour chaque mois
)
SELECT
    t10.year,
    t10.month,
    u.pseudo,
    t10.nope_count,
    -- stringagg concatène tous les tags associés à l'utilisateur
    STRING_AGG(tg.nom_tag, ', ') AS associated_tags
FROM
    Top10NopedUsers t10
JOIN
    utilisateur u ON t10.user_id_cible = u.user_id
LEFT JOIN -- LEFT JOIN car un utilisateur peut ne pas avoir de tags
    utilisateur_tag ut ON u.user_id = ut.user_id
LEFT JOIN
    tag tg ON ut.tag_id = tg.tag_id
GROUP BY
    t10.year, t10.month, u.pseudo, t10.nope_count
ORDER BY
    t10.year, t10.month, t10.nope_count DESC;
*/




/*Requête qui permet à un utilisateur de savoir si il y a des évènements dans sa ville dans les 30 prochains jours*/
/*
SELECT DISTINCT
    e.nom_evenement,
    l.nom_lieu,
    l.adresse,
    el.date_debut,
    el.date_fin
FROM
    evenement e
JOIN
    evenement_lieu el ON e.id = el.evenement_id
JOIN
    lieu l ON el.lieu_id = l.id
JOIN
    utilisateur u ON u.user_id = 28 -- On met 28 pour la démo
JOIN
    profil p ON u.user_id = p.user_id
LEFT JOIN
    historique_activite ha ON u.user_id = ha.user_id AND e.id = ha.evenement_id
WHERE
    l.ville = p.ville -- Événements dans la même ville que l'utilisateur
    AND el.date_debut BETWEEN CURRENT_TIMESTAMP AND CURRENT_TIMESTAMP + INTERVAL '30 days' -- Dans les 30 prochains jours
    AND ha.historique_id IS NULL -- L'utilisateur n'a pas encore participé à cet événement
ORDER BY
    el.date_debut;
*/



/*Cette requête permet à un utilisateur de connaître les évènements susceptibles de lui plaire proches de chez lui (même ville)
mais elle peut aussi servir à l'application afin qu'elle propose aux utilisateurs qui possèdent les mêmes tags que les évènements proches
de chez eux de participer à ces dits-évènements*/
/*
SELECT
    e.nom_evenement,
    l.nom_lieu,
    l.adresse,
    l.ville,
    el.date_debut,
    el.date_fin,
    COUNT(et.tag_id) AS nombre_tags_communs -- Compte le nombre de tags en commun
FROM
    evenement e
JOIN
    evenement_lieu el ON e.id = el.evenement_id
JOIN
    lieu l ON el.lieu_id = l.id
JOIN
    evenement_tag et ON e.id = et.evenement_id
JOIN
    utilisateur_tag ut ON et.tag_id = ut.tag_id
JOIN
    profil p ON ut.user_id = p.user_id
WHERE
    ut.user_id = 28 -- On met 28 pour la démo (intéressé par les soirées)
    AND l.ville = p.ville -- Événements dans la même ville que l'utilisateur
    AND el.date_debut > CURRENT_TIMESTAMP -- Événements à venir
GROUP BY
    e.id, e.nom_evenement, l.nom_lieu, l.adresse, l.ville, el.date_debut, el.date_fin
ORDER BY
    nombre_tags_communs DESC, -- Les événements avec le plus de tags en commun d'abord
    el.date_debut ASC; 
*/


/*Requête qui permet à un utilisateur de voir le nombre de tags en communs avec les utilisateurs qu'il a liké et qui 
l'ont liké en retour*/
/*
SELECT
    u2.user_id,
    u2.pseudo,
    p2.ville,
    COUNT(DISTINCT ut2.tag_id) AS nb_tags_communs
FROM
    utilisateur u1 -- L'utilisateur qui lance la requête
JOIN
    interaction_utilisateur i12 ON u1.user_id = i12.user_id_source
JOIN
    utilisateur u2 ON i12.user_id_cible = u2.user_id
JOIN
    interaction_utilisateur i21 ON u2.user_id = i21.user_id_source AND u1.user_id = i21.user_id_cible
JOIN
    profil p2 ON u2.user_id = p2.user_id
LEFT JOIN
    utilisateur_tag ut1 ON u1.user_id = ut1.user_id
LEFT JOIN
    utilisateur_tag ut2 ON u2.user_id = ut2.user_id AND ut1.tag_id = ut2.tag_id
WHERE
    u1.user_id = 17 -- 17 pour la démo (je n'ai pas trouvé avec au moins 1 tag en commun (1% de chance de like..))
    AND i12.type_interaction = 'like'
    AND i21.type_interaction = 'like'
GROUP BY
    u2.user_id, u2.pseudo, p2.ville
ORDER BY
    nb_tags_communs DESC,
    u2.pseudo ASC;
*/



/*Requête qui donne les 10 utilisateurs les plus actifs au niveau des évènements passés et futurs*/
/*
SELECT
    u.user_id,
    u.pseudo,
    p.ville,
    p.pays,
    COUNT(DISTINCT ha.evenement_id) AS nombre_evenements_participes,
    COUNT(DISTINCT et_future.evenement_id) AS nombre_evenements_futurs_pertinents,
    ARRAY_AGG(DISTINCT t.nom_tag) AS tags_interets
FROM
    utilisateur u
JOIN
    profil p ON u.user_id = p.user_id
LEFT JOIN
    historique_activite ha ON u.user_id = ha.user_id
LEFT JOIN
    utilisateur_tag ut ON u.user_id = ut.user_id
LEFT JOIN
    tag t ON ut.tag_id = t.tag_id
LEFT JOIN
    evenement_tag et_future ON ut.tag_id = et_future.tag_id
LEFT JOIN
    evenement_lieu el_future ON et_future.evenement_id = el_future.evenement_id
LEFT JOIN
    lieu l_future ON el_future.lieu_id = l_future.id
WHERE
    (el_future.date_debut IS NULL OR (el_future.date_debut > CURRENT_TIMESTAMP AND l_future.ville = p.ville))
    AND NOT EXISTS ( -- Exclure les événements futurs auxquels l'utilisateur a déjà participé
        SELECT 1
        FROM historique_activite ha_check
        WHERE ha_check.user_id = u.user_id
        AND ha_check.evenement_id = et_future.evenement_id
    )
GROUP BY
    u.user_id, u.pseudo, p.ville, p.pays
ORDER BY
    nombre_evenements_participes DESC, nombre_evenements_futurs_pertinents DESC
LIMIT 10; -- On en veut que 10
*/



/*Requête qui permet d'obtenir les tags populaires dans une ville qui n'a pas encore d'évènement prévu dans cette ville */
/*
WITH TagsPopulairesParVille AS (
    SELECT
        p.ville,
        t.tag_id,
        t.nom_tag,
        COUNT(ut.user_id) AS nombre_utilisateurs
    FROM
        utilisateur_tag ut
    JOIN
        tag t ON ut.tag_id = t.tag_id
    JOIN
        profil p ON ut.user_id = p.user_id
    GROUP BY
        p.ville, t.tag_id, t.nom_tag
    HAVING
        COUNT(ut.user_id) > 10
),
EvenementsFutursParTagVille AS (
    SELECT DISTINCT
        l.ville,
        et.tag_id
    FROM
        evenement_tag et
    JOIN
        evenement_lieu el ON et.evenement_id = el.evenement_id
    JOIN
        lieu l ON el.lieu_id = l.id
    WHERE
        el.date_debut > CURRENT_TIMESTAMP
)
SELECT
    tpv.ville AS ville_utilisateur,
    tpv.nom_tag AS interet_populaire_non_couvert
FROM
    TagsPopulairesParVille tpv
LEFT JOIN
    EvenementsFutursParTagVille efptv
    ON tpv.ville = efptv.ville AND tpv.tag_id = efptv.tag_id
WHERE
    efptv.tag_id IS NULL -- Signifie qu'il n'y a pas d'événement futur pour ce tag dans cette ville
ORDER BY
    tpv.nombre_utilisateurs DESC;
*/




/*Requête qui permet de voir quels utilisateurs ont nopé un autre utilisateur, mais ont quand même participé aux mêmes
évènements que les utilisateurs qu'ils ont nopé*/
/*
SELECT DISTINCT
    u_nopeur.user_id AS user_id_nopeur,
    u_nopeur.pseudo AS pseudo_nopeur,
    u_nope.user_id AS user_id_nope,
    u_nope.pseudo AS pseudo_nope,
    e.nom_evenement,
    el.date_debut AS date_evenement
FROM
    interaction_utilisateur iu
JOIN
    utilisateur u_nopeur ON iu.user_id_source = u_nopeur.user_id
JOIN
    utilisateur u_nope ON iu.user_id_cible = u_nope.user_id
JOIN
    historique_activite ha_nopeur ON u_nopeur.user_id = ha_nopeur.user_id
JOIN
    historique_activite ha_nope ON u_nope.user_id = ha_nope.user_id AND ha_nopeur.evenement_id = ha_nope.evenement_id
JOIN
    evenement e ON ha_nopeur.evenement_id = e.id
JOIN
    evenement_lieu el ON e.id = el.evenement_id AND ha_nopeur.evenement_id = el.evenement_id
WHERE
    iu.type_interaction = 'nope'
    AND ha_nopeur.date_participation IS NOT NULL -- S'assurer qu'il y a eu participation
    AND ha_nope.date_participation IS NOT NULL
ORDER BY
    u_nopeur.pseudo, u_nope.pseudo, e.nom_evenement;
*/




/*Requête qui donne les 10 tags qui sont les plus utilisés parmi les utilisateurs ayant participé à des évènements*/
/*
SELECT
    t.nom_tag,
    COUNT(DISTINCT ut.user_id) AS nombre_utilisateurs_actifs_avec_ce_tag,
    COUNT(DISTINCT ha.evenement_id) AS nombre_evenements_lies_a_ce_tag
FROM
    tag t
JOIN
    utilisateur_tag ut ON t.tag_id = ut.tag_id
JOIN
    historique_activite ha ON ut.user_id = ha.user_id
WHERE
    ut.user_id IN (
        SELECT user_id
        FROM historique_activite
        GROUP BY user_id
        HAVING COUNT(DISTINCT evenement_id) >= 1 -- On définit un utilisateur "actif" comme ayant participé à au moins 1 événement
    )
GROUP BY
    t.nom_tag
ORDER BY
    nombre_utilisateurs_actifs_avec_ce_tag DESC, nombre_evenements_lies_a_ce_tag DESC
LIMIT 10;
*/





/*Requête qui permet de savoir qui sont les 10 utilisateurs qui ont le plus de like de la part d'utilisateurs provenant de la même ville*/
/*
SELECT
    u.user_id,
    u.pseudo,
    p.ville,
    COUNT(iu.user_id_source) AS nombre_de_likes_locaux
FROM
    utilisateur u
JOIN
    profil p ON u.user_id = p.user_id
JOIN
    interaction_utilisateur iu ON u.user_id = iu.user_id_cible
JOIN
    profil p_source ON iu.user_id_source = p_source.user_id
WHERE
    iu.type_interaction = 'like'
    AND p.ville = p_source.ville -- Le like doit venir d'un utilisateur de la même ville
    AND p.ville = 'Bordeaux' -- On met Bordeaux pour la démo, mais on peut mettre les autres villes aussi
GROUP BY
    u.user_id, u.pseudo, p.ville
ORDER BY
    nombre_de_likes_locaux DESC
LIMIT 10;
*/



/*
SELECT
    u_reco.user_id,
    u_reco.pseudo,
    u_reco.nom,
    u_reco.prenom,
    p_reco.ville AS ville_reco,
    p_reco.pays AS pays_reco,
    EXTRACT(YEAR FROM AGE(CURRENT_DATE, p_reco.date_naissance)) AS age_reco,
    g_reco.nom_du_genre AS genre_reco,
    o_reco.nom_orientation AS orientation_reco,
    -- Calcul de l'indice de recommandation
    (
        -- 1. Compatibilité Genre/Orientation (critère fondamental, doit être un match)
        CASE WHEN cgo_cible_vers_reco.genre_id IS NOT NULL THEN 100 ELSE 0 END -- Cible attirée par recommandé
        -- 2. Même pays
        + CASE WHEN p_cible.pays = p_reco.pays THEN 20 ELSE 0 END
        -- 3. Même ville
        + CASE WHEN p_cible.ville = p_reco.ville THEN 50 ELSE 0 END
        -- 4. Points pour les tags en commun (somme des points par tag)
        + COALESCE(SUM(CASE WHEN ut_cible.tag_id = ut_reco.tag_id THEN 15 ELSE 0 END), 0)
        -- 5. Points pour la tranche d'âge
        + CASE
            WHEN ABS(EXTRACT(YEAR FROM AGE(CURRENT_DATE, p_cible.date_naissance)) - EXTRACT(YEAR FROM AGE(CURRENT_DATE, p_reco.date_naissance))) <= 5 THEN 30
            WHEN ABS(EXTRACT(YEAR FROM AGE(CURRENT_DATE, p_cible.date_naissance)) - EXTRACT(YEAR FROM AGE(CURRENT_DATE, p_reco.date_naissance))) <= 10 THEN 15
            ELSE 0
          END
        -- 6. Points pour les tags d'événements à venir communs (non encore participé par l'utilisateur cible)
        + COALESCE(SUM(CASE
                            WHEN e_t.id IS NOT NULL
                            AND ha_cible.historique_id IS NULL -- Utilisateur cible n'a pas encore participé
                            AND el.date_debut > CURRENT_TIMESTAMP -- Événement à venir
                            AND l.ville = p_cible.ville -- Événement dans la même ville
                            THEN 25 ELSE 0 END), 0)
    ) AS score_compatibilite,
    COUNT(DISTINCT ut_cible.tag_id) FILTER (WHERE ut_cible.tag_id = ut_reco.tag_id) AS nb_tags_communs,
    ARRAY_AGG(DISTINCT t_commun.nom_tag) FILTER (WHERE ut_cible.tag_id = ut_reco.tag_id) AS tags_communs,
    ARRAY_AGG(DISTINCT e_t.nom_evenement) FILTER (WHERE e_t.id IS NOT NULL AND ha_cible.historique_id IS NULL AND el.date_debut > CURRENT_TIMESTAMP AND l.ville = p_cible.ville) AS evenements_suggeres_par_tags
FROM
    utilisateur u_cible -- L'utilisateur pour qui nous cherchons des recommandations
JOIN
    profil p_cible ON u_cible.user_id = p_cible.user_id
JOIN
    utilisateur_genre ug_cible ON u_cible.user_id = ug_cible.user_id
JOIN
    utilisateur_orientation uo_cible ON u_cible.user_id = uo_cible.user_id
JOIN
    utilisateur u_reco ON u_cible.user_id <> u_reco.user_id -- Ne pas se recommander soi-même
JOIN
    profil p_reco ON u_reco.user_id = p_reco.user_id
JOIN
    utilisateur_genre ug_reco ON u_reco.user_id = ug_reco.user_id
JOIN
    utilisateur_orientation uo_reco ON u_reco.user_id = uo_reco.user_id
-- Jointure pour la compatibilité de la cible (moi) vers l'utilisateur recommandé
JOIN -- CHANGEMENT : JOINTURE INTERNE pour la compatibilité obligatoire
    compatibilite_genre_orientation cgo_cible_vers_reco
    ON ug_cible.genre_id = cgo_cible_vers_reco.genre_id
   AND uo_cible.orientation_id = cgo_cible_vers_reco.orientation_id
   AND ug_reco.genre_id = cgo_cible_vers_reco.target_genre_id
-- Jointure pour la compatibilité de l'utilisateur recommandé vers la cible (moi)
JOIN -- NOUVEAU : JOINTURE INTERNE pour la compatibilité obligatoire dans l'autre sens
    compatibilite_genre_orientation cgo_reco_vers_cible
    ON ug_reco.genre_id = cgo_reco_vers_cible.genre_id
   AND uo_reco.orientation_id = cgo_reco_vers_cible.orientation_id
   AND ug_cible.genre_id = cgo_reco_vers_cible.target_genre_id
-- Jointures pour les tags communs
LEFT JOIN
    utilisateur_tag ut_cible ON u_cible.user_id = ut_cible.user_id
LEFT JOIN
    utilisateur_tag ut_reco ON u_reco.user_id = ut_reco.user_id
LEFT JOIN
    tag t_commun ON ut_cible.tag_id = t_commun.tag_id AND ut_reco.tag_id = t_commun.tag_id
-- Jointures pour les événements avec tags communs
LEFT JOIN
    evenement_tag event_tag_match ON ut_cible.tag_id = event_tag_match.tag_id -- Événements qui ont les tags de l'utilisateur cible
LEFT JOIN
    evenement e_t ON event_tag_match.evenement_id = e_t.id
LEFT JOIN
    evenement_lieu el ON e_t.id = el.evenement_id
LEFT JOIN
    lieu l ON el.lieu_id = l.id
LEFT JOIN
    historique_activite ha_cible ON u_cible.user_id = ha_cible.user_id AND e_t.id = ha_cible.evenement_id -- Vérifier si utilisateur cible a déjà participé
LEFT JOIN
    genre g_reco ON ug_reco.genre_id = g_reco.genre_id
LEFT JOIN
    orientation o_reco ON uo_reco.orientation_id = o_reco.orientation_id
WHERE
    u_cible.user_id = 5 -- L'ID de l'utilisateur pour lequel on génère les recommandations (ex: 5)
    -- L'ancienne condition `cgo.genre_id IS NOT NULL` n'est plus nécessaire ici car les JOINs sont maintenant internes.
GROUP BY
    u_reco.user_id, u_reco.pseudo, u_reco.nom, u_reco.prenom, p_reco.ville, p_reco.pays, p_reco.date_naissance,
    g_reco.nom_du_genre, o_reco.nom_orientation,
    p_cible.pays, p_cible.ville, p_cible.date_naissance,
    cgo_cible_vers_reco.genre_id -- Il faut que toutes les colonnes non agrégées utilisées dans le SELECT ou le calcul du score soient dans le GROUP BY
ORDER BY
    score_compatibilite DESC;
*/






/*
WITH utilisateur_source AS (
    SELECT u.user_id, p.ville, ug.genre_id, uo.orientation_id
    FROM utilisateur u
    JOIN profil p ON u.user_id = p.user_id
    JOIN utilisateur_genre ug ON u.user_id = ug.user_id
    JOIN utilisateur_orientation uo ON u.user_id = uo.user_id
    WHERE u.pseudo = 'MargaretDiallo7060679'
)

SELECT 
    u2.user_id, 
    u2.pseudo, 
    p2.ville, 
    g2.nom_du_genre AS genre, 
    o2.nom_orientation AS orientation, 
    STRING_AGG(t.nom_tag, ', ') AS hobbies
FROM utilisateur_source us
JOIN profil p2 ON p2.ville = us.ville
JOIN utilisateur u2 ON u2.user_id = p2.user_id AND u2.user_id != us.user_id
JOIN utilisateur_genre ug2 ON ug2.user_id = u2.user_id
JOIN genre g2 ON g2.genre_id = ug2.genre_id
JOIN utilisateur_orientation uo2 ON uo2.user_id = u2.user_id
JOIN orientation o2 ON o2.orientation_id = uo2.orientation_id
-- Compatibilité depuis l'utilisateur source vers les autres
JOIN compatibilite_genre_orientation cgo 
    ON cgo.genre_id = us.genre_id 
   AND cgo.orientation_id = us.orientation_id 
   AND cgo.target_genre_id = ug2.genre_id
-- Compatibilité réciproque (facultatif)
JOIN compatibilite_genre_orientation cgo2 
    ON cgo2.genre_id = ug2.genre_id 
   AND cgo2.orientation_id = uo2.orientation_id 
   AND cgo2.target_genre_id = us.genre_id
-- Hobbies
LEFT JOIN utilisateur_tag ut ON ut.user_id = u2.user_id
LEFT JOIN tag t ON t.tag_id = ut.tag_id
GROUP BY u2.user_id, u2.pseudo, p2.ville, g2.nom_du_genre, o2.nom_orientation
;
*/




/*A pour but de donner un évenement en fonction des hobbys commun d'un date*/
/*
WITH date_users AS (
    SELECT utilisateur_1_id, utilisateur_2_id
    FROM dates
    WHERE id = 1
),
common_tags AS (
    SELECT ut1.tag_id
    FROM utilisateur_tag ut1
    JOIN utilisateur_tag ut2
        ON ut1.tag_id = ut2.tag_id
    JOIN date_users du
        ON ut1.user_id = du.utilisateur_1_id AND ut2.user_id = du.utilisateur_2_id
),
events_with_common_tags AS (
    SELECT e.id, e.nom_evenement
    FROM evenement e
    JOIN evenement_tag et ON e.id = et.evenement_id
    WHERE et.tag_id IN (SELECT tag_id FROM common_tags)
)
SELECT *
FROM events_with_common_tags;
*/










-- Quels sont les utilisateurs qui ont la même date d'anniversaire ?
--On pourra utiliser cette requête pour proposer des évènements de fête d'anniversaire partagées

--Version donnant un résultat beaucoup trop grand
-- SELECT u1.pseudo, p1.date_naissance, u2.pseudo, p2.date_naissance
-- FROM utilisateur u1
-- JOIN profil p1 ON u1.user_id = p1.user_id
-- JOIN utilisateur u2 ON u1.user_id <> u2.user_id
-- JOIN profil p2 ON u2.user_id = p2.user_id
-- WHERE EXTRACT(MONTH FROM p1.date_naissance) = EXTRACT(MONTH FROM p2.date_naissance) AND EXTRACT(DAY FROM p1.date_naissance) = EXTRACT(DAY FROM p2.date_naissance)
-- ORDER BY EXTRACT(MONTH FROM p1.date_naissance), EXTRACT(DAY FROM p1.date_naissance), p1.date_naissance;

-- SELECT TO_CHAR(p1.date_naissance, 'MM-DD'), STRING_AGG(DISTINCT u1.pseudo || ' (' || EXTRACT(YEAR FROM p1.date_naissance) || ')', ', ')
-- FROM utilisateur u1
-- JOIN profil p1 ON u1.user_id = p1.user_id
-- JOIN utilisateur u2 ON u1.user_id <> u2.user_id
-- JOIN profil p2 ON u2.user_id = p2.user_id
-- WHERE EXTRACT(MONTH FROM p1.date_naissance) = EXTRACT(MONTH FROM p2.date_naissance) AND EXTRACT(DAY FROM p1.date_naissance) = EXTRACT(DAY FROM p2.date_naissance)
-- GROUP BY TO_CHAR(p1.date_naissance, 'MM-DD')
-- HAVING COUNT(DISTINCT u1.user_id) + COUNT(DISTINCT u2.user_id) >= 2
-- ORDER BY TO_CHAR(p1.date_naissance, 'MM-DD');

COMMIT;