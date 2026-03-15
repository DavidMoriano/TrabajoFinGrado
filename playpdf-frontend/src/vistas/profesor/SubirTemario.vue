<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { servicioTemas } from '@/servicios/api'

const rutaActual = useRoute()
const identificadorAsignatura = rutaActual.params.identificadorAsignatura

const listaTemas = ref([])
const estaCargando = ref(true)
const mostrarVentanaModal = ref(false)
const datosFormularioTema = ref({ titulo: '', descripcion: '', archivoSeleccionado: null })
const nombreArchivoSeleccionado = ref('')
const estaSobreZonaArrastre = ref(false)

onMounted(async () => {
  try {
    const respuesta = await servicioTemas.obtenerPorAsignatura(identificadorAsignatura)
    listaTemas.value = respuesta.data
  } catch (error) {
    listaTemas.value = []
  } finally {
    estaCargando.value = false
  }
})

function alSeleccionarArchivo(evento) {
  const archivo = evento.target.files[0]
  if (archivo) { datosFormularioTema.value.archivoSeleccionado = archivo; nombreArchivoSeleccionado.value = archivo.name }
}

function alSoltarArchivo(evento) {
  estaSobreZonaArrastre.value = false
  const archivo = evento.dataTransfer.files[0]
  if (archivo && archivo.type === 'application/pdf') { datosFormularioTema.value.archivoSeleccionado = archivo; nombreArchivoSeleccionado.value = archivo.name }
}

async function agregarTema() {
  try {
    const datosMultipart = new FormData()
    datosMultipart.append('titulo', datosFormularioTema.value.titulo)
    datosMultipart.append('descripcion', datosFormularioTema.value.descripcion)
    datosMultipart.append('id_asignatura', identificadorAsignatura)
    datosMultipart.append('archivo_pdf', datosFormularioTema.value.archivoSeleccionado)

    const respuesta = await servicioTemas.crear(datosMultipart)
    listaTemas.value.push(respuesta.data)
    mostrarVentanaModal.value = false
    datosFormularioTema.value = { titulo: '', descripcion: '', archivoSeleccionado: null }
    nombreArchivoSeleccionado.value = ''
  } catch (error) {
    alert('Error al subir el tema')
  }
}

async function eliminarTema(identificador) {
  if (confirm('¿Eliminar este tema?')) {
    try {
      await servicioTemas.eliminar(identificador)
      listaTemas.value = listaTemas.value.filter(tema => tema.id_tema !== identificador)
    } catch (error) {
      alert('Error al eliminar el tema')
    }
  }
}
</script>

<template>
  <div class="container py-4">
    <div class="mb-4" style="display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:1rem;">
      <div>
        <h1 class="titulo-pagina">Temario de la asignatura</h1>
        <p class="subtitulo-pagina">Sube archivos PDF con el contenido de cada tema</p>
      </div>
      <button class="boton boton-principal" @click="mostrarVentanaModal = true">+ Subir tema</button>
    </div>
    <div v-if="listaTemas.length === 0" class="estado-vacio">
      <div class="estado-vacio-icono">📄</div>
      <p class="estado-vacio-titulo">Sin temas todavía</p>
      <p class="estado-vacio-texto">Sube tu primer PDF para que los alumnos puedan estudiar</p>
    </div>
    <div v-else class="hijos-escalonados" style="display:flex;flex-direction:column;gap:var(--espacio-medio);">
      <div v-for="tema in listaTemas" :key="tema.id_tema" class="tarjeta animacion-aparecer-desde-abajo"
        style="display:flex;align-items:center;justify-content:space-between;gap:1rem;">
        <div style="display:flex;align-items:center;gap:1rem;flex:1;">
          <div
            style="width:44px;height:44px;border-radius:var(--redondeo-medio);background:var(--color-error-claro);display:flex;align-items:center;justify-content:center;font-size:1.3rem;flex-shrink:0;">
            📕</div>
          <div style="flex:1;min-width:0;">
            <h3 style="font-size:0.95rem;font-weight:700;">{{ tema.titulo }}</h3>
            <p
              style="font-size:0.82rem;color:var(--color-texto-secundario);white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">
              {{ tema.descripcion }}</p>
          </div>
        </div>
        <div style="display:flex;align-items:center;gap:var(--espacio-medio);flex-shrink:0;">
          <span class="etiqueta etiqueta-acento">{{ tema.nombreArchivoPdf }}</span>
          <span style="font-size:0.78rem;color:var(--color-texto-terciario);">{{ tema.fechaSubida }}</span>
          <button class="boton boton-fantasma boton-pequeno" @click="eliminarTema(tema.id_tema)"
            style="color:var(--color-error);">🗑️</button>
        </div>
      </div>
    </div>
    <div v-if="mostrarVentanaModal" class="superposicion-modal" @click.self="mostrarVentanaModal = false">
      <div class="contenido-modal animacion-escalar-entrada">
        <div class="cabecera-modal">
          <h2 class="titulo-modal">Subir nuevo tema</h2><button class="boton boton-fantasma boton-icono"
            @click="mostrarVentanaModal = false">✕</button>
        </div>
        <form @submit.prevent="agregarTema" style="display:flex;flex-direction:column;gap:var(--espacio-medio);">
          <div class="grupo-campo"><label class="etiqueta-campo">Título del tema</label><input
              v-model="datosFormularioTema.titulo" required placeholder="Ej: Tema 3 - Funciones" /></div>
          <div class="grupo-campo"><label class="etiqueta-campo">Descripción</label><textarea
              v-model="datosFormularioTema.descripcion" rows="2"
              placeholder="Breve descripción del contenido"></textarea></div>
          <div class="grupo-campo">
            <label class="etiqueta-campo">Archivo PDF</label>
            <div class="zona-arrastre-archivo" :class="{ 'zona-arrastre-activa': estaSobreZonaArrastre }"
              @dragover.prevent="estaSobreZonaArrastre = true" @dragleave="estaSobreZonaArrastre = false"
              @drop.prevent="alSoltarArchivo" @click="$refs.campoArchivo.click()">
              <input ref="campoArchivo" type="file" accept=".pdf" @change="alSeleccionarArchivo"
                style="display:none;" />
              <div v-if="!nombreArchivoSeleccionado" style="text-align:center;">
                <div style="font-size:2rem;margin-bottom:0.5rem;">📤</div>
                <p style="font-size:0.88rem;color:var(--color-texto-secundario);">Arrastra tu PDF aquí o haz clic para
                  seleccionar</p>
              </div>
              <div v-else style="display:flex;align-items:center;gap:0.75rem;"><span
                  style="font-size:1.3rem;">📄</span><span style="font-weight:600;font-size:0.9rem;">{{
                  nombreArchivoSeleccionado }}</span><button type="button" class="boton boton-fantasma boton-pequeno"
                  @click.stop="nombreArchivoSeleccionado = ''; datosFormularioTema.archivoSeleccionado = null">✕</button>
              </div>
            </div>
          </div>
          <div class="acciones-modal"><button type="button" class="boton boton-secundario"
              @click="mostrarVentanaModal = false">Cancelar</button><button type="submit" class="boton boton-principal"
              :disabled="!nombreArchivoSeleccionado">Subir tema</button></div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.zona-arrastre-archivo {
  border: 2px dashed var(--color-borde-principal);
  border-radius: var(--redondeo-grande);
  padding: var(--espacio-extra-grande);
  cursor: pointer;
  transition: all var(--transicion-rapida);
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 120px;
}

.zona-arrastre-archivo:hover,
.zona-arrastre-activa {
  border-color: var(--color-acento);
  background: var(--color-acento-sutil);
}
</style>
