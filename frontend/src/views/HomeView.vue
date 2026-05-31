<template>
  <div class="app-container">
    <header class="main-header">
      <div class="header-top">
        <div class="user-welcome">
          <span class="welcome-label">Hello,</span>
          <span class="highlight username">{{ welcomeName }}</span>
          <button @click="handleLogout" class="btn-logout">Logout</button>
        </div>
      </div>

      <div class="header-content">
        <h1>DREAM <span class="thin">GARAGE</span></h1>
        <p class="subtitle">INVENTORY MANAGEMENT SYSTEM</p>
      </div>
    </header>

    <main class="content">
      <section class="user-hero-section" v-if="!authStore.isAdmin">
        <div class="hero-card">
          <div class="hero-text">
            <h2>Explore the <span class="highlight">Elite Collection</span></h2>
            <p>Browse through our curated inventory of high-performance vehicles and luxury classics.</p>
          </div>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-value">{{ products.length }}</div>
              <div class="stat-label">Units Available</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">€ {{ totalValue }}</div>
              <div class="stat-label">Market Value</div>
            </div>
          </div>
        </div>
      </section>
      <section class="form-section" v-if="authStore.isAdmin">
        <div class="form-card">
          <h2 class="section-title">
            {{ isEditing ? "Edit Vehicle Details" : "Register New Vehicle" }}
          </h2>

          <div class="input-grid">
            <div class="input-group">
              <label>Model Name</label>
              <input v-model="form.name" placeholder="e.g. Porsche 911 GT3 RS" />
            </div>
            <div class="input-group">
              <label>Market Price (€)</label>
              <input v-model.number="form.price" type="number" placeholder="223000" />
            </div>
            <div class="input-group">
              <label>Category</label>
              <select v-model="form.categoryId" class="custom-select">
                <option :value="null">Select Category</option>
                <option v-for="cat in categoryStore.categories" :key="cat.id" :value="cat.id">
                  {{ cat.name }}
                </option>
              </select>
            </div>
            <div class="input-group">
              <label>Image URL</label>
              <input v-model="form.imageUrl" placeholder="https://..." />
            </div>
            <div class="input-group full-width">
              <label>Description</label>
              <textarea v-model="form.description" placeholder="Vehicle history, specs, etc."></textarea>
            </div>
          </div>

          <div class="form-actions">
            <button @click="submitForm" class="btn-primary">
              {{ isEditing ? "Update Entry" : "Add to Collection" }}
            </button>
            <button v-if="isEditing" @click="cancelEdit" class="btn-secondary">
              Cancel
            </button>
          </div>
        </div>
      </section>
      <section class="list-section">
        <h2 class="section-title">Current Inventory ({{ products.length }})</h2>

        <div v-if="products.length === 0" class="empty-state">
          No vehicles in garage yet.
        </div>

        <div class="product-grid">
          <div v-for="p in products" :key="p.id" class="product-card">
            <div class="card-image">
              <img :src="p.imageUrl || 'https://via.placeholder.com/400x250?text=No+Image'" alt="Car" />
              <div class="image-overlay">
                <span class="price-badge">{{ formatCurrency(p.price) }}</span>
              </div>
            </div>

            <div class="card-info">
              <div class="info-header">
                <span class="category-tag">{{ p.category?.name || "Uncategorized" }}</span>
                <span class="status-indicator"></span>
              </div>
              <h3>{{ p.name }}</h3>
              <p>{{ p.description }}</p>
            </div>

            <div class="card-actions" v-if="authStore.isAdmin">
              <button @click="startEdit(p)" class="btn-edit">Edit</button>
              <button @click="deleteProduct(p.id)" class="btn-delete">Delete</button>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import api from "../services/api.js";
import { useAuthStore } from "../stores/auth.js";
import { useCategoryStore } from "../stores/categoryStore.js";

const authStore = useAuthStore();
const categoryStore = useCategoryStore();
const router = useRouter();

const welcomeName = computed(() => authStore.user?.username || "Guest");

const products = ref([]);
const isEditing = ref(false);
const editId = ref(null);
const form = ref({
  name: "",
  price: null,
  description: "",
  categoryId: null,
  imageUrl: "",
});

const handleLogout = () => {
  authStore.logout();
  router.push("/login");
};

const fetchProducts = async () => {
  try {
    const res = await api.get("/products");
    products.value = res.data;
  } catch (err) {
    console.error("Error fetching products", err);
  }
};

const submitForm = async () => {
  if (!form.value.name || !form.value.price || !form.value.categoryId) {
    return alert("Ju lutem plotësoni fushat kryesore.");
  }

  const payload = {
    name: form.value.name,
    price: form.value.price,
    description: form.value.description,
    imageUrl: form.value.imageUrl,
    category: { id: form.value.categoryId },
  };

  try {
    if (isEditing.value) {
      await api.put(`/products/${editId.value}`, payload);
    } else {
      await api.post("/products", payload);
    }
    cancelEdit();
    fetchProducts();
  } catch (err) {
    console.error("Gabim:", err);
  }
};

const startEdit = (product) => {
  isEditing.value = true;
  editId.value = product.id;
  form.value = {
    name: product.name,
    price: product.price,
    description: product.description,
    imageUrl: product.imageUrl,
    categoryId: product.category ? product.category.id : null,
  };
  window.scrollTo({ top: 0, behavior: "smooth" });
};

const cancelEdit = () => {
  isEditing.value = false;
  editId.value = null;
  form.value = { name: "", price: null, description: "", categoryId: null, imageUrl: "" };
};

const deleteProduct = async (id) => {
  if (confirm("Konfirmoni fshirjen?")) {
    try {
      await api.delete(`/products/${id}`);
      fetchProducts();
    } catch (err) {
      console.error("Gabim:", err);
    }
  }
};

const formatCurrency = (value) => {
  return new Intl.NumberFormat("de-DE", {
    style: "currency",
    currency: "EUR",
    maximumFractionDigits: 0,
  }).format(value);
};

const totalValue = computed(() => {
  const total = products.value.reduce((acc, p) => acc + (p.price || 0), 0);
  return new Intl.NumberFormat("de-DE").format(total);
});

onMounted(() => {
  authStore.initializeAuth();
  fetchProducts();
  categoryStore.fetchCategories();
});
</script>

<style scoped>
/* 1. GLOBAL & CONTAINER */
.app-container {
  background-color: #fcfcfc;
  min-height: 100vh;
  font-family: 'Inter', sans-serif;
  color: #000;
}

.content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 80px;
}

/* 2. MODERN HEADER */
.main-header {
  background: #fff;
  padding: 40px 0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 60px;
}

.header-top {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: center;
}

.user-welcome {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #f8f8f8;
  padding: 6px 15px;
  border-radius: 50px;
  border: 1px solid #eee;
}

.welcome-label { font-size: 11px; color: #888; text-transform: uppercase; font-weight: 600; }
.username { font-weight: 800; font-size: 12px; }

.btn-logout {
  all: unset;
  cursor: pointer;
  font-size: 9px;
  font-weight: 900;
  text-transform: uppercase;
  color: #ff4444;
  border-left: 1px solid #ddd;
  padding-left: 10px;
  margin-left: 5px;
}

.header-content { text-align: center; margin-top: 30px; }
.header-content h1 { font-size: 3.5rem; font-weight: 900; letter-spacing: -3px; margin: 0; line-height: 1; }
.thin { font-weight: 100; color: #ccc; }
.subtitle { font-size: 10px; letter-spacing: 5px; color: #999; margin-top: 10px; text-transform: uppercase; font-weight: 700; }

.hero-card {
  background: #000;
  color: #fff;
  padding: 60px;
  border-radius: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 80px;
  box-shadow: 0 40px 80px rgba(0,0,0,0.15);
}

.hero-text h2 { font-size: 2.8rem; font-weight: 900; letter-spacing: -1px; margin-bottom: 15px; }
.hero-text p { color: #666; font-size: 1.1rem; max-width: 400px; }

.highlight {
  color: #000000 !important;
  background: transparent !important;
  text-decoration: underline;
  text-underline-offset: 8px;
  text-decoration-thickness: 2px;
}

.stats-grid { display: flex; gap: 50px; }
.stat-item { display: flex; flex-direction: column; gap: 5px; }
.stat-value { font-size: 3rem; font-weight: 200; line-height: 1; color: #fff; }
.stat-label { font-size: 10px; text-transform: uppercase; letter-spacing: 2px; color: #555; font-weight: 700; }

/* 4. ADMIN FORM */
.form-card {
  background: #fff;
  padding: 50px;
  border: 1px solid #eee;
  border-radius: 12px;
  margin-bottom: 80px;
}

.section-title { font-size: 12px; letter-spacing: 3px; text-transform: uppercase; font-weight: 900; margin-bottom: 40px; border-left: 4px solid #000; padding-left: 15px; }

.input-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 30px; }
.input-group label { display: block; font-size: 10px; font-weight: 900; text-transform: uppercase; margin-bottom: 10px; color: #888; }
input, textarea, select { width: 100%; padding: 16px; border: 1px solid #eee; background: #fbfbfb; border-radius: 6px; font-size: 14px; box-sizing: border-box; transition: all 0.2s; }
input:focus { outline: none; border-color: #000; background: #fff; }
.full-width { grid-column: span 2; }

.form-actions { margin-top: 30px; display: flex; gap: 15px; }
.btn-primary { background: #000; color: #fff; padding: 18px 40px; border: none; border-radius: 6px; font-weight: 900; text-transform: uppercase; font-size: 12px; letter-spacing: 1px; cursor: pointer; }
.btn-secondary { background: #fff; border: 1px solid #eee; padding: 18px 40px; font-weight: 900; text-transform: uppercase; font-size: 12px; cursor: pointer; }

/* 5. PRODUCT LISTING */
.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(360px, 1fr)); gap: 40px; }
.product-card { background: #fff; border: 1px solid #eee; border-radius: 12px; overflow: hidden; transition: transform 0.4s cubic-bezier(0.165, 0.84, 0.44, 1); }
.product-card:hover { transform: translateY(-10px); box-shadow: 0 30px 60px rgba(0,0,0,0.05); }

.card-image { height: 240px; position: relative; }
.card-image img { width: 100%; height: 100%; object-fit: cover; }
.price-badge { position: absolute; bottom: 20px; right: 20px; background: #000; color: #fff; padding: 10px 18px; font-weight: 900; border-radius: 4px; font-size: 14px; }

.card-info { padding: 30px; }
.info-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
.category-tag { font-size: 9px; font-weight: 900; text-transform: uppercase; background: #f0f0f0; padding: 4px 10px; border-radius: 3px; color: #666; }
.status-indicator { width: 8px; height: 8px; background: #00ff88; border-radius: 50%; box-shadow: 0 0 10px #00ff88; }

.card-info h3 { font-size: 1.8rem; font-weight: 900; text-transform: uppercase; letter-spacing: -1px; margin: 0 0 10px; }
.card-info p { color: #777; font-size: 0.95rem; line-height: 1.6; height: 3rem; overflow: hidden; }

.card-actions { display: flex; padding: 0 30px 30px; gap: 10px; }
.btn-edit, .btn-delete { flex: 1; padding: 12px; font-size: 10px; font-weight: 900; text-transform: uppercase; border-radius: 4px; cursor: pointer; transition: all 0.2s; }
.btn-edit { background: #fff; border: 1px solid #ddd; }
.btn-edit:hover { background: #000; color: #fff; border-color: #000; }
.btn-delete { background: #fff; border: 1px solid #fee; color: #ff4444; }
.btn-delete:hover { background: #ff4444; color: #fff; border-color: #ff4444; }

.empty-state { text-align: center; padding: 100px; color: #ccc; font-weight: 700; text-transform: uppercase; letter-spacing: 2px; }

/* =============================================
   6. RESPONSIVE DESIGN (MOBILE & TABLET)
   ============================================= */

/* Tabletët dhe ekrane mesatare */
@media (max-width: 1024px) {
  .hero-card {
    flex-direction: column;
    text-align: center;
    padding: 40px;
    gap: 30px;
  }

  .hero-text p {
    max-width: 100%;
  }

  .stats-grid {
    width: 100%;
    justify-content: center;
    gap: 30px;
  }
}

/* Telefonat (Mobile) */
@media (max-width: 768px) {
  .main-header {
    padding: 30px 0;
    margin-bottom: 30px;
  }

  .header-content h1 {
    font-size: 2.2rem; /* Më e vogël për mobile */
  }

  .header-top {
    padding: 0 10px;
  }

  .user-welcome {
    width: 100%;
    justify-content: space-between;
    border-radius: 8px;
  }

  .content {
    padding: 0 15px;
  }

  /* Form Grid në 1 kolonë */
  .input-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .full-width {
    grid-column: span 1;
  }

  /* Hero Section rregullimi */
  .hero-card {
    padding: 30px 20px;
    border-radius: 15px;
  }

  .hero-text h2 {
    font-size: 1.8rem;
  }

  .stat-value {
    font-size: 2.2rem;
  }

  /* Kartat e produkteve në 1 kolonë */
  .product-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .card-image {
    height: 200px;
  }

  .card-info h3 {
    font-size: 1.5rem;
  }

  .form-card {
    padding: 25px;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn-primary, .btn-secondary {
    width: 100%;
    text-align: center;
  }
}

/* Telefonat shumë të vegjël */
@media (max-width: 480px) {
  .header-content h1 {
    font-size: 1.8rem;
    letter-spacing: -1px;
  }
  
  .stats-grid {
    flex-direction: column;
    gap: 20px;
  }
}
</style>