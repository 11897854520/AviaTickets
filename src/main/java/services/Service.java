package services;

import dao.Dao;
import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import model.airportData.Airport;
import model.airportData.Coordinates;
import model.airportData.NameTranslation;
import model.flight.Flight;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Service {

    public Service() throws FileNotFoundException, JsonException {
        createAirportDataBase();
    }

    private void createAirportDataBase() throws FileNotFoundException, JsonException {
        if (Dao.findAllAirports().isEmpty()) {
            FileReader fileReader = new FileReader("src/main/resources/airports.json");
            JsonArray airportsArray = (JsonArray) Jsoner.deserialize(fileReader);
            airportsArray.forEach(o -> {
                JsonObject object = (JsonObject) o;
                JsonObject jsonObjectCoordinates = (JsonObject) object.get("coordinates");
                Coordinates coordinates = new Coordinates((BigDecimal) jsonObjectCoordinates.get("lat")
                        , (BigDecimal) jsonObjectCoordinates.get("lon"));
                JsonObject jsonObjectTranslation = (JsonObject) object.get("name_translations");
                NameTranslation nameTranslation = new NameTranslation((String) jsonObjectTranslation.get("en"));
                Dao.save(new Airport((String) object.get("city_code"), (String) object.get("country_code")
                        , nameTranslation, (String) object.get("time_zone"), (Boolean) object.get("flightable")
                        , coordinates, (String) object.get("name"), (String) object.get("code")
                        , (String) object.get("iata_type")));
            });
        }
    }

    private String showMainMenu() {
        return System.lineSeparator()
                .concat("Главное меню: ")
                .concat(System.lineSeparator())
                .concat(System.lineSeparator())
                .concat("1 - ввод рейса")
                .concat(System.lineSeparator())
                .concat("2 - вывод всех рейсов")
                .concat(System.lineSeparator())
                .concat("3 - поиск рейса по номеру")
                .concat(System.lineSeparator())
                .concat("0 - завершение работы")
                .concat(System.lineSeparator());
    }

    public void console() {
        System.out.println("Сервис поиска авиабилетов");
        String error = "Некорректный номер пункта меню!!!".concat(System.lineSeparator());
        AtomicBoolean stop = new AtomicBoolean(false);
        while (!stop.get()) {
            try {
                Scanner scanner = new Scanner(System.in);
                int operationNumber;
                System.out.println(showMainMenu());
                System.out.println("Введите номер пункта меню: ");
                operationNumber = scanner.nextInt();
                if (operationNumber > 3 || operationNumber < 0) {
                    System.out.println(error);
                    continue;
                }
                switchOperationNumber(scanner, operationNumber, stop);
            } catch (Exception e) {
                System.out.println(error);
            }
        }
    }

    private void switchOperationNumber(Scanner scanner, int operationNumber, AtomicBoolean stop) {
        switch (operationNumber) {
            case (0):
                System.out.println("Работа закончена");
                stop.set(true);
                break;
            case (1):
                addFlight(scanner);
                break;
            case (2):
                showAllFlights();
                break;
            case (3):
                showConcreteFlight(scanner);
                break;
        }
    }

    private void addFlight(Scanner scanner) {
        String flightNumber;
        String flightDate;
        String flightTakeOffTime;
        String timeOfFlight;
        String airportFrom;
        String airportTo;
        String price;
        List<String> codes = new ArrayList<>();
        System.out.println(System.lineSeparator().concat("Введите данные рейса"));
        flightNumber = createString(scanner, 1, codes);
        flightDate = createString(scanner, 2, codes);
        flightTakeOffTime = createString(scanner, 3, codes);
        timeOfFlight = createString(scanner, 4, codes);
        airportFrom = createString(scanner, 5, codes);
        airportTo = createString(scanner, 6, codes);
        price = createString(scanner, 7, codes);
        LocalDate date = LocalDate.parse(flightDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        double flightTime = Double.parseDouble(timeOfFlight);
        double flightPrice = Double.parseDouble(price);
        Flight flight = new Flight(flightNumber, date, flightTakeOffTime, flightTime,
                Dao.findByCode(airportFrom), Dao.findByCode(airportTo), flightPrice);
        Dao.save(flight);
        System.out.println(System.lineSeparator().concat("Информация о рейсе ")
                .concat(flight.toString()).concat("*").concat(" ")
                .concat("добавлена"));
    }

    private String createString(Scanner scanner, int kindOfString, List<String> codes) {
        AtomicReference<String> regex = new AtomicReference<>();
        AtomicReference<String> error = new AtomicReference<>();
        AtomicReference<String> start = new AtomicReference<>();
        String enterMore = "Введите заново: ";
        switchString(kindOfString, regex, error, start);
        scanner = new Scanner(System.in);
        System.out.println(start);
        while (true) {
            String returnString = scanner.nextLine();
            if (kindOfString == 7 && Double.parseDouble(returnString) <= 0) {
                System.out.println(error);
                System.out.println(enterMore);
            } else if (((kindOfString == 5 || kindOfString == 6)
                    && !Dao.findAllAirports().contains(returnString.toUpperCase()))) {
                System.out.println("Такого аэропорта не существует!!!");
                System.out.println(enterMore);
            } else if ((kindOfString == 6)
                    && codes.contains(returnString.toUpperCase())) {
                System.out.println("Код аэропорта прибытия не может соответствовать коду" +
                        "аэропорта вылета!!!");
                System.out.println(enterMore);
            } else if (kindOfString == 1 && Dao.findByNumber(returnString) != null) {
                System.out.println("рейс с таким номером уже существует!!!");
                System.out.println(enterMore);
            } else if (returnString.matches(regex.get())) {
                codes.add(kindOfString == 5 ? returnString.toUpperCase() : "");
                return returnString.toUpperCase();
            } else {
                System.out.println(error);
                System.out.println(enterMore);
            }
        }
    }

    private void switchString(int kindOfString, AtomicReference<String> regex, AtomicReference<String> error
            , AtomicReference<String> start) {
        switch (kindOfString) {
            case (1):
                regex.set("\\w{4}");
                error.set("Некорректный ввод номера рейса");
                start.set("XXXX - Введите номер рейса:");
                break;
            case (2):
                regex.set("(((0[1-9]|[12][0-9]|3[01])([/])(0[13578]|10|12)([/])(\\d{4}))|(([0][1-9]" +
                        "|[12][0-9]|30)([/])(0[469]|11)([/])(\\d{4}))|((0[1-9]|1[0-9]|2[0-8])([/])" +
                        "(02)([/])(\\d{4}))|((29)(\\/)(02)([/])([02468][048]00))|((29)([/])(02)([/])" +
                        "([13579][26]00))|((29)([/])(02)([/])([0-9][0-9][0][48]))|((29)([/])(02)([/])" +
                        "([0-9][0-9][2468][048]))|((29)([/])(02)([/])([0-9][0-9][13579][26])))");
                error.set("Некорректный ввод даты вылета");
                start.set("ДД/ММ/ГГГГ - дата рейса:");
                break;
            case (3):
                regex.set("[0-2][\\d][:][0-5][\\d]");
                error.set("Некорректный ввод времени вылета");
                start.set("ЧЧ:ММ - время вылета:");
                break;
            case (4):
                regex.set("[\\d]+([.][0-5][\\d]?+)?");
                error.set("Некорректный ввод длительности перелета");
                start.set("XX.XX - длительность перелета:");
                break;
            case (5):
                regex.set("\\w{3}");
                error.set("Некорректный ввод кода аэропорта");
                start.set("XXX - аэропорт вылета:");
                break;
            case (6):
                regex.set("\\w{3}");
                error.set("Некорректный ввод кода аэропорта");
                start.set("XXX - аэропорт назначения:");
                break;
            case (7):
                regex.set("[\\d]+([.][\\d]+)?");
                error.set("Некорректный ввод стоимости перелета");
                start.set(".XX - стоимость билета (> 0):");
                break;
        }
    }

    private void showConcreteFlight(Scanner scanner) {
        scanner = new Scanner(System.in);
        System.out.println("Введите номер рейса в формате ХХХХ: ");
        String name = scanner.nextLine();
        Flight flight = Dao.findByNumber(name);
        System.out.println(flight != null ? "Информация о рейсе: ".concat(flight.toString())
                : "Рейс ".concat(name).concat(" не найден"));
    }

    private void showAllFlights() {
        if (!Dao.findAllFlights().isEmpty()) {
            Dao.findAllFlights().forEach(flight -> System.out.println("Информация о рейсе: "
                    .concat(flight.toString())));
        } else {
            System.out.println("Информация о рейсах отсутствует");
        }
    }
}
