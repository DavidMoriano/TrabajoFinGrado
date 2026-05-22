<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { usarAlmacenAutenticacion } from '@/almacenes/autenticacion'
import { servicioAsignaturas } from '@/servicios/api'

const enrutador = useRouter()
const almacenAutenticacion = usarAlmacenAutenticacion()

const misAsignaturas = ref([])

const totalTemas = computed(() =>
  misAsignaturas.value.reduce((suma, a) => suma + (a.cantidad_temas || 0), 0)
)

onMounted(async () => {
  try {
    const idProfesor = almacenAutenticacion.usuarioActual?.id_usuario
    const respuesta = await servicioAsignaturas.obtenerPorProfesor(idProfesor)
    misAsignaturas.value = respuesta.data
  } catch {
    misAsignaturas.value = []
  }
})
</script>

<template>
  <div class="container py-4">
    <div class="mb-4">
      <h1 class="titulo-pagina">Bienvenido, {{ almacenAutenticacion.usuarioActual?.nombre || 'Profesor' }}</h1>
      <p class="subtitulo-pagina">Gestiona tus asignaturas y sube temario para tus alumnos</p>
    </div>
    <div class="row g-3 mb-4">
      <div class="col-12 col-md-4">
        <div class="tarjeta tarjeta-estadistica animacion-aparecer-desde-abajo h-100">
          <span class="icono-estadistica">📘</span>
          <div class="valor-estadistica">{{ misAsignaturas.length }}</div>
          <div class="descripcion-estadistica">Mis asignaturas</div>
        </div>
      </div>
      <div class="col-12 col-md-4">
        <div class="tarjeta tarjeta-estadistica animacion-aparecer-desde-abajo h-100">
          <span class="icono-estadistica">📄</span>
          <div class="valor-estadistica">{{ totalTemas }}</div>
          <div class="descripcion-estadistica">Temas subidos</div>
        </div>
      </div>
      <div class="col-12 col-md-4">
        <div class="tarjeta tarjeta-estadistica animacion-aparecer-desde-abajo h-100">
          <span class="icono-estadistica">🎓</span>
          <div class="valor-estadistica">0</div>
          <div class="descripcion-estadistica">Alumnos activos</div>
        </div>
      </div>
    </div>
    <h2 class="fw-bold mb-3">Mis asignaturas</h2>
    <div class="row g-3">
      <div v-for="asignatura in misAsignaturas" :key="asignatura.id_asignatura" class="col-12 col-md-6">
        <div class="tarjeta tarjeta-interactiva animacion-aparecer-desde-abajo h-100">
          <div class="d-flex justify-content-between align-items-start">
            <div>
              <h3 class="fw-bold texto-nombre-grande">{{ asignatura.nombre }}</h3>
              <p class="text-secondary small mt-1">{{ asignatura.descripcion }}</p>
            </div>
            <span class="badge bg-success bg-opacity-10 text-success">{{ asignatura.cantidad_temas }} temas</span>
          </div>
          <div class="d-flex gap-2 mt-3">
            <button class="btn btn-primary btn-sm"
              @click="enrutador.push({ name: 'MisCentros' })">
              🏫 Mis centros
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
