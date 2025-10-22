// src/stores/auth.ts
import { defineStore } from 'pinia';
import apiClient from '@/apiService'; 

// 定義使用者物件的型別，增加程式碼健壯性
interface User {
  username: string;
  email: string;
}

export const useAuthStore = defineStore('auth', {
  // State: 定義這個 store 的資料
  state: () => ({
    user: null as User | null,
    isAuthenticated: false,
  }),

  // Actions: 定義操作 state 的方法
  actions: {
    /**
     * 處理使用者註冊
     */
    async register(credentials: any) {
      // 我們只呼叫 API，不改變登入狀態
      await apiClient.post('/api/auth/register', credentials);
    },

    /**
     * 處理使用者登入
     */
    async login(credentials: any) {
      // 呼叫後端登入 API
      const response = await apiClient.post('/api/auth/login', credentials);

      // 登入成功後，更新 state
      this.isAuthenticated = true;
      this.user = response.data; // 後端會回傳使用者資訊
    },

    /**
     * 處理使用者登出
     */
    async logout() {
      try {
        await apiClient.post('/api/auth/logout');
      } finally {
        // 無論後端登出是否成功 (例如 session 已過期)，前端都必須重置狀態
        this.isAuthenticated = false;
        this.user = null;
      }
    },

    /**
     * 在頁面刷新時，嘗試從後端獲取使用者資訊以恢復登入狀態
     */
    async fetchUser() {
      // 如果 state 中已經是登入狀態，就不重複請求
      if (this.isAuthenticated) return;

      try {
        // 呼叫後端 API 來驗證 session cookie 是否有效
        const response = await apiClient.get('/api/user/me');
        this.isAuthenticated = true;
        this.user = response.data;
      } catch (error) {
        // 如果請求失敗 (例如 cookie 失效，後端回 401)，就確保狀態是登出的
        this.isAuthenticated = false;
        this.user = null;
      }
    }
  },
});