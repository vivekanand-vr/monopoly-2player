import React, { useState } from 'react';
import axios from 'axios';

function MonopolyGame() {
    
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
            setTurnMessage('Game Starts with player A, roll the dice');
            setTransactionMessage(response.data);
            setGameStarted(true);
        } catch (error) {
            console.error('Error creating new game:', error);
        }
    };

    const play =  async () => {
        // Switching players and updating turn messages
        try {
            // Make the API call to perform the transaction
            const response = await axios.post(
                currentPlayer === 'A' ? 'http://localhost:9999/Monopoly/roll-die/p1' : 'http://localhost:9999/Monopoly/roll-die/p2'
            );
            setTransactionMessage(response.data);

            // Fetch updated cash details of both players after the transaction
            const cashResponse = await axios.get('http://localhost:9999/Monopoly/get-cash-details');
            setCashA(cashResponse.data.cashA);
            setCashB(cashResponse.data.cashB);

            // Change turn
            setCurrentPlayer(currentPlayer === 'A' ? 'B' : 'A');
            setTurnMessage(`Player ${currentPlayer === 'A' ? 'A' : 'B'} made the move and now turn ${currentPlayer === 'A' ? 'B' : 'A'}`);
        } 
        catch (error) {
            console.error(`Error making a move for player ${currentPlayer === 'A' ? 'A' : 'B'}`, error);
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
                        <button onClick={play} disabled={currentPlayer !== 'A'}>Roll Dice</button>
                    </div>
                    <div className="player" id="playerB">
                        <h2>Player B</h2>
                        <p>Cash: <span id="cashB">${cashB}</span></p>
                        <button onClick={play} disabled={currentPlayer !== 'B'}>Roll Dice</button>
                    </div>
                </div>
            )}
            { gameStarted && <div id="turnMessage">{turnMessage}</div> }
            { gameStarted && <div id="transactionMessage">{transactionMessage}</div> }
        </div>
    );
}

export default MonopolyGame;
