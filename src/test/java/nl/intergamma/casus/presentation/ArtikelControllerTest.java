package nl.intergamma.casus.presentation;

import nl.intergamma.casus.access.Artikel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArtikelControllerTest {

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  private String baseUrl;

  private HttpHeaders headers;

  @BeforeEach
  void init() {
    baseUrl = "http://localhost:" + port + ArtikelController.BASE_PATH;
    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
  }

  @Test
  void contextLoads() {}

  @Test
  void crud() {
    // CREATE
    var artikel = new Artikel("schuifmaat-code", "123");
    var createdArtikel = createArtikel(artikel);

    assertThat(createdArtikel.getId()).isNotNull();
    assertThat(createdArtikel.getCode()).isEqualTo(artikel.getCode());
    assertThat(createdArtikel.getFiliaalnaam()).isEqualTo(artikel.getFiliaalnaam());

    // READ: find
    var artikelen =
        restTemplate.getForObject(baseUrl + "?code=" + artikel.getCode(), Artikel[].class);
    assertThat(artikelen).hasSize(1);
    assertThat(artikelen[0]).isInstanceOf(Artikel.class);
    assertThat(artikelen[0]).usingRecursiveComparison().isEqualTo(createdArtikel);
    // READ: get
    var fetchedArtikel = getArtikel(createdArtikel.getId());
    assertThat(fetchedArtikel).usingRecursiveComparison().isEqualTo(createdArtikel);

    // UPDATE
    var userId = "jan";
    fetchedArtikel.setFiliaalnaam("456");
    var updatedArtikel = update(fetchedArtikel, userId);
//    restTemplate.put(baseUrl + "/" + fetchedArtikel.getId(), updateRequest, Artikel.class);
//    var updatedArtikel = getArtikel(fetchedArtikel.getId());qqqq
    assertThat(updatedArtikel).usingRecursiveComparison().isEqualTo(fetchedArtikel);

    // DELETE
    restTemplate.delete(baseUrl + "/" + updatedArtikel.getId() + "?userId=" + userId);
    ResponseEntity<Artikel> entity =
        restTemplate.getForEntity(baseUrl + "/" + updatedArtikel.getId(), Artikel.class);
    assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  private Artikel getArtikel(long id) {
    return restTemplate.getForObject(baseUrl + "/" + id, Artikel.class);
  }

  private Artikel createArtikel(Artikel artikel) {
    var createRequest = new HttpEntity<>(artikel, headers);
    return restTemplate.postForObject(baseUrl, createRequest, Artikel.class);
  }

  @Test
  void reserveer() throws InterruptedException {
    // CREATE
    var artikel = new Artikel("waterpas", "456");
    artikel = createArtikel(artikel);
    assertThat(artikel.isGereserveerd()).isFalse();

    final var userId = "jan";//qqqq member
    final var otherUserId = "klaas";

    // RESERVEER
    artikel = reserveer(artikel, userId);
    assertThat(artikel.isGereserveerd()).isTrue();

    // RESERVEER door een ander faalt
    try {
      reserveer(artikel, otherUserId);
    } catch (HttpClientErrorException ex) {
      assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
      assertThat(ex.getMessage()).isEqualTo("artikel.id=" + artikel.getId());
    }

    // RESERVEER door dezelfde user slaagt
    artikel = reserveer(artikel, userId);
    assertThat(artikel.isGereserveerd()).isTrue();

    // UPDATE door een ander faalt
    artikel.setFiliaalnaam("Leiden");
    try {
      update(artikel, otherUserId);
    } catch (HttpClientErrorException ex) {
      assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
      assertThat(ex.getMessage()).isEqualTo("artikel.id=" + artikel.getId());
    }

    // UPDATE door dezelfde user slaagt
    var updatedArtikel = update(artikel, userId);
    assertThat(updatedArtikel.getFiliaalnaam()).isEqualTo("Leiden");

    // qqqq delete fails+slaagt

    Thread.sleep(1050);
    assertThat(artikel.isGereserveerd()).isFalse();
  }

  private Artikel reserveer(Artikel artikel, String userId) {
    var reserveerRequest = new HttpEntity<>(artikel, headers);
    return restTemplate.patchForObject(
        baseUrl + "/" + artikel.getId() + "/reserveer?periodeInSec=1&userId=" + userId,
        reserveerRequest,
        Artikel.class);
  }

  private Artikel update(Artikel artikel, String userId) {
    var updateRequest = new HttpEntity<>(artikel, headers);
    restTemplate.put(baseUrl + "/" + artikel.getId() + "?userId=" + userId, updateRequest, Artikel.class);
    return getArtikel(artikel.getId());
  }
}
