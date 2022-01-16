package com.insurance.app.mapper;

import org.apache.ibatis.annotations.Param;

import com.insurance.app.domain.TlContracts;
import com.insurance.app.domain.TlInput;
import com.insurance.app.domain.TlInsuredPersons;

public interface TlMapper {
    int nayoseInsuredPersons(@Param("tlInput") TlInput tlInput);

    void insertInsuredPersons(TlInsuredPersons wlInsuredPersons);

    void updateInsuredPersons(TlInsuredPersons wlInsuredPersons);

    int getContractId(int insured_person_id);

    void insertContracts(TlContracts wlContracts);
}