import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

import router from './router'
import store from './store'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css' // ep-dark-css
import './style-dark.css' // dark-style
import 'normalize.css'

createApp(App).use(store).use(router).use(ElementPlus).mount('#app')
