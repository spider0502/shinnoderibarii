<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useAuthStore } from '../stores/auth'
import { fetchStores, type Store } from '../api/stores'

const auth = useAuthStore()
const router = useRouter()

const stores = ref<Store[]>([])
const loading = ref(false)
const errorMessage = ref('')

async function loadStores() {
  loading.value = true
  errorMessage.value = ''

  try {
    stores.value = await fetchStores()
  } catch (error: unknown) {
    if (axios.isAxiosError(error)) {
      if (error.response?.status === 401) {
        errorMessage.value = 'ログインの有効期限が切れました。再度ログインしてください。'
      } else {
        errorMessage.value = '店舗一覧を取得できませんでした。'
      }
    } else {
      errorMessage.value = '予期しないエラーが発生しました。'
    }
  } finally {
    loading.value = false
  }
}

async function handleLogout() {
  auth.logout()
  await router.push('/login')
}

function handleStoreClick(storeId: number) {
  router.push({
    name: 'store-products',
    params: { storeId },
  })
}

onMounted(async () => {
  if (auth.token && !auth.isLoggedIn) {
    try {
      await auth.fetchCurrentUser()
    } catch {
      return
    }
  }

  await loadStores()
})
</script>

<template>
  <main class="page">
    <header class="header">
      <div>
        <h1>宅配注文管理システム</h1>
        <p>店舗一覧</p>
      </div>

      <div class="account">
        <span>{{ auth.user?.email }}</span>
        <button type="button" class="secondary" @click="handleLogout">ログアウト</button>
      </div>
    </header>

    <section class="content">
      <h2>店舗を探す</h2>

      <p v-if="loading">店舗一覧を読み込み中...</p>

      <div v-else-if="errorMessage" class="error" role="alert">
        {{ errorMessage }}
        <button type="button" class="secondary" @click="loadStores">再読み込み</button>
      </div>

      <div v-else-if="stores.length === 0" class="empty">店舗がまだ登録されていません。</div>

      <div v-else class="store-grid">
        <article
          v-for="store in stores"
          :key="store.id"
          class="store-card"
          role="button"
          tabindex="0"
          @click="handleStoreClick(store.id)"
          @keydown.enter="handleStoreClick(store.id)"
        >
          <div class="store-icon">🍱</div>

          <div class="store-info">
            <h3>{{ store.name }}</h3>
            <p v-if="store.description">{{ store.description }}</p>
            <p class="address">{{ store.address }}</p>
            <p v-if="store.phone" class="phone">{{ store.phone }}</p>
          </div>

          <span class="status" :class="store.status === 'OPEN' ? 'open' : 'closed'">
            {{ store.status === 'OPEN' ? '営業中' : '休業中' }}
          </span>
        </article>
      </div>
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
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 24px 5%;
  background: white;
  border-bottom: 1px solid #e5e7eb;
}

.header h1 {
  margin: 0;
  font-size: 24px;
}

.header p {
  margin-bottom: 0;
  color: #666;
}

.account {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.content {
  max-width: 1100px;
  margin: 0 auto;
  padding: 32px 24px;
}

.store-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.store-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
}

.store-card {
  cursor: pointer;
  transition:
    transform 0.15s,
    box-shadow 0.15s;
}

.store-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
}

.store-card:focus-visible {
  outline: 2px solid #2563eb;
  outline-offset: 3px;
}

.store-icon {
  font-size: 32px;
}

.store-info {
  flex: 1;
  min-width: 0;
}

.store-info h3 {
  margin-top: 0;
}

.store-info p {
  overflow-wrap: anywhere;
}

.address,
.phone {
  color: #666;
  font-size: 14px;
}

.status {
  flex-shrink: 0;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
}

.open {
  color: #166534;
  background: #dcfce7;
}

.closed {
  color: #92400e;
  background: #fef3c7;
}

.empty {
  padding: 32px;
  background: white;
  border-radius: 12px;
  text-align: center;
}

.error {
  padding: 20px;
  color: #b91c1c;
  background: white;
  border-radius: 12px;
}

button {
  padding: 9px 14px;
  color: white;
  background: #2563eb;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.secondary {
  color: #333;
  background: #e5e7eb;
}

@media (max-width: 600px) {
  .header {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
