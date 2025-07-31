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
}

export interface CreatePolicyRequest {
  clientId: number;
  vehicleId: number;
  insuranceType: 'OC' | 'AC' | 'NNW';
  startDate: string;
  endDate: string;
  discountSurcharge?: number;
}

export interface UpdatePolicyRequest {
  clientId: number;
  vehicleId: number;
  insuranceType: 'OC' | 'AC' | 'NNW';
  startDate: string;
  endDate: string;
  discountSurcharge?: number;
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