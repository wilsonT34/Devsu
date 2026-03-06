package com.banco.cuentamovimiento.repository;


import com.banco.cuentamovimiento.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 *
 * @author user
 */

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, String> {
    List<Cuenta> findByClienteid(String clienteid);
    boolean existsByNumeroCuenta(String numeroCuenta);
}