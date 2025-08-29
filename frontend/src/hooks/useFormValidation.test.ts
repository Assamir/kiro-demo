import { renderHook, act } from '@testing-library/react';
import { useFormValidation } from './useFormValidation';
import { ValidationSchema } from '../utils/validators';

interface TestFormData {
  email: string;
  password: string;
  age: number;
}

const mockValidationSchema: ValidationSchema<TestFormData> = {
  email: {
    required: true,
    rules: [
      {
        validator: (value: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value),
        message: 'Please enter a valid email address',
      },
    ],
  },
  password: {
    required: true,
    rules: [
      {
        validator: (value: string) => value.length >= 8,
        message: 'Password must be at least 8 characters long',
      },
    ],
  },
  age: {
    required: true,
    rules: [
      {
        validator: (value: number) => value >= 18,
        message: 'Age must be at least 18',
      },
    ],
  },
};

const initialValues: TestFormData = {
  email: '',
  password: '',
  age: 0,
};

describe('useFormValidation', () => {
  it('should initialize with initial values', () => {
    const { result } = renderHook(() =>
      useFormValidation({
        initialValues,
        validationSchema: mockValidationSchema,
      })
    );

    expect(result.current.values).toEqual(initialValues);
    expect(result.current.errors).toEqual({});
    expect(result.current.touched).toEqual({});
    expect(result.current.isSubmitting).toBe(false);
    expect(result.current.isValid).toBe(true);
    expect(result.current.isDirty).toBe(false);
  });

  it('should update values when setValue is called', () => {
    const { result } = renderHook(() =>
      useFormValidation({
        initialValues,
        validationSchema: mockValidationSchema,
      })
    );

    act(() => {
      result.current.setValue('email', 'test@example.com');
    });

    expect(result.current.values.email).toBe('test@example.com');
    expect(result.current.isDirty).toBe(true);
  });

  it('should validate field on blur when validateOnBlur is true', () => {
    const { result } = renderHook(() =>
      useFormValidation({
        initialValues,
        validationSchema: mockValidationSchema,
        validateOnBlur: true,
      })
    );

    // Simulate blur event with invalid email
    act(() => {
      result.current.setValue('email', 'invalid-email');
    });

    const mockBlurEvent = {
      target: { value: 'invalid-email' },
    } as React.FocusEvent<HTMLInputElement>;

    act(() => {
      result.current.handleBlur('email')(mockBlurEvent);
    });

    expect(result.current.errors.email).toBe('Please enter a valid email address');
    expect(result.current.touched.email).toBe(true);
    expect(result.current.isValid).toBe(false);
  });

  it('should validate field on change when validateOnChange is true and field is touched', () => {
    const { result } = renderHook(() =>
      useFormValidation({
        initialValues: { ...initialValues, email: 'invalid-email' }, // Start with invalid email
        validationSchema: mockValidationSchema,
        validateOnChange: true,
      })
    );

    // First touch the field
    act(() => {
      result.current.setFieldTouched('email');
    });

    // Then change the value to another invalid email
    act(() => {
      result.current.setValue('email', 'another-invalid');
    });

    expect(result.current.errors.email).toBe('Please enter a valid email address');
  });

  it('should clear errors when valid value is entered', () => {
    const { result } = renderHook(() =>
      useFormValidation({
        initialValues: { ...initialValues, email: 'invalid-email' }, // Start with invalid email
        validationSchema: mockValidationSchema,
        validateOnChange: true,
      })
    );

    // Touch field to enable validation
    act(() => {
      result.current.setFieldTouched('email');
    });

    // Trigger validation by setting the same invalid value
    act(() => {
      result.current.setValue('email', 'invalid-email');
    });

    expect(result.current.errors.email).toBe('Please enter a valid email address');

    // Set valid value
    act(() => {
      result.current.setValue('email', 'valid@example.com');
    });

    expect(result.current.errors.email).toBeUndefined();
  });

  it('should validate entire form on submit', () => {
    const mockOnSubmit = jest.fn();
    const { result } = renderHook(() =>
      useFormValidation({
        initialValues,
        validationSchema: mockValidationSchema,
        onSubmit: mockOnSubmit,
      })
    );

    const mockSubmitEvent = {
      preventDefault: jest.fn(),
    } as unknown as React.FormEvent;

    act(() => {
      result.current.handleSubmit(mockSubmitEvent);
    });

    expect(mockSubmitEvent.preventDefault).toHaveBeenCalled();
    expect(result.current.errors.email).toBe('email is required');
    expect(result.current.errors.password).toBe('password is required');
    expect(result.current.errors.age).toBe('Age must be at least 18'); // Age is 0, so it fails the rule validation
    expect(mockOnSubmit).not.toHaveBeenCalled();
  });

  it('should call onSubmit when form is valid', () => {
    const mockOnSubmit = jest.fn();
    const validValues: TestFormData = {
      email: 'test@example.com',
      password: 'password123',
      age: 25,
    };

    const { result } = renderHook(() =>
      useFormValidation({
        initialValues: validValues,
        validationSchema: mockValidationSchema,
        onSubmit: mockOnSubmit,
      })
    );

    const mockSubmitEvent = {
      preventDefault: jest.fn(),
    } as unknown as React.FormEvent;

    act(() => {
      result.current.handleSubmit(mockSubmitEvent);
    });

    expect(mockOnSubmit).toHaveBeenCalledWith(validValues);
  });

  it('should handle async onSubmit', async () => {
    const mockOnSubmit = jest.fn().mockResolvedValue(undefined);
    const validValues: TestFormData = {
      email: 'test@example.com',
      password: 'password123',
      age: 25,
    };

    const { result } = renderHook(() =>
      useFormValidation({
        initialValues: validValues,
        validationSchema: mockValidationSchema,
        onSubmit: mockOnSubmit,
      })
    );

    const mockSubmitEvent = {
      preventDefault: jest.fn(),
    } as unknown as React.FormEvent;

    act(() => {
      result.current.handleSubmit(mockSubmitEvent);
    });

    expect(result.current.isSubmitting).toBe(true);

    // Wait for async operation to complete
    await act(async () => {
      await Promise.resolve();
    });

    expect(result.current.isSubmitting).toBe(false);
  });

  it('should reset form to initial values', () => {
    const { result } = renderHook(() =>
      useFormValidation({
        initialValues,
        validationSchema: mockValidationSchema,
      })
    );

    // Change values and add errors
    act(() => {
      result.current.setValue('email', 'test@example.com');
      result.current.setError('email', 'Some error');
      result.current.setFieldTouched('email');
    });

    expect(result.current.values.email).toBe('test@example.com');
    expect(result.current.errors.email).toBe('Some error');
    expect(result.current.touched.email).toBe(true);

    // Reset form
    act(() => {
      result.current.reset();
    });

    expect(result.current.values).toEqual(initialValues);
    expect(result.current.errors).toEqual({});
    expect(result.current.touched).toEqual({});
  });

  it('should reset form to new values', () => {
    const newValues: TestFormData = {
      email: 'new@example.com',
      password: 'newpassword',
      age: 30,
    };

    const { result } = renderHook(() =>
      useFormValidation({
        initialValues,
        validationSchema: mockValidationSchema,
      })
    );

    act(() => {
      result.current.reset(newValues);
    });

    expect(result.current.values).toEqual(newValues);
  });

  it('should handle change events correctly', () => {
    const { result } = renderHook(() =>
      useFormValidation({
        initialValues,
        validationSchema: mockValidationSchema,
      })
    );

    const mockChangeEvent = {
      target: { value: 'test@example.com', type: 'text' },
    } as React.ChangeEvent<HTMLInputElement>;

    act(() => {
      result.current.handleChange('email')(mockChangeEvent);
    });

    expect(result.current.values.email).toBe('test@example.com');
  });

  it('should handle checkbox change events correctly', () => {
    interface CheckboxFormData {
      isChecked: boolean;
    }

    const checkboxSchema: ValidationSchema<CheckboxFormData> = {
      isChecked: { required: true },
    };

    const checkboxInitialValues: CheckboxFormData = {
      isChecked: false,
    };

    const { result } = renderHook(() =>
      useFormValidation({
        initialValues: checkboxInitialValues,
        validationSchema: checkboxSchema,
      })
    );

    const mockCheckboxEvent = {
      target: { checked: true, type: 'checkbox' },
    } as React.ChangeEvent<HTMLInputElement>;

    act(() => {
      result.current.handleChange('isChecked')(mockCheckboxEvent);
    });

    expect(result.current.values.isChecked).toBe(true);
  });

  it('should set multiple values at once', () => {
    const { result } = renderHook(() =>
      useFormValidation({
        initialValues,
        validationSchema: mockValidationSchema,
      })
    );

    const newValues = {
      email: 'test@example.com',
      password: 'password123',
    };

    act(() => {
      result.current.setValues(newValues);
    });

    expect(result.current.values.email).toBe('test@example.com');
    expect(result.current.values.password).toBe('password123');
    expect(result.current.values.age).toBe(0); // Should remain unchanged
  });

  it('should set and clear errors manually', () => {
    const { result } = renderHook(() =>
      useFormValidation({
        initialValues,
        validationSchema: mockValidationSchema,
      })
    );

    act(() => {
      result.current.setError('email', 'Custom error');
    });

    expect(result.current.errors.email).toBe('Custom error');

    act(() => {
      result.current.clearError('email');
    });

    expect(result.current.errors.email).toBeUndefined();
  });

  it('should clear all errors', () => {
    const { result } = renderHook(() =>
      useFormValidation({
        initialValues,
        validationSchema: mockValidationSchema,
      })
    );

    act(() => {
      result.current.setErrors({
        email: 'Email error',
        password: 'Password error',
      });
    });

    expect(Object.keys(result.current.errors)).toHaveLength(2);

    act(() => {
      result.current.clearErrors();
    });

    expect(result.current.errors).toEqual({});
  });
});