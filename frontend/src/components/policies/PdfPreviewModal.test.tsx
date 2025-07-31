import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import PdfPreviewModal from './PdfPreviewModal';
import { policyService } from '../../services/policyService';
import { Policy } from '../../types/policy';

// Mock the policy service
jest.mock('../../services/policyService');
const mockPolicyService = policyService as jest.Mocked<typeof policyService>;

// Mock URL.createObjectURL and URL.revokeObjectURL
const mockCreateObjectURL = jest.fn();
const mockRevokeObjectURL = jest.fn();
global.URL.createObjectURL = mockCreateObjectURL;
global.URL.revokeObjectURL = mockRevokeObjectURL;

// Mock document.createElement and related DOM methods
const mockClick = jest.fn();
const mockAppendChild = jest.fn();
const mockRemoveChild = jest.fn();
const mockCreateElement = jest.fn(() => ({
  href: '',
  download: '',
  click: mockClick,
}));
global.document.createElement = mockCreateElement;
global.document.body.appendChild = mockAppendChild;
global.document.body.removeChild = mockRemoveChild;

describe('PdfPreviewModal', () => {
  const mockPolicy: Policy = {
    id: 1,
    policyNumber: 'POL-2024-001',
    clientName: 'John Doe',
    vehicleRegistration: 'ABC123',
    insuranceType: 'OC',
    startDate: '2024-01-01',
    endDate: '2024-12-31',
    premium: 1200,
    status: 'ACTIVE',
    policyDetails: {},
  };

  const mockOnClose = jest.fn();
  const mockPdfBlob = new Blob(['PDF content'], { type: 'application/pdf' });

  beforeEach(() => {
    jest.clearAllMocks();
    mockCreateObjectURL.mockReturnValue('blob:mock-url');
  });

  it('should render modal when open and call PDF service', async () => {
    mockPolicyService.generatePolicyPdf.mockResolvedValue(mockPdfBlob);

    render(
      <PdfPreviewModal
        open={true}
        onClose={mockOnClose}
        policy={mockPolicy}
      />
    );

    expect(screen.getByText('Policy PDF Preview')).toBeInTheDocument();
    expect(screen.getByText('- POL-2024-001')).toBeInTheDocument();
    
    await waitFor(() => {
      expect(mockPolicyService.generatePolicyPdf).toHaveBeenCalledWith(1);
    });
  });

  it('should display error message when PDF generation fails', async () => {
    const errorMessage = 'PDF generation failed';
    mockPolicyService.generatePolicyPdf.mockRejectedValue(
      new Error(errorMessage)
    );

    render(
      <PdfPreviewModal
        open={true}
        onClose={mockOnClose}
        policy={mockPolicy}
      />
    );

    await waitFor(() => {
      expect(screen.getByText('PDF Generation Failed')).toBeInTheDocument();
    });

    expect(screen.getByText(errorMessage)).toBeInTheDocument();
  });

  it('should call onClose when close button is clicked', () => {
    render(
      <PdfPreviewModal
        open={true}
        onClose={mockOnClose}
        policy={mockPolicy}
      />
    );

    fireEvent.click(screen.getByText('Close'));
    expect(mockOnClose).toHaveBeenCalled();
  });

  it('should not generate PDF when policy is null', () => {
    render(
      <PdfPreviewModal
        open={true}
        onClose={mockOnClose}
        policy={null}
      />
    );

    expect(mockPolicyService.generatePolicyPdf).not.toHaveBeenCalled();
  });
});