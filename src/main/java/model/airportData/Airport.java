package model.airportData;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Entity
public class Airport {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 @Column(name = "id", nullable = false)
 private int id;
 @Column(name = "city_code")
 private final String cityCode;
 @Column(name = "country_code")
 private final String countryCode;
 @OneToOne(cascade = CascadeType.ALL)
 private final NameTranslation nameTranslation;
 @Column(name = "time_zone")
 private final String timeZone;
 private final Boolean flightable;
 @OneToOne(cascade = CascadeType.ALL)
 private final Coordinates coordinates;
 private final String name;
 private final String code;
 @Column(name = "iata_type")
 private final String iataType;

}
