package io.github.fisher2911.kingdoms.kingdom.upgrade;

public enum UpgradeId {

    MAX_CLAIMS("Max Claims"),
    MAX_SPAWNERS("Max Spawners"),
    MAX_HOPPERS_PER_CHUNK("Max Hoppers Per Chunk"),
    MAX_MEMBERS("Max Members"),
    BANK_LIMIT("Bank Limit"),
    MAX_ALLIES("Max Allies"),
    MAX_TRUCES("Max Truces"),
    MAX_ENEMIES("Max Enemies"),

    ;

    private final String displayName;

    UpgradeId(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return this.displayName;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase().replace("_", "-");
    }
}
