// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router';
import Login from '@/views/Login.vue';
import Register from '@/views/Register.vue'; // 假設您有這個檔案
import Dashboard from '@/views/Dashboard.vue';
import MFAVerify from '@/views/MFAVerify.vue'; // <--- 新增引入
import { useAuthStore } from '@/stores/auth';

const routes = [
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  },
  // --- 新增: MFA 驗證頁面路由 ---
  {
    path: '/verify-mfa',
    name: 'MFAVerify',
    component: MFAVerify,
    meta: { requiresMfaPending: true } // 自訂 meta: 需要 MFA 待定狀態
  },
  { path: '/', redirect: '/login' },
  { path: '/:pathMatch(.*)*', redirect: '/login' }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 全域路由守衛
router.beforeEach(async (to, _from, next) => {
  const authStore = useAuthStore();

  // 頁面刷新處理：嘗試恢復登入狀態
  if (!authStore.isAuthenticated && !authStore.isMfaPending) {
    await authStore.fetchUser();
  }

  const isAuthenticated = authStore.isAuthenticated;
  const isMfaPending = authStore.isMfaPending;
  
  // 權限檢查
  if (to.meta.requiresAuth) {
    // 1. 需要登入的頁面 (Dashboard)
    if (isAuthenticated) {
      next();
    } else if (isMfaPending) {
      // 密碼對了但還沒驗 MFA -> 去驗證頁
      next('/verify-mfa');
    } else {
      next('/login');
    }
  } else if (to.meta.requiresMfaPending) {
    // 2. MFA 驗證頁面
    if (isMfaPending) {
      next();
    } else if (isAuthenticated) {
      // 已經登入了，不需要驗證 -> 去首頁
      next('/dashboard');
    } else {
      // 沒登入 -> 去登入頁
      next('/login');
    }
  } else {
    // 3. 公開頁面 (Login/Register)
    // 如果已經登入，訪問 login 自動跳轉 dashboard
    if (to.path === '/login' && isAuthenticated) {
        next('/dashboard');
    } else {
        next();
    }
  }
});

export default router;