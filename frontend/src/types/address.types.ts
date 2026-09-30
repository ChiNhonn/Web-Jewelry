export interface Address {
  id: number;
  receiverName: string;
  phone: string;
  streetAddress: string;
  ward: string;
  district: string;
  province: string;
  postalCode?: string;
  isDefault: boolean;
  fullAddress: string;
  createdAt?: string;
}

export interface AddressRequest {
  receiverName: string;
  phone: string;
  streetAddress: string;
  ward: string;
  district: string;
  province: string;
  postalCode?: string;
  isDefault?: boolean;
}
