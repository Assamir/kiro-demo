import { apiClient } from './apiClient';
import { Policy, CreatePolicyRequest, UpdatePolicyRequest, Client, Vehicle } from '../types/policy';

export const policyService = {
  async getAllPolicies(): Promise<Policy[]> {
    const response = await apiClient.get<Policy[]>('/policies');
    return response.data;
  },

  async getPolicyById(id: number): Promise<Policy> {
    const response = await apiClient.get<Policy>(`/policies/${id}`);
    return response.data;
  },

  async getPoliciesByClient(clientId: number): Promise<Policy[]> {
    const response = await apiClient.get<Policy[]>(`/policies/client/${clientId}`);
    return response.data;
  },

  async createPolicy(policyData: CreatePolicyRequest): Promise<Policy> {
    const response = await apiClient.post<Policy>('/policies', policyData);
    return response.data;
  },

  async updatePolicy(id: number, policyData: UpdatePolicyRequest): Promise<Policy> {
    const response = await apiClient.put<Policy>(`/policies/${id}`, policyData);
    return response.data;
  },

  async cancelPolicy(id: number): Promise<void> {
    await apiClient.delete(`/policies/${id}`);
  },

  async generatePolicyPdf(id: number): Promise<Blob> {
    const response = await apiClient.post(`/policies/${id}/pdf`, {}, {
      responseType: 'blob',
    });
    return response.data;
  },

  // Helper services for form data
  async getAllClients(): Promise<Client[]> {
    const response = await apiClient.get<Client[]>('/clients');
    return response.data;
  },

  async getAllVehicles(): Promise<Vehicle[]> {
    const response = await apiClient.get<Vehicle[]>('/vehicles');
    return response.data;
  },
};