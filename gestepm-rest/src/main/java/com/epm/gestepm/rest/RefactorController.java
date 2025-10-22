package com.epm.gestepm.rest;

import com.epm.gestepm.modelapi.common.utils.Utiles;
import com.epm.gestepm.modelapi.common.utils.datatables.DataTableRequest;
import com.epm.gestepm.modelapi.common.utils.datatables.DataTableResults;
import com.epm.gestepm.modelapi.common.utils.datatables.PaginationCriteria;
import com.epm.gestepm.modelapi.deprecated.project.dto.Project;
import com.epm.gestepm.modelapi.deprecated.project.dto.ProjectFamilyDTO;
import com.epm.gestepm.modelapi.deprecated.project.service.ProjectOldService;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.deprecated.family.FamilyMapper;
import com.epm.gestepm.modelapi.deprecated.family.dto.Family;
import com.epm.gestepm.modelapi.deprecated.family.dto.FamilyTableDTO;
import com.epm.gestepm.modelapi.deprecated.family.service.FamilyService;
import com.epm.gestepm.modelapi.userholiday.dto.UserHoliday;
import com.epm.gestepm.modelapi.userholiday.dto.UserHolidayDTO;
import com.epm.gestepm.modelapi.userholiday.service.UserHolidaysService;
import lombok.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Data
@RestController
public class RefactorController {

    private static final Log log = LogFactory.getLog(RefactorController.class);

    private final FamilyService familyService;

    private final MessageSource messageSource;

    private final ProjectOldService projectOldService;

    private final UserHolidaysService userHolidaysService;

    @ResponseBody
    @GetMapping("/users/{id}/holidays/dt")
    public DataTableResults<UserHolidayDTO> userHolidaysDatatable(@PathVariable Long id, HttpServletRequest request, Locale locale) {

        try {

            DataTableRequest<UserHoliday> dataTableInRQ = new DataTableRequest<>(request);
            PaginationCriteria pagination = dataTableInRQ.getPaginationRequest();

            List<UserHolidayDTO> userHolidays = userHolidaysService.getUserHolidaysDTOsByUserId(id, pagination);

            Long totalRecords = userHolidaysService.getUserHolidaysCountByUser(id);

            DataTableResults<UserHolidayDTO> dataTableResult = new DataTableResults<>();
            dataTableResult.setDraw(dataTableInRQ.getDraw());
            dataTableResult.setData(userHolidays);
            dataTableResult.setRecordsTotal(String.valueOf(totalRecords));
            dataTableResult.setRecordsFiltered(Long.toString(totalRecords));

            if (userHolidays != null && !userHolidays.isEmpty() && !dataTableInRQ.getPaginationRequest().isFilterByEmpty()) {
                dataTableResult.setRecordsFiltered(Integer.toString(userHolidays.size()));
            }

            return dataTableResult;

        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    @GetMapping("/v1/projects/{id}/export")
    public HttpEntity<ByteArrayResource> generateExcel(@PathVariable Long id, @RequestParam(required = false) Integer year, Locale locale) throws IOException {

        XSSFWorkbook workbook = null;

        try {

            // Recover user
            User user = Utiles.getUsuario();

            // Log info
            log.info("El usuario " + user.getId() + " está generando el excel del proyecto " + id);

            if (year == null) {
                year = Calendar.getInstance().get(Calendar.YEAR);
            }

            // Recover project
            Project project = projectOldService.getProjectById(id);

            ByteArrayOutputStream archivo = new ByteArrayOutputStream();

            workbook = projectOldService.generateProjectExcel(id, user.getId(), project, year, locale);
            workbook.write(archivo);

            byte[] excelContent = archivo.toByteArray();

            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "force-download"));
            header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Project_" + id + ".xlsx");

            return new HttpEntity<>(new ByteArrayResource(excelContent), header);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }

        return null;
    }

    @ResponseBody
    @GetMapping("/v1/projects/{id}/families/dt")
    public DataTableResults<FamilyTableDTO> projectFamiliesDataTable(@PathVariable Long id, HttpServletRequest request, Locale locale) {

        DataTableRequest<Project> dataTableInRQ = new DataTableRequest<>(request);
        PaginationCriteria pagination = dataTableInRQ.getPaginationRequest();

        List<FamilyTableDTO> families = familyService.getFamiliesDataTablesByProjectId(id, pagination);

        Long totalRecords = familyService.getFamiliesCountByProjectId(id);

        DataTableResults<FamilyTableDTO> dataTableResult = new DataTableResults<>();
        dataTableResult.setDraw(dataTableInRQ.getDraw());
        dataTableResult.setData(families);
        dataTableResult.setRecordsTotal(String.valueOf(totalRecords));
        dataTableResult.setRecordsFiltered(Long.toString(totalRecords));

        if (families != null && !families.isEmpty() && !dataTableInRQ.getPaginationRequest().isFilterByEmpty()) {
            dataTableResult.setRecordsFiltered(Integer.toString(families.size()));
        }

        return dataTableResult;
    }

    @ResponseBody
    @PostMapping("/v1/projects/{id}/families")
    public ResponseEntity<String> createProjectFamilies(@ModelAttribute ProjectFamilyDTO projectFamilyDTO, @PathVariable Long id, Locale locale) {

        try {

            // Recover user
            User user = Utiles.getUsuario();

            Project project = projectOldService.getProjectById(id);
            if (project == null) {
                throw new Exception("Proyecto " + id + " no encontrado");
            }

            Family family = FamilyMapper.mapProjectFamilyDTOToFamily(projectFamilyDTO, familyService);
            family.setProject(project);
            family = familyService.save(family);

            log.info("Familia " + family.getId() + " añadida al proyecto " + id + " por parte del usuario " + user.getId());

            // Return data
            return new ResponseEntity<>(messageSource.getMessage("project.detail.families.success", new Object[] {}, locale), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(messageSource.getMessage("project.detail.families.error", new Object[] {}, locale), HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @GetMapping("/v1/projects/{projectId}/families/{id}")
    public ResponseEntity<?> loadFamilyInfo(@PathVariable Long projectId, @PathVariable Long id) {

        Family family = familyService.getById(id);

        if (family == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(FamilyMapper.mapToDTO(family), HttpStatus.OK);
    }

    @ResponseBody
    @PutMapping("/v1/projects/{id}/families/{familyId}")
    public ResponseEntity<String> editProjectFamilies(@ModelAttribute ProjectFamilyDTO projectFamilyDTO, @PathVariable Long id, @PathVariable Long familyId, Locale locale) {

        try {

            // Recover user
            User user = Utiles.getUsuario();

            Family family = familyService.getById(familyId);
            family.setNameES(projectFamilyDTO.getNameES());
            family.setNameFR(projectFamilyDTO.getNameFR());
            family.setFamily(familyService.getById(projectFamilyDTO.getFamilyId()));
            family.setBrand(projectFamilyDTO.getBrand());
            family.setModel(projectFamilyDTO.getModel());
            family.setEnrollment(projectFamilyDTO.getEnrollment());

            family = familyService.save(family);

            log.info("Familia " + family.getId() + " modificada por parte del usuario " + user.getId());

            // Return data
            return new ResponseEntity<>(messageSource.getMessage("project.detail.families.success", new Object[] {}, locale), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(messageSource.getMessage("project.detail.families.error", new Object[] {}, locale), HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @DeleteMapping("/v1/projects/{id}/families/{familyId}")
    public ResponseEntity<String> deleteProjectFamily(@PathVariable Long familyId, @PathVariable Long id, Locale locale) {

        try {

            // Recover user
            User user = Utiles.getUsuario();

            familyService.delete(familyId);

            log.info("Familia " + familyId + " eliminado del proyecto " + id + " por parte del usuario " + user.getId());

            // Return data
            return new ResponseEntity<>(
                    messageSource.getMessage("project.detail.families.delete", new Object[] {}, locale), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(
                    messageSource.getMessage("project.detail.families.derror", new Object[] {}, locale),
                    HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @PostMapping("/projects/{id}/families/import")
    public ResponseEntity<String> importFamilies(@PathVariable Long id, @RequestParam("file") MultipartFile file, Locale locale) {

        try {

            // Recover user
            User user = Utiles.getUsuario();

            Project project = projectOldService.getProjectById(id);
            if (project == null) {
                throw new Exception("Proyecto " + id + " no encontrado");
            }

            // Action
            familyService.importFamilyFile(file, project, locale);

            // Log
            log.info("Fichero Familias importado en el proyecto " + id + " por parte del usuario " + user.getId());

            // Return data
            return new ResponseEntity<>(messageSource.getMessage("project.detail.families.import.success", new Object[] {}, locale), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(messageSource.getMessage("project.detail.families.import.error", new Object[] {}, locale), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/projects/{id}/families/export")
    public HttpEntity<ByteArrayResource> exportFamilies(@PathVariable Long id, Locale locale) throws IOException {

        XSSFWorkbook workbook = null;

        try {

            // Recover user
            User user = Utiles.getUsuario();

            // Log info
            log.info("El usuario " + user.getId() + " está exportando las familias del proyecto " + id);

            ByteArrayOutputStream archivo = new ByteArrayOutputStream();

            workbook = familyService.generateFamiliesExcel(id, locale);
            workbook.write(archivo);

            byte[] excelContent = archivo.toByteArray();

            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "force-download"));
            header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Families_Project_" + id + ".xlsx");

            return new HttpEntity<>(new ByteArrayResource(excelContent), header);

        } catch (Exception e) {
            log.error(e);
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }

        return null;
    }

}
