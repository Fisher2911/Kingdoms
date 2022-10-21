package io.github.fisher2911.kingdoms.economy;

import java.util.EnumMap;
import java.util.Map;

public class PriceManager {

    private final Map<PriceType, Price> priceMap = new EnumMap<>(PriceType.class);

    public PriceManager() {
    }

    public Price getPrice(PriceType priceType) {
        return this.priceMap.getOrDefault(priceType, Price.IMPOSSIBLE);
    }

    public Price getPrice(PriceType priceType, Price def) {
        return this.priceMap.getOrDefault(priceType, def);
    }

    public void setPrice(PriceType priceType, Price price) {
        this.priceMap.put(priceType, price);
    }
}
