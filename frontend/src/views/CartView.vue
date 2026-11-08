<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { fetchCart, updateCartItemQuantity, removeCartItem, type CartItem } from '../api/cart'
import { useCartStore } from '../stores/cart'

const router = useRouter()
const cartStore = useCartStore()

const items = ref<CartItem[]>([])
const selectedItemIds = ref<number[]>([])
const loading = ref(false)
const syncing = ref(false)
const syncFailed = ref(false)
const errorMessage = ref('')
const actionLoadingIds = ref<number[]>([])

// 只有营业中店铺的商品可以被选中
const selectableItems = computed(() => items.value.filter((item) => item.storeStatus === 'OPEN'))

const isAllSelected = computed(
  () =>
    selectableItems.value.length > 0 &&
    selectableItems.value.every((item) => selectedItemIds.value.includes(item.id)),
)

// 已选商品
const selectedItems = computed(() =>
  items.value.filter(
    (item) => selectedItemIds.value.includes(item.id) && item.storeStatus === 'OPEN',
  ),
)

// 已选商品总金额
const selectedTotal = computed(() =>
  selectedItems.value.reduce((total, item) => total + item.price * item.quantity, 0),
)

// 已选商品总数量
const selectedQuantity = computed(() =>
  selectedItems.value.reduce((total, item) => total + item.quantity, 0),
)

function toggleItem(item: CartItem) {
  if (item.storeStatus !== 'OPEN') return

  if (selectedItemIds.value.includes(item.id)) {
    selectedItemIds.value = selectedItemIds.value.filter((id) => id !== item.id)
  } else {
    selectedItemIds.value.push(item.id)
  }
}

function toggleAll(event: Event) {
  const checked = (event.target as HTMLInputElement).checked

  selectedItemIds.value = checked ? selectableItems.value.map((item) => item.id) : []
}

// 初次加载购物车
async function loadCart() {
  loading.value = true
  errorMessage.value = ''
  syncFailed.value = false

  try {
    const cart = await fetchCart()
    items.value = cart.items
    selectedItemIds.value = []
    cartStore.setTotalQuantity(cart.totalQuantity)
  } catch {
    errorMessage.value = 'カートの取得に失敗しました。'
  } finally {
    loading.value = false
  }
}

// 后台重新同步，不触发整个页面的 loading 状态
async function syncCart() {
  syncing.value = true

  try {
    const cart = await fetchCart()

    items.value = cart.items

    cartStore.setTotalQuantity(cart.totalQuantity)

    // 保留仍存在且所属店铺仍在营业的已选商品
    selectedItemIds.value = selectedItemIds.value.filter((id) =>
      cart.items.some((item) => item.id === id && item.storeStatus === 'OPEN'),
    )

    syncFailed.value = false
  } finally {
    syncing.value = false
  }
}

// 判断请求结果是否不确定
function isUncertainError(error: unknown): boolean {
  if (!axios.isAxiosError(error)) return false

  // 没有收到服务器响应，或者服务器发生 5xx 错误
  return !error.response || error.response.status >= 500
}

// 请求结果不确定时，重新向服务器确认购物车状态
async function handleUncertainResult() {
  try {
    await syncCart()

    errorMessage.value = '通信結果を確認するため、カートを最新状態に同期しました。'
  } catch {
    syncFailed.value = true
    errorMessage.value = 'カートの同期に失敗しました。通信状態を確認して再試行してください。'
  }
}

// 手动重新同步
async function retrySync() {
  errorMessage.value = ''
  await handleUncertainResult()
}

async function changeQuantity(item: CartItem, quantity: number) {
  if (quantity < 1 || actionLoadingIds.value.includes(item.id)) return

  actionLoadingIds.value.push(item.id)
  errorMessage.value = ''

  try {
    await updateCartItemQuantity(item.id, quantity)

    // 服务器确认成功，只更新当前商品
    item.quantity = quantity

    try {
      await cartStore.refreshCartCount()
    } catch {
      // 角标同步失败不影响商品数量修改结果
    }
  } catch (error) {
    if (isUncertainError(error)) {
      await handleUncertainResult()
    } else if (axios.isAxiosError(error)) {
      errorMessage.value = error.response?.data?.message ?? '数量の変更に失敗しました。'
    } else {
      errorMessage.value = '数量の変更に失敗しました。'
    }
  } finally {
    actionLoadingIds.value = actionLoadingIds.value.filter((id) => id !== item.id)
  }
}

async function deleteItem(item: CartItem) {
  if (actionLoadingIds.value.includes(item.id)) return

  actionLoadingIds.value.push(item.id)
  errorMessage.value = ''

  try {
    await removeCartItem(item.id)

    // 服务器确认删除成功，只移除当前商品
    items.value = items.value.filter((currentItem) => currentItem.id !== item.id)

    selectedItemIds.value = selectedItemIds.value.filter((id) => id !== item.id)

    try {
      await cartStore.refreshCartCount()
    } catch {
      // 角标同步失败不影响商品删除结果
    }
  } catch (error) {
    if (isUncertainError(error)) {
      await handleUncertainResult()
    } else {
      errorMessage.value = '商品の削除に失敗しました。'
    }
  } finally {
    actionLoadingIds.value = actionLoadingIds.value.filter((id) => id !== item.id)
  }
}

function goBack() {
  router.push({ name: 'home' })
}

onMounted(loadCart)
</script>

<template>
  <main class="cart-page">
    <header class="page-header">
      <button class="back-button" @click="goBack">← 店舗一覧へ戻る</button>
      <h1>ショッピングカート</h1>
    </header>

    <div v-if="errorMessage" class="error-message">
      <span>{{ errorMessage }}</span>

      <button v-if="syncFailed" type="button" @click="retrySync" :disabled="syncing">
        {{ syncing ? '同期中...' : '再試行' }}
      </button>
    </div>

    <p v-if="syncing" class="sync-message">カートを同期しています...</p>

    <div v-if="loading" class="loading">読み込み中...</div>

    <div v-else-if="items.length === 0" class="empty-cart">
      <p>カートは空です。</p>
      <button @click="goBack">商品を探す</button>
    </div>

    <template v-else>
      <div class="cart-toolbar">
        <label>
          <input
            type="checkbox"
            :checked="isAllSelected"
            :disabled="selectableItems.length === 0"
            @change="toggleAll"
          />
          営業中の商品をすべて選択
        </label>
        <span>{{ selectedItems.length }} 件選択中</span>
      </div>

      <section class="cart-items">
        <article v-for="item in items" :key="item.id" class="cart-item">
          <input
            type="checkbox"
            :checked="selectedItemIds.includes(item.id)"
            :disabled="item.storeStatus !== 'OPEN'"
            @change="toggleItem(item)"
          />

          <div class="item-info">
            <div class="store-info">
              <strong>{{ item.storeName }}</strong>
              <span class="store-status" :class="item.storeStatus === 'OPEN' ? 'open' : 'closed'">
                {{ item.storeStatus === 'OPEN' ? '営業中' : '休業中' }}
              </span>
            </div>

            <h3>{{ item.name }}</h3>
            <p v-if="item.description">{{ item.description }}</p>
            <strong>¥{{ item.price.toLocaleString() }}</strong>

            <div class="quantity-controls">
              <button
                :disabled="item.quantity <= 1 || actionLoadingIds.includes(item.id)"
                @click="changeQuantity(item, item.quantity - 1)"
              >
                −
              </button>

              <span>{{ item.quantity }}</span>

              <button
                :disabled="actionLoadingIds.includes(item.id)"
                @click="changeQuantity(item, item.quantity + 1)"
              >
                +
              </button>

              <button
                class="delete-button"
                :disabled="actionLoadingIds.includes(item.id)"
                @click="deleteItem(item)"
              >
                削除
              </button>

              <span v-if="actionLoadingIds.includes(item.id)" class="action-loading">
                処理中...
              </span>
            </div>

            <p v-if="item.storeStatus !== 'OPEN'" class="closed-notice">
              この店舗は休業中のため、注文できません。
            </p>
          </div>

          <div class="item-subtotal">¥{{ (item.price * item.quantity).toLocaleString() }}</div>
        </article>
      </section>

      <section class="cart-summary">
        <p>選択商品数：{{ selectedQuantity }} 点</p>
        <h2>
          選択商品の合計：
          <span>¥{{ selectedTotal.toLocaleString() }}</span>
        </h2>
        <button class="order-button" disabled>注文機能は準備中です</button>
      </section>
    </template>
  </main>
</template>

<style scoped>
.cart-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 32px 24px;
}

.page-header {
  margin-bottom: 28px;
}

.page-header h1 {
  margin-top: 16px;
}

.back-button {
  border: none;
  background: none;
  cursor: pointer;
  color: #555;
}

.cart-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 16px;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.cart-item {
  display: flex;
  align-items: flex-start;
  gap: 18px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.item-info {
  flex: 1;
}

.item-info h3 {
  margin: 10px 0;
}

.item-info p {
  color: #666;
}

.store-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.store-status {
  font-size: 12px;
  padding: 3px 8px;
  border-radius: 4px;
}

.store-status.open {
  background: #e5f5e9;
  color: #237a3b;
}

.store-status.closed {
  background: #eee;
  color: #777;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 16px;
}

.quantity-controls button {
  padding: 4px 12px;
  cursor: pointer;
}

.quantity-controls button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.delete-button {
  margin-left: 12px;
}

.item-subtotal {
  font-weight: bold;
  white-space: nowrap;
}

.closed-notice {
  color: #b45309 !important;
  font-size: 13px;
  margin-top: 10px;
}

.cart-summary {
  margin-top: 28px;
  padding: 24px;
  background: #f8f8f8;
  border-radius: 8px;
  text-align: right;
}

.cart-summary h2 span {
  font-size: 24px;
}

.order-button {
  margin-top: 16px;
  padding: 12px 28px;
  border: none;
  border-radius: 6px;
  background: #aaa;
  color: white;
  cursor: not-allowed;
}

.loading,
.empty-cart {
  text-align: center;
  padding: 60px 20px;
}

.error-message {
  color: #c0392b;
  margin-bottom: 16px;
}
.action-loading {
  color: #666;
  font-size: 13px;
  white-space: nowrap;
}

.sync-message {
  color: #666;
  font-size: 13px;
  margin-bottom: 12px;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #c0392b;
  margin-bottom: 16px;
}

.error-message button {
  padding: 5px 12px;
  cursor: pointer;
}

@media (max-width: 600px) {
  .cart-item {
    gap: 10px;
    padding: 14px;
  }

  .item-subtotal {
    font-size: 14px;
  }

  .cart-summary {
    text-align: left;
  }
}
</style>
