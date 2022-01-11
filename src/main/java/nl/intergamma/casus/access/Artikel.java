package nl.intergamma.casus.access;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Voor elk fysiek artikel is er een Artikel record in de database. Het totaal aantal artikelen van
 * een product en totaal of per winkel vind je met een count(*) query. Als een fysiek artikel is
 * geleverd, dus echt weg is, wordt het uit de database verwijderd.
 */
@Entity
public class Artikel {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String productCode; // FK to Product{code, naam, omschrijving}
  private String filiaalnaam;
  private String stellingnummer;
  private String gereserveerdDoorUserId;
  private long gereserveerdTot;
  private boolean verkocht;

  protected Artikel() {}

  public Artikel(String productCode, String filiaalnaam, String stellingnummer) {
    this.productCode = productCode;
    this.filiaalnaam = filiaalnaam;
    this.stellingnummer = stellingnummer;
  }

  public void update(Artikel newValues) {
    productCode = newValues.getProductCode();
    filiaalnaam = newValues.getFiliaalnaam();
    gereserveerdDoorUserId = newValues.getGereserveerdDoorUserId();
    gereserveerdTot = newValues.getGereserveerdTot();
    verkocht = newValues.isVerkocht();
  }

  public Long getId() {
    return id;
  }

  public String getProductCode() {
    return productCode;
  }

  public String getFiliaalnaam() {
    return filiaalnaam;
  }

  public void setFiliaalnaam(String filiaalnaam) {
    this.filiaalnaam = filiaalnaam;
  }

  public String getStellingnummer() {
    return stellingnummer;
  }

  public void setStellingnummer(String stellingnummer) {
    this.stellingnummer = stellingnummer;
  }

  public String getGereserveerdDoorUserId() {
    return gereserveerdDoorUserId;
  }

  public long getGereserveerdTot() {
    return gereserveerdTot;
  }

  public void setGereserveerd(String gereserveerdDoorUserId, long gereserveerdTot) {
    this.gereserveerdDoorUserId = gereserveerdDoorUserId;
    this.gereserveerdTot = gereserveerdTot;
  }

  @JsonIgnore
  public boolean isGereserveerd() {
    return System.currentTimeMillis() < gereserveerdTot;
  }

  public boolean isVerkocht() {
    return verkocht;
  }

  public void setVerkocht(boolean verkocht) {
    this.verkocht = verkocht;
  }

  @Override
  public String toString() {
    return "Artikel{"
        + "id="
        + id
        + ", code='"
        + productCode
        + "', filiaalnaam='"
        + filiaalnaam
        + "', gereservedDoor='"
        + gereserveerdDoorUserId
        + "', gereservedTot="
        + gereserveerdTot
        + ", verkocht="
        + verkocht
        + '}';
  }
}
