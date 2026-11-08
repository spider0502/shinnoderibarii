<script setup lang="ts">
import { computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'
import { useCartStore } from './stores/cart'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const cartStore = useCartStore()

const isLoggedIn = computed(() => auth.isLoggedIn)

function goHome() {
  router.push({ name: 'home' })
}

function goCart() {
  router.push({ name: 'cart' })
}

function logout() {
  auth.logout()
  router.push({ name: 'login' })
}

// 登录后同步购物车数量，退出登录时清空数量
watch(
  () => auth.isLoggedIn,
  async (loggedIn) => {
    if (!loggedIn) {
      cartStore.resetCart()
      return
    }

    try {
      await cartStore.refreshCartCount()
    } catch {
      // 获取失败时保留当前数量，后续可以重新同步
    }
  },
  { immediate: true },
)
</script>

<template>
  <div class="app">
    <header v-if="isLoggedIn" class="global-header">
      <div class="header-inner">
        <div class="logo" @click="goHome">宅配注文管理システム</div>

        <nav class="header-nav">
          <button
            type="button"
            :class="{ active: route.name === 'home' || route.name === 'store-products' }"
            @click="goHome"
          >
            店舗一覧
          </button>

          <button type="button" :class="{ active: route.name === 'cart' }" @click="goCart">
            <span>カート</span>

            <span v-if="cartStore.totalQuantity > 0" class="cart-badge">
              {{ cartStore.totalQuantity > 99 ? '99+' : cartStore.totalQuantity }}
            </span>
          </button>

          <button type="button" @click="logout">ログアウト</button>
        </nav>
      </div>
    </header>

    <RouterView />
  </div>
</template>

<style scoped>
.global-header {
  background: #fff;
  border-bottom: 1px solid #e5e5e5;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  min-height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.logo {
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
}

.header-nav {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-nav button {
  padding: 9px 14px;
  border: none;
  border-radius: 6px;
  background: transparent;
  cursor: pointer;
  color: #444;
}

.header-nav button:hover {
  background: #f3f3f3;
}

.header-nav button.active {
  background: #eef2ff;
  color: #3730a3;
  font-weight: 600;
}
.cart-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 19px;
  height: 19px;
  padding: 0 5px;
  margin-left: 5px;
  border-radius: 10px;
  background: #e53935;
  color: white;
  font-size: 11px;
  font-weight: 700;
  line-height: 1;
}

@media (max-width: 600px) {
  .header-inner {
    padding: 0 12px;
    flex-wrap: wrap;
  }

  .logo {
    font-size: 15px;
  }

  .header-nav {
    gap: 2px;
  }

  .header-nav button {
    padding: 8px;
    font-size: 13px;
  }
}
</style>
