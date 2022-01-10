package com.insurance.app.mapper;

import com.insurance.app.domain.WcContracts;
import com.insurance.app.domain.WcInsuredPersons;

public interface WcMapper {
    int nayoseInsuredPersons(String insured_person_name_kana,
                             String insured_person_birth_date,
                             String insured_person_sex);

    void insertInsuredPersons(WcInsuredPersons wcInsuredPersons);

    void updateInsuredPersons(WcInsuredPersons wcInsuredPersons);

    int getContractId(int insured_person_id);

    void insertContracts(WcContracts wcContracts);
}
