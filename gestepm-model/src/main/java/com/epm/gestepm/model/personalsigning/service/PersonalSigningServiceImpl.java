package com.epm.gestepm.model.personalsigning.service;

import com.epm.gestepm.model.personalsigning.dao.PersonalSigningRepository;
import com.epm.gestepm.modelapi.personalsigning.dto.PersonalSigning;
import com.epm.gestepm.modelapi.personalsigning.service.PersonalSigningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonalSigningServiceImpl implements PersonalSigningService {

	private final PersonalSigningRepository personalSigningRepository;

	@Override
	public PersonalSigning save(PersonalSigning personalSigning) {
		return personalSigningRepository.save(personalSigning);
	}
	
	@Override
	public PersonalSigning getById(Long id) {
		return personalSigningRepository.findById(id).orElse(null);
	}

}
