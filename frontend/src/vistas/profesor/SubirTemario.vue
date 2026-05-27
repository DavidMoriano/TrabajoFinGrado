<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { servicioTemas } from "@/servicios/api";
import { servicioJuegos } from "@/servicios/api";

const rutaActual = useRoute();
const identificadorAsignatura = rutaActual.params.identificadorAsignatura;

const listaTemas = ref([]);
const estaCargando = ref(true);
const mostrarVentanaModal = ref(false);
const datosFormularioTema = ref({ titulo: "", descripcion: "", archivoSeleccionado: null });
const nombreArchivoSeleccionado = ref("");
const estaSobreZonaArrastre = ref(false);

const estadoRegeneracion = ref({});

onMounted(async () => {
  try {
    const respuesta = await servicioTemas.obtenerPorAsignatura(identificadorAsignatura);
    listaTemas.value = respuesta.data;
  } catch {
    listaTemas.value = [];
  } finally {
    estaCargando.value = false;
  }
});

function alSeleccionarArchivo(evento) {
  const archivo = evento.target.files[0];
  if (archivo) {
    datosFormularioTema.value.archivoSeleccionado = archivo;
    nombreArchivoSeleccionado.value = archivo.name;
  }
}

function alSoltarArchivo(evento) {
  estaSobreZonaArrastre.value = false;
  const archivo = evento.dataTransfer.files[0];
  if (archivo && archivo.type === "application/pdf") {
    datosFormularioTema.value.archivoSeleccionado = archivo;
    nombreArchivoSeleccionado.value = archivo.name;
  }
}

async function agregarTema() {
  try {
    const datosMultipart = new FormData();
    datosMultipart.append("titulo", datosFormularioTema.value.titulo);
    datosMultipart.append("descripcion", datosFormularioTema.value.descripcion);
    datosMultipart.append("id_asignatura", identificadorAsignatura);
    datosMultipart.append("archivo_pdf", datosFormularioTema.value.archivoSeleccionado);
    const respuesta = await servicioTemas.crear(datosMultipart);
    listaTemas.value.push(respuesta.data);
    mostrarVentanaModal.value = false;
    datosFormularioTema.value = { titulo: "", descripcion: "", archivoSeleccionado: null };
    nombreArchivoSeleccionado.value = "";
  } catch (error) {
    alert("Error al subir el tema: " + (error.response?.data?.message || error.response?.status || error.message));
  }
}

async function eliminarTema(identificador) {
  if (confirm("¿Eliminar este tema? También se borrarán sus preguntas generadas.")) {
    try {

      await servicioJuegos.borrarPreguntas(identificador).catch(() => {});
      await servicioTemas.eliminar(identificador);
      listaTemas.value = listaTemas.value.filter(t => t.id_tema !== identificador);
    } catch {
      alert("Error al eliminar el tema");
    }
  }
}

async function regenerarPreguntas(idTema) {
  estadoRegeneracion.value[idTema] = "cargando";
  try {
    await servicioJuegos.regenerarPreguntas(idTema);
    estadoRegeneracion.value[idTema] = "ok";
    setTimeout(() => { estadoRegeneracion.value[idTema] = "idle"; }, 3000);
  } catch {
    estadoRegeneracion.value[idTema] = "error";
    setTimeout(() => { estadoRegeneracion.value[idTema] = "idle"; }, 3000);
  }
}

async function borrarPreguntas(idTema) {
  if (confirm("¿Borrar las preguntas de este tema? Se podrán regenerar después.")) {
    estadoRegeneracion.value[idTema] = "cargando";
    try {
      await servicioJuegos.borrarPreguntas(idTema);
      estadoRegeneracion.value[idTema] = "idle";
    } catch {
      estadoRegeneracion.value[idTema] = "error";
      setTimeout(() => { estadoRegeneracion.value[idTema] = "idle"; }, 3000);
    }
  }
}
</script>

<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
      <div>
        <h1 class="titulo-pagina">Temario de la asignatura</h1>
        <p class="subtitulo-pagina">Sube PDFs y gestiona las preguntas generadas por IA</p>
      </div>
      <button class="btn btn-primary" @click="mostrarVentanaModal = true">+ Subir tema</button>
    </div>

    <div v-if="estaCargando" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else-if="listaTemas.length === 0" class="estado-vacio">
      <div class="estado-vacio-icono">📄</div>
      <p class="estado-vacio-titulo">Sin temas todavía</p>
      <p class="estado-vacio-texto">Sube tu primer PDF para que los alumnos puedan estudiar</p>
    </div>

    <div v-else class="d-flex flex-column gap-3">
      <div v-for="tema in listaTemas" :key="tema.id_tema"
        class="tarjeta animacion-aparecer-desde-abajo">

        <div class="d-flex align-items-center gap-3 flex-wrap">
          <div class="icono-tema-pdf">📕</div>
          <div class="flex-grow-1 min-w-0">
            <h3 class="titulo-tema mb-0">{{ tema.titulo }}</h3>
            <p class="descripcion-tema mb-0">{{ tema.descripcion }}</p>
          </div>
          <div class="d-flex align-items-center gap-2 flex-shrink-0 flex-wrap">
            <span class="badge bg-primary bg-opacity-10 text-primary">{{ tema.nombre_archivo_pdf }}</span>
            <span class="texto-fecha">{{ tema.fecha_subida }}</span>
            <button class="btn btn-outline-danger btn-sm" @click="eliminarTema(tema.id_tema)">🗑️ Tema</button>
          </div>
        </div>

        <div class="barra-ia mt-3 pt-3">
          <span class="etiqueta-ia">🤖 Preguntas IA</span>
          <div class="acciones-ia">

            <button
              class="btn btn-sm btn-outline-primary"
              :disabled="estadoRegeneracion[tema.id_tema] === 'cargando'"
              @click="regenerarPreguntas(tema.id_tema)"
            >
              <span v-if="estadoRegeneracion[tema.id_tema] === 'cargando'">
                <span class="spinner-border spinner-border-sm me-1"></span>Generando...
              </span>
              <span v-else-if="estadoRegeneracion[tema.id_tema] === 'ok'">✅ Regeneradas</span>
              <span v-else-if="estadoRegeneracion[tema.id_tema] === 'error'">❌ Error</span>
              <span v-else>🔄 Regenerar preguntas</span>
            </button>

            <button
              class="btn btn-sm btn-outline-danger"
              :disabled="estadoRegeneracion[tema.id_tema] === 'cargando'"
              @click="borrarPreguntas(tema.id_tema)"
            >
              🗑️ Borrar preguntas
            </button>
          </div>
        </div>

      </div>
    </div>

    <div v-if="mostrarVentanaModal" class="superposicion-modal" @click.self="mostrarVentanaModal = false">
      <div class="contenido-modal animacion-escalar-entrada">
        <div class="cabecera-modal">
          <h2 class="titulo-modal">Subir nuevo tema</h2>
          <button class="boton boton-fantasma boton-icono" @click="mostrarVentanaModal = false">✕</button>
        </div>
        <form @submit.prevent="agregarTema" class="d-flex flex-column gap-3">
          <div class="grupo-campo">
            <label class="etiqueta-campo">Título del tema</label>
            <input v-model="datosFormularioTema.titulo" class="form-control" required
              placeholder="Ej: Tema 3 - Funciones" />
          </div>
          <div class="grupo-campo">
            <label class="etiqueta-campo">Descripción</label>
            <textarea v-model="datosFormularioTema.descripcion" class="form-control" rows="2"
              placeholder="Breve descripción del contenido"></textarea>
          </div>
          <div class="grupo-campo">
            <label class="etiqueta-campo">Archivo PDF</label>
            <div class="zona-arrastre-archivo" :class="{ 'zona-arrastre-activa': estaSobreZonaArrastre }"
              @dragover.prevent="estaSobreZonaArrastre = true" @dragleave="estaSobreZonaArrastre = false"
              @drop.prevent="alSoltarArchivo" @click="$refs.campoArchivo.click()">
              <input ref="campoArchivo" type="file" accept=".pdf" @change="alSeleccionarArchivo"
                class="campo-archivo-oculto" />
              <div v-if="!nombreArchivoSeleccionado" class="text-center">
                <div class="icono-subida">📤</div>
                <p class="texto-subida">Arrastra tu PDF aquí o haz clic para seleccionar</p>
              </div>
              <div v-else class="d-flex align-items-center gap-2">
                <span class="icono-archivo">📄</span>
                <span class="nombre-archivo">{{ nombreArchivoSeleccionado }}</span>
                <button type="button" class="btn btn-outline-secondary btn-sm"
                  @click.stop="nombreArchivoSeleccionado = ''; datosFormularioTema.archivoSeleccionado = null">✕</button>
              </div>
            </div>
          </div>
          <div class="acciones-modal">
            <button type="button" class="boton boton-secundario" @click="mostrarVentanaModal = false">Cancelar</button>
            <button type="submit" class="boton boton-principal" :disabled="!nombreArchivoSeleccionado">Subir tema</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.icono-tema-pdf {
  width: 44px;
  height: 44px;
  border-radius: var(--redondeo-medio);
  background: var(--color-error-claro);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.3rem;
  flex-shrink: 0;
}

.titulo-tema {
  font-size: 0.95rem;
  font-weight: 700;
}

.descripcion-tema {
  font-size: 0.82rem;
  color: var(--color-texto-secundario);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.texto-fecha {
  font-size: 0.78rem;
  color: var(--color-texto-terciario);
}

.barra-ia {
  display: flex;
  align-items: center;
  gap: var(--espacio-medio);
  border-top: 1px solid var(--color-borde-secundario);
  flex-wrap: wrap;
}

.etiqueta-ia {
  font-size: 0.78rem;
  font-weight: 700;
  color: var(--color-texto-secundario);
  white-space: nowrap;
}

.acciones-ia {
  display: flex;
  gap: var(--espacio-pequeno);
  flex-wrap: wrap;
}

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

.campo-archivo-oculto {
  display: none;
}

.icono-subida {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.texto-subida {
  font-size: 0.88rem;
  color: var(--color-texto-secundario);
}

.icono-archivo {
  font-size: 1.3rem;
}

.nombre-archivo {
  font-weight: 600;
  font-size: 0.9rem;
}
</style>
