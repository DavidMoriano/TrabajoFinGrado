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
const identificadorEdicion = ref(null)
const datosFormularioAsignatura = ref({ nombre: '', descripcion: '' })

onMounted(async () => {
  try {
    const respuesta = await servicioAsignaturas.obtenerPorProfesor(almacenAutenticacion.usuarioActual.id_usuario)
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
        <h1 class="titulo-pagina">Mis asignaturas</h1>
        <p class="subtitulo-pagina">Crea, modifica o elimina tus asignaturas</p>
      </div>
      <button class="btn btn-primary" @click="abrirModalCreacion">+ Nueva asignatura</button>
    </div>
    <div v-if="listaAsignaturas.length === 0" class="estado-vacio">
      <div class="estado-vacio-icono">📘</div>
      <p class="estado-vacio-titulo">Sin asignaturas</p>
      <p class="estado-vacio-texto">Crea tu primera asignatura para empezar a subir temario</p>
      <button class="btn btn-primary mt-3" @click="abrirModalCreacion">Crear asignatura</button>
    </div>
    <div v-else class="row g-3">
      <div v-for="asignatura in listaAsignaturas" :key="asignatura.id_asignatura" class="col-12 col-md-6">
        <div class="tarjeta tarjeta-interactiva animacion-aparecer-desde-abajo h-100">
          <div class="d-flex justify-content-between align-items-start">
            <h3 class="fw-bold" style="font-size:1.05rem;">{{ asignatura.nombre }}</h3>
            <span class="badge bg-success bg-opacity-10 text-success">{{ asignatura.cantidadTemas }} temas</span>
          </div>
          <p class="text-secondary small my-2">{{ asignatura.descripcion }}</p>
          <div class="d-flex gap-2 mt-3">
            <button class="btn btn-primary btn-sm"
              @click="enrutador.push({ name: 'SubirTemario', params: { identificadorAsignatura: asignatura.id_asignatura } })">📄
              Temario</button>
            <button class="btn btn-outline-secondary btn-sm" @click="abrirModalEdicion(asignatura)">✏️ Editar</button>
            <button class="btn btn-outline-danger btn-sm" @click="eliminarAsignatura(asignatura.id_asignatura)">🗑️
              Eliminar</button>
          </div>
        </div>
      </div>
    </div>
    <div v-if="mostrarVentanaModal" class="superposicion-modal" @click.self="mostrarVentanaModal = false">
      <div class="contenido-modal animacion-escalar-entrada">
        <div class="cabecera-modal">
          <h2 class="titulo-modal">{{ identificadorEdicion ? 'Editar' : 'Nueva' }} asignatura</h2><button
            class="boton boton-fantasma boton-icono" @click="mostrarVentanaModal = false">✕</button>
        </div>
        <form @submit.prevent="guardarAsignatura" style="display:flex;flex-direction:column;gap:var(--espacio-medio);">
          <div class="grupo-campo"><label class="etiqueta-campo">Nombre</label><input
              v-model="datosFormularioAsignatura.nombre" required placeholder="Nombre de la asignatura" /></div>
          <div class="grupo-campo"><label class="etiqueta-campo">Descripción</label><textarea
              v-model="datosFormularioAsignatura.descripcion" rows="3" placeholder="Describe la asignatura"></textarea>
          </div>
          <div class="acciones-modal"><button type="button" class="boton boton-secundario"
              @click="mostrarVentanaModal = false">Cancelar</button><button type="submit"
              class="boton boton-principal">{{ identificadorEdicion ? 'Guardar' : 'Crear' }}</button></div>
        </form>
      </div>
    </div>
  </div>
</template>
