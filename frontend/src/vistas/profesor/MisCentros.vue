<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { servicioCentros } from '@/servicios/api'
import { usarAlmacenAutenticacion } from '@/almacenes/autenticacion'

const enrutador = useRouter()
const almacenAutenticacion = usarAlmacenAutenticacion()

const idUsuario = almacenAutenticacion.usuarioActual?.id_usuario
const CLAVE_CENTROS = `playpdf-mis-centros-${idUsuario}`

const misCentros = ref([])
const mostrarModalCodigo = ref(false)
const codigoIntroducido = ref('')
const errorCodigo = ref('')
const buscandoCodigo = ref(false)

onMounted(() => {
  const guardados = JSON.parse(localStorage.getItem(CLAVE_CENTROS) || '[]')
  misCentros.value = guardados
})

async function unirseConCodigo() {
  errorCodigo.value = ''
  const codigo = codigoIntroducido.value.trim().toUpperCase()
  if (!codigo) return

  buscandoCodigo.value = true
  try {
    const respuesta = await servicioCentros.obtenerPorCodigo(codigo)
    const centro = respuesta.data
    const yaExiste = misCentros.value.some(c => c.id_centro === centro.id_centro)
    if (!yaExiste) {
      misCentros.value.push(centro)
      localStorage.setItem(CLAVE_CENTROS, JSON.stringify(misCentros.value))
    }
    mostrarModalCodigo.value = false
    codigoIntroducido.value = ''
  } catch {
    errorCodigo.value = 'Código incorrecto. Comprueba que el código sea el correcto.'
  } finally {
    buscandoCodigo.value = false
  }
}

function abandonarCentro(idCentro) {
  if (confirm('¿Quieres abandonar este centro? No se eliminan tus asignaturas.')) {
    misCentros.value = misCentros.value.filter(c => c.id_centro !== idCentro)
    localStorage.setItem(CLAVE_CENTROS, JSON.stringify(misCentros.value))
  }
}
</script>

<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
      <div>
        <h1 class="titulo-pagina">Mis centros</h1>
        <p class="subtitulo-pagina">Únete a un centro con su código para gestionar asignaturas</p>
      </div>
      <button class="btn btn-primary" @click="mostrarModalCodigo = true">🔑 Unirse con código</button>
    </div>

    <div v-if="misCentros.length === 0" class="estado-vacio">
      <div class="estado-vacio-icono">🏫</div>
      <p class="estado-vacio-titulo">Aún no perteneces a ningún centro</p>
      <p class="estado-vacio-texto">Introduce el código de acceso que te ha dado el administrador de tu centro</p>
      <button class="btn btn-primary mt-3" @click="mostrarModalCodigo = true">🔑 Introducir código</button>
    </div>

    <div v-else class="row g-3">
      <div v-for="centro in misCentros" :key="centro.id_centro" class="col-12 col-md-6">
        <div class="tarjeta tarjeta-interactiva animacion-aparecer-desde-abajo h-100">
          <div class="d-flex align-items-start gap-3">
            <div class="icono-centro-grande">🏫</div>
            <div class="flex-grow-1 min-w-0">
              <h3 class="fw-bold texto-nombre-grande mb-1">{{ centro.nombre }}</h3>
              <span class="etiqueta etiqueta-acento">{{ centro.ciudad }}</span>
              <p class="text-secondary small mt-1 mb-0">{{ centro.direccion }}</p>
            </div>
          </div>
          <div class="d-flex gap-2 mt-3">
            <button class="btn btn-primary btn-sm flex-grow-1"
              @click="enrutador.push({ name: 'AsignaturasDeCentro', params: { identificadorCentro: centro.id_centro } })">
              📚 Ver asignaturas
            </button>
            <button class="btn btn-outline-danger btn-sm" @click="abandonarCentro(centro.id_centro)">
              Abandonar
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="mostrarModalCodigo" class="superposicion-modal" @click.self="mostrarModalCodigo = false; errorCodigo = ''">
      <div class="contenido-modal animacion-escalar-entrada">
        <div class="cabecera-modal">
          <h2 class="titulo-modal">Unirse a un centro</h2>
          <button class="boton boton-fantasma boton-icono" @click="mostrarModalCodigo = false; errorCodigo = ''">✕</button>
        </div>
        <p class="text-secondary small mb-3">Introduce el código de acceso que te ha proporcionado el administrador del centro.</p>
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
              <span v-else>Unirse al centro</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.icono-centro-grande {
  font-size: 2rem;
  width: 52px;
  height: 52px;
  background: var(--color-acento-sutil);
  border-radius: var(--redondeo-medio);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
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
