// src/stores/auth.ts
import { defineStore } from 'pinia';
import apiClient from '@/apiService';

// 定義使用者物件的型別，增加 mfaEnabled
interface User {
  username: string;
  email: string;
  mfaEnabled?: boolean; // 新增欄位
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as User | null,
    isAuthenticated: false,
    isMfaPending: false, // 新增: MFA 待定狀態 (密碼正確但未驗證 MFA)
    mfaSetupQrUri: null as string | null, // 新增: 用於顯示 QR Code
    loading: false, // 新增: 處理讀取狀態
    error: null as string | null, // 新增: 處理錯誤訊息
  }),

  actions: {
    /**
     * 處理使用者註冊
     */
    async register(credentials: any) {
      this.loading = true;
      this.error = null;
      try {
        await apiClient.post('/api/auth/register', credentials);
      } catch (err: any) {
        // 捕捉錯誤並存入 state
        this.error = err.response?.data || '註冊失敗';
        throw err;
      } finally {
        this.loading = false;
      }
    },

    /**
     * 處理使用者登入 (修改版: 支援 MFA)
     */
    async login(credentials: any) {
      this.loading = true;
      this.error = null;
      this.isMfaPending = false; // 重置

      try {
        const response = await apiClient.post('/api/auth/login', credentials);

        // 判斷後端回傳的狀態
        if (response.data.status === 'MFA_REQUIRED') {
            // Case A: 需要 MFA 驗證
            this.isMfaPending = true;
            this.user = { username: credentials.username } as User; // 暫存使用者名稱
            // 注意: 此時 isAuthenticated 仍為 false
        } else {
            // Case B: 登入成功 (無 MFA 或已通過)
            this.isAuthenticated = true;
            this.user = response.data;
            this.isMfaPending = false;
        }
      } catch (err: any) {
         this.error = err.response?.data || '登入失敗';
         throw err;
      } finally {
        this.loading = false;
      }
    },

    /**
     * 新增: 處理 MFA 二次驗證
     */
    async verifyMfa(username: string, mfaCode: string) {
        this.loading = true;
        this.error = null;
        try {
            const response = await apiClient.post('/api/auth/verify-mfa', {
                username,
                mfaCode
            });
            // 驗證成功，正式登入
            this.user = response.data;
            this.isAuthenticated = true;
            this.isMfaPending = false;
        } catch (err: any) {
            this.error = err.response?.data || '驗證碼錯誤';
            throw err;
        } finally {
            this.loading = false;
        }
    },

    /**
     * 新增: 請求啟用 MFA (獲取 QR Code)
     */
    async setupMfa() {
        this.loading = true;
        this.error = null;
        this.mfaSetupQrUri = null;
        try {
            const response = await apiClient.post('/api/user/mfa/setup');
            this.mfaSetupQrUri = response.data.qrUri;
        } catch (err: any) {
            this.error = err.response?.data || '無法啟用 MFA';
        } finally {
            this.loading = false;
        }
    },

    /**
     * 修改: 確認啟用 MFA
     * 修正重點: 加入 username 欄位，解決後端 400 錯誤
     */
    async confirmMfa(code: string) {
        this.loading = true;
        this.error = null;
        
        // 防呆：確保有使用者資訊
        if (!this.user?.username) {
            this.error = "無法獲取使用者資訊";
            this.loading = false;
            return;
        }

        try {
            // 👇👇👇 關鍵修正：Payload 必須包含 username 和 mfaCode 👇👇👇
            await apiClient.post('/api/user/mfa/confirm', { 
                username: this.user.username, 
                mfaCode: code 
            });
            
            // 更新本地狀態顯示已啟用
            if (this.user) {
                this.user.mfaEnabled = true;
            }
            this.mfaSetupQrUri = null; // 清除 QR Code
        } catch (err: any) {
            this.error = err.response?.data || '驗證碼錯誤';
            throw err;
        } finally {
            this.loading = false;
        }
    },

    async disableMfa() {
        this.loading = true;
        this.error = null;
        try {
            await apiClient.post('/api/user/mfa/disable');
            
            // 更新本地狀態
            if (this.user) {
                this.user.mfaEnabled = false;
            }
            alert('MFA 已成功停用。');
        } catch (err: any) {
            this.error = err.response?.data || '停用失敗，請稍後再試';
        } finally {
            this.loading = false;
        }
    },
    
    async logout() {
      try {
        await apiClient.post('/api/auth/logout');
      } finally {
        this.isAuthenticated = false;
        this.isMfaPending = false;
        this.user = null;
        this.mfaSetupQrUri = null;
      }
    },

    /**
     * 在頁面刷新時，嘗試從後端獲取使用者資訊
     * (注意: 確保後端 UserController 有對應的 GET /api/user/me 端點，或根據需要修改路徑)
     */
    async fetchUser() {
      if (this.isAuthenticated) return;

      try {
        // 這裡假設後端有 /api/user/me，如果沒有，請在後端 UserController 新增一個回傳當前 User 的端點
        const response = await apiClient.get('/api/user/me'); 
        this.isAuthenticated = true;
        this.user = response.data;
      } catch (error) {
        this.isAuthenticated = false;
        this.user = null;
      }
    }
  },
});