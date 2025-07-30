// Email validation
export const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

// Password validation
export const isValidPassword = (password: string): boolean => {
  // At least 8 characters, one uppercase, one lowercase, one number
  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{8,}$/;
  return passwordRegex.test(password);
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

// VIN validation
export const isValidVIN = (vin: string): boolean => {
  const vinRegex = /^[A-HJ-NPR-Z0-9]{17}$/;
  return vinRegex.test(vin);
};

// Registration number validation
export const isValidRegistrationNumber = (regNumber: string): boolean => {
  // Basic validation - can be extended based on specific country requirements
  const regRegex = /^[A-Z0-9]{2,10}$/;
  return regRegex.test(regNumber.toUpperCase());
};

// Phone number validation
export const isValidPhoneNumber = (phone: string): boolean => {
  const phoneRegex = /^\+?[\d\s\-\(\)]{9,15}$/;
  return phoneRegex.test(phone);
};

// Required field validation
export const isRequired = (value: string | number | null | undefined): boolean => {
  if (value === null || value === undefined) return false;
  if (typeof value === 'string') return value.trim().length > 0;
  return true;
};