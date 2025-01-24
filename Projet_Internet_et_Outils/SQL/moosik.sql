-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : dim. 07 mai 2023 à 14:16
-- Version du serveur : 10.4.28-MariaDB
-- Version de PHP : 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `moosik`
--

-- --------------------------------------------------------

--
-- Structure de la table `abonnement`
--

CREATE TABLE `abonnement` (
  `idA` int(255) NOT NULL,
  `id_abonne` int(255) NOT NULL,
  `id_abonnement` int(255) NOT NULL,
  `date` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `abonnement`
--

INSERT INTO `abonnement` (`idA`, `id_abonne`, `id_abonnement`, `date`) VALUES
(16, 2, 1, '2023-05-05 21:29:01'),
(53, 4, 2, '2023-05-06 18:38:41'),
(54, 1, 2, '2023-05-06 18:38:51'),
(55, 4, 1, '2023-05-06 18:41:53'),
(56, 5, 4, '2023-05-06 20:05:28'),
(57, 5, 1, '2023-05-07 12:20:15'),
(58, 1, 4, '2023-05-07 13:17:58'),
(59, 1, 5, '2023-05-07 13:18:43'),
(60, 6, 4, '2023-05-07 13:56:21'),
(61, 6, 1, '2023-05-07 13:56:40');

-- --------------------------------------------------------

--
-- Structure de la table `album`
--

CREATE TABLE `album` (
  `nom` varchar(256) NOT NULL,
  `artiste` varchar(256) NOT NULL,
  `annee` int(255) NOT NULL,
  `feat1` varchar(256) NOT NULL,
  `feat2` varchar(256) NOT NULL,
  `feat3` varchar(256) NOT NULL,
  `feat4` varchar(256) NOT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `album`
--

INSERT INTO `album` (`nom`, `artiste`, `annee`, `feat1`, `feat2`, `feat3`, `feat4`, `id`) VALUES
('premier_album', 'Gab', 2019, 'Kai', '', '', '', 1),
('ahahbahzbi', 'Gab', 2021, 'AA', 'BB', '', '', 2),
('Call me if you get lost', 'Tyler the creator', 2021, '', '', '', '', 3),
('premier_album', 'Gab', 2019, 'Kai', '', '', '', 4),
('ahahbahzbi', 'Gab', 2021, 'AA', 'BB', '', '', 5),
('Call me if you get lost', 'Tyler the creator', 2021, '', '', '', '', 6),
('test_search', 'Kai', 2022, '', '', '', '', 7);

-- --------------------------------------------------------

--
-- Structure de la table `artiste`
--

CREATE TABLE `artiste` (
  `nom` varchar(256) NOT NULL,
  `nb_albums` int(255) NOT NULL,
  `nb_morceaux` int(255) NOT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `artiste`
--

INSERT INTO `artiste` (`nom`, `nb_albums`, `nb_morceaux`, `id`) VALUES
('Gab', 3, 44, 1),
('Kai', 2, 26, 2),
('Tyler the creator', 0, 0, 3);

-- --------------------------------------------------------

--
-- Structure de la table `likes`
--

CREATE TABLE `likes` (
  `id` int(255) NOT NULL,
  `publication` int(255) NOT NULL,
  `user` int(255) NOT NULL,
  `date` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `likes`
--

INSERT INTO `likes` (`id`, `publication`, `user`, `date`) VALUES
(149, 4, 1, '2023-05-07 13:27:19'),
(150, 56, 5, '2023-05-07 13:30:29'),
(153, 30, 5, '2023-05-07 13:41:23'),
(155, 30, 1, '2023-05-07 13:41:34'),
(156, 22, 1, '2023-05-07 13:41:36'),
(157, 20, 5, '2023-05-07 13:44:28'),
(158, 30, 6, '2023-05-07 13:56:29'),
(159, 17, 6, '2023-05-07 13:56:49');

-- --------------------------------------------------------

--
-- Structure de la table `morceau`
--

CREATE TABLE `morceau` (
  `nom` varchar(256) NOT NULL,
  `artiste` varchar(256) NOT NULL,
  `album` varchar(256) NOT NULL,
  `annee` int(255) NOT NULL,
  `feat1` varchar(256) NOT NULL,
  `feat2` varchar(256) NOT NULL,
  `feat3` varchar(256) NOT NULL,
  `feat4` varchar(256) NOT NULL,
  `Date` datetime NOT NULL DEFAULT current_timestamp(),
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `morceau`
--

INSERT INTO `morceau` (`nom`, `artiste`, `album`, `annee`, `feat1`, `feat2`, `feat3`, `feat4`, `Date`, `id`) VALUES
('premier_son', 'Gab', 'premier_album', 2019, 'Kai', '', '', '', '2023-05-02 00:00:00', 1),
('test_deux', 'Gab', 'paaaerhaetha', 2019, 'Kai', 'Deuxiemefeat', '', '', '2023-05-02 00:00:00', 2),
('ajjajzaz', 'Kai', 'petitalbum', 2022, '', '', '', '', '2023-05-02 00:00:00', 3),
('bbbbab', 'Kai', 'petitalbum', 2022, 'Gab', 'zga', 'aerge', 'arga', '2023-05-02 00:00:00', 4);

-- --------------------------------------------------------

--
-- Structure de la table `new_album`
--

CREATE TABLE `new_album` (
  `id` int(255) NOT NULL,
  `nom` varchar(256) NOT NULL,
  `artiste` varchar(256) NOT NULL,
  `annee` int(255) NOT NULL,
  `feat1` varchar(256) NOT NULL,
  `feat2` varchar(256) NOT NULL,
  `feat3` varchar(256) NOT NULL,
  `feat4` varchar(256) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `new_artiste`
--

CREATE TABLE `new_artiste` (
  `nom` varchar(256) NOT NULL,
  `morceau1` varchar(256) NOT NULL,
  `morceau2` varchar(256) NOT NULL,
  `morceau3` varchar(256) NOT NULL,
  `morceau4` varchar(256) NOT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `new_artiste`
--

INSERT INTO `new_artiste` (`nom`, `morceau1`, `morceau2`, `morceau3`, `morceau4`, `id`) VALUES
('zvzv', 'zv', 'zv', 'zvz', 'z', 1),
('zvzv', 'zv', 'zv', 'zvz', 'z', 2),
('fazf', 'zafazf', 'azef', 'azef', '', 3);

-- --------------------------------------------------------

--
-- Structure de la table `new_morceau`
--

CREATE TABLE `new_morceau` (
  `nom` varchar(256) NOT NULL,
  `artiste` varchar(256) NOT NULL,
  `album` varchar(256) NOT NULL,
  `annee` int(255) NOT NULL,
  `feat1` varchar(256) NOT NULL,
  `feat2` varchar(256) NOT NULL,
  `feat3` varchar(256) NOT NULL,
  `feat4` varchar(256) NOT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `new_morceau`
--

INSERT INTO `new_morceau` (`nom`, `artiste`, `album`, `annee`, `feat1`, `feat2`, `feat3`, `feat4`, `id`) VALUES
('eazrg', 'eraerh', 'arg', 33, 'aerh', 'earh', '', '', 1),
('eye', 'srusr', 'zgqzrh', 5, '', '', '', '', 2),
('eazrg', 'eraerh', 'arg', 33, 'aerh', 'earh', '', '', 3),
('eye', 'srusr', 'zgqzrh', 5, '', '', '', '', 4),
('ssfdf', 'dfgdf', 'dfg', 3, '', '', '', '', 5);

-- --------------------------------------------------------

--
-- Structure de la table `posts`
--

CREATE TABLE `posts` (
  `id` int(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `userId` int(255) NOT NULL,
  `titre` varchar(255) NOT NULL,
  `artiste` varchar(255) DEFAULT NULL,
  `etoiles` int(2) DEFAULT NULL,
  `texte` varchar(255) DEFAULT NULL,
  `morceau` varchar(255) DEFAULT NULL,
  `date` datetime NOT NULL DEFAULT current_timestamp(),
  `likes` int(255) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `posts`
--

INSERT INTO `posts` (`id`, `type`, `userId`, `titre`, `artiste`, `etoiles`, `texte`, `morceau`, `date`, `likes`) VALUES
(14, 'article', 2, 'aerzeryaeryzey', '', 0, 'zeyze', '', '2023-05-06 14:55:36', 0),
(15, 'article', 2, 'ezryzeyr', '', 0, 'zeryezy', '', '2023-05-06 14:55:42', 0),
(16, 'article', 2, 'zeyzeyz', '', 0, 'zeryezryez', '', '2023-05-06 14:55:46', 0),
(17, 'article', 2, 'rtyjtrjtyrj', '', 0, 'rtyjtrjyrtjr', '', '2023-05-06 14:55:50', 1),
(18, 'article', 2, 'rtyjtjrtjtrjrtj', '', 0, 'rtyjrtjytryjr', '', '2023-05-06 14:55:55', 0),
(19, 'article', 1, 'Bonjour', '', 0, 'Rien', '', '2023-05-06 18:39:08', 0),
(20, 'avis_album', 1, 'test_search', 'Kai', 10, '', '', '2023-05-06 19:10:58', 1),
(21, 'avis_morceau', 5, 'ajjajzaz', 'Kai', 8, 'trop bien', '', '2023-05-06 19:16:18', 0),
(22, 'morceau', 5, 'Boys a liar ft joe', 'Obama', 0, '', '64569732579209.55195454.mp3', '2023-05-06 20:06:42', 0),
(30, 'morceau', 4, 'Boys a liar pt.2', 'IceSpice ', 0, '', '6457858cd876a7.63206851.mp3', '2023-05-07 13:03:40', 3);

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `username` varchar(256) NOT NULL,
  `mail` text NOT NULL,
  `mdp` varchar(256) NOT NULL,
  `admin` tinyint(1) NOT NULL DEFAULT 0,
  `abonnés` int(255) NOT NULL DEFAULT 0,
  `abonnements` int(255) NOT NULL DEFAULT 0,
  `nom` varchar(40) DEFAULT NULL,
  `bio` varchar(256) DEFAULT NULL,
  `pdp` varchar(256) DEFAULT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`username`, `mail`, `mdp`, `admin`, `abonnés`, `abonnements`, `nom`, `bio`, `pdp`, `id`) VALUES
('kai', 'kai@gmail.com', 'e79fb748c3c8ab532a8fcf2da53ae54d', 0, 3, 3, NULL, NULL, NULL, 1),
('Gab', 'gab@mail', '098f6bcd4621d373cade4e832627b4f6', 1, 2, 0, 'Gaby', 'Je fais des tests', '6454e25b9744d7.71177431.jpg', 2),
('test', 'test', '098f6bcd4621d373cade4e832627b4f6', 0, 0, 0, '', '', '', 3),
('IceSpice', 'IceSpice@gmail.com', 'be1ab1632e4285edc3733b142935c60b', 0, 3, 2, NULL, NULL, NULL, 4),
('Obama', 'obama@gmail.com', 'cf617aad567637e30de27caf1a926114', 1, 1, 2, 'Obama', 'Hi guys ', '645696c0286353.11247028.jpg', 5),
('jean', 'jean@gmail.com', 'b71985397688d6f1820685dde534981b', 0, 0, 2, 'fddf', 'fdgdf', '6457938604be16.88796215.png', 6);

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `abonnement`
--
ALTER TABLE `abonnement`
  ADD PRIMARY KEY (`idA`);

--
-- Index pour la table `album`
--
ALTER TABLE `album`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `artiste`
--
ALTER TABLE `artiste`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `likes`
--
ALTER TABLE `likes`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `morceau`
--
ALTER TABLE `morceau`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `new_album`
--
ALTER TABLE `new_album`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `new_artiste`
--
ALTER TABLE `new_artiste`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `new_morceau`
--
ALTER TABLE `new_morceau`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `posts`
--
ALTER TABLE `posts`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `abonnement`
--
ALTER TABLE `abonnement`
  MODIFY `idA` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=62;

--
-- AUTO_INCREMENT pour la table `album`
--
ALTER TABLE `album`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT pour la table `artiste`
--
ALTER TABLE `artiste`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT pour la table `likes`
--
ALTER TABLE `likes`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=160;

--
-- AUTO_INCREMENT pour la table `morceau`
--
ALTER TABLE `morceau`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT pour la table `new_album`
--
ALTER TABLE `new_album`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `new_artiste`
--
ALTER TABLE `new_artiste`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `new_morceau`
--
ALTER TABLE `new_morceau`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT pour la table `posts`
--
ALTER TABLE `posts`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
