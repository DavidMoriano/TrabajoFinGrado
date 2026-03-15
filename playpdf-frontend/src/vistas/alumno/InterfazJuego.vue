<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { servicioJuegos } from '@/servicios/api'

const rutaActual = useRoute()
const enrutador = useRouter()
const identificadorTema = rutaActual.params.identificadorTema
const tipoJuego = rutaActual.params.tipoJuego

const listaPreguntas = ref([])
const estaCargando = ref(true)
const errorCarga = ref(null)

const indicePreguntaActual = ref(0)
const identificadorRespuestaSeleccionada = ref(null)
const preguntaRespondida = ref(false)
const puntuacionTotal = ref(0)
const juegoTerminado = ref(false)

const preguntaActual = computed(() => listaPreguntas.value[indicePreguntaActual.value])
const porcentajeProgreso = computed(() => {
  if (listaPreguntas.value.length === 0) return 0
  return ((indicePreguntaActual.value + 1) / listaPreguntas.value.length) * 100
})

onMounted(async () => {
  try {
    await servicioJuegos.generarPreguntasConInteligenciaArtificial(identificadorTema)
    const respuesta = await servicioJuegos.obtenerPreguntas(identificadorTema, tipoJuego)
    listaPreguntas.value = respuesta.data
  } catch (error) {
    errorCarga.value = 'No se pudieron cargar las preguntas. Comprueba que el backend esté funcionando.'
  } finally {
    estaCargando.value = false
  }
})

function seleccionarRespuesta(respuesta) {
  if (preguntaRespondida.value) return
  identificadorRespuestaSeleccionada.value = respuesta.id_respuesta
  preguntaRespondida.value = true
  if (respuesta.esCorrecta) puntuacionTotal.value++
}

function avanzarSiguientePregunta() {
  if (indicePreguntaActual.value < listaPreguntas.value.length - 1) {
    indicePreguntaActual.value++
    identificadorRespuestaSeleccionada.value = null
    preguntaRespondida.value = false
  } else {
    juegoTerminado.value = true
  }
}

function reiniciarJuego() {
  indicePreguntaActual.value = 0
  identificadorRespuestaSeleccionada.value = null
  preguntaRespondida.value = false
  puntuacionTotal.value = 0
  juegoTerminado.value = false
}
</script>

<template>
  <div class="container py-4" style="max-width:700px;">
    <div v-if="estaCargando" class="tarjeta animacion-aparecer-desde-abajo"
      style="text-align:center;padding:var(--espacio-gigante);">
      <div class="indicador-carga" style="margin:0 auto var(--espacio-medio);"></div>
      <p style="color:var(--color-texto-secundario);font-size:0.95rem;">Generando preguntas con IA a partir del
        temario...</p>
    </div>

    <div v-else-if="errorCarga" class="tarjeta animacion-aparecer-desde-abajo"
      style="text-align:center;padding:var(--espacio-gigante);">
      <div style="font-size:3rem;margin-bottom:var(--espacio-medio);">⚠️</div>
      <p style="font-weight:600;font-size:1.05rem;margin-bottom:var(--espacio-pequeno);">No se pudieron cargar las
        preguntas</p>
      <p style="color:var(--color-texto-secundario);font-size:0.88rem;margin-bottom:var(--espacio-grande);">{{
        errorCarga }}</p>
      <button class="boton boton-secundario" @click="enrutador.push({ name: 'SeleccionJuegos' })">← Volver a
        juegos</button>
    </div>

    <div v-else-if="listaPreguntas.length === 0" class="tarjeta animacion-aparecer-desde-abajo"
      style="text-align:center;padding:var(--espacio-gigante);">
      <div style="font-size:3rem;margin-bottom:var(--espacio-medio);">📭</div>
      <p style="font-weight:600;font-size:1.05rem;margin-bottom:var(--espacio-pequeno);">No hay preguntas disponibles
      </p>
      <p style="color:var(--color-texto-secundario);font-size:0.88rem;margin-bottom:var(--espacio-grande);">Asegúrate de
        que el tema tiene un PDF subido con contenido suficiente</p>
      <button class="boton boton-secundario" @click="enrutador.push({ name: 'SeleccionJuegos' })">← Volver a
        juegos</button>
    </div>

    <div v-if="juegoTerminado" class="tarjeta animacion-escalar-entrada"
      style="text-align:center;padding:var(--espacio-gigante);">
      <div style="font-size:4rem;margin-bottom:var(--espacio-medio);">🎉</div>
      <h2 style="font-size:1.5rem;font-weight:800;margin-bottom:var(--espacio-pequeno);">¡Partida terminada!</h2>
      <div class="valor-estadistica" style="font-size:3rem;">{{ puntuacionTotal }}/{{ listaPreguntas.length }}</div>
      <p style="color:var(--color-texto-secundario);margin:var(--espacio-medio) 0;">
        {{ puntuacionTotal === listaPreguntas.length ? '¡Perfecto! 🏆' : puntuacionTotal >= listaPreguntas.length / 2 ?
          '¡Buen trabajo! 💪' : 'Sigue practicando 📖' }}
      </p>
      <div style="display:flex;gap:var(--espacio-pequeno);justify-content:center;margin-top:var(--espacio-grande);">
        <button class="boton boton-principal" @click="reiniciarJuego">🔄 Jugar de nuevo</button>
        <button class="boton boton-secundario" @click="enrutador.push({ name: 'SeleccionJuegos' })">← Volver</button>
      </div>
    </div>

    <template v-else>
      <div style="margin-bottom:var(--espacio-extra-grande);">
        <div style="display:flex;justify-content:space-between;margin-bottom:0.5rem;">
          <span style="font-size:0.85rem;font-weight:600;color:var(--color-texto-secundario);">Pregunta {{
            indicePreguntaActual + 1 }} de {{ listaPreguntas.length }}</span>
          <span class="etiqueta etiqueta-acento">{{ puntuacionTotal }} aciertos</span>
        </div>
        <div style="height:6px;background:var(--color-fondo-terciario);border-radius:3px;overflow:hidden;">
          <div
            :style="{ width: porcentajeProgreso + '%', height: '100%', background: 'var(--gradiente-acento)', borderRadius: '3px', transition: 'width 0.4s ease' }">
          </div>
        </div>
      </div>

      <div class="tarjeta animacion-aparecer-desde-abajo" style="padding:var(--espacio-extra-grande);">
        <h2 style="font-size:1.15rem;font-weight:700;margin-bottom:var(--espacio-extra-grande);line-height:1.5;">{{
          preguntaActual.enunciado }}</h2>
        <div style="display:flex;flex-direction:column;gap:var(--espacio-pequeno);">
          <button v-for="respuesta in preguntaActual.respuestas" :key="respuesta.id_respuesta" class="boton-respuesta"
            :class="{
              'respuesta-seleccionada': identificadorRespuestaSeleccionada === respuesta.id_respuesta,
              'respuesta-correcta': preguntaRespondida && respuesta.esCorrecta,
              'respuesta-incorrecta': preguntaRespondida && identificadorRespuestaSeleccionada === respuesta.id_respuesta && !respuesta.esCorrecta
            }" @click="seleccionarRespuesta(respuesta)" :disabled="preguntaRespondida">
            <span>{{ respuesta.texto }}</span>
            <span v-if="preguntaRespondida && respuesta.esCorrecta" style="font-size:1.1rem;">✅</span>
            <span
              v-else-if="preguntaRespondida && identificadorRespuestaSeleccionada === respuesta.id_respuesta && !respuesta.esCorrecta"
              style="font-size:1.1rem;">❌</span>
          </button>
        </div>
        <div v-if="preguntaRespondida" style="margin-top:var(--espacio-extra-grande);text-align:right;">
          <button class="boton boton-principal" @click="avanzarSiguientePregunta">
            {{ indicePreguntaActual < listaPreguntas.length - 1 ? 'Siguiente →' : 'Ver resultado' }} </button>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
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
</style>
