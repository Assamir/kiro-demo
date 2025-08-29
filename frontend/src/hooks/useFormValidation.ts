import { useState, useCallback, useEffect } from 'react';
import { ValidationSchema, validateForm, validateField, ValidationResult } from '../utils/validators';

interface UseFormValidationOptions<T> {
  initialValues: T;
  validationSchema: ValidationSchema<T>;
  validateOnChange?: boolean;
  validateOnBlur?: boolean;
  onSubmit?: (values: T) => void | Promise<void>;
}

interface FormState<T> {
  values: T;
  errors: Partial<Record<keyof T, string>>;
  touched: Partial<Record<keyof T, boolean>>;
  isSubmitting: boolean;
  isValid: boolean;
  isDirty: boolean;
}

interface FormActions<T> {
  setValue: (field: keyof T, value: T[keyof T]) => void;
  setValues: (values: Partial<T>) => void;
  setError: (field: keyof T, error: string) => void;
  setErrors: (errors: Partial<Record<keyof T, string>>) => void;
  clearError: (field: keyof T) => void;
  clearErrors: () => void;
  setTouched: (field: keyof T, touched?: boolean) => void;
  setFieldTouched: (field: keyof T) => void;
  validateField: (field: keyof T) => ValidationResult;
  validateForm: () => boolean;
  handleChange: (field: keyof T) => (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void;
  handleBlur: (field: keyof T) => (event: React.FocusEvent<HTMLInputElement | HTMLTextAreaElement>) => void;
  handleSubmit: (event: React.FormEvent) => void;
  reset: (values?: T) => void;
}

export function useFormValidation<T extends Record<string, any>>({
  initialValues,
  validationSchema,
  validateOnChange = true,
  validateOnBlur = true,
  onSubmit,
}: UseFormValidationOptions<T>): FormState<T> & FormActions<T> {
  const [values, setValuesState] = useState<T>(initialValues);
  const [errors, setErrorsState] = useState<Partial<Record<keyof T, string>>>({});
  const [touched, setTouchedState] = useState<Partial<Record<keyof T, boolean>>>({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Calculate derived state
  const isValid = Object.keys(errors).length === 0;
  const isDirty = JSON.stringify(values) !== JSON.stringify(initialValues);

  // Validate a single field
  const validateSingleField = useCallback((field: keyof T): ValidationResult => {
    const fieldValidation = validationSchema[field];
    if (!fieldValidation) {
      return { isValid: true };
    }

    return validateField(values[field], String(field), fieldValidation);
  }, [values, validationSchema]);

  // Validate entire form
  const validateEntireForm = useCallback((): boolean => {
    const { isValid: formIsValid, errors: formErrors } = validateForm(values, validationSchema);
    setErrorsState(formErrors);
    return formIsValid;
  }, [values, validationSchema]);

  // Set single value
  const setValue = useCallback((field: keyof T, value: T[keyof T]) => {
    setValuesState(prev => ({ ...prev, [field]: value }));

    // Validate on change if enabled and field is touched
    if (validateOnChange && touched[field]) {
      const result = validateSingleField(field);
      if (!result.isValid) {
        setErrorsState(prev => ({ ...prev, [field]: result.message }));
      } else {
        setErrorsState(prev => {
          const newErrors = { ...prev };
          delete newErrors[field];
          return newErrors;
        });
      }
    }
  }, [validateOnChange, touched, validateSingleField]);

  // Set multiple values
  const setValues = useCallback((newValues: Partial<T>) => {
    setValuesState(prev => ({ ...prev, ...newValues }));
  }, []);

  // Set single error
  const setError = useCallback((field: keyof T, error: string) => {
    setErrorsState(prev => ({ ...prev, [field]: error }));
  }, []);

  // Set multiple errors
  const setErrors = useCallback((newErrors: Partial<Record<keyof T, string>>) => {
    setErrorsState(prev => ({ ...prev, ...newErrors }));
  }, []);

  // Clear single error
  const clearError = useCallback((field: keyof T) => {
    setErrorsState(prev => {
      const newErrors = { ...prev };
      delete newErrors[field];
      return newErrors;
    });
  }, []);

  // Clear all errors
  const clearErrors = useCallback(() => {
    setErrorsState({});
  }, []);

  // Set touched state
  const setTouched = useCallback((field: keyof T, isTouched: boolean = true) => {
    setTouchedState(prev => ({ ...prev, [field]: isTouched }));
  }, []);

  // Set field as touched
  const setFieldTouched = useCallback((field: keyof T) => {
    setTouched(field, true);
  }, [setTouched]);

  // Handle input change
  const handleChange = useCallback((field: keyof T) => {
    return (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
      const value = event.target.type === 'checkbox' 
        ? (event.target as HTMLInputElement).checked 
        : event.target.value;
      setValue(field, value as T[keyof T]);
    };
  }, [setValue]);

  // Handle input blur
  const handleBlur = useCallback((field: keyof T) => {
    return (event: React.FocusEvent<HTMLInputElement | HTMLTextAreaElement>) => {
      setFieldTouched(field);

      // Validate on blur if enabled
      if (validateOnBlur) {
        const result = validateSingleField(field);
        if (!result.isValid) {
          setError(field, result.message || 'Invalid value');
        } else {
          clearError(field);
        }
      }
    };
  }, [validateOnBlur, validateSingleField, setFieldTouched, setError, clearError]);

  // Handle form submission
  const handleSubmit = useCallback((event: React.FormEvent) => {
    event.preventDefault();
    
    if (isSubmitting) return;

    // Mark all fields as touched
    const allTouched = Object.keys(values).reduce((acc, key) => {
      acc[key as keyof T] = true;
      return acc;
    }, {} as Record<keyof T, boolean>);
    setTouchedState(allTouched);

    // Validate form
    const formIsValid = validateEntireForm();
    
    if (formIsValid && onSubmit) {
      setIsSubmitting(true);
      
      Promise.resolve(onSubmit(values))
        .catch((error) => {
          console.error('Form submission error:', error);
          // Handle submission errors here if needed
        })
        .finally(() => {
          setIsSubmitting(false);
        });
    }
  }, [values, isSubmitting, validateEntireForm, onSubmit]);

  // Reset form
  const reset = useCallback((newValues?: T) => {
    const resetValues = newValues || initialValues;
    setValuesState(resetValues);
    setErrorsState({});
    setTouchedState({});
    setIsSubmitting(false);
  }, [initialValues]);

  // Update values when initialValues change
  useEffect(() => {
    setValuesState(initialValues);
  }, [initialValues]);

  return {
    // State
    values,
    errors,
    touched,
    isSubmitting,
    isValid,
    isDirty,
    
    // Actions
    setValue,
    setValues,
    setError,
    setErrors,
    clearError,
    clearErrors,
    setTouched,
    setFieldTouched,
    validateField: validateSingleField,
    validateForm: validateEntireForm,
    handleChange,
    handleBlur,
    handleSubmit,
    reset,
  };
}