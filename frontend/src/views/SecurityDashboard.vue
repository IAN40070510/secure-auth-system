<template>
  <div class="security-card">
    <h3>🔐 帳戶安全設定</h3>
    
    <div class="status-box">
      當前狀態：
      <strong :class="isMfaEnabled ? 'text-green' : 'text-orange'">
        {{ isMfaEnabled ? '已啟用 MFA 保護' : '未啟用 MFA' }}
      </strong>
    </div>

    <div v-if="!isMfaEnabled">
      <p>啟用雙重驗證可以大幅提升您的帳號安全性。</p>
      
      <div v-if="!qrUri">
        <button @click="authStore.setupMfa()" :disabled="authStore.loading">
            開啟雙重驗證設定
        </button>
      </div>

      <div v-else class="setup-area">
        <div class="step">
          <h4>步驟 1：掃描 QR Code</h4>
          <p>請開啟 Google Authenticator 並掃描下方條碼：</p>
          <img :src="qrUri" alt="MFA QR Code" class="qr-img" />
        </div>

        <div class="step">
          <h4>步驟 2：驗證確認</h4>
          <input 
            v-model="verifyCode" 
            type="text" 
            placeholder="輸入 6 位驗證碼" 
            maxlength="6"
            class="code-input"
          />
          <button @click="confirmSetup" :disabled="authStore.loading">
            確認啟用
          </button>
        </div>
      </div>
    </div>

    <div v-else>
      <p>您的帳號目前受到高規格保護。</p>
      
      <div class="disable-section">
        <button 
            @click="handleDisable" 
            class="btn-danger" 
            :disabled="authStore.loading"
        >
            {{ authStore.loading ? '處理中...' : '❌ 停用雙重驗證' }}
        </button>
      </div>
    </div>

    <p v-if="authStore.error" class="error-message">{{ authStore.error }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const verifyCode = ref('');

const isMfaEnabled = computed(() => authStore.user?.mfaEnabled || false);
const qrUri = computed(() => authStore.mfaSetupQrUri);

const confirmSetup = async () => {
    try {
        await authStore.confirmMfa(verifyCode.value);
        alert('MFA 啟用成功！下次登入時生效。');
    } catch (e) {
        // 錯誤已顯示在畫面上
    }
};

// 👇👇👇 補上缺少的 handleDisable 函式 👇👇👇
const handleDisable = async () => {
    // 增加確認視窗，防止誤觸
    if (confirm('確定要取消雙重驗證嗎？這將降低您的帳戶安全性。')) {
        await authStore.disableMfa();
    }
};
// 👆👆👆 補上結束 👆👆👆
</script>

<style scoped>
/* 設定卡片深色背景 */
.security-card { 
  border: 1px solid #333;
  padding: 20px; 
  border-radius: 8px; 
  margin-top: 20px; 
  box-shadow: 0 2px 4px rgba(0,0,0,0.2); 
  background-color: #1a1a1a; 
  color: white; 
}

.text-green { color: #2ecc71; }
.text-orange { color: #f39c12; }

.qr-img { 
  border: 5px solid white; 
  box-shadow: 0 0 5px rgba(0,0,0,0.2); 
  margin: 10px 0; 
  max-width: 100%;
}

.code-input { 
  width: 100%;
  max-width: 200px;
  padding: 10px; 
  text-align: center; 
  font-size: 1.2em;
  letter-spacing: 2px;
  border: 2px solid #ccc;
  border-radius: 4px;
  margin-right: 10px;
  
  /* 確保輸入框文字可見 (黑字白底) */
  color: #000000 !important;
  background-color: #ffffff !important;
}

.error-message { color: #e74c3c; margin-top: 10px; }

/* MFA 設定區塊 */
.setup-area { 
  background: #f9f9f9; 
  padding: 15px; 
  border-radius: 5px; 
  margin-top: 10px; 
  color: #000000; /* 強制黑字 */
}

/* 確保裡面的標題和段落也是黑色 */
.setup-area h4, 
.setup-area p {
  color: #000000; 
}

/* 預設按鈕 (藍色) */
button { 
  background-color: #007bff; 
  color: white; 
  border: none; 
  padding: 8px 16px; 
  border-radius: 4px; 
  cursor: pointer; 
  transition: background 0.3s;
}

button:disabled { 
  opacity: 0.6; 
  cursor: not-allowed;
}

/* 👇👇👇 新增：紅色危險按鈕樣式 👇👇👇 */
.btn-danger {
  background-color: #e74c3c; /* 紅色 */
}

.btn-danger:hover {
  background-color: #c0392b; /* 深紅色 */
}

.disable-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #333; /* 分隔線 */
}
/* 👆👆👆 新增結束 👆👆👆 */

.step { margin-bottom: 20px; }
</style>