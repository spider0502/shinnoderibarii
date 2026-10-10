
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import axios from 'axios'
import { Odometer, List, Shop, Goods } from '@element-plus/icons-vue'

const backendStatus = ref('CHECKING')
const errorMessage = ref('')

onMounted(async () => {
  try {
    const response = await axios.get<{ status: string }>(
      'http://localhost:8080/api/health',
    )
    backendStatus.value = response.data.status
  } catch {
    backendStatus.value = 'ERROR'
    errorMessage.value = 'バックエンドに接続できません。'
  }
})
</script>

<template>
  <el-container class="layout">
    <el-aside width="220px" class="sidebar">
      <div class="brand">Shin no Delivery</div>
      <el-menu default-active="dashboard" class="side-menu">
        <el-menu-item index="dashboard">
          <el-icon><Odometer /></el-icon>
          <span>ダッシュボード</span>
        </el-menu-item>
        <el-menu-item index="orders">
          <el-icon><List /></el-icon>
          <span>注文管理</span>
        </el-menu-item>
        <el-menu-item index="stores">
          <el-icon><Shop /></el-icon>
          <span>店舗管理</span>
        </el-menu-item>
        <el-menu-item index="products">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="topbar">
        <span>宅配注文管理システム</span>
        <el-tag type="success">開発環境</el-tag>
      </el-header>

      <el-main class="content">
        <h1>ダッシュボード</h1>
        <p class="subtitle">システムの稼働状況を確認できます。</p>

        <el-row :gutter="20">
          <el-col :xs="24" :md="12">
            <el-card shadow="hover">
              <template #header>バックエンド API</template>
              <el-tag v-if="backendStatus === 'UP'" type="success">
                正常稼働中
              </el-tag>
              <el-tag v-else-if="backendStatus === 'CHECKING'" type="info">
                接続確認中
              </el-tag>
              <el-tag v-else type="danger">接続エラー</el-tag>
              <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
              <p class="endpoint">GET /api/health</p>
            </el-card>
          </el-col>

          <el-col :xs="24" :md="12">
            <el-card shadow="hover">
              <template #header>開発ステータス</template>
              <el-tag type="info">開発中</el-tag>
              <p>フロントエンドとバックエンドの接続確認</p>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </el-container>
</template>
