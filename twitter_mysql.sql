CREATE DATABASE  IF NOT EXISTS `twitter_db`;

USE `twitter_db`;

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
	`id` int NOT NULL AUTO_INCREMENT,
	`username` varchar(255) NOT NULL UNIQUE,
	`password` varchar(255) NOT NULL,
	`email` varchar (255) NOT NULL UNIQUE,
	`display_name` varchar (255) NOT NULL,
	`following_count` int NOT NULL,
	`followers_count` int NOT NULL,
	PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `following`;

CREATE TABLE `following` (
	`id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
	`following_id` int NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `tweets`;

CREATE TABLE `tweets` (
	`id` int NOT NULL AUTO_INCREMENT,
	`user_id` int NOT NULL,
	`message` varchar(255) NOT NULL,
	`time` DATETIME NOT NULL,
	PRIMARY KEY (`id`)
);

ALTER TABLE `following` ADD CONSTRAINT `following_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`);

ALTER TABLE `following` ADD CONSTRAINT `following_following_id_fk` FOREIGN KEY (`following_id`) REFERENCES `user`(`id`);

ALTER TABLE `tweets` ADD CONSTRAINT `tweets_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`);
