package br.com.infnet.guilda_dos_aventureiros.Repository;

import br.com.infnet.guilda_dos_aventureiros.Models.legacy.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}