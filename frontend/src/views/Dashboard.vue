<template>
  <div class="dashboard-container">
    <h1>歡迎, {{ authStore.user?.username }}!</h1>
    <p>這是一個受保護的儀表板頁面。</p>
    <p>你的電子郵件是: {{ authStore.user?.email }}</p>
    <button @click="handleLogout">登出</button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const router = useRouter();

const handleLogout = async () => {
  await authStore.logout();
  router.push('/login');
};
</script>