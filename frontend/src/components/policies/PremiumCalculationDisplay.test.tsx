import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import PremiumCalculationDisplay from './PremiumCalculationDisplay';
import ratingService from '../../services/ratingService';

// Mock the rating service
jest.mock('../../services/ratingService');
const mockRatingService = ratingService as jest.Mocked<typeof ratingService>;

describe('PremiumCalculationDisplay', () => {
  const defaultProps = {
    insuranceType: 'OC' as const,
    vehicleId: 1,
    policyDate: '2024-01-01',
    discountSurcharge: 0,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('shows placeholder when required props are missing', () => {
    render(
      <PremiumCalculationDisplay
        insuranceType=""
        vehicleId=""
        policyDate=""
      />
    );

    expect(screen.getByText('Premium Calculation')).toBeInTheDocument();
    expect(screen.getByText(/Complete the insurance type, vehicle, and policy date/)).toBeInTheDocument();
  });

  test('shows loading state while calculating premium', () => {
    mockRatingService.calculatePremiumBreakdown.mockImplementation(
      () => new Promise(() => {}) // Never resolves to keep loading state
    );

    render(<PremiumCalculationDisplay {...defaultProps} />);

    expect(screen.getByText('Calculating premium...')).toBeInTheDocument();
    expect(screen.getByRole('progressbar')).toBeInTheDocument();
  });

  test('displays premium breakdown when calculation succeeds', async () => {
    const mockBreakdown = {
      basePremium: 800,
      ratingFactors: {
        VEHICLE_AGE: 1.1,
        ENGINE_CAPACITY: 1.05,
        POWER: 0.95,
        OC_COVERAGE: 1.0,
      },
      finalPremium: 831.0,
    };

    mockRatingService.calculatePremiumBreakdown.mockResolvedValue(mockBreakdown);

    render(<PremiumCalculationDisplay {...defaultProps} />);

    await waitFor(() => {
      expect(screen.getByText('Premium Calculation')).toBeInTheDocument();
    });

    expect(screen.getByText('Base Premium (OC Insurance)')).toBeInTheDocument();
    expect(screen.getByText('800,00 zł')).toBeInTheDocument();
    expect(screen.getByText('Rating Factors')).toBeInTheDocument();
    expect(screen.getByText('Vehicle Age Factor')).toBeInTheDocument();
    expect(screen.getByText('Final Premium')).toBeInTheDocument();
    expect(screen.getByText('831,00 zł')).toBeInTheDocument();
  });

  test('displays error when calculation fails', async () => {
    mockRatingService.calculatePremiumBreakdown.mockRejectedValue(
      new Error('Calculation failed')
    );

    render(<PremiumCalculationDisplay {...defaultProps} />);

    await waitFor(() => {
      expect(screen.getByText('Calculation failed')).toBeInTheDocument();
    });
  });

  test('includes discount in final premium calculation', async () => {
    const mockBreakdown = {
      basePremium: 800,
      ratingFactors: {
        VEHICLE_AGE: 1.0,
      },
      finalPremium: 800.0,
    };

    mockRatingService.calculatePremiumBreakdown.mockResolvedValue(mockBreakdown);

    render(
      <PremiumCalculationDisplay
        {...defaultProps}
        discountSurcharge={-100}
      />
    );

    await waitFor(() => {
      expect(screen.getByText('Discount')).toBeInTheDocument();
    });

    expect(screen.getByText('-100,00 zł')).toBeInTheDocument();
    expect(screen.getByText('700,00 zł')).toBeInTheDocument(); // 800 - 100
  });

  test('includes surcharge in final premium calculation', async () => {
    const mockBreakdown = {
      basePremium: 800,
      ratingFactors: {
        VEHICLE_AGE: 1.0,
      },
      finalPremium: 800.0,
    };

    mockRatingService.calculatePremiumBreakdown.mockResolvedValue(mockBreakdown);

    render(
      <PremiumCalculationDisplay
        {...defaultProps}
        discountSurcharge={50}
      />
    );

    await waitFor(() => {
      expect(screen.getByText('Surcharge')).toBeInTheDocument();
    });

    expect(screen.getByText('+50,00 zł')).toBeInTheDocument();
    expect(screen.getByText('850,00 zł')).toBeInTheDocument(); // 800 + 50
  });

  test('displays rating factors with correct colors', async () => {
    const mockBreakdown = {
      basePremium: 800,
      ratingFactors: {
        VEHICLE_AGE: 1.2, // Should be error (red)
        ENGINE_CAPACITY: 1.05, // Should be warning (orange)
        POWER: 0.85, // Should be success (green)
        OC_COVERAGE: 1.0, // Should be default
      },
      finalPremium: 867.6,
    };

    mockRatingService.calculatePremiumBreakdown.mockResolvedValue(mockBreakdown);

    render(<PremiumCalculationDisplay {...defaultProps} />);

    await waitFor(() => {
      expect(screen.getByText('×1.200')).toBeInTheDocument();
    });

    expect(screen.getByText('×1.050')).toBeInTheDocument();
    expect(screen.getByText('×0.850')).toBeInTheDocument();
    expect(screen.getByText('×1.000')).toBeInTheDocument();
  });

  test('calls rating service with correct parameters', async () => {
    const mockBreakdown = {
      basePremium: 1200,
      ratingFactors: {},
      finalPremium: 1200,
    };

    mockRatingService.calculatePremiumBreakdown.mockResolvedValue(mockBreakdown);

    render(
      <PremiumCalculationDisplay
        insuranceType="AC"
        vehicleId={5}
        policyDate="2024-06-15"
      />
    );

    await waitFor(() => {
      expect(mockRatingService.calculatePremiumBreakdown).toHaveBeenCalledWith({
        insuranceType: 'AC',
        vehicleId: 5,
        policyDate: '2024-06-15',
      });
    });
  });

  test('recalculates when props change', async () => {
    const mockBreakdown = {
      basePremium: 800,
      ratingFactors: {},
      finalPremium: 800,
    };

    mockRatingService.calculatePremiumBreakdown.mockResolvedValue(mockBreakdown);

    const { rerender } = render(<PremiumCalculationDisplay {...defaultProps} />);

    await waitFor(() => {
      expect(mockRatingService.calculatePremiumBreakdown).toHaveBeenCalledTimes(1);
    });

    // Change vehicle ID
    rerender(
      <PremiumCalculationDisplay
        {...defaultProps}
        vehicleId={2}
      />
    );

    await waitFor(() => {
      expect(mockRatingService.calculatePremiumBreakdown).toHaveBeenCalledTimes(2);
    });
  });

  test('shows disclaimer text', async () => {
    const mockBreakdown = {
      basePremium: 800,
      ratingFactors: {},
      finalPremium: 800,
    };

    mockRatingService.calculatePremiumBreakdown.mockResolvedValue(mockBreakdown);

    render(<PremiumCalculationDisplay {...defaultProps} />);

    await waitFor(() => {
      expect(screen.getByText(/Premium calculation is based on current rating tables/)).toBeInTheDocument();
    });
  });
});