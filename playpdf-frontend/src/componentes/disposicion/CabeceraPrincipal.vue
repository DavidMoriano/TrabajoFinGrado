<script setup>
import { useRoute } from 'vue-router'
import { computed } from 'vue'

const emit = defineEmits(['alternar-barra-lateral'])

const rutaActual = useRoute()

const tituloPagina = computed(() => {
  const diccionarioTitulos = {
    PanelAdministrador: 'Panel de administración',
    GestionCentros: 'Gestión de centros',
    GestionAsignaturas: 'Gestión de asignaturas',
    ListaProfesores: 'Profesores',
    ListaAlumnos: 'Alumnos',
    PanelProfesor: 'Mi panel',
    MisAsignaturas: 'Mis asignaturas',
    SubirTemario: 'Subir temario',
    PanelAlumno: 'Mi panel',
    SeleccionJuegos: 'Minijuegos educativos',
    InterfazJuego: 'Jugando',
    VistaEstadisticas: 'Estadísticas de uso',
    MapaCentrosEducativos: 'Localización de centros',
    VerCentros: 'Centros educativos',
    ConfiguracionAccesibilidad: 'Accesibilidad y colores'
  }
  return diccionarioTitulos[rutaActual.name] || 'PlayPDF'
})
</script>

<template>
  <header class="cabecera-aplicacion">
    <button class="boton-menu-movil" @click="emit('alternar-barra-lateral')">
      <i class="bi bi-list"></i>
    </button>
    <h1 class="titulo-cabecera">{{ tituloPagina }}</h1>
  </header>
</template>

<style scoped>
.cabecera-aplicacion {
  position: fixed;
  top: 0;
  right: 0;
  left: var(--ancho-barra-lateral);
  height: var(--alto-cabecera);
  background: var(--cristal-fondo);
  backdrop-filter: var(--cristal-desenfoque);
  -webkit-backdrop-filter: var(--cristal-desenfoque);
  border-bottom: 1px solid var(--color-borde-secundario);
  display: flex;
  align-items: center;
  gap: var(--espacio-medio);
  padding: 0 var(--espacio-enorme);
  z-index: 90;
  transition:
    background-color var(--transicion-tema),
    border-color var(--transicion-tema),
    left var(--transicion-normal);
}

.titulo-cabecera {
  font-family: var(--fuente-titulos);
  font-size: 1.15rem;
  font-weight: 700;
  color: var(--color-texto-principal);
  letter-spacing: -0.01em;
}

.boton-menu-movil {
  display: none;
  width: 40px;
  height: 40px;
  border-radius: var(--redondeo-medio);
  background: transparent;
  border: 1px solid var(--color-borde-principal);
  color: var(--color-texto-principal);
  font-size: 1.3rem;
  cursor: pointer;
  align-items: center;
  justify-content: center;
  transition: all var(--transicion-rapida);
  flex-shrink: 0;
}

.boton-menu-movil:hover {
  background: var(--color-fondo-hover);
}

@media (max-width: 768px) {
  .cabecera-aplicacion {
    left: 0;
    padding: 0 var(--espacio-medio);
  }

  .boton-menu-movil {
    display: flex;
  }

  .titulo-cabecera {
    font-size: 1rem;
  }
}
</style>
