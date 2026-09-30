export type ReviewStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface Review {
  id: number;
  productId: number;
  productName: string;
  userId: number;
  customerName: string;
  orderId?: number;
  rating: number;
  comment?: string;
  status: ReviewStatus;
  imageUrls: string[];
  createdAt: string;
}

export interface ProductRatingSummary {
  productId: number;
  averageRating: number;
  totalReviews: number;
  ratingBreakdown: Record<number, number>; // 1 -> count, 2 -> count, ..., 5 -> count
}

export interface CreateReviewRequest {
  orderId: number;
  rating: number;
  comment?: string;
  imageUrls?: string[];
}

export interface UpdateReviewStatusRequest {
  status: ReviewStatus;
  rejectionReason?: string;
}
