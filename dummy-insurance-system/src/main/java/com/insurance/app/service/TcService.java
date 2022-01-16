package com.insurance.app.service;

import org.apache.ibatis.binding.BindingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.insurance.app.domain.TcContracts;
import com.insurance.app.domain.TcInsuredPersons;
import com.insurance.app.mapper.TcMapper;

@Service
public class TcService {

    @Autowired
    private TcMapper tcMapper;

    String errorInfo;

    @Transactional(rollbackFor = Exception.class)
    public String entryContracts(TcInsuredPersons tcInsuredPersons, TcContracts tcContracts)
                                 throws BindingException, Exception {

        try {
            int    insured_person_id = 0;     //被保険者番号
            int    contract_id       = 10000; //契約番号
            String iuComment;                 //被保険者情報テーブルの登録／更新情報を設定

            //被保険者番号の編集を行なう
            try {
                //名寄せにより被保険者番号を取得する
                insured_person_id = tcMapper.nayoseInsuredPersons(tcInsuredPersons.getInsured_person_name_kana(),
                                                                  tcInsuredPersons.getInsured_person_birth_date(),
                                                                  tcInsuredPersons.getInsured_person_sex());
            }catch(BindingException e) {
                //名寄せで被保険者情報テーブルが取得できない場合は処理なし
            }catch(Exception e) {
                e.printStackTrace();
                errorInfo = editErrorNayoseInsuredPersons(tcInsuredPersons);
                throw new Exception(errorInfo,e);
            }


            //名寄せによる被保険者番号の取得結果に応じて、被保険者情報テーブルの登録・更新を行なう
            if(insured_person_id == 0) {
                try {
                  //新規の被保険者情報テーブルを登録する
                    tcMapper.insertInsuredPersons(tcInsuredPersons);
                    iuComment = "※登録";
                }catch(Exception e) {
                    e.printStackTrace();
                    errorInfo = editErrorInsertInsuredPersons(tcInsuredPersons);
                    throw new Exception(errorInfo,e);
                }
            }else {
                try {
                    //既存の被保険者テーブルを更新する
                    tcInsuredPersons.setInsured_person_id(insured_person_id);
                    tcMapper.updateInsuredPersons(tcInsuredPersons);
                    iuComment = "※更新";
                }catch(Exception e) {
                    e.printStackTrace();
                    errorInfo = editErrorUpdateInsuredPersons(tcInsuredPersons);
                    throw new Exception(errorInfo,e);
                }
            }


            //被保険者情報テーブル・被保険者番号を契約者情報テーブルの同項目に設定する
            tcContracts.setInsured_person_id(tcInsuredPersons.getInsured_person_id());


            //契約番号の編集を行なう
            try {
                //契約番号（登録済みの契約情報と重複しない番号を設定する）
                contract_id = tcMapper.getContractId(tcInsuredPersons.getInsured_person_id()) + 1;
            }catch(BindingException e) {
                //対象の被保険者配下に契約が存在しない場合は処理なし（契約番号は初期設定値の10000）
            }catch(Exception e) {
                e.printStackTrace();
                errorInfo = editErrorGetContractId(tcInsuredPersons.getInsured_person_id());
                throw new Exception(errorInfo,e);
            }
            tcContracts.setContract_id(contract_id);


            //契約情報テーブルに新規データを登録する
            try {
                tcMapper.insertContracts(tcContracts);
            }catch(Exception e) {
                e.printStackTrace();
                errorInfo = editErrorInsertContracts(tcContracts);
                throw new Exception(errorInfo,e);
            }

            return iuComment;

        }catch(Exception e) {
            throw new Exception(e);
        }

    }

    //異常終了時の出力メッセージ（被保険者情報テーブル取得エラー（名寄せによる取得））
    private String editErrorNayoseInsuredPersons(TcInsuredPersons tcInsuredPersons) {
        return  "被保険者情報テーブル取得エラー（名寄せによる取得）" +  ","
               + "被保険者氏名(ｶﾅ)：" + tcInsuredPersons.getInsured_person_name_kana()   +  ","
               + "生年月日　　　　：" + tcInsuredPersons.getInsured_person_birth_date()  +  ","
               + "性別　　　　　　：" + tcInsuredPersons.getInsured_person_sex()         +  ","
               + "詳細はコンソール画面に出力されたログを参照願います";
    }

    //異常終了時の出力メッセージ（被保険者情報テーブル登録エラー）
    private String editErrorInsertInsuredPersons(TcInsuredPersons tcInsuredPersons) {
        return    "被保険者情報テーブル登録エラー" +  ","
                + "被保険者氏名(ｶﾅ)：" + tcInsuredPersons.getInsured_person_name_kana()  +  ","
                + "生年月日　　　　：" + tcInsuredPersons.getInsured_person_birth_date() +  ","
                + "性別　　　　　　：" + tcInsuredPersons.getInsured_person_sex()        +  ","
                + "詳細はコンソール画面に出力されたログを参照願います";
    }

    //異常終了時の出力メッセージ（被保険者情報テーブル更新エラー）
    private String editErrorUpdateInsuredPersons(TcInsuredPersons tcInsuredPersons) {
        return    "被保険者情報テーブル更新エラー" +  ","
                + "被保険者番号　　：" + tcInsuredPersons.getInsured_person_id()         +  ","
                + "被保険者氏名(ｶﾅ)：" + tcInsuredPersons.getInsured_person_name_kana()  +  ","
                + "生年月日　　　　：" + tcInsuredPersons.getInsured_person_birth_date() +  ","
                + "性別　　　　　　：" + tcInsuredPersons.getInsured_person_sex()        +  ","
                + "詳細はコンソール画面に出力されたログを参照願います";
    }

    //異常終了時の出力メッセージ（契約情報テーブル　契約番号取得エラー）
    private String editErrorGetContractId(int insured_person_id) {
        return    "契約情報テーブル　契約番号取得エラー"    +  ","
                + "被保険者番号　　：" + insured_person_id  +  ","
                + "詳細はコンソール画面に出力されたログを参照願います";
    }

    //異常終了時の出力メッセージ（契約情報テーブル登録エラー）
    private String editErrorInsertContracts(TcContracts tcContracts) {
        return   "契約情報テーブル登録エラー" +  ","
               + "被保険者番号　　：" + tcContracts.getInsured_person_id()      +  ","
               + "契約番号　　　　：" + tcContracts.getContract_id()            +  ","
               + "契約履歴番号　　：" + tcContracts.getContract_history_id()    +  ","
               + "契約開始日　　　：" + tcContracts.getContract_start_date()    +  ","
               + "契約終了日　　　：" + tcContracts.getContract_end_date()      +  ","
               + "契約終了事由　　：" + tcContracts.getContract_end_reason()    +  ","
               + "契約満期日　　　：" + tcContracts.getContract_maturity_date() +  ","
               + "保障種類　　　　：" + tcContracts.getSecurity_type()          +  ","
               + "加入年齢　　　　：" + tcContracts.getEntry_age()              +  ","
               + "払込方法　　　　：" + tcContracts.getPayment_method()         +  ","
               + "保険金　　　　　：" + tcContracts.getInsurance_money()        +  ","
               + "掛金　　　　　　：" + tcContracts.getPremium()                +  ","
               + "掛金払込期間　　：" + tcContracts.getPremium_payment_term()   +  ","
               + "契約期間　　　　：" + tcContracts.getContract_term()          +  ","
               + "払込満了年齢　　：" + tcContracts.getPayment_expiration_age() +  ","
               + "詳細はコンソール画面に出力されたログを参照願います";
    }
}
