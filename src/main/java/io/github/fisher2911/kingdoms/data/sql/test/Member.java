package io.github.fisher2911.kingdoms.data.sql.test;

public class Member {

    private final String name;
    private final double balance;

    public Member(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "Member{" +
                "name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
