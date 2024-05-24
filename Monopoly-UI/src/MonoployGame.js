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

    const play = async () => {
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
        <div className='flex justify-center'>
            <div className="bg-black w-[1000px] text-center pb-4 my-10 mx-4 rounded-2xl border-solid border h-fit">
                <h1 className='text-white px-2 font-jersey-25 text-3xl md:text-6xl mb-4 mt-5 text-shadow-md'>Welcome to Monopoly Game</h1>
                <button className='text-lg md:text-xl font-semibold bg-[#ff6700] text-white rounded cursor-pointer mb-4 px-3 md:px-5 py-2 md:py-2.5 border-none hover:bg-[#0056b3]' 
                        onClick={handleNewGameClick}>Create New Game</button>

                {gameStarted && (
                    <div className="flex flex-col md:flex-row justify-between mb-4">
                        <div className="flex-1 bg-[#fff4e4] rounded shadow-md mx-4 my-2 md:my-0 p-2 px-4">
                            <h2 className='text-3xl md:text-5xl font-jersey-25 mb-2'>Player A</h2>
                            <p className='pb-2'>Cash: <span id="cashA">${cashA}</span></p>
                            <button className='text-lg md:text-xl font-semibold bg-[#ff6700] text-white rounded cursor-pointer mb-2 px-3 md:px-5 py-2 md:py-2.5 border-none hover:bg-[#0056b3]' 
                                    onClick={play} disabled={currentPlayer !== 'A'}>Roll Dice</button>
                        </div>
                        <div className="flex-1 bg-[#fff4e4] rounded shadow-md mx-4 my-2 md:my-0 p-2 px-4">
                            <h2 className='text-3xl md:text-5xl font-jersey-25 mb-2'>Player B</h2>
                            <p className='pb-2'>Cash: <span id="cashB">${cashB}</span></p>
                            <button className='text-lg md:text-xl font-semibold bg-[#ff6700] text-white rounded cursor-pointer mb-2 px-3 md:px-5 py-2 md:py-2.5 border-none hover:bg-[#0056b3]'
                                    onClick={play} disabled={currentPlayer !== 'B'}>Roll Dice</button>
                        </div>
                    </div>
                )}
                { gameStarted && <div className="text-white text-base md:text-lg mb-4">{turnMessage}</div> }
                { gameStarted && <div className="flex-1 md:flex-none bg-[#0a704e] text-lg md:text-xl font-poppins text-white mx-3 py-2 md:pb-3 px-3 md:px-4 rounded-xl border border-white border-solid">{transactionMessage}</div> }
            </div>
        </div>
    );
}

export default MonopolyGame;
