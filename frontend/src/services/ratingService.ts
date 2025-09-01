import { apiClient } from './apiClient';
import { RatingTable, PremiumBreakdown } from '../types/policy';
import { withRetry } from '../utils/retryUtils';

export interface PremiumCalculationRequest {
  insuranceType: 'OC' | 'AC' | 'NNW';
  vehicleId: number;
  policyDate: string;
  clientAge?: number;
  vehicleAge?: number;
  engineCapacity?: number;
  power?: number;
}

export class RatingDataMissingError extends Error {
  constructor(message: string, public missingData: string[]) {
    super(message);
    this.name = 'RatingDataMissingError';
  }
}

export class PremiumCalculationError extends Error {
  constructor(message: string, public originalError?: any) {
    super(message);
    this.name = 'PremiumCalculationError';
  }
}

class RatingService {
  /**
   * Gets rating tables for a specific insurance type with comprehensive error handling
   */
  async getRatingTables(insuranceType: 'OC' | 'AC' | 'NNW'): Promise<RatingTable[]> {
    try {
      const response = await withRetry(
        () => apiClient.get(`/rating/tables/${insuranceType}`),
        {
          maxAttempts: 3,
          baseDelay: 1000,
          retryCondition: (error: any) => {
            // Retry on network errors or server errors, but not on 404 (no rating tables found)
            return error?.status !== 404 && (error?.retryable === true || error?.status >= 500);
          }
        }
      );
      
      if (!response.data || response.data.length === 0) {
        throw new RatingDataMissingError(
          `No rating tables found for insurance type ${insuranceType}`,
          [`rating_tables_${insuranceType.toLowerCase()}`]
        );
      }
      
      return response.data;
    } catch (error: any) {
      if (error instanceof RatingDataMissingError) {
        throw error;
      }
      
      if (error?.status === 404) {
        throw new RatingDataMissingError(
          `Rating tables not available for insurance type ${insuranceType}`,
          [`rating_tables_${insuranceType.toLowerCase()}`]
        );
      }
      
      throw new PremiumCalculationError(
        `Failed to retrieve rating tables for ${insuranceType}: ${error?.message || 'Unknown error'}`,
        error
      );
    }
  }

  /**
   * Gets rating tables for a specific insurance type and date with error handling
   */
  async getRatingTablesForDate(insuranceType: 'OC' | 'AC' | 'NNW', date: string): Promise<RatingTable[]> {
    try {
      const response = await withRetry(
        () => apiClient.get(`/rating/tables/${insuranceType}/date/${date}`),
        {
          maxAttempts: 3,
          baseDelay: 1000,
          retryCondition: (error: any) => error?.status !== 404 && error?.retryable === true
        }
      );
      
      if (!response.data || response.data.length === 0) {
        throw new RatingDataMissingError(
          `No rating tables found for insurance type ${insuranceType} on date ${date}`,
          [`rating_tables_${insuranceType.toLowerCase()}_${date}`]
        );
      }
      
      return response.data;
    } catch (error: any) {
      if (error instanceof RatingDataMissingError) {
        throw error;
      }
      
      if (error?.status === 404) {
        throw new RatingDataMissingError(
          `Rating tables not available for insurance type ${insuranceType} on date ${date}`,
          [`rating_tables_${insuranceType.toLowerCase()}_${date}`]
        );
      }
      
      throw new PremiumCalculationError(
        `Failed to retrieve rating tables for ${insuranceType} on ${date}: ${error?.message || 'Unknown error'}`,
        error
      );
    }
  }

  /**
   * Validates that all required rating data is available for premium calculation
   */
  private validateRatingData(request: PremiumCalculationRequest, ratingTables: RatingTable[]): void {
    const missingData: string[] = [];
    
    // Check for required rating factors based on insurance type
    const requiredFactors = this.getRequiredRatingFactors(request.insuranceType);
    
    for (const factor of requiredFactors) {
      const hasRatingTable = ratingTables.some(table => 
        table.ratingKey === factor && 
        this.isRatingTableValid(table, request.policyDate)
      );
      
      if (!hasRatingTable) {
        missingData.push(factor);
      }
    }
    
    // Check for required request data
    if (request.insuranceType === 'OC' || request.insuranceType === 'AC') {
      if (!request.vehicleAge && request.vehicleAge !== 0) missingData.push('vehicle_age');
      if (!request.engineCapacity) missingData.push('engine_capacity');
      if (!request.power) missingData.push('power');
    }
    
    if (request.insuranceType === 'AC' && (!request.clientAge && request.clientAge !== 0)) {
      missingData.push('client_age');
    }
    
    if (missingData.length > 0) {
      throw new RatingDataMissingError(
        `Missing required rating data for ${request.insuranceType} premium calculation: ${missingData.join(', ')}`,
        missingData
      );
    }
  }

  /**
   * Gets required rating factors for each insurance type
   */
  private getRequiredRatingFactors(insuranceType: 'OC' | 'AC' | 'NNW'): string[] {
    switch (insuranceType) {
      case 'OC':
        return ['VEHICLE_AGE', 'ENGINE_CAPACITY', 'POWER', 'OC_COVERAGE'];
      case 'AC':
        return ['VEHICLE_AGE', 'ENGINE_CAPACITY', 'POWER', 'CLIENT_AGE', 'AC_COVERAGE'];
      case 'NNW':
        return ['NNW_COVERAGE'];
      default:
        return [];
    }
  }

  /**
   * Checks if a rating table is valid for the given date
   */
  private isRatingTableValid(table: RatingTable, policyDate: string): boolean {
    const date = new Date(policyDate);
    const validFrom = new Date(table.validFrom);
    const validTo = table.validTo ? new Date(table.validTo) : null;
    
    return date >= validFrom && (!validTo || date <= validTo);
  }

  /**
   * Calculates premium breakdown for given parameters with comprehensive error handling
   */
  async calculatePremiumBreakdown(request: PremiumCalculationRequest): Promise<PremiumBreakdown> {
    try {
      // Validate input parameters
      if (!request.insuranceType || !request.vehicleId || !request.policyDate) {
        throw new PremiumCalculationError('Missing required parameters for premium calculation');
      }

      // For now, use mock calculation since the backend rating API requires authentication
      // and the proper approach would be to have a dedicated premium calculation endpoint
      return await this.calculateMockPremiumWithVehicleData(request);
      
    } catch (error: any) {
      if (error instanceof RatingDataMissingError || error instanceof PremiumCalculationError) {
        throw error;
      }
      
      throw new PremiumCalculationError(
        `Premium calculation failed: ${error?.message || 'Unknown error'}`,
        error
      );
    }
  }

  /**
   * Enhanced mock premium calculation with proper error handling
   */
  private async calculateMockPremium(request: PremiumCalculationRequest, ratingTables: RatingTable[]): Promise<PremiumBreakdown> {
    const basePremiums = {
      OC: 800,
      AC: 1200,
      NNW: 300
    };

    const basePremium = basePremiums[request.insuranceType];
    const ratingFactors: Record<string, number> = {};

    try {
      // Apply rating factors from tables
      for (const table of ratingTables) {
        if (this.isRatingTableValid(table, request.policyDate)) {
          ratingFactors[table.ratingKey] = table.multiplier;
        }
      }

      // Calculate final premium
      let finalPremium = basePremium;
      Object.values(ratingFactors).forEach(factor => {
        finalPremium *= factor;
      });

      return {
        basePremium,
        ratingFactors,
        finalPremium: Math.round(finalPremium * 100) / 100
      };
    } catch (error: any) {
      throw new PremiumCalculationError(
        `Error during premium calculation: ${error?.message || 'Calculation failed'}`,
        error
      );
    }
  }

  /**
   * Mock premium calculation that doesn't require rating table data
   * This is a temporary solution until proper backend integration is implemented
   */
  private async calculateMockPremiumWithVehicleData(request: PremiumCalculationRequest): Promise<PremiumBreakdown> {
    const basePremiums = {
      OC: 800,
      AC: 1200,
      NNW: 300
    };

    const basePremium = basePremiums[request.insuranceType];
    const ratingFactors: Record<string, number> = {};

    try {
      // Calculate vehicle age from current year (assuming 2024)
      const currentYear = new Date().getFullYear();
      const vehicleAge = request.vehicleAge || (currentYear - 2020); // Default to 4 years old
      
      // Mock rating factors based on typical insurance logic
      if (request.insuranceType === 'OC' || request.insuranceType === 'AC') {
        // Vehicle age factor
        if (vehicleAge <= 2) {
          ratingFactors['VEHICLE_AGE'] = 0.95;
        } else if (vehicleAge <= 5) {
          ratingFactors['VEHICLE_AGE'] = 1.0;
        } else if (vehicleAge <= 10) {
          ratingFactors['VEHICLE_AGE'] = 1.1;
        } else {
          ratingFactors['VEHICLE_AGE'] = 1.3;
        }

        // Engine capacity factor (mock values)
        const engineCapacity = request.engineCapacity || 1600;
        if (engineCapacity <= 1200) {
          ratingFactors['ENGINE_CAPACITY'] = 0.85;
        } else if (engineCapacity <= 1800) {
          ratingFactors['ENGINE_CAPACITY'] = 1.0;
        } else if (engineCapacity <= 2500) {
          ratingFactors['ENGINE_CAPACITY'] = 1.2;
        } else {
          ratingFactors['ENGINE_CAPACITY'] = 1.5;
        }

        // Power factor (mock values)
        const power = request.power || 132;
        if (power <= 100) {
          ratingFactors['POWER'] = 0.9;
        } else if (power <= 150) {
          ratingFactors['POWER'] = 1.0;
        } else if (power <= 200) {
          ratingFactors['POWER'] = 1.3;
        } else {
          ratingFactors['POWER'] = 1.6;
        }

        // Coverage factor
        ratingFactors[`${request.insuranceType}_COVERAGE`] = 1.0;
      }

      if (request.insuranceType === 'AC' && request.clientAge) {
        // Client age factor for AC insurance
        if (request.clientAge < 25) {
          ratingFactors['CLIENT_AGE'] = 1.4;
        } else if (request.clientAge < 35) {
          ratingFactors['CLIENT_AGE'] = 1.1;
        } else if (request.clientAge < 55) {
          ratingFactors['CLIENT_AGE'] = 1.0;
        } else {
          ratingFactors['CLIENT_AGE'] = 1.2;
        }
      }

      if (request.insuranceType === 'NNW') {
        ratingFactors['NNW_COVERAGE'] = 1.0;
      }

      // Calculate final premium
      let finalPremium = basePremium;
      Object.values(ratingFactors).forEach(factor => {
        finalPremium *= factor;
      });

      return {
        basePremium,
        ratingFactors,
        finalPremium: Math.round(finalPremium * 100) / 100
      };
    } catch (error: any) {
      throw new PremiumCalculationError(
        `Error during premium calculation: ${error?.message || 'Calculation failed'}`,
        error
      );
    }
  }

  /**
   * Checks if rating data is available for a specific scenario
   */
  async isRatingDataAvailable(insuranceType: 'OC' | 'AC' | 'NNW', policyDate: string): Promise<{
    available: boolean;
    missingData: string[];
    message?: string;
  }> {
    try {
      const ratingTables = await this.getRatingTablesForDate(insuranceType, policyDate);
      const requiredFactors = this.getRequiredRatingFactors(insuranceType);
      const missingData: string[] = [];

      for (const factor of requiredFactors) {
        const hasRatingTable = ratingTables.some(table => 
          table.ratingKey === factor && 
          this.isRatingTableValid(table, policyDate)
        );
        
        if (!hasRatingTable) {
          missingData.push(factor);
        }
      }

      return {
        available: missingData.length === 0,
        missingData,
        message: missingData.length > 0 
          ? `Missing rating data: ${missingData.join(', ')}` 
          : undefined
      };
    } catch (error: any) {
      return {
        available: false,
        missingData: ['all'],
        message: error?.message || 'Unable to check rating data availability'
      };
    }
  }
}

const ratingService = new RatingService();
export default ratingService;