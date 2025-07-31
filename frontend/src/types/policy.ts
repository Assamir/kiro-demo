export interface Policy {
  id: number;
  policyNumber: string;
  clientName: string;
  vehicleRegistration: string;
  insuranceType: 'OC' | 'AC' | 'NNW';
  startDate: string;
  endDate: string;
  premium: number;
  status: 'ACTIVE' | 'CANCELED' | 'EXPIRED';
  policyDetails?: PolicyDetails;
}

export interface PolicyDetails {
  id?: number;
  // OC Insurance fields
  guaranteedSum?: number;
  coverageArea?: string;
  // AC Insurance fields
  acVariant?: 'STANDARD' | 'MAXIMUM';
  sumInsured?: number;
  coverageScope?: string;
  deductible?: number;
  workshopType?: string;
  // NNW Insurance fields
  coveredPersons?: string;
}

export interface CreatePolicyRequest {
  clientId: number;
  vehicleId: number;
  insuranceType: 'OC' | 'AC' | 'NNW';
  startDate: string;
  endDate: string;
  discountSurcharge?: number;
  policyDetails?: PolicyDetails;
}

export interface UpdatePolicyRequest {
  clientId: number;
  vehicleId: number;
  insuranceType: 'OC' | 'AC' | 'NNW';
  startDate: string;
  endDate: string;
  discountSurcharge?: number;
  policyDetails?: PolicyDetails;
}

export interface PremiumBreakdown {
  basePremium: number;
  ratingFactors: Record<string, number>;
  finalPremium: number;
}

export interface RatingTable {
  id: number;
  insuranceType: 'OC' | 'AC' | 'NNW';
  ratingKey: string;
  multiplier: number;
  validFrom: string;
  validTo?: string;
}

export interface Client {
  id: number;
  fullName: string;
  pesel: string;
  address: string;
  email: string;
  phoneNumber: string;
}

export interface Vehicle {
  id: number;
  make: string;
  model: string;
  yearOfManufacture: number;
  registrationNumber: string;
  vin: string;
  engineCapacity: number;
  power: number;
  firstRegistrationDate: string;
}

export interface PolicySearchFilters {
  clientName?: string;
  policyNumber?: string;
  insuranceType?: 'ALL' | 'OC' | 'AC' | 'NNW';
  status?: 'ALL' | 'ACTIVE' | 'CANCELED' | 'EXPIRED';
}