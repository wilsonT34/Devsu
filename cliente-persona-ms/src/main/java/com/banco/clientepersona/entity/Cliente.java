package com.banco.clientepersona.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author user
 */

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    
    @Id
    @Column(name = "clienteid")
    private String clienteId;
    
    @Column(nullable = false)
    private String contrasena;
    
    @Column(nullable = false)
    private Boolean estado = true;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id", referencedColumnName = "id")
    private Persona persona;
}