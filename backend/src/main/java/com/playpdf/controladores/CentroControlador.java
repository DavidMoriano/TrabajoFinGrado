package com.playpdf.controladores;

import com.playpdf.modelos.Centro;
import com.playpdf.modelos.Usuario;
import com.playpdf.modelos.UsuarioCentro;
import com.playpdf.repositorios.UsuarioCentroRepositorio;
import com.playpdf.repositorios.UsuarioRepositorio;
import com.playpdf.servicios.CentroServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/centros")
public class CentroControlador {

    private final CentroServicio centroServicio;
    private final UsuarioCentroRepositorio usuarioCentroRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    public CentroControlador(CentroServicio centroServicio,
            UsuarioCentroRepositorio usuarioCentroRepositorio,
            UsuarioRepositorio usuarioRepositorio) {
        this.centroServicio = centroServicio;
        this.usuarioCentroRepositorio = usuarioCentroRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @GetMapping
    public ResponseEntity<List<Centro>> obtenerTodos() {
        return ResponseEntity.ok(centroServicio.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(centroServicio.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<?> obtenerPorCodigo(@PathVariable String codigo) {
        try {
            return ResponseEntity.ok(centroServicio.obtenerPorCodigo(codigo));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/mis-centros")
    public ResponseEntity<?> obtenerMisCentros() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<Centro> centros = usuarioCentroRepositorio.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream().map(UsuarioCentro::getCentro).collect(Collectors.toList());
        return ResponseEntity.ok(centros);
    }

    @PostMapping("/unirse")
    public ResponseEntity<?> unirseACentro(@RequestBody Map<String, Object> datos) {
        try {
            String codigo = datos.get("codigo_acceso").toString().toUpperCase();
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario usuario = usuarioRepositorio.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Centro centro = centroServicio.obtenerPorCodigo(codigo);
            if (usuarioCentroRepositorio.existsByUsuarioIdUsuarioAndCentroIdCentro(
                    usuario.getIdUsuario(), centro.getIdCentro())) {
                return ResponseEntity.ok(centro);
            }
            UsuarioCentro uc = new UsuarioCentro();
            uc.setUsuario(usuario);
            uc.setCentro(centro);
            usuarioCentroRepositorio.save(uc);
            return ResponseEntity.ok(centro);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/abandonar/{idCentro}")
    public ResponseEntity<?> abandonarCentro(@PathVariable Long idCentro) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario usuario = usuarioRepositorio.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            usuarioCentroRepositorio.findByUsuarioIdUsuarioAndCentroIdCentro(
                    usuario.getIdUsuario(), idCentro)
                    .ifPresent(usuarioCentroRepositorio::delete);
            return ResponseEntity.ok(Map.of("message", "Centro abandonado"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Centro centro) {
        try {
            if (centro.getCodigoAcceso() != null) {
                centro.setCodigoAcceso(centro.getCodigoAcceso().toUpperCase());
            }
            return ResponseEntity.ok(centroServicio.crear(centro));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "El código de acceso ya está en uso. Elige otro código."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            centroServicio.eliminar(id);
            return ResponseEntity.ok(Map.of("message", "Centro eliminado"));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "No se puede eliminar el centro porque tiene asignaturas asociadas. Elimina primero todas sus asignaturas."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
