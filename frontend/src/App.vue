<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { usarAlmacenAutenticacion } from '@/almacenes/autenticacion'
import BarraLateral from '@/componentes/disposicion/BarraLateral.vue'
import CabeceraPrincipal from '@/componentes/disposicion/CabeceraPrincipal.vue'
import { usarTema } from '@/composables/usarTema'

const rutaActual = useRoute()
const almacenAutenticacion = usarAlmacenAutenticacion()
const { temaActual } = usarTema()

const barraLateralAbiertaEnMovil = ref(false)

const esPaginaAutenticacion = computed(() => {
  return ['InicioSesion', 'Registro'].includes(rutaActual.name)
})

const mostrarDisposicionCompleta = computed(() => {
  return almacenAutenticacion.estaAutenticado && !esPaginaAutenticacion.value
})

function alternarBarraLateral() {
  barraLateralAbiertaEnMovil.value = !barraLateralAbiertaEnMovil.value
}

function cerrarBarraLateral() {
  barraLateralAbiertaEnMovil.value = false
}

watch(() => rutaActual.path, () => {
  barraLateralAbiertaEnMovil.value = false
})
</script>

<template>
  <div class="raiz-aplicacion">
    <template v-if="!mostrarDisposicionCompleta">
      <router-view v-slot="{ Component }">
        <transition name="transicion-pagina" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </template>

    <template v-else>
      <div v-if="barraLateralAbiertaEnMovil" class="fondo-oscuro-movil" @click="cerrarBarraLateral"></div>

      <BarraLateral :class="{ 'barra-lateral-visible-movil': barraLateralAbiertaEnMovil }" />

      <div class="contenido-principal-aplicacion">
        <CabeceraPrincipal @alternar-barra-lateral="alternarBarraLateral" />
        <main class="area-contenido">
          <router-view v-slot="{ Component }">
            <transition name="transicion-pagina" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </main>
      </div>
    </template>
  </div>
</template>

<style scoped>
.raiz-aplicacion {
  display: flex;
  min-height: 100vh;
}

.contenido-principal-aplicacion {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-left: var(--ancho-barra-lateral);
  transition: margin-left var(--transicion-normal);
}

.area-contenido {
  flex: 1;
  padding: var(--espacio-extra-grande) var(--espacio-enorme);
  padding-top: calc(var(--alto-cabecera) + var(--espacio-extra-grande));
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
}

.fondo-oscuro-movil {
  display: none;
}

@media (max-width: 768px) {
  .contenido-principal-aplicacion {
    margin-left: 0;
  }

  .area-contenido {
    padding: var(--espacio-grande) var(--espacio-medio);
    padding-top: calc(var(--alto-cabecera) + var(--espacio-grande));
  }

  .fondo-oscuro-movil {
    display: block;
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.5);
    z-index: 99;
  }
}
</style>
