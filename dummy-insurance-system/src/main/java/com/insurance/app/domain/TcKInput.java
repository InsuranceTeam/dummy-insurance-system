package com.insurance.app.domain;

public class TcKInput{

    //照会画面情報
    private TcSContract tcSContract = new TcSContract();

    //解約・取消区分
    private String  select_cancel;

    //解約日
    private String  cancel_date;

    public TcSContract getTcSContract() {
        return tcSContract;
    }

    public void setTcSContract(TcSContract tcSContract) {
        this.tcSContract = tcSContract;
    }

    public String getSelect_cancel() {
        return select_cancel;
    }

    public void setSelect_cancel(String select_cancel) {
        this.select_cancel = select_cancel;
    }

    public String getCancel_date() {
        return cancel_date;
    }

    public void setCancel_date(String cancel_date) {
        this.cancel_date = cancel_date;
    }

}
