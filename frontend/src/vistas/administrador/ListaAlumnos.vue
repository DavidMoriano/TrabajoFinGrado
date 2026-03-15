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
            <td class="fw-semibold">{{ alumno.nombre }} {{ alumno.apellidos }}</td>
            <td class="texto-secundario-pequeno">{{ alumno.email }}</td>
            <td><span class="etiqueta etiqueta-acento">{{ alumno.estudios }}</span></td>
            <td class="text-center">{{ alumno.edad }}</td>
            <td class="texto-terciario-pequeno">{{ alumno.fechaRegistro }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
