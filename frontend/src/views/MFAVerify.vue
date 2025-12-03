<template>
  <div class="auth-container">
    <h2>二步驟驗證</h2>
    <p>請輸入您的 Authenticator App 上的 6 位數驗證碼。</p>
    
    <form @submit.prevent="handleVerify">
      <div class="form-group">
        <input 
          v-model="code" 
          type="text" 
          maxlength="6" 
          placeholder="000000" 
          required 
          class="code-input"
        />
      </div>
      <button type="submit" :disabled="authStore.loading">
        {{ authStore.loading ? '驗證中...' : '送出驗證' }}
      </button>
    </form>
    
    <p v-if="authStore.error" class="error-message">{{ authStore.error }}</p>
    
    <div style="margin-top: 20px;">
        <a href="#" @click.prevent="backToLogin">返回登入</a>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const code = ref('');
const router = useRouter();
const authStore = useAuthStore();

const handleVerify = async () => {
    if (!authStore.user?.username) {
        router.push('/login');
        return;
    }

    try {
        // 呼叫 Store 的 MFA 驗證 Action
        await authStore.verifyMfa(authStore.user.username, code.value);
        router.push('/dashboard');
    } catch (err) {
        // 錯誤訊息由 store 管理
        code.value = ''; // 清空輸入框
    }
};

const backToLogin = async () => {
    await authStore.logout();
    router.push('/login');
};
</script>

<style scoped>
.auth-container { max-width: 400px; margin: 50px auto; padding: 20px; border: 1px solid #ccc; border-radius: 8px; text-align: center; }
.code-input { width: 100%; padding: 12px; font-size: 1.5rem; text-align: center; letter-spacing: 0.5rem; margin-bottom: 15px; }
.error-message { color: red; margin-top: 10px; }
button { width: 100%; padding: 10px; background-color: #3498db; color: white; border: none; border-radius: 4px; cursor: pointer; }
button:disabled { background-color: #85c1e9; }
</style>