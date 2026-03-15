<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { usarAlmacenAutenticacion } from '@/almacenes/autenticacion'
import { usarTema } from '@/composables/usarTema'

const enrutador = useRouter()
const almacenAutenticacion = usarAlmacenAutenticacion()
const { alternarTema, esModoOscuro } = usarTema()

const datosFormulario = ref({
  email: '',
  contrasena: ''
})

async function manejarInicioSesion() {
  try {
    await almacenAutenticacion.iniciarSesion(datosFormulario.value)
    enrutador.push('/')
  } catch (errorPeticion) {
  }
}
</script>

<template>
  <div class="pagina-autenticacion">
    <div class="fondo-decorativo">
      <div class="esfera-decorativa esfera-primera"></div>
      <div class="esfera-decorativa esfera-segunda"></div>
      <div class="esfera-decorativa esfera-tercera"></div>
    </div>

    <button class="boton-tema-autenticacion" @click="alternarTema">
      {{ esModoOscuro() ? '☀️' : '🌙' }}
    </button>

    <div class="contenedor-autenticacion animacion-aparecer-desde-abajo">
      <div class="cabecera-autenticacion">
        <div class="logotipo-autenticacion">
          <div class="icono-logotipo-autenticacion">▶</div>
          <h1 class="texto-logotipo-autenticacion">PlayPDF</h1>
        </div>
        <p class="subtitulo-autenticacion">Inicia sesión para continuar</p>
      </div>

      <form @submit.prevent="manejarInicioSesion" class="formulario-autenticacion">
        <div class="grupo-campo">
          <label class="etiqueta-campo">Email</label>
          <input v-model="datosFormulario.email" type="email" placeholder="tu@email.com" required
            autocomplete="email" />
        </div>

        <div class="grupo-campo">
          <label class="etiqueta-campo">Contraseña</label>
          <input v-model="datosFormulario.contrasena" type="password" placeholder="••••••••" required
            autocomplete="current-password" />
        </div>

        <p v-if="almacenAutenticacion.mensajeError" class="mensaje-error-campo text-center">
          {{ almacenAutenticacion.mensajeError }}
        </p>

        <button type="submit" class="boton boton-principal boton-grande boton-enviar-autenticacion"
          :disabled="almacenAutenticacion.estaCargando">
          <span v-if="almacenAutenticacion.estaCargando" class="indicador-carga indicador-carga-boton"></span>
          <span v-else>Iniciar sesión</span>
        </button>
      </form>

      <p class="texto-pie-autenticacion">
        ¿No tienes cuenta?
        <router-link to="/registro" class="enlace-autenticacion">Regístrate</router-link>
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

.esfera-tercera {
  width: 300px;
  height: 300px;
  background: #6d28d9;
  top: 40%;
  left: 50%;
  opacity: 0.06;
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
  box-shadow: var(--sombra-suave);
}

.boton-tema-autenticacion:hover {
  transform: scale(1.05);
  box-shadow: var(--sombra-media);
}

.contenedor-autenticacion {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 400px;
  padding: var(--espacio-grande);
}

.cabecera-autenticacion {
  text-align: center;
  margin-bottom: var(--espacio-enorme);
}

.logotipo-autenticacion {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  margin-bottom: var(--espacio-medio);
}

.icono-logotipo-autenticacion {
  width: 48px;
  height: 48px;
  background: var(--gradiente-acento);
  border-radius: var(--redondeo-grande);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.4rem;
  box-shadow: 0 4px 20px rgba(109, 40, 217, 0.3);
}

.texto-logotipo-autenticacion {
  font-family: var(--fuente-titulos);
  font-size: 2rem;
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

.enlace-autenticacion:hover {
  text-decoration: underline;
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
    font-size: 1.6rem;
  }

  .cabecera-autenticacion {
    margin-bottom: var(--espacio-extra-grande);
  }
}
</style>
