import { createApp } from 'vue'
import { createPinia } from 'pinia' // 1. Importo Pinia
import App from './App.vue'
import router from './router'

const app = createApp(App)
const pinia = createPinia() // 2. Krijo instancën

app.use(pinia) // 3. DUHET të jetë para router-it dhe mount-it
app.use(router)

app.mount('#app')