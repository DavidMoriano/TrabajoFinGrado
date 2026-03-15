<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usarAlmacenAutenticacion } from '@/almacenes/autenticacion'
import { usarTema } from '@/composables/usarTema'

const rutaActual = useRoute()
const enrutador = useRouter()
const almacenAutenticacion = usarAlmacenAutenticacion()
const { alternarTema, esModoOscuro } = usarTema()

const enlacesAdministrador = [
  { nombreRuta: 'PanelAdministrador', etiqueta: 'Panel principal', icono: '◈' },
  { nombreRuta: 'GestionCentros', etiqueta: 'Centros', icono: '🏫' },
  { nombreRuta: 'GestionAsignaturas', etiqueta: 'Asignaturas', icono: '📚' },
  { nombreRuta: 'ListaProfesores', etiqueta: 'Profesores', icono: '👨‍🏫' },
  { nombreRuta: 'ListaAlumnos', etiqueta: 'Alumnos', icono: '🎓' },
  { nombreRuta: 'VerCentros', etiqueta: 'Ver centros', icono: '🌐' }
]

const enlacesProfesor = [
  { nombreRuta: 'PanelProfesor', etiqueta: 'Panel principal', icono: '◈' },
  { nombreRuta: 'MisAsignaturas', etiqueta: 'Mis asignaturas', icono: '📘' },
  { nombreRuta: 'VerCentros', etiqueta: 'Ver centros', icono: '🌐' }
]

const enlacesAlumno = [
  { nombreRuta: 'PanelAlumno', etiqueta: 'Panel principal', icono: '◈' },
  { nombreRuta: 'SeleccionJuegos', etiqueta: 'Jugar', icono: '🎮' },
  { nombreRuta: 'VistaEstadisticas', etiqueta: 'Estadísticas', icono: '📊' },
  { nombreRuta: 'MapaCentrosEducativos', etiqueta: 'Mapa centros', icono: '🗺️' },
  { nombreRuta: 'VerCentros', etiqueta: 'Ver centros', icono: '🌐' },
  { nombreRuta: 'ConfiguracionAccesibilidad', etiqueta: 'Accesibilidad', icono: '⚙️' }
]

const enlacesNavegacion = computed(() => {
  if (almacenAutenticacion.esAdministrador) return enlacesAdministrador
  if (almacenAutenticacion.esProfesor) return enlacesProfesor
  if (almacenAutenticacion.esAlumno) return enlacesAlumno
  return []
})

const etiquetaRol = computed(() => {
  if (almacenAutenticacion.esAdministrador) return 'Administrador'
  if (almacenAutenticacion.esProfesor) return 'Profesor'
  return 'Alumno'
})

function manejarCierreSesion() {
  almacenAutenticacion.cerrarSesion()
  enrutador.push('/inicio-sesion')
}
</script>

<template>
  <aside class="barra-lateral">
    <div class="seccion-logotipo">
      <div class="icono-logotipo-contenedor">
        <span class="icono-logotipo">▶</span>
      </div>
      <div class="texto-logotipo">
        <span class="nombre-aplicacion">PlayPDF</span>
        <span class="etiqueta-rol-usuario">{{ etiquetaRol }}</span>
      </div>
    </div>

    <nav class="navegacion-lateral">
      <router-link v-for="enlace in enlacesNavegacion" :key="enlace.nombreRuta" :to="{ name: enlace.nombreRuta }"
        class="enlace-navegacion" :class="{ 'enlace-activo': rutaActual.name === enlace.nombreRuta }">
        <span class="icono-enlace">{{ enlace.icono }}</span>
        <span class="texto-enlace">{{ enlace.etiqueta }}</span>
      </router-link>
    </nav>

    <div class="pie-barra-lateral">
      <button class="boton-cambiar-tema" @click="alternarTema"
        :title="esModoOscuro() ? 'Cambiar a modo claro' : 'Cambiar a modo oscuro'">
        <span class="icono-tema">{{ esModoOscuro() ? '☀️' : '🌙' }}</span>
        <span class="texto-tema">{{ esModoOscuro() ? 'Modo claro' : 'Modo oscuro' }}</span>
      </button>

      <div class="seccion-usuario">
        <div class="avatar-usuario">
          {{ almacenAutenticacion.usuarioActual?.nombre?.charAt(0) || '?' }}
        </div>
        <div class="informacion-usuario">
          <span class="nombre-usuario">{{ almacenAutenticacion.nombreCompletoUsuario }}</span>
          <span class="email-usuario">{{ almacenAutenticacion.usuarioActual?.email }}</span>
        </div>
        <button class="boton-cerrar-sesion" @click="manejarCierreSesion" title="Cerrar sesión">
          ⏻
        </button>
      </div>
    </div>
  </aside>
</template>

<style scoped>
.barra-lateral {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: var(--ancho-barra-lateral);
  background: var(--color-fondo-barra-lateral);
  border-right: 1px solid var(--color-borde-principal);
  display: flex;
  flex-direction: column;
  z-index: 100;
  transition:
    background-color var(--transicion-tema),
    border-color var(--transicion-tema);
}

.seccion-logotipo {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: var(--espacio-grande);
  border-bottom: 1px solid var(--color-borde-secundario);
}

.icono-logotipo-contenedor {
  width: 38px;
  height: 38px;
  background: var(--gradiente-acento);
  border-radius: var(--redondeo-medio);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icono-logotipo {
  color: white;
  font-size: 1.1rem;
}

.nombre-aplicacion {
  font-family: var(--fuente-titulos);
  font-weight: 800;
  font-size: 1.15rem;
  letter-spacing: -0.03em;
  background: var(--gradiente-acento);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.etiqueta-rol-usuario {
  display: block;
  font-size: 0.7rem;
  color: var(--color-texto-terciario);
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-weight: 600;
}

.navegacion-lateral {
  flex: 1;
  padding: var(--espacio-medio) var(--espacio-pequeno);
  display: flex;
  flex-direction: column;
  gap: 2px;
  overflow-y: auto;
}

.enlace-navegacion {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.65rem 0.85rem;
  border-radius: var(--redondeo-medio);
  color: var(--color-texto-secundario);
  font-size: 0.9rem;
  font-weight: 500;
  text-decoration: none;
  transition: all var(--transicion-rapida);
}

.enlace-navegacion:hover {
  background: var(--color-fondo-hover);
  color: var(--color-texto-principal);
}

.enlace-activo {
  background: var(--color-acento-sutil);
  color: var(--color-acento);
  font-weight: 600;
}

.icono-enlace {
  font-size: 1.15rem;
  width: 24px;
  text-align: center;
  flex-shrink: 0;
}

.pie-barra-lateral {
  border-top: 1px solid var(--color-borde-secundario);
  padding: var(--espacio-pequeno);
}

.boton-cambiar-tema {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  width: 100%;
  padding: 0.6rem 0.85rem;
  border-radius: var(--redondeo-medio);
  background: transparent;
  border: none;
  color: var(--color-texto-secundario);
  font-family: var(--fuente-cuerpo);
  font-size: 0.85rem;
  cursor: pointer;
  transition: all var(--transicion-rapida);
}

.boton-cambiar-tema:hover {
  background: var(--color-fondo-hover);
  color: var(--color-texto-principal);
}

.icono-tema {
  font-size: 1.1rem;
  width: 24px;
  text-align: center;
}

.seccion-usuario {
  display: flex;
  align-items: center;
  gap: 0.65rem;
  padding: 0.6rem 0.85rem;
  border-radius: var(--redondeo-medio);
  margin-top: 2px;
}

.avatar-usuario {
  width: 32px;
  height: 32px;
  border-radius: var(--redondeo-completo);
  background: var(--gradiente-acento);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 0.85rem;
  flex-shrink: 0;
}

.informacion-usuario {
  flex: 1;
  min-width: 0;
}

.nombre-usuario {
  display: block;
  font-size: 0.82rem;
  font-weight: 600;
  color: var(--color-texto-principal);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.email-usuario {
  display: block;
  font-size: 0.72rem;
  color: var(--color-texto-terciario);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.boton-cerrar-sesion {
  width: 30px;
  height: 30px;
  border-radius: var(--redondeo-pequeno);
  background: transparent;
  border: none;
  color: var(--color-texto-terciario);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
  transition: all var(--transicion-rapida);
  flex-shrink: 0;
}

.boton-cerrar-sesion:hover {
  background: var(--color-error-claro);
  color: var(--color-error);
}

@media (max-width: 768px) {
  .barra-lateral {
    transform: translateX(-100%);
    transition: transform var(--transicion-normal);
  }

  .barra-lateral.barra-lateral-visible-movil {
    transform: translateX(0);
  }
}
</style>
