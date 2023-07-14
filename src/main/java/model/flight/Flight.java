package model.flight;

import jakarta.persistence.*;
import lombok.*;
import model.airportData.Airport;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Entity
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private final String number;
    private final LocalDate date;
    private final String time;
    @Column(name = "flight_time")
    private final Double flightTime;
    @ManyToOne
    @JoinColumn(name = "airport_from_id")
    private final Airport airportFrom;
    @ManyToOne
    @JoinColumn(name = "airport_to_id")
    private final Airport airportTo;
    private final Double price;

    public String toString() {
        return number.concat(" ").concat(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).concat(" ")
                .concat(time).concat(" ").concat(String.valueOf(flightTime).endsWith(".0")
                        ? String.valueOf(Math.round(flightTime)) : String.valueOf(flightTime)).concat(" ")
                .concat(airportFrom.getCode()).concat(" ").concat(airportTo.getCode()).concat(" ")
                .concat(String.valueOf(price).endsWith(".0") ? String.valueOf(Math.round(price))
                        : String.valueOf(price));
    }
}
