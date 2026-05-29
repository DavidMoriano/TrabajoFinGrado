package com.playpdf.modelos;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario_centro", uniqueConstraints = @UniqueConstraint(columnNames = {"id_usuario", "id_centro"}))
public class UsuarioCentro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_centro", nullable = false)
    private Centro centro;

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Centro getCentro() { return centro; }
    public void setCentro(Centro centro) { this.centro = centro; }
}
