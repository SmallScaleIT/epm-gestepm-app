CREATE TABLE `workshop_signing` (
  `workshop_signing_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `warehouse_signing_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `project_id` int(10) unsigned NOT NULL,
  `started_at` datetime NOT NULL,
  `closed_at` datetime DEFAULT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`workshop_signing_id`),
  KEY `user_id` (`user_id`),
  KEY `project_id` (`project_id`),
  KEY `warehouse_signing_id` (`warehouse_signing_id`),
  CONSTRAINT `fk_wk_project_id` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_wk_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE cascade,
  CONSTRAINT `fk_wk_warehouse_signing_id` FOREIGN KEY (`warehouse_signing_id`) REFERENCES `warehouse_signing` (`warehouse_signing_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;