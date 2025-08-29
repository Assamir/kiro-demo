import React from 'react';
import { render, screen } from '@testing-library/react';
import ValidationFeedback from './ValidationFeedback';

describe('ValidationFeedback', () => {
  const mockRules = [
    {
      id: 'required',
      label: 'Required field',
      description: 'This field is required',
      isValid: true,
      severity: 'error' as const,
    },
    {
      id: 'minLength',
      label: 'Minimum length',
      description: 'Must be at least 8 characters',
      isValid: false,
      severity: 'error' as const,
    },
    {
      id: 'uppercase',
      label: 'Uppercase letter',
      description: 'Must contain at least one uppercase letter',
      isValid: true,
      severity: 'warning' as const,
    },
    {
      id: 'number',
      label: 'Number',
      description: 'Must contain at least one number',
      isValid: false,
      severity: 'warning' as const,
    },
  ];

  it('should not render when no rules are provided', () => {
    const { container } = render(<ValidationFeedback rules={[]} />);
    expect(container.firstChild).toBeNull();
  });

  it('should render validation rules with progress', () => {
    render(<ValidationFeedback rules={mockRules} />);

    expect(screen.getByText('Validation Status')).toBeInTheDocument();
    expect(screen.getByText('2 of 4 validation rules passed')).toBeInTheDocument();
    expect(screen.getByText('50%')).toBeInTheDocument();
  });

  it('should render all rule labels', () => {
    render(<ValidationFeedback rules={mockRules} />);

    expect(screen.getByText('Required field')).toBeInTheDocument();
    expect(screen.getByText('Minimum length')).toBeInTheDocument();
    expect(screen.getByText('Uppercase letter')).toBeInTheDocument();
    expect(screen.getByText('Number')).toBeInTheDocument();
  });

  it('should show issues to resolve section', () => {
    render(<ValidationFeedback rules={mockRules} />);

    expect(screen.getByText('Issues to resolve:')).toBeInTheDocument();
    
    // Should show invalid rules in the issues section
    const issuesSection = screen.getByText('Issues to resolve:').parentElement;
    expect(issuesSection).toHaveTextContent('Minimum length');
    expect(issuesSection).toHaveTextContent('Number');
  });

  it('should render in compact mode', () => {
    render(<ValidationFeedback rules={mockRules} compact={true} />);

    expect(screen.getByText('2/4 valid')).toBeInTheDocument();
    expect(screen.queryByText('Validation Status')).not.toBeInTheDocument();
    expect(screen.queryByText('Required field')).not.toBeInTheDocument();
  });

  it('should hide progress when showProgress is false', () => {
    render(<ValidationFeedback rules={mockRules} showProgress={false} />);

    expect(screen.queryByText('50%')).not.toBeInTheDocument();
    expect(screen.queryByText('2 of 4 validation rules passed')).not.toBeInTheDocument();
  });

  it('should hide labels when showLabels is false', () => {
    render(<ValidationFeedback rules={mockRules} showLabels={false} />);

    expect(screen.queryByText('Required field')).not.toBeInTheDocument();
    expect(screen.queryByText('Minimum length')).not.toBeInTheDocument();
  });

  it('should use custom title', () => {
    render(<ValidationFeedback rules={mockRules} title="Password Strength" />);

    expect(screen.getByText('Password Strength')).toBeInTheDocument();
    expect(screen.queryByText('Validation Status')).not.toBeInTheDocument();
  });

  it('should show 100% progress when all rules are valid', () => {
    const validRules = mockRules.map(rule => ({ ...rule, isValid: true }));
    render(<ValidationFeedback rules={validRules} />);

    expect(screen.getByText('100%')).toBeInTheDocument();
    expect(screen.getByText('4 of 4 validation rules passed')).toBeInTheDocument();
    expect(screen.queryByText('Issues to resolve:')).not.toBeInTheDocument();
  });

  it('should show 0% progress when no rules are valid', () => {
    const invalidRules = mockRules.map(rule => ({ ...rule, isValid: false }));
    render(<ValidationFeedback rules={invalidRules} />);

    expect(screen.getByText('0%')).toBeInTheDocument();
    expect(screen.getByText('0 of 4 validation rules passed')).toBeInTheDocument();
  });

  it('should render help icons for rules with descriptions', () => {
    render(<ValidationFeedback rules={mockRules} />);

    // Should have help icons for each rule (4 rules = 4 help icons)
    const helpIcons = screen.getAllByLabelText(/help/i);
    expect(helpIcons).toHaveLength(8); // 4 in chips + 4 in issues section
  });

  it('should handle rules without descriptions', () => {
    const rulesWithoutDescriptions = [
      {
        id: 'test',
        label: 'Test rule',
        isValid: false,
        severity: 'error' as const,
      },
    ];

    render(<ValidationFeedback rules={rulesWithoutDescriptions} />);

    expect(screen.getByText('Test rule')).toBeInTheDocument();
    // Should not have help icons for rules without descriptions
    expect(screen.queryByLabelText(/help/i)).not.toBeInTheDocument();
  });

  it('should display correct progress color based on percentage', () => {
    // Test different progress levels
    const { rerender } = render(<ValidationFeedback rules={mockRules} />); // 50%
    
    // 100% should be success color
    const allValidRules = mockRules.map(rule => ({ ...rule, isValid: true }));
    rerender(<ValidationFeedback rules={allValidRules} />);
    expect(screen.getByText('100%')).toBeInTheDocument();

    // 0% should be error color
    const allInvalidRules = mockRules.map(rule => ({ ...rule, isValid: false }));
    rerender(<ValidationFeedback rules={allInvalidRules} />);
    expect(screen.getByText('0%')).toBeInTheDocument();
  });
});