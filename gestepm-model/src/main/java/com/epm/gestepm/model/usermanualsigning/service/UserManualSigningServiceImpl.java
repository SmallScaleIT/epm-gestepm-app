package com.epm.gestepm.model.usermanualsigning.service;

import com.epm.gestepm.model.usermanualsigning.dao.UserManualSigningRepository;
import com.epm.gestepm.modelapi.usermanualsigning.dto.UserManualSigning;
import com.epm.gestepm.modelapi.usermanualsigning.dto.UserManualSigningTableDTO;
import com.epm.gestepm.modelapi.usermanualsigning.service.UserManualSigningService;
import com.epm.gestepm.modelapi.common.utils.datatables.PaginationCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserManualSigningServiceImpl implements UserManualSigningService {

    @Autowired
    private UserManualSigningRepository userManualSigningRepository;

    @Override
    public UserManualSigning getById(Long id) {
        return this.userManualSigningRepository.findById(id).orElse(null);
    }

    @Override
    public UserManualSigning save(UserManualSigning userManualSigning) {
        return this.userManualSigningRepository.save(userManualSigning);
    }

    @Override
    public void delete(Long id) {
        this.userManualSigningRepository.deleteById(id);
    }

    @Override
    public List<UserManualSigningTableDTO> getUserManualSigningDTOsByUserId(Long userId, PaginationCriteria pagination) {
        return this.userManualSigningRepository.findUserManualSigningDTOsByUserId(userId, pagination);
    }

    @Override
    public Long getUserManualSigningCountByUser(Long userId) {
        return this.userManualSigningRepository.findUserManualSigningCountByUserId(userId);
    }

    @Override
    public List<UserManualSigning> getWeekManualSigningsByUserId(Date startDate, Date endDate, Long userId) {
        return this.userManualSigningRepository.findWeekManualSigningsByUserId(startDate, endDate, userId);
    }
}
