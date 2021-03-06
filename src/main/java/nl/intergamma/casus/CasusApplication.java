package nl.intergamma.casus;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@SpringBootApplication
@RestController
public class CasusApplication {
  private static final Logger LOG = LoggerFactory.getLogger(CasusApplication.class);

  static final String BASE_PATH = "/artikelen";

  @Autowired ArtikelRepository repository;

  public static void main(String[] args) {
    SpringApplication.run(CasusApplication.class, args);
  }

  @Operation(summary = "Test of de applicatie loopt.")
  @GetMapping("/hello")
  public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
    return String.format("Hello %s!", name);
  }

  @Operation(summary = "Creëer artikel.")
  @PostMapping(BASE_PATH)
  public Artikel create(@RequestBody Artikel artikel) {
    log("create: artikel={}", artikel);
    var result = getRepository().save(artikel);
    log("create: result={}", result);
    return result;
  }

  @Operation(summary = "Vind artikel met gegeven id.")
  @GetMapping(BASE_PATH + "/{id}")
  public Artikel get(@PathVariable(value = "id") Long id) {
    log("get: id={}", id);
    var artikel = getArtikel(id);
    log("get: result={}", artikel);
    return artikel;
  }

  @Operation(summary = "Vind alle artikelen voor gegeven productcode.")
  @GetMapping(BASE_PATH)
  public List<Artikel> find(@RequestParam(value = "code") String code) {
    return getRepository().findByCode(code);
  }

  @Operation(summary = "Update artikel met gegeven id")
  @PutMapping(BASE_PATH + "/{id}")
  public Artikel update(@RequestBody Artikel artikel) {
    var existingArtikel = getArtikel(artikel.getId());
    existingArtikel.setCode(artikel.getCode());
    existingArtikel.setNaam(artikel.getNaam());
    return getRepository().save(existingArtikel);
  }

  @Operation(summary = "Reserveer artikel met gegeven id voor een periodeInSec.")
  @PatchMapping(BASE_PATH + "/{id}/reserveer")
  public Artikel reserveer(
      @PathVariable(value = "id") Long id,
      @RequestParam(value = "periodeInSec") Long periodeInSec) {
    var existingArtikel = getArtikel(id);
    existingArtikel.setGereservedTot(System.currentTimeMillis() + periodeInSec * 1000L);
    return getRepository().save(existingArtikel);
  }

  @Operation(summary = "Delete artikel met gegeven id.")
  @DeleteMapping(BASE_PATH + "/{id}")
  public void delete(@PathVariable(value = "id") Long id) {
    var artikel = getArtikel(id);
    getRepository().delete(artikel);
  }

  private Artikel getArtikel(long id) {
    var artikel = getRepository().findById(id);
    if (artikel.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id=" + id);
    }
    return artikel.get();
  }

  private void log(String format, Object... args) {
    LOG.info(format, args);
  }

  @Bean //qqqq maar aparte config class
  public ArtikelRepository getRepository() {
    return repository;
  }
}
