# Monopoly Game with Spring Boot

This project is a Monopoly game implemented using Spring Boot. It allows players to create and play the game online with the following features:

## Features Implemented

1. **Create New Game**: Ability for a host (also a player) to create a new game, therefore discarding the old one.
2. **Play with Another Player**: Ability for a host (also a player) to play the game with another player.
3. **Cash System**: Ability for a player to have their cash system, and start off with $1000.
4. **Roll Dice**: Ability for a player to roll 2 dice and be informed of the place landed.
5. **Auto Purchase Place**: Ability to auto purchase a place when landed and informed.
6. **Auto Pay Rent**: Ability to auto pay to the person whose place was landed on and informed.
7. **Start Bonus**: Ability to gain +$200 when the “start” is crossed.
8. **Declare Winner**: Ability to declare who the winner of a game is based on bankruptcy or the player with the highest cash before turn 50.

## Example System Usage

- Person A : [http://localhost:8080/create-game/](http://localhost:8080/create-game/) >> Game Created Successfully
- Person A : [http://localhost:8080/roll-die/p1](http://localhost:8080/roll-die/p1) >> Die rolled 11 and landed on Place ABC, Unclaimed place and hence bought for $200. Remaining balance is $800.
- Person B : [http://localhost:8080/roll-die/p2](http://localhost:8080/roll-die/p2) >> Die rolled 4 and landed on Place DEF, Unclaimed place and hence bought for $150. Remaining balance is $850.
- ...

## Non-Functional Features

1. **Database Data Persistence**: Data persistence ensures that even if the Spring application is restarted, the game can be played uninterrupted.
2. **Design Patterns**: MVC design pattern is used for a highly maintainable and readable code base.
3. **Documentation**: Documentation ensures a highly maintainable and readable code base.

## Git Usage

- Commit often with descriptive messages. Use a branch per feature to simulate team collaboration.

## Extensibility

Additional features implemented should be clearly stated in `guide.txt`.

## Checklist

- [ ] Diagrammed the database schema.
- [ ] (Bonus) Diagrammed the system design.
- [x] Completed a minimal working model with documentation of the required features.
- [x] Improvise working model to incorporate appropriate design/architectural patterns.
- [ ] Perform unit testing.
- [ ] Create and Push `guide.txt`.
- [ ] Create and Push `decisions.txt`.
- [ ] Upload and Push `database_schema.png`.
- [ ] (Bonus) Upload and Push `system_design.png`.
- [x] Added User Interface to play and display messages.

## Contributors
- Vivekanand Vernekar
- Dino James

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
