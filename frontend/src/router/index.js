import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import LoginView from '../views/LoginView.vue';

const routes = [
  { path: '/login', component: LoginView },
  { path: '/', component: HomeView }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// Kjo pjesë e detyron faqen me shku te Login nëse s'je i loguar
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  if (to.path !== '/login' && !token) {
    next('/login');
  } else {
    next();
  }
});

export default router;