import { apiClient } from './apiClient';
import { RatingTable, PremiumBreakdown } from '../types/policy';

export interface PremiumCalculationRequest {
  insuranceType: 'OC' | 'AC' | 'NNW';
  vehicleId: number;
  policyDate: string;
}

class RatingService {
  /**
   * Gets rating tables for a specific insurance type
   */
  async getRatingTables(insuranceType: 'OC' | 'AC' | 'NNW'): Promise<RatingTable[]> {
    const response = await apiClient.get(`/rating/tables/${insuranceType}`);
    return response.data;
  }

  /**
   * Gets rating tables for a specific insurance type and date
   */
  async getRatingTablesForDate(insuranceType: 'OC' | 'AC' | 'NNW', date: string): Promise<RatingTable[]> {
    const response = await apiClient.get(`/rating/tables/${insuranceType}/date/${date}`);
    return response.data;
  }

  /**
   * Calculates premium breakdown for given parameters
   * Note: This is a mock implementation since the backend doesn't have a calculate endpoint yet
   */
  async calculatePremiumBreakdown(request: PremiumCalculationRequest): Promise<PremiumBreakdown> {
    // Mock implementation - in a real scenario, this would call the backend
    const basePremiums = {
      OC: 800,
      AC: 1200,
      NNW: 300
    };

    const basePremium = basePremiums[request.insuranceType];
    
    // Mock rating factors
    const ratingFactors = {
      VEHICLE_AGE: 1.1,
      ENGINE_CAPACITY: 1.05,
      POWER: 0.95,
      [`${request.insuranceType}_COVERAGE`]: 1.0
    };

    let finalPremium = basePremium;
    Object.values(ratingFactors).forEach(factor => {
      finalPremium *= factor;
    });

    return {
      basePremium,
      ratingFactors,
      finalPremium: Math.round(finalPremium * 100) / 100
    };
  }
}

const ratingService = new RatingService();
export default ratingService;