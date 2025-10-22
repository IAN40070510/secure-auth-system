<template>
  <main>
    <div class="background-grid"></div>
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </main>
</template>

<style>
/* 引入科技感的字体 (可选，但推荐) */
@import url('https://fonts.googleapis.com/css2?family=Roboto+Mono:wght@300;400;500&display=swap');

/* --- 全局样式变量 --- */
:root {
  --background-color: #0a0f1e; /* 深海军蓝背景 */
  --primary-color: #00f2ea;     /* 科技青色 (强调色) */
  --glow-color: rgba(0, 242, 234, 0.5); /* 光晕颜色 */
  --text-color: #e0e0e0;        /* 柔和的白色文字 */
  --border-color: rgba(0, 242, 234, 0.2); /* 边框颜色 */
  --input-bg-color: rgba(255, 255, 255, 0.05); /* 输入框背景 */
  --error-color: #ff4d4d;       /* 错误红色 */
}

/* --- 基础样式重置与设定 --- */
body {
  font-family: 'Roboto Mono', monospace; /* 使用科技感字体 */
  margin: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: var(--background-color);
  color: var(--text-color);
  overflow: hidden; /* 隐藏滚动条 */
}

/* --- 动态背景网格 --- */
.background-grid {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
    linear-gradient(to right, rgba(0, 242, 234, 0.1) 1px, transparent 1px),
    linear-gradient(to bottom, rgba(0, 242, 234, 0.1) 1px, transparent 1px);
  background-size: 40px 40px;
  animation: pan-grid 60s linear infinite;
  z-index: -1;
}

@keyframes pan-grid {
  0% { background-position: 0 0; }
  100% { background-position: 400px 400px; }
}


/* --- 核心容器样式 (用于登录、注册、仪表板) --- */
.auth-container, .dashboard-container {
  background: rgba(10, 15, 30, 0.8); /* 半透明背景 */
  padding: 2.5rem;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  box-shadow: 0 0 30px var(--glow-color); /* 核心发光效果 */
  width: 100%;
  max-width: 400px;
  backdrop-filter: blur(10px); /* 毛玻璃效果 */
  z-index: 1;
}

/* --- 表单元素样式 --- */
.form-group {
  margin-bottom: 1.5rem;
}

h1 {
  color: var(--primary-color);
  text-align: center;
  margin-bottom: 2rem;
  text-shadow: 0 0 10px var(--glow-color);
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 300;
  color: #a0a0a0;
}

input {
  width: 100%;
  padding: 0.85rem;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  box-sizing: border-box;
  background-color: var(--input-bg-color);
  color: var(--text-color);
  font-family: 'Roboto Mono', monospace;
  transition: all 0.3s ease;
}

input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 15px var(--glow-color);
}

button {
  width: 100%;
  padding: 0.85rem;
  background: transparent;
  color: var(--primary-color);
  border: 1px solid var(--primary-color);
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  font-family: 'Roboto Mono', monospace;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

button:hover {
  background: var(--primary-color);
  color: var(--background-color);
  box-shadow: 0 0 20px var(--glow-color);
}

button:disabled {
  border-color: #555;
  color: #555;
  cursor: not-allowed;
  background: transparent;
  box-shadow: none;
}

/* --- 提示信息样式 --- */
.error-message {
  color: var(--error-color);
  margin-top: 1rem;
  text-align: center;
  text-shadow: 0 0 5px rgba(255, 77, 77, 0.5);
}

.switch-form {
  margin-top: 1.5rem;
  text-align: center;
}

.switch-form a {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 500;
}

/* --- 仪表板特定样式 --- */
.dashboard-container p {
  text-align: center;
  line-height: 1.6;
}

.dashboard-container p:first-of-type {
  font-size: 1.2rem;
  margin-bottom: 2rem;
}

/* --- 页面切换动画 --- */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>