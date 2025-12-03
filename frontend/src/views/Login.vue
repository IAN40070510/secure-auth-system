<template>
  <div class="auth-container">
    <h1>登入系統</h1>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="username">使用者名稱</label>
        <input id="username" v-model="username" type="text" placeholder="Username" required />
      </div>
      <div class="form-group">
        <label for="password">密碼</label>
        <input id="password" v-model="password" type="password" placeholder="Password" required />
      </div>
      <button type="submit" :disabled="authStore.loading">
        {{ authStore.loading ? '登入中...' : '登入' }}
      </button>
    </form>
    
    <p v-if="authStore.error" class="error-message">{{ authStore.error }}</p>
    
    <p class="switch-form">
      還沒有帳號？ <router-link to="/register">點此註冊</router-link>
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const username = ref('');
const password = ref('');
const router = useRouter();
const authStore = useAuthStore();

const handleSubmit = async () => {
  try {
    await authStore.login({
      username: username.value,
      password: password.value,
    });
    
    // --- 關鍵修改 ---
    if (authStore.isMfaPending) {
      // 如果狀態是 MFA 待定，跳轉到驗證頁
      router.push('/verify-mfa');
    } else {
      // 一般登入成功，跳轉到儀表板
      router.push('/dashboard');
    }
  } catch (err) {
    // 錯誤已在 store 處理，這裡不需要額外操作，或者可以清空密碼
    password.value = '';
  }
};
</script>

<style scoped>
/* 簡單樣式 */
.auth-container { max-width: 400px; margin: 50px auto; padding: 20px; border: 1px solid #ccc; border-radius: 8px; }
.form-group { margin-bottom: 15px; }
.form-group label { display: block; margin-bottom: 5px; }
.form-group input { width: 100%; padding: 8px; box-sizing: border-box; }
.error-message { color: red; margin-top: 10px; }
button { width: 100%; padding: 10px; background-color: #42b983; color: white; border: none; border-radius: 4px; cursor: pointer; }
button:disabled { background-color: #a0d8b9; }
</style>