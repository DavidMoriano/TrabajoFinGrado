<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { servicioCentros, servicioAsignaturas } from '@/servicios/api'
import { usarAlmacenAutenticacion } from '@/almacenes/autenticacion'

const rutaActual = useRoute()
const enrutador = useRouter()
const almacenAutenticacion = usarAlmacenAutenticacion()
const identificadorCentro = Number(rutaActual.params.identificadorCentro)

const centro = ref(null)
const listaAsignaturas = ref([])
const estaCargando = ref(true)
const mostrarVentanaModal = ref(false)
const mostrarModalConfirmacion = ref(false)
const identificadorAEliminar = ref(null)
const identificadorEdicion = ref(null)
const datosFormulario = ref({ nombre: '', descripcion: '' })
const errorFormulario = ref('')

onMounted(async () => {
  try {
    const [resCentro, resAsig] = await Promise.all([
      servicioCentros.obtenerPorId(identificadorCentro),
      servicioAsignaturas.obtenerPorCentro(identificadorCentro),
    ])
    centro.value = resCentro.data
    const idProfesor = almacenAutenticacion.usuarioActual?.id_usuario
    listaAsignaturas.value = resAsig.data.filter(a => a.id_profesor === idProfesor)
  } catch {
    listaAsignaturas.value = []
  } finally {
    estaCargando.value = false
  }
})

function abrirModalCreacion() {
  identificadorEdicion.value = null
  datosFormulario.value = { nombre: '', descripcion: '' }
  errorFormulario.value = ''
  mostrarVentanaModal.value = true
}

function abrirModalEdicion(asignatura) {
  identificadorEdicion.value = asignatura.id_asignatura
  datosFormulario.value = { nombre: asignatura.nombre, descripcion: asignatura.descripcion }
  errorFormulario.value = ''
  mostrarVentanaModal.value = true
}

async function guardarAsignatura() {
  errorFormulario.value = ''
  try {
    if (identificadorEdicion.value) {
      const resp = await servicioAsignaturas.actualizar(identificadorEdicion.value, datosFormulario.value)
      const idx = listaAsignaturas.value.findIndex(a => a.id_asignatura === identificadorEdicion.value)
      if (idx >= 0) listaAsignaturas.value[idx] = resp.data
    } else {
      const resp = await servicioAsignaturas.crear({
        nombre: datosFormulario.value.nombre,
        descripcion: datosFormulario.value.descripcion,
        id_centro: identificadorCentro,
      })
      listaAsignaturas.value.push(resp.data)
    }
    mostrarVentanaModal.value = false
  } catch (error) {
    errorFormulario.value = error.response?.data?.message || 'Error al guardar la asignatura'
  }
}

function pedirConfirmacion(id) {
  identificadorAEliminar.value = id
  mostrarModalConfirmacion.value = true
}

async function confirmarEliminar() {
  try {
    await servicioAsignaturas.eliminar(identificadorAEliminar.value)
    listaAsignaturas.value = listaAsignaturas.value.filter(a => a.id_asignatura !== identificadorAEliminar.value)
    mostrarModalConfirmacion.value = false
  } catch (error) {
    alert(error.response?.data?.message || 'Error al eliminar')
  }
}
</script>

<template>
  <div class="container py-4">
    <!-- Cabecera con volver -->
    <div class="mb-4">
      <button class="btn btn-link p-0 mb-2 text-secondary" @click="enrutador.push({ name: 'MisCentros' })">
        ← Volver a mis centros
      </button>
      <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
        <div>
          <h1 class="titulo-pagina">{{ centro?.nombre || 'Centro' }}</h1>
          <p class="subtitulo-pagina">{{ centro?.ciudad }} · Mis asignaturas en este centro</p>
        </div>
        <button class="btn btn-primary" @click="abrirModalCreacion">+ Nueva asignatura</button>
      </div>
    </div>

    <div v-if="estaCargando" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else-if="listaAsignaturas.length === 0" class="estado-vacio">
      <div class="estado-vacio-icono">📘</div>
      <p class="estado-vacio-titulo">Sin asignaturas en este centro</p>
      <p class="estado-vacio-texto">Crea tu primera asignatura para que los alumnos puedan estudiar</p>
      <button class="btn btn-primary mt-3" @click="abrirModalCreacion">Crear asignatura</button>
    </div>

    <div v-else class="row g-3">
      <div v-for="asignatura in listaAsignaturas" :key="asignatura.id_asignatura" class="col-12 col-md-6">
        <div class="tarjeta tarjeta-interactiva animacion-aparecer-desde-abajo h-100">
          <div class="d-flex justify-content-between align-items-start">
            <h3 class="fw-bold texto-nombre-grande">{{ asignatura.nombre }}</h3>
            <span class="badge bg-success bg-opacity-10 text-success">{{ asignatura.cantidad_temas }} temas</span>
          </div>
          <p class="text-secondary small my-2">{{ asignatura.descripcion }}</p>
          <div class="d-flex gap-2 mt-3">
            <button class="btn btn-primary btn-sm"
              @click="enrutador.push({ name: 'SubirTemario', params: { identificadorAsignatura: asignatura.id_asignatura } })">
              📄 Temario
            </button>
            <button class="btn btn-outline-secondary btn-sm" @click="abrirModalEdicion(asignatura)">
              ✏️ Editar
            </button>
            <button class="btn btn-outline-danger btn-sm" @click="pedirConfirmacion(asignatura.id_asignatura)">
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
            <input v-model="datosFormulario.nombre" required placeholder="Ej: Matemáticas 1ºDAW" />
          </div>
          <div class="grupo-campo">
            <label class="etiqueta-campo">Descripción</label>
            <textarea v-model="datosFormulario.descripcion" rows="3" placeholder="Describe la asignatura"></textarea>
          </div>
          <div v-if="errorFormulario" class="alerta-error">{{ errorFormulario }}</div>
          <div class="acciones-modal">
            <button type="button" class="boton boton-secundario" @click="mostrarVentanaModal = false">Cancelar</button>
            <button type="submit" class="boton boton-principal">{{ identificadorEdicion ? 'Guardar' : 'Crear'
              }}</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Modal confirmar eliminar -->
    <div v-if="mostrarModalConfirmacion" class="superposicion-modal">
      <div class="contenido-modal animacion-escalar-entrada">
        <div class="cabecera-modal">
          <h2 class="titulo-modal">Eliminar asignatura</h2>
        </div>
        <p class="mb-4 text-secondary">¿Seguro que quieres eliminar esta asignatura? Se eliminarán también sus temas y
          preguntas.</p>
        <div class="acciones-modal">
          <button class="boton boton-secundario" @click="mostrarModalConfirmacion = false">Cancelar</button>
          <button class="boton boton-principal" style="background:var(--color-error);border-color:var(--color-error)"
            @click="confirmarEliminar">Eliminar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.alerta-error {
  background: var(--color-error-claro);
  color: var(--color-error);
  border: 1px solid var(--color-error);
  border-radius: var(--redondeo-medio);
  padding: 0.6rem 1rem;
  font-size: 0.88rem;
}
</style>
