import axios from "axios";

const clienteHttp = axios.create({
  baseURL: "/api",
  headers: {
    "Content-Type": "application/json",
  },
});

clienteHttp.interceptors.request.use((configuracion) => {
  const tokenSesion = localStorage.getItem("playpdf-token");
  if (tokenSesion) {
    configuracion.headers.Authorization = `Bearer ${tokenSesion}`;
  }
  return configuracion;
});

clienteHttp.interceptors.response.use(
  (respuesta) => respuesta,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("playpdf-token");
      localStorage.removeItem("playpdf-usuario");
      window.location.href = "/inicio-sesion";
    }
    return Promise.reject(error);
  },
);

export default clienteHttp;

export const servicioAutenticacion = {
  iniciarSesion(credenciales) {
    return clienteHttp.post("/auth/login", credenciales);
  },
  registrarUsuario(datosUsuario) {
    return clienteHttp.post("/auth/register", datosUsuario);
  },
  cerrarSesion() {
    localStorage.removeItem("playpdf-token");
    localStorage.removeItem("playpdf-usuario");
  },
};

export const servicioCentros = {
  obtenerTodos() {
    return clienteHttp.get("/centros");
  },
  obtenerPorId(identificador) {
    return clienteHttp.get(`/centros/${identificador}`);
  },
  crear(datosCentro) {
    return clienteHttp.post("/centros", datosCentro);
  },
  eliminar(identificador) {
    return clienteHttp.delete(`/centros/${identificador}`);
  },
};

export const servicioAsignaturas = {
  obtenerTodas() {
    return clienteHttp.get("/asignaturas");
  },
  obtenerPorId(identificador) {
    return clienteHttp.get(`/asignaturas/${identificador}`);
  },
  obtenerPorProfesor(identificadorProfesor) {
    return clienteHttp.get(`/asignaturas/profesor/${identificadorProfesor}`);
  },
  crear(datosAsignatura) {
    return clienteHttp.post("/asignaturas", datosAsignatura);
  },
  actualizar(identificador, datosAsignatura) {
    return clienteHttp.put(`/asignaturas/${identificador}`, datosAsignatura);
  },
  eliminar(identificador) {
    return clienteHttp.delete(`/asignaturas/${identificador}`);
  },
};

export const servicioTemas = {
  obtenerPorAsignatura(identificadorAsignatura) {
    return clienteHttp.get(`/temas/asignatura/${identificadorAsignatura}`);
  },
  obtenerPorId(identificador) {
    return clienteHttp.get(`/temas/${identificador}`);
  },
  crear(datosFormulario) {
    return clienteHttp.post("/temas", datosFormulario, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },
  actualizar(identificador, datosFormulario) {
    return clienteHttp.put(`/temas/${identificador}`, datosFormulario, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },
  eliminar(identificador) {
    return clienteHttp.delete(`/temas/${identificador}`);
  },
};

export const servicioUsuarios = {
  obtenerProfesores() {
    return clienteHttp.get("/usuarios/profesores");
  },
  obtenerAlumnos() {
    return clienteHttp.get("/usuarios/alumnos");
  },
  obtenerPorId(identificador) {
    return clienteHttp.get(`/usuarios/${identificador}`);
  },
};

export const servicioJuegos = {
  obtenerTodos() {
    return clienteHttp.get("/juegos");
  },
  obtenerPorId(identificador) {
    return clienteHttp.get(`/juegos/${identificador}`);
  },
  obtenerPreguntas(identificadorTema, identificadorJuego) {
    return clienteHttp.get(
      `/juegos/${identificadorJuego}/preguntas?tema=${identificadorTema}`,
    );
  },
  generarPreguntasConInteligenciaArtificial(identificadorTema) {
    return clienteHttp.post("/juegos/generar-preguntas", {
      id_tema: identificadorTema,
    });
  },
  borrarPreguntas(identificadorTema) {
    return clienteHttp.delete(`/juegos/preguntas?tema=${identificadorTema}`);
  },
  regenerarPreguntas(identificadorTema) {
    return clienteHttp.post("/juegos/regenerar-preguntas", {
      id_tema: identificadorTema,
    });
  },
};

export const servicioEstadisticas = {
  obtenerMisEstadisticas() {
    return clienteHttp.get("/estadisticas/me");
  },
  registrarPartida(identificadorAsignatura, aciertos, totalPreguntas) {
    return clienteHttp.post("/estadisticas", {
      id_asignatura: identificadorAsignatura,
      aciertos: aciertos,
      total_preguntas: totalPreguntas,
    });
  },
  obtenerEstadisticasGlobales() {
    return clienteHttp.get("/estadisticas/globales");
  },
};
