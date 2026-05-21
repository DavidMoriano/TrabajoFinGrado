<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { servicioAsignaturas } from '@/servicios/api'
import { usarAlmacenAutenticacion } from '@/almacenes/autenticacion'

const enrutador = useRouter()
const almacenAutenticacion = usarAlmacenAutenticacion()

const listaAsignaturas = ref([])
const estaCargando = ref(true)
const mostrarVentanaModal = ref(false)
const mostrarModalConfirmacion = ref(false)
const identificadorAEliminar = ref(null)
const identificadorEdicion = ref(null)
const datosFormularioAsignatura = ref({ nombre: '', descripcion: '' })

onMounted(async () => {
  try {
    const idProfesor = almacenAutenticacion.usuarioActual?.id_usuario
    const respuesta = await servicioAsignaturas.obtenerPorProfesor(idProfesor)
    listaAsignaturas.value = respuesta.data
  } catch (error) {
    listaAsignaturas.value = []
  } finally {
    estaCargando.value = false
  }
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
      const indice = listaAsignaturas.value.findIndex(a => a.id_asignatura === identificadorEdicion.value)
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

function pedirConfirmacionEliminar(identificador) {
  identificadorAEliminar.value = identificador
  mostrarModalConfirmacion.value = true
}

async function confirmarEliminar() {
  try {
    await servicioAsignaturas.eliminar(identificadorAEliminar.value)
    listaAsignaturas.value = listaAsignaturas.value.filter(a => a.id_asignatura !== identificadorAEliminar.value)
    mostrarModalConfirmacion.value = false
    identificadorAEliminar.value = null
  } catch (error) {
    alert('Error: ' + (error.response?.data?.message || error.message))
  }
}

function cancelarEliminar() {
  mostrarModalConfirmacion.value = false
  identificadorAEliminar.value = null
}
</script>

<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
      <div>
        <h1 class="titulo-pagina">Mis asignaturas</h1>
        <p class="subtitulo-pagina">Crea, modifica o elimina tus asignaturas</p>
      </div>
      <button class="btn btn-primary" @click="abrirModalCreacion">+ Nueva asignatura</button>
    </div>

    <div v-if="estaCargando" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else-if="listaAsignaturas.length === 0" class="estado-vacio">
      <div class="estado-vacio-icono">📘</div>
      <p class="estado-vacio-titulo">Sin asignaturas</p>
      <p class="estado-vacio-texto">Crea tu primera asignatura para empezar a subir temario</p>
      <button class="btn btn-primary mt-3" @click="abrirModalCreacion">Crear asignatura</button>
    </div>

    <div v-else class="row g-3">
      <div v-for="asignatura in listaAsignaturas" :key="asignatura.id_asignatura" class="col-12 col-md-6">
        <div class="tarjeta tarjeta-interactiva animacion-aparecer-desde-abajo h-100">
          <div class="d-flex justify-content-between align-items-start">
            <h3 class="fw-bold texto-nombre-grande">{{ asignatura.nombre }}</h3>
            <span class="badge bg-success bg-opacity-10 text-success">
              {{ asignatura.cantidad_temas }} temas
            </span>
          </div>
          <p class="text-secondary small my-2">{{ asignatura.descripcion }}</p>
          <div class="d-flex gap-2 mt-3">
            <button class="btn btn-primary btn-sm"
              @click.stop="enrutador.push({ name: 'SubirTemario', params: { identificadorAsignatura: asignatura.id_asignatura } })">
              📄 Temario
            </button>
            <button class="btn btn-outline-secondary btn-sm" @click.stop="abrirModalEdicion(asignatura)">
              ✏️ Editar
            </button>
            <button class="btn btn-outline-danger btn-sm"
              @click.stop="pedirConfirmacionEliminar(asignatura.id_asignatura)">
              🗑️ Eliminar
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal crear / editar -->
    <div v-if="mostrarVentanaModal" class="superposicion-modal" @click.self="mostrarVentanaModal = false">
      <div class="contenido-modal animacion-escalar-entrada">
        <div class="cabecera-modal">
          <h2 class="titulo-modal">{{ identificadorEdicion ? 'Editar' : 'Nueva' }} asignatura</h2>
          <button class="boton boton-fantasma boton-icono" @click="mostrarVentanaModal = false">✕</button>
        </div>
        <form @submit.prevent="guardarAsignatura" class="d-flex flex-column gap-3">
          <div class="grupo-campo">
            <label class="etiqueta-campo">Nombre</label>
            <input v-model="datosFormularioAsignatura.nombre" required placeholder="Nombre de la asignatura" />
          </div>
          <div class="grupo-campo">
            <label class="etiqueta-campo">Descripción</label>
            <textarea v-model="datosFormularioAsignatura.descripcion" rows="3"
              placeholder="Describe la asignatura"></textarea>
          </div>
          <div class="acciones-modal">
            <button type="button" class="boton boton-secundario" @click="mostrarVentanaModal = false">
              Cancelar
            </button>
            <button type="submit" class="boton boton-principal">
              {{ identificadorEdicion ? 'Guardar' : 'Crear' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Modal confirmación eliminar -->
    <div v-if="mostrarModalConfirmacion" class="superposicion-modal">
      <div class="contenido-modal animacion-escalar-entrada">
        <div class="cabecera-modal">
          <h2 class="titulo-modal">Eliminar asignatura</h2>
        </div>
        <p class="texto-confirmacion-eliminar">
          ¿Estás seguro de que quieres eliminar esta asignatura? Esta acción no se puede deshacer.
        </p>
        <div class="acciones-modal">
          <button class="boton boton-secundario" @click="cancelarEliminar">Cancelar</button>
          <button class="boton boton-principal boton-eliminar-confirmar" @click="confirmarEliminar">Eliminar</button>
        </div>
      </div>
    </div>
  </div>
</template>
