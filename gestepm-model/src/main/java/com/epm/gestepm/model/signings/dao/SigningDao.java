package com.epm.gestepm.model.signings.dao;

import com.epm.gestepm.model.signings.dao.entity.Signing;
import com.epm.gestepm.model.signings.dao.entity.filter.SigningFilter;

import javax.validation.Valid;
import java.util.List;

public interface SigningDao {
    List<@Valid Signing> list(@Valid SigningFilter filter);
}
