package com.insurance.backoffice.application.service;

/**
 * Exception thrown when PDF generation fails.
 * Clean Code: Specific exception for PDF generation errors.
 */
public class PdfGenerationException extends RuntimeException {
    
    public PdfGenerationException(String message) {
        super(message);
    }
    
    public PdfGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}