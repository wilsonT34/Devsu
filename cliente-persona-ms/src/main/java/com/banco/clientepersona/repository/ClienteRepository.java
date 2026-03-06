package com.banco.clientepersona.repository;

import com.banco.clientepersona.entity.Cliente;
//import com.miapp.core.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author user
 */

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {
    boolean existsByPersonaIdentificacion(String identificacion);
}



