<script setup>
import { ref, computed, onMounted } from 'vue'
import { servicioAsignaturas } from '@/servicios/api'

const listaAsignaturas = ref([])
const terminoBusqueda = ref('')
const mostrarVentanaModal = ref(false)
const identificadorEdicion = ref(null)
const estaCargando = ref(true)
const datosFormularioAsignatura = ref({ nombre: '', descripcion: '' })

onMounted(async () => {
  try {
    const respuesta = await servicioAsignaturas.obtenerTodas()
    listaAsignaturas.value = respuesta.data
  } catch (error) {
    listaAsignaturas.value = []
  } finally {
    estaCargando.value = false
  }
})

const asignaturasFiltradas = computed(() => {
  if (!terminoBusqueda.value) return listaAsignaturas.value
  const busqueda = terminoBusqueda.value.toLowerCase()
  return listaAsignaturas.value.filter(asignatura => asignatura.nombre.toLowerCase().includes(busqueda))
})

function abrirModalCreacion() {
  identificadorEdicion.value = null
  datosFormularioAsignatura.value = { nombre: '', descripcion: '' }
  mostrarVentanaModal.value = true
}

function abrirModalEdicion(asignatura) {
  identificadorEdicion.value = asignatura.id_asignatura
  datosFormularioAsignatura.value = { nombre: asignatura.nombre, descripcion: asignatura.descripcion }
  mostrarVentanaModal.value = true
}

async function guardarAsignatura() {
  try {
    if (identificadorEdicion.value) {
      const respuesta = await servicioAsignaturas.actualizar(identificadorEdicion.value, datosFormularioAsignatura.value)
      const indice = listaAsignaturas.value.findIndex(asignatura => asignatura.id_asignatura === identificadorEdicion.value)
      if (indice >= 0) listaAsignaturas.value[indice] = respuesta.data
    } else {
      const respuesta = await servicioAsignaturas.crear(datosFormularioAsignatura.value)
      listaAsignaturas.value.push(respuesta.data)
    }
    mostrarVentanaModal.value = false
  } catch (error) {
    alert('Error al guardar la asignatura')
  }
}

async function eliminarAsignatura(identificador) {
  if (confirm('¿Eliminar esta asignatura?')) {
    try {
      await servicioAsignaturas.eliminar(identificador)
      listaAsignaturas.value = listaAsignaturas.value.filter(asignatura => asignatura.id_asignatura !== identificador)
    } catch (error) {
      alert('Error al eliminar la asignatura')
    }
  }
}
</script>

<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
      <div>
        <h1 class="titulo-pagina">Gestión de asignaturas</h1>
        <p class="subtitulo-pagina">Crear, editar o eliminar cualquier asignatura</p>
      </div>
      <div class="d-flex gap-2 align-items-center">
        <div class="barra-busqueda"><span class="icono-busqueda">🔍</span><input v-model="terminoBusqueda" type="text"
            class="form-control campo-busqueda" placeholder="Buscar..." /></div>
      </div>
    </div>
    <div class="table-responsive border rounded-3 animacion-aparecer-desde-abajo">
      <table class="table table-hover mb-0">
        <thead>
          <tr>
            <th>Asignatura</th>
            <th>Descripción</th>
            <th>Centro</th>
            <th>Profesor</th>
            <th class="columna-acciones">Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="asignatura in asignaturasFiltradas" :key="asignatura.id_asignatura">
            <td class="fw-semibold">{{ asignatura.nombre }}</td>
            <td class="texto-descripcion-truncada">
              {{ asignatura.descripcion }}</td>
            <td><span class="etiqueta etiqueta-acento">{{ asignatura.nombre_centro }}</span></td>
            <td class="texto-pequeno">{{ asignatura.nombre_profesor }}</td>
            <td class="text-center">
              <button class="boton boton-fantasma boton-pequeno" @click="abrirModalEdicion(asignatura)">✏️</button>
              <button class="boton boton-fantasma boton-pequeno texto-error"
                @click="eliminarAsignatura(asignatura.id_asignatura)">🗑️</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-if="mostrarVentanaModal" class="superposicion-modal" @click.self="mostrarVentanaModal = false">
      <div class="contenido-modal animacion-escalar-entrada">
        <div class="cabecera-modal">
          <h2 class="titulo-modal">{{ identificadorEdicion ? 'Editar' : 'Nueva' }} asignatura</h2><button
            class="boton boton-fantasma boton-icono" @click="mostrarVentanaModal = false">✕</button>
        </div>
        <form @submit.prevent="guardarAsignatura" class="d-flex flex-column gap-3">
          <div class="grupo-campo"><label class="etiqueta-campo">Nombre</label><input
              v-model="datosFormularioAsignatura.nombre" required placeholder="Nombre de la asignatura" /></div>
          <div class="grupo-campo"><label class="etiqueta-campo">Descripción</label><textarea
              v-model="datosFormularioAsignatura.descripcion" rows="3"
              placeholder="Descripción de la asignatura"></textarea></div>
          <div class="acciones-modal">
            <button type="button" class="boton boton-secundario" @click="mostrarVentanaModal = false">Cancelar</button>
            <button type="submit" class="boton boton-principal">{{ identificadorEdicion ? 'Guardar cambios' : 'Crear'
            }}</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
