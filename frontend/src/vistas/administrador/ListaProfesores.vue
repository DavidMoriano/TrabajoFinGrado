<script setup>
import { ref, onMounted } from 'vue'
import { servicioUsuarios } from '@/servicios/api'

const listaProfesores = ref([])
const estaCargando = ref(true)

onMounted(async () => {
  try {
    const respuesta = await servicioUsuarios.obtenerProfesores()
    listaProfesores.value = respuesta.data
  } catch (error) {
    listaProfesores.value = []
  } finally {
    estaCargando.value = false
  }
})
</script>

<template>
  <div class="container py-4">
    <div class="mb-4">
      <h1 class="titulo-pagina">Profesores registrados</h1>
      <p class="subtitulo-pagina">Listado completo de profesores con acceso a la plataforma</p>
    </div>
    <div class="table-responsive border rounded-3 animacion-aparecer-desde-abajo">
      <table class="table table-hover mb-0">
        <thead>
          <tr>
            <th>Profesor</th>
            <th>Email</th>
            <th>Estudios</th>
            <th>Fecha registro</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="profesor in listaProfesores" :key="profesor.id_usuario">
            <td class="fw-semibold">{{ profesor.nombre }} {{ profesor.apellidos }}</td>
            <td class="texto-secundario-pequeno">{{ profesor.email }}</td>
            <td><span class="etiqueta etiqueta-acento">{{ profesor.estudios }}</span></td>
            <td class="texto-terciario-pequeno">{{ profesor.fecha_registro }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
