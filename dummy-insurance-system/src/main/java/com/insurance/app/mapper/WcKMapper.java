package com.insurance.app.mapper;

import com.insurance.app.domain.WcPayment;
import com.insurance.app.domain.WcPaymentIdx;
import com.insurance.app.domain.WcSReference;

public interface WcKMapper {

    void updateKContracts(WcSReference wcSReference);
    void insertKPayment(WcPayment wcPayment);
    void insertKPaymentIdx(WcPaymentIdx paymentIdx);
}
