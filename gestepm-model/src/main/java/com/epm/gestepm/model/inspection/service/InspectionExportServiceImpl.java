package com.epm.gestepm.model.inspection.service;

import com.epm.gestepm.lib.locale.LocaleProvider;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.model.common.pdf.ImageUtils;
import com.epm.gestepm.modelapi.common.utils.Utiles;
import com.epm.gestepm.modelapi.deprecated.family.dto.Family;
import com.epm.gestepm.modelapi.deprecated.family.service.FamilyService;
import com.epm.gestepm.modelapi.inspection.dto.InspectionDto;
import com.epm.gestepm.modelapi.inspection.dto.InspectionFileDto;
import com.epm.gestepm.modelapi.inspection.dto.MaterialDto;
import com.epm.gestepm.modelapi.inspection.dto.finder.InspectionFileByIdFinderDto;
import com.epm.gestepm.modelapi.inspection.exception.InspectionExportException;
import com.epm.gestepm.modelapi.inspection.exception.InspectionNotEndedException;
import com.epm.gestepm.modelapi.inspection.service.InspectionExportService;
import com.epm.gestepm.modelapi.inspection.service.InspectionFileService;
import com.epm.gestepm.modelapi.inspection.service.InspectionMaterialsExportException;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.projectmaterial.dto.ProjectMaterialDto;
import com.epm.gestepm.modelapi.projectmaterial.dto.filter.ProjectMaterialFilterDto;
import com.epm.gestepm.modelapi.projectmaterial.service.ProjectMaterialService;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.NoProgrammedShareDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.finder.NoProgrammedShareByIdFinderDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.service.NoProgrammedShareService;
import com.epm.gestepm.modelapi.subfamily.dto.SubFamily;
import com.epm.gestepm.modelapi.subfamily.service.SubFamilyService;
import com.epm.gestepm.modelapi.user.dto.UserDto;
import com.epm.gestepm.modelapi.user.dto.finder.UserByIdFinderDto;
import com.epm.gestepm.modelapi.user.service.UserService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.SERVICE;

@Service
@Validated
@AllArgsConstructor
@EnableExecutionLog(layerMarker = SERVICE)
public class InspectionExportServiceImpl implements InspectionExportService {

    private static final String TEMPLATE_PATH = "/templates/pdf/intervention_share_no_programmed_%s.pdf";

    private static final String MATERIALS_TEMPLATE_PATH = "/templates/pdf/intervention_share_no_programmed_%s_materials.pdf";

    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    private final FamilyService familyService;

    private final InspectionFileService inspectionFileService;

    private final LocaleProvider localeProvider;

    private final MessageSource messageSource;

    private final NoProgrammedShareService noProgrammedShareService;

    private final ProjectService projectService;

    private final ProjectMaterialService projectMaterialService;

    private final SubFamilyService subFamilyService;

    private final UserService userService;

    @Override
    public byte[] generate(InspectionDto inspection) {
        try {
            if (inspection.getEndDate() == null) {
                throw new InspectionNotEndedException(inspection.getId());
            }

            final NoProgrammedShareDto noProgrammedShare = this.noProgrammedShareService.findOrNotFound(
                    new NoProgrammedShareByIdFinderDto(inspection.getShareId()));
            final ProjectDto project = this.projectService.findOrNotFound(new ProjectByIdFinderDto(noProgrammedShare.getProjectId()));
            final UserDto firstTechnical = this.userService.findOrNotFound(new UserByIdFinderDto(inspection.getFirstTechnicalId()));

            final String language = localeProvider.getLocale().orElse("es");
            final Locale locale = new Locale(language);
            final PdfReader pdfTemplate = new PdfReader(String.format(TEMPLATE_PATH, language));
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final PdfStamper stamper = new PdfStamper(pdfTemplate, baos);
            final AcroFields acroFields = stamper.getAcroFields();

            acroFields.setField("faultNum", noProgrammedShare.getId().toString());
            acroFields.setField("station", project.getName());
            acroFields.setField("faultDate", Utiles.transform(noProgrammedShare.getStartDate(), DATE_FORMAT));
            acroFields.setField("faultDescription", noProgrammedShare.getDescription().replaceAll("( *\n *){2,}", "\n"));
            acroFields.setField("inspectionId", inspection.getId().toString());
            acroFields.setField("inspectionType", this.messageSource.getMessage("inspection.type." + inspection.getAction().name().toLowerCase(), null, locale));
            acroFields.setField("interventionStartDate", Utiles.transform(inspection.getStartDate(), DATE_FORMAT));
            acroFields.setField("interventionEndDate", Utiles.transform(inspection.getEndDate(), DATE_FORMAT));
            acroFields.setField("technic1", firstTechnical.getFullName());

            if (inspection.getSecondTechnicalId() != null) {
                final UserDto secondTechnical = this.userService.findOrNotFound(new UserByIdFinderDto(inspection.getSecondTechnicalId()));
                acroFields.setField("technic2", secondTechnical.getFullName());
            }

            if (noProgrammedShare.getFamilyId() != null) {
                final Family family = this.familyService.getById(noProgrammedShare.getFamilyId().longValue());
                acroFields.setField("family", ("es".equals(language) ? family.getNameES() : family.getNameFR()));
                acroFields.setField("brand", family.getBrand());
                acroFields.setField("model", family.getModel());
                acroFields.setField("enrollment", family.getEnrollment());
            }

            if (noProgrammedShare.getSubFamilyId() != null) {
                final SubFamily subFamily = this.subFamilyService.getById(noProgrammedShare.getSubFamilyId().longValue());
                acroFields.setField("subFamily", ("es".equals(language) ? subFamily.getNameES() : subFamily.getNameFR()));
            }

            acroFields.setField("interventionDesc", inspection.getDescription().replaceAll("( *\n *){2,}", "\n"));
            acroFields.setField("clientName", inspection.getClientName());
            acroFields.setField("equipmentHours", inspection.getEquipmentHours() != null ? inspection.getEquipmentHours().toString() : "");

            if (!CollectionUtils.isEmpty(inspection.getMaterials())) {
                this.generateMaterialsPage(stamper, inspection);
            }

            if (!StringUtils.isBlank(inspection.getSignature()) && !"data:,".equals(inspection.getSignature())) {
                printSignature(stamper, inspection.getSignature(), "clientSign");
            }

            if (!StringUtils.isBlank(inspection.getOperatorSignature()) && !"data:,".equals(inspection.getOperatorSignature())) {
                printSignature(stamper, inspection.getOperatorSignature(), "opSign");
            }

            if (!inspection.getFileIds().isEmpty()) {
                this.printImages(stamper, pdfTemplate, inspection);
            }

            acroFields.setGenerateAppearances(true);
            stamper.setFormFlattening(true);

            stamper.close();
            pdfTemplate.close();

            return baos.toByteArray();
        } catch (IOException | DocumentException ex) {
            throw new InspectionExportException(inspection.getId());
        }
    }

    @Override
    public byte[] generateMaterials(InspectionDto inspection) {
        try {
            if (inspection.getEndDate() == null) {
                throw new InspectionNotEndedException(inspection.getId());
            }

            final NoProgrammedShareDto noProgrammedShare = this.noProgrammedShareService.findOrNotFound(
                    new NoProgrammedShareByIdFinderDto(inspection.getShareId()));

            final ProjectMaterialFilterDto projectMaterialFilterDto = new ProjectMaterialFilterDto();
            projectMaterialFilterDto.setProjectIds(List.of(noProgrammedShare.getProjectId()));

            final List<ProjectMaterialDto> materials = this.projectMaterialService.list(projectMaterialFilterDto)
                    .stream().limit(30).collect(Collectors.toList());

            final String language = localeProvider.getLocale().orElse("es");
            final PdfReader pdfTemplate = new PdfReader(String.format(MATERIALS_TEMPLATE_PATH, language));
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final PdfStamper stamper = new PdfStamper(pdfTemplate, baos);
            final AcroFields acroFields = stamper.getAcroFields();

            stamper.getAcroFields().setField("faultNum", noProgrammedShare.getId().toString());
            acroFields.setField("inspectionId", inspection.getId().toString());
            acroFields.setField("excelNum", "EXCEL: " + (inspection.getMaterialsFile() != null ? noProgrammedShare.getId() : "S/N"));

            for (int i = 0; i < materials.size(); i++) {
                acroFields.setField("reqMaterialDesc" + i, "es".equals(language) ? materials.get(i).getNameEs() : materials.get(i).getNameFr());
                acroFields.setField("reqMaterialRef" + i, "x");
            }

            stamper.getAcroFields().setGenerateAppearances(true);
            stamper.setFormFlattening(true);

            stamper.close();
            pdfTemplate.close();

            return baos.toByteArray();

        } catch (IOException | DocumentException ex) {
            throw new InspectionMaterialsExportException(inspection.getId());
        }
    }

    private void printSignature(final PdfStamper stamper, final String signature, final String fieldName) throws DocumentException, IOException {
        final Rectangle signFieldRec = stamper.getAcroFields().getFieldPositions(fieldName).get(0).position;
        final Image sign = Image.getInstance(Utiles.base64PngToByteArray(signature));

        sign.scaleAbsoluteHeight(signFieldRec.getHeight());
        sign.scaleAbsoluteWidth(signFieldRec.getWidth());
        sign.scaleToFit(signFieldRec);
        stamper.getAcroFields().removeField(fieldName);
        sign.setAbsolutePosition(signFieldRec.getLeft(), signFieldRec.getBottom());

        final PdfContentByte canvas = stamper.getOverContent(1);
        canvas.addImage(sign);
    }

    private void printImages(final PdfStamper stamper, final PdfReader pdfTemplate, final InspectionDto inspection) throws DocumentException, IOException {
        int pageNumber = pdfTemplate.getNumberOfPages();

        final float pageWidth = pdfTemplate.getPageSize(pageNumber).getWidth();
        final float pageHeight = pdfTemplate.getPageSize(pageNumber).getHeight();

        for (Integer id : inspection.getFileIds()) {

            final InspectionFileDto file = this.inspectionFileService.findOrNotFound(new InspectionFileByIdFinderDto(id));

            if (StringUtils.isNoneBlank(file.getContent())) {

                final byte[] imageBytes = Base64.getDecoder().decode(file.getContent());
                final byte[] compressedBytes = ImageUtils.compressImage(imageBytes, 0.5f);

                if (compressedBytes == null) {
                    continue;
                }

                final Image image = Image.getInstance(compressedBytes);

                if (image.getWidth() > pageWidth || image.getHeight() > pageHeight) {
                    image.scaleToFit(pageWidth, pageHeight);
                }

                float x = (pageWidth - image.getScaledWidth()) / 2;
                float y = (pageHeight - image.getScaledHeight()) / 2;
                image.setAbsolutePosition(x, y);

                stamper.insertPage(++pageNumber, pdfTemplate.getPageSizeWithRotation(1));
                stamper.getOverContent(pageNumber).addImage(image);
            }
        }
    }

    private void generateMaterialsPage(final PdfStamper stamper, final InspectionDto inspection) throws DocumentException, IOException {
        final PdfReader reader = stamper.getReader();
        stamper.insertPage(reader.getNumberOfPages() + 1, reader.getPageSize(1));

        final Rectangle pageSize = reader.getPageSizeWithRotation(Math.min(1, reader.getNumberOfPages()));
        final PdfContentByte canvas = stamper.getOverContent(reader.getNumberOfPages());

        final PdfPTable table = new PdfPTable(3);

        final float marginL = 60f, marginR = 60f, marginT = 60f;
        float usableWidth = pageSize.getWidth() - marginL - marginR;

        if (usableWidth < 1f) usableWidth = pageSize.getWidth() - 2f;

        table.setTotalWidth(usableWidth);
        table.setLockedWidth(true);
        table.setWidths(new float[]{2f, 1f, 1f});

        final byte[] ttf;
        try (InputStream in = new ClassPathResource("fonts/trebuc.ttf").getInputStream()) {
            ttf = in.readAllBytes();
        }

        final BaseFont base = BaseFont.createFont("trebuc.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED, false, ttf, null);
        final Font normal = new Font(base, 10, Font.NORMAL);
        final Font bold = new Font(base, 10, Font.BOLD);

        final Locale locale = new Locale(this.localeProvider.getLocale().orElse("es"));
        final String[] headers = {
                this.messageSource.getMessage("description", null, locale),
                this.messageSource.getMessage("shares.intervention.create.materials.uds", null, locale),
                this.messageSource.getMessage("shares.intervention.create.materials.ref", null, locale),
        };

        for (String h : headers) {
            final PdfPCell cell = new PdfPCell(new Phrase(h, bold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            table.addCell(cell);
        }

        final List<MaterialDto> materials = inspection.getMaterials().stream().limit(5).collect(Collectors.toList());

        materials.forEach(material -> {
            table.addCell(makeCell(material.getDescription(), normal));
            table.addCell(makeCell(material.getUnits().toString(), normal));
            table.addCell(makeCell(material.getReference(), normal));
        });

        float x = pageSize.getLeft() + marginL;
        float topY = pageSize.getTop() - marginT;

        table.writeSelectedRows(0, -1, x, topY, canvas);
    }

    private PdfPCell makeCell(String text, Font font) {
        final PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
}
