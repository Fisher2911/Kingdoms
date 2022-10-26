# Upgrades

[Example File](https://github.com/Fisher2911/Kingdoms/blob/master/src/main/resources/kingdom-defaults/upgrades.yml)

## Format
```yaml
# There are some built-in upgrades that require specific ids, such as:

# max-claims
# max-members
# bank-limit
# max-allies
# max-truces
# max-enemies

# Others, such as the potion type, can have as many as you want

# This has to be at the top
upgrades:
  # Arbitrary number, not used for anything
  1:
    # the type of upgrade, options are int, double, and potion (for now)
    type: int
    id: max_claims
    display-name: "The name to display"
    expression: "10 level * 5" # the equation used to calculate the value at each level
    money-price-expression: "level * 100" # the equation used to calculate the price at each level
    max-level: 10 # the max level the upgrade can be
    display-item: # what the item looks like in the display when it can still be leveled up
      material: GRASS_BLOCK
      name: '<blue>%upgrade_display_name%'
      lore:
        - ""
        - "<green>Value: %upgrade_display_value%"
        - "<red>Cost: $%upgrade_display_price%"
    max-level-item: # what the item looks like in the display when it is at the max level
      material: BARRIER
      name: '<blue>Max Claims'
      lore:
        - ""
        - "<gray>Value: <gold>%upgrade_display_value%"
        - "<red>Max Level!"
  2:
    type: potion
    id: some_id
    display-name: '<blue>Resistance'
    expression: "level - 1" # Resistance is 1 per level, potion effects start at 0
    money-price-expression: "level * 100" # same as above
    max-level: 3
    display-item:
      material: ANVIL
      name: '<blue>%upgrade_display_name%'
      lore:
        - ""
        - "<green>Value: %upgrade_display_value%"
        - "<red>Cost: $%upgrade_display_price%"
    max-level-item:
      material: BARRIER
      name: '<blue>Some name'
      lore:
        - ""
        - "<gray>Value: <gold>%upgrade_display_value%"
        - "<red>Max Level!"
    potion-effect-types: # found at (https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html)
      - RESISTANCE
    # who the potion effect applies to
    applies-to:
      - ALLY
      - TRUCE
    applies-to-self: true # whether or not the potion effect applies to the kingdom members
```

## Roles

Roles are pretty self-explanatory, they can be found [here](https://github.com/Fisher2911/Kingdoms/blob/master/src/main/resources/kingdom-defaults/roles.yml)

## [Permissions GUI](https://github.com/Fisher2911/Kingdoms/blob/master/src/main/resources/guis/gui-display-items.yml)

```yaml
# this has to be at the top
permission-items:
  # this will be applied to all permissions that are not specified below
  default-item:
    item:
      material: "PAPER"
      amount: 1
      name: "%upgrade_display_value"
      lore:
        - ""
        - "%upgrade_display_cost"
        - "%upgrade_display_value"
    # the actions when it is clicked
    actions:
      # options are: [swap_value, previous_page, next_page, none]
      swap_value:
        # which click type the action should be applied to
        click-types: ["RIGHT"]
      previous_page:
        click-types: ["LEFT"]
        
# this will be the same format above, except this must be a number, which specifies the slot in the GUI
# the slot can be any number, the GUI will become paginated if it is filled up
  1:
    item:
      material: "PAPER"
      amount: 1
      name: "%upgrade_display_value"
      lore:
        - ""
        - "%upgrade_display_cost"
        - "%upgrade_display_value"
    # this can be found at https://github.com/Fisher2911/Kingdoms/blob/47a6b3d1ca70ad249d3a90b9f1a27b6e2e3d2f11/src/main/java/io/github/fisher2911/kingdoms/kingdom/permission/KPermission.java#L20
    permission: "breakblock"
    # the actions when it is clicked
    actions:
      # options are: [swap_value, previous_page, next_page, none]
      swap_value:
        # which click type the action should be applied to
        click-types: ["RIGHT"]
      previous_page:
        click-types: ["LEFT"]
```