<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { usarAlmacenAutenticacion } from '@/almacenes/autenticacion'

const enrutador = useRouter()
const almacenAutenticacion = usarAlmacenAutenticacion()

const accionesRapidas = [
  { etiqueta: 'Jugar', icono: '🎮', descripcion: 'Minijuegos educativos', nombreRuta: 'SeleccionJuegos' },
  { etiqueta: 'Estadísticas', icono: '📊', descripcion: 'Tu progreso', nombreRuta: 'VistaEstadisticas' },
  { etiqueta: 'Mapa', icono: '🗺️', descripcion: 'Localización de centros', nombreRuta: 'MapaCentrosEducativos' },
  { etiqueta: 'Centros', icono: '🏫', descripcion: 'Ver centros', nombreRuta: 'VerCentros' }
]

const partidasRecientes = ref([])
</script>

<template>
  <div class="container py-4">
    <div class="mb-4">
      <h1 class="titulo-pagina">¡Hola, {{ almacenAutenticacion.usuarioActual?.nombre || 'Alumno' }}!</h1>
      <p class="subtitulo-pagina">¿Listo para aprender jugando?</p>
    </div>
    <div class="row g-3 mb-4">
      <div v-for="accion in accionesRapidas" :key="accion.nombreRuta" class="col-6 col-lg-3">
        <div class="tarjeta tarjeta-interactiva animacion-aparecer-desde-abajo h-100 text-center"
          @click="enrutador.push({ name: accion.nombreRuta })"
          style="cursor:pointer;padding:var(--espacio-extra-grande);">
          <div style="font-size:2.2rem;margin-bottom:0.5rem;">{{ accion.icono }}</div>
          <h3 class="fw-bold" style="font-size:1rem;">{{ accion.etiqueta }}</h3>
          <p class="text-secondary small mt-1 mb-0">{{ accion.descripcion }}</p>
        </div>
      </div>
    </div>
    <div class="tarjeta animacion-aparecer-desde-abajo">
      <h3 class="fw-bold mb-3" style="font-size:1rem;">Partidas recientes</h3>
      <div v-if="partidasRecientes.length === 0" class="estado-vacio py-5">
        <div class="estado-vacio-icono">🎮</div>
        <p class="estado-vacio-titulo">Aún no has jugado ninguna partida</p>
        <p class="estado-vacio-texto">Empieza seleccionando una asignatura y un tema en la sección de juegos</p>
        <button class="btn btn-primary mt-3" @click="enrutador.push({ name: 'SeleccionJuegos' })">¡Jugar ahora!</button>
      </div>
      <div v-else class="list-group list-group-flush">
        <div v-for="(partida, indice) in partidasRecientes" :key="indice"
          class="list-group-item d-flex align-items-center justify-content-between px-0"
          style="background:transparent;border-color:var(--color-borde-secundario);">
          <div class="d-flex align-items-center gap-2"><span style="font-size:1.1rem;">🎮</span><span
              class="fw-medium small">{{ partida.nombreJuego }}</span></div>
          <div class="d-flex align-items-center gap-2">
            <span class="badge bg-success bg-opacity-10 text-success">{{ partida.puntuacion }}</span>
            <span class="text-muted" style="font-size:0.78rem;">{{ partida.fecha }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
