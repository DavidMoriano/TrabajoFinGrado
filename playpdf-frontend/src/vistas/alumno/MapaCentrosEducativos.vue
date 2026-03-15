<script setup>
import { ref, onMounted } from 'vue'
import { servicioCentros } from '@/servicios/api'

const listaCentrosConUbicacion = ref([])
const estaCargando = ref(true)

const abrirEnGoogleMaps = (latitud, longitud) => {
  window.open(`https://www.google.com/maps?q=${latitud},${longitud}`, '_blank')
}

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
  <div class="pagina-contenedor">
    <div class="cabecera">
      <h1 class="titulo-pagina">Localización de centros</h1>
      <p class="subtitulo-pagina">Visualiza la ubicación geográfica de todos los centros educativos</p>
    </div>

    <div v-if="estaCargando" class="estado-carga">
      <p>Cargando centros...</p>
    </div>

    <div v-else class="lista-centros">
      <div v-for="centro in listaCentrosConUbicacion" :key="centro.id_centro"
        class="tarjeta tarjeta-interactiva animacion-aparecer-desde-abajo tarjeta-centro">
        <div class="tarjeta-centro__contenido">
          <div class="tarjeta-centro__icono">📍</div>
          <div class="tarjeta-centro__info">
            <h4 class="tarjeta-centro__nombre">{{ centro.nombre }}</h4>
            <p class="tarjeta-centro__direccion">{{ centro.direccion }}</p>
            <span class="tarjeta-centro__coordenadas">
              {{ centro.latitud.toFixed(4) }}, {{ centro.longitud.toFixed(4) }}
            </span>
          </div>
        </div>
        <button class="tarjeta-centro__boton" @click="abrirEnGoogleMaps(centro.latitud, centro.longitud)">
          🗺️ Ver en Google Maps
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.pagina-contenedor {
  padding: var(--espacio-grande, 1.5rem);
}

.cabecera {
  margin-bottom: 1.5rem;
}

.estado-carga {
  text-align: center;
  padding: 2rem;
  color: var(--color-texto-secundario, #6c757d);
}

.lista-centros {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1rem;
}

.tarjeta-centro {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 1rem;
  height: 100%;
}

.tarjeta-centro__contenido {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.tarjeta-centro__icono {
  width: 40px;
  height: 40px;
  border-radius: var(--redondeo-medio, 8px);
  background: var(--color-informacion-claro, #e8f4fd);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  flex-shrink: 0;
}

.tarjeta-centro__nombre {
  font-size: 0.95rem;
  font-weight: 700;
  margin: 0;
}

.tarjeta-centro__direccion {
  font-size: 0.85rem;
  color: var(--color-texto-secundario, #6c757d);
  margin: 0;
}

.tarjeta-centro__coordenadas {
  font-size: 0.72rem;
  color: var(--color-texto-terciario, #adb5bd);
  font-family: var(--fuente-codigo, monospace);
}

.tarjeta-centro__boton {
  width: 100%;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: var(--redondeo-medio, 8px);
  background: var(--color-primario, #0d6efd);
  color: white;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s ease;
}

.tarjeta-centro__boton:hover {
  opacity: 0.85;
}
</style>