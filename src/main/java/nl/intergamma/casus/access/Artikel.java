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

  private String code; // FK to Product{code, naam, omschrijving}
  private String filiaalnaam;
  private String gereservedDoor;
  private long gereservedTot;
  private boolean verkocht;

  protected Artikel() {}

  public Artikel(
          String code,
          String filiaalnaam) {
    this(code, filiaalnaam, null, 0, false);
  }

  public Artikel(
      String code,
      String filiaalnaam,
      String gereservedDoor,
      long gereservedTot,
      boolean verkocht) {
    this.code = code;
    this.filiaalnaam = filiaalnaam;
    this.gereservedDoor = gereservedDoor;
    this.gereservedTot = gereservedTot;
    this.verkocht = verkocht;
  }

  public void update(Artikel newValues) {
    code = newValues.getCode();
    filiaalnaam = newValues.getFiliaalnaam();
    gereservedDoor = newValues.getGereservedDoor();
    gereservedTot = newValues.getGereservedTot();
    verkocht = newValues.isVerkocht();
  }

  public Long getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getFiliaalnaam() {
    return filiaalnaam;
  }

  public void setFiliaalnaam(String filiaalnaam) {
    this.filiaalnaam = filiaalnaam;
  }

  public String getGereservedDoor() {
    return gereservedDoor;
  }

  public void setGereservedDoor(String gereservedDoor) {
    this.gereservedDoor = gereservedDoor;
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
        + code
        + "', filiaalnaam='"
        + filiaalnaam
        + "', gereservedDoor='"
        + gereservedDoor
        + "', gereservedTot="
        + gereservedTot
        + ", verkocht="
        + verkocht
        + '}';
  }
}
