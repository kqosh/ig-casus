package nl.intergamma.casus;

import static org.assertj.core.api.Assertions.assertThat;

import nl.intergamma.casus.access.Artikel;
import nl.intergamma.casus.presentation.CasusApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CasusApplicationTests {

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  private String baseUrl;

  private HttpHeaders headers;

  @BeforeEach
  void init() {
    baseUrl = "http://localhost:" + port + CasusApplication.BASE_PATH;
    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
  }

  @Test
  void contextLoads() {}

  @Test
  void helloShouldReturnDefaultMessage() {
    assertThat(restTemplate.getForObject("http://localhost:" + port + "/hello", String.class))
        .startsWith("Hello");
  }

  @Test
  void crud() {
    // CREATE
    var artikel = new Artikel("schuifmaat", "123");
    var createdArtikel = createArtikel(artikel);

    assertThat(createdArtikel.getId()).isNotNull();
    assertThat(createdArtikel.getCode()).isEqualTo(artikel.getCode());
    assertThat(createdArtikel.getNaam()).isEqualTo(artikel.getNaam());

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
    fetchedArtikel.setNaam("nijptang");
    var updateRequest = new HttpEntity<>(fetchedArtikel, headers);
    restTemplate.put(baseUrl + "/" + fetchedArtikel.getId(), updateRequest, Artikel.class);
    var updatedArtikel = getArtikel(fetchedArtikel.getId());
    assertThat(updatedArtikel).usingRecursiveComparison().isEqualTo(fetchedArtikel);

    // DELETE
    restTemplate.delete(baseUrl + "/" + updatedArtikel.getId());
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
    var createdArtikel = createArtikel(artikel);
    assertThat(createdArtikel.isGereserveerd()).isFalse();

    // RESERVEER
    var reserveerRequest = new HttpEntity<>(createdArtikel, headers);
    var gereserveerdArtikel =
        restTemplate.patchForObject(
            baseUrl + "/" + createdArtikel.getId() + "/reserveer?periodeInSec=1",
            reserveerRequest,
            Artikel.class);
    assertThat(gereserveerdArtikel.isGereserveerd()).isTrue();
    Thread.sleep(1050);
    assertThat(gereserveerdArtikel.isGereserveerd()).isFalse();
  }
}
