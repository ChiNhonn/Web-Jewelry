import apiClient from '../api/axiosClient';
import { ApiResponse } from '../types/api.types';

export const uploadService = {
  /**
   * Upload a single image file (max 5MB)
   */
  async uploadSingleImage(file: File, folder: string = 'general'): Promise<string> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('folder', folder);

    const response = await apiClient.post<ApiResponse<string>>(
      '/api/v1/uploads/image',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );
    return response.data.data;
  },

  /**
   * Upload up to 5 image files simultaneously
   */
  async uploadMultipleImages(files: File[], folder: string = 'reviews'): Promise<string[]> {
    const formData = new FormData();
    files.forEach((file) => {
      formData.append('files', file);
    });
    formData.append('folder', folder);

    const response = await apiClient.post<ApiResponse<string[]>>(
      '/api/v1/uploads/images',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );
    return response.data.data;
  },
};
