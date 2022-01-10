package com.insurance.app.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.ibatis.binding.BindingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.insurance.app.domain.CmTlActuarial;
import com.insurance.app.domain.TlContracts;
import com.insurance.app.domain.TlInput;
import com.insurance.app.domain.TlInsuredPersons;
import com.insurance.app.service.CmActuarialService;
import com.insurance.app.service.TlInputService;
import com.insurance.app.service.TlEntryService;

@Controller
@RequestMapping("/tl")
public class TlInputController {

  /* 定期生命 */
  @Autowired
  TlEntryService tlEntryService;

  @Autowired
  TlInputService tlInputService;

  /* 掛金算出 */
  @Autowired
  private CmActuarialService cmActuarialService;


  /* 入力画面初期表示 */
  @GetMapping("/input")
  public String init(Model model, TlInput tlInput) {
    model.addAttribute("tlInput", tlInput);
    return "tl_input";
  }

  /* 入力チェック処理 */
  @PostMapping("/input")
  public String check(RedirectAttributes redirectAttributes, TlInput tlInput, Model model)
      throws BindingException, Exception {

    /* チェック処理呼び出し */
    boolean result = tlInputService.execute(tlInput);

    /* 掛金算出 */
    if (result) {

      CmTlActuarial cmTlActuarial = convertCmTlActuarial(tlInput);
      int premium = cmActuarialService.findPremium(cmTlActuarial);
      tlInput.setPrice_premium(premium);

      redirectAttributes.addFlashAttribute("tlInput", tlInput);
      return "redirect:/tl/confirmation";

      /* 入力エラー */
    } else {

      model.addAttribute("tlInput", tlInput);
      return "tl_input";
    }

  }

  /* 確認画面初期表示 */
  @GetMapping("/confirmation")
  public String confirm(Model model, TlInput tlInput) {

    model.addAttribute("tlInput", tlInput);

    return "tl_confirmation";
  }

  /* キャンセル処理 */
  @PostMapping(path = "/completion", params = "action=cancel")
  public String cancel(Model model, RedirectAttributes redirectAttributes, TlInput tlInput)
      throws BindingException, Exception {
    redirectAttributes.addFlashAttribute("tlInput", tlInput);
    return "redirect:/tl/input";

  }

  /* 完了画面初期表示・登録処理 */
  @PostMapping(path = "/completion", params = "action=completion")
  public String complete(Model model, TlInput tlInput) throws BindingException, Exception {

    TlInsuredPersons tlInsuredPersons = new TlInsuredPersons();
    TlContracts tlContracts = new TlContracts();

    tlEntryService.entryContracts(tlInsuredPersons, tlContracts, tlInput);

    model.addAttribute("tlInsuredPersons", tlInsuredPersons);
    model.addAttribute("tlContracts", tlContracts);

    return "tl_completion";
  }

  /* 掛金取得パラメータ生成 */
  private CmTlActuarial convertCmTlActuarial(TlInput tlInput) {

    CmTlActuarial cmTlActuarial = new CmTlActuarial();
    String strStart = tlInput.getStart_year() + tlInput.getStart_month() + tlInput.getStart_day();

    cmTlActuarial.setEntry_age(tlInput.getAge());
    cmTlActuarial.setInsurance_money(tlInput.getInsurance_money());
    cmTlActuarial.setInsured_person_sex(tlInput.getInsured_person_sex());
    cmTlActuarial.setPayment_method(tlInput.getPayment_method());
    cmTlActuarial.setPremium_payment_term(Integer.parseInt(tlInput.getContract_period()));
    cmTlActuarial
        .setReference_date(LocalDate.parse(strStart, DateTimeFormatter.ofPattern("yyyyMMdd")));
    cmTlActuarial.setSecurity_type("TL");

    return cmTlActuarial;

  }

}
