package com.insurance.app.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.ibatis.binding.BindingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.insurance.app.domain.TcKConfimation;
import com.insurance.app.domain.TcPayment;
import com.insurance.app.domain.TcPaymentIdx;
import com.insurance.app.domain.TcSContract;
import com.insurance.app.mapper.TcKMapper;

@Service
public class TcKService {

    @Autowired
    private TcKMapper tcKMapper;

    String errorInfo;

    @Transactional(rollbackFor = Exception.class)
    public String update(TcKConfimation tcKConfimation)
                                 throws BindingException, Exception {

        try {

            TcSContract tcSContract = tcKConfimation.getTcSContract();
            TcPayment tcPayment = new TcPayment(tcKConfimation.getCancellation_refund());
            TcPaymentIdx tcPaymentIdx = null;

            if(tcKConfimation.getSelect_cancel().equals("1")) {
                //解約処理に応じた更新処理を行なう

                try {
                    //契約情報テーブルの更新を行なう
                    tcSContract.setContract_end_date(tcKConfimation.getCancel_date().replace("/", ""));
                    tcSContract.setContract_end_reason("K0");
                    tcKMapper.updateKContracts(tcSContract);
                    tcSContract.setC_update_at(LocalDateTime.now());
                }catch(Exception e) {
                    e.printStackTrace();
                    errorInfo = editErrorUpdateKContracts(tcSContract);
                    throw new Exception(errorInfo,e);
                };

                //支払情報テーブルの登録を行なう
                try {
                    tcKMapper.insertKPayment(tcPayment);

                    //支払情報を照会情報エリアに設定する
                    tcSContract.setPayment_control_id(tcPayment.getPayment_control_id());
                    tcSContract.setPayment_amount(tcPayment.getPayment_amount());
                    tcSContract.setP_update_at(LocalDateTime.now());

                }catch(Exception e) {
                    e.printStackTrace();
                    errorInfo = editErrorInsertKPayment(tcSContract);
                    throw new Exception(errorInfo,e);
                };

                try {
                    //支払情報インデックステーブルの登録を行なう
                    tcPaymentIdx = new TcPaymentIdx(tcSContract.getInsured_person_id(),
                                                    tcSContract.getContract_id(),
                                                    tcPayment.getPayment_control_id());
                    tcKMapper.insertKPaymentIdx(tcPaymentIdx);
                }catch(Exception e) {
                    e.printStackTrace();
                    errorInfo = editErrorInsertKPaymentIdx(tcPaymentIdx);
                    throw new Exception(errorInfo,e);
                };

                return "解約処理が完了しました";

            }else {
                //取消処理に応じた更新処理を行なう

                try {
                    //契約情報テーブルの更新を行なう（契約終了日は契約発効日の前日を設定する）
                    LocalDate start_date  = LocalDate.parse(tcSContract.getContract_start_date(),
                                             DateTimeFormatter.ofPattern("uuuuMMdd"));
                    LocalDate cancel_date = start_date.minusDays(1);
                    tcSContract.setContract_end_date(cancel_date.format(DateTimeFormatter.ofPattern("uuuuMMdd")));
                    tcSContract.setContract_end_reason("K2");
                    tcKMapper.updateKContracts(tcSContract);
                }catch(Exception e) {
                    e.printStackTrace();
                    errorInfo = editErrorUpdateKContracts(tcSContract);
                    throw new Exception(errorInfo,e);
                };

                return "取消処理が完了しました";
            }

        }catch(Exception e) {
            throw new Exception(e);
        }
    }

    //異常終了時の出力メッセージ（契約情報情報テーブル更新エラー）
    private String editErrorUpdateKContracts(TcSContract tcSContract) {
        return   "契約情報テーブル更新エラー" +  ","
                + "被保険者番号　　：" + tcSContract.getInsured_person_id()      +  ","
                + "契約番号　　　　：" + tcSContract.getContract_id()            +  ","
                + "契約履歴番号　　：" + tcSContract.getContract_history_id()    +  ","
                + "契約開始日　　　：" + tcSContract.getContract_start_date()    +  ","
                + "契約終了日　　　：" + tcSContract.getContract_end_date()      +  ","
                + "契約終了事由　　：" + tcSContract.getContract_end_reason()    +  ","
                + "詳細はコンソール画面に出力されたログを参照願います";
    }

    //異常終了時の出力メッセージ（支払情報テーブル登録エラー）
    private String editErrorInsertKPayment(TcSContract tcSContract) {
        return    "支払情報テーブル更新エラー" +  ","
                + "被保険者番号　　：" + tcSContract.getInsured_person_id()      +  ","
                + "契約番号　　　　：" + tcSContract.getContract_id()            +  ","
                + "詳細はコンソール画面に出力されたログを参照願います";
    }

    //異常終了時の出力メッセージ（支払情報インデックステーブル登録エラー）
    private String editErrorInsertKPaymentIdx(TcPaymentIdx tcPaymentIdx) {
        return    "支払情報インデックステーブル更新エラー" +  ","
                + "被保険者番号　　：" + tcPaymentIdx.getInsured_person_id()      +  ","
                + "契約番号　　　　：" + tcPaymentIdx.getContract_id()            +  ","
                + "支払管理番号　　：" + tcPaymentIdx.getPayment_control_id()     +  ","
                + "詳細はコンソール画面に出力されたログを参照願います";
    }
}
