import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { ErrorResponse } from '../types/api.types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

// Request Interceptor: Attach JWT Token automatically if present
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('access_token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// Response Interceptor: Unified Error Handling
apiClient.interceptors.response.use(
  (response) => {
    // Return the response directly
    return response;
  },
  (error: AxiosError<ErrorResponse>) => {
    if (error.response) {
      const { status, data } = error.response;

      if (status === 401) {
        // Unauthorized - Token expired or invalid
        console.warn('Unauthorized request. Clearing credentials...');
        localStorage.removeItem('access_token');
      } else if (status === 403) {
        console.error('Forbidden action:', data?.message || 'Access denied');
      } else if (status === 404) {
        console.warn('Resource not found:', data?.message);
      } else if (status === 500) {
        console.error('Internal server error occurred:', data?.message);
      }
    } else if (error.request) {
      // Network failure or Server down
      console.error('Network Error: Cannot connect to Backend server at', API_BASE_URL);
    }

    return Promise.reject(error);
  }
);

export default apiClient;
