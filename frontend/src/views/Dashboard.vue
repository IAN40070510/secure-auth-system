<template>
  <div class="dashboard-container">
    <div class="welcome-section">
      <h1 v-if="authStore.user">歡迎, <span class="highlight">{{ authStore.user.username }}</span>!</h1>
      <h1 v-else>歡迎回來!</h1>
      
      <p>這是一個受保護的儀表板頁面。</p>
      <p v-if="authStore.user" class="email-text">
        你的電子郵件是: {{ authStore.user.email }}
      </p>
    </div>

    <hr class="divider" />

    <SecurityDashboard />

    <hr class="divider" />
    
    <button @click="handleLogout" class="logout-btn">登出</button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import SecurityDashboard from '@/views/SecurityDashboard.vue';

const authStore = useAuthStore();
const router = useRouter();

const handleLogout = async () => {
  await authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.dashboard-container {
  /* 👇 關鍵修正：確保寬度適應螢幕，不會被撐爆 👇 */
  width: 100%;            /* 佔滿可用空間 */
  max-width: 800px;       /* 限制最大寬度 */
  margin: 0 auto;         /* 水平置中 */
  padding: 40px 20px;     /* 上下 40px，左右 20px (保留呼吸空間) */
  
  font-family: Arial, sans-serif;
  color: white; 
}

.welcome-section {
  margin-bottom: 30px;
  text-align: center;
}

.highlight {
  color: #42b983;
  font-weight: bold;
}

.email-text {
  color: #aaa;
  font-size: 0.9rem;
  margin-top: 5px;
}

.divider {
  border: 0;
  border-top: 1px solid #333;
  margin: 30px 0;
}

.logout-btn {
  display: block;
  width: 100%;
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: bold;
  transition: background 0.3s;
}

.logout-btn:hover {
  background-color: #c0392b;
}
</style>