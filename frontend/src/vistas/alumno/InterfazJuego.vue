<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { servicioJuegos, servicioEstadisticas } from "@/servicios/api";

const rutaActual = useRoute();
const enrutador = useRouter();
const identificadorTema = rutaActual.params.identificadorTema;
const tipoJuego = rutaActual.params.tipoJuego;
const identificadorAsignatura = rutaActual.query.idAsignatura;

const esPuzzle = tipoJuego === "puzzle";

const listaPreguntas = ref([]);
const estaCargando = ref(true);
const errorCarga = ref(null);

const indicePreguntaActual = ref(0);
const preguntaRespondida = ref(false);
const puntuacionTotal = ref(0);
const juegoTerminado = ref(false);

const identificadorRespuestaSeleccionada = ref(null);

const respuestaEscrita = ref("");
const resultadoPuzzle = ref(null);

const preguntaActual = computed(() => listaPreguntas.value[indicePreguntaActual.value]);
const porcentajeProgreso = computed(() => {
  if (listaPreguntas.value.length === 0) return 0;
  return ((indicePreguntaActual.value + 1) / listaPreguntas.value.length) * 100;
});

const palabraCorrecta = computed(() => {
  if (!preguntaActual.value) return "";
  const correcta = preguntaActual.value.respuestas.find(r => r.esCorrecta);
  return correcta ? correcta.texto.toLowerCase().trim() : "";
});

const partesPuzzle = computed(() => {
  if (!preguntaActual.value) return { antes: "", despues: "" };
  const partes = preguntaActual.value.enunciado.split("_____");
  return { antes: partes[0] || "", despues: partes[1] || "" };
});

onMounted(async () => {
  try {
    await servicioJuegos.generarPreguntasConInteligenciaArtificial(identificadorTema);
    const respuesta = await servicioJuegos.obtenerPreguntas(identificadorTema, tipoJuego);
    listaPreguntas.value = respuesta.data;
  } catch (error) {
    errorCarga.value = "No se pudieron cargar las preguntas. Comprueba que el backend esté funcionando.";
  } finally {
    estaCargando.value = false;
  }
});

function seleccionarRespuesta(respuesta) {
  if (preguntaRespondida.value) return;
  identificadorRespuestaSeleccionada.value = respuesta.id_respuesta;
  preguntaRespondida.value = true;
  if (respuesta.esCorrecta) puntuacionTotal.value++;
}

function comprobarPuzzle() {
  if (preguntaRespondida.value) return;
  const escrita = respuestaEscrita.value.toLowerCase().trim();
  const correcta = palabraCorrecta.value;
  preguntaRespondida.value = true;
  if (escrita === correcta) {
    resultadoPuzzle.value = "correcta";
    puntuacionTotal.value++;
  } else {
    resultadoPuzzle.value = "incorrecta";
  }
}

async function avanzarSiguientePregunta() {
  if (indicePreguntaActual.value < listaPreguntas.value.length - 1) {
    indicePreguntaActual.value++;
    preguntaRespondida.value = false;
    identificadorRespuestaSeleccionada.value = null;
    respuestaEscrita.value = "";
    resultadoPuzzle.value = null;
  } else {
    juegoTerminado.value = true;
    if (identificadorAsignatura) {
      try {
        await servicioEstadisticas.registrarPartida(
          identificadorAsignatura,
          puntuacionTotal.value,
          listaPreguntas.value.length
        );
      } catch { }
    }
  }
}

function reiniciarJuego() {
  indicePreguntaActual.value = 0;
  preguntaRespondida.value = false;
  identificadorRespuestaSeleccionada.value = null;
  respuestaEscrita.value = "";
  resultadoPuzzle.value = null;
  puntuacionTotal.value = 0;
  juegoTerminado.value = false;
}
</script>

<template>
  <div class="contenedor-juego container py-4">

    <div v-if="estaCargando" class="tarjeta animacion-aparecer-desde-abajo tarjeta-estado">
      <div class="indicador-carga indicador-carga-centrado"></div>
      <p class="texto-estado">Generando preguntas con IA a partir del temario...</p>
    </div>

    <div v-else-if="errorCarga" class="tarjeta animacion-aparecer-desde-abajo tarjeta-estado">
      <div class="icono-estado-grande">⚠️</div>
      <p class="titulo-estado">No se pudieron cargar las preguntas</p>
      <p class="descripcion-estado">{{ errorCarga }}</p>
      <button class="boton boton-secundario" @click="enrutador.push({ name: 'SeleccionJuegos' })">← Volver a
        juegos</button>
    </div>

    <div v-else-if="listaPreguntas.length === 0" class="tarjeta animacion-aparecer-desde-abajo tarjeta-estado">
      <div class="icono-estado-grande">📭</div>
      <p class="titulo-estado">No hay preguntas disponibles</p>
      <p class="descripcion-estado">Asegúrate de que el tema tiene un PDF subido con contenido suficiente</p>
      <button class="boton boton-secundario" @click="enrutador.push({ name: 'SeleccionJuegos' })">← Volver a
        juegos</button>
    </div>

    <div v-else-if="juegoTerminado" class="tarjeta animacion-escalar-entrada tarjeta-estado">
      <div class="icono-resultado">🎉</div>
      <h2 class="titulo-resultado">¡Partida terminada!</h2>
      <div class="valor-estadistica puntuacion-final">{{ puntuacionTotal }}/{{ listaPreguntas.length }}</div>
      <p class="mensaje-resultado">
        {{ puntuacionTotal === listaPreguntas.length ? '¡Perfecto! 🏆'
          : puntuacionTotal >= listaPreguntas.length / 2 ? '¡Buen trabajo! 💪'
            : 'Sigue practicando 📖' }}
      </p>
      <div class="acciones-resultado">
        <button class="boton boton-principal" @click="reiniciarJuego">🔄 Jugar de nuevo</button>
        <button class="boton boton-secundario" @click="enrutador.push({ name: 'SeleccionJuegos' })">← Volver</button>
      </div>
    </div>

    <template v-else>
      <div class="seccion-progreso">
        <div class="fila-progreso">
          <span class="texto-progreso">Pregunta {{ indicePreguntaActual + 1 }} de {{ listaPreguntas.length }}</span>
          <span class="etiqueta etiqueta-acento">{{ puntuacionTotal }} aciertos</span>
        </div>
        <div class="barra-progreso-fondo">
          <div class="barra-progreso-relleno" :style="{ width: porcentajeProgreso + '%' }"></div>
        </div>
      </div>

      <div v-if="!esPuzzle" class="tarjeta animacion-aparecer-desde-abajo tarjeta-pregunta">
        <h2 class="enunciado-pregunta">{{ preguntaActual.enunciado }}</h2>
        <div class="lista-respuestas">
          <button v-for="respuesta in preguntaActual.respuestas" :key="respuesta.id_respuesta" class="boton-respuesta"
            :class="{
              'respuesta-seleccionada': identificadorRespuestaSeleccionada === respuesta.id_respuesta,
              'respuesta-correcta': preguntaRespondida && respuesta.esCorrecta,
              'respuesta-incorrecta': preguntaRespondida && identificadorRespuestaSeleccionada === respuesta.id_respuesta && !respuesta.esCorrecta
            }" @click="seleccionarRespuesta(respuesta)" :disabled="preguntaRespondida">
            <span>{{ respuesta.texto }}</span>
            <span v-if="preguntaRespondida && respuesta.esCorrecta" class="icono-resultado-respuesta">✅</span>
            <span
              v-else-if="preguntaRespondida && identificadorRespuestaSeleccionada === respuesta.id_respuesta && !respuesta.esCorrecta"
              class="icono-resultado-respuesta">❌</span>
          </button>
        </div>
        <div v-if="preguntaRespondida" class="contenedor-boton-siguiente">
          <button class="boton boton-principal" @click="avanzarSiguientePregunta">
            {{ indicePreguntaActual < listaPreguntas.length - 1 ? 'Siguiente →' : 'Ver resultado' }} </button>
        </div>
      </div>

      <div v-else class="tarjeta animacion-aparecer-desde-abajo tarjeta-pregunta">
        <p class="etiqueta-puzzle">🧩 Completa la frase</p>
        <div class="frase-puzzle">
          <span class="texto-frase">{{ partesPuzzle.antes }}</span>
          <span v-if="!preguntaRespondida" class="hueco-activo">_____</span>
          <span v-else class="palabra-revelada"
            :class="resultadoPuzzle === 'correcta' ? 'palabra-correcta' : 'palabra-incorrecta'">
            {{ palabraCorrecta }}
          </span>
          <span class="texto-frase">{{ partesPuzzle.despues }}</span>
        </div>

        <div v-if="!preguntaRespondida" class="grupo-input-puzzle">
          <input v-model="respuestaEscrita" class="input-puzzle" type="text"
            placeholder="Escribe la palabra que falta..." @keyup.enter="comprobarPuzzle" autofocus />
          <button class="boton boton-principal" @click="comprobarPuzzle" :disabled="!respuestaEscrita.trim()">
            Comprobar ✓
          </button>
        </div>

        <div v-else class="feedback-puzzle"
          :class="resultadoPuzzle === 'correcta' ? 'feedback-correcto' : 'feedback-incorrecto'">
          <span v-if="resultadoPuzzle === 'correcta'">✅ ¡Correcto!</span>
          <span v-else>❌ Incorrecto — la respuesta era: <strong>{{ palabraCorrecta }}</strong></span>
        </div>

        <div v-if="preguntaRespondida" class="contenedor-boton-siguiente">
          <button class="boton boton-principal" @click="avanzarSiguientePregunta">
            {{ indicePreguntaActual < listaPreguntas.length - 1 ? 'Siguiente →' : 'Ver resultado' }} </button>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.contenedor-juego {
  max-width: 700px;
}

.tarjeta-estado {
  text-align: center;
  padding: var(--espacio-gigante);
}

.indicador-carga-centrado {
  margin: 0 auto var(--espacio-medio);
}

.texto-estado {
  color: var(--color-texto-secundario);
  font-size: 0.95rem;
}

.icono-estado-grande {
  font-size: 3rem;
  margin-bottom: var(--espacio-medio);
}

.titulo-estado {
  font-weight: 600;
  font-size: 1.05rem;
  margin-bottom: var(--espacio-pequeno);
}

.descripcion-estado {
  color: var(--color-texto-secundario);
  font-size: 0.88rem;
  margin-bottom: var(--espacio-grande);
}

.icono-resultado {
  font-size: 4rem;
  margin-bottom: var(--espacio-medio);
}

.titulo-resultado {
  font-size: 1.5rem;
  font-weight: 800;
  margin-bottom: var(--espacio-pequeno);
}

.puntuacion-final {
  font-size: 3rem;
}

.mensaje-resultado {
  color: var(--color-texto-secundario);
  margin: var(--espacio-medio) 0;
}

.acciones-resultado {
  display: flex;
  gap: var(--espacio-pequeno);
  justify-content: center;
  margin-top: var(--espacio-grande);
}

.seccion-progreso {
  margin-bottom: var(--espacio-extra-grande);
}

.fila-progreso {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.texto-progreso {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--color-texto-secundario);
}

.barra-progreso-fondo {
  height: 6px;
  background: var(--color-fondo-terciario);
  border-radius: 3px;
  overflow: hidden;
}

.barra-progreso-relleno {
  height: 100%;
  background: var(--gradiente-acento);
  border-radius: 3px;
  transition: width 0.4s ease;
}

.tarjeta-pregunta {
  padding: var(--espacio-extra-grande);
}

/* Quiz */
.enunciado-pregunta {
  font-size: 1.15rem;
  font-weight: 700;
  margin-bottom: var(--espacio-extra-grande);
  line-height: 1.5;
}

.lista-respuestas {
  display: flex;
  flex-direction: column;
  gap: var(--espacio-pequeno);
}

.icono-resultado-respuesta {
  font-size: 1.1rem;
}

.boton-respuesta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.85rem 1.1rem;
  border-radius: var(--redondeo-medio);
  border: 1.5px solid var(--color-borde-principal);
  background: var(--color-fondo-secundario);
  color: var(--color-texto-principal);
  font-family: var(--fuente-cuerpo);
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transicion-rapida);
  text-align: left;
}

.boton-respuesta:not(:disabled):hover {
  border-color: var(--color-acento);
  background: var(--color-acento-sutil);
}

.respuesta-seleccionada {
  border-color: var(--color-acento);
  background: var(--color-acento-sutil);
}

.respuesta-correcta {
  border-color: var(--color-exito) !important;
  background: var(--color-exito-claro) !important;
}

.respuesta-incorrecta {
  border-color: var(--color-error) !important;
  background: var(--color-error-claro) !important;
}

.boton-respuesta:disabled {
  cursor: default;
}

.etiqueta-puzzle {
  font-size: 0.8rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: var(--color-texto-secundario);
  margin-bottom: var(--espacio-medio);
}

.frase-puzzle {
  font-size: 1.15rem;
  font-weight: 600;
  line-height: 2;
  margin-bottom: var(--espacio-extra-grande);
  background: var(--color-fondo-terciario);
  border-radius: var(--redondeo-medio);
  padding: var(--espacio-grande);
}

.texto-frase {
  color: var(--color-texto-principal);
}

.hueco-activo {
  display: inline-block;
  min-width: 80px;
  border-bottom: 2.5px solid var(--color-acento);
  color: var(--color-acento);
  font-weight: 800;
  letter-spacing: 0.12em;
  margin: 0 4px;
}

.palabra-revelada {
  display: inline-block;
  margin: 0 4px;
  padding: 0 6px;
  border-radius: 4px;
  font-weight: 800;
}

.palabra-correcta {
  background: var(--color-exito-claro);
  color: var(--color-exito);
}

.palabra-incorrecta {
  background: var(--color-error-claro);
  color: var(--color-error);
}

.grupo-input-puzzle {
  display: flex;
  gap: var(--espacio-pequeno);
  margin-bottom: var(--espacio-medio);
}

.input-puzzle {
  flex: 1;
  padding: 0.75rem 1rem;
  border-radius: var(--redondeo-medio);
  border: 1.5px solid var(--color-borde-principal);
  background: var(--color-fondo-secundario);
  color: var(--color-texto-principal);
  font-family: var(--fuente-cuerpo);
  font-size: 1rem;
  outline: none;
  transition: border-color var(--transicion-rapida);
}

.input-puzzle:focus {
  border-color: var(--color-acento);
}

.feedback-puzzle {
  padding: 0.75rem 1rem;
  border-radius: var(--redondeo-medio);
  font-weight: 600;
  font-size: 0.95rem;
  margin-bottom: var(--espacio-medio);
}

.feedback-correcto {
  background: var(--color-exito-claro);
  color: var(--color-exito);
  border: 1px solid var(--color-exito);
}

.feedback-incorrecto {
  background: var(--color-error-claro);
  color: var(--color-error);
  border: 1px solid var(--color-error);
}

.contenedor-boton-siguiente {
  margin-top: var(--espacio-extra-grande);
  text-align: right;
}
</style>
