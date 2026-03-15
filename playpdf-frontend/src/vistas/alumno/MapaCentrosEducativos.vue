<script setup>
import { ref, onMounted } from 'vue'
import { servicioCentros } from '@/servicios/api'

const listaCentrosConUbicacion = ref([])
const referenciaContenedorMapa = ref(null)
const estaCargando = ref(true)

onMounted(async () => {
  try {
    const respuesta = await servicioCentros.obtenerTodos()
    listaCentrosConUbicacion.value = respuesta.data
  } catch (error) {
    listaCentrosConUbicacion.value = []
  } finally {
    estaCargando.value = false
  }
})
</script>

<template>
  <div class="container py-4">
    <div class="mb-4">
      <h1 class="titulo-pagina">Localización de centros</h1>
      <p class="subtitulo-pagina">Visualiza la ubicación geográfica de todos los centros educativos</p>
    </div>
    <div class="row g-3 mb-4">
      <div v-for="centro in listaCentrosConUbicacion" :key="centro.id_centro" class="col-12 col-md-4">
        <div class="tarjeta tarjeta-interactiva animacion-aparecer-desde-abajo h-100" style="cursor:pointer;">
          <div class="d-flex align-items-center gap-2">
            <div
              style="width:40px;height:40px;border-radius:var(--redondeo-medio);background:var(--color-informacion-claro);display:flex;align-items:center;justify-content:center;font-size:1.2rem;flex-shrink:0;">
              📍</div>
            <div>
              <h4 class="fw-bold" style="font-size:0.95rem;">{{ centro.nombre }}</h4>
              <p class="text-secondary small mb-0">{{ centro.direccion }}</p>
              <span class="text-muted" style="font-size:0.72rem;font-family:var(--fuente-codigo);">{{
                centro.latitud.toFixed(4) }}, {{ centro.longitud.toFixed(4) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="tarjeta animacion-aparecer-desde-abajo" style="padding:0;overflow:hidden;">
      <div ref="referenciaContenedorMapa" class="contenedor-mapa">
        <div class="marcador-posicion-mapa">
          <div style="font-size:3rem;" class="mb-3">🗺️</div>
          <h3 class="fw-bold" style="font-size:1.1rem;">Mapa de Google Maps</h3>
          <p class="text-secondary small mt-1" style="max-width:400px;">
            Aquí se cargará el mapa interactivo con Google Maps API.
            Configura tu API Key en el archivo <code
              style="font-family:var(--fuente-codigo);background:var(--color-fondo-terciario);padding:0.1rem 0.4rem;border-radius:4px;font-size:0.82rem;">.env</code>
            para activarlo.
          </p>
          <div class="d-flex gap-2 flex-wrap justify-content-center mt-3">
            <span v-for="centro in listaCentrosConUbicacion" :key="centro.id_centro"
              class="badge bg-primary bg-opacity-10 text-primary">📍 {{ centro.nombre }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.contenedor-mapa {
  width: 100%;
  height: 450px;
  background: var(--color-fondo-terciario);
}

.marcador-posicion-mapa {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: var(--espacio-extra-grande);
}
</style>
