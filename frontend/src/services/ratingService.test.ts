import ratingService from './ratingService';
import apiClient from './apiClient';

// Mock the API client
jest.mock('./apiClient');
const mockApiClient = apiClient as jest.Mocked<typeof apiClient>;

describe('RatingService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('getRatingTables', () => {
    test('fetches rating tables for OC insurance', async () => {
      const mockRatingTables = [
        {
          id: 1,
          insuranceType: 'OC',
          ratingKey: 'VEHICLE_AGE_1',
          multiplier: 1.1,
          validFrom: '2024-01-01',
        },
      ];

      mockApiClient.get.mockResolvedValue({ data: mockRatingTables });

      const result = await ratingService.getRatingTables('OC');

      expect(mockApiClient.get).toHaveBeenCalledWith('/rating/tables/OC');
      expect(result).toEqual(mockRatingTables);
    });

    test('fetches rating tables for AC insurance', async () => {
      const mockRatingTables = [
        {
          id: 2,
          insuranceType: 'AC',
          ratingKey: 'ENGINE_CAPACITY',
          multiplier: 1.05,
          validFrom: '2024-01-01',
        },
      ];

      mockApiClient.get.mockResolvedValue({ data: mockRatingTables });

      const result = await ratingService.getRatingTables('AC');

      expect(mockApiClient.get).toHaveBeenCalledWith('/rating/tables/AC');
      expect(result).toEqual(mockRatingTables);
    });

    test('fetches rating tables for NNW insurance', async () => {
      const mockRatingTables = [
        {
          id: 3,
          insuranceType: 'NNW',
          ratingKey: 'NNW_STANDARD',
          multiplier: 1.0,
          validFrom: '2024-01-01',
        },
      ];

      mockApiClient.get.mockResolvedValue({ data: mockRatingTables });

      const result = await ratingService.getRatingTables('NNW');

      expect(mockApiClient.get).toHaveBeenCalledWith('/rating/tables/NNW');
      expect(result).toEqual(mockRatingTables);
    });
  });

  describe('getRatingTablesForDate', () => {
    test('fetches rating tables for specific date', async () => {
      const mockRatingTables = [
        {
          id: 1,
          insuranceType: 'OC',
          ratingKey: 'VEHICLE_AGE_1',
          multiplier: 1.1,
          validFrom: '2024-01-01',
          validTo: '2024-12-31',
        },
      ];

      mockApiClient.get.mockResolvedValue({ data: mockRatingTables });

      const result = await ratingService.getRatingTablesForDate('OC', '2024-06-15');

      expect(mockApiClient.get).toHaveBeenCalledWith('/rating/tables/OC/date/2024-06-15');
      expect(result).toEqual(mockRatingTables);
    });
  });

  describe('calculatePremiumBreakdown', () => {
    test('calculates premium breakdown for OC insurance', async () => {
      const request = {
        insuranceType: 'OC' as const,
        vehicleId: 1,
        policyDate: '2024-01-01',
      };

      const result = await ratingService.calculatePremiumBreakdown(request);

      expect(result.basePremium).toBe(800);
      expect(result.ratingFactors).toHaveProperty('VEHICLE_AGE');
      expect(result.ratingFactors).toHaveProperty('ENGINE_CAPACITY');
      expect(result.ratingFactors).toHaveProperty('POWER');
      expect(result.ratingFactors).toHaveProperty('OC_COVERAGE');
      expect(result.finalPremium).toBeGreaterThan(0);
      expect(typeof result.finalPremium).toBe('number');
    });

    test('calculates premium breakdown for AC insurance', async () => {
      const request = {
        insuranceType: 'AC' as const,
        vehicleId: 2,
        policyDate: '2024-01-01',
      };

      const result = await ratingService.calculatePremiumBreakdown(request);

      expect(result.basePremium).toBe(1200);
      expect(result.ratingFactors).toHaveProperty('VEHICLE_AGE');
      expect(result.ratingFactors).toHaveProperty('ENGINE_CAPACITY');
      expect(result.ratingFactors).toHaveProperty('POWER');
      expect(result.ratingFactors).toHaveProperty('AC_COVERAGE');
      expect(result.finalPremium).toBeGreaterThan(0);
    });

    test('calculates premium breakdown for NNW insurance', async () => {
      const request = {
        insuranceType: 'NNW' as const,
        vehicleId: 3,
        policyDate: '2024-01-01',
      };

      const result = await ratingService.calculatePremiumBreakdown(request);

      expect(result.basePremium).toBe(300);
      expect(result.ratingFactors).toHaveProperty('VEHICLE_AGE');
      expect(result.ratingFactors).toHaveProperty('ENGINE_CAPACITY');
      expect(result.ratingFactors).toHaveProperty('POWER');
      expect(result.ratingFactors).toHaveProperty('NNW_COVERAGE');
      expect(result.finalPremium).toBeGreaterThan(0);
    });

    test('applies rating factors correctly', async () => {
      const request = {
        insuranceType: 'OC' as const,
        vehicleId: 1,
        policyDate: '2024-01-01',
      };

      const result = await ratingService.calculatePremiumBreakdown(request);

      // Calculate expected final premium
      let expectedPremium = result.basePremium;
      Object.values(result.ratingFactors).forEach(factor => {
        expectedPremium *= factor;
      });

      expect(result.finalPremium).toBeCloseTo(expectedPremium, 2);
    });

    test('rounds final premium to 2 decimal places', async () => {
      const request = {
        insuranceType: 'OC' as const,
        vehicleId: 1,
        policyDate: '2024-01-01',
      };

      const result = await ratingService.calculatePremiumBreakdown(request);

      // Check that the result has at most 2 decimal places
      const decimalPlaces = (result.finalPremium.toString().split('.')[1] || '').length;
      expect(decimalPlaces).toBeLessThanOrEqual(2);
    });

    test('includes all expected rating factors', async () => {
      const request = {
        insuranceType: 'AC' as const,
        vehicleId: 1,
        policyDate: '2024-01-01',
      };

      const result = await ratingService.calculatePremiumBreakdown(request);

      const expectedFactors = ['VEHICLE_AGE', 'ENGINE_CAPACITY', 'POWER', 'AC_COVERAGE'];
      expectedFactors.forEach(factor => {
        expect(result.ratingFactors).toHaveProperty(factor);
        expect(typeof result.ratingFactors[factor]).toBe('number');
      });
    });
  });
});