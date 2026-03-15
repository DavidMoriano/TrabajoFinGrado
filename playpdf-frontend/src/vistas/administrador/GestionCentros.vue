<script setup>
import { ref, computed, onMounted } from 'vue'
import { servicioCentros } from '@/servicios/api'

const listaCentros = ref([])
const terminoBusqueda = ref('')
const mostrarVentanaModal = ref(false)
const estaCargando = ref(true)
const datosFormularioCentro = ref({ nombre: '', ciudad: '', direccion: '', latitud: '', longitud: '' })

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
  return listaCentros.value.filter(centro => centro.nombre.toLowerCase().includes(busqueda) || centro.ciudad.toLowerCase().includes(busqueda))
})

function abrirModalCreacion() {
  datosFormularioCentro.value = { nombre: '', ciudad: '', direccion: '', latitud: '', longitud: '' }
  mostrarVentanaModal.value = true
}

async function agregarCentro() {
  try {
    const respuesta = await servicioCentros.crear({
      ...datosFormularioCentro.value,
      latitud: parseFloat(datosFormularioCentro.value.latitud),
      longitud: parseFloat(datosFormularioCentro.value.longitud)
    })
    listaCentros.value.push(respuesta.data)
    mostrarVentanaModal.value = false
  } catch (error) {
    alert('Error al crear el centro')
  }
}

async function eliminarCentro(identificador) {
  if (confirm('¿Eliminar este centro?')) {
    try {
      await servicioCentros.eliminar(identificador)
      listaCentros.value = listaCentros.value.filter(centro => centro.id_centro !== identificador)
    } catch (error) {
      alert('Error al eliminar el centro')
    }
  }
}
</script>

<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
      <div>
        <h1 class="titulo-pagina">Gestión de centros</h1>
        <p class="subtitulo-pagina">Agregar o eliminar centros educativos</p>
      </div>
      <div class="d-flex gap-2 align-items-center">
        <div class="barra-busqueda"><span class="icono-busqueda">🔍</span><input v-model="terminoBusqueda" type="text"
            class="form-control campo-busqueda" placeholder="Buscar..." /></div>
        <button class="btn btn-primary" @click="abrirModalCreacion">+ Nuevo centro</button>
      </div>
    </div>

    <div class="table-responsive border rounded-3 animacion-aparecer-desde-abajo">
      <table class="table table-hover mb-0">
        <thead>
          <tr>
            <th>Centro</th>
            <th>Ciudad</th>
            <th>Dirección</th>
            <th class="columna-acciones-estrecha">Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="centro in centrosFiltrados" :key="centro.id_centro">
            <td class="fw-semibold">{{ centro.nombre }}</td>
            <td><span class="etiqueta etiqueta-acento">{{ centro.ciudad }}</span></td>
            <td class="texto-secundario-pequeno">{{ centro.direccion }}</td>
            <td class="text-center">
              <button class="boton boton-fantasma boton-pequeno texto-error"
                @click="eliminarCentro(centro.id_centro)">🗑️</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="mostrarVentanaModal" class="superposicion-modal" @click.self="mostrarVentanaModal = false">
      <div class="contenido-modal animacion-escalar-entrada">
        <div class="cabecera-modal">
          <h2 class="titulo-modal">Nuevo centro</h2>
          <button class="boton boton-fantasma boton-icono" @click="mostrarVentanaModal = false">✕</button>
        </div>
        <form @submit.prevent="agregarCentro" class="d-flex flex-column gap-3">
          <div class="grupo-campo"><label class="etiqueta-campo">Nombre</label><input
              v-model="datosFormularioCentro.nombre" required placeholder="Nombre del centro" /></div>
          <div class="grupo-campo"><label class="etiqueta-campo">Ciudad</label><input
              v-model="datosFormularioCentro.ciudad" required placeholder="Ciudad" /></div>
          <div class="grupo-campo"><label class="etiqueta-campo">Dirección</label><input
              v-model="datosFormularioCentro.direccion" required placeholder="Dirección completa" /></div>
          <div class="row g-3">
            <div class="grupo-campo"><label class="etiqueta-campo">Latitud</label><input
                v-model="datosFormularioCentro.latitud" type="number" step="any" required placeholder="40.9629" /></div>
            <div class="grupo-campo"><label class="etiqueta-campo">Longitud</label><input
                v-model="datosFormularioCentro.longitud" type="number" step="any" required placeholder="-5.6631" />
            </div>
          </div>
          <div class="acciones-modal">
            <button type="button" class="boton boton-secundario" @click="mostrarVentanaModal = false">Cancelar</button>
            <button type="submit" class="boton boton-principal">Agregar centro</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
