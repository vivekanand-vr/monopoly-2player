import React from 'react';
import { render, fireEvent, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import MonopolyGame from '../MonoployGame';

describe('MonopolyGame Component', () => {
    // Testing for initial render
    it('Renders the component with initial state', async () => {
        const { getByText } = render(<MonopolyGame />);

        expect(getByText('Welcome to Monopoly Game')).toBeInTheDocument();
        expect(getByText('Create New Game')).toBeInTheDocument();

        // Simulate clicking the "Create New Game" button
        const button = screen.getByText(/Create New Game/i); 
        fireEvent.click(button);     
    });
});
