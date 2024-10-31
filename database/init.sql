USE lolstagram;
CREATE TABLE `summoner_info` (
  `summoner_id` varchar(255) NOT NULL,
  `league_id` varchar(255) DEFAULT NULL,
  `league_points` bigint DEFAULT NULL,
  `tier` varchar(255) DEFAULT NULL,
  `tier_rank` bigint DEFAULT NULL,
  `total_avg_of_win` double DEFAULT NULL,
  `total_losses` bigint DEFAULT NULL,
  `total_wins` bigint DEFAULT NULL,
  `create_time` datetime(6) NOT NULL,
  `most_champion_list` varchar(1000) DEFAULT NULL,
  `puu_id` varchar(255) DEFAULT NULL,
  `recent_losses` bigint DEFAULT NULL,
  `recent_wins` bigint DEFAULT NULL,
  `total_kda` double DEFAULT NULL,
  `summoner_name` varchar(255) DEFAULT NULL,
  `summoner_tag` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`summoner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `matches` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assists` bigint DEFAULT NULL,
  `champion_name` varchar(255) DEFAULT NULL,
  `deaths` bigint DEFAULT NULL,
  `game_start_timestamp` bigint DEFAULT NULL,
  `game_type` varchar(255) DEFAULT NULL,
  `item_list` varchar(255) DEFAULT NULL,
  `kda` varchar(255) DEFAULT NULL,
  `kills` bigint DEFAULT NULL,
  `main_rune` bigint DEFAULT NULL,
  `match_id` varchar(255) DEFAULT NULL,
  `result` varchar(255) DEFAULT NULL,
  `sub_rune` bigint DEFAULT NULL,
  `summoner_spell_list` varchar(255) DEFAULT NULL,
  `summoner_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmvn7ym4w8h682hsu9r0hjaax1` (`summoner_id`),
  CONSTRAINT `FKmvn7ym4w8h682hsu9r0hjaax1` FOREIGN KEY (`summoner_id`) REFERENCES `summoner_info` (`summoner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=221 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `member` (
  `member_id` bigint NOT NULL AUTO_INCREMENT,
  `email_id` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `profile_image` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `summoner_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`member_id`),
  KEY `FK27nkitil97vbniv5xy9y9nlvt` (`summoner_id`),
  CONSTRAINT `FK27nkitil97vbniv5xy9y9nlvt` FOREIGN KEY (`summoner_id`) REFERENCES `summoner_info` (`summoner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `post` (
  `post_id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(255) DEFAULT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `member_id` bigint DEFAULT NULL,
  PRIMARY KEY (`post_id`),
  KEY `FK83s99f4kx8oiqm3ro0sasmpww` (`member_id`),
  CONSTRAINT `FK83s99f4kx8oiqm3ro0sasmpww` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `post_comment` (
  `post_comment_id` bigint NOT NULL AUTO_INCREMENT,
  `comment` varchar(255) DEFAULT NULL,
  `post` bigint DEFAULT NULL,
  `writer` bigint DEFAULT NULL,
  PRIMARY KEY (`post_comment_id`),
  KEY `FKp3lr8t5c4vy9bno6yrokieeny` (`post`),
  KEY `FKelsa3ojwk6fct3g3mh19ne5ee` (`writer`),
  CONSTRAINT `FKelsa3ojwk6fct3g3mh19ne5ee` FOREIGN KEY (`writer`) REFERENCES `member` (`member_id`),
  CONSTRAINT `FKp3lr8t5c4vy9bno6yrokieeny` FOREIGN KEY (`post`) REFERENCES `post` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
