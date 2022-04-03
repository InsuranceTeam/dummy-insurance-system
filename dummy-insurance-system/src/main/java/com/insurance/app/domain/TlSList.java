package com.insurance.app.domain;

import lombok.Data;

@Data
public class TlSList {
	private int insured_person_id; //被保険者番号
	private String insured_person_name_kanji; //被保険者氏名（漢字）
	private String insured_person_name_kana; //被保険者氏名（ｶﾅ）
	private String insured_person_birth_date; //被保険者生年月日
	private String insured_person_sex; //被保険者性別
	private int contract_id; //契約番号
	private String contract_start_date; //契約開始日
	private String contract_end_date; //契約終了日

}
