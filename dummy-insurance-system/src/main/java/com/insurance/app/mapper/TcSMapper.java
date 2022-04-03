package com.insurance.app.mapper;

import java.util.List;

import com.insurance.app.domain.TcSContract;
import com.insurance.app.domain.TcSList;

public interface TcSMapper {

    List<TcSList>     searchTcSList(int insured_person_id);
    List<TcSContract> getTcSContract(int insured_person_id, int contract_id);
}
