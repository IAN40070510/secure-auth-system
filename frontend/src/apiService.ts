// src/apiService.ts
import axios from 'axios';

// 1. 建立 Axios 實例
const apiClient = axios.create({
  baseURL: 'http://localhost:8080', // 後端 API 的基礎 URL
  withCredentials: true,             // 允許跨域請求攜帶 Cookie
});

// 2. 建立請求攔截器來自動處理 CSRF Token
apiClient.interceptors.request.use(config => {
  // 從 Cookie 中讀取 'XSRF-TOKEN'
  const getCookie = (name: string) => {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop()?.split(';').shift();
  }

  const csrfToken = getCookie('XSRF-TOKEN');

  // 如果是 POST, PUT, DELETE 等會改變狀態的請求，且 Token 存在，
  // 就將 Token 加入到 'X-XSRF-TOKEN' Header 中
  if (csrfToken && config.method && ['post', 'put', 'delete'].includes(config.method)) {
    config.headers['X-XSRF-TOKEN'] = csrfToken;
  }

  return config;
}, error => {
  return Promise.reject(error);
});

export default apiClient;