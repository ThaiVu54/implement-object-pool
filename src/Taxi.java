public class Taxi {
    private String name;

    @Override
    public String toString() {
        return "Taxi{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Taxi(String name) {
        this.name = name;
    }

    public Taxi() {
    }
}
