package com.insurance.app.mapper;

import com.insurance.app.domain.TcContracts;
import com.insurance.app.domain.TcInsuredPersons;

public interface TcMapper {
	int nayoseInsuredPersons(String insured_person_name_kana,
			String insured_person_birth_date,
			String insured_person_sex);

	void insertInsuredPersons(TcInsuredPersons wlInsuredPersons);

	void updateInsuredPersons(TcInsuredPersons wlInsuredPersons);

	int getContractId(int insured_person_id);

	void insertContracts(TcContracts wlContracts);
}
