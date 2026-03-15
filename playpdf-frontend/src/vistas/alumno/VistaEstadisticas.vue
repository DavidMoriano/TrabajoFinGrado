<script setup>
import { ref, computed } from "vue";

const resumenEstadisticas = ref({ totalPartidas: 0, totalAciertos: 0, totalPreguntas: 0, rachaActual: 0 });
const porcentajePrecision = computed(() => {
  if (resumenEstadisticas.value.totalPreguntas === 0) return 0;
  return Math.round((resumenEstadisticas.value.totalAciertos / resumenEstadisticas.value.totalPreguntas) * 100);
});

const rendimientoPorAsignatura = ref([]);
const historialPartidas = ref([]);

const noHayDatos = computed(() => resumenEstadisticas.value.totalPartidas === 0);
</script>

<template>
  <div class="container py-4">
    <div class="mb-4">
      <h1 class="titulo-pagina">Estadísticas de uso</h1>
      <p class="subtitulo-pagina">Tu progreso y rendimiento en la plataforma</p>
    </div>

    <div class="row g-3 mb-4">
      <div class="col-6 col-lg-3">
        <div class="tarjeta tarjeta-estadistica animacion-aparecer-desde-abajo h-100">
          <span class="icono-estadistica">🎮</span>
          <div class="valor-estadistica">{{ resumenEstadisticas.totalPartidas }}</div>
          <div class="descripcion-estadistica">Partidas jugadas</div>
        </div>
      </div>
      <div class="col-6 col-lg-3">
        <div class="tarjeta tarjeta-estadistica animacion-aparecer-desde-abajo h-100">
          <span class="icono-estadistica">✅</span>
          <div class="valor-estadistica">{{ porcentajePrecision }}%</div>
          <div class="descripcion-estadistica">Tasa de acierto</div>
        </div>
      </div>
      <div class="col-6 col-lg-3">
        <div class="tarjeta tarjeta-estadistica animacion-aparecer-desde-abajo h-100">
          <span class="icono-estadistica">🎯</span>
          <div class="valor-estadistica">{{ resumenEstadisticas.totalAciertos }}</div>
          <div class="descripcion-estadistica">Respuestas correctas</div>
        </div>
      </div>
      <div class="col-6 col-lg-3">
        <div class="tarjeta tarjeta-estadistica animacion-aparecer-desde-abajo h-100">
          <span class="icono-estadistica">🔥</span>
          <div class="valor-estadistica">{{ resumenEstadisticas.rachaActual }}</div>
          <div class="descripcion-estadistica">Racha actual</div>
        </div>
      </div>
    </div>

    <div v-if="noHayDatos" class="tarjeta animacion-aparecer-desde-abajo">
      <div class="estado-vacio py-5">
        <div class="estado-vacio-icono">📊</div>
        <p class="estado-vacio-titulo">Aún no hay estadísticas</p>
        <p class="estado-vacio-texto">Juega algunas partidas para ver aquí tu rendimiento por asignatura y tu historial
        </p>
      </div>
    </div>

    <div v-else class="row g-3">
      <div class="col-12 col-md-6">
        <div class="tarjeta animacion-aparecer-desde-abajo h-100">
          <h3 class="fw-bold mb-3 titulo-seccion">Rendimiento por asignatura</h3>
          <div class="d-flex flex-column gap-3">
            <div v-for="asignatura in rendimientoPorAsignatura" :key="asignatura.nombreAsignatura">
              <div class="d-flex justify-content-between mb-1">
                <span class="fw-semibold small">{{ asignatura.nombreAsignatura }}</span>
                <span class="text-secondary small">{{ asignatura.porcentaje }}%</span>
              </div>
              <div class="progress barra-progreso-asignatura">
                <div class="progress-bar barra-progreso-relleno" role="progressbar"
                  :style="{ width: asignatura.porcentaje + '%' }"></div>
              </div>
              <span class="texto-detalle-asignatura">{{ asignatura.cantidadPartidas }} partidas · {{ asignatura.aciertos
                }}/{{ asignatura.totalPreguntas }} aciertos</span>
            </div>
          </div>
        </div>
      </div>
      <div class="col-12 col-md-6">
        <div class="tarjeta animacion-aparecer-desde-abajo h-100">
          <h3 class="fw-bold mb-3 titulo-seccion">Historial reciente</h3>
          <div class="list-group list-group-flush">
            <div v-for="(partida, indice) in historialPartidas" :key="indice"
              class="list-group-item d-flex align-items-center justify-content-between px-0 elemento-historial">
              <div class="contenido-partida">
                <p class="mb-0 small fw-medium">{{ partida.nombreAsignatura }} — {{ partida.nombreTema }}</p>
                <span class="texto-detalle-partida">{{ partida.tipoJuego }} · {{ partida.fecha }}</span>
              </div>
              <span class="badge bg-success bg-opacity-10 text-success ms-2">{{ partida.puntuacion }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.icono-estadistica {
  font-size: 1.4rem;
}

.titulo-seccion {
  font-size: 1rem;
}

.barra-progreso-asignatura {
  height: 8px;
}

.barra-progreso-relleno {
  background: var(--gradiente-acento);
}

.texto-detalle-asignatura {
  font-size: 0.75rem;
  color: var(--color-texto-terciario);
}

.elemento-historial {
  background: transparent;
  border-color: var(--color-borde-secundario);
}

.contenido-partida {
  min-width: 0;
  flex: 1;
}

.texto-detalle-partida {
  font-size: 0.75rem;
  color: var(--color-texto-terciario);
}
</style>
