<script setup>
import { ref, watch } from "vue";
import { usarTema } from "@/composables/usarTema";

const { alternarTema, esModoOscuro } = usarTema();

const tamanoTextoSeleccionado = ref(localStorage.getItem("playpdf-tamano-texto") || "normal");

const opcionesTamanoTexto = [
  { valor: "pequeno", etiqueta: "Pequeño", tamanoPixeles: "14px" },
  { valor: "normal", etiqueta: "Normal", tamanoPixeles: "16px" },
  { valor: "grande", etiqueta: "Grande", tamanoPixeles: "18px" },
  { valor: "muy-grande", etiqueta: "Muy grande", tamanoPixeles: "20px" }
];

watch(tamanoTextoSeleccionado, (nuevoValor) => {
  const opcionEncontrada = opcionesTamanoTexto.find(opcion => opcion.valor === nuevoValor);
  document.documentElement.style.fontSize = opcionEncontrada?.tamanoPixeles || "16px";
  localStorage.setItem("playpdf-tamano-texto", nuevoValor);
});
</script>

<template>
  <div class="container py-4 contenedor-accesibilidad">
    <div class="mb-4">
      <h1 class="titulo-pagina">Accesibilidad y colores</h1>
      <p class="subtitulo-pagina">Personaliza la visualización para tu comodidad</p>
    </div>

    <div class="tarjeta animacion-aparecer-desde-abajo seccion-tarjeta">
      <h3 class="titulo-seccion">Tema de la interfaz</h3>
      <p class="descripcion-seccion">Cambia entre modo claro y oscuro con un solo clic</p>
      <div class="fila-opciones">
        <div class="opcion-visual" :class="{ 'opcion-visual-activa': !esModoOscuro() }"
          @click="!esModoOscuro() || alternarTema()">
          <span class="icono-opcion">☀️</span>
          <span class="texto-opcion">Claro</span>
        </div>
        <div class="opcion-visual" :class="{ 'opcion-visual-activa': esModoOscuro() }"
          @click="esModoOscuro() || alternarTema()">
          <span class="icono-opcion">🌙</span>
          <span class="texto-opcion">Oscuro</span>
        </div>
      </div>
    </div>

    <div class="tarjeta animacion-aparecer-desde-abajo">
      <h3 class="titulo-seccion">Tamaño de texto</h3>
      <div class="fila-opciones">
        <div v-for="opcion in opcionesTamanoTexto" :key="opcion.valor" class="opcion-visual"
          :class="{ 'opcion-visual-activa': tamanoTextoSeleccionado === opcion.valor }"
          @click="tamanoTextoSeleccionado = opcion.valor">
          <span class="muestra-tamano" :style="{ fontSize: opcion.tamanoPixeles }">Aa</span>
          <span class="etiqueta-opcion">{{ opcion.etiqueta }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.contenedor-accesibilidad {
  max-width: 700px;
}

.seccion-tarjeta {
  margin-bottom: var(--espacio-grande);
}

.titulo-seccion {
  font-size: 1rem;
  font-weight: 700;
  margin-bottom: var(--espacio-medio);
}

.descripcion-seccion {
  font-size: 0.85rem;
  color: var(--color-texto-secundario);
  margin-bottom: var(--espacio-medio);
}

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
}

.opcion-visual:hover {
  border-color: var(--color-acento);
}

.opcion-visual-activa {
  border-color: var(--color-acento);
  background: var(--color-acento-sutil);
  color: var(--color-acento);
}

.icono-opcion {
  font-size: 1.5rem;
}

.texto-opcion {
  font-weight: 600;
  font-size: 0.9rem;
}

.muestra-tamano {
  font-weight: 700;
}

.etiqueta-opcion {
  font-size: 0.78rem;
}
</style>
