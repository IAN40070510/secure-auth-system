// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router';
import Login from '@/views/Login.vue';
import Register from '@/views/Register.vue';
import Dashboard from '@/views/Dashboard.vue';
import { useAuthStore } from '@/stores/auth'; // 1. 引入我們的 Auth Store

const routes = [
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true } // 我們將根據這個 meta 標記來判斷是否需要保護
  },
  { path: '/', redirect: '/login' },
  { path: '/:pathMatch(.*)*', redirect: '/login' }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 2. 建立全域路由守衛
router.beforeEach(async (to, _from, next) => {
  // 在守衛內部獲取 store 實例
  const authStore = useAuthStore();

  // **關鍵邏輯：處理頁面刷新**
  // 如果 Pinia 中沒有登入狀態，就嘗試透過後端 session 恢復它
  if (!authStore.isAuthenticated) {
    await authStore.fetchUser();
  }

  // 檢查目標路由是否需要認證
  const requiresAuth = to.meta.requiresAuth;

  // 如果路由需要認證，但用戶最終仍未登入，則導向登入頁
  if (requiresAuth && !authStore.isAuthenticated) {
    next('/login');
  } else {
    // 否則，允許訪問
    next();
  }
});

export default router;