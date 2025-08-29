// Validation result interface
export interface ValidationResult {
  isValid: boolean;
  message?: string;
  severity?: 'error' | 'warning' | 'info';
}

// Validation rule interface
export interface ValidationRule<T = any> {
  validator: (value: T) => boolean;
  message: string;
  severity?: 'error' | 'warning' | 'info';
}

// Field validation configuration
export interface FieldValidation<T = any> {
  required?: boolean;
  rules?: ValidationRule<T>[];
  customValidator?: (value: T) => ValidationResult;
}

// Form validation schema
export type ValidationSchema<T> = {
  [K in keyof T]?: FieldValidation<T[K]>;
};

// Email validation
export const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

// Enhanced email validation with detailed feedback
export const validateEmail = (email: string): ValidationResult => {
  if (!email || email.trim().length === 0) {
    return { isValid: false, message: 'Email is required' };
  }
  
  if (!isValidEmail(email)) {
    return { isValid: false, message: 'Please enter a valid email address' };
  }
  
  return { isValid: true };
};

// Password validation
export const isValidPassword = (password: string): boolean => {
  // At least 8 characters, one uppercase, one lowercase, one number
  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{8,}$/;
  return passwordRegex.test(password);
};

// Enhanced password validation with detailed feedback
export const validatePassword = (password: string): ValidationResult => {
  if (!password || password.length === 0) {
    return { isValid: false, message: 'Password is required' };
  }
  
  if (password.length < 8) {
    return { isValid: false, message: 'Password must be at least 8 characters long' };
  }
  
  if (!/(?=.*[a-z])/.test(password)) {
    return { isValid: false, message: 'Password must contain at least one lowercase letter' };
  }
  
  if (!/(?=.*[A-Z])/.test(password)) {
    return { isValid: false, message: 'Password must contain at least one uppercase letter' };
  }
  
  if (!/(?=.*\d)/.test(password)) {
    return { isValid: false, message: 'Password must contain at least one number' };
  }
  
  return { isValid: true };
};

// PESEL validation (Polish personal identification number)
export const isValidPESEL = (pesel: string): boolean => {
  if (pesel.length !== 11 || !/^\d{11}$/.test(pesel)) {
    return false;
  }

  const weights = [1, 3, 7, 9, 1, 3, 7, 9, 1, 3];
  let sum = 0;

  for (let i = 0; i < 10; i++) {
    sum += parseInt(pesel[i]) * weights[i];
  }

  const checksum = (10 - (sum % 10)) % 10;
  return checksum === parseInt(pesel[10]);
};

// Enhanced PESEL validation
export const validatePESEL = (pesel: string): ValidationResult => {
  if (!pesel || pesel.trim().length === 0) {
    return { isValid: false, message: 'PESEL is required' };
  }
  
  if (pesel.length !== 11) {
    return { isValid: false, message: 'PESEL must be exactly 11 digits' };
  }
  
  if (!/^\d{11}$/.test(pesel)) {
    return { isValid: false, message: 'PESEL must contain only digits' };
  }
  
  if (!isValidPESEL(pesel)) {
    return { isValid: false, message: 'Invalid PESEL number' };
  }
  
  return { isValid: true };
};

// VIN validation
export const isValidVIN = (vin: string): boolean => {
  const vinRegex = /^[A-HJ-NPR-Z0-9]{17}$/;
  return vinRegex.test(vin);
};

// Enhanced VIN validation
export const validateVIN = (vin: string): ValidationResult => {
  if (!vin || vin.trim().length === 0) {
    return { isValid: false, message: 'VIN is required' };
  }
  
  if (vin.length !== 17) {
    return { isValid: false, message: 'VIN must be exactly 17 characters' };
  }
  
  if (!isValidVIN(vin.toUpperCase())) {
    return { isValid: false, message: 'Invalid VIN format (no I, O, Q allowed)' };
  }
  
  return { isValid: true };
};

// Registration number validation
export const isValidRegistrationNumber = (regNumber: string): boolean => {
  // Basic validation - can be extended based on specific country requirements
  const regRegex = /^[A-Z0-9]{2,10}$/;
  return regRegex.test(regNumber.toUpperCase());
};

// Enhanced registration number validation
export const validateRegistrationNumber = (regNumber: string): ValidationResult => {
  if (!regNumber || regNumber.trim().length === 0) {
    return { isValid: false, message: 'Registration number is required' };
  }
  
  if (regNumber.length < 2 || regNumber.length > 10) {
    return { isValid: false, message: 'Registration number must be 2-10 characters' };
  }
  
  if (!isValidRegistrationNumber(regNumber)) {
    return { isValid: false, message: 'Invalid registration number format' };
  }
  
  return { isValid: true };
};

// Phone number validation
export const isValidPhoneNumber = (phone: string): boolean => {
  const phoneRegex = /^\+?[\d\s\-()]{9,15}$/;
  return phoneRegex.test(phone);
};

// Enhanced phone number validation
export const validatePhoneNumber = (phone: string): ValidationResult => {
  if (!phone || phone.trim().length === 0) {
    return { isValid: false, message: 'Phone number is required' };
  }
  
  if (!isValidPhoneNumber(phone)) {
    return { isValid: false, message: 'Please enter a valid phone number' };
  }
  
  return { isValid: true };
};

// Required field validation
export const isRequired = (value: string | number | null | undefined): boolean => {
  if (value === null || value === undefined) return false;
  if (typeof value === 'string') return value.trim().length > 0;
  return true;
};

// Enhanced required field validation
export const validateRequired = (value: any, fieldName: string = 'Field'): ValidationResult => {
  if (!isRequired(value)) {
    return { isValid: false, message: `${fieldName} is required` };
  }
  return { isValid: true };
};

// Date validation
export const validateDate = (date: string, fieldName: string = 'Date'): ValidationResult => {
  if (!date || date.trim().length === 0) {
    return { isValid: false, message: `${fieldName} is required` };
  }
  
  const dateObj = new Date(date);
  if (isNaN(dateObj.getTime())) {
    return { isValid: false, message: `Please enter a valid ${fieldName.toLowerCase()}` };
  }
  
  return { isValid: true };
};

// Date range validation
export const validateDateRange = (
  startDate: string, 
  endDate: string, 
  startFieldName: string = 'Start date',
  endFieldName: string = 'End date'
): ValidationResult => {
  const startValidation = validateDate(startDate, startFieldName);
  if (!startValidation.isValid) return startValidation;
  
  const endValidation = validateDate(endDate, endFieldName);
  if (!endValidation.isValid) return endValidation;
  
  if (new Date(startDate) >= new Date(endDate)) {
    return { 
      isValid: false, 
      message: `${endFieldName} must be after ${startFieldName.toLowerCase()}` 
    };
  }
  
  return { isValid: true };
};

// Number validation
export const validateNumber = (
  value: string | number, 
  fieldName: string = 'Value',
  min?: number,
  max?: number
): ValidationResult => {
  if (value === '' || value === null || value === undefined) {
    return { isValid: false, message: `${fieldName} is required` };
  }
  
  const numValue = typeof value === 'string' ? parseFloat(value) : value;
  
  if (isNaN(numValue)) {
    return { isValid: false, message: `${fieldName} must be a valid number` };
  }
  
  if (min !== undefined && numValue < min) {
    return { isValid: false, message: `${fieldName} must be at least ${min}` };
  }
  
  if (max !== undefined && numValue > max) {
    return { isValid: false, message: `${fieldName} must be at most ${max}` };
  }
  
  return { isValid: true };
};

// Generic field validator
export const validateField = <T>(
  value: T, 
  fieldName: string, 
  validation: FieldValidation<T>
): ValidationResult => {
  // Check required
  if (validation.required && !isRequired(value)) {
    return { isValid: false, message: `${fieldName} is required` };
  }
  
  // Skip other validations if field is not required and empty
  if (!validation.required && !isRequired(value)) {
    return { isValid: true };
  }
  
  // Check custom validator first
  if (validation.customValidator) {
    const result = validation.customValidator(value);
    if (!result.isValid) return result;
  }
  
  // Check rules
  if (validation.rules) {
    for (const rule of validation.rules) {
      if (!rule.validator(value)) {
        return { 
          isValid: false, 
          message: rule.message,
          severity: rule.severity || 'error'
        };
      }
    }
  }
  
  return { isValid: true };
};

// Form validator
export const validateForm = <T extends Record<string, any>>(
  formData: T, 
  schema: ValidationSchema<T>
): { isValid: boolean; errors: Record<keyof T, string> } => {
  const errors: Record<keyof T, string> = {} as Record<keyof T, string>;
  let isValid = true;
  
  for (const fieldName in schema) {
    const fieldValidation = schema[fieldName];
    if (fieldValidation) {
      const result = validateField(formData[fieldName], String(fieldName), fieldValidation);
      if (!result.isValid) {
        errors[fieldName] = result.message || 'Invalid value';
        isValid = false;
      }
    }
  }
  
  return { isValid, errors };
};

// Real-time validation hook helper
export const createRealTimeValidator = <T extends Record<string, any>>(
  schema: ValidationSchema<T>
) => {
  return (fieldName: keyof T, value: T[keyof T]): ValidationResult => {
    const fieldValidation = schema[fieldName];
    if (!fieldValidation) {
      return { isValid: true };
    }
    
    return validateField(value, String(fieldName), fieldValidation);
  };
};