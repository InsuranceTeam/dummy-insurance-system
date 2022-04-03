package com.insurance.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.insurance.app.domain.TcSContract;
import com.insurance.app.domain.TcSList;
import com.insurance.app.mapper.TcSMapper;

@Service
public class TcSService {

    @Autowired
    private TcSMapper tcSMapper;

    String errorInfo;

    //一覧へ表示する定期医療契約の取得を行なう
    @Transactional
    public List<TcSList> searchTcSList(int insured_person_id) throws Exception {
        try {
            return tcSMapper.searchTcSList(insured_person_id);
        }catch(Exception e) {
            e.printStackTrace();
            errorInfo = editErrorTcSList(insured_person_id);
            throw new Exception(errorInfo,e);
        }
    }

    //契約照会へ表示する定期医療契約の取得を行なう
    @Transactional
    public List<TcSContract> getTcSContract(int insured_person_id, int contract_id) throws Exception {
        try {
            return tcSMapper.getTcSContract(insured_person_id, contract_id);
        }catch(Exception e) {
            e.printStackTrace();
            errorInfo = editErrorTcSContract(insured_person_id, contract_id);
            throw new Exception(errorInfo,e);
        }
    }

    //異常終了時の出力メッセージ（一覧表示用の定期医療契約取得エラー）
    private String editErrorTcSList(int insured_person_id) {
        return    "一覧表示用の定期医療契約取得エラー" +  ","
                + "被保険者番号　　：" + insured_person_id         +  ","
                + "詳細はコンソール画面に出力されたログを参照願います";
    }

    //異常終了時の出力メッセージ（契約照会用の定期医療契約取得エラー）
    private String editErrorTcSContract(int insured_person_id, int contract_id) {
        return    "契約照会用の定期医療契約取得エラー" +  ","
                + "被保険者番号　　：" + insured_person_id         +  ","
                + "契約番号　　　　：" + contract_id               +  ","
                + "詳細はコンソール画面に出力されたログを参照願います";
    }
}

