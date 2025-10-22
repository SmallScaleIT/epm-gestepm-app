package com.epm.gestepm.controller;

import com.epm.gestepm.model.deprecated.activitycenter.service.ActivityCenterServiceImpl;
import com.epm.gestepm.model.deprecated.country.service.CountryServiceOldImpl;
import com.epm.gestepm.model.deprecated.holiday.service.HolidayServiceImpl;
import com.epm.gestepm.model.deprecated.holiday.service.mapper.HolidayMapper;
import com.epm.gestepm.model.family.service.FamilyServiceImpl;
import com.epm.gestepm.model.subfamily.service.SubFamilyServiceImpl;
import com.epm.gestepm.model.subfamily.service.mapper.MapSFToSubFamilyDto;
import com.epm.gestepm.model.subrole.service.SubRoleServiceImpl;
import com.epm.gestepm.modelapi.common.utils.ModelUtil;
import com.epm.gestepm.modelapi.common.utils.Utiles;
import com.epm.gestepm.modelapi.common.utils.datatables.DataTableRequest;
import com.epm.gestepm.modelapi.common.utils.datatables.DataTableResults;
import com.epm.gestepm.modelapi.common.utils.datatables.PaginationCriteria;
import com.epm.gestepm.modelapi.deprecated.activitycenter.dto.ActivityCenter;
import com.epm.gestepm.modelapi.deprecated.country.dto.Country;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.deprecated.user.exception.InvalidUserSessionException;
import com.epm.gestepm.modelapi.deprecated.family.FamilyMapper;
import com.epm.gestepm.modelapi.deprecated.family.dto.Family;
import com.epm.gestepm.modelapi.deprecated.family.dto.FamilyDTO;
import com.epm.gestepm.modelapi.deprecated.family.dto.FamilyTableDTO;
import com.epm.gestepm.modelapi.holiday.dto.Holiday;
import com.epm.gestepm.modelapi.holiday.dto.HolidayDTO;
import com.epm.gestepm.modelapi.holiday.dto.HolidayTableDTO;
import com.epm.gestepm.modelapi.subfamily.dto.SubFamily;
import com.epm.gestepm.modelapi.subfamily.dto.SubFamilyDto;
import com.epm.gestepm.modelapi.subfamily.dto.SubFamilyOldDTO;
import com.epm.gestepm.modelapi.subrole.dto.SubRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.mapstruct.factory.Mappers.getMapper;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private static final Log log = LogFactory.getLog(AdminController.class);
	
	@Autowired
	private ActivityCenterServiceImpl activityCenterServiceOld;
	
	@Autowired
	private CountryServiceOldImpl countryService;
	
	@Autowired
	private FamilyServiceImpl familyService;
	
	@Autowired
	private HolidayServiceImpl holidayService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SubFamilyServiceImpl subFamilyService;
	
	@Autowired
	private SubRoleServiceImpl subRoleService;

	@Autowired
	private ObjectMapper objectMapper;

	@GetMapping("/holidays")
	public String holidays(Locale locale, Model model, HttpServletRequest request) {

		try {

			// Loading constants
			ModelUtil.loadConstants(locale, model, request);

			// Recover user
			User user = Utiles.getUsuario();

			// Log info
			log.info("El usuario " + user.getId() + " ha accedido a la vista de Admin Holidays");

			// Load all countries
			List<Country> countries = countryService.findAll();
			
			// Load all activity centers
			List<ActivityCenter> activityCenters = activityCenterServiceOld.findAll();
			
			// Order by name
			activityCenters.sort(Comparator.comparing(ActivityCenter::getName));
						
			// Load months
			Map<Integer, String> months = ModelUtil.loadMonths(messageSource, locale);

			// Carga del modelo
			model.addAttribute("tableActionButtons", ModelUtil.getTableModifyActionButtons());
			model.addAttribute("countries", countries);
			model.addAttribute("activityCenters", activityCenters);
			model.addAttribute("months", months);
						
			// Loading view
			return "admin-holidays";

		} catch (InvalidUserSessionException e) {
			log.error(e);
			return "redirect:/login";
		}
	}
	
	@ResponseBody
	@GetMapping("/holidays/dt")
	public DataTableResults<HolidayTableDTO> holidaysDatatable(HttpServletRequest request, Locale locale) {

		DataTableRequest<Holiday> dataTableInRQ = new DataTableRequest<>(request);
		PaginationCriteria pagination = dataTableInRQ.getPaginationRequest();

		List<HolidayTableDTO> holidays = holidayService.getHolidaysDataTables(pagination);

		Long totalRecords = holidayService.getHolidaysCount();

		DataTableResults<HolidayTableDTO> dataTableResult = new DataTableResults<>();
		dataTableResult.setDraw(dataTableInRQ.getDraw());
		dataTableResult.setData(holidays);
		dataTableResult.setRecordsTotal(String.valueOf(totalRecords));
		dataTableResult.setRecordsFiltered(Long.toString(totalRecords));

		if (!holidays.isEmpty() && !dataTableInRQ.getPaginationRequest().isFilterByEmpty()) {
			dataTableResult.setRecordsFiltered(Integer.toString(holidays.size()));
		}

		return dataTableResult;
	}
	
	@ResponseBody
	@PostMapping("/holidays/create")
	public ResponseEntity<String> createHolidays(@ModelAttribute HolidayDTO holidayDTO, Locale locale) {
		
		try {			
			
			// Recover user
			User user = Utiles.getUsuario();
						
			Holiday holiday = HolidayMapper.mapDTOToHoliday(holidayDTO, countryService, activityCenterServiceOld);
			
			holidayService.save(holiday);
			
			log.info("Vacación " + holiday.getId() + " creado con éxito por parte del usuario " + user.getId());
			
			// Return data
			return new ResponseEntity<>(messageSource.getMessage("holidays.admin.create.success", new Object[] { }, locale), HttpStatus.OK);
		
		} catch (Exception e) {
			return new ResponseEntity<>(messageSource.getMessage("holidays.admin.create.error", new Object[] { }, locale), HttpStatus.NOT_FOUND);
		}
	}
	
	@ResponseBody
	@PostMapping("/holidays/update/{id}")
	public ResponseEntity<String> updateHoliday(@PathVariable Long id, @ModelAttribute HolidayDTO holidayDTO, Locale locale) {

		try {
			
			// Recover user
			User user = Utiles.getUsuario();
						
			Holiday holiday = HolidayMapper.mapDTOToHoliday(holidayDTO, countryService, activityCenterServiceOld);
			holiday.setId(id);
			
			holidayService.save(holiday);
			
			log.info("Vacación " + id + " actualizado con éxito por parte del usuario " + user.getId());
			
			// Return data
			return new ResponseEntity<>(messageSource.getMessage("holidays.admin.update.success", new Object[] { }, locale), HttpStatus.OK);
		
		} catch (Exception e) {
			log.error(e);
			return new ResponseEntity<>(messageSource.getMessage("holidays.admin.update.error", new Object[] { }, locale), HttpStatus.NOT_FOUND);
		}
	}
	
	@ResponseBody
	@DeleteMapping("/holidays/delete/{id}")
	public ResponseEntity<String> deleteHoliday(@PathVariable Long id, Locale locale) {

		try {
			
			// Recover user
			User user = Utiles.getUsuario();
						
			holidayService.delete(id);
			
			log.info("Vacación " + id + " eliminado con éxito por parte del usuario " + user.getId());
			
			// Return data
			return new ResponseEntity<>(messageSource.getMessage("holidays.admin.delete.success", new Object[] { }, locale), HttpStatus.OK);
		
		} catch (Exception e) {
			return new ResponseEntity<>(messageSource.getMessage("holidays.admin.delete.error", new Object[] { }, locale), HttpStatus.NOT_FOUND);
		}
	}

	/*
	@GetMapping("/families")
	public String families(Locale locale, Model model, HttpServletRequest request) {

		try {

			// Loading constants
			ModelUtil.loadConstants(locale, model, request);

			// Recover user
			User user = Utiles.getUsuario();

			// Log info
			log.info("El usuario " + user.getId() + " ha accedido a la vista de Admin Familias");
						
			// Carga del modelo		
			model.addAttribute("tableActionButtons", ModelUtil.getTableModifyActionButtons());
			
			// Loading view
			return "admin-families";

		} catch (InvalidUserSessionException e) {
			log.error(e);
			return "redirect:/login";
		}
	}
	
	@ResponseBody
	@GetMapping("/families/dt")
	public DataTableResults<FamilyTableDTO> familiesDatatable(HttpServletRequest request, Locale locale) {

		DataTableRequest<Family> dataTableInRQ = new DataTableRequest<>(request);
		PaginationCriteria pagination = dataTableInRQ.getPaginationRequest();

		List<FamilyTableDTO> families = familyService.getFamilyTableDTOs(pagination);

		Long totalRecords = familyService.getFamiliesCount();

		DataTableResults<FamilyTableDTO> dataTableResult = new DataTableResults<>();
		dataTableResult.setDraw(dataTableInRQ.getDraw());
		dataTableResult.setData(families);
		dataTableResult.setRecordsTotal(String.valueOf(totalRecords));
		dataTableResult.setRecordsFiltered(Long.toString(totalRecords));

		if (!families.isEmpty() && !dataTableInRQ.getPaginationRequest().isFilterByEmpty()) {
			dataTableResult.setRecordsFiltered(Integer.toString(families.size()));
		}

		return dataTableResult;
	}
	
	@GetMapping("/families/create")
	public String createFamilies(Locale locale, Model model, HttpServletRequest request) {

		try {

			// Loading constants
			ModelUtil.loadConstants(locale, model, request);

			// Recover user
			User user = Utiles.getUsuario();
		
			// Get Sub Roles
			List<SubRole> subRoles = subRoleService.getAll();
						
			// Carga del modelo		
			model.addAttribute("subRoles", subRoles);
			model.addAttribute("tableActionButtons", ModelUtil.getTableModifyActionButtons());
			
			// Log info
			log.info("El usuario " + user.getId() + " ha accedido a la vista de Admin Crear Familia");
						
			// Loading view
			return "admin-families-create";

		} catch (InvalidUserSessionException e) {
			log.error(e);
			return "redirect:/login";
		}
	}
	
	@ResponseBody
	@PostMapping("/families/create")
	public ResponseEntity<String> createFamily(@RequestBody FamilyDTO familyDTO, Locale locale) {

		try {

			if (familyDTO.getSubfamilies() == null || familyDTO.getSubfamilies().isEmpty()) {
				return new ResponseEntity<>(messageSource.getMessage("families.admin.create.sub.empty", new Object[] {}, locale), HttpStatus.NOT_FOUND);
			}

			// Recover user
			User user = Utiles.getUsuario();

			// Create family
			Family family = FamilyMapper.mapDTOToFamily(familyDTO);
			
			family = familyService.create(family);

			log.info("Familia " + family.getId() + " creado con éxito por parte del usuario " + user.getId());
			
			// Return data
			return new ResponseEntity<>("", HttpStatus.OK);
			
		} catch (InvalidUserSessionException e) {
			log.error(e);
			return new ResponseEntity<>(messageSource.getMessage("families.admin.create.error", new Object[] {}, locale), HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/families/{familyId}/edit")
	public String getAdminFamily(@PathVariable Long familyId, Locale locale, Model model, HttpServletRequest request) {

		try {

			// Loading constants
			ModelUtil.loadConstants(locale, model, request);

			// Recover user
			User user = Utiles.getUsuario();
			
			Family family = familyService.getById(familyId);
			
			if (family == null) {
				log.error("El usuario " + user.getId() + " ha intentado acceder al detalle de la familia " + familyId + " que no existe.");
				return "redirect:/login";
			}

			// Log info
			log.info("El usuario " + user.getId() + " ha accedido a la vista de Admin Family Edit");
			
			List<SubFamilyOldDTO> subFamilies = subFamilyService.getByFamily(familyId);
			
			// Get Sub Roles
			List<SubRole> subRoles = subRoleService.getAll();

			// Carga del modelo
			model.addAttribute("tableActionButtons", ModelUtil.getTableModifyActionButtons());
			model.addAttribute("family", family);
			model.addAttribute("subRoles", subRoles);
			model.addAttribute("dataRows", this.objectMapper.writeValueAsString(subFamilies));

			// Loading view
			return "admin-families-edit";

		} catch (InvalidUserSessionException | JsonProcessingException e) {
			log.error(e);
			return "redirect:/login";
		}
	}
	
	@ResponseBody
	@PostMapping("/families/{familyId}/edit")
	public ResponseEntity<String> updateFamily(@PathVariable Long familyId, @RequestBody FamilyDTO familyDTO, Locale locale) {

		try {

			if (familyDTO.getSubfamilies() == null || familyDTO.getSubfamilies().isEmpty()) {
				return new ResponseEntity<>(messageSource.getMessage("families.admin.create.sub.empty", new Object[] {}, locale), HttpStatus.NOT_FOUND);
			}

			// Recover user
			User user = Utiles.getUsuario();
			
			Family currentFamily = familyService.getById(familyId);
			
			if (currentFamily == null) {
				log.error("Error al actualizar la familia: No existe la familia con id " + familyId);
				return new ResponseEntity<>(messageSource.getMessage("families.admin.update.error", new Object[] {}, locale), HttpStatus.NOT_FOUND);
			}

			// Update container
			Family family = FamilyMapper.mapDTOToFamily(familyDTO);
			family.setId(familyId);
			family.setProject(currentFamily.getProject()); // important many to many
			
			family = familyService.update(family, currentFamily);

			log.info("Familia " + family.getId() + " actualizada con éxito por parte del usuario " + user.getId());
			
			// Return data
			return new ResponseEntity<>("", HttpStatus.OK);
			
		} catch (InvalidUserSessionException e) {
			log.error(e);
			return new ResponseEntity<>(messageSource.getMessage("families.admin.update.error", new Object[] {}, locale),
					HttpStatus.NOT_FOUND);
		}
	}
	
	@ResponseBody
	@DeleteMapping("/families/delete/{id}")
	public ResponseEntity<String> deleteFamily(@PathVariable Long id, Locale locale) {

		try {
			
			// Recover user
			User user = Utiles.getUsuario();
						
			familyService.delete(id);
			
			log.info("Familia " + id + " eliminada con éxito por parte del usuario " + user.getId());
			
			// Return data
			return new ResponseEntity<>(messageSource.getMessage("families.admin.delete.success", new Object[] { }, locale), HttpStatus.OK);
		
		} catch (Exception e) {
			return new ResponseEntity<>(messageSource.getMessage("families.admin.delete.error", new Object[] { }, locale), HttpStatus.NOT_FOUND);
		}
	}
	
	@ResponseBody
	@GetMapping("/families/{familyId}/subfamilies")
	public ResponseEntity<List<SubFamilyDto>> loadSubFamiliesFromFamily(@PathVariable Long familyId, Locale locale) {

		final List<SubFamily> subFamilies = new ArrayList<>();
		final Family family = familyService.getById(familyId);
		
		if (family != null) {

			if (family.getCommon() == 3) {
				subFamilies.addAll(family.getFamily().getSubFamilies());
			} else {
				subFamilies.addAll(family.getSubFamilies());
			}

			if ("es".equals(locale.getLanguage())) {
				subFamilies.sort(Comparator.comparing(SubFamily::getNameES));
			} else {
				subFamilies.sort(Comparator.comparing(SubFamily::getNameFR));
			}
		}

		final List<SubFamilyDto> response = getMapper(MapSFToSubFamilyDto.class).from(subFamilies, locale);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	*/
}
