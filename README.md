# Dice: Bridge + Adapter

Tabletop-RPG dice rolls (what we roll) separated from sources of randomness (where numbers come from).

## Build and test (single command)
    mvn test

## Run the demo
    mvn -q compile exec:java -Dexec.mainClass=dice.app.DiceApp
or compile manually and run `dice.app.DiceApp`.

Input examples: `2d6+3`, `adv @tower`, `1d20 @secure`, `jam`, `unjam`, `quit`.

## Structure
| Role | Class |
|---|---|
| Abstraction | `Roll` |
| Refined Abstractions | `DamageRoll`, `AdvantageRoll` |
| Implementor | `RandomSource` |
| Concrete Implementors | `PseudoRandomSource`, `SecureRandomSource`, `DiceTowerAdapter` |
| Adaptee (not modified) | `DiceTowerDevice` |
| Complexity module | Dynamic implementor selection: `RollCommandFactory` |
