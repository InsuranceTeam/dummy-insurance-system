package com.insurance.app.mapper;

import java.util.List;

import com.insurance.app.domain.TlSContracts;
import com.insurance.app.domain.TlSList;
import com.insurance.app.domain.TlSPayment;

public interface TlSMapper {

    List<TlSList>     searchTlSList(String insured_person_id);
    TlSContracts getTlSContract(String insured_person_id, String contract_id);
    void updateRefundContract(String contract_end_date, String contract_end_reason,String insured_person_id,String contract_id,String contract_history_id);
    void insertPaymentIdx(TlSPayment tlSPayment);
    void insertPayment(TlSPayment tlSPayment);
    
}
