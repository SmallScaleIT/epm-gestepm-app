package com.epm.gestepm.rest.signings.personal.request;

import com.epm.gestepm.lib.controller.RestRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PersonalSigningListRestRequest extends RestRequest {

    private List<Integer> ids;

    private List<Integer> userIds;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
