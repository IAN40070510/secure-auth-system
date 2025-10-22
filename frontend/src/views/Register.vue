<template>
  <div class="auth-container">
    <h1>建立您的帳號</h1>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="username">使用者名稱</label>
        <input id="username" v-model="username" type="text" placeholder="Username" required />
      </div>
      <div class="form-group">
        <label for="email">電子郵件</label>
        <input id="email" v-model="email" type="email" placeholder="Email" required />
      </div>
      <div class="form-group">
        <label for="password">密碼</label>
        <input id="password" v-model="password" type="password" placeholder="Password" required />
      </div>
      <button type="submit" :disabled="isLoading">{{ isLoading ? '註冊中...' : '註冊' }}</button>
    </form>
    <p v-if="error" class="error-message">{{ error }}</p>
    <p class="switch-form">
      已經有帳號了？ <router-link to="/login">點此登入</router-link>
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const username = ref('');
const email = ref('');
const password = ref('');
const error = ref<string | null>(null);
const isLoading = ref(false);

const router = useRouter();
const authStore = useAuthStore();

const handleSubmit = async () => {
  isLoading.value = true;
  error.value = null;
  try {
    await authStore.register({
      username: username.value,
      email: email.value,
      password: password.value,
    });
    // 註冊成功後，引導使用者去登入頁面
    router.push('/login');
  } catch (err: any) {
    error.value = err.response?.data?.message || '註冊失敗，請稍後再試。';
  } finally {
    isLoading.value = false;
  }
};
</script>