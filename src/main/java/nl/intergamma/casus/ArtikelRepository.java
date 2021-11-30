package nl.intergamma.casus;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ArtikelRepository extends CrudRepository<Artikel, Long> {

  List<Artikel> findByCode(String code);
}
