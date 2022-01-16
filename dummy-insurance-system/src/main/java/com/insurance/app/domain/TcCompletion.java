package com.insurance.app.domain;

public class TcCompletion {
    private TcInsuredPersons tcInsuredPersons;
    private TcContracts tcContracts;

    public TcInsuredPersons getTcInsuredPersons() {
        return tcInsuredPersons;
    }
    public void setTcInsuredPersons(TcInsuredPersons tcInsuredPersons) {
        this.tcInsuredPersons = tcInsuredPersons;
    }
    public TcContracts getTcContracts() {
        return tcContracts;
    }
    public void setTcContracts(TcContracts tcContracts) {
        this.tcContracts = tcContracts;
    }

}
