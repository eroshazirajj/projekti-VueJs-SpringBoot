<template>
  <div class="login-container">
    <div class="login-box">
      <h2>DREAM GARAGE</h2>
      <p class="mode-toggle">{{ isLogin ? 'Kyçu në sistem' : 'Krijo llogari të re' }}</p>
      
      <form @submit.prevent="handleSubmit">
        <div class="input-group">
          <label>Username</label>
          <input v-model="form.username" type="text" placeholder="Psh. eros_admin" required />
        </div>
        <div class="input-group">
          <label>Password</label>
          <input v-model="form.password" type="password" placeholder="••••••••" required />
        </div>
        <div class="input-group" v-if="!isLogin">
          <label>Zgjidh Rolin</label>
          <select v-model="form.role" class="role-select">
            <option value="ROLE_USER">User (Standard)</option>
            <option value="ROLE_ADMIN">Admin (Full Access)</option>
          </select>
        </div>
        
        <button type="submit" class="btn-submit" :disabled="loading">
          {{ loading ? 'Duke u procesuar...' : (isLogin ? 'Login' : 'Register') }}
        </button>

        <p class="switch-text">
          {{ isLogin ? "Nuk keni llogari?" : "Keni llogari?" }}
          <span @click="isLogin = !isLogin">{{ isLogin ? 'Regjistrohuni' : 'Kyçuni' }}</span>
        </p>
        
        <p v-if="message" :class="['msg', isError ? 'error' : 'success']">{{ message }}</p>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();
const router = useRouter();

const isLogin = ref(true);
const loading = ref(false);
const message = ref('');
const isError = ref(false);

const form = ref({ 
  username: '', 
  password: '',
  role: 'ROLE_USER' // Default role
});

const handleSubmit = async () => {
  loading.value = true;
  message.value = '';
  isError.value = false;
  
  try {
    if (isLogin.value) {
      await authStore.login({ 
        username: form.value.username, 
        password: form.value.password 
      });
      router.push('/');
    } else {
      // Register logic
      const response = await fetch('http://localhost:8081/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form.value)
      });
      
      if (!response.ok) throw new Error("Regjistrimi dështoi!");
      
      message.value = "Llogaria u krijua! Tani kyçuni.";
      isLogin.value = true;
    }

    } catch (err) {
    isError.value = true;
    
    // Kontrollojmë llojin e gabimit për të kthyer mesazh të personalizuar
    if (err.response && err.response.status === 403) {
      message.value = "Username ose Password nuk janë të sakta.";
    } else if (err.response && err.response.status === 401) {
      message.value = "Nuk keni autorizim për të hyrë.";
    } else if (err.message === "Regjistrimi dështoi!") {
      message.value = "Ky emër përdoruesi ekziston, provo një tjetër.";
    } else {
      message.value = "Ndodhi një gabim në server. Provo përsëri.";
    }
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;700;900&display=swap');

body{
  margin: 0;
}

.login-container { 
  display: flex; 
  justify-content: center; 
  align-items: center; 
  width: 100%;
  height: 100vh; 
  background: #050505; /* Black minimalist */
  font-family: 'Inter', sans-serif;
}

.login-box { 
  background: #0a0a0a; 
  padding: 60px 45px; 
  border-radius: 8px; 
  border: 1px solid #151515; 
  width: 100%;
  max-width: 400px; 
  box-shadow: 0 40px 100px rgba(0, 0, 0, 0.8);
}

h2 { 
  letter-spacing: 4px; 
  font-weight: 900; 
  margin-bottom: 10px;
  font-size: 1.8rem;
  text-align: center;
  color: #fff;
  text-transform: uppercase;
}

.mode-toggle { 
  font-size: 10px; 
  text-transform: uppercase; 
  color: #444; 
  margin-bottom: 40px; 
  letter-spacing: 2px;
  font-weight: 700;
  text-align: center;
}

.input-group { margin-bottom: 25px; }

label {
  display: block;
  font-size: 9px;
  letter-spacing: 2px;
  color: #555;
  margin-bottom: 8px;
  font-weight: 800;
  text-transform: uppercase;
}

input, .role-select { 
  width: 100%; 
  padding: 16px; 
  background: #111; 
  border: 1px solid #222; 
  color: #fff; 
  border-radius: 4px;
  font-size: 14px;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

input:focus, .role-select:focus {
  outline: none;
  border-color: #fff;
  background: #151515;
}

.btn-submit { 
  width: 100%; 
  padding: 18px; 
  background: #fff; 
  color: #000; 
  border: none; 
  font-weight: 900; 
  font-size: 12px;
  cursor: pointer; 
  margin-top: 15px; 
  letter-spacing: 2px;
  text-transform: uppercase;
  transition: transform 0.2s ease;
}

.btn-submit:hover:not(:disabled) {
  transform: scale(1.02);
  background: #f0f0f0;
}

.btn-submit:disabled {
  background: #222;
  color: #444;
}

.switch-text { 
  font-size: 11px; 
  margin-top: 30px; 
  color: #555; 
  text-align: center;
}

.switch-text span { 
  color: #fff; 
  cursor: pointer; 
  margin-left: 5px;
  font-weight: 700;
}

/* Përditëso këtë te pjesa <style> */
.msg { 
  font-size: 11px; 
  margin-top: 25px; 
  text-align: center;
  padding: 12px;
  border-radius: 4px;
  letter-spacing: 0.5px;
  font-weight: 500;
  text-transform: none; /* Mos i bëj uppercase mesazhet e gabimit, duken më pak agresive */
}

.error { background: rgba(255, 68, 68, 0.1); color: #ff4444; border: 1px solid rgba(255, 68, 68, 0.2); }
.success { background: rgba(0, 255, 136, 0.1); color: #00ff88; border: 1px solid rgba(0, 255, 136, 0.2); }
</style>