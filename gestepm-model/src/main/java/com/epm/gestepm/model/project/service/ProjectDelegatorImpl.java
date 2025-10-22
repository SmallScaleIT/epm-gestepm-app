package com.epm.gestepm.model.project.service;

import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.model.customer.service.mapper.MapCUToCustomerCreate;
import com.epm.gestepm.model.project.service.mapper.MapPRToProjectCreate;
import com.epm.gestepm.model.projectmaterial.service.mapper.MapPRMATToProjectMaterialCreate;
import com.epm.gestepm.modelapi.customer.dto.CustomerDto;
import com.epm.gestepm.modelapi.customer.dto.creator.CustomerCreateDto;
import com.epm.gestepm.modelapi.customer.dto.finder.CustomerByProjectIdFinderDto;
import com.epm.gestepm.modelapi.customer.service.CustomerService;
import com.epm.gestepm.modelapi.deprecated.project.dto.Project;
import com.epm.gestepm.modelapi.deprecated.project.service.ProjectOldService;
import com.epm.gestepm.modelapi.deprecated.family.dto.Family;
import com.epm.gestepm.modelapi.deprecated.family.service.FamilyService;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.creator.ProjectCreateDto;
import com.epm.gestepm.modelapi.project.dto.creator.ProjectLeaderCreateDto;
import com.epm.gestepm.modelapi.project.dto.creator.ProjectMemberCreateDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
import com.epm.gestepm.modelapi.project.service.ProjectDelegator;
import com.epm.gestepm.modelapi.project.service.ProjectLeaderService;
import com.epm.gestepm.modelapi.project.service.ProjectMemberService;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.projectmaterial.dto.ProjectMaterialDto;
import com.epm.gestepm.modelapi.projectmaterial.dto.creator.ProjectMaterialCreateDto;
import com.epm.gestepm.modelapi.projectmaterial.dto.filter.ProjectMaterialFilterDto;
import com.epm.gestepm.modelapi.projectmaterial.service.ProjectMaterialService;
import com.epm.gestepm.modelapi.user.dto.UserDto;
import com.epm.gestepm.modelapi.user.dto.filter.UserFilterDto;
import com.epm.gestepm.modelapi.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import java.util.List;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.DELEGATOR;
import static org.mapstruct.factory.Mappers.getMapper;

@Validated
@Service
@AllArgsConstructor
@EnableExecutionLog(layerMarker = DELEGATOR)
public class ProjectDelegatorImpl implements ProjectDelegator {

    private final CustomerService customerService;

    private final FamilyService familyService;

    private final ProjectService projectService;

    private final ProjectOldService projectOldService;

    private final ProjectMemberService projectMemberService;

    private final ProjectLeaderService projectLeaderService;

    private final ProjectMaterialService projectMaterialService;

    private final UserService userService;

    @Override
    @Transactional
    public ProjectDto duplicate(final ProjectByIdFinderDto finderDto) {

        final ProjectDto project = this.projectService.findOrNotFound(finderDto);

        final ProjectCreateDto createDto = getMapper(MapPRToProjectCreate.class).from(project);

        final ProjectDto newProject = this.projectService.create(createDto);

        this.duplicateCustomer(project, newProject);
        this.duplicateMembers(project, newProject);
        this.duplicateLeaders(project, newProject);
        this.duplicateMaterials(project, newProject);
        this.duplicateFamilies(project, newProject);

        return newProject;
    }

    private void duplicateCustomer(final ProjectDto project, final ProjectDto newProject) {

        final CustomerDto customer = this.customerService.findOrNotFound(new CustomerByProjectIdFinderDto(project.getId()));

        final CustomerCreateDto createDto = getMapper(MapCUToCustomerCreate.class).from(customer);
        createDto.setProjectId(newProject.getId());

        this.customerService.create(createDto);
    }

    public void duplicateMembers(final ProjectDto project, final ProjectDto newProject) {

        final UserFilterDto filterDto = new UserFilterDto();
        filterDto.setMemberProjectId(project.getId());

        final List<UserDto> members = this.userService.list(filterDto);
        members.forEach(member -> this.projectMemberService.create(new ProjectMemberCreateDto(newProject.getId(), member.getId())));
    }

    public void duplicateLeaders(final ProjectDto project, final ProjectDto newProject) {

        final UserFilterDto filterDto = new UserFilterDto();
        filterDto.setLeadingProjectId(project.getId());

        final List<UserDto> leaders = this.userService.list(filterDto);
        leaders.forEach(leader -> this.projectLeaderService.create(new ProjectLeaderCreateDto(newProject.getId(), leader.getId())));
    }

    public void duplicateMaterials(final ProjectDto project, final ProjectDto newProject) {

        final ProjectMaterialFilterDto filterDto = new ProjectMaterialFilterDto();
        filterDto.setProjectIds(List.of(project.getId()));

        final List<ProjectMaterialDto> materials = this.projectMaterialService.list(filterDto);
        materials.forEach(material -> {
            final ProjectMaterialCreateDto createDto = getMapper(MapPRMATToProjectMaterialCreate.class).from(material);
            createDto.setProjectId(newProject.getId());
            this.projectMaterialService.create(createDto);
        });
    }

    public void duplicateFamilies(final ProjectDto project, final ProjectDto newProject) {

        final Project newProjectDeprecated = this.projectOldService.getProjectById(newProject.getId().longValue());

        final List<Family> families = this.familyService.findByProjectId(project.getId().longValue());

        families.forEach(family -> {
            family.setProject(newProjectDeprecated);
            this.familyService.create(family);
        });
    }
}
