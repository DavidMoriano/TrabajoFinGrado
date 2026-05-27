<script setup>
import { ref, computed, onMounted } from "vue";
import { servicioEstadisticas } from "@/servicios/api";

const resumen = ref({ totalPartidas: 0, totalAciertos: 0, totalPreguntas: 0, rachaActual: 0 });
const porAsignatura = ref([]);
const estaCargando = ref(true);
const errorCarga = ref(null);

const tasaAcierto = computed(() => {
  if (resumen.value.totalPreguntas === 0) return 0;
  return Math.round((resumen.value.totalAciertos / resumen.value.totalPreguntas) * 100);
});

const noHayDatos = computed(() => resumen.value.totalPartidas === 0);

onMounted(async () => {
  try {
    const resp = await servicioEstadisticas.obtenerMisEstadisticas();
    const d = resp.data;
    resumen.value = {
      totalPartidas:  d.total_partidas  ?? d.totalPartidas  ?? 0,
      totalAciertos:  d.total_aciertos  ?? d.totalAciertos  ?? 0,
      totalPreguntas: d.total_preguntas ?? d.totalPreguntas ?? 0,
      rachaActual:    d.racha_actual    ?? d.rachaActual    ?? 0,
    };
    porAsignatura.value = (d.por_asignatura ?? d.porAsignatura ?? []).map(a => ({
      nombre:         a.nombre_asignatura ?? a.nombreAsignatura ?? "—",
      partidas:       a.total_partidas    ?? a.totalPartidas    ?? 0,
      aciertos:       a.total_aciertos    ?? a.totalAciertos    ?? 0,
      totalPreguntas: a.total_preguntas   ?? a.totalPreguntas   ?? 0,
      porcentaje: (a.total_preguntas ?? a.totalPreguntas ?? 0) > 0
        ? Math.round(((a.total_aciertos ?? a.totalAciertos ?? 0) / (a.total_preguntas ?? a.totalPreguntas ?? 0)) * 100)
        : 0,
    }));
  } catch {
    errorCarga.value = "No se pudieron cargar las estadísticas.";
  } finally {
    estaCargando.value = false;
  }
});
</script>

<template>
  <div class="container py-4">
    <div class="mb-4">
      <h1 class="titulo-pagina">Mis estadísticas</h1>
      <p class="subtitulo-pagina">Tu progreso y rendimiento en la plataforma</p>
    </div>

    <div v-if="estaCargando" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else-if="errorCarga" class="tarjeta text-center py-5 animacion-aparecer-desde-abajo">
      <div style="font-size:2rem">⚠️</div>
      <p class="fw-semibold mt-2">{{ errorCarga }}</p>
    </div>

    <template v-else>

      <div class="row g-3 mb-4">
        <div class="col-6 col-lg-3">
          <div class="tarjeta tarjeta-estadistica animacion-aparecer-desde-abajo h-100">
            <span class="icono-estadistica">🎮</span>
            <div class="valor-estadistica">{{ resumen.totalPartidas }}</div>
            <div class="descripcion-estadistica">Partidas jugadas</div>
          </div>
        </div>
        <div class="col-6 col-lg-3">
          <div class="tarjeta tarjeta-estadistica animacion-aparecer-desde-abajo h-100">
            <span class="icono-estadistica">✅</span>
            <div class="valor-estadistica">{{ tasaAcierto }}%</div>
            <div class="descripcion-estadistica">Tasa de acierto</div>
          </div>
        </div>
        <div class="col-6 col-lg-3">
          <div class="tarjeta tarjeta-estadistica animacion-aparecer-desde-abajo h-100">
            <span class="icono-estadistica">🎯</span>
            <div class="valor-estadistica">{{ resumen.totalAciertos }}/{{ resumen.totalPreguntas }}</div>
            <div class="descripcion-estadistica">Respuestas correctas</div>
          </div>
        </div>
        <div class="col-6 col-lg-3">
          <div class="tarjeta tarjeta-estadistica animacion-aparecer-desde-abajo h-100">
            <span class="icono-estadistica">🔥</span>
            <div class="valor-estadistica">{{ resumen.rachaActual }}</div>
            <div class="descripcion-estadistica">Racha de partidas perfectas</div>
          </div>
        </div>
      </div>

      <div v-if="noHayDatos" class="tarjeta animacion-aparecer-desde-abajo">
        <div class="estado-vacio py-5">
          <div class="estado-vacio-icono">📊</div>
          <p class="estado-vacio-titulo">Aún no hay estadísticas</p>
          <p class="estado-vacio-texto">Completa alguna partida para ver aquí tu rendimiento</p>
        </div>
      </div>

      <div v-else class="tarjeta animacion-aparecer-desde-abajo">
        <h3 class="fw-bold mb-4 titulo-seccion">Rendimiento por asignatura</h3>
        <div class="d-flex flex-column gap-4">
          <div v-for="a in porAsignatura" :key="a.nombre">
            <div class="d-flex justify-content-between align-items-center mb-1">
              <span class="fw-semibold small">{{ a.nombre }}</span>
              <div class="d-flex gap-2 align-items-center">
                <span class="badge-aciertos">{{ a.aciertos }}/{{ a.totalPreguntas }} aciertos</span>
                <span class="badge-porcentaje" :class="a.porcentaje >= 70 ? 'badge-verde' : a.porcentaje >= 40 ? 'badge-amarillo' : 'badge-rojo'">
                  {{ a.porcentaje }}%
                </span>
              </div>
            </div>
            <div class="barra-fondo">
              <div class="barra-relleno" :style="{ width: a.porcentaje + '%' }"
                :class="a.porcentaje >= 70 ? 'barra-verde' : a.porcentaje >= 40 ? 'barra-amarillo' : 'barra-rojo'">
              </div>
            </div>
            <span class="texto-detalle">{{ a.partidas }} {{ a.partidas === 1 ? 'partida' : 'partidas' }}</span>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.icono-estadistica { font-size: 1.4rem; }
.titulo-seccion { font-size: 1rem; }

.barra-fondo {
  height: 10px;
  background: var(--color-fondo-terciario);
  border-radius: 5px;
  overflow: hidden;
  margin-bottom: 4px;
}

.barra-relleno {
  height: 100%;
  border-radius: 5px;
  transition: width 0.6s ease;
}

.barra-verde    { background: var(--color-exito); }
.barra-amarillo { background: #f59e0b; }
.barra-rojo     { background: var(--color-error); }

.badge-aciertos {
  font-size: 0.72rem;
  color: var(--color-texto-terciario);
}

.badge-porcentaje {
  font-size: 0.72rem;
  font-weight: 700;
  padding: 2px 7px;
  border-radius: 20px;
}

.badge-verde    { background: var(--color-exito-claro);  color: var(--color-exito); }
.badge-amarillo { background: #fef3c7; color: #92400e; }
.badge-rojo     { background: var(--color-error-claro);  color: var(--color-error); }

.texto-detalle {
  font-size: 0.73rem;
  color: var(--color-texto-terciario);
}
</style>
