<script setup>
import { ref, onMounted } from 'vue'
import { servicioUsuarios } from '@/servicios/api'

const listaAlumnos = ref([])
const estaCargando = ref(true)

onMounted(async () => {
  try {
    const respuesta = await servicioUsuarios.obtenerAlumnos()
    listaAlumnos.value = respuesta.data
  } catch (error) {
    listaAlumnos.value = []
  } finally {
    estaCargando.value = false
  }
})
</script>

<template>
  <div class="container py-4">
    <div class="mb-4">
      <h1 class="titulo-pagina">Alumnos registrados</h1>
      <p class="subtitulo-pagina">Listado de usuarios con nivel básico</p>
    </div>
    <div class="table-responsive border rounded-3 animacion-aparecer-desde-abajo">
      <table class="table table-hover mb-0">
        <thead>
          <tr>
            <th>Alumno</th>
            <th>Email</th>
            <th>Estudios</th>
            <th>Edad</th>
            <th>Registro</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="alumno in listaAlumnos" :key="alumno.id_usuario">
            <td style="font-weight:600;">{{ alumno.nombre }} {{ alumno.apellidos }}</td>
            <td style="color:var(--color-texto-secundario);font-size:0.88rem;">{{ alumno.email }}</td>
            <td><span class="etiqueta etiqueta-acento">{{ alumno.estudios }}</span></td>
            <td style="text-align:center;">{{ alumno.edad }}</td>
            <td style="font-size:0.85rem;color:var(--color-texto-terciario);">{{ alumno.fechaRegistro }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
