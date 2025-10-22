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
      <button type="submit" :disabled="isLoading">{{ isLoading ? '登入中...' : '登入' }}</button>
    </form>
    <p v-if="error" class="error-message">{{ error }}</p>
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
const error = ref<string | null>(null);
const isLoading = ref(false);

const router = useRouter();
const authStore = useAuthStore();

const handleSubmit = async () => {
  isLoading.value = true;
  error.value = null;
  try {
    await authStore.login({
      username: username.value,
      password: password.value,
    });
    // 登入成功後，跳轉到儀表板
    router.push('/dashboard');
  } catch (err: any) {
    error.value = '使用者名稱或密碼錯誤。';
  } finally {
    isLoading.value = false;
  }
};
</script>