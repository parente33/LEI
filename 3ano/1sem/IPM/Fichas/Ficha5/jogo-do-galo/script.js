document.addEventListener('DOMContentLoaded', function() {
    const gameBoard = document.getElementById('gameBoard');
    const resetButton = document.getElementById('resetButton');
    const statusElement = document.getElementById('status');
    
    let currentPlayer = 'X';
    let gameBoardState = Array(9).fill('');
    let gameOver = false;
    
    for (let i = 0; i < 9; i++) {
        const cell = document.createElement('div');
        cell.className = 'cell';
        cell.dataset.index = i;
        
        cell.addEventListener('click', () => handleCellClick(i));
        
        gameBoard.appendChild(cell);
    }
    
    function handleCellClick(index) {
        if (gameOver) {
            alert("Jogo Terminado! Faça reset para recomeçar.");
            return;
        }
        
        if (gameBoardState[index] !== '') {
            alert("Célula já preenchida");
            return;
        }
        
        gameBoardState[index] = currentPlayer;
        
        const cell = document.querySelector(`.cell[data-index="${index}"]`);
        cell.textContent = currentPlayer;
        
        // Verifica se há um vencedor
        const winner = checkWinner();
        if (winner) {
            gameOver = true;
            if (winner === 'tie') {
                alert("Empate");
                statusElement.textContent = "Jogo Terminado! Empate";
            } else {
                alert(`Jogador ${winner} venceu!`);
                statusElement.textContent = `Jogo Terminado! Vencedor: ${winner}`;
            }
            return;
        }
        
        // Alterna jogador
        currentPlayer = currentPlayer === 'X' ? 'O' : 'X';
        statusElement.textContent = `Vez do jogador: ${currentPlayer}`;
    }
    
    // Função para verificar se há um vencedor
    function checkWinner() {
        const winPatterns = [
            [0, 1, 2], [3, 4, 5], [6, 7, 8], // Linhas
            [0, 3, 6], [1, 4, 7], [2, 5, 8], // Colunas
            [0, 4, 8], [2, 4, 6]             // Diagonais
        ];
        
        for (const pattern of winPatterns) {
            const [a, b, c] = pattern;
            if (gameBoardState[a] && 
                gameBoardState[a] === gameBoardState[b] && 
                gameBoardState[a] === gameBoardState[c]) {
                return gameBoardState[a];
            }
        }
        
        // Verificar empate
        if (gameBoardState.every(cell => cell !== '')) {
            return 'tie';
        }
        
        return null;
    }
    
    function resetGame() {
        gameBoardState = Array(9).fill('');
        currentPlayer = 'X';
        gameOver = false;
        
        document.querySelectorAll('.cell').forEach(cell => {
            cell.textContent = '';
        });
        
        statusElement.textContent = `Vez do jogador: ${currentPlayer}`;
    }
    
    resetButton.addEventListener('click', resetGame);
});