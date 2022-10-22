package io.github.fisher2911.kingdoms.kingdom.upgrade;

public enum UpgradeId {

    MAX_CLAIMS("Max Claims"),
    MAX_SPAWNERS("Max Spawners"),
    MAX_HOPPERS_PER_CHUNK("Max Hoppers Per Chunk"),
    MAX_MEMBERS("Max Members")

    ;

    private String displayName;

    UpgradeId(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return this.displayName;
    }

}
