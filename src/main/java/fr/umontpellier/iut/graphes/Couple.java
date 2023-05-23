package fr.umontpellier.iut.graphes;

public class Couple {
    private Integer previous;
    private Integer distance;

    public Couple() {
        this.distance = Integer.MAX_VALUE;
        this.previous = null;
    }

    public Couple(Integer distance) {
        this.distance = distance;
    }

    public Couple(Integer previous, Integer distance) {
        this.previous = previous;
        this.distance = distance;
    }

    public Integer getPrevious() {
        return previous;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setPrevious(Integer previous) {
        this.previous = previous;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }
}
