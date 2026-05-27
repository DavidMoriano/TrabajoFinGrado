<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { usarAlmacenAutenticacion } from '@/almacenes/autenticacion'
import { servicioEstadisticas } from '@/servicios/api'

const enrutador = useRouter()
const almacenAutenticacion = usarAlmacenAutenticacion()

const accionesRapidas = [
  { etiqueta: 'Jugar', icono: '🎮', descripcion: 'Minijuegos educativos', nombreRuta: 'SeleccionJuegos' },
  { etiqueta: 'Estadísticas', icono: '📊', descripcion: 'Tu progreso', nombreRuta: 'VistaEstadisticas' },
  { etiqueta: 'Mapa', icono: '🗺️', descripcion: 'Localización de centros', nombreRuta: 'MapaCentrosEducativos' },
  { etiqueta: 'Centros', icono: '🏫', descripcion: 'Ver centros', nombreRuta: 'VerCentros' }
]

const resumen = ref({ totalPartidas: 0, tasaAcierto: 0 })
const rendimientoPorAsignatura = ref([])

onMounted(async () => {
  try {
    const resp = await servicioEstadisticas.obtenerMisEstadisticas()
    const d = resp.data
    const totalPartidas = d.total_partidas ?? d.totalPartidas ?? 0
    const totalAciertos = d.total_aciertos ?? d.totalAciertos ?? 0
    const totalPreguntas = d.total_preguntas ?? d.totalPreguntas ?? 0
    resumen.value = {
      totalPartidas,
      tasaAcierto: totalPreguntas > 0 ? Math.round((totalAciertos / totalPreguntas) * 100) : 0
    }
    rendimientoPorAsignatura.value = (d.por_asignatura ?? d.porAsignatura ?? []).map(a => ({
      nombre: a.nombre_asignatura ?? a.nombreAsignatura ?? '—',
      partidas: a.total_partidas ?? a.totalPartidas ?? 0,
      porcentaje: (a.total_preguntas ?? a.totalPreguntas ?? 0) > 0
        ? Math.round(((a.total_aciertos ?? a.totalAciertos ?? 0) / (a.total_preguntas ?? a.totalPreguntas ?? 0)) * 100)
        : 0
    }))
  } catch {
    // Sin datos todavía
  }
})
</script>

<template>
  <div class="container py-4">
    <div class="mb-4">
      <h1 class="titulo-pagina">¡Hola, {{ almacenAutenticacion.usuarioActual?.nombre || 'Alumno' }}!</h1>
      <p class="subtitulo-pagina">¿Listo para aprender jugando?</p>
    </div>
    <div class="row g-3 mb-4">
      <div v-for="accion in accionesRapidas" :key="accion.nombreRuta" class="col-6 col-lg-3">
        <div
          class="tarjeta tarjeta-interactiva animacion-aparecer-desde-abajo h-100 text-center tarjeta-clicable tarjeta-con-padding"
          @click="enrutador.push({ name: accion.nombreRuta })">
          <div class="icono-accion-rapida">{{ accion.icono }}</div>
          <h3 class="fw-bold titulo-seccion">{{ accion.etiqueta }}</h3>
          <p class="text-secondary small mt-1 mb-0">{{ accion.descripcion }}</p>
        </div>
      </div>
    </div>
    <div class="tarjeta animacion-aparecer-desde-abajo">
      <h3 class="fw-bold mb-3 titulo-seccion">Mi progreso</h3>
      <div v-if="resumen.totalPartidas === 0" class="estado-vacio py-5">
        <div class="estado-vacio-icono">🎮</div>
        <p class="estado-vacio-titulo">Aún no has jugado ninguna partida</p>
        <p class="estado-vacio-texto">Empieza seleccionando una asignatura y un tema en la sección de juegos</p>
        <button class="btn btn-primary mt-3" @click="enrutador.push({ name: 'SeleccionJuegos' })">¡Jugar ahora!</button>
      </div>
      <div v-else>
        <div class="row g-3 mb-3">
          <div class="col-6">
            <div class="tarjeta tarjeta-estadistica h-100">
              <span class="icono-estadistica">🎮</span>
              <div class="valor-estadistica">{{ resumen.totalPartidas }}</div>
              <div class="descripcion-estadistica">Partidas jugadas</div>
            </div>
          </div>
          <div class="col-6">
            <div class="tarjeta tarjeta-estadistica h-100">
              <span class="icono-estadistica">✅</span>
              <div class="valor-estadistica">{{ resumen.tasaAcierto }}%</div>
              <div class="descripcion-estadistica">Tasa de acierto</div>
            </div>
          </div>
        </div>
        <div v-if="rendimientoPorAsignatura.length > 0">
          <p class="fw-semibold small mb-2">Por asignatura:</p>
          <div class="d-flex flex-column gap-2">
            <div v-for="a in rendimientoPorAsignatura" :key="a.nombre">
              <div class="d-flex justify-content-between mb-1">
                <span class="small">{{ a.nombre }}</span>
                <span class="small text-secondary">{{ a.partidas }} partidas · {{ a.porcentaje }}%</span>
              </div>
              <div class="progress" style="height:6px">
                <div class="progress-bar" role="progressbar" :style="{ width: a.porcentaje + '%' }"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
