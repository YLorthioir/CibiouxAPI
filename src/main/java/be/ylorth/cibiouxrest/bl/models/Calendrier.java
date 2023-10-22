package be.ylorth.cibiouxrest.bl.models;

import java.time.LocalDate;
import java.util.Set;

public record Calendrier (Set<LocalDate> datesReservees,Set<LocalDate> datesFermetures){
}
