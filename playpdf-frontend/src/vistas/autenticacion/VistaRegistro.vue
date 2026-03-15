<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { usarAlmacenAutenticacion } from '@/almacenes/autenticacion'
import { usarTema } from '@/composables/usarTema'

const enrutador = useRouter()
const almacenAutenticacion = usarAlmacenAutenticacion()
const { alternarTema, esModoOscuro } = usarTema()

const datosFormulario = ref({
  nombre: '',
  apellidos: '',
  email: '',
  contrasena: '',
  confirmarContrasena: '',
  edad: '',
  estudios: '',
  rol: 'alumno',
  codigo_acceso: ''
})

const necesitaCodigoAcceso = computed(() => {
  return datosFormulario.value.rol === 'profesor' ||
         datosFormulario.value.rol === 'administrador'
})

async function manejarRegistro() {
  if (datosFormulario.value.contrasena !== datosFormulario.value.confirmarContrasena) {
    almacenAutenticacion.mensajeError = 'Las contraseñas no coinciden'
    return
  }

  try {
    const rolBackend = {
      'alumno':        'ALUMNO',
      'profesor':      'PROFESOR',
      'administrador': 'ADMIN'
    }

    const datosEnvio = {
      nombre:        datosFormulario.value.nombre,
      apellidos:     datosFormulario.value.apellidos,
      email:         datosFormulario.value.email,
      contrasena:    datosFormulario.value.contrasena,
      edad:          parseInt(datosFormulario.value.edad),
      estudios:      datosFormulario.value.estudios,
      rol:           rolBackend[datosFormulario.value.rol],
      codigo_acceso: datosFormulario.value.codigo_acceso
    }

    await almacenAutenticacion.registrarUsuario(datosEnvio)
    enrutador.push('/')
  } catch (errorPeticion) { }
}
</script>

<template>
  <div class="pagina-autenticacion">
    <div class="fondo-decorativo">
      <div class="esfera-decorativa esfera-primera"></div>
      <div class="esfera-decorativa esfera-segunda"></div>
    </div>
    <button class="boton-tema-autenticacion" @click="alternarTema">{{ esModoOscuro() ? '☀️' : '🌙' }}</button>

    <div class="contenedor-autenticacion animacion-aparecer-desde-abajo" style="max-width:460px;">
      <div class="cabecera-autenticacion">
        <div class="logotipo-autenticacion">
          <div class="icono-logotipo-autenticacion">▶</div>
          <h1 class="texto-logotipo-autenticacion">PlayPDF</h1>
        </div>
        <p class="subtitulo-autenticacion">Crea tu cuenta</p>
      </div>

      <form @submit.prevent="manejarRegistro" class="formulario-autenticacion">
        <div class="row g-3">
          <div class="col-12 col-md-6">
            <div class="grupo-campo">
              <label class="etiqueta-campo">Nombre</label>
              <input v-model="datosFormulario.nombre" type="text" class="form-control"
                placeholder="Pablo" required />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="grupo-campo">
              <label class="etiqueta-campo">Apellidos</label>
              <input v-model="datosFormulario.apellidos" type="text" class="form-control"
                placeholder="García López" required />
            </div>
          </div>
        </div>

        <div class="grupo-campo">
          <label class="etiqueta-campo">Email</label>
          <input v-model="datosFormulario.email" type="email" class="form-control"
            placeholder="tu@email.com" required />
        </div>

        <div class="row g-3">
          <div class="col-12 col-md-6">
            <div class="grupo-campo">
              <label class="etiqueta-campo">Contraseña</label>
              <input v-model="datosFormulario.contrasena" type="password" class="form-control"
                placeholder="••••••••" required />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="grupo-campo">
              <label class="etiqueta-campo">Confirmar</label>
              <input v-model="datosFormulario.confirmarContrasena" type="password" class="form-control"
                placeholder="••••••••" required />
            </div>
          </div>
        </div>

        <div class="row g-3">
          <div class="col-12 col-md-6">
            <div class="grupo-campo">
              <label class="etiqueta-campo">Edad</label>
              <input v-model="datosFormulario.edad" type="number" class="form-control"
                min="10" max="99" placeholder="18" required />
            </div>
          </div>
          <div class="col-12 col-md-6">
            <div class="grupo-campo">
              <label class="etiqueta-campo">Estudios</label>
              <input v-model="datosFormulario.estudios" type="text" class="form-control"
                placeholder="DAW, Bachillerato..." required />
            </div>
          </div>
        </div>

        <div class="grupo-campo">
          <label class="etiqueta-campo">Tipo de usuario</label>
          <div class="selector-rol">
            <label class="opcion-rol" :class="{ 'opcion-seleccionada': datosFormulario.rol === 'alumno' }">
              <input type="radio" v-model="datosFormulario.rol" value="alumno" />
              <span class="icono-rol">🎓</span>
              <span>Alumno</span>
            </label>
            <label class="opcion-rol" :class="{ 'opcion-seleccionada': datosFormulario.rol === 'profesor' }">
              <input type="radio" v-model="datosFormulario.rol" value="profesor" />
              <span class="icono-rol">👨‍🏫</span>
              <span>Profesor</span>
            </label>
            <label class="opcion-rol" :class="{ 'opcion-seleccionada': datosFormulario.rol === 'administrador' }">
              <input type="radio" v-model="datosFormulario.rol" value="administrador" />
              <span class="icono-rol">🛡️</span>
              <span>Admin</span>
            </label>
          </div>
        </div>

        <div class="grupo-campo" v-if="necesitaCodigoAcceso">
          <label class="etiqueta-campo">Código de acceso</label>
          <input v-model="datosFormulario.codigo_acceso" type="text"
            placeholder="Introduce el código proporcionado" required />
          <small style="color:var(--color-texto-terciario);font-size:0.78rem;">
            Necesitas un código especial para registrarte como {{ datosFormulario.rol }}.
          </small>
        </div>

        <p v-if="almacenAutenticacion.mensajeError" class="mensaje-error-campo" style="text-align:center;">
          {{ almacenAutenticacion.mensajeError }}
        </p>

        <button type="submit" class="boton boton-principal boton-grande boton-enviar-autenticacion"
          :disabled="almacenAutenticacion.estaCargando">
          <span v-if="almacenAutenticacion.estaCargando" class="indicador-carga"
            style="width:18px;height:18px;border-width:2px;"></span>
          <span v-else>Crear cuenta</span>
        </button>
      </form>

      <p class="texto-pie-autenticacion">
        ¿Ya tienes cuenta?
        <router-link to="/inicio-sesion" class="enlace-autenticacion">Inicia sesión</router-link>
      </p>
    </div>
  </div>
</template>

<style scoped>
.pagina-autenticacion {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: var(--color-fondo-principal);
  padding: var(--espacio-extra-grande) 0;
}

.fondo-decorativo {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.esfera-decorativa {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
}

.esfera-primera {
  width: 500px;
  height: 500px;
  background: var(--color-acento);
  top: -150px;
  right: -100px;
  opacity: 0.12;
}

.esfera-segunda {
  width: 400px;
  height: 400px;
  background: #a855f7;
  bottom: -100px;
  left: -100px;
  opacity: 0.1;
}

.boton-tema-autenticacion {
  position: fixed;
  top: var(--espacio-grande);
  right: var(--espacio-grande);
  width: 44px;
  height: 44px;
  border-radius: var(--redondeo-completo);
  background: var(--color-fondo-elevado);
  border: 1px solid var(--color-borde-principal);
  font-size: 1.2rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
  transition: all var(--transicion-rapida);
}

.contenedor-autenticacion {
  position: relative;
  z-index: 1;
  width: 100%;
  padding: var(--espacio-grande);
}

.cabecera-autenticacion {
  text-align: center;
  margin-bottom: var(--espacio-extra-grande);
}

.logotipo-autenticacion {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  margin-bottom: var(--espacio-pequeno);
}

.icono-logotipo-autenticacion {
  width: 44px;
  height: 44px;
  background: var(--gradiente-acento);
  border-radius: var(--redondeo-grande);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.2rem;
  box-shadow: 0 4px 20px rgba(109, 40, 217, 0.3);
}

.texto-logotipo-autenticacion {
  font-family: var(--fuente-titulos);
  font-size: 1.8rem;
  font-weight: 800;
  letter-spacing: -0.04em;
  background: var(--gradiente-acento);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitulo-autenticacion {
  color: var(--color-texto-secundario);
  font-size: 0.95rem;
}

.formulario-autenticacion {
  background: var(--color-fondo-secundario);
  border: 1px solid var(--color-borde-principal);
  border-radius: var(--redondeo-extra-grande);
  padding: var(--espacio-extra-grande);
  display: flex;
  flex-direction: column;
  gap: var(--espacio-medio);
  box-shadow: var(--sombra-fuerte);
}

.selector-rol {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--espacio-pequeno);
}

.opcion-rol {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.3rem;
  padding: 0.7rem;
  border-radius: var(--redondeo-medio);
  border: 1px solid var(--color-borde-principal);
  cursor: pointer;
  transition: all var(--transicion-rapida);
  font-size: 0.82rem;
  font-weight: 500;
  color: var(--color-texto-secundario);
}

.opcion-rol input {
  display: none;
}

.opcion-rol:hover {
  border-color: var(--color-acento);
}

.opcion-seleccionada {
  border-color: var(--color-acento);
  background: var(--color-acento-sutil);
  color: var(--color-acento);
}

.icono-rol {
  font-size: 1.3rem;
}

.boton-enviar-autenticacion {
  width: 100%;
  margin-top: var(--espacio-pequeno);
}

.texto-pie-autenticacion {
  text-align: center;
  margin-top: var(--espacio-grande);
  color: var(--color-texto-secundario);
  font-size: 0.9rem;
}

.enlace-autenticacion {
  color: var(--color-acento);
  font-weight: 600;
}

@media (max-width: 576px) {
  .contenedor-autenticacion {
    padding: var(--espacio-medio);
    max-width: 100%;
  }

  .formulario-autenticacion {
    padding: var(--espacio-grande);
    border-radius: var(--redondeo-grande);
  }

  .texto-logotipo-autenticacion {
    font-size: 1.5rem;
  }

  .selector-rol {
    grid-template-columns: 1fr;
  }
}
</style>