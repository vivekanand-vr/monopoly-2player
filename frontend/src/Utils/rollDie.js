function rollDie() {
    // Generates random number from 1 to 6, we need to roll 2 die 
    let die1 =  Math.floor(Math.random() * 6) + 1;
    let die2 =  Math.floor(Math.random() * 6) + 1;
    return die1 + die2;
}
module.exports = rollDie;
