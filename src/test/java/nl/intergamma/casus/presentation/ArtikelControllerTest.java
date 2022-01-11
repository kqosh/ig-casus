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

  private static final String USER_ID = "jan";
  private static final String OTHER_USER_ID = "klaas";


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
    var artikel = new Artikel("schuifmaat-code", "Alphen", "1.3.2");
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
    fetchedArtikel.setFiliaalnaam("456");
    var updatedArtikel = update(fetchedArtikel, USER_ID);
    assertThat(updatedArtikel).usingRecursiveComparison().isEqualTo(fetchedArtikel);

    // DELETE
    delete(updatedArtikel, USER_ID);
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
    var artikel = new Artikel("waterpas", "Leiderdorp", "7.2.4");
    artikel = createArtikel(artikel);
    assertThat(artikel.isGereserveerd()).isFalse();

    // RESERVEER
    artikel = reserveer(artikel, USER_ID);
    assertThat(artikel.isGereserveerd()).isTrue();

    // RESERVEER door een ander faalt
    try {
      reserveer(artikel, OTHER_USER_ID);
    } catch (HttpClientErrorException ex) {
      assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
      assertThat(ex.getMessage()).isEqualTo("artikel.id=" + artikel.getId());
    }

    // RESERVEER door dezelfde user slaagt
    artikel = reserveer(artikel, USER_ID);
    assertThat(artikel.isGereserveerd()).isTrue();

    // UPDATE door een ander faalt
    artikel.setFiliaalnaam("Leiden");
    try {
      update(artikel, OTHER_USER_ID);
    } catch (HttpClientErrorException ex) {
      assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
      assertThat(ex.getMessage()).isEqualTo("artikel.id=" + artikel.getId());
    }

    // UPDATE door dezelfde user slaagt
    var updatedArtikel = update(artikel, USER_ID);
    assertThat(updatedArtikel.getFiliaalnaam()).isEqualTo("Leiden");

    // DELETE door een ander faalt
    try {
      update(artikel, OTHER_USER_ID);
    } catch (HttpClientErrorException ex) {
      assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
      assertThat(ex.getMessage()).isEqualTo("artikel.id=" + artikel.getId());
    }

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

  private void delete(Artikel artikel, String userId) {
    restTemplate.delete(baseUrl + "/" + artikel.getId() + "?userId=" + userId);
  }
}
