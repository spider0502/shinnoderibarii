import http from './http'

export interface Category {
  id: number
  storeId: number
  name: string
  sortOrder: number
}

export interface Product {
  id: number
  storeId: number
  categoryId: number
  name: string
  description: string | null
  price: number
  imageUrl: string | null
  status: string
  sortOrder: number
}

export async function fetchCategories(storeId: number): Promise<Category[]> {
  const response = await http.get<Category[]>(`/stores/${storeId}/categories`)
  return response.data
}

export async function fetchProducts(storeId: number): Promise<Product[]> {
  const response = await http.get<Product[]>(`/stores/${storeId}/products`)
  return response.data
}
