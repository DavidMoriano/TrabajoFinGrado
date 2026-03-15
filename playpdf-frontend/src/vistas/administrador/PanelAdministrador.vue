<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const enrutador = useRouter()

const tarjetasEstadisticas = ref([
  { descripcion: 'Centros', valor: '0', icono: '🏫', nombreRuta: 'GestionCentros' },
  { descripcion: 'Asignaturas', valor: '0', icono: '📚', nombreRuta: 'GestionAsignaturas' },
  { descripcion: 'Profesores', valor: '0', icono: '👨‍🏫', nombreRuta: 'ListaProfesores' },
  { descripcion: 'Alumnos', valor: '0', icono: '🎓', nombreRuta: 'ListaAlumnos' }
])

const actividadReciente = ref([])
</script>

<template>
  <div class="container py-4">
    <div class="mb-4">
      <h1 class="titulo-pagina">Panel de administración</h1>
      <p class="subtitulo-pagina">Resumen general de la plataforma PlayPDF</p>
    </div>

    <div class="row g-3 mb-4">
      <div v-for="tarjeta in tarjetasEstadisticas" :key="tarjeta.descripcion" class="col-6 col-lg-3">
        <div class="tarjeta tarjeta-interactiva tarjeta-estadistica animacion-aparecer-desde-abajo h-100"
          @click="enrutador.push({ name: tarjeta.nombreRuta })" style="cursor:pointer;">
          <div class="d-flex align-items-center justify-content-between mb-2">
            <span style="font-size:1.6rem;">{{ tarjeta.icono }}</span>
            <span class="badge bg-primary bg-opacity-10 text-primary">Ver</span>
          </div>
          <div class="valor-estadistica">{{ tarjeta.valor }}</div>
          <div class="descripcion-estadistica">{{ tarjeta.descripcion }}</div>
        </div>
      </div>
    </div>

    <div class="row g-3">
      <div class="col-12 col-md-6">
        <div class="tarjeta animacion-aparecer-desde-abajo h-100">
          <h3 style="font-size:1rem;font-weight:700;" class="mb-3">Acciones rápidas</h3>
          <div class="d-grid gap-2">
            <button class="btn btn-outline-secondary text-start" @click="enrutador.push({ name: 'GestionCentros' })">🏫
              Gestionar centros</button>
            <button class="btn btn-outline-secondary text-start"
              @click="enrutador.push({ name: 'GestionAsignaturas' })">📚 Gestionar asignaturas</button>
            <button class="btn btn-outline-secondary text-start"
              @click="enrutador.push({ name: 'ListaProfesores' })">👨‍🏫 Ver profesores</button>
            <button class="btn btn-outline-secondary text-start" @click="enrutador.push({ name: 'ListaAlumnos' })">🎓
              Ver alumnos</button>
          </div>
        </div>
      </div>
      <div class="col-12 col-md-6">
        <div class="tarjeta animacion-aparecer-desde-abajo h-100">
          <h3 style="font-size:1rem;font-weight:700;" class="mb-3">Actividad reciente</h3>
          <div v-if="actividadReciente.length === 0" class="text-center py-4 text-muted">
            <div style="font-size:1.5rem;" class="mb-2">📋</div>
            Sin actividad reciente
          </div>
          <div v-else class="d-flex flex-column gap-3">
            <div v-for="(actividad, indice) in actividadReciente" :key="indice" class="d-flex align-items-start gap-2">
              <div
                :style="{ width: '8px', height: '8px', borderRadius: '50%', marginTop: '6px', flexShrink: 0, background: actividad.colorIndicador }">
              </div>
              <div>
                <p class="mb-0 small fw-medium">{{ actividad.texto }}</p>
                <span class="text-muted" style="font-size:0.78rem;">{{ actividad.tiempo }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
