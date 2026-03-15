<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { usarAlmacenAutenticacion } from '@/almacenes/autenticacion'

const enrutador = useRouter()
const almacenAutenticacion = usarAlmacenAutenticacion()

const misAsignaturas = ref([])

const totalTemas = 0
</script>

<template>
  <div class="container py-4">
    <div class="mb-4">
      <h1 class="titulo-pagina">Bienvenido, {{ almacenAutenticacion.usuarioActual?.nombre || 'Profesor' }}</h1>
      <p class="subtitulo-pagina">Gestiona tus asignaturas y sube temario para tus alumnos</p>
    </div>
    <div class="row g-3 mb-4">
      <div class="col-12 col-md-4">
        <div class="tarjeta tarjeta-estadistica animacion-aparecer-desde-abajo h-100"><span
            style="font-size:1.5rem;">📘</span>
          <div class="valor-estadistica">{{ misAsignaturas.length }}</div>
          <div class="descripcion-estadistica">Mis asignaturas</div>
        </div>
      </div>
      <div class="col-12 col-md-4">
        <div class="tarjeta tarjeta-estadistica animacion-aparecer-desde-abajo h-100"><span
            style="font-size:1.5rem;">📄</span>
          <div class="valor-estadistica">{{ totalTemas }}</div>
          <div class="descripcion-estadistica">Temas subidos</div>
        </div>
      </div>
      <div class="col-12 col-md-4">
        <div class="tarjeta tarjeta-estadistica animacion-aparecer-desde-abajo h-100"><span
            style="font-size:1.5rem;">🎓</span>
          <div class="valor-estadistica">0</div>
          <div class="descripcion-estadistica">Alumnos activos</div>
        </div>
      </div>
    </div>
    <h2 class="fw-bold mb-3" style="font-size:1.1rem;">Mis asignaturas</h2>
    <div class="row g-3">
      <div v-for="asignatura in misAsignaturas" :key="asignatura.id_asignatura" class="col-12 col-md-6">
        <div class="tarjeta tarjeta-interactiva animacion-aparecer-desde-abajo h-100">
          <div class="d-flex justify-content-between align-items-start">
            <div>
              <h3 class="fw-bold" style="font-size:1.05rem;">{{ asignatura.nombre }}</h3>
              <p class="text-secondary small mt-1">{{ asignatura.descripcion }}</p>
            </div>
            <span class="badge bg-success bg-opacity-10 text-success">{{ asignatura.cantidadTemas }} temas</span>
          </div>
          <div class="d-flex gap-2 mt-3">
            <button class="btn btn-primary btn-sm"
              @click="enrutador.push({ name: 'SubirTemario', params: { identificadorAsignatura: asignatura.id_asignatura } })">📄
              Subir temario</button>
            <button class="btn btn-outline-secondary btn-sm" @click="enrutador.push({ name: 'MisAsignaturas' })">✏️
              Editar</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
