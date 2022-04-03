package com.insurance.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.insurance.app.domain.TlSList;
import com.insurance.app.mapper.TlSMapper;

@Service
public class TlSSearchService {

	@Autowired
	TlSMapper tlSMapper;

	@Transactional(rollbackFor = Exception.class)
	public List<TlSList> execute(String insured_person_id) {

		return tlSMapper.searchTlSList(insured_person_id);

	}

}
