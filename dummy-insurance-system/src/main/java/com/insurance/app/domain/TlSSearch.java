package com.insurance.app.domain;

import lombok.Data;

@Data
public class TlSSearch {

	private String insured_person_id; // 被保険者番号
	private String insured_person_id_error; // 被保険者番号 - エラー

}
