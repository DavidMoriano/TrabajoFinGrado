<script setup>
import { ref, computed, onMounted } from 'vue'
import { servicioCentros } from '@/servicios/api'

const listaCentros = ref([])
const terminoBusqueda = ref('')
const mostrarVentanaModal = ref(false)
const estaCargando = ref(true)
const datosFormularioCentro = ref({ nombre: '', ciudad: '', direccion: '', latitud: '', longitud: '', codigoAcceso: '' })
const errorCrear = ref('')

onMounted(async () => {
  try {
    const respuesta = await servicioCentros.obtenerTodos()
    listaCentros.value = respuesta.data
  } catch {
    listaCentros.value = []
  } finally {
    estaCargando.value = false
  }
})

const centrosFiltrados = computed(() => {
  if (!terminoBusqueda.value) return listaCentros.value
  const b = terminoBusqueda.value.toLowerCase()
  return listaCentros.value.filter(c =>
    c.nombre.toLowerCase().includes(b) || c.ciudad.toLowerCase().includes(b)
  )
})

function abrirModalCreacion() {
  datosFormularioCentro.value = { nombre: '', ciudad: '', direccion: '', latitud: '', longitud: '', codigoAcceso: '' }
  errorCrear.value = ''
  mostrarVentanaModal.value = true
}

async function agregarCentro() {
  errorCrear.value = ''
  try {
    const respuesta = await servicioCentros.crear({
      nombre: datosFormularioCentro.value.nombre,
      ciudad: datosFormularioCentro.value.ciudad,
      direccion: datosFormularioCentro.value.direccion,
      latitud: parseFloat(datosFormularioCentro.value.latitud),
      longitud: parseFloat(datosFormularioCentro.value.longitud),
      codigo_acceso: datosFormularioCentro.value.codigoAcceso.toUpperCase(),
    })
    listaCentros.value.push(respuesta.data)
    mostrarVentanaModal.value = false
  } catch (error) {
    errorCrear.value = error.response?.data?.message || 'Error al crear el centro'
  }
}

async function eliminarCentro(identificador) {
  if (confirm('¿Eliminar este centro? Se eliminarán también sus asignaturas asociadas.')) {
    try {
      await servicioCentros.eliminar(identificador)
      listaCentros.value = listaCentros.value.filter(c => c.id_centro !== identificador)
    } catch (error) {
      alert(error.response?.data?.message || 'Error al eliminar el centro')
    }
  }
}
</script>

<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
      <div>
        <h1 class="titulo-pagina">Gestión de centros</h1>
        <p class="subtitulo-pagina">Crea centros con su código de acceso único para profesores y alumnos</p>
      </div>
      <div class="d-flex gap-2 align-items-center">
        <div class="barra-busqueda">
          <span class="icono-busqueda">🔍</span>
          <input v-model="terminoBusqueda" type="text" class="form-control campo-busqueda" placeholder="Buscar..." />
        </div>
        <button class="btn btn-primary" @click="abrirModalCreacion">+ Nuevo centro</button>
      </div>
    </div>

    <div v-if="estaCargando" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else-if="centrosFiltrados.length === 0" class="estado-vacio">
      <div class="estado-vacio-icono">🏫</div>
      <p class="estado-vacio-titulo">No hay centros</p>
      <p class="estado-vacio-texto">Crea el primer centro educativo de la plataforma</p>
    </div>

    <div v-else class="table-responsive border rounded-3 animacion-aparecer-desde-abajo">
      <table class="table table-hover mb-0">
        <thead>
          <tr>
            <th>Centro</th>
            <th>Ciudad</th>
            <th>Dirección</th>
            <th>Código de acceso</th>
            <th class="columna-acciones-estrecha">Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="centro in centrosFiltrados" :key="centro.id_centro">
            <td class="fw-semibold">{{ centro.nombre }}</td>
            <td><span class="etiqueta etiqueta-acento">{{ centro.ciudad }}</span></td>
            <td class="texto-secundario-pequeno">{{ centro.direccion }}</td>
            <td>
              <span class="badge-codigo">{{ centro.codigo_acceso }}</span>
            </td>
            <td class="text-center">
              <button class="boton boton-fantasma boton-pequeno texto-error"
                @click="eliminarCentro(centro.id_centro)">🗑️</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="mostrarVentanaModal" class="superposicion-modal" @click.self="mostrarVentanaModal = false">
      <div class="contenido-modal animacion-escalar-entrada">
        <div class="cabecera-modal">
          <h2 class="titulo-modal">Nuevo centro</h2>
          <button class="boton boton-fantasma boton-icono" @click="mostrarVentanaModal = false">✕</button>
        </div>
        <form @submit.prevent="agregarCentro" class="d-flex flex-column gap-3">
          <div class="grupo-campo">
            <label class="etiqueta-campo">Nombre del centro</label>
            <input v-model="datosFormularioCentro.nombre" required placeholder="IES ejemplo" />
          </div>
          <div class="grupo-campo">
            <label class="etiqueta-campo">Ciudad</label>
            <input v-model="datosFormularioCentro.ciudad" required placeholder="Salamanca" />
          </div>
          <div class="grupo-campo">
            <label class="etiqueta-campo">Dirección</label>
            <input v-model="datosFormularioCentro.direccion" required placeholder="Calle Mayor 1" />
          </div>
          <div class="row g-2">
            <div class="col-6 grupo-campo">
              <label class="etiqueta-campo">Latitud</label>
              <input v-model="datosFormularioCentro.latitud" type="number" step="any" required placeholder="40.9629" />
            </div>
            <div class="col-6 grupo-campo">
              <label class="etiqueta-campo">Longitud</label>
              <input v-model="datosFormularioCentro.longitud" type="number" step="any" required placeholder="-5.6631" />
            </div>
          </div>
          <div class="grupo-campo">
            <label class="etiqueta-campo">Código de acceso</label>
            <input v-model="datosFormularioCentro.codigoAcceso" required
              placeholder="Ej: IES2024"
              maxlength="20"
              style="text-transform:uppercase;letter-spacing:0.08em;font-weight:700" />
            <p class="texto-ayuda-campo">Código único que usarán profesores y alumnos para unirse al centro</p>
          </div>
          <div v-if="errorCrear" class="alerta-error">{{ errorCrear }}</div>
          <div class="acciones-modal">
            <button type="button" class="boton boton-secundario" @click="mostrarVentanaModal = false">Cancelar</button>
            <button type="submit" class="boton boton-principal">Crear centro</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.badge-codigo {
  font-family: monospace;
  font-size: 0.85rem;
  font-weight: 800;
  letter-spacing: 0.1em;
  background: var(--color-acento-sutil);
  color: var(--color-acento);
  padding: 3px 10px;
  border-radius: 6px;
  border: 1px solid var(--color-acento);
}
.texto-ayuda-campo {
  font-size: 0.75rem;
  color: var(--color-texto-terciario);
  margin-top: 4px;
  margin-bottom: 0;
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
