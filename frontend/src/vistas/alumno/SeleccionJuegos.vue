<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { servicioAsignaturas } from "@/servicios/api";
import { servicioTemas } from "@/servicios/api";

const enrutador = useRouter();

const listaAsignaturas = ref([]);
const temasDeAsignaturaSeleccionada = ref([]);
const estaCargandoAsignaturas = ref(true);
const estaCargandoTemas = ref(false);
const errorCarga = ref(null);

const asignaturaSeleccionada = ref(null);
const temaSeleccionado = ref(null);

const tiposDeJuego = [
  { tipo: "quiz", nombre: "Quiz tipo test", icono: "❓", descripcion: "Preguntas de opción múltiple generadas por IA" },
  { tipo: "puzzle", nombre: "Puzzle de palabras", icono: "🧩", descripcion: "Completa las palabras clave del tema" }
];

onMounted(async () => {
  try {
    const respuesta = await servicioAsignaturas.obtenerTodas();
    listaAsignaturas.value = respuesta.data;
  } catch (error) {
    errorCarga.value = "No se pudieron cargar las asignaturas. Comprueba que el backend esté funcionando.";
  } finally {
    estaCargandoAsignaturas.value = false;
  }
});

async function seleccionarAsignatura(asignatura) {
  asignaturaSeleccionada.value = asignatura;
  temaSeleccionado.value = null;
  temasDeAsignaturaSeleccionada.value = [];
  estaCargandoTemas.value = true;
  try {
    const respuesta = await servicioTemas.obtenerPorAsignatura(asignatura.id_asignatura);
    temasDeAsignaturaSeleccionada.value = respuesta.data;
  } catch (error) {
    temasDeAsignaturaSeleccionada.value = [];
  } finally {
    estaCargandoTemas.value = false;
  }
}

function iniciarJuego(tipoJuego) {
  if (temaSeleccionado.value) {
    enrutador.push({ name: "InterfazJuego", params: { identificadorTema: temaSeleccionado.value.id_tema, tipoJuego: tipoJuego } });
  }
}
</script>

<template>
  <div class="container py-4">
    <div class="mb-4">
      <h1 class="titulo-pagina">Minijuegos educativos</h1>
      <p class="subtitulo-pagina">Selecciona una asignatura, un tema y el tipo de juego</p>
    </div>

    <div v-if="errorCarga" class="tarjeta animacion-aparecer-desde-abajo tarjeta-estado-error">
      <div class="icono-error-grande">⚠️</div>
      <p class="texto-error-carga">{{ errorCarga }}</p>
    </div>

    <template v-else>
      <h3 class="encabezado-paso">1. Elige asignatura</h3>
      <div v-if="estaCargandoAsignaturas" class="text-center py-5">
        <div class="spinner-border text-primary" role="status"></div>
      </div>
      <div v-else-if="listaAsignaturas.length === 0" class="estado-vacio estado-vacio-con-padding">
        <div class="estado-vacio-icono">📘</div>
        <p class="estado-vacio-titulo">No hay asignaturas disponibles</p>
        <p class="estado-vacio-texto">Los profesores aún no han creado asignaturas con temario</p>
      </div>
      <div v-else class="row g-3 mb-4">
        <div v-for="asignatura in listaAsignaturas" :key="asignatura.id_asignatura" class="col-12 col-md-4">
          <div class="tarjeta tarjeta-interactiva animacion-aparecer-desde-abajo h-100 tarjeta-seleccion"
            :class="{ 'tarjeta-seleccionada': asignaturaSeleccionada?.id_asignatura === asignatura.id_asignatura }"
            @click="seleccionarAsignatura(asignatura)">
            <div class="icono-asignatura">📘</div>
            <h4 class="fw-bold">{{ asignatura.nombre }}</h4>
          </div>
        </div>
      </div>

      <template v-if="asignaturaSeleccionada">
        <h3 class="encabezado-paso">2. Elige tema</h3>
        <div v-if="estaCargandoTemas" class="text-center py-5">
          <div class="spinner-border text-primary" role="status"></div>
        </div>
        <div v-else-if="temasDeAsignaturaSeleccionada.length === 0" class="estado-vacio py-4">
          <div class="estado-vacio-icono">📄</div>
          <p class="estado-vacio-titulo">Esta asignatura no tiene temas</p>
          <p class="estado-vacio-texto">El profesor aún no ha subido temario para esta asignatura</p>
        </div>
        <div v-else class="row g-3 mb-4">
          <div v-for="tema in temasDeAsignaturaSeleccionada" :key="tema.id_tema" class="col-12 col-md-4">
            <div class="tarjeta tarjeta-interactiva animacion-aparecer-desde-abajo tarjeta-tema"
              :class="{ 'tarjeta-seleccionada': temaSeleccionado?.id_tema === tema.id_tema }"
              @click="temaSeleccionado = tema">
              <div class="d-flex align-items-center gap-2"><span>📄</span><span class="fw-semibold small">{{ tema.titulo
                  }}</span></div>
            </div>
          </div>
        </div>
      </template>

      <template v-if="temaSeleccionado">
        <h3 class="encabezado-paso">3. Elige juego</h3>
        <div class="row g-3">
          <div v-for="juego in tiposDeJuego" :key="juego.tipo" class="col-12 col-md-6">
            <div class="tarjeta tarjeta-interactiva animacion-aparecer-desde-abajo text-center tarjeta-juego"
              @click="iniciarJuego(juego.tipo)">
              <div class="icono-juego">{{ juego.icono }}</div>
              <h4 class="fw-bold nombre-juego">{{ juego.nombre }}</h4>
              <p class="text-secondary small mt-1">{{ juego.descripcion }}</p>
              <button class="btn btn-primary btn-sm mt-3">¡Jugar!</button>
            </div>
          </div>
        </div>
      </template>
    </template>
  </div>
</template>

<style scoped>
.encabezado-paso {
  font-size: 0.9rem;
  font-weight: 700;
  color: var(--color-texto-secundario);
  margin-bottom: var(--espacio-pequeno);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.tarjeta-seleccionada {
  border-color: var(--color-acento) !important;
  background: var(--color-acento-sutil) !important;
}

.tarjeta-estado-error {
  text-align: center;
  padding: var(--espacio-gigante);
}

.icono-error-grande {
  font-size: 3rem;
  margin-bottom: var(--espacio-medio);
}

.texto-error-carga {
  font-weight: 600;
  margin-bottom: var(--espacio-pequeno);
}

.estado-vacio-con-padding {
  padding: var(--espacio-enorme) var(--espacio-extra-grande);
}

.tarjeta-seleccion {
  cursor: pointer;
  text-align: center;
  padding: var(--espacio-grande);
}

.icono-asignatura {
  font-size: 1.5rem;
  margin-bottom: 0.3rem;
}

.tarjeta-tema {
  cursor: pointer;
  padding: var(--espacio-medio);
}

.tarjeta-juego {
  cursor: pointer;
  padding: var(--espacio-extra-grande);
}

.icono-juego {
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
}

.nombre-juego {
  font-size: 1.05rem;
}
</style>
