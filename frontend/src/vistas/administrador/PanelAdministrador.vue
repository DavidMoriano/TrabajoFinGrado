<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { servicioCentros, servicioAsignaturas, servicioUsuarios } from "@/servicios/api";

const enrutador = useRouter();

const tarjetasEstadisticas = ref([
  { descripcion: "Centros", valor: "—", icono: "🏫", nombreRuta: "GestionCentros" },
  { descripcion: "Asignaturas", valor: "—", icono: "📚", nombreRuta: "GestionAsignaturas" },
  { descripcion: "Profesores", valor: "—", icono: "👨‍🏫", nombreRuta: "ListaProfesores" },
  { descripcion: "Alumnos", valor: "—", icono: "🎓", nombreRuta: "ListaAlumnos" },
]);

const actividadReciente = ref([]);

onMounted(async () => {
  try {
    const [resCentros, resAsig, resProfesores, resAlumnos] = await Promise.all([
      servicioCentros.obtenerTodos(),
      servicioAsignaturas.obtenerTodas(),
      servicioUsuarios.obtenerProfesores(),
      servicioUsuarios.obtenerAlumnos(),
    ]);
    tarjetasEstadisticas.value[0].valor = resCentros.data.length;
    tarjetasEstadisticas.value[1].valor = resAsig.data.length;
    tarjetasEstadisticas.value[2].valor = resProfesores.data.length;
    tarjetasEstadisticas.value[3].valor = resAlumnos.data.length;
  } catch {
    tarjetasEstadisticas.value.forEach(t => { if (t.valor === "—") t.valor = "?"; });
  }
});
</script>

<template>
  <div class="container py-4">
    <div class="mb-4">
      <h1 class="titulo-pagina">Panel de administración</h1>
      <p class="subtitulo-pagina">Resumen general de la plataforma PlayPDF</p>
    </div>

    <div class="row g-3 mb-4">
      <div v-for="tarjeta in tarjetasEstadisticas" :key="tarjeta.descripcion" class="col-6 col-lg-3">
        <div
          class="tarjeta tarjeta-interactiva tarjeta-estadistica animacion-aparecer-desde-abajo h-100 tarjeta-clicable"
          @click="enrutador.push({ name: tarjeta.nombreRuta })">
          <div class="d-flex align-items-center justify-content-between mb-2">
            <span class="icono-tarjeta-estadistica">{{ tarjeta.icono }}</span>
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
          <h3 class="titulo-seccion mb-3">Acciones rápidas</h3>
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
          <h3 class="titulo-seccion mb-3">Actividad reciente</h3>
          <div v-if="actividadReciente.length === 0" class="text-center py-4 text-muted">
            <div class="icono-vacio mb-2">📋</div>
            Sin actividad reciente
          </div>
          <div v-else class="d-flex flex-column gap-3">
            <div v-for="(actividad, indice) in actividadReciente" :key="indice" class="d-flex align-items-start gap-2">
              <div class="indicador-actividad" :style="{ background: actividad.colorIndicador }"></div>
              <div>
                <p class="mb-0 small fw-medium">{{ actividad.texto }}</p>
                <span class="texto-tiempo">{{ actividad.tiempo }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.tarjeta-clicable {
  cursor: pointer;
}

.icono-tarjeta-estadistica {
  font-size: 1.6rem;
}

.titulo-seccion {
  font-size: 1rem;
  font-weight: 700;
}

.icono-vacio {
  font-size: 1.5rem;
}

.indicador-actividad {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-top: 6px;
  flex-shrink: 0;
}

.texto-tiempo {
  font-size: 0.78rem;
  color: var(--color-texto-terciario);
}
</style>
