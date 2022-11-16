package src;

public class Pair<U, V> {
    U first;
    V second;

    public static <U, V> Pair<U, V> of(U first, V second) {
        return new Pair<>(first, second);
    }

    public Pair(U first, V second) {
        this.first = first;
        this.second = second;
    }
}