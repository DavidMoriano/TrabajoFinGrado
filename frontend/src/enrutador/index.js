import { createRouter, createWebHistory } from "vue-router";

import VistaInicioSesion from "@/vistas/autenticacion/VistaInicioSesion.vue";
import VistaRegistro from "@/vistas/autenticacion/VistaRegistro.vue";

import VistaCentros from "@/vistas/publico/VistaCentros.vue";

import PanelAdministrador from "@/vistas/administrador/PanelAdministrador.vue";
import GestionCentros from "@/vistas/administrador/GestionCentros.vue";
import GestionAsignaturas from "@/vistas/administrador/GestionAsignaturas.vue";
import ListaProfesores from "@/vistas/administrador/ListaProfesores.vue";
import ListaAlumnos from "@/vistas/administrador/ListaAlumnos.vue";

import PanelProfesor from "@/vistas/profesor/PanelProfesor.vue";
import MisCentros from "@/vistas/profesor/MisCentros.vue";
import AsignaturasDeCentro from "@/vistas/profesor/AsignaturasDeCentro.vue";
import SubirTemario from "@/vistas/profesor/SubirTemario.vue";

import PanelAlumno from "@/vistas/alumno/PanelAlumno.vue";
import SeleccionJuegos from "@/vistas/alumno/SeleccionJuegos.vue";
import InterfazJuego from "@/vistas/alumno/InterfazJuego.vue";
import VistaEstadisticas from "@/vistas/alumno/VistaEstadisticas.vue";
import MapaCentrosEducativos from "@/vistas/alumno/MapaCentrosEducativos.vue";
import ConfiguracionAccesibilidad from "@/vistas/alumno/ConfiguracionAccesibilidad.vue";

const rutas = [
  {
    path: "/inicio-sesion",
    name: "InicioSesion",
    component: VistaInicioSesion,
    meta: { soloInvitados: true },
  },
  {
    path: "/registro",
    name: "Registro",
    component: VistaRegistro,
    meta: { soloInvitados: true },
  },

  {
    path: "/centros",
    name: "VerCentros",
    component: VistaCentros,
    meta: { requiereAutenticacion: true },
  },

  {
    path: "/administrador",
    name: "PanelAdministrador",
    component: PanelAdministrador,
    meta: { requiereAutenticacion: true, rolRequerido: "ADMIN" },
  },
  {
    path: "/administrador/centros",
    name: "GestionCentros",
    component: GestionCentros,
    meta: { requiereAutenticacion: true, rolRequerido: "ADMIN" },
  },
  {
    path: "/administrador/asignaturas",
    name: "GestionAsignaturas",
    component: GestionAsignaturas,
    meta: { requiereAutenticacion: true, rolRequerido: "ADMIN" },
  },
  {
    path: "/administrador/profesores",
    name: "ListaProfesores",
    component: ListaProfesores,
    meta: { requiereAutenticacion: true, rolRequerido: "ADMIN" },
  },
  {
    path: "/administrador/alumnos",
    name: "ListaAlumnos",
    component: ListaAlumnos,
    meta: { requiereAutenticacion: true, rolRequerido: "ADMIN" },
  },

  {
    path: "/profesor",
    name: "PanelProfesor",
    component: PanelProfesor,
    meta: { requiereAutenticacion: true, rolRequerido: "PROFESOR" },
  },
  {
    path: "/profesor/centros",
    name: "MisCentros",
    component: MisCentros,
    meta: { requiereAutenticacion: true, rolRequerido: "PROFESOR" },
  },
  {
    path: "/profesor/centros/:identificadorCentro/asignaturas",
    name: "AsignaturasDeCentro",
    component: AsignaturasDeCentro,
    meta: { requiereAutenticacion: true, rolRequerido: "PROFESOR" },
  },
  {
    path: "/profesor/asignaturas",
    redirect: "/profesor/centros",
  },
  {
    path: "/profesor/temario/:identificadorAsignatura",
    name: "SubirTemario",
    component: SubirTemario,
    meta: { requiereAutenticacion: true, rolRequerido: "PROFESOR" },
  },

  {
    path: "/alumno",
    name: "PanelAlumno",
    component: PanelAlumno,
    meta: { requiereAutenticacion: true, rolRequerido: "ALUMNO" },
  },
  {
    path: "/alumno/juegos",
    name: "SeleccionJuegos",
    component: SeleccionJuegos,
    meta: { requiereAutenticacion: true, rolRequerido: "ALUMNO" },
  },
  {
    path: "/alumno/jugar/:identificadorTema/:tipoJuego",
    name: "InterfazJuego",
    component: InterfazJuego,
    meta: { requiereAutenticacion: true, rolRequerido: "ALUMNO" },
  },
  {
    path: "/alumno/estadisticas",
    name: "VistaEstadisticas",
    component: VistaEstadisticas,
    meta: { requiereAutenticacion: true, rolRequerido: "ALUMNO" },
  },
  {
    path: "/alumno/mapa-centros",
    name: "MapaCentrosEducativos",
    component: MapaCentrosEducativos,
    meta: { requiereAutenticacion: true, rolRequerido: "ALUMNO" },
  },
  {
    path: "/alumno/accesibilidad",
    name: "ConfiguracionAccesibilidad",
    component: ConfiguracionAccesibilidad,
    meta: { requiereAutenticacion: true, rolRequerido: "ALUMNO" },
  },

  {
    path: "/",
    redirect: () => {
      const datosUsuario = JSON.parse(
        localStorage.getItem("playpdf-usuario") || "null",
      );
      if (!datosUsuario) return "/inicio-sesion";
      switch (datosUsuario.rol) {
        case "ADMIN":
          return "/administrador";
        case "PROFESOR":
          return "/profesor";
        case "ALUMNO":
          return "/alumno";
        default:
          return "/inicio-sesion";
      }
    },
  },

  {
    path: "/:rutaNoEncontrada(.*)*",
    redirect: "/inicio-sesion",
  },
];

const enrutador = createRouter({
  history: createWebHistory(),
  routes: rutas,
});

enrutador.beforeEach((rutaDestino, rutaOrigen, siguiente) => {
  const tokenSesion = localStorage.getItem("playpdf-token");
  const datosUsuario = JSON.parse(
    localStorage.getItem("playpdf-usuario") || "null",
  );

  if (rutaDestino.meta.requiereAutenticacion && !tokenSesion) {
    if (rutaDestino.path !== "/inicio-sesion") {
      return siguiente("/inicio-sesion");
    }
  }

  if (rutaDestino.meta.soloInvitados && tokenSesion) {
    const rutaInicio = datosUsuario?.rol === "ADMIN" ? "/administrador" :
                       datosUsuario?.rol === "PROFESOR" ? "/profesor" :
                       datosUsuario?.rol === "ALUMNO" ? "/alumno" : "/inicio-sesion";
    if (rutaDestino.path !== rutaInicio) {
      return siguiente(rutaInicio);
    }
  }

  if (
    rutaDestino.meta.rolRequerido &&
    datosUsuario?.rol !== rutaDestino.meta.rolRequerido
  ) {
    const rutaInicio = datosUsuario?.rol === "ADMIN" ? "/administrador" :
                       datosUsuario?.rol === "PROFESOR" ? "/profesor" :
                       datosUsuario?.rol === "ALUMNO" ? "/alumno" : "/inicio-sesion";
    if (rutaDestino.path !== rutaInicio) {
      return siguiente(rutaInicio);
    }
  }

  siguiente();
});

export default enrutador;
