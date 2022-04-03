package com.insurance.app.mapper;

import java.util.List;

import com.insurance.app.domain.WcSList;
import com.insurance.app.domain.WcSReference;

public interface WcSMapper {

    List<WcSList>     selectWcSList(int insured_person_id);
    List<WcSReference> selectWcSReference(int insured_person_id, int contract_id);
}
