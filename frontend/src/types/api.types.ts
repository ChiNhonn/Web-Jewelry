/**
 * Standard API Response Structure (matching backend ApiResponse<T>)
 */
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

/**
 * Standard Paginated Response Structure (matching backend PageResponse<T>)
 */
export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

/**
 * Standard Error Response Structure (matching backend ErrorResponse)
 */
export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  details?: Array<{
    field: string;
    rejectedValue: unknown;
    message: string;
  }>;
}

/**
 * Health Check Response Data
 */
export interface HealthStatus {
  status: 'UP' | 'DOWN';
  application: string;
  environment: string;
  version: string;
  serverTime: string;
}
