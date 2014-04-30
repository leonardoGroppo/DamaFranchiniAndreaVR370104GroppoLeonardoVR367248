package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import model.Board;
import controller.GameController;

/**
 * Classe che gestisce graficamente la dama
 * @author id164pff
 *
 */
public class Game extends JPanel{
	
	// VARIABILI D'ISTANZA 
	
	private int numberOfClick; 			// conta il numero di click
	private JPanel thisGame;  			// pannelllo contenente il gioco
	private GameController controller;	// controllore del gioco
	private Board board;				// damiera (NON DOVREBBE ESSERCI!!!!!)
	private GraphicInterface graphic;	// interfaccia grafica
	
	private int srcX;					// ascissa del bottone in cui si trova la pedina da muovere
	private int srcY;					// ordinata del bottone in cui si trova la pedina da muovere
	private int destX;					// ascissa del bottone in cui voglio che la pedina(o dama) si muova
	private int destY;					// ordinata del bottone in cui voglio che la pedina(o dama) si muova


	public Game(Board board, GameController controller, GraphicInterface graphic) {

		this.board = board; // DEVI LEVARLO!!!	
		this.controller = controller;
		this.graphic = graphic;
		
		numberOfClick = 0;
		
		setLayout();

		repaint(this.board.getBoard());
		
		thisGame = this;

	}

	/**
	 * Aggiornamento a livello grafico della damiera
	 * @param board: struttra dati rappresentate la damiera, sulla quale ci si basa per effettuare l'aggiornamento grafico
	 */
	public void repaint(int board[][]){
		
		this.removeAll(); // rimuovo tutti componenti dal JPanel
		
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				
				Button b = new Button(i,j); // creo un nuovo bottone con coordinate (i,j)
				
				switch(board[i][j]){		// in base al valore della matrice che sto analizzando
			
					case 0: // in caso di cella  bianca vuota
							b.setIcon(new ImageIcon("images/white_cell.png")); // imposta icona bianca vuota
							setListener(b);									  // imposto il listenere del bottone 
							add(b);											  // aggiungo il bottone al JPanel
							break;
					case 1: // in caso di cella nera vuota
							b.setIcon(new ImageIcon("images/black_cell.png"));
							setListener(b);
							add(b);
							break;
					case 2: // in caso di cella con pedina bianca
							b.setIcon(new ImageIcon("images/white_pawn.png"));
							setListener(b);
							add(b);
							break;
					case 3: // in caso di una cella con pedina nera
							b.setIcon(new ImageIcon("images/black_pawn.png"));
							setListener(b);
							add(b);
							break;
					case 4: // in caso di una cella con dama bianca
							b.setIcon(new ImageIcon("images/white_lady.png"));
							setListener(b);
							add(b);
							break;
					case 5: // in caso di una cella con dama nera
							b.setIcon(new ImageIcon("images/black_lady.png"));
							setListener(b);
							add(b);
							break;
					default: ;
				}
				
			}
		} //fine for
	}
	
	// METODI SET
	
	/**
	 * Imposto il layout del gioco come un grid layout(la griglia è perfetta per simulare una scacchiera)
	 */
	private void setLayout() {
		this.setLayout(new GridLayout(8, 8));
	}
	
	/**
	 * Imposto l'ascoltatore di eventi
	 * @param b: bottone su cui deve agire il listener
	 */
	private void setListener(Button b){
		
		final Button tmp = b; // creo una copia final del bottone
		
		// imposto il listener
		b.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				
				if(board.isGameInProgress()){		// nel caso in cui sia in corso una partita
				
					numberOfClick++;				// incremento il numero di click effettuati
				
					switch(numberOfClick) {			// in base al numero di click
				
						case 1: // se è il primo click
								srcX = tmp.getMyX();					// acquisisco ascissa del bottone cliccato
								srcY = tmp.getMyY();					// acquisisco ordinata del bottone cliccato
								graphic.getMenu().setIllegalLabel(""); 	// imposto a vuota l'etichetta che inidica se la mossa è illegale 
																		// o meno
								break;
								
						case 2: // se è il secondo click
								destX = tmp.getMyX(); // acquisisco ascissa del bottone in cui voglio che il pezzo si sposti
								destY = tmp.getMyY(); // acquisisco ordinata del bottone in cui voglio che il pezzo si sposti
								controller.move(srcX, srcY, destX, destY); // chiamo il metodo move del controllore
								numberOfClick = 0;	 // aazzero il numero di click effettuati
								break;
						default: ;
					}
				}
			}
		});
		
	}
	
}
