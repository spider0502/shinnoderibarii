import { defineStore } from 'pinia'
import { ref } from 'vue'
import { fetchCart } from '../api/cart'

export const useCartStore = defineStore('cart', () => {
  // 购物车商品总件数（包含休业中店铺的商品）
  const totalQuantity = ref(0)

  // 是否正在同步购物车数量
  const loading = ref(false)

  async function refreshCartCount() {
    loading.value = true

    try {
      const cart = await fetchCart()
      totalQuantity.value = cart.totalQuantity
    } finally {
      loading.value = false
    }
  }

  function setTotalQuantity(quantity: number) {
    totalQuantity.value = quantity
  }

  function resetCart() {
    totalQuantity.value = 0
  }

  return {
    totalQuantity,
    loading,
    refreshCartCount,
    setTotalQuantity,
    resetCart,
  }
})
