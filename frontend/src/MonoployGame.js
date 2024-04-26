import React, { useState } from 'react';
import axios from 'axios';

function MonopolyGame() {
    // Make it config driven UI
    const [cashA, setCashA] = useState(1000);
    const [cashB, setCashB] = useState(1000); 
    const [transactionMessage, setTransactionMessage] = useState(''); 
    const [turnMessage, setTurnMessage] = useState('');
    const [currentPlayer, setCurrentPlayer] = useState('');
    const [gameStarted, setGameStarted] = useState(false);

    const handleNewGameClick = async () => {
        try {
            const response = await axios.post('http://localhost:9999/Monopoly/create-game');
            setCurrentPlayer('A');
            setCashA(1000);
            setCashB(1000);
            setTransactionMessage(response.data);
            setGameStarted(true);
        } catch (error) {
            console.error('Error creating new game:', error);
        }
    };

    const handleRollDice = () => {
        try{
            // fetch player details on every roll
            const response = await axios.post('http://localhost:9999/...');
        }    
        // Die will be rolled on the backend
        // Display cash details only

        // Switching players and updatind turn messages
        if (currentPlayer === 'A') {
            // Logic for Player A's turn
            setCurrentPlayer('B');
            setCashA(response.cashA);
            setTurnMessage('Player A die rolled ' + diceNumber);

            //send the data to the backend player and dice number to do the transaction


        } 
        else {
            // Logic for Player B's turn
            setCurrentPlayer('A');
            setCashA(response.cashA);
            setTurnMessage('Player B die rolled ' + diceNumber);

            // send the data to the backend (player name and dice number for transaction)
        }
    };

    return (
        <div className="container">
            <h1>Welcome to Monopoly Game</h1>
            <button onClick={handleNewGameClick}>Create New Game</button>

            {gameStarted && (
                <div className="players">
                    <div className="player" id="playerA">
                        <h2>Player A</h2>
                        <p>Cash: <span id="cashA">${cashA}</span></p>
                        <button onClick={handleRollDice} disabled={currentPlayer !== 'A'}>Roll Dice</button>
                    </div>
                    <div className="player" id="playerB">
                        <h2>Player B</h2>
                        <p>Cash: <span id="cashB">${cashB}</span></p>
                        <button onClick={handleRollDice} disabled={currentPlayer !== 'B'}>Roll Dice</button>
                    </div>
                </div>
            )}

            <div id="turnMessage">{turnMessage}</div>
            <div id="transactionMessage">{transactionMessage}</div>
        </div>
    );
}

export default MonopolyGame;
