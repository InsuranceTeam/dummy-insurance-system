package com.insurance.app.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.insurance.app.domain.TlSPayment;
import com.insurance.app.mapper.TlSMapper;

@Service
public class TlSRefundService {

	@Autowired
	TlSMapper tlSMapper;

	@Transactional(rollbackFor = Exception.class)
	public void execute(String insured_person_id, String contract_id, String contract_history_id,String contract_end_reason, String refund_money,
			String contract_end_date, String contract_start_date) throws ParseException {

		if (contract_end_reason.equals("K2")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sdf.setLenient(false);
			Date dateStart = sdf.parse(contract_start_date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dateStart);
			calendar.add(Calendar.DATE, -1);
			contract_end_date = sdf.format(calendar.getTime());

			tlSMapper.updateRefundContract(contract_end_date, contract_end_reason, insured_person_id, contract_id,contract_history_id);
		} else {
			TlSPayment tlSPayment = new TlSPayment();
			tlSPayment.setPayment_amount(Integer.parseInt(refund_money));
			tlSPayment.setInsured_person_id(Integer.parseInt(insured_person_id));
			tlSPayment.setContract_id(Integer.parseInt(contract_id));

			tlSMapper.updateRefundContract(contract_end_date, contract_end_reason, insured_person_id, contract_id,contract_history_id);

			tlSMapper.insertPayment(tlSPayment);

			tlSMapper.insertPaymentIdx(tlSPayment);
		}

	}

}
