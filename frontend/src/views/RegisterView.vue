
<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { registerUser } from '../api/users'

const router = useRouter()

const name = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')

const errorMessage = ref('')
const successMessage = ref('')
const loading = ref(false)

async function handleRegister() {
  errorMessage.value = ''
  successMessage.value = ''

  if (password.value !== confirmPassword.value) {
    errorMessage.value = 'パスワードが一致しません'
    return
  }

  if (password.value.length < 8 || password.value.length > 72) {
    errorMessage.value = 'パスワードは8文字以上72文字以内で入力してください'
    return
  }

  loading.value = true

  try {
    await registerUser({
      name: name.value,
      email: email.value,
      password: password.value,
    })

    successMessage.value = '登録に成功しました。ログインしてください。'

    password.value = ''
    confirmPassword.value = ''

    await router.push({
      name: 'login',
      query: { registered: 'true' },
    })
  } catch (error: unknown) {
    if (axios.isAxiosError(error)) {
      errorMessage.value =
        error.response?.data?.message ??
        '登録に失敗しました。入力内容を確認してください。'
    } else {
      errorMessage.value = '予期しないエラーが発生しました'
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="page">
    <section class="panel">
      <h1>宅配注文管理システム</h1>
      <h2>新規登録</h2>
      <p class="subtitle">アカウントを作成してください。</p>

      <form @submit.prevent="handleRegister">
        <label for="name">名前</label>
        <input
          id="name"
          v-model.trim="name"
          type="text"
          maxlength="100"
          autocomplete="name"
          required
        />

        <label for="email">メールアドレス</label>
        <input
          id="email"
          v-model.trim="email"
          type="email"
          maxlength="255"
          autocomplete="email"
          required
        />

        <label for="password">パスワード</label>
        <input
          id="password"
          v-model="password"
          type="password"
          minlength="8"
          maxlength="72"
          autocomplete="new-password"
          required
        />

        <label for="confirmPassword">パスワード（確認）</label>
        <input
          id="confirmPassword"
          v-model="confirmPassword"
          type="password"
          autocomplete="new-password"
          required
        />

        <button type="submit" :disabled="loading">
          {{ loading ? '登録中...' : '登録' }}
        </button>
      </form>

      <p v-if="errorMessage" class="error" role="alert">
        {{ errorMessage }}
      </p>

      <p v-if="successMessage" class="success" role="status">
        {{ successMessage }}
      </p>

      <RouterLink to="/login">ログインに戻る</RouterLink>
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
  box-sizing: border-box;
  width: 100%;
  max-width: 440px;
  padding: 32px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgb(0 0 0 / 8%);
}

h1 {
  font-size: 24px;
}

.subtitle {
  color: #666;
}

form {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

label {
  margin-top: 8px;
  font-weight: 600;
}

input {
  box-sizing: border-box;
  width: 100%;
  padding: 11px;
  border: 1px solid #ccc;
  border-radius: 6px;
}

button {
  margin-top: 16px;
  padding: 12px;
  color: white;
  background: #2563eb;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

button:disabled {
  opacity: 0.6;
  cursor: wait;
}

.error {
  color: #c62828;
}

.success {
  color: #16803c;
}
</style>
