import { defineStore } from "pinia";
import axios from "axios";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    user: JSON.parse(localStorage.getItem("user")) || null,
    token: localStorage.getItem("token") || null,
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
    userRole: (state) => state.user?.role || "ROLE_USER",
    isAdmin: (state) => state.user?.role === "ROLE_ADMIN" || state.user?.role === "ADMIN",
  },
  actions: {
    async login(userData) {
      try {
        const response = await axios.post("http://localhost:8081/api/auth/login", userData);
        
        const token = response.data.accessToken || response.data.token;
        const username = response.data.username;
        const role = response.data.role;

        if (token) {
          this.token = token;
          this.user = { username, role };

          localStorage.setItem("token", token);
          localStorage.setItem("user", JSON.stringify(this.user));
          axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
        }
        return response.data;
      } catch (error) {
        console.error("Login Error:", error);
        throw error;
      }
    },
    logout() {
      this.token = null;
      this.user = null;
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      delete axios.defaults.headers.common["Authorization"];
    },
    initializeAuth() {
      if (this.token) {
        axios.defaults.headers.common["Authorization"] = `Bearer ${this.token}`;
      }
    }
  },
});