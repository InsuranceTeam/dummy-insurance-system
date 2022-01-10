CREATE TABLE `insurance_db`.`payment` (
  `payment_control_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '支払管理番号',
  `payment_amount` varchar(8) NOT NULL COMMENT '支払額',
  `create_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '作成日',
  `update_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日',
  PRIMARY KEY (`payment_control_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000000000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支払情報テーブル'