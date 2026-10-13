
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Odometer, User } from '@element-plus/icons-vue'
import axios from 'axios'

interface RegisterResponse {
  id: number
  name: string
  email: string
  role: string
}

interface ApiError {
  message?: string
}

const backendStatus = ref('CHECKING')
const submitting = ref(false)
const message = ref('')
const messageType = ref<'success' | 'error'>('success')
const registeredUser = ref<RegisterResponse | null>(null)

const form = reactive({
  name: '',
  email: '',
  password: '',
})

onMounted(async () => {
  try {
    await axios.get('http://localhost:8080/api/health')
    backendStatus.value = 'UP'
  } catch {
    backendStatus.value = 'ERROR'
  }
})

async function register() {
  message.value = ''
  registeredUser.value = null
  submitting.value = true

  try {
    const response = await axios.post<RegisterResponse>(
      'http://localhost:8080/api/users',
      {
        name: form.name,
        email: form.email,
        password: form.password,
      },
    )

    registeredUser.value = response.data
    message.value = 'ユーザー登録に成功しました！'
    messageType.value = 'success'

    form.name = ''
    form.email = ''
    form.password = ''
  } catch (error: unknown) {
    if (axios.isAxiosError<ApiError>(error)) {
      message.value =
        error.response?.data?.message ??
        '登録に失敗しました。バックエンドの状態を確認してください。'
    } else {
      message.value = '予期しないエラーが発生しました。'
    }

    messageType.value = 'error'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <el-container class="layout">
    <el-aside width="220px" class="sidebar">
      <div class="brand">Shin no Delivery</div>

      <el-menu default-active="register" class="side-menu">
        <el-menu-item index="dashboard">
          <el-icon><Odometer /></el-icon>
          <span>ダッシュボード</span>
        </el-menu-item>

        <el-menu-item index="register">
          <el-icon><User /></el-icon>
          <span>ユーザー登録</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="topbar">
        <span>宅配注文管理システム</span>
        <el-tag :type="backendStatus === 'UP' ? 'success' : 'danger'">
          {{ backendStatus === 'UP' ? 'API 正常稼働中' : 'API 未接続' }}
        </el-tag>
      </el-header>

      <el-main class="content">
        <h1>ユーザー登録</h1>
        <p class="subtitle">
          新しいユーザーのアカウントを作成します。
        </p>

        <el-card class="register-card" shadow="hover">
          <el-form
            :model="form"
            label-position="top"
            @submit.prevent="register"
          >
            <el-form-item label="名前">
              <el-input
                v-model="form.name"
                placeholder="山田 太郎"
                maxlength="100"
                autocomplete="name"
                required
              />
            </el-form-item>

            <el-form-item label="メールアドレス">
              <el-input
                v-model="form.email"
                placeholder="taro@example.com"
                type="email"
                maxlength="255"
                autocomplete="email"
                required
              />
            </el-form-item>

            <el-form-item label="パスワード">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="8文字以上"
                minlength="8"
                maxlength="72"
                show-password
                autocomplete="new-password"
                required
              />
            </el-form-item>

            <el-button
              type="primary"
              native-type="submit"
              :loading="submitting"
              :disabled="backendStatus !== 'UP'"
            >
              登録する
            </el-button>
          </el-form>

          <el-alert
            v-if="message"
            :title="message"
            :type="messageType"
            show-icon
            :closable="true"
            class="result-alert"
          />

          <div v-if="registeredUser" class="result-details">
            <p>ユーザー ID：{{ registeredUser.id }}</p>
            <p>名前：{{ registeredUser.name }}</p>
            <p>メールアドレス：{{ registeredUser.email }}</p>
            <p>ロール：{{ registeredUser.role }}</p>
          </div>
        </el-card>
      </el-main>
    </el-container>
  </el-container>
</template>
