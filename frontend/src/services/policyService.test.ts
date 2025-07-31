import { policyService } from './policyService';
import { apiClient } from './apiClient';
import { Policy, CreatePolicyRequest, UpdatePolicyRequest } from '../types/policy';

// Mock the apiClient
jest.mock('./apiClient');
const mockedApiClient = apiClient as jest.Mocked<typeof apiClient>;

describe('policyService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('getAllPolicies', () => {
    test('should fetch all policies', async () => {
      const mockPolicies: Policy[] = [
        {
          id: 1,
          policyNumber: 'POL-2024-001',
          clientName: 'John Doe',
          vehicleRegistration: 'ABC123',
          insuranceType: 'OC',
          startDate: '2024-01-01',
          endDate: '2024-12-31',
          premium: 1200.00,
          status: 'ACTIVE',
        },
      ];

      mockedApiClient.get.mockResolvedValue({ data: mockPolicies });

      const result = await policyService.getAllPolicies();

      expect(mockedApiClient.get).toHaveBeenCalledWith('/policies');
      expect(result).toEqual(mockPolicies);
    });
  });

  describe('getPolicyById', () => {
    test('should fetch policy by id', async () => {
      const mockPolicy: Policy = {
        id: 1,
        policyNumber: 'POL-2024-001',
        clientName: 'John Doe',
        vehicleRegistration: 'ABC123',
        insuranceType: 'OC',
        startDate: '2024-01-01',
        endDate: '2024-12-31',
        premium: 1200.00,
        status: 'ACTIVE',
      };

      mockedApiClient.get.mockResolvedValue({ data: mockPolicy });

      const result = await policyService.getPolicyById(1);

      expect(mockedApiClient.get).toHaveBeenCalledWith('/policies/1');
      expect(result).toEqual(mockPolicy);
    });
  });

  describe('getPoliciesByClient', () => {
    test('should fetch policies by client id', async () => {
      const mockPolicies: Policy[] = [
        {
          id: 1,
          policyNumber: 'POL-2024-001',
          clientName: 'John Doe',
          vehicleRegistration: 'ABC123',
          insuranceType: 'OC',
          startDate: '2024-01-01',
          endDate: '2024-12-31',
          premium: 1200.00,
          status: 'ACTIVE',
        },
      ];

      mockedApiClient.get.mockResolvedValue({ data: mockPolicies });

      const result = await policyService.getPoliciesByClient(1);

      expect(mockedApiClient.get).toHaveBeenCalledWith('/policies/client/1');
      expect(result).toEqual(mockPolicies);
    });
  });

  describe('createPolicy', () => {
    test('should create a new policy', async () => {
      const createRequest: CreatePolicyRequest = {
        clientId: 1,
        vehicleId: 1,
        insuranceType: 'OC',
        startDate: '2024-01-01',
        endDate: '2024-12-31',
        discountSurcharge: 0,
      };

      const mockCreatedPolicy: Policy = {
        id: 1,
        policyNumber: 'POL-2024-001',
        clientName: 'John Doe',
        vehicleRegistration: 'ABC123',
        insuranceType: 'OC',
        startDate: '2024-01-01',
        endDate: '2024-12-31',
        premium: 1200.00,
        status: 'ACTIVE',
      };

      mockedApiClient.post.mockResolvedValue({ data: mockCreatedPolicy });

      const result = await policyService.createPolicy(createRequest);

      expect(mockedApiClient.post).toHaveBeenCalledWith('/policies', createRequest);
      expect(result).toEqual(mockCreatedPolicy);
    });
  });

  describe('updatePolicy', () => {
    test('should update an existing policy', async () => {
      const updateRequest: UpdatePolicyRequest = {
        clientId: 1,
        vehicleId: 1,
        insuranceType: 'AC',
        startDate: '2024-01-01',
        endDate: '2024-12-31',
        discountSurcharge: -100,
      };

      const mockUpdatedPolicy: Policy = {
        id: 1,
        policyNumber: 'POL-2024-001',
        clientName: 'John Doe',
        vehicleRegistration: 'ABC123',
        insuranceType: 'AC',
        startDate: '2024-01-01',
        endDate: '2024-12-31',
        premium: 2400.00,
        status: 'ACTIVE',
      };

      mockedApiClient.put.mockResolvedValue({ data: mockUpdatedPolicy });

      const result = await policyService.updatePolicy(1, updateRequest);

      expect(mockedApiClient.put).toHaveBeenCalledWith('/policies/1', updateRequest);
      expect(result).toEqual(mockUpdatedPolicy);
    });
  });

  describe('cancelPolicy', () => {
    test('should cancel a policy', async () => {
      mockedApiClient.delete.mockResolvedValue({});

      await policyService.cancelPolicy(1);

      expect(mockedApiClient.delete).toHaveBeenCalledWith('/policies/1');
    });
  });

  describe('generatePolicyPdf', () => {
    test('should generate policy PDF', async () => {
      const mockBlob = new Blob(['pdf content'], { type: 'application/pdf' });
      mockedApiClient.post.mockResolvedValue({ data: mockBlob });

      const result = await policyService.generatePolicyPdf(1);

      expect(mockedApiClient.post).toHaveBeenCalledWith('/policies/1/pdf', {}, {
        responseType: 'blob',
      });
      expect(result).toEqual(mockBlob);
    });
  });

  describe('getAllClients', () => {
    test('should fetch all clients', async () => {
      const mockClients = [
        {
          id: 1,
          fullName: 'John Doe',
          pesel: '12345678901',
          address: '123 Main St',
          email: 'john@example.com',
          phoneNumber: '+48123456789',
        },
      ];

      mockedApiClient.get.mockResolvedValue({ data: mockClients });

      const result = await policyService.getAllClients();

      expect(mockedApiClient.get).toHaveBeenCalledWith('/clients');
      expect(result).toEqual(mockClients);
    });
  });

  describe('getAllVehicles', () => {
    test('should fetch all vehicles', async () => {
      const mockVehicles = [
        {
          id: 1,
          make: 'Toyota',
          model: 'Corolla',
          yearOfManufacture: 2020,
          registrationNumber: 'ABC123',
          vin: '1234567890ABCDEFG',
          engineCapacity: 1600,
          power: 120,
          firstRegistrationDate: '2020-01-15',
        },
      ];

      mockedApiClient.get.mockResolvedValue({ data: mockVehicles });

      const result = await policyService.getAllVehicles();

      expect(mockedApiClient.get).toHaveBeenCalledWith('/vehicles');
      expect(result).toEqual(mockVehicles);
    });
  });
});