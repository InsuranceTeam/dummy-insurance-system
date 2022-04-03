package com.insurance.app.domain;

import lombok.Data;

@Data
public class TlSPayment {
	private int payment_control_id;
	private int payment_amount;
	private int insured_person_id;
	private int contract_id;

}
