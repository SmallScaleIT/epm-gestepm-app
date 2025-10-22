package com.epm.gestepm.model.family.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.epm.gestepm.model.family.dao.FamilyRepository;
import com.epm.gestepm.model.subfamily.dao.SubFamilyRepository;
import com.epm.gestepm.modelapi.deprecated.family.dto.FamilyDTO;
import com.epm.gestepm.modelapi.deprecated.family.dto.FamilyTableDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.epm.gestepm.modelapi.deprecated.family.dto.Family;
import com.epm.gestepm.modelapi.deprecated.project.dto.Project;
import com.epm.gestepm.modelapi.subfamily.dto.SubFamily;
import com.epm.gestepm.modelapi.deprecated.family.service.FamilyService;
import com.epm.gestepm.modelapi.common.utils.ExcelUtils;
import com.epm.gestepm.modelapi.common.utils.datatables.PaginationCriteria;

@Service
public class FamilyServiceImpl implements FamilyService {

	private static final Log log = LogFactory.getLog(FamilyServiceImpl.class);
	
	@Autowired
	private FamilyRepository familyRepository;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SubFamilyRepository subFamilyRepository;

	@Override
	public Family getById(Long id) {
		return familyRepository.findById(id).get();
	}
	
	@Override
	public Family getFamilyByName(String name) {
		return familyRepository.findFamilyByName(name);
	}
	
	@Override
	public List<Family> findAll() {
		return (List<Family>) familyRepository.findAll();
	}

	@Override
	public List<Family> findByProjectId(Long projectId) {
		return familyRepository.findByProjectId(projectId);
	}

	@Override
	@Transactional
	public Family save(Family family) {
		return familyRepository.save(family);
	}
	
	@Override
	@Transactional
	public Family create(Family family) {
		
		// Copy new sub families
		List<SubFamily> subFamilies = new ArrayList<>(family.getSubFamilies());
				
		// Create container
		family = save(family);
		
		// Insert tools
		if (!subFamilies.isEmpty()) {
			
			for (SubFamily subFamily : subFamilies) {
				
				subFamily.setFamily(family);
				subFamilyRepository.save(subFamily);
			}
		}
		
		return family;
	}
	
	@Override
	@Transactional
	public Family update(Family family, Family currentFamily) {
		
		// Copy new sub families
		List<SubFamily> subFamilies = new ArrayList<>(family.getSubFamilies());
				
		// Remove sub families
		List<Long> removedIds = getRemovedSubFamilies(subFamilies, currentFamily.getSubFamilies());
		
		for (Long id : removedIds) {
			subFamilyRepository.deleteById(id);
		}
		
		// Update family
		family = save(family);
		
		// Insert new tools
		if (!subFamilies.isEmpty()) {
			
			for (SubFamily subFamily : subFamilies) {
				
				subFamily.setFamily(family);
				subFamilyRepository.save(subFamily);
			}
		}
		
		return family;
	}
	
	@Override
	public void delete(Long familyId) {
		familyRepository.deleteById(familyId);
	}
	
	@Override
	public List<FamilyTableDTO> getFamilyTableDTOs(PaginationCriteria pagination) {
		return familyRepository.findFamilyTableDTOs(pagination);
	}
	
	@Override
	public Long getFamiliesCount() {
		return familyRepository.findFamiliesCount();
	}
	
	@Override
	public List<FamilyTableDTO> getFamiliesDataTablesByProjectId(Long projectId, PaginationCriteria pagination) {
		return familyRepository.findFamiliesDataTablesByProjectId(projectId, pagination);
	}
	
	@Override
	public Long getFamiliesCountByProjectId(Long projectId) {
		return familyRepository.findFamiliesCountByProjectId(projectId);
	}
	
	@Override
	public List<FamilyDTO> getCommonFamilyDTOsByProjectId(Long projectId, Locale locale) {
		return familyRepository.findCommonFamilyDTOsByProjectId(projectId, locale);
	}
	
	@Override
	public List<FamilyDTO> getCustomFamilyDTOsByProjectId(Long projectId) {
		return familyRepository.findCustomFamilyDTOsByProjectId(projectId);
	}
	
	@Override
	public List<FamilyDTO> getClonableFamilyDTOs(Locale locale) {
		return familyRepository.findClonableFamilyDTOs(locale);
	}
	
	@Override
	public void importFamilyFile(MultipartFile file, Project project, Locale locale) throws Exception {
		
		XSSFWorkbook wb = null;
		
		try {
			
			String lowerCaseFileName = file.getOriginalFilename().toLowerCase();
			
			if (lowerCaseFileName.endsWith(".xlsx")) {
				wb = new XSSFWorkbook(file.getInputStream());
	        } else {
	           throw new Exception("El fichero cargado no tiene un formato valido: " + lowerCaseFileName);
	        }
			
			Sheet sheet = wb.getSheetAt(0);
			int rowsCount = sheet.getLastRowNum();
			
			// Check structure
			if (!checkHeaderStructure(sheet, locale)) {
				throw new Exception("La estructura del fichero cargado no es valida");
			}
			
			// Import info
			for (int i = 1; i <= rowsCount; i++) {
				importFamily(sheet, i, project);
			}

		} catch (Exception e) {
			
			log.error(e);
			throw e;
			
		} finally {
			
			if (wb != null) {
				wb.close();
			}
		}
	}
	
	@Override
	public XSSFWorkbook generateFamiliesExcel(Long projectId, Locale locale) {
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		
		Sheet sheet = workbook.createSheet(messageSource.getMessage("project.detail.equipments", new Object[] {}, locale));
		
		// Header
		generateHeader(workbook, sheet, locale);
				
		// Get Project Families
		List<FamilyDTO> families = getCustomFamilyDTOsByProjectId(projectId);
		
		// Row index
		int rowIndex = 1;
		
		if (families != null && !families.isEmpty()) {
			
			for (FamilyDTO family : families) {
				appendFamilyRow(sheet, rowIndex++, family, locale);
			}
		}		
		
		autoSizeColumns(sheet);
		
		return workbook;
	}
	
	private boolean checkHeaderStructure(Sheet sheet, Locale locale) {
	
		// Get Header Row
		Row headerRow = sheet.getRow(0);
		
		for (int i = 0; i < 6; i++) {
			
			// Cell
			Cell cell = headerRow.getCell(i);
			
			String cellText = cell.getStringCellValue();
			String cellNeededText = null;
			
			switch (i) {
				
				case 0:
					cellNeededText = messageSource.getMessage("project.detail.equipments.id", new Object[] {}, locale).toUpperCase();
					break;
					
				case 1:
					cellNeededText = messageSource.getMessage("shares.intervention.create.family", new Object[] {}, locale).toUpperCase();
					break;
					
				case 2:
					cellNeededText = messageSource.getMessage("project.detail.equipments.name.es", new Object[] {}, locale).toUpperCase();
					break;
					
				case 3:
					cellNeededText = messageSource.getMessage("project.detail.equipments.name.fr", new Object[] {}, locale).toUpperCase();
					break;
					
				case 4:
					cellNeededText = messageSource.getMessage("project.detail.equipments.brand", new Object[] {}, locale).toUpperCase();
					break;
					
				case 5:
					cellNeededText = messageSource.getMessage("project.detail.equipments.model", new Object[] {}, locale).toUpperCase();
					break;
					
				case 6:
					cellNeededText = messageSource.getMessage("project.detail.equipments.enrollment", new Object[] {}, locale).toUpperCase();
					break;
					
				default:
					break;
			}
			
			if (!cellText.equals(cellNeededText)) {
				return false;
			}			
		}
		
		return true;
	}
	
	private void importFamily(Sheet sheet, int rowIndex, Project project) throws Exception {
		
		// Get Row
		Row row = sheet.getRow(rowIndex);
				
		// Cell
		Cell cellId = row.getCell(0);
		long familyId = cellId == null ? 0 : (long) cellId.getNumericCellValue();
		
		Family family = null;
		
		if (familyId == 0) {
			family = new Family();
		} else {
			family = getById(familyId);
		}
		
		// Cell
		Cell cellFamilyName = row.getCell(1);
		String familyName = cellFamilyName == null ? null : cellFamilyName.getStringCellValue();
		
		if (familyName == null) {
			throw new Exception("El campo familyName debe estar completo para la fila " + rowIndex);
		}
		
		Family linkedFamily = getFamilyByName(familyName);
		
		if (linkedFamily == null) {
			throw new Exception("No existe la familia con nombre " + familyName);
		}
				
		// Cell
		Cell cellNameES = row.getCell(2);
		String nameES = cellNameES == null ? null : cellNameES.getStringCellValue();
		
		if (nameES == null) {
			throw new Exception("El campo nameES debe estar completo para la fila " + rowIndex);
		}
		
		// Cell
		Cell cellNameFR = row.getCell(3);
		String nameFR = cellNameFR == null ? null : cellNameFR.getStringCellValue();
		
		if (nameFR == null) {
			throw new Exception("El campo nameFR debe estar completo para la fila " + rowIndex);
		}
		
		// Cell
		Cell cellBrand = row.getCell(4);
		String brand = cellBrand == null ? null : cellBrand.getStringCellValue();
		
		// Cell
		Cell cellModel = row.getCell(5);
		String model = cellModel == null ? null : cellModel.getStringCellValue();
		
		// Cell
		Cell cellEnrollment = row.getCell(6);
		String enrollment = cellEnrollment == null ? null : cellEnrollment.getStringCellValue();
	
		// Set values
		family.setFamily(linkedFamily);
		family.setProject(project);
		family.setNameES(nameES);
		family.setNameFR(nameFR);
		family.setBrand(brand);
		family.setModel(model);
		family.setEnrollment(enrollment);
		family.setCommon(3);
	
		// Save
		family = save(family);
		
		// log
		log.info("Familia " + family.getId() + " " + (familyId == 0 ? "creada" : "actualizada") + " con exito");
	}
	
	private void generateHeader(XSSFWorkbook workbook, Sheet sheet, Locale locale) {
		
		// Create header row
		Row headerRow = sheet.createRow(0);
		
		// Styling header cells
		Cell headerIdCell = CellUtil.createCell(headerRow, 0, messageSource.getMessage("project.detail.equipments.id", new Object[] {}, locale).toUpperCase());
		headerIdCell.setCellStyle(ExcelUtils.getLightYellowStyle(workbook, HorizontalAlignment.CENTER, true, true, BorderStyle.THIN));
		
		Cell headerFamilyCell = CellUtil.createCell(headerRow, 1, messageSource.getMessage("shares.intervention.create.family", new Object[] {}, locale).toUpperCase());
		headerFamilyCell.setCellStyle(ExcelUtils.getLightYellowStyle(workbook, HorizontalAlignment.CENTER, true, true, BorderStyle.THIN));
		
		Cell headerNameESCell = CellUtil.createCell(headerRow, 2, messageSource.getMessage("project.detail.equipments.name.es", new Object[] {}, locale).toUpperCase());
		headerNameESCell.setCellStyle(ExcelUtils.getLightYellowStyle(workbook, HorizontalAlignment.CENTER, true, true, BorderStyle.THIN));
		
		Cell headerNameFRCell = CellUtil.createCell(headerRow, 3, messageSource.getMessage("project.detail.equipments.name.fr", new Object[] {}, locale).toUpperCase());
		headerNameFRCell.setCellStyle(ExcelUtils.getLightYellowStyle(workbook, HorizontalAlignment.CENTER, true, true, BorderStyle.THIN));
		
		Cell headerBrandCell = CellUtil.createCell(headerRow, 4, messageSource.getMessage("project.detail.equipments.brand", new Object[] {}, locale).toUpperCase());
		headerBrandCell.setCellStyle(ExcelUtils.getLightYellowStyle(workbook, HorizontalAlignment.CENTER, true, true, BorderStyle.THIN));
		
		Cell headerModelCell = CellUtil.createCell(headerRow, 5, messageSource.getMessage("project.detail.equipments.model", new Object[] {}, locale).toUpperCase());
		headerModelCell.setCellStyle(ExcelUtils.getLightYellowStyle(workbook, HorizontalAlignment.CENTER, true, true, BorderStyle.THIN));
		
		Cell headerEnrollmentCell = CellUtil.createCell(headerRow, 6, messageSource.getMessage("project.detail.equipments.enrollment", new Object[] {}, locale).toUpperCase());
		headerEnrollmentCell.setCellStyle(ExcelUtils.getLightYellowStyle(workbook, HorizontalAlignment.CENTER, true, true, BorderStyle.THIN));
	}
	
	private void appendFamilyRow(Sheet sheet, int rowIndex, FamilyDTO family, Locale locale) {
		
		// Create header row
		Row dateRow = sheet.createRow(rowIndex);
					
		for (int i = 0; i < 6; i++) {
			
			// Cell
			Cell cell = dateRow.createCell(i);
			
			switch (i) {
			
				case 0:
					cell.setCellValue(family.getId());
					break;
					
				case 1:
					
					Family linkFamily = getById(family.getFamilyId());
					
					if (linkFamily != null) {
						cell.setCellValue("es".equals(locale.getLanguage()) ? linkFamily.getNameES() : linkFamily.getNameFR());
					}
					
					break;
					
				case 2:
					cell.setCellValue(family.getNameES());
					break;	
					
				case 3:
					cell.setCellValue(family.getNameFR());
					break;
					
				case 4:
					cell.setCellValue(family.getBrand());
					break;	
					
				case 5:
					cell.setCellValue(family.getModel());
					break;
					
				case 6:
					cell.setCellValue(family.getEnrollment());
					break;	
			}
		}
	}
	
	private void autoSizeColumns(Sheet sheet) {
		for (int i = 0; i <= 6; i++) {
			sheet.autoSizeColumn(i);
		}
	}
	
	private List<Long> getRemovedSubFamilies(List<SubFamily> currentSubFamilies, List<SubFamily> oldSubFamilies) {
		
		List<Long> currentIds = new ArrayList<>();
		List<Long> oldIds = new ArrayList<>();
		
		for (SubFamily subFamily : currentSubFamilies) {
			
			if (subFamily.getId() == null) {
				continue;
			}
			
			currentIds.add(subFamily.getId());
		}
		
		for (SubFamily subFamily : oldSubFamilies) {
			oldIds.add(subFamily.getId());
		}
		
		oldIds.removeAll(currentIds);
		
		return oldIds;
	}
}
