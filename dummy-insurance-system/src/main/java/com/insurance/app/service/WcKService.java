package com.insurance.app.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.ibatis.binding.BindingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.insurance.app.domain.WcKConfimation;
import com.insurance.app.domain.WcPayment;
import com.insurance.app.domain.WcPaymentIdx;
import com.insurance.app.domain.WcSReference;
import com.insurance.app.mapper.WcKMapper;

@Service
public class WcKService {

    @Autowired
    private WcKMapper wcKMapper;

    String errorInfo;

    @Transactional(rollbackFor = Exception.class)
    public String update(WcKConfimation wcKConfimation)
                                 throws BindingException, Exception {

        try {

            WcSReference wcSReference = wcKConfimation.getWcSReference();
            WcPayment wcPayment = new WcPayment(wcKConfimation.getCancellation_refund());
            WcPaymentIdx wcPaymentIdx = null;

            if(wcKConfimation.getSelect_cancel().equals("1")) {
                //解約処理に応じた更新処理を行なう

                try {
                    //契約情報テーブルの更新を行なう
                    wcSReference.setContract_end_date(wcKConfimation.getCancel_date().replace("/", ""));
                    wcSReference.setContract_end_reason("K0");
                    wcKMapper.updateKContracts(wcSReference);
                }catch(Exception e) {
                    e.printStackTrace();
                    errorInfo = editErrorUpdateKContracts(wcSReference);
                    throw new Exception(errorInfo,e);
                };

                //支払情報テーブルの登録を行なう
                try {
                    wcKMapper.insertKPayment(wcPayment);

                    //支払情報を照会情報エリアに設定する
                    wcSReference.setPayment_control_id(wcPayment.getPayment_control_id());
                    wcSReference.setPayment_amount(wcPayment.getPayment_amount());

                }catch(Exception e) {
                    e.printStackTrace();
                    errorInfo = editErrorInsertKPayment(wcSReference);
                    throw new Exception(errorInfo,e);
                };

                try {
                    //支払情報インデックステーブルの登録を行なう
                    wcPaymentIdx = new WcPaymentIdx(wcSReference.getInsured_person_id(),
                    	                        	wcSReference.getContract_id(),
                                                    wcPayment.getPayment_control_id());
                    wcKMapper.insertKPaymentIdx(wcPaymentIdx);
                }catch(Exception e) {
                    e.printStackTrace();
                    errorInfo = editErrorInsertKPaymentIdx(wcPaymentIdx);
                    throw new Exception(errorInfo,e);
                };

                return "解約処理が完了しました";

            }else {
                //取消処理に応じた更新処理を行なう

                try {
                    //契約情報テーブルの更新を行なう（契約終了日は契約発効日の前日を設定する）
                    LocalDate start_date  = LocalDate.parse(wcSReference.getContract_start_date(),
                                             DateTimeFormatter.ofPattern("uuuuMMdd"));
                    LocalDate cancel_date = start_date.minusDays(1);
                    wcSReference.setContract_end_date(cancel_date.format(DateTimeFormatter.ofPattern("uuuuMMdd")));
                    wcSReference.setContract_end_reason("K2");
                    wcKMapper.updateKContracts(wcSReference);
                }catch(Exception e) {
                    e.printStackTrace();
                    errorInfo = editErrorUpdateKContracts(wcSReference);
                    throw new Exception(errorInfo,e);
                };

                return "取消処理が完了しました";
            }

        }catch(Exception e) {
            throw new Exception(e);
        }
    }

    //異常終了時の出力メッセージ（契約情報情報テーブル更新エラー）
    private String editErrorUpdateKContracts(WcSReference wcSReference) {
        return   "契約情報テーブル更新エラー" +  ","
                + "被保険者番号　　：" + wcSReference.getInsured_person_id()      +  ","
                + "契約番号　　　　：" + wcSReference.getContract_id()            +  ","
                + "契約履歴番号　　：" + wcSReference.getContract_history_id()    +  ","
                + "契約開始日　　　：" + wcSReference.getContract_start_date()    +  ","
                + "契約終了日　　　：" + wcSReference.getContract_end_date()      +  ","
                + "契約終了事由　　：" + wcSReference.getContract_end_reason()    +  ","
                + "詳細はコンソール画面に出力されたログを参照願います";
    }

    //異常終了時の出力メッセージ（支払情報テーブル登録エラー）
    private String editErrorInsertKPayment(WcSReference wcSReference) {
        return    "支払情報テーブル更新エラー" +  ","
                + "被保険者番号　　：" + wcSReference.getInsured_person_id()      +  ","
                + "契約番号　　　　：" + wcSReference.getContract_id()            +  ","
                + "詳細はコンソール画面に出力されたログを参照願います";
    }

    //異常終了時の出力メッセージ（支払情報インデックステーブル登録エラー）
    private String editErrorInsertKPaymentIdx(WcPaymentIdx wcPaymentIdx) {
        return    "支払情報インデックステーブル更新エラー" +  ","
                + "被保険者番号　　：" + wcPaymentIdx.getInsured_person_id()      +  ","
                + "契約番号　　　　：" + wcPaymentIdx.getContract_id()            +  ","
                + "支払管理番号　　：" + wcPaymentIdx.getPayment_control_id()     +  ","
                + "詳細はコンソール画面に出力されたログを参照願います";
    }
}
