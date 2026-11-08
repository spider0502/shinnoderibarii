<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { fetchCategories, fetchProducts, type Category, type Product } from '../api/products'
import { fetchStoreById, type Store } from '../api/stores'
import { addCartItem } from '../api/cart'
import { useCartStore } from '../stores/cart'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const store = ref<Store | null>(null)
const categories = ref<Category[]>([])
const products = ref<Product[]>([])

const loading = ref(false)
const errorMessage = ref('')

const addingProductIds = ref<number[]>([])
const successMessage = ref('')
const cartErrorMessage = ref('')

const storeId = computed(() => Number(route.params.storeId))

// 按分类获取商品
function getProductsByCategory(categoryId: number): Product[] {
  return products.value.filter((product) => product.categoryId === categoryId)
}

function scrollToCategory(categoryId: number) {
  const element = document.getElementById(`category-${categoryId}`)

  element?.scrollIntoView({
    behavior: 'smooth',
    block: 'start',
  })
}

function handleImageError(event: Event) {
  const image = event.target as HTMLImageElement
  image.style.display = 'none'
}

async function loadStoreProducts() {
  if (!Number.isInteger(storeId.value) || storeId.value <= 0) {
    errorMessage.value = '店舗IDが正しくありません。'
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    const [storeData, categoryData, productData] = await Promise.all([
      fetchStoreById(storeId.value),
      fetchCategories(storeId.value),
      fetchProducts(storeId.value),
    ])

    if (!storeData) {
      errorMessage.value = '店舗が見つかりません。'
      return
    }

    store.value = storeData
    categories.value = categoryData
    products.value = productData
  } catch (error: unknown) {
    if (axios.isAxiosError(error)) {
      if (error.response?.status === 401) {
        errorMessage.value = 'ログインの有効期限が切れました。'
      } else {
        errorMessage.value = '商品情報を取得できませんでした。'
      }
    } else {
      errorMessage.value = '予期しないエラーが発生しました。'
    }
  } finally {
    loading.value = false
  }
}

async function handleAddToCart(productId: number) {
  if (addingProductIds.value.includes(productId)) {
    return
  }

  addingProductIds.value.push(productId)
  successMessage.value = ''
  cartErrorMessage.value = ''

  try {
    await addCartItem(productId, 1)

    successMessage.value = '商品をカートに追加しました。'

    try {
      await cartStore.refreshCartCount()
    } catch {
      // 商品已经添加成功，角标同步失败不应影响添加结果
    }
  } catch (error: unknown) {
    if (axios.isAxiosError(error)) {
      if (error.response?.status === 401) {
        cartErrorMessage.value = 'ログインの有効期限が切れました。'
      } else {
        cartErrorMessage.value = error.response?.data?.message ?? 'カートへの追加に失敗しました。'
      }
    } else {
      cartErrorMessage.value = '予期しないエラーが発生しました。'
    }
  } finally {
    addingProductIds.value = addingProductIds.value.filter((id) => id !== productId)
  }
}

function goBack() {
  router.push({ name: 'home' })
}

onMounted(loadStoreProducts)
</script>

<template>
  <main class="page">
    <header class="header">
      <button type="button" class="back-button" @click="goBack">← 店舗一覧へ戻る</button>

      <h1>{{ store?.name ?? '店舗商品一覧' }}</h1>
      <p v-if="store?.description">{{ store.description }}</p>
      <p v-if="store?.address" class="store-address">
        {{ store.address }}
      </p>
    </header>

    <section class="content">
      <p v-if="loading" class="message">商品情報を読み込み中...</p>

      <div v-else-if="errorMessage" class="error" role="alert">
        {{ errorMessage }}
        <button type="button" @click="loadStoreProducts">再読み込み</button>
      </div>

      <div v-else-if="categories.length === 0" class="empty">商品カテゴリがありません。</div>

      <template v-else>
        <p v-if="successMessage" class="success-message" role="status">
          {{ successMessage }}
        </p>

        <p v-if="cartErrorMessage" class="error-message" role="alert">
          {{ cartErrorMessage }}
        </p>
        <nav class="category-nav" aria-label="商品分类">
          <button
            v-for="category in categories"
            :key="category.id"
            type="button"
            class="category-nav-button"
            @click="scrollToCategory(category.id)"
          >
            {{ category.name }}
          </button>
        </nav>
        <section
          v-for="category in categories"
          :key="category.id"
          :id="`category-${category.id}`"
          class="category-section"
        >
          <h2>{{ category.name }}</h2>

          <div v-if="getProductsByCategory(category.id).length === 0" class="empty-category">
            このカテゴリには商品がありません。
          </div>

          <div v-else class="product-list">
            <article
              v-for="product in getProductsByCategory(category.id)"
              :key="product.id"
              class="product-card"
            >
              <div class="product-image">
                <img
                  v-if="product.imageUrl"
                  :src="product.imageUrl"
                  :alt="product.name"
                  @error="handleImageError"
                />

                <span v-else>🍱</span>
              </div>

              <div class="product-info">
                <h3>{{ product.name }}</h3>

                <p v-if="product.description">
                  {{ product.description }}
                </p>

                <strong>¥{{ product.price.toLocaleString('ja-JP') }}</strong>
              </div>

              <span
                class="product-status"
                :class="product.status === 'ON_SALE' ? 'available' : 'sold-out'"
              >
                {{ product.status === 'ON_SALE' ? '販売中' : '売り切れ' }}
              </span>
              <button
                type="button"
                class="add-cart-button"
                :disabled="product.status !== 'ON_SALE' || addingProductIds.includes(product.id)"
                @click="handleAddToCart(product.id)"
              >
                {{ addingProductIds.includes(product.id) ? '追加中...' : 'カートに追加' }}
              </button>
            </article>
          </div>
        </section>
      </template>
    </section>
  </main>
</template>

<style scoped>
.page {
  min-height: 100vh;
  background: #f3f5f8;
  color: #222;
}

.header {
  padding: 24px 5%;
  background: white;
  border-bottom: 1px solid #e5e7eb;
}

.header h1 {
  margin-bottom: 0;
  font-size: 24px;
}

.back-button {
  padding: 8px 12px;
  color: #333;
  background: #e5e7eb;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.content {
  max-width: 900px;
  margin: 0 auto;
  padding: 32px 24px;
}

.store-address {
  color: #666;
  font-size: 14px;
  margin-bottom: 0;
}

.category-section {
  margin-bottom: 32px;
}

.category-section h2 {
  padding-bottom: 12px;
  border-bottom: 2px solid #ddd;
}
.category-nav {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding: 12px 0;
  margin-bottom: 24px;
  background: #f3f5f8;
}

.category-nav-button {
  flex-shrink: 0;
  padding: 9px 16px;
  color: #333;
  background: white;
  border: 1px solid #ddd;
  border-radius: 20px;
  cursor: pointer;
  white-space: nowrap;
}

.category-nav-button:hover {
  color: white;
  background: #2563eb;
  border-color: #2563eb;
}

.category-section {
  scroll-margin-top: 70px;
}

.product-list {
  display: grid;
  gap: 12px;
}

.product-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 20px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
}

.product-info {
  min-width: 0;
}

.product-info h3 {
  margin: 0 0 8px;
}

.product-info p {
  color: #666;
  font-size: 14px;
}

.product-info strong {
  color: #2563eb;
}

.product-status {
  flex-shrink: 0;
  padding: 5px 10px;
  border-radius: 6px;
  font-size: 12px;
}

.product-image {
  width: 110px;
  height: 110px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: #f3f4f6;
  border-radius: 8px;
  font-size: 36px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 20px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
}

.add-cart-button {
  flex-shrink: 0;
  padding: 9px 14px;
  color: white;
  background: #2563eb;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.add-cart-button:hover:not(:disabled) {
  background: #1d4ed8;
}

.add-cart-button:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.success-message {
  padding: 12px;
  color: #166534;
  background: #dcfce7;
  border-radius: 6px;
}

.error-message {
  padding: 12px;
  color: #b91c1c;
  background: #fee2e2;
  border-radius: 6px;
}

.available {
  color: #166534;
  background: #dcfce7;
}

.sold-out {
  color: #92400e;
  background: #fef3c7;
}

.message,
.empty {
  padding: 32px;
  background: white;
  border-radius: 10px;
  text-align: center;
}

.empty-category {
  padding: 16px;
  color: #888;
}

.error {
  padding: 20px;
  color: #b91c1c;
  background: white;
  border-radius: 10px;
}

@media (max-width: 600px) {
  .product-card {
    align-items: flex-start;
  }

  .product-image {
    width: 80px;
    height: 80px;
  }
}
</style>
