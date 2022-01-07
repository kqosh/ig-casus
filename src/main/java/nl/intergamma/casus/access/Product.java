package nl.intergamma.casus.access;

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
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String code;

  private String naam;
  private String omschrijving;

  protected Product() {}

  public Product(String name, String omschrijving) {
    this.code = code;
    this.omschrijving = omschrijving;
  }

  public String getCode() {
    return code;
  }

  public String getNaam() {
    return naam;
  }

  public void setNaam(String naam) {
    this.naam = naam;
  }

  public String getOmschrijving() {
    return omschrijving;
  }

  public void setOmschrijving(String omschrijving) {
    this.omschrijving = omschrijving;
  }

  @Override
  public String toString() {
    return "Product{"
        + "code='"
        + code
        + "', naam='"
        + naam
        + "', omschrijving='"
        + omschrijving
        + "'}";
  }
}
