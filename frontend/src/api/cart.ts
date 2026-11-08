import http from './http'

export interface CartItem {
  id: number
  productId: number
  storeId: number
  storeName: string
  storeStatus: string
  name: string
  description: string | null
  price: number
  imageUrl: string | null
  quantity: number
  subtotal: number
}

export interface Cart {
  items: CartItem[]
  totalAmount: number
  totalQuantity: number
}

/**
 * 获取购物车
 */
export async function fetchCart(): Promise<Cart> {
  const response = await http.get<Cart>('/cart')
  return response.data
}

/**
 * 添加商品到购物车
 */
export async function addCartItem(productId: number, quantity: number): Promise<void> {
  await http.post('/cart/items', {
    productId,
    quantity,
  })
}

/**
 * 修改购物车商品数量
 */
export async function updateCartItemQuantity(cartItemId: number, quantity: number): Promise<void> {
  await http.put(`/cart/items/${cartItemId}`, {
    quantity,
  })
}

/**
 * 删除购物车商品
 */
export async function removeCartItem(cartItemId: number): Promise<void> {
  await http.delete(`/cart/items/${cartItemId}`)
}
