CREATE TABLE `insurance_db`.`insured_persons` (
  `insured_person_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '被保険者番号',
  `insured_person_name_kanji` varchar(20) DEFAULT NULL COMMENT '被保険者氏名（漢字）',
  `insured_person_name_kana` varchar(20) NOT NULL COMMENT '被保険者氏名（ｶﾅ）',
  `insured_person_birth_date` date NOT NULL COMMENT '被保険者生年月日',
  `insured_person_sex` varchar(1) NOT NULL COMMENT '被保険者性別',
  `create_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '作成日',
  `update_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日',
  PRIMARY KEY (`insured_person_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000000000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='被保険者情報テーブル'
