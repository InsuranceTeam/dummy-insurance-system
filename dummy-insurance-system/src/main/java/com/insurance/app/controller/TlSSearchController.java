package com.insurance.app.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.insurance.app.domain.CmKTlActuarial;
import com.insurance.app.domain.TlSContracts;
import com.insurance.app.domain.TlSList;
import com.insurance.app.domain.TlSSearch;
import com.insurance.app.service.CmKActuarialService;
import com.insurance.app.service.TlSContractsService;
import com.insurance.app.service.TlSRefundService;
import com.insurance.app.service.TlSSearchService;

@Controller
@RequestMapping("/tl/s")
public class TlSSearchController {

	@Autowired
	TlSSearchService tlSearchService;

	@Autowired
	TlSContractsService TlSContractsService;

	@Autowired
	CmKActuarialService cmKActuarialService;

	@Autowired
	TlSRefundService tlSRefundService;

	/* 入力画面初期表示 */
	@GetMapping("/search")
	public String search(Model model, TlSSearch tlSSearch) {
		model.addAttribute("tlSSearch", tlSSearch);
		return "tl_s_search";
	}

	/* 一覧画面初期表示 */
	@PostMapping("/list")
	public String list(RedirectAttributes redirectAttributes, Model model, TlSSearch tlSSearch) {
		tlSSearch.setInsured_person_id_error("");
		//定期生命契約を取得する
		List<TlSList> tlSLists = tlSearchService.execute(tlSSearch.getInsured_person_id());

		if (tlSLists.size() > 0) {
			model.addAttribute("tlSList", tlSLists);
			return "tl_s_list";
		} else {
			redirectAttributes.addFlashAttribute("tlSSearch", tlSSearch);
			tlSSearch.setInsured_person_id_error("契約が見つかりませんでした。");
			return "redirect:/tl/s/search";
		}

	}

	/* 一覧画面初期表示 */
	@RequestMapping("/contract")
	public String contract(Model model, @ModelAttribute("insured_person_id")String insured_person_id, @ModelAttribute("contract_id")String contract_id) {
		
		TlSContracts tlSContracts = TlSContractsService.execute(insured_person_id, contract_id);
		model.addAttribute("tlSContracts", tlSContracts);

		return "tl_s_contracts";
	}

	/* 解約/取消実施 */
	@PostMapping("/refund_execute")
	public String refund_execute(RedirectAttributes redirectAttributes, Model model, String insured_person_id,
			String contract_id, String contract_history_id ,String contract_end_reason, String refund_money, String contract_end_date,
			String contract_start_date) throws ParseException {

		redirectAttributes.addFlashAttribute("insured_person_id", insured_person_id);
		redirectAttributes.addFlashAttribute("contract_id", contract_id);
		
		model.addAttribute("insured_person_id", insured_person_id);
		model.addAttribute("contract_id", contract_id);

		tlSRefundService.execute(insured_person_id, contract_id, contract_history_id, contract_end_reason, refund_money, contract_end_date,
				contract_start_date);

		return "redirect:/tl/s/contract";
	}

	/* 解約返戻金取得処理 */
	@PostMapping("/refund")
	@ResponseBody
	public int refund(String insured_person_id, String insured_person_birth_date, String insured_person_sex,
			String entry_age, String contract_id, String contract_history_id, String contract_start_date,
			String contract_maturity_date, String contract_end_date, String premium_payment_term, String payment_method,
			String insurance_money, String premium, String cancel_date) throws Exception {

		CmKTlActuarial cmKTlActuarial = new CmKTlActuarial();

		cmKTlActuarial.setInsured_person_birth_date(
				LocalDate.parse(insured_person_birth_date, DateTimeFormatter.ofPattern("yyyyMMdd")));
		cmKTlActuarial.setInsured_person_sex(insured_person_sex);
		cmKTlActuarial
				.setContract_start_date(LocalDate.parse(contract_start_date, DateTimeFormatter.ofPattern("yyyyMMdd")));
		cmKTlActuarial
				.setContract_maturity_date(
						LocalDate.parse(contract_maturity_date, DateTimeFormatter.ofPattern("yyyyMMdd")));
		cmKTlActuarial
				.setEntry_age(Integer.parseInt(entry_age));
		cmKTlActuarial.setSecurity_type("TL");
		cmKTlActuarial.setPayment_method(payment_method);
		cmKTlActuarial.setInsurance_money(Integer.parseInt(insurance_money));
		cmKTlActuarial.setPremium(Integer.parseInt(premium));
		cmKTlActuarial.setPremium_payment_term(Integer.parseInt(premium_payment_term));
		cmKTlActuarial.setPayment_expiration_age(0);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sdf.setLenient(false);
		try {
			sdf.parse(cancel_date);
				        
	        LocalDate cancel =
	            LocalDate.parse(cancel_date, DateTimeFormatter.ofPattern("yyyyMMdd"));
	        LocalDate start =
		            LocalDate.parse(contract_start_date, DateTimeFormatter.ofPattern("yyyyMMdd"));
	        LocalDate end =
		            LocalDate.parse(contract_maturity_date,DateTimeFormatter.ofPattern("yyyyMMdd"));
			
			if (cancel.compareTo(start) > -1 && cancel.compareTo(end) < 1) {
				cmKTlActuarial.setCancel_date(cancel);
				return cmKActuarialService.cancellationRefundCalculation(cmKTlActuarial);
			}
		} catch (ParseException e) {
			return -1;
		} 
		return -2;
	}

}
