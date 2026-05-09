import { defineStore } from 'pinia'
import api from '../services/api.js' // Importo api.js

export const useCategoryStore = defineStore('category', {
  state: () => ({
    categories: []
  }),
  actions: {
    async fetchCategories() {
      try {
        const response = await api.get('/categories')
        this.categories = response.data
      } catch (error) {
        console.error("Gabim gjatë marrjes së kategorive:", error)
      }
    }
  }
})