package com.banco.cuentamovimiento.repository;

import com.banco.cuentamovimiento.entity.ClienteLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author user
 */
@Repository
public interface ClienteLocalRepository extends JpaRepository<ClienteLocal, String> {
    boolean existsByClienteId(String clienteId);
}
