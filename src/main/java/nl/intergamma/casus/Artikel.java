package nl.intergamma.casus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Voor elk fysiek artikel is er een Artikel record in de database. Het totaal aantal artikelen van een product
 * en totaal of per winkel vind je met een count(*) query.
 * Als een fysiek artikel is geleverd, dus echt weg is, wordt het uit de database verwijderd.
 */
@Entity
public class Artikel {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String naam; //qqqq drop, zit in Product
  private String code; //qqqq FK to Product{code, naam, omschrijving}
  //qqqq private String filiaalnaam;
  //qqqq private String gereservedDoor;
  private long gereservedTot;
//  private boolean verkocht;qqqq

  protected Artikel() {}

  public Artikel(String naam, String code) {
    this.naam = naam;
    this.code = code;
  }

  public Long getId() {
    return id;
  }

  public String getNaam() {
    return naam;
  }

  public void setNaam(String naam) {
    this.naam = naam;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public long getGereservedTot() {
    return gereservedTot;
  }

  public void setGereservedTot(long gereservedTot) {
    this.gereservedTot = gereservedTot;
  }

  @JsonIgnore
  public boolean isGereserveerd() {
    return System.currentTimeMillis() < gereservedTot;
  }

  @Override
  public String toString() {
    return "Artikel{"
        + "id="
        + id
        + ", naam='"
        + naam
        + "', code='"
        + code
        + "', gereservedTot="
        + gereservedTot
        + '}';
  }
}
