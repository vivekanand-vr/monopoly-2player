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
        try {
            const response = await axios.post(
                currentPlayer === 'A' ? 'http://localhost:9999/Monopoly/roll-die/p1' : 'http://localhost:9999/Monopoly/roll-die/p2'
            );
            setTransactionMessage(response.data);
            const cashResponse = await axios.get('http://localhost:9999/Monopoly/get-cash-details');
            setCashA(cashResponse.data.cashA);
            setCashB(cashResponse.data.cashB);

            setCurrentPlayer(currentPlayer === 'A' ? 'B' : 'A');
            setTurnMessage(`Player ${currentPlayer === 'A' ? 'A' : 'B'} made the move and now turn ${currentPlayer === 'A' ? 'B' : 'A'}`);
        } 
        catch (error) {
            console.error(`Error making a move for player ${currentPlayer === 'A' ? 'A' : 'B'}`, error);
        }
    };

    return (
        <div className="bg-[black] max-w-[1000px] text-center pb-4 mt-10 m-auto rounded-[20px] border-[solid]">
            <h1 className='text-white px-2 font-jersey-25 text-[50px] pt-4 mb-4 mt-5 text-shadow-md'>Welcome to Monopoly Game</h1>
            <button className='text-xl font-semibold bg-[#ff6700] text-white rounded cursor-pointer mb-4 px-5 py-2.5 border-[none] hover:bg-[#0056b3]' 
                    onClick={handleNewGameClick}>Create New Game</button>

            {gameStarted && (
                <div className="flex justify-between mb-4">
                    <div className="flex-1 bg-[#fff4e4] rounded shadow-[0_2px_4px_rgba(0,0,0,0.1)] mx-4 my-0 p-2 px-4">
                        <h2 className='text-5xl font-jersey-25 mb-2'>Player A</h2>
                        <p  className='pb-2'>Cash: <span id="cashA">${cashA}</span></p>
                        <button className='text-xl font-semibold bg-[#ff6700] text-white rounded cursor-pointer mb-2 px-5 py-2.5 border-[none] hover:bg-[#0056b3]' 
                                onClick={play} disabled={currentPlayer !== 'A'}>Roll Dice</button>
                    </div>
                    <div className="flex-1 bg-[#fff4e4] rounded shadow-[0_2px_4px_rgba(0,0,0,0.1)] mx-4 my-0 p-2 px-4">
                        <h2 className='text-5xl font-jersey-25 mb-2'>Player B</h2>
                        <p  className='pb-2'>Cash: <span id="cashB">${cashB}</span></p>
                        <button className='text-xl font-semibold bg-[#ff6700] text-white rounded cursor-pointer mb-2 px-5 py-2.5 border-[none] hover:bg-[#0056b3]'
                                onClick={play} disabled={currentPlayer !== 'B'}>Roll Dice</button>
                    </div>
                </div>
            )}
            { gameStarted && <div className="text-[white] text-[large] mb-4">{turnMessage}</div> }
            { gameStarted && <div className="bg-[#0a704e] text-[25px] font-poppins text-white mx-3 pb- p-3 rounded-[10px] border border-white border-solid">{transactionMessage}</div> }
        </div>
    );
}

export default MonopolyGame;
