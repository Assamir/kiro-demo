import axios, { AxiosInstance, AxiosResponse, AxiosError, AxiosRequestConfig } from 'axios';

// Enhanced error interface
export interface ApiError {
  message: string;
  status?: number;
  code?: string;
  details?: any;
  retryable?: boolean;
}

// Retry configuration
interface RetryConfig {
  retries: number;
  retryDelay: number;
  retryCondition?: (error: AxiosError) => boolean;
}

// Extended request config with retry support
interface ExtendedAxiosRequestConfig extends AxiosRequestConfig {
  retry?: RetryConfig;
  metadata?: {
    startTime: Date;
  };
}

const DEFAULT_RETRY_CONFIG: RetryConfig = {
  retries: 3,
  retryDelay: 1000,
  retryCondition: (error: AxiosError) => {
    // Retry on network errors or 5xx server errors
    return !error.response || (error.response.status >= 500 && error.response.status < 600);
  },
};

// Create axios instance with base configuration
export const apiClient: AxiosInstance = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add retry functionality to axios instance
apiClient.defaults.retry = DEFAULT_RETRY_CONFIG;

// Request interceptor to add auth token and retry config
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // Add retry metadata
    config.metadata = { startTime: new Date() };
    
    return config;
  },
  (error) => {
    return Promise.reject(createApiError(error));
  }
);

// Response interceptor for error handling and retry logic
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    // Log successful requests in development
    if (process.env.NODE_ENV === 'development') {
      const startTime = response.config.metadata?.startTime;
      const duration = startTime ? new Date().getTime() - startTime.getTime() : 0;
      console.log(`✅ ${response.config.method?.toUpperCase()} ${response.config.url} - ${response.status} (${duration}ms)`);
    }
    return response;
  },
  async (error: AxiosError) => {
    const config = error.config as ExtendedAxiosRequestConfig & { 
      retryCount?: number;
    };

    // Handle authentication errors
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
      return Promise.reject(createApiError(error, 'Authentication required. Please log in again.'));
    }

    // Handle authorization errors
    if (error.response?.status === 403) {
      return Promise.reject(createApiError(error, 'You do not have permission to perform this action.'));
    }

    // Retry logic
    const retryConfig = config.retry || DEFAULT_RETRY_CONFIG;
    const currentRetryCount = config.retryCount || 0;

    if (
      currentRetryCount < retryConfig.retries &&
      retryConfig.retryCondition &&
      retryConfig.retryCondition(error)
    ) {
      config.retryCount = currentRetryCount + 1;
      
      // Log retry attempt
      console.warn(`🔄 Retrying request (${config.retryCount}/${retryConfig.retries}):`, {
        url: config.url,
        method: config.method,
        error: error.message,
      });

      // Wait before retrying with exponential backoff
      const delay = retryConfig.retryDelay * Math.pow(2, currentRetryCount);
      await new Promise(resolve => setTimeout(resolve, delay));

      return apiClient(config);
    }

    // Log failed requests
    const duration = config.metadata?.startTime ? 
      new Date().getTime() - config.metadata.startTime.getTime() : 0;
    console.error(`❌ ${config.method?.toUpperCase()} ${config.url} - ${error.response?.status || 'Network Error'} (${duration}ms)`, {
      error: error.message,
      retries: currentRetryCount,
    });

    return Promise.reject(createApiError(error));
  }
);

// Helper function to create standardized API errors
function createApiError(error: AxiosError, customMessage?: string): ApiError {
  const status = error.response?.status;
  const responseData = error.response?.data as any;
  
  let message = customMessage || 'An unexpected error occurred';
  let code = 'UNKNOWN_ERROR';
  let retryable = false;

  if (error.code === 'ECONNABORTED') {
    message = 'Request timeout. Please check your connection and try again.';
    code = 'TIMEOUT_ERROR';
    retryable = true;
  } else if (error.code === 'ERR_NETWORK') {
    message = 'Network error. Please check your internet connection.';
    code = 'NETWORK_ERROR';
    retryable = true;
  } else if (status) {
    switch (status) {
      case 400:
        message = responseData?.message || 'Invalid request. Please check your input.';
        code = 'VALIDATION_ERROR';
        break;
      case 401:
        message = 'Authentication required. Please log in again.';
        code = 'AUTHENTICATION_ERROR';
        break;
      case 403:
        message = 'You do not have permission to perform this action.';
        code = 'AUTHORIZATION_ERROR';
        break;
      case 404:
        message = responseData?.message || 'The requested resource was not found.';
        code = 'NOT_FOUND_ERROR';
        break;
      case 409:
        message = responseData?.message || 'Conflict with existing data.';
        code = 'CONFLICT_ERROR';
        break;
      case 422:
        message = responseData?.message || 'Validation failed. Please check your input.';
        code = 'VALIDATION_ERROR';
        break;
      case 429:
        message = 'Too many requests. Please wait a moment and try again.';
        code = 'RATE_LIMIT_ERROR';
        retryable = true;
        break;
      case 500:
        message = 'Internal server error. Please try again later.';
        code = 'SERVER_ERROR';
        retryable = true;
        break;
      case 502:
      case 503:
      case 504:
        message = 'Service temporarily unavailable. Please try again later.';
        code = 'SERVICE_UNAVAILABLE';
        retryable = true;
        break;
      default:
        if (status >= 500) {
          message = 'Server error. Please try again later.';
          code = 'SERVER_ERROR';
          retryable = true;
        }
    }
  }

  return {
    message,
    status,
    code,
    details: responseData,
    retryable,
  };
}

// Utility function for making requests with custom retry configuration
export const apiRequest = async <T = any>(
  config: ExtendedAxiosRequestConfig & { retry?: Partial<RetryConfig> }
): Promise<T> => {
  const { retry, ...requestConfig } = config;
  
  if (retry) {
    (requestConfig as ExtendedAxiosRequestConfig).retry = { ...DEFAULT_RETRY_CONFIG, ...retry };
  }

  const response = await apiClient(requestConfig);
  return response.data;
};

// Utility functions for common HTTP methods with retry support
export const apiGet = <T = any>(
  url: string, 
  config?: AxiosRequestConfig & { retry?: Partial<RetryConfig> }
): Promise<T> => {
  return apiRequest<T>({ ...config, method: 'GET', url });
};

export const apiPost = <T = any>(
  url: string, 
  data?: any, 
  config?: AxiosRequestConfig & { retry?: Partial<RetryConfig> }
): Promise<T> => {
  return apiRequest<T>({ ...config, method: 'POST', url, data });
};

export const apiPut = <T = any>(
  url: string, 
  data?: any, 
  config?: AxiosRequestConfig & { retry?: Partial<RetryConfig> }
): Promise<T> => {
  return apiRequest<T>({ ...config, method: 'PUT', url, data });
};

export const apiDelete = <T = any>(
  url: string, 
  config?: AxiosRequestConfig & { retry?: Partial<RetryConfig> }
): Promise<T> => {
  return apiRequest<T>({ ...config, method: 'DELETE', url });
};

// Type augmentation for axios config
declare module 'axios' {
  interface AxiosRequestConfig {
    retry?: RetryConfig;
    retryCount?: number;
    metadata?: {
      startTime: Date;
    };
  }
}