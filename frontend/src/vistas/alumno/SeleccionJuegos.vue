<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { servicioCentros, servicioAsignaturas, servicioTemas } from "@/servicios/api";

const enrutador = useRouter();

const misCentros = ref([]);
const centroExpandido = ref(null);
const asignaturasPorCentro = ref({});
const temasDeAsignaturaSeleccionada = ref([]);
const estaCargando = ref(true);
const estaCargandoTemas = ref(false);
const mostrarModalCodigo = ref(false);
const codigoIntroducido = ref('');
const errorCodigo = ref('');
const buscandoCodigo = ref(false);

const asignaturaSeleccionada = ref(null);
const temaSeleccionado = ref(null);

const tiposDeJuego = [
  { tipo: "quiz", nombre: "Quiz tipo test", icono: "❓", descripcion: "Preguntas de opción múltiple generadas por IA" },
  { tipo: "puzzle", nombre: "Puzzle de palabras", icono: "🧩", descripcion: "Completa las palabras clave del tema" }
];

onMounted(async () => {
  try {
    const resp = await servicioCentros.obtenerMisCentros();
    misCentros.value = resp.data;
    for (const centro of misCentros.value) {
      const resAsig = await servicioAsignaturas.obtenerPorCentro(centro.id_centro);
      asignaturasPorCentro.value[centro.id_centro] = resAsig.data;
    }
  } catch {
    misCentros.value = [];
  } finally {
    estaCargando.value = false;
  }
});

async function unirseConCodigo() {
  errorCodigo.value = '';
  const codigo = codigoIntroducido.value.trim().toUpperCase();
  if (!codigo) return;
  buscandoCodigo.value = true;
  try {
    const resp = await servicioCentros.unirseACentro(codigo);
    const centro = resp.data;
    const yaExiste = misCentros.value.some(c => c.id_centro === centro.id_centro);
    if (!yaExiste) {
      misCentros.value.push(centro);
      const resAsig = await servicioAsignaturas.obtenerPorCentro(centro.id_centro);
      asignaturasPorCentro.value[centro.id_centro] = resAsig.data;
    }
    mostrarModalCodigo.value = false;
    codigoIntroducido.value = '';
  } catch (error) {
    errorCodigo.value = error.response?.data?.message || 'Código incorrecto.';
  } finally {
    buscandoCodigo.value = false;
  }
}

async function seleccionarAsignatura(asignatura) {
  asignaturaSeleccionada.value = asignatura;
  temaSeleccionado.value = null;
  temasDeAsignaturaSeleccionada.value = [];
  estaCargandoTemas.value = true;
  try {
    const resp = await servicioTemas.obtenerPorAsignatura(asignatura.id_asignatura);
    temasDeAsignaturaSeleccionada.value = resp.data;
  } catch {
    temasDeAsignaturaSeleccionada.value = [];
  } finally {
    estaCargandoTemas.value = false;
  }
}

function iniciarJuego(tipoJuego) {
  if (temaSeleccionado.value) {
    enrutador.push({
      name: "InterfazJuego",
      params: { identificadorTema: temaSeleccionado.value.id_tema, tipoJuego: tipoJuego },
      query: { idAsignatura: asignaturaSeleccionada.value?.id_asignatura }
    });
  }
}
</script>

<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
      <div>
        <h1 class="titulo-pagina">Minijuegos educativos</h1>
        <p class="subtitulo-pagina">Selecciona un centro, asignatura, tema y tipo de juego</p>
      </div>
      <button class="btn btn-outline-primary btn-sm" @click="mostrarModalCodigo = true">🔑 Unirse a un centro</button>
    </div>

    <div v-if="estaCargando" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else-if="misCentros.length === 0" class="estado-vacio">
      <div class="estado-vacio-icono">🏫</div>
      <p class="estado-vacio-titulo">No perteneces a ningún centro</p>
      <p class="estado-vacio-texto">Introduce el código de acceso de tu centro educativo para empezar a jugar</p>
      <button class="btn btn-primary mt-3" @click="mostrarModalCodigo = true">🔑 Introducir código</button>
    </div>

    <template v-else>
      <h3 class="encabezado-paso">1. Elige centro y asignatura</h3>
      <div class="d-flex flex-column gap-3 mb-4">
        <div v-for="centro in misCentros" :key="centro.id_centro" class="tarjeta">
          <div class="d-flex align-items-center justify-content-between cursor-pointer"
            @click="centroExpandido = centroExpandido === centro.id_centro ? null : centro.id_centro">
            <div class="d-flex align-items-center gap-3">
              <span style="font-size:1.3rem">🏫</span>
              <div>
                <h4 class="fw-bold mb-0 small">{{ centro.nombre }}</h4>
                <span class="text-secondary" style="font-size:0.75rem">{{ centro.ciudad }} · Código: <strong>{{ centro.codigo_acceso }}</strong></span>
              </div>
            </div>
            <span class="text-secondary">{{ centroExpandido === centro.id_centro ? '▲' : '▼' }}</span>
          </div>

          <div v-if="centroExpandido === centro.id_centro" class="mt-3">
            <div v-if="!asignaturasPorCentro[centro.id_centro]?.length" class="text-secondary small py-2">
              Este centro no tiene asignaturas disponibles aún.
            </div>
            <div v-else class="row g-2">
              <div v-for="asignatura in asignaturasPorCentro[centro.id_centro]" :key="asignatura.id_asignatura" class="col-12 col-md-4">
                <div class="tarjeta tarjeta-interactiva tarjeta-seleccion"
                  :class="{ 'tarjeta-seleccionada': asignaturaSeleccionada?.id_asignatura === asignatura.id_asignatura }"
                  @click="seleccionarAsignatura(asignatura)">
                  <div class="icono-asignatura">📘</div>
                  <h5 class="fw-bold small mb-0">{{ asignatura.nombre }}</h5>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <template v-if="asignaturaSeleccionada">
        <h3 class="encabezado-paso">2. Elige tema</h3>
        <div v-if="estaCargandoTemas" class="text-center py-4">
          <div class="spinner-border text-primary" role="status"></div>
        </div>
        <div v-else-if="temasDeAsignaturaSeleccionada.length === 0" class="estado-vacio py-4">
          <div class="estado-vacio-icono">📄</div>
          <p class="estado-vacio-titulo">Esta asignatura no tiene temas</p>
        </div>
        <div v-else class="row g-3 mb-4">
          <div v-for="tema in temasDeAsignaturaSeleccionada" :key="tema.id_tema" class="col-12 col-md-4">
            <div class="tarjeta tarjeta-interactiva tarjeta-tema"
              :class="{ 'tarjeta-seleccionada': temaSeleccionado?.id_tema === tema.id_tema }"
              @click="temaSeleccionado = tema">
              <div class="d-flex align-items-center gap-2">
                <span>📄</span>
                <span class="fw-semibold small">{{ tema.titulo }}</span>
              </div>
            </div>
          </div>
        </div>
      </template>

      <template v-if="temaSeleccionado">
        <h3 class="encabezado-paso">3. Elige juego</h3>
        <div class="row g-3">
          <div v-for="juego in tiposDeJuego" :key="juego.tipo" class="col-12 col-md-6">
            <div class="tarjeta tarjeta-interactiva text-center tarjeta-juego" @click="iniciarJuego(juego.tipo)">
              <div class="icono-juego">{{ juego.icono }}</div>
              <h4 class="fw-bold nombre-juego">{{ juego.nombre }}</h4>
              <p class="text-secondary small mt-1">{{ juego.descripcion }}</p>
              <button class="btn btn-primary btn-sm mt-3">¡Jugar!</button>
            </div>
          </div>
        </div>
      </template>
    </template>

    <div v-if="mostrarModalCodigo" class="superposicion-modal" @click.self="mostrarModalCodigo = false; errorCodigo = ''">
      <div class="contenido-modal animacion-escalar-entrada">
        <div class="cabecera-modal">
          <h2 class="titulo-modal">Unirse a un centro</h2>
          <button class="boton boton-fantasma boton-icono" @click="mostrarModalCodigo = false; errorCodigo = ''">✕</button>
        </div>
        <p class="text-secondary small mb-3">Introduce el código de acceso de tu centro educativo.</p>
        <form @submit.prevent="unirseConCodigo" class="d-flex flex-column gap-3">
          <div class="grupo-campo">
            <label class="etiqueta-campo">Código de acceso</label>
            <input v-model="codigoIntroducido" required placeholder="Ej: IES2024"
              style="text-transform:uppercase;letter-spacing:0.1em;font-weight:700;font-size:1.1rem;text-align:center"
              maxlength="20" autofocus />
          </div>
          <div v-if="errorCodigo" class="alerta-error">{{ errorCodigo }}</div>
          <div class="acciones-modal">
            <button type="button" class="boton boton-secundario" @click="mostrarModalCodigo = false; errorCodigo = ''">Cancelar</button>
            <button type="submit" class="boton boton-principal" :disabled="buscandoCodigo">
              <span v-if="buscandoCodigo"><span class="spinner-border spinner-border-sm me-1"></span>Buscando...</span>
              <span v-else>Unirse</span>
            </button>
          </div>
        </form>
      </div>
    </div>
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
.cursor-pointer {
  cursor: pointer;
}
.alerta-error {
  background: var(--color-error-claro);
  color: var(--color-error);
  border: 1px solid var(--color-error);
  border-radius: var(--redondeo-medio);
  padding: 0.6rem 1rem;
  font-size: 0.88rem;
}
</style>
