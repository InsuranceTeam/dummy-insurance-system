package com.insurance.app.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.ibatis.binding.BindingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.insurance.app.domain.TlContracts;
import com.insurance.app.domain.TlInput;
import com.insurance.app.domain.TlInsuredPersons;
import com.insurance.app.mapper.TlMapper;


@Service
public class TlEntryService {

  @Autowired
  TlMapper tlMapper;

  String errorInfo;

  @Transactional(rollbackFor = Exception.class)
  public void entryContracts(TlInsuredPersons tlInsuredPersons, TlContracts tlContracts,
      TlInput tlInput) throws BindingException, Exception {

    int insured_person_id = 0; // 被保険者番号
    int contract_id = 10000; // 契約番号

    convertInsuredPersons(tlInsuredPersons, tlInput);

    try {

      // 被保険者番号の編集を行なう
      try {
        // 名寄せにより被保険者番号を取得する
        insured_person_id = tlMapper.nayoseInsuredPersons(tlInput);
      } catch (BindingException e) {
        // 名寄せで被保険者情報テーブルが取得できない場合は処理なし
      } catch (Exception e) {
        e.printStackTrace();
        errorInfo = editErrorNayoseInsuredPersons(tlInsuredPersons);
        throw new Exception(errorInfo, e);
      }

      // 名寄せによる被保険者番号の取得結果に応じて、被保険者情報テーブルの登録・更新を行なう
      if (insured_person_id == 0) {
        try {
          // 新規の被保険者情報テーブルを登録する
          tlMapper.insertInsuredPersons(tlInsuredPersons);
        } catch (Exception e) {
          e.printStackTrace();
          errorInfo = editErrorInsertInsuredPersons(tlInsuredPersons);
          throw new Exception(errorInfo, e);
        }
      } else {
        try {
          // 既存の被保険者テーブルを更新する
          tlInsuredPersons.setInsured_person_id(insured_person_id);
          tlMapper.updateInsuredPersons(tlInsuredPersons);
        } catch (Exception e) {
          e.printStackTrace();
          errorInfo = editErrorUpdateInsuredPersons(tlInsuredPersons);
          throw new Exception(errorInfo, e);
        }
      }

      convertTlContracts(tlContracts, tlInput, tlInsuredPersons);


      // 被保険者情報テーブル・被保険者番号を契約者情報テーブルの同項目に設定する
      tlContracts.setInsured_person_id(tlInsuredPersons.getInsured_person_id());


      // 契約番号の編集を行なう
      try {
        // 契約番号（登録済みの契約情報と重複しない番号を設定する）
        contract_id = tlMapper.getContractId(tlInsuredPersons.getInsured_person_id()) + 1;
      } catch (BindingException e) {
        // 対象の被保険者配下に契約が存在しない場合は処理なし（契約番号は初期設定値の10000）
      } catch (Exception e) {
        e.printStackTrace();
        errorInfo = editErrorGetContractId(tlInsuredPersons.getInsured_person_id());
        throw new Exception(errorInfo, e);
      }
      tlContracts.setContract_id(contract_id);


      // 契約情報テーブルに新規データを登録する
      try {
        tlMapper.insertContracts(tlContracts);
      } catch (Exception e) {
        e.printStackTrace();
        errorInfo = editErrorInsertContracts(tlContracts);
        throw new Exception(errorInfo, e);
      }

    } catch (Exception e) {
      throw new Exception(e);
    }

  }

  private void convertInsuredPersons(TlInsuredPersons tlInsuredPersons, TlInput tlInput) {

    tlInsuredPersons.setInsured_person_birth_date(tlInput.getInsured_person_birth_year()
        + tlInput.getInsured_person_birth_month() + tlInput.getInsured_person_birth_day());
    tlInsuredPersons.setInsured_person_name_kana(tlInput.getInsured_person_name_kana());
    tlInsuredPersons.setInsured_person_name_kanji(tlInput.getInsured_person_name_kanji());
    tlInsuredPersons.setInsured_person_sex(tlInput.getInsured_person_sex());

  }

  private void convertTlContracts(TlContracts tlContracts, TlInput tlInput,
      TlInsuredPersons tlInsuredPersons) throws ParseException {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    sdf.setLenient(false);
    String strStart = tlInput.getStart_year() + tlInput.getStart_month() + tlInput.getStart_day();
    Date dateStart = sdf.parse(strStart);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(dateStart);
    calendar.add(Calendar.YEAR, Integer.parseInt(tlInput.getContract_period()));
    calendar.add(Calendar.DATE, -1);
    Date dateEnd = calendar.getTime();
    String strEnd = sdf.format(dateEnd);

    tlContracts.setInsured_person_id(tlInsuredPersons.getInsured_person_id());
    tlContracts.setContract_start_date(strStart);
    tlContracts.setContract_end_date("00000000");
    tlContracts.setContract_end_reason(" ");
    tlContracts.setContract_maturity_date(strEnd);
    tlContracts.setSecurity_type("TL");
    tlContracts.setContract_history_id(10000);
    tlContracts.setEntry_age(tlInput.getAge());
    tlContracts.setPayment_method(tlInput.getPayment_method());
    tlContracts.setInsurance_money(tlInput.getInsurance_money());
    tlContracts.setPremium(tlInput.getPrice_premium());
    tlContracts.setPremium_payment_term(Integer.parseInt(tlInput.getContract_period()));
    tlContracts.setContract_term(Integer.parseInt(tlInput.getContract_period()));
    tlContracts.setPayment_expiration_age(0);
  }

  // 異常終了時の出力メッセージ（被保険者情報テーブル取得エラー（名寄せによる取得））
  private String editErrorNayoseInsuredPersons(TlInsuredPersons tlInsuredPersons) {
    return "被保険者情報テーブル取得エラー（名寄せによる取得）" + "," + "被保険者氏名(ｶﾅ)："
        + tlInsuredPersons.getInsured_person_name_kana() + "," + "生年月日　　　　："
        + tlInsuredPersons.getInsured_person_birth_date() + "," + "性別　　　　　　："
        + tlInsuredPersons.getInsured_person_sex() + "," + "詳細はコンソール画面に出力されたログを参照願います";
  }

  // 異常終了時の出力メッセージ（被保険者情報テーブル登録エラー）
  private String editErrorInsertInsuredPersons(TlInsuredPersons tlInsuredPersons) {
    return "被保険者情報テーブル登録エラー" + "," + "被保険者氏名(ｶﾅ)：" + tlInsuredPersons.getInsured_person_name_kana()
        + "," + "生年月日　　　　：" + tlInsuredPersons.getInsured_person_birth_date() + "," + "性別　　　　　　："
        + tlInsuredPersons.getInsured_person_sex() + "," + "詳細はコンソール画面に出力されたログを参照願います";
  }

  // 異常終了時の出力メッセージ（被保険者情報テーブル更新エラー）
  private String editErrorUpdateInsuredPersons(TlInsuredPersons tlInsuredPersons) {
    return "被保険者情報テーブル更新エラー" + "," + "被保険者番号　　：" + tlInsuredPersons.getInsured_person_id() + ","
        + "被保険者氏名(ｶﾅ)：" + tlInsuredPersons.getInsured_person_name_kana() + "," + "生年月日　　　　："
        + tlInsuredPersons.getInsured_person_birth_date() + "," + "性別　　　　　　："
        + tlInsuredPersons.getInsured_person_sex() + "," + "詳細はコンソール画面に出力されたログを参照願います";
  }

  // 異常終了時の出力メッセージ（契約情報テーブル 契約番号取得エラー）
  private String editErrorGetContractId(int insured_person_id) {
    return "契約情報テーブル　契約番号取得エラー" + "," + "被保険者番号　　：" + insured_person_id + ","
        + "詳細はコンソール画面に出力されたログを参照願います";
  }

  // 異常終了時の出力メッセージ（契約情報テーブル登録エラー）
  private String editErrorInsertContracts(TlContracts tlContracts) {
    return "契約情報テーブル登録エラー" + "," + "被保険者番号　　：" + tlContracts.getInsured_person_id() + ","
        + "契約番号　　　　：" + tlContracts.getContract_id() + "," + "契約履歴番号　　："
        + tlContracts.getContract_history_id() + "," + "契約開始日　　　："
        + tlContracts.getContract_start_date() + "," + "契約終了日　　　："
        + tlContracts.getContract_end_date() + "," + "契約終了事由　　："
        + tlContracts.getContract_end_reason() + "," + "契約満期日　　　："
        + tlContracts.getContract_maturity_date() + "," + "保障種類　　　　："
        + tlContracts.getSecurity_type() + "," + "加入年齢　　　　：" + tlContracts.getEntry_age() + ","
        + "払込方法　　　　：" + tlContracts.getPayment_method() + "," + "保険金　　　　　："
        + tlContracts.getInsurance_money() + "," + "掛金　　　　　　：" + tlContracts.getPremium() + ","
        + "掛金払込期間　　：" + tlContracts.getPremium_payment_term() + "," + "契約期間　　　　："
        + tlContracts.getContract_term() + "," + "払込満了年齢　　："
        + tlContracts.getPayment_expiration_age() + "," + "詳細はコンソール画面に出力されたログを参照願います";
  }
}
