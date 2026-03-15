import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { servicioAutenticacion } from "@/servicios/api";

export const usarAlmacenAutenticacion = defineStore("autenticacion", () => {
  const usuarioActual = ref(
    JSON.parse(localStorage.getItem("playpdf-usuario") || "null"),
  );
  const tokenSesion = ref(localStorage.getItem("playpdf-token") || null);
  const estaCargando = ref(false);
  const mensajeError = ref(null);

  const estaAutenticado = computed(() => !!tokenSesion.value);
  const esAdministrador = computed(
    () => usuarioActual.value?.rol === "administrador",
  );
  const esProfesor = computed(() => usuarioActual.value?.rol === "profesor");
  const esAlumno = computed(() => usuarioActual.value?.rol === "alumno");
  const rolUsuario = computed(() => usuarioActual.value?.rol || null);
  const nombreCompletoUsuario = computed(() => {
    if (!usuarioActual.value) return "";
    return `${usuarioActual.value.nombre} ${usuarioActual.value.apellidos}`;
  });

  async function iniciarSesion(credenciales) {
    estaCargando.value = true;
    mensajeError.value = null;
    try {
      const respuesta = await servicioAutenticacion.iniciarSesion(credenciales);
      tokenSesion.value = respuesta.data.token;
      usuarioActual.value = respuesta.data.usuario;
      localStorage.setItem("playpdf-token", tokenSesion.value);
      localStorage.setItem(
        "playpdf-usuario",
        JSON.stringify(usuarioActual.value),
      );
      return respuesta.data;
    } catch (error) {
      mensajeError.value =
        error.response?.data?.message || "Error al iniciar sesión";
      throw error;
    } finally {
      estaCargando.value = false;
    }
  }

  async function registrarUsuario(datosUsuario) {
    estaCargando.value = true;
    mensajeError.value = null;
    try {
      const respuesta =
        await servicioAutenticacion.registrarUsuario(datosUsuario);
      tokenSesion.value = respuesta.data.token;
      usuarioActual.value = respuesta.data.usuario;
      localStorage.setItem("playpdf-token", tokenSesion.value);
      localStorage.setItem(
        "playpdf-usuario",
        JSON.stringify(usuarioActual.value),
      );
      return respuesta.data;
    } catch (error) {
      mensajeError.value =
        error.response?.data?.message || "Error al registrarse";
      throw error;
    } finally {
      estaCargando.value = false;
    }
  }

  function cerrarSesion() {
    servicioAutenticacion.cerrarSesion();
    tokenSesion.value = null;
    usuarioActual.value = null;
  }

  return {
    usuarioActual,
    tokenSesion,
    estaCargando,
    mensajeError,
    estaAutenticado,
    esAdministrador,
    esProfesor,
    esAlumno,
    rolUsuario,
    nombreCompletoUsuario,
    iniciarSesion,
    registrarUsuario,
    cerrarSesion,
  };
});
