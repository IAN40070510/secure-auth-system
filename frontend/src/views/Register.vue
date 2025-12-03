<template>
  <div class="register-page">
    <div class="auth-container">
      <h1>建立您的帳號</h1>
      
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="username">使用者名稱</label>
          <input 
            id="username" 
            v-model="username" 
            type="text" 
            placeholder="Username" 
            required 
          />
        </div>

        <div class="form-group">
          <label for="email">電子郵件</label>
          <input 
            id="email" 
            v-model="email" 
            type="email" 
            placeholder="Email" 
            required 
          />
        </div>

        <div class="form-group">
          <label for="password">密碼</label>
          <input 
            id="password" 
            v-model="password" 
            type="password" 
            placeholder="Password" 
            required 
          />
        </div>

        <button type="submit" :disabled="authStore.loading">
          {{ authStore.loading ? '註冊中...' : '註冊' }}
        </button>
      </form>

      <p v-if="authStore.error" class="error-message">{{ authStore.error }}</p>

      <p class="switch-form">
        已經有帳號了？ <router-link to="/login">點此登入</router-link>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const username = ref('');
const email = ref('');
const password = ref('');

const router = useRouter();
const authStore = useAuthStore();

const handleSubmit = async () => {
  try {
    // 使用 store 的 register action
    await authStore.register({
      username: username.value,
      email: email.value,
      password: password.value,
    });
    // 註冊成功後，引導使用者去登入頁面
    alert('註冊成功！請登入。');
    router.push('/login');
  } catch (err: any) {
    // 錯誤通常已經由 store 的 error state 處理
    // 如果需要額外處理可以在這裡寫
  }
};
</script>

<style scoped>
/* 1. 外層容器：負責「全螢幕置中」 */
.register-page {
  display: flex;
  justify-content: center; /* 水平置中 */
  align-items: center;     /* 垂直置中 */
  min-height: 100vh;       /* 佔滿整個螢幕高度 */
  width: 100%;
  padding: 20px;
  background-color: #0d1117; /* 防止背景露白 */
}

/* 2. 表單容器：深色背景、發光邊框 */
.auth-container {
  width: 100%;
  max-width: 400px; /* 限制最大寬度，避免太寬 */
  padding: 40px;
  background-color: #0d1117; /* 深色背景 */
  border: 1px solid #30363d;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 255, 255, 0.1); /* 青色光暈 */
  text-align: center;
  color: white;
}

h1 {
  margin-bottom: 30px;
  color: #00ffff; /* 青色標題 */
  text-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
}

.form-group {
  margin-bottom: 20px;
  text-align: left;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #8b949e; /* 淺灰色標籤 */
}

/* 3. 輸入框修正：確保文字看得見 */
input {
  width: 100%;
  padding: 12px;
  background-color: #21262d; /* 深灰色背景 */
  border: 1px solid #30363d;
  border-radius: 6px;
  color: #ffffff; /* ❗關鍵修正：強制文字為白色❗ */
  font-size: 1rem;
  outline: none;
  transition: border-color 0.3s;
}

input:focus {
  border-color: #00ffff;
}

/* 4. 按鈕樣式 */
button {
  width: 100%;
  padding: 12px;
  background-color: #21262d; /* 暗色按鈕 */
  color: #00ffff;            /* 青色文字 */
  border: 1px solid #00ffff;
  border-radius: 6px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
  margin-top: 10px;
}

button:hover {
  background-color: #00ffff;
  color: #0d1117;
  box-shadow: 0 0 15px rgba(0, 255, 255, 0.4);
}

button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 5. 錯誤訊息樣式：鮮紅色，確保看得見 */
.error-message {
  color: #ff6b6b; /* 亮紅色 */
  margin-top: 15px;
  background: rgba(255, 0, 0, 0.1); /* 淡淡的紅色背景 */
  padding: 10px;
  border-radius: 4px;
}

.switch-form {
  margin-top: 20px;
  color: #8b949e;
}

.switch-form a {
  color: #00ffff;
  text-decoration: none;
}

.switch-form a:hover {
  text-decoration: underline;
}
</style>