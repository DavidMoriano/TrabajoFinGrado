<script setup>
import { ref, computed, onMounted } from 'vue'
import { servicioCentros } from '@/servicios/api'

const listaCentros = ref([])
const terminoBusqueda = ref('')
const estaCargando = ref(true)

onMounted(async () => {
  try {
    const respuesta = await servicioCentros.obtenerTodos()
    listaCentros.value = respuesta.data
  } catch (error) {
    listaCentros.value = []
  } finally {
    estaCargando.value = false
  }
})

const centrosFiltrados = computed(() => {
  if (!terminoBusqueda.value) return listaCentros.value
  const busqueda = terminoBusqueda.value.toLowerCase()
  return listaCentros.value.filter(centro =>
    centro.nombre.toLowerCase().includes(busqueda) || centro.ciudad.toLowerCase().includes(busqueda)
  )
})
</script>

<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
      <div>
        <h1 class="titulo-pagina">Centros educativos</h1>
        <p class="subtitulo-pagina">Consulta todos los centros disponibles en la plataforma</p>
      </div>
      <div class="barra-busqueda">
        <span class="icono-busqueda">🔍</span>
        <input v-model="terminoBusqueda" type="text" class="form-control campo-busqueda-ancho"
          placeholder="Buscar centro..." />
      </div>
    </div>

    <div v-if="estaCargando" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else-if="centrosFiltrados.length === 0" class="estado-vacio">
      <div class="estado-vacio-icono">🏫</div>
      <p class="estado-vacio-titulo">No hay centros disponibles</p>
      <p class="estado-vacio-texto">Aún no se han registrado centros educativos en la plataforma</p>
    </div>

    <div v-else class="row g-3">
      <div v-for="centro in centrosFiltrados" :key="centro.id_centro" class="col-12 col-md-6 col-lg-4">
        <div class="tarjeta tarjeta-interactiva animacion-aparecer-desde-abajo h-100">
          <div class="d-flex align-items-center gap-2 mb-2">
            <div class="icono-centro">
              🏫</div>
            <div>
              <h3 class="titulo-seccion">{{ centro.nombre }}</h3>
              <span class="badge bg-primary bg-opacity-10 text-primary">{{ centro.ciudad }}</span>
            </div>
          </div>
          <p class="text-secondary small mb-0">{{ centro.direccion }}</p>
        </div>
      </div>
    </div>
  </div>
</template>
