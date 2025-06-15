-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Cze 14, 2025 at 03:11 PM
-- Wersja serwera: 10.4.28-MariaDB
-- Wersja PHP: 8.1.17

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `healthtracker`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `activities`
--

CREATE TABLE `activities` (
  `id` bigint(20) NOT NULL,
  `avgSpeed` double DEFAULT NULL,
  `caloriesBurned` int(11) DEFAULT NULL,
  `distanceKm` double DEFAULT NULL,
  `durationMinutes` int(11) NOT NULL,
  `heartRateAvg` int(11) DEFAULT NULL,
  `heartRateMax` int(11) DEFAULT NULL,
  `intensity` enum('LOW','MODERATE','HIGH','VERY_HIGH') DEFAULT NULL,
  `notes` varchar(500) DEFAULT NULL,
  `timestamp` datetime(6) NOT NULL,
  `type_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `activities`
--

INSERT INTO `activities` (`id`, `avgSpeed`, `caloriesBurned`, `distanceKm`, `durationMinutes`, `heartRateAvg`, `heartRateMax`, `intensity`, `notes`, `timestamp`, `type_id`, `user_id`) VALUES
(1, NULL, 350, NULL, 60, NULL, NULL, 'VERY_HIGH', NULL, '2025-05-26 18:03:24.000000', 5, 1),
(2, NULL, 564, NULL, 35, 117, 197, 'HIGH', NULL, '2025-06-12 20:10:12.000000', 7, 1),
(3, NULL, 93, NULL, 32, 121, 131, 'LOW', NULL, '2025-06-13 16:01:04.000000', 6, 1),
(4, NULL, 93, NULL, 32, 121, 131, 'LOW', NULL, '2025-06-13 16:01:00.000000', 6, 3),
(5, NULL, 564, NULL, 35, 117, 197, 'HIGH', NULL, '2025-06-12 20:10:00.000000', 7, 3),
(6, NULL, 350, NULL, 60, NULL, NULL, 'VERY_HIGH', NULL, '2025-05-26 18:03:00.000000', 5, 3);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `activity_types`
--

CREATE TABLE `activity_types` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `unit` varchar(255) NOT NULL,
  `category` enum('CARDIO','STRENGTH','FLEXIBILITY','WATER_SPORTS','TEAM_SPORTS','WINTER_SPORTS','OUTDOOR') DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `iconName` varchar(255) DEFAULT NULL,
  `requiresDistance` bit(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `activity_types`
--

INSERT INTO `activity_types` (`id`, `name`, `unit`, `category`, `description`, `iconName`, `requiresDistance`) VALUES
(1, 'Bieganie', 'km', 'CARDIO', 'Bieganie na świeżym powietrzu lub bieżni', NULL, b'1'),
(2, 'Rower', 'km', 'CARDIO', 'Jazda na rowerze', NULL, b'1'),
(3, 'Chodzenie', 'km', 'CARDIO', 'Spacer lub marsz', NULL, b'1'),
(4, 'Pływanie', 'm', 'WATER_SPORTS', 'Pływanie w basenie lub akwenie', NULL, b'1'),
(5, 'Siłownia', 'serie', 'STRENGTH', 'Trening siłowy', NULL, b'0'),
(6, 'Joga', 'min', 'FLEXIBILITY', 'Ćwiczenia jogi', NULL, b'0'),
(7, 'Aerobik', 'min', 'CARDIO', 'Ćwiczenia aerobowe', NULL, b'0'),
(8, 'Pilates', 'min', 'FLEXIBILITY', 'Ćwiczenia pilates', NULL, b'0'),
(9, 'Tenis', 'sety', 'TEAM_SPORTS', 'Gra w tenisa', NULL, b'0');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `bp_measurements`
--

CREATE TABLE `bp_measurements` (
  `diastolic` int(11) NOT NULL,
  `systolic` int(11) NOT NULL,
  `id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bp_measurements`
--

INSERT INTO `bp_measurements` (`diastolic`, `systolic`, `id`) VALUES
(80, 120, 2),
(80, 120, 5),
(70, 100, 9),
(87, 117, 13),
(80, 120, 26),
(80, 120, 29),
(70, 100, 32),
(87, 117, 36),
(79, 118, 38),
(75, 110, 40),
(82, 123, 43),
(78, 115, 47),
(85, 125, 50),
(83, 121, 53),
(76, 119, 56),
(80, 122, 59),
(89, 120, 63);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `goals`
--

CREATE TABLE `goals` (
  `id` bigint(20) NOT NULL,
  `createdDate` date NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `dueDate` date NOT NULL,
  `goalType` enum('WEIGHT_LOSS','TARGET_WEIGHT','ACTIVITY_HOURS','DAILY_CALORIES') NOT NULL,
  `targetValue` double NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `goals`
--

INSERT INTO `goals` (`id`, `createdDate`, `description`, `dueDate`, `goalType`, `targetValue`, `user_id`) VALUES
(1, '2025-05-26', '', '2025-07-31', 'TARGET_WEIGHT', 75, 1);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `heart_rate_measurements`
--

CREATE TABLE `heart_rate_measurements` (
  `bpm` int(11) NOT NULL,
  `id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `heart_rate_measurements`
--

INSERT INTO `heart_rate_measurements` (`bpm`, `id`) VALUES
(66, 3),
(98, 8),
(103, 12),
(66, 27),
(98, 31),
(103, 35),
(72, 39),
(94, 41),
(70, 44),
(101, 45),
(88, 51),
(77, 54),
(81, 57),
(85, 60),
(74, 62);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `meals`
--

CREATE TABLE `meals` (
  `id` bigint(20) NOT NULL,
  `calories` int(11) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `timestamp` datetime(6) DEFAULT NULL,
  `type_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `meals`
--

INSERT INTO `meals` (`id`, `calories`, `description`, `timestamp`, `type_id`, `user_id`) VALUES
(1, 90, 'Rosół', '2025-05-26 18:11:32.000000', 2, 1);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `meal_types`
--

CREATE TABLE `meal_types` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `meal_types`
--

INSERT INTO `meal_types` (`id`, `name`) VALUES
(5, 'Drugie śniadanie'),
(3, 'Kolacja'),
(2, 'Obiad'),
(6, 'Podwieczorek'),
(4, 'Przekąska'),
(1, 'Śniadanie');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `measurements`
--

CREATE TABLE `measurements` (
  `id` bigint(20) NOT NULL,
  `timestamp` datetime(6) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `measurements`
--

INSERT INTO `measurements` (`id`, `timestamp`, `user_id`) VALUES
(1, '2025-05-22 10:00:00.000000', 1),
(2, '2025-05-22 10:00:00.000000', 1),
(3, '2025-05-22 10:00:00.000000', 1),
(4, '2025-05-22 10:00:00.000000', 1),
(5, '2025-05-22 11:00:00.000000', 1),
(7, '2025-05-25 20:33:09.000000', 1),
(8, '2025-05-25 20:33:09.000000', 1),
(9, '2025-05-25 20:33:09.000000', 1),
(10, '2025-05-26 19:27:02.000000', 1),
(11, '2025-06-06 22:07:11.000000', 1),
(12, '2025-06-06 22:07:11.000000', 1),
(13, '2025-06-06 22:07:11.000000', 1),
(20, '2025-05-22 10:00:00.000000', 4),
(21, '2025-05-22 10:00:00.000000', 4),
(22, '2025-05-25 20:33:00.000000', 4),
(23, '2025-05-26 19:27:00.000000', 4),
(24, '2025-06-06 22:07:00.000000', 4),
(25, '2025-05-22 10:00:00.000000', 3),
(26, '2025-05-22 10:00:00.000000', 3),
(27, '2025-05-22 10:00:00.000000', 3),
(28, '2025-05-22 10:00:00.000000', 3),
(29, '2025-05-22 11:00:00.000000', 3),
(30, '2025-05-25 20:33:00.000000', 3),
(31, '2025-05-25 20:33:00.000000', 3),
(32, '2025-05-25 20:33:00.000000', 3),
(33, '2025-05-26 19:27:00.000000', 3),
(34, '2025-06-06 22:07:00.000000', 3),
(35, '2025-06-06 22:07:00.000000', 3),
(36, '2025-06-06 22:07:00.000000', 3),
(37, '2025-05-23 08:45:00.000000', 1),
(38, '2025-05-23 08:45:00.000000', 1),
(39, '2025-05-23 08:45:00.000000', 1),
(40, '2025-05-24 21:10:00.000000', 1),
(41, '2025-05-24 21:10:00.000000', 1),
(42, '2025-05-27 10:00:00.000000', 1),
(43, '2025-05-27 10:00:00.000000', 1),
(44, '2025-05-27 10:00:00.000000', 1),
(45, '2025-05-30 17:30:00.000000', 1),
(46, '2025-06-05 09:15:00.000000', 1),
(47, '2025-06-05 09:15:00.000000', 1),
(48, '2025-05-24 21:10:00.000000', 1),
(49, '2025-05-30 17:30:00.000000', 1),
(50, '2025-05-30 17:30:00.000000', 1),
(51, '2025-06-05 09:15:00.000000', 1),
(52, '2025-06-07 14:20:00.000000', 1),
(53, '2025-06-07 14:20:00.000000', 1),
(54, '2025-06-07 14:20:00.000000', 1),
(55, '2025-06-08 07:45:00.000000', 1),
(56, '2025-06-08 07:45:00.000000', 1),
(57, '2025-06-08 07:45:00.000000', 1),
(58, '2025-06-09 18:30:00.000000', 1),
(59, '2025-06-09 18:30:00.000000', 1),
(60, '2025-06-09 18:30:00.000000', 1),
(61, '2025-06-13 18:36:57.000000', 1),
(62, '2025-06-13 18:36:57.000000', 1),
(63, '2025-06-13 18:36:57.000000', 1);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `passwordHash` varchar(255) DEFAULT NULL,
  `role` enum('USER','ADMIN') DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `createdAt` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `email`, `passwordHash`, `role`, `username`, `createdAt`) VALUES
(1, 'w-piwowar@wp.pl', '$2a$10$U65wbOHiHyh3NYlUeWLfJOL4vphOND8Tzl/uU2A6D4KNNLqlolsUW', 'USER', 'DevZER0', NULL),
(3, 'staruszka123@example.com', '$2a$10$NKpZ59jVPVFqntpOoqv6Buh.dBH1F1qD24G1BlGGorwKu.BwjxDG6', 'USER', 'staruszka123', NULL),
(4, 'pjoter420@example.com', '$2a$10$PueTedI75gsFHUiHPmYxauJ1et7OREAf3bh3KQ90zJZNbgX1X/PZS', 'USER', 'pjoter420', '2025-06-13 15:01:42.000000'),
(6, 'admin@example.com', '$2a$10$9S8rlpr1vagY1bzgQqAu0.IrP4cnXVp/jRSMayUYgo/gWslkTQuJa', 'ADMIN', 'Admin', '2025-06-13 22:36:52.000000');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `weight_measurements`
--

CREATE TABLE `weight_measurements` (
  `weight` double NOT NULL,
  `id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `weight_measurements`
--

INSERT INTO `weight_measurements` (`weight`, `id`) VALUES
(72.5, 1),
(72.5, 4),
(61, 7),
(69.8, 10),
(76.5, 11),
(72, 20),
(72, 21),
(61, 22),
(69, 23),
(76, 24),
(72.5, 25),
(72.5, 28),
(61, 30),
(69.8, 33),
(76.5, 34),
(71.9, 37),
(70.5, 42),
(76.1, 46),
(68.2, 48),
(69.8, 49),
(75.3, 52),
(74.9, 55),
(75.6, 58),
(75.5, 61);

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `activities`
--
ALTER TABLE `activities`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKrfrixuyeyeh9u3wy36hj3pid5` (`type_id`),
  ADD KEY `FKq6cjukylkgxdjkm9npk9va2f2` (`user_id`);

--
-- Indeksy dla tabeli `activity_types`
--
ALTER TABLE `activity_types`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_36uexkiww6jkyqnxwjl5a9mmy` (`name`);

--
-- Indeksy dla tabeli `bp_measurements`
--
ALTER TABLE `bp_measurements`
  ADD PRIMARY KEY (`id`);

--
-- Indeksy dla tabeli `goals`
--
ALTER TABLE `goals`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKb1mp6ulyqkpcw6bc1a2mr7v1g` (`user_id`);

--
-- Indeksy dla tabeli `heart_rate_measurements`
--
ALTER TABLE `heart_rate_measurements`
  ADD PRIMARY KEY (`id`);

--
-- Indeksy dla tabeli `meals`
--
ALTER TABLE `meals`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK2m5fichndhhdclfvoufk92rsl` (`type_id`),
  ADD KEY `FK677c66qpjr7234luomahc1ale` (`user_id`);

--
-- Indeksy dla tabeli `meal_types`
--
ALTER TABLE `meal_types`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_ixwt13sabar55l63e36prodw3` (`name`);

--
-- Indeksy dla tabeli `measurements`
--
ALTER TABLE `measurements`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKa6kjrhmi7c4w4y0djie5yifpg` (`user_id`);

--
-- Indeksy dla tabeli `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- Indeksy dla tabeli `weight_measurements`
--
ALTER TABLE `weight_measurements`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `activities`
--
ALTER TABLE `activities`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `activity_types`
--
ALTER TABLE `activity_types`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `goals`
--
ALTER TABLE `goals`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `meals`
--
ALTER TABLE `meals`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `meal_types`
--
ALTER TABLE `meal_types`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `measurements`
--
ALTER TABLE `measurements`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=64;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `activities`
--
ALTER TABLE `activities`
  ADD CONSTRAINT `FKq6cjukylkgxdjkm9npk9va2f2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKrfrixuyeyeh9u3wy36hj3pid5` FOREIGN KEY (`type_id`) REFERENCES `activity_types` (`id`);

--
-- Constraints for table `bp_measurements`
--
ALTER TABLE `bp_measurements`
  ADD CONSTRAINT `FKdu9cg98rx297wo5kkp15qq82u` FOREIGN KEY (`id`) REFERENCES `measurements` (`id`);

--
-- Constraints for table `goals`
--
ALTER TABLE `goals`
  ADD CONSTRAINT `FKb1mp6ulyqkpcw6bc1a2mr7v1g` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `heart_rate_measurements`
--
ALTER TABLE `heart_rate_measurements`
  ADD CONSTRAINT `FK3xc3atv7ldb3qc0qwg8bd8c1e` FOREIGN KEY (`id`) REFERENCES `measurements` (`id`);

--
-- Constraints for table `meals`
--
ALTER TABLE `meals`
  ADD CONSTRAINT `FK2m5fichndhhdclfvoufk92rsl` FOREIGN KEY (`type_id`) REFERENCES `meal_types` (`id`),
  ADD CONSTRAINT `FK677c66qpjr7234luomahc1ale` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `measurements`
--
ALTER TABLE `measurements`
  ADD CONSTRAINT `FKa6kjrhmi7c4w4y0djie5yifpg` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `weight_measurements`
--
ALTER TABLE `weight_measurements`
  ADD CONSTRAINT `FK7hrem3wg16rifould5ff8h16v` FOREIGN KEY (`id`) REFERENCES `measurements` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
