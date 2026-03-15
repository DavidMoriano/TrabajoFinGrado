import { createApp } from "vue";
import { createPinia } from "pinia";
import Aplicacion from "./App.vue";
import enrutador from "./enrutador";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import "./assets/css/variables.css";
import "./assets/css/estilos-globales.css";
import "./assets/css/animaciones.css";

const aplicacion = createApp(Aplicacion);

aplicacion.use(createPinia());
aplicacion.use(enrutador);

aplicacion.mount("#app");
