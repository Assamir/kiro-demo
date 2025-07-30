// API endpoints
export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/auth/login',
    LOGOUT: '/auth/logout',
    ME: '/auth/me',
  },
  USERS: {
    BASE: '/users',
    BY_ID: (id: number) => `/users/${id}`,
  },
  POLICIES: {
    BASE: '/policies',
    BY_ID: (id: number) => `/policies/${id}`,
    BY_CLIENT: (clientId: number) => `/policies/client/${clientId}`,
    PDF: (id: number) => `/policies/${id}/pdf`,
  },
  RATING: {
    TABLES: (insuranceType: string) => `/rating/tables/${insuranceType}`,
    CALCULATE: '/rating/calculate',
  },
} as const;

// User roles
export const USER_ROLES = {
  ADMIN: 'ADMIN',
  OPERATOR: 'OPERATOR',
} as const;

// Insurance types
export const INSURANCE_TYPES = {
  OC: 'OC',
  AC: 'AC',
  NNW: 'NNW',
} as const;

// Policy statuses
export const POLICY_STATUSES = {
  ACTIVE: 'ACTIVE',
  CANCELED: 'CANCELED',
  EXPIRED: 'EXPIRED',
} as const;

// Local storage keys
export const STORAGE_KEYS = {
  TOKEN: 'token',
  USER: 'user',
} as const;