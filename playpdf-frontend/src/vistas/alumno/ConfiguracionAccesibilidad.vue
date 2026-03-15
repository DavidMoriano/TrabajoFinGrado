<script setup>
import { ref, watch } from 'vue'
import { usarTema } from '@/composables/usarTema'

const { alternarTema, esModoOscuro } = usarTema()

const tamanoTextoSeleccionado = ref(localStorage.getItem('playpdf-tamano-texto') || 'normal')

const opcionesTamanoTexto = [
  { valor: 'pequeno', etiqueta: 'Pequeño', tamanoPixeles: '14px' },
  { valor: 'normal', etiqueta: 'Normal', tamanoPixeles: '16px' },
  { valor: 'grande', etiqueta: 'Grande', tamanoPixeles: '18px' },
  { valor: 'muy-grande', etiqueta: 'Muy grande', tamanoPixeles: '20px' }
]

watch(tamanoTextoSeleccionado, (nuevoValor) => {
  const opcionEncontrada = opcionesTamanoTexto.find(opcion => opcion.valor === nuevoValor)
  document.documentElement.style.fontSize = opcionEncontrada?.tamanoPixeles || '16px'
  localStorage.setItem('playpdf-tamano-texto', nuevoValor)
})
</script>

<template>
  <div class="container py-4" style="max-width:700px;">
    <div class="mb-4">
      <h1 class="titulo-pagina">Accesibilidad y colores</h1>
      <p class="subtitulo-pagina">Personaliza la visualización para tu comodidad</p>
    </div>

    <div class="tarjeta animacion-aparecer-desde-abajo" style="margin-bottom:var(--espacio-grande);">
      <h3 style="font-size:1rem;font-weight:700;margin-bottom:var(--espacio-medio);">Tema de la interfaz</h3>
      <p style="font-size:0.85rem;color:var(--color-texto-secundario);margin-bottom:var(--espacio-medio);">Cambia entre
        modo claro y oscuro con un solo clic</p>
      <div class="fila-opciones">
        <div class="opcion-visual" :class="{ 'opcion-visual-activa': !esModoOscuro() }"
          @click="!esModoOscuro() || alternarTema()"><span style="font-size:1.5rem;">☀️</span><span
            style="font-weight:600;font-size:0.9rem;">Claro</span></div>
        <div class="opcion-visual" :class="{ 'opcion-visual-activa': esModoOscuro() }"
          @click="esModoOscuro() || alternarTema()"><span style="font-size:1.5rem;">🌙</span><span
            style="font-weight:600;font-size:0.9rem;">Oscuro</span></div>
      </div>
    </div>

    <div class="tarjeta animacion-aparecer-desde-abajo">
      <h3 style="font-size:1rem;font-weight:700;margin-bottom:var(--espacio-medio);">Tamaño de texto</h3>
      <div class="fila-opciones">
        <div v-for="opcion in opcionesTamanoTexto" :key="opcion.valor" class="opcion-visual"
          :class="{ 'opcion-visual-activa': tamanoTextoSeleccionado === opcion.valor }"
          @click="tamanoTextoSeleccionado = opcion.valor">
          <span :style="{ fontSize: opcion.tamanoPixeles, fontWeight: 700 }">Aa</span>
          <span style="font-size:0.78rem;">{{ opcion.etiqueta }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.fila-opciones {
  display: flex;
  gap: var(--espacio-pequeno);
  flex-wrap: wrap;
}

.opcion-visual {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.3rem;
  padding: 0.8rem 1.2rem;
  border-radius: var(--redondeo-medio);
  border: 1.5px solid var(--color-borde-principal);
  cursor: pointer;
  transition: all var(--transicion-rapida);
  min-width: 80px;
  font-weight: 600;
  font-size: 0.9rem;
}

.opcion-visual:hover {
  border-color: var(--color-acento);
}

.opcion-visual-activa {
  border-color: var(--color-acento);
  background: var(--color-acento-sutil);
  color: var(--color-acento);
}
</style>
