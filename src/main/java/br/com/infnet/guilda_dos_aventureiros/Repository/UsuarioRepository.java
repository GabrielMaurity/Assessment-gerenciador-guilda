package br.com.infnet.guilda_dos_aventureiros.Repository;

import br.com.infnet.guilda_dos_aventureiros.Models.legacy.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u " +
            "LEFT JOIN FETCH u.roles r " +
            "LEFT JOIN FETCH r.permissions " +
            "WHERE u.id = :id")
    Optional<Usuario> findByIdWithRolesAndPermissions(@Param("id") Long id);
}
