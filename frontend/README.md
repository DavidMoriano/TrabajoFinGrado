# PlayPDF - Frontend

## Instalación

```bash
npm install
npm run dev
```

## Acceso sin backend

Como no hay backend, el login no funciona. Para entrar hay que inyectar un usuario en el navegador.

Abre la consola del navegador y pega uno de estos bloques según el rol que quieras probar:

### Entrar como alumno

```javascript
localStorage.setItem("playpdf-token", "token-demo-123");
localStorage.setItem(
  "playpdf-usuario",
  JSON.stringify({
    id_usuario: 1,
    nombre: "Pablo",
    apellidos: "Arráez Esteban",
    email: "pablo@gmail.com",
    rol: "alumno",
    edad: 21,
    estudios: "DAW",
  }),
);
location.reload();
```

### Entrar como profesor

```javascript
localStorage.setItem("playpdf-token", "token-demo-123");
localStorage.setItem(
  "playpdf-usuario",
  JSON.stringify({
    id_usuario: 2,
    nombre: "Juan",
    apellidos: "Pérez García",
    email: "juan@calasanz.es",
    rol: "profesor",
    edad: 35,
    estudios: "Ingeniería Informática",
  }),
);
location.reload();
```

### Entrar como administrador

```javascript
localStorage.setItem("playpdf-token", "token-demo-123");
localStorage.setItem(
  "playpdf-usuario",
  JSON.stringify({
    id_usuario: 3,
    nombre: "Admin",
    apellidos: "PlayPDF",
    email: "admin@playpdf.es",
    rol: "administrador",
    edad: 40,
    estudios: "Administración",
  }),
);
location.reload();
```