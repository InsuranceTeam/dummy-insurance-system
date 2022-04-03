package com.insurance.app.mapper;

import com.insurance.app.domain.TcPayment;
import com.insurance.app.domain.TcPaymentIdx;
import com.insurance.app.domain.TcSContract;

public interface TcKMapper {

    void updateKContracts(TcSContract tcSContract);
    void insertKPayment(TcPayment tcPayment);
    void insertKPaymentIdx(TcPaymentIdx paymentIdx);
}
