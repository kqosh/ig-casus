package nl.intergamma.casus.presentation;

import io.swagger.v3.oas.annotations.Operation;
import nl.intergamma.casus.access.Artikel;
import nl.intergamma.casus.service.ArtikelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
@RestController
public class ArtikelController {
  private static final Logger LOG = LoggerFactory.getLogger(ArtikelController.class);

  static final String BASE_PATH = "/artikelen";

  @Autowired ArtikelService service;

  @Operation(summary = "Creëer artikel.")
  @PostMapping(BASE_PATH)
  public Artikel create(@RequestBody Artikel artikel) {
    log("create: artikel={}", artikel);
    var result = service.save(artikel);
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
    return service.findByCode(code);
  }

  @Operation(summary = "Update artikel met gegeven id")
  @PutMapping(BASE_PATH + "/{id}")
  public Artikel update(
      @RequestBody Artikel artikel, @RequestParam(value = "userId") String userId) {
    var existingArtikel = getArtikel(artikel.getId());
    validateNotReserved(existingArtikel, userId);
    existingArtikel.update(artikel);
    return service.save(existingArtikel);
  }

  void validateNotReserved(Artikel artikel, String userId) {
    if (artikel.isGereserveerd() && !artikel.getGereserveerdDoorUserId().equals(userId)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "artikel.id=" + artikel.getId());
    }
  }

  @Operation(summary = "Reserveer artikel met gegeven id voor een periodeInSec.")
  @PatchMapping(BASE_PATH + "/{id}/reserveer")
  public Artikel reserveer(
      @PathVariable(value = "id") Long id,
      @RequestParam(value = "periodeInSec") Long periodeInSec,
      @RequestParam(value = "userId") String userId) {
    var existingArtikel = getArtikel(id);
    validateNotReserved(existingArtikel, userId);
    existingArtikel.setGereserveerd(userId, System.currentTimeMillis() + periodeInSec * 1000L);
    return service.save(existingArtikel);
  }

  @Operation(summary = "Delete artikel met gegeven id.")
  @DeleteMapping(BASE_PATH + "/{id}")
  public void delete(
      @PathVariable(value = "id") Long id, @RequestParam(value = "userId") String userId) {
    var artikel = getArtikel(id);
    validateNotReserved(artikel, userId);
    service.delete(artikel);
  }

  private Artikel getArtikel(long id) {
    var artikel = service.findById(id);
    if (artikel.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id=" + id);
    }
    return artikel.get();
  }

  private void log(String format, Object... args) {
    LOG.info(format, args);
  }
}
