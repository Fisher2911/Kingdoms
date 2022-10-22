package io.github.fisher2911.kingdoms.economy;

import io.github.fisher2911.kingdoms.user.User;

public interface Price {

    Price FREE = new Price() {
        @Override
        public boolean canAfford(User user) {
            return true;
        }

        @Override
        public void pay(User user) {

        }

        @Override
        public boolean payIfCanAfford(User user) {
            return true;
        }

        @Override
        public String getDisplay() {
            return "Free";
        }
    };

    Price IMPOSSIBLE = new Price() {
        @Override
        public boolean canAfford(User user) {
            return false;
        }

        @Override
        public void pay(User user) {

        }

        @Override
        public boolean payIfCanAfford(User user) {
            return false;
        }

        @Override
        public String getDisplay() {
            return "Unaffordable";
        }
    };

    static Price money(double cost) {
        return new MoneyPrice(cost);
    }

    boolean canAfford(User user);
    void pay(User user);
    boolean payIfCanAfford(User user);
    String getDisplay();

}
