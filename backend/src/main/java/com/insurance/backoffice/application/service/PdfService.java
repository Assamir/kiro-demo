package com.insurance.backoffice.application.service;

import com.insurance.backoffice.domain.*;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Service for generating PDF documents for insurance policies.
 * Clean Code: Single responsibility - focused on PDF generation only.
 */
@Service
public class PdfService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DeviceRgb COMPANY_BLUE = new DeviceRgb(0, 102, 204);
    private static final DeviceRgb COMPANY_GOLD = new DeviceRgb(255, 215, 0);
    private static final DeviceRgb LIGHT_GRAY = new DeviceRgb(245, 245, 245);
    
    /**
     * Generates a PDF document for the given policy.
     * Clean Code: Main public method with clear intention.
     * 
     * @param policy the policy to generate PDF for
     * @return PDF document as byte array
     * @throws PdfGenerationException if PDF generation fails
     */
    public byte[] generatePolicyPdf(Policy policy) {
        validatePolicy(policy);
        
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);
            
            addHeader(document);
            addPolicyInformation(document, policy);
            addClientInformation(document, policy.getClient());
            addVehicleInformation(document, policy.getVehicle());
            addCoverageDetails(document, policy);
            addPremiumInformation(document, policy);
            addFooter(document);
            
            document.close();
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            throw new PdfGenerationException("Failed to generate PDF for policy: " + policy.getPolicyNumber(), e);
        }
    }
    
    /**
     * Validates that the policy has all required data for PDF generation.
     * Clean Code: Fail-fast validation with meaningful error messages.
     */
    private void validatePolicy(Policy policy) {
        if (policy == null) {
            throw new IllegalArgumentException("Policy cannot be null");
        }
        if (policy.getClient() == null) {
            throw new IllegalArgumentException("Policy must have a client");
        }
        if (policy.getVehicle() == null) {
            throw new IllegalArgumentException("Policy must have a vehicle");
        }
        if (policy.getPolicyNumber() == null || policy.getPolicyNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Policy must have a policy number");
        }
    }
    
    /**
     * Adds the company header with branding.
     * Clean Code: Extracted method for header creation.
     */
    private void addHeader(Document document) throws IOException {
        PdfFont boldFont = PdfFontFactory.createFont();
        
        // Company name and title
        Paragraph companyName = new Paragraph("INSURANCE COMPANY")
                .setFont(boldFont)
                .setFontSize(20)
                .setFontColor(COMPANY_BLUE)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        
        Paragraph title = new Paragraph("INSURANCE POLICY CERTIFICATE")
                .setFont(boldFont)
                .setFontSize(16)
                .setFontColor(COMPANY_GOLD)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        
        document.add(companyName);
        document.add(title);
        
        // Add separator line
        Table separatorTable = new Table(1);
        separatorTable.setWidth(UnitValue.createPercentValue(100));
        Cell separatorCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(COMPANY_BLUE, 2))
                .setHeight(10);
        separatorTable.addCell(separatorCell);
        document.add(separatorTable);
    }
    
    /**
     * Adds policy information section.
     * Clean Code: Focused method for policy data presentation.
     */
    private void addPolicyInformation(Document document, Policy policy) throws IOException {
        PdfFont boldFont = PdfFontFactory.createFont();
        
        document.add(new Paragraph("POLICY INFORMATION")
                .setFont(boldFont)
                .setFontSize(14)
                .setFontColor(COMPANY_BLUE)
                .setMarginTop(15)
                .setMarginBottom(10));
        
        Table policyTable = createInfoTable();
        
        addTableRow(policyTable, "Policy Number:", policy.getPolicyNumber());
        addTableRow(policyTable, "Insurance Type:", getInsuranceTypeDescription(policy.getInsuranceType()));
        addTableRow(policyTable, "Issue Date:", formatDate(policy.getIssueDate()));
        addTableRow(policyTable, "Coverage Period:", 
                formatDate(policy.getStartDate()) + " - " + formatDate(policy.getEndDate()));
        addTableRow(policyTable, "Status:", policy.getStatus().toString());
        
        document.add(policyTable);
    }
    
    /**
     * Adds client information section.
     * Clean Code: Separated client data presentation logic.
     */
    private void addClientInformation(Document document, Client client) throws IOException {
        PdfFont boldFont = PdfFontFactory.createFont();
        
        document.add(new Paragraph("POLICYHOLDER INFORMATION")
                .setFont(boldFont)
                .setFontSize(14)
                .setFontColor(COMPANY_BLUE)
                .setMarginTop(15)
                .setMarginBottom(10));
        
        Table clientTable = createInfoTable();
        
        addTableRow(clientTable, "Full Name:", client.getFullName());
        addTableRow(clientTable, "PESEL:", client.getPesel());
        addTableRow(clientTable, "Address:", client.getAddress());
        addTableRow(clientTable, "Email:", client.getEmail());
        addTableRow(clientTable, "Phone:", client.getPhoneNumber());
        
        document.add(clientTable);
    }
    
    /**
     * Adds vehicle information section.
     * Clean Code: Vehicle data presentation with clear structure.
     */
    private void addVehicleInformation(Document document, Vehicle vehicle) throws IOException {
        PdfFont boldFont = PdfFontFactory.createFont();
        
        document.add(new Paragraph("VEHICLE INFORMATION")
                .setFont(boldFont)
                .setFontSize(14)
                .setFontColor(COMPANY_BLUE)
                .setMarginTop(15)
                .setMarginBottom(10));
        
        Table vehicleTable = createInfoTable();
        
        addTableRow(vehicleTable, "Make & Model:", vehicle.getFullDescription());
        addTableRow(vehicleTable, "Year of Manufacture:", vehicle.getYearOfManufacture().toString());
        addTableRow(vehicleTable, "Registration Number:", vehicle.getRegistrationNumber());
        addTableRow(vehicleTable, "VIN:", vehicle.getVin());
        addTableRow(vehicleTable, "Engine Capacity:", vehicle.getEngineCapacity() + " cm³");
        addTableRow(vehicleTable, "Power:", vehicle.getPower() + " HP");
        addTableRow(vehicleTable, "First Registration:", formatDate(vehicle.getFirstRegistrationDate()));
        
        document.add(vehicleTable);
    }
    
    /**
     * Adds coverage details specific to insurance type.
     * Clean Code: Polymorphic behavior based on insurance type.
     */
    private void addCoverageDetails(Document document, Policy policy) throws IOException {
        PdfFont boldFont = PdfFontFactory.createFont();
        
        document.add(new Paragraph("COVERAGE DETAILS")
                .setFont(boldFont)
                .setFontSize(14)
                .setFontColor(COMPANY_BLUE)
                .setMarginTop(15)
                .setMarginBottom(10));
        
        Table coverageTable = createInfoTable();
        
        PolicyDetails details = policy.getPolicyDetails();
        if (details != null) {
            addInsuranceSpecificDetails(coverageTable, policy.getInsuranceType(), details);
        } else {
            addTableRow(coverageTable, "Coverage:", "Standard coverage as per policy terms");
        }
        
        document.add(coverageTable);
    }
    
    /**
     * Adds insurance type specific details to the coverage table.
     * Clean Code: Switch statement with clear case handling.
     */
    private void addInsuranceSpecificDetails(Table table, InsuranceType insuranceType, PolicyDetails details) {
        switch (insuranceType) {
            case OC:
                addOCDetails(table, details);
                break;
            case AC:
                addACDetails(table, details);
                break;
            case NNW:
                addNNWDetails(table, details);
                break;
        }
    }
    
    /**
     * Adds OC insurance specific details.
     * Clean Code: Focused method for OC insurance data.
     */
    private void addOCDetails(Table table, PolicyDetails details) {
        if (details.getGuaranteedSum() != null) {
            addTableRow(table, "Guaranteed Sum:", formatCurrency(details.getGuaranteedSum()));
        }
        if (details.getCoverageArea() != null) {
            addTableRow(table, "Coverage Area:", details.getCoverageArea());
        }
        addTableRow(table, "Coverage Type:", "Third Party Liability (OC)");
    }
    
    /**
     * Adds AC insurance specific details.
     * Clean Code: AC insurance data presentation.
     */
    private void addACDetails(Table table, PolicyDetails details) {
        if (details.getAcVariant() != null) {
            addTableRow(table, "Variant:", details.getAcVariant().toString());
        }
        if (details.getSumInsured() != null) {
            addTableRow(table, "Sum Insured:", formatCurrency(details.getSumInsured()));
        }
        if (details.getCoverageScope() != null) {
            addTableRow(table, "Coverage Scope:", details.getCoverageScope());
        }
        if (details.getDeductible() != null) {
            addTableRow(table, "Deductible:", formatCurrency(details.getDeductible()));
        }
        if (details.getWorkshopType() != null) {
            addTableRow(table, "Workshop Type:", details.getWorkshopType());
        }
        addTableRow(table, "Coverage Type:", "Comprehensive (AC)");
    }
    
    /**
     * Adds NNW insurance specific details.
     * Clean Code: NNW insurance data presentation.
     */
    private void addNNWDetails(Table table, PolicyDetails details) {
        if (details.getSumInsured() != null) {
            addTableRow(table, "Sum Insured:", formatCurrency(details.getSumInsured()));
        }
        if (details.getCoveredPersons() != null) {
            addTableRow(table, "Covered Persons:", details.getCoveredPersons());
        }
        addTableRow(table, "Coverage Type:", "Accident Insurance (NNW)");
    }
    
    /**
     * Adds premium information section.
     * Clean Code: Financial information presentation with clear formatting.
     */
    private void addPremiumInformation(Document document, Policy policy) throws IOException {
        PdfFont boldFont = PdfFontFactory.createFont();
        
        document.add(new Paragraph("PREMIUM INFORMATION")
                .setFont(boldFont)
                .setFontSize(14)
                .setFontColor(COMPANY_BLUE)
                .setMarginTop(15)
                .setMarginBottom(10));
        
        Table premiumTable = createInfoTable();
        
        addTableRow(premiumTable, "Base Premium:", formatCurrency(policy.getPremium()));
        
        if (policy.getDiscountSurcharge() != null && policy.getDiscountSurcharge().compareTo(BigDecimal.ZERO) != 0) {
            String discountLabel = policy.getDiscountSurcharge().compareTo(BigDecimal.ZERO) > 0 ? "Surcharge:" : "Discount:";
            addTableRow(premiumTable, discountLabel, formatCurrency(policy.getDiscountSurcharge().abs()));
        }
        
        // Add total premium row with highlighting
        Cell totalLabelCell = new Cell()
                .add(new Paragraph("TOTAL PREMIUM:").setFont(boldFont))
                .setBackgroundColor(LIGHT_GRAY)
                .setBorder(Border.NO_BORDER)
                .setPadding(8);
        
        Cell totalValueCell = new Cell()
                .add(new Paragraph(formatCurrency(policy.getTotalPremium())).setFont(boldFont))
                .setBackgroundColor(LIGHT_GRAY)
                .setBorder(Border.NO_BORDER)
                .setPadding(8);
        
        premiumTable.addCell(totalLabelCell);
        premiumTable.addCell(totalValueCell);
        
        document.add(premiumTable);
    }
    
    /**
     * Adds footer with company information and generation date.
     * Clean Code: Footer creation with professional appearance.
     */
    private void addFooter(Document document) throws IOException {
        document.add(new Paragraph("\n"));
        
        // Add separator line
        Table separatorTable = new Table(1);
        separatorTable.setWidth(UnitValue.createPercentValue(100));
        Cell separatorCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setBorderTop(new com.itextpdf.layout.borders.SolidBorder(COMPANY_BLUE, 1))
                .setHeight(10);
        separatorTable.addCell(separatorCell);
        document.add(separatorTable);
        
        Paragraph footer = new Paragraph("This document was generated on " + formatDate(LocalDate.now()) + 
                " by Insurance Company Backoffice System")
                .setFontSize(10)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10);
        
        document.add(footer);
    }
    
    /**
     * Creates a standardized information table.
     * Clean Code: Reusable table creation with consistent styling.
     */
    private Table createInfoTable() {
        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginBottom(15);
        return table;
    }
    
    /**
     * Adds a row to an information table with consistent styling.
     * Clean Code: Consistent table row creation.
     */
    private void addTableRow(Table table, String label, String value) {
        try {
            PdfFont boldFont = PdfFontFactory.createFont();
            
            Cell labelCell = new Cell()
                    .add(new Paragraph(label).setFont(boldFont))
                    .setBorder(Border.NO_BORDER)
                    .setPadding(5);
            
            Cell valueCell = new Cell()
                    .add(new Paragraph(value != null ? value : "N/A"))
                    .setBorder(Border.NO_BORDER)
                    .setPadding(5);
            
            table.addCell(labelCell);
            table.addCell(valueCell);
        } catch (IOException e) {
            throw new PdfGenerationException("Failed to create table row", e);
        }
    }
    
    /**
     * Formats a date for display in the PDF.
     * Clean Code: Consistent date formatting.
     */
    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "N/A";
    }
    
    /**
     * Formats a currency amount for display.
     * Clean Code: Consistent currency formatting.
     */
    private String formatCurrency(BigDecimal amount) {
        return amount != null ? String.format("%.2f PLN", amount) : "N/A";
    }
    
    /**
     * Returns a human-readable description for insurance type.
     * Clean Code: Mapping enum to user-friendly description.
     */
    private String getInsuranceTypeDescription(InsuranceType insuranceType) {
        return switch (insuranceType) {
            case OC -> "Third Party Liability (OC)";
            case AC -> "Comprehensive (AC)";
            case NNW -> "Accident Insurance (NNW)";
        };
    }
}