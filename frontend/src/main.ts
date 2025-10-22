import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router' // 確保這行路徑正確
import './style.css' // 你可以刪除這個檔案或保留

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')