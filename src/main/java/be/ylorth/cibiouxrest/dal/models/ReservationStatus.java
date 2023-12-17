package be.ylorth.cibiouxrest.dal.models;

public enum ReservationStatus {
    ACCEPTE("acceptée"),
    EN_ATTENTE("en attente"),
    REFUSE("refusée");

    private final String status;

    ReservationStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
