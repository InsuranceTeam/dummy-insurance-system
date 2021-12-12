CREATE TABLE `insurance_db`.`payment_idx` (
  `insured_person_id` int unsigned NOT NULL COMMENT '被保険者番号',
  `contract_id` smallint unsigned NOT NULL COMMENT '契約番号',
  `payment_control_id` int unsigned NOT NULL COMMENT '支払管理番号',
  `create_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '作成日',
  `update_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日',
  PRIMARY KEY (`insured_person_id`,`contract_id`,`payment_control_id`),
  CONSTRAINT `payment_idx_ibfk_1` FOREIGN KEY (`insured_person_id`, `contract_id`) REFERENCES `contracts` (`insured_person_id`, `contract_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支払情報インデックステーブル'