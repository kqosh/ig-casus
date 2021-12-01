package nl.intergamma.casus;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ArtikelRepository extends CrudRepository<Artikel, Long> {//qqqq voeg transactionaliteit toe

  List<Artikel> findByCode(String code); //qqqq dit doet nog nix
}
