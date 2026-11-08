<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

const registeredMessage = computed(() =>
  route.query.registered === 'true' ? 'ユーザー登録が完了しました。ログインしてください。' : '',
)

const email = ref('')
const password = ref('')
const errorMessage = ref('')
const loading = ref(false)

async function handleLogin() {
  errorMessage.value = ''
  loading.value = true

  try {
    await auth.login(email.value, password.value)
    await router.push('/')
  } catch {
    errorMessage.value = 'ログインに失敗しました。入力内容を確認してください。'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="page">
    <section class="panel">
      <h1>宅配注文管理システム</h1>
      <h2>ログイン</h2>

      <form @submit.prevent="handleLogin">
        <label for="email">メールアドレス</label>
        <input id="email" v-model.trim="email" type="email" autocomplete="username" required />

        <label for="password">パスワード</label>
        <input
          id="password"
          v-model="password"
          type="password"
          autocomplete="current-password"
          required
        />

        <button type="submit" :disabled="loading">
          {{ loading ? 'ログイン中...' : 'ログイン' }}
        </button>
      </form>

      <p v-if="errorMessage" class="error" role="alert">
        {{ errorMessage }}
      </p>

      <p v-if="registeredMessage" class="success" role="status">
        {{ registeredMessage }}
      </p>

      <RouterLink to="/register">新規登録はこちら</RouterLink>
    </section>
  </main>
</template>

<style scoped>
.page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
  background: #f3f5f8;
}

.panel {
  width: 100%;
  max-width: 400px;
  box-sizing: border-box;
  padding: 32px;
  background: white;
  border-radius: 12px;
}

form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

input {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 6px;
}

button {
  padding: 12px;
  margin-bottom: 12px;
  color: white;
  background: #2563eb;
  border: 0;
  border-radius: 6px;
  cursor: pointer;
}

.error {
  color: #c62828;
}

.success {
  color: #16803c;
}
</style>
