import { ref, watch } from "vue";

const temaActual = ref(localStorage.getItem("playpdf-tema") || "oscuro");

export function usarTema() {
  const aplicarTema = (nombreTema) => {
    document.documentElement.setAttribute("data-tema", nombreTema);
    localStorage.setItem("playpdf-tema", nombreTema);
  };

  const alternarTema = () => {
    temaActual.value = temaActual.value === "oscuro" ? "claro" : "oscuro";
    aplicarTema(temaActual.value);
  };

  const esModoOscuro = () => temaActual.value === "oscuro";

  aplicarTema(temaActual.value);

  watch(temaActual, aplicarTema);

  return {
    temaActual,
    alternarTema,
    esModoOscuro,
  };
}
