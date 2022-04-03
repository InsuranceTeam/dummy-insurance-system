package com.insurance.app.domain;

public class WcKInput{

    //照会画面情報
    private WcSReference wcSReference = new WcSReference();

    //解約・取消区分
    private String  select_cancel;

    //解約日
    private String  cancel_date;

    public WcSReference getWcSReference() {
        return wcSReference;
    }

    public void setWcSReference(WcSReference wcSReference) {
        this.wcSReference = wcSReference;
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
