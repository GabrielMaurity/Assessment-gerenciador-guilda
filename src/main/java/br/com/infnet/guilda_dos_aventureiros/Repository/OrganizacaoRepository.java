package br.com.infnet.guilda_dos_aventureiros.Repository;

import br.com.infnet.guilda_dos_aventureiros.Models.legacy.Organizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizacaoRepository extends JpaRepository<Organizacao, Long> {

}
