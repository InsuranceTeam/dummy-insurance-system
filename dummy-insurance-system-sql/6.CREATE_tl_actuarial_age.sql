CREATE TABLE `insurance_db`.`tl_actuarial_age` (
  `security_type` varchar(2) NOT NULL COMMENT '保障種類',
  `entry_age` tinyint unsigned NOT NULL COMMENT '加入年齢',
  `insured_person_sex` varchar(1) NOT NULL COMMENT '被保険者性別',
  `payment_method` varchar(1) NOT NULL COMMENT '払込方法',
  `premium_payment_term` tinyint unsigned NOT NULL COMMENT '掛金払込期間',
  `apply_start_date` date NOT NULL COMMENT '適用開始日',
  `apply_end_date` date NOT NULL COMMENT '適用終了日',
  `unit_price_premium_age` int NOT NULL COMMENT '掛金単価（年齢別）',
  `create_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '作成日',
  `update_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日',
  PRIMARY KEY (`security_type`,`entry_age`,`insured_person_sex`,`payment_method`,`premium_payment_term`,`apply_start_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数理テーブル（定期生命）＿年齢別'