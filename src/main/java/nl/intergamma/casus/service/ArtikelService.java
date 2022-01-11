package nl.intergamma.casus.service;

import nl.intergamma.casus.access.Artikel;
import nl.intergamma.casus.access.ArtikelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class ArtikelService {

  @Autowired
  ArtikelRepository repository;

  @Transactional(readOnly = true)
  public Optional<Artikel> findById(long id) {
    return repository.findById(id);
  }

  @Transactional(readOnly = true)
  public List<Artikel> findByCode(String code) {
    return repository.findByProductCode(code);
  }

  @Transactional
  public Artikel save(Artikel artikel) {
    return repository.save(artikel);
  }

  @Transactional
  public void delete(Artikel artikel) {
    repository.delete(artikel);
  }
}
