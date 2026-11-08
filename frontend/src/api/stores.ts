import http from './http'

export interface Store {
  id: number
  name: string
  description: string | null
  address: string
  phone: string | null
  status: string
  createdAt: string
  updatedAt: string
}

export async function fetchStores(): Promise<Store[]> {
  const response = await http.get<Store[]>('/stores')
  return response.data
}

export async function fetchStoreById(storeId: number): Promise<Store | null> {
  const stores = await fetchStores()
  return stores.find((store) => store.id === storeId) ?? null
}
