package com.insurance.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.insurance.app.domain.TlSContracts;
import com.insurance.app.mapper.TlSMapper;

@Service
public class TlSContractsService {

	@Autowired
	TlSMapper tlSMapper;

	@Transactional(rollbackFor = Exception.class)
	public TlSContracts execute( String insured_person_id,String contract_id) {

		TlSContracts tlContracts =  tlSMapper.getTlSContract(insured_person_id,contract_id);
		
		return tlContracts;

	}

}
