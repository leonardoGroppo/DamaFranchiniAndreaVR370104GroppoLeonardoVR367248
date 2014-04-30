package controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.*;

import view.*;
import model.*;

/**
 * Controllore del gioco: valuta le mosse e la loro correttezza
 * @author Andrea Franchini & Leonardo Groppo
 *
 */

public class GameController {
	
	// VARIABILI D'ISTANZA
	
	protected static Board board; 							// struttura dati della damiera
	protected static GraphicInterface graphic;				// iinterfaccia grafica che rappresenta la damiera
	protected static boolean whiteRound;					// indica se il turno è bianco o nero
	protected static boolean playerVsComputer;				// indica se è in corso una partita tra umano e computer
	protected static ArrayList<int[]> pawnThatCanEat;		// contiene le pedine che possono mangiare
	protected static ArrayList<int[]> pawnInMultipleEat;	// contiene le pedine che possono effettuare una mangiata multipla
	protected static ArrayList<int[]> pawnThatCanMove;		// contiene le pedine che possono muoversi
	protected static int canEatMore;						// indica quante pedine ha mangiato nel corso di una mangiata multipla
	protected static boolean hasEaten;						// indica se nell'ultima mossa ha già mangiato una pedina
	protected static boolean smartAI;						// indica se avviare il livello 1 o il livello 2 dell'intelligenza artificiale

	// METODO COSTRUTTORE
	/**
	 * 
	 * @param b: damiera su cui effettuare i controlli
	 * @param g: interfaccia grafica relativa a quella damiera
	 */
	public GameController(Board b, GraphicInterface g) {

		pawnThatCanEat = new ArrayList<int[]>();
		pawnInMultipleEat = new ArrayList<int[]>();
		board = b;
		graphic = g;
		whiteRound = true;
		canEatMore = 0;
		hasEaten = false;
	}
	
	// METODI GET
	
	/**
	 * 
	 * @return la struttra dati della damiera
	 */
	public Board getBoard() {
		return this.board;
	}
	
	/**
	 * 
	 * @return indica se la partita in corso è contro il computer a livello 1 oppure a livello 2
	 */
	public static boolean isSmartAI() {
		return smartAI;
	}
	
	/**
	 * 
	 * @return restituisce la stringa che indica di chi è il turno
	 */
	public static String getRound() {

		if (whiteRound) {
			return "Bianco";
		} else {
			return "Nero";
		}
	}
	
	/**
	 * 
	 * @return restituisce l'array list contenente le pedine che possono mangiare
	 */
	public ArrayList getPawnThatCanEat() {
		return pawnThatCanEat;
	}
	
	// METODI SET
	
	public static void setSmartAI(boolean smartAI) {
		GameController.smartAI = smartAI;
	}
	
	public void setPlayerVsComputer(boolean b) {
		playerVsComputer = b;
	}


	public void setWhiteRound(boolean b) {
		whiteRound = b;
	}
	
	static void changeTurn(){
		whiteRound = !whiteRound;
		graphic.getMenu().setRoundLabel("Turno: " + getRound());
	}
	
	// PREMESSA: bisogna fare molta attenzionell'uso dei parametri che a volte deve essere inverso perche
	// le ordinate e le ascisse della matrice sono invertite
	// rispetto alle ordinatee alle ascisse dello schermo
	
	
	/**
	 * In base al tipo di partita si occupa di effettuare la mossa
	 * 
	 * @param srcX: ascissa della cella in cui si trova la pedina da muovere
	 * @param srcY: ordinata della cella in cui si trova la pedina da muovere
	 * @param destX: ascissa della cella in cui voglio muovere la pedina
	 * @param destY: ordinata della cella in cui voglio muovere la pedina
	 */
	public void move(int srcX, int srcY, int destX, int destY) {

		if (playerVsComputer) { 						// se la modalità di gioco è umano contro computer
			playerVsComputer(srcX, srcY, destX, destY); // richiama il metodo apposito passandogli le coordinate della mossa
		} else {										// modalità di gioco umano contro umano
			playerVsPlayer(srcX, srcY, destX, destY);   // richiama il metodo apposito passandogli le coordinate della mossa
		}

		graphic.setVisible(true);
	}
	
	/**
	 * 
	 * @param srcX: ascissa della cella in cui si trova la pedina da muovere
	 * @param srcY: ordinata della cella in cui si trova la pedina da muovere
	 * @param destX: ascissa della cella in cui voglio muovere la pedina
	 * @param destY: ordinata della cella in cui voglio muovere la pedina
	 */
	private void playerVsComputer(int srcX, int srcY, int destX, int destY) {
		// 
		playerVsPlayer(srcX, srcY, destX, destY);
		if(!whiteRound)
			artificialIntelligence();

		
	}
	/**
	 * In base al valore di smartAi decide il liello di difficoltà del gioco
	 */
	private void artificialIntelligence(){ 

		if ( smartAI == true){
			SmartAI artificialIntelligence = new SmartAI();
			artificialIntelligence.start();
		}
		else{
			AI artificialIntelligence = new AI();
			artificialIntelligence.start();
		}

	}
	
	/**
	 * 
	 * @param srcX: ascissa della cella in cui si trova la pedina da muovere
	 * @param srcY: ordinata della cella in cui si trova la pedina da muovere
	 * @param destX: ascissa della cella in cui voglio muovere la pedina
	 * @param destY: ordinata della cella in cui voglio muovere la pedina
	 */
	static void playerVsPlayer(int srcX, int srcY, int destX, int destY) {
		
		// se c'è situazione di stallo
		if(checkStallSituation()){
			JOptionPane.showMessageDialog(null, "Stallo: " + getRound() + " perde"); // comunicalo
			endGame(); // fine del gioco
		}
		
		// imposto a false il fatto di aver mangiato qualcosa
		hasEaten = false;

		// se dal turno precedente ho mangiato una pedina
		if (canEatMore > 0 && canEatMore < 3) {

			int[] userCoordinates = new int[4]; // salvo le coordinate passate dall'utente in un array

			userCoordinates[0] = srcX;
			userCoordinates[1] = srcY;
			userCoordinates[2] = destX;
			userCoordinates[3] = destY;
			
			Iterator it = pawnInMultipleEat.iterator(); // costruisco un iteratore sull'arraylist contenente le pedine che possono
														// effettuare una mangiata multipla
			
			boolean equals;								// utilizzata per verificare se la mossa voluta dal giocatore coincide 
														// con le mangiate possibili
			while(it.hasNext()){		
				
				equals = true; 
				
				// uso un array coordinates per salvare la mossa che sto analizzando 
				// tra le varie pedine che possono effettuare una mangiata multipla. Effettuo questo assegnamento
				// per verificare che la mossa che vuole fare l'utente corrisponde con le possibili destinazioni della mangiata 
				// multipla corrente
				int []coordinates = (int[]) it.next(); 
				
				for(int i=0; i<4; i++){
					if(userCoordinates[i] != coordinates[i]){ // se trovo un valroe che non corrisponde
						equals=false;
						continue; // esco dal cilo
					}
				}
				// se la mosa inserita dal giocatore corrisponde con una delle possibili mangiate
				if(equals){
					eat(srcX, srcY, destX, destY); 	// effettua la mangiata
					hasEaten = true; 				// dichiaro di aver mangiato qualcosa
					continue; 						// esco dal ciclo
				}
				
			}	
			
			// se ho mangiato qualcosa
			if (hasEaten) {
				pawnInMultipleEat.clear(); 	// svuoto l'array contenente le possibili mangiate
				
				canEatMore(destX, destY); 	// verifico se posso mangiare ancora
				if (canEatMore >= 3)		// se ho mangiato al massimo 3 pezzi di fila
					canEatMore = 0;			// non posso più mangiare e azzero la variabile che conta il numero di pezzi mangiati di fila
			} else { // nel caso in cui non abbia mangiato nulla pur potendo mangiare
				graphic.getMenu().setIllegalLabel("Mossa illegale."); // comunica che è stata fatta una mossa illegale
			}

			if (canEatMore == 0){ // se non ho mangiato nulla, 
				changeTurn(); // cambia turno
			}

		} else { // nel caso in cui ci sia un semplice cambio di turno

			canEat(); // controllo se posso mangiare un pezzo avversario

			if (pawnThatCanEat.isEmpty()) { 				// se non ci sono pezzi che possono mangiare
				if (canMove(srcX, srcY, destX, destY)) {	// controllo se posso muovermi nella destinazione indicata dall'utente
					movePawn(srcX, srcY, destX, destY);		// se posso muovermi, mi muovo
					changeTurn();							// e cambio turno
				} else {
					graphic.getMenu().setIllegalLabel("Mossa illegale."); // altrimenti la mossa che l'utente sta tentando di fare è illegale
				}
			}

			// se invece ci sono pezzi che si possono mangiare
			else {

				int[] userCoordinates = new int[4]; // salvo la mossa che l'utente vuole fare in un array di interi

				userCoordinates[0] = srcX;
				userCoordinates[1] = srcY;
				userCoordinates[2] = destX;
				userCoordinates[3] = destY;
				
				Iterator it = pawnThatCanEat.iterator(); // creo un iteratore sull'arraylist contenente i pezzi che possono mangiare
				boolean equals;							 // verifica se la mossa inserita dall'utente corrisponde tra le possibili mangiate
				
				while(it.hasNext()){
					
					equals = true;
					int []coordinates = (int[]) it.next();
					
					for(int i=0; i<4; i++){
						if(userCoordinates[i] != coordinates[i]){ // se trova una coordinata dell'utente che non corrisponde con 
																// la rispettiva coordinata di mangiata possibile.
							equals=false;
							continue; // esci dal ciclo
						}
					}
					
					if(equals){ // se trova corrispondenza
						eat(srcX, srcY, destX, destY); // effettua la mangiata
						hasEaten = true; // dichiara di aver mangiato qualcosa
						continue; // esci dal cilo
					}
					
				}	

				pawnThatCanEat.clear(); // svuota arraylist con possibili mangiate di quel turno
				
				if (hasEaten) { // se ho mangiato
					
					canEatMore(destX, destY); // verifico di poter mangiare ancora
					if (canEatMore < 1) { // se non posso mangiare ancora
						changeTurn();		// cambio turno
					}
				} else { // se invece non ho mangiato(unica mosssa possibile)
					graphic.getMenu().setIllegalLabel("Mossa illegale."); // MOSSA ILLEGALE
				}

			}// fine se ci sono pedine da mangiare
		}
		
		// aggiorno l'interfaccia grafica
		graphic.repaint(board.getBoard());
		
		// controllo se la partita si è conclusa 
		checkWin();
		
	}
	
	/**
	 * Provvede a muovere la pedina
	 * @param srcX: ascissa della cella in cui si trova la pedina da muovere
	 * @param srcY: ordinata della cella in cui si trova la pedina da muovere
	 * @param destX: ascissa della cella in cui voglio muovere la pedina
	 * @param destY: ordinata della cella in cui voglio muovere la pedina
	 */
	static void movePawn(int srcX, int srcY, int destX, int destY) {

		// sposto la pedina nella destinazione, modificando il valore nella matrice
		board.setValueAt(destX, destY, board.getValueAt(srcX, srcY));
		// svuoto la casella di origine
		board.setValueAt(srcX, srcY, 1);
		// controllo se una pedina diventa dama
		checkPromotion(destX, destY);

	}
	
	/**
	 * Modifica la damiera per effettuare una mangiata
	 * @param srcX: ascissa della cella in cui si trova la pedina da muovere
	 * @param srcY: ordinata della cella in cui si trova la pedina da muovere
	 * @param destX: ascissa della cella in cui voglio muovere la pedina
	 * @param destY: ordinata della cella in cui voglio muovere la pedina
	 */
	static void eat(int srcX, int srcY, int destX, int destY) {

		// sposto la pedina nella destinazione
		board.setValueAt(destX, destY, board.getValueAt(srcX, srcY));
		// svuoto la casella di origine
		board.setValueAt(srcX, srcY, 1);

		// svuoto la casella dove c'era la pedina mangiata
		// muovo in basso a destra
		if (srcX < destX && srcY < destY)
			board.setValueAt(srcX + 1, srcY + 1, 1);
		// muovo in basso a sinistra
		if (srcX < destX && srcY > destY)
			board.setValueAt(srcX + 1, srcY - 1, 1);
		// muovo in alto a destra
		if (srcX > destX && srcY < destY)
			board.setValueAt(srcX - 1, srcY + 1, 1);
		// muovo in alto a sinistra
		if (srcX > destX && srcY > destY)
			board.setValueAt(srcX - 1, srcY - 1, 1);

		// controllo se una pedina diventa dama
		checkPromotion(destX, destY);
		
	}
	
	/**
	 * Controlla se ci sono possibili mangiate
	 */
	static void canEat() {

		if (whiteRound) { // se tocca al bianco

			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {

					int pawn = board.getValueAt(i, j); // prendo il valore che si trova nella matrice alla posizione (i,j)
					switch (pawn) { // in base al suo valore...

					case 2: // se è una pedina bianca
						checkEatWhitePawn(i, j); // richiamo il metodo che controlla le possibili mangiate di pedine bianche
						break;

					case 4: // se è una dama nera
						checkEatWhiteLady(i, j); // richiamo il metodo che controlla le possibili mangiate di pedine bianche
						break;

					default:
						;
					}
				}
			}
		} else { // se tocca al nero
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {

					int pawn = board.getValueAt(i, j); // prendo il valore che si trova nella matrice alla posizione (i,j)
					switch (pawn) { // in base al suo valore...

					case 3:
						checkEatBlackPawn(i, j); // richiamo il metodo che controlla le possibili mangiate di pedine bianche
						break;

					case 5:
						checkEatBlackLady(i, j); // richiamo il metodo che controlla le possibili mangiate di pedine bianche
						break;

					default:
						;
					}
				}
			}
		}

	}
	/**
	 * Controlla se le pedine bianche possono mangiare
	 * @param x: ascissa della cella della matrice
	 * @param y: ordinata della cella della matrice
	 */
	private static void checkEatWhitePawn(int x, int y) {
		
		try {
			if (board.getValueAt(x - 1, y - 1) == 3 		// se il valore che si trova in alto a sinistra di una posizione è una pedina nera
					&& board.getValueAt(x - 2, y - 2) == 1) // e la casella che si trova in alto a sinistra di 2 posizioni è vuota
				addPointToArrayList(x, y, x - 2, y - 2); 	// aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}

		try {
			if (board.getValueAt(x - 1, y + 1) == 3			// se il valore che si trova in alto a destra di una posizione è una pedina nera
					&& board.getValueAt(x - 2, y + 2) == 1) // e la casella che si trova in alto a sinistra di 2 posizioni è vuota
				addPointToArrayList(x, y, x - 2, y + 2);	// aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}

	}

	/**
	 * 
	 * Controlla se le pedine nere possono mangiare
	 * @param x: ascissa della cella della matrice
	 * @param y: ordinata della cella della matrice
	 */
	private static void checkEatBlackPawn(int x, int y) {

		try {
			if (board.getValueAt(x + 1, y - 1) == 2 // se la cella che si trova in basso a sinistra di una posizione è una pedina bianca
					&& board.getValueAt(x + 2, y - 2) == 1) // e la cella che si trova in basso a sinistra di 2 posizioni è nera vuota
				addPointToArrayList(x, y, x + 2, y - 2); // aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}
		try {
			if (board.getValueAt(x + 1, y + 1) == 2 // se la cella che si trova in basso a destra di una posizione è una pedina bianca
					&& board.getValueAt(x + 2, y + 2) == 1) // // e la cella che si trova in basso a destra di 2 posizioni è nera vuota
				addPointToArrayList(x, y, x + 2, y + 2); // aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}
	}
	
	/**
	 * 
	 * Controlla se le dame bianche possono mangiare. Oltre a controllare in alto a destra e in alto a sinistra controlla 
	 * nelle altre 2 direzioni. Inoltre può mangiare sia dame che pedine. 
	 * @param x: ascissa della cella della matrice
	 * @param y: ordinata della cella della matrice
	 */
	private static void checkEatWhiteLady(int x, int y) {

		try {
			if (board.getValueAt(x - 1, y - 1) == 3			// se il valore che si trova in alto a sinistra di una posizione è una pedina nera
					&& board.getValueAt(x - 2, y - 2) == 1) // e la casella che si trova in alto a sinistra di 2 posizioni è vuota
				addPointToArrayList(x, y, x - 2, y - 2);	// aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}
		try {
			if (board.getValueAt(x - 1, y + 1) == 3			// se il valore che si trova in basso a sinistra di una posizione è una pedina nera
					&& board.getValueAt(x - 2, y + 2) == 1)	// e la casella che si trova in basso a sinistra di 2 posizioni è vuota
				addPointToArrayList(x, y, x - 2, y + 2);	// aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}
		try {
			if (board.getValueAt(x + 1, y - 1) == 3			// se il valore che si trova in alto a destra di una posizione è una pedina nera
					&& board.getValueAt(x + 2, y - 2) == 1)	// e la casella che si trova in alto a destra di 2 posizioni è vuota
				addPointToArrayList(x, y, x + 2, y - 2);	// aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}
		try {
			if (board.getValueAt(x + 1, y + 1) == 3			// se il valore che si trova in basso a destra di una posizione è una pedina nera
					&& board.getValueAt(x + 2, y + 2) == 1) // e la casella che si trova in basso a destra di 2 posizioni è vuota
				addPointToArrayList(x, y, x + 2, y + 2);	// aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}
		try {
			if (board.getValueAt(x - 1, y - 1) == 5			// se il valore che si trova in alto a sinistra di una posizione è una dama nera
					&& board.getValueAt(x - 2, y - 2) == 1) // e la casella che si trova in alto a sinistra di 2 posizioni è vuota
				addPointToArrayList(x, y, x - 2, y - 2);	// aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}
		try {
			if (board.getValueAt(x - 1, y + 1) == 5 		// se il valore che si trova in basso a sinistra di una posizione è una dama nera
					&& board.getValueAt(x - 2, y + 2) == 1) // e la casella che si trova in basso a sinistra di 2 posizioni è vuota
				addPointToArrayList(x, y, x - 2, y + 2);	// aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}
		try {
			if (board.getValueAt(x + 1, y - 1) == 5			// se il valore che si trova in alto a destra di una posizione è una dama nera
					&& board.getValueAt(x + 2, y - 2) == 1) // e la casella che si trova in alto a destra di 2 posizioni è vuota
				addPointToArrayList(x, y, x + 2, y - 2);	// aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}
		try {
			if (board.getValueAt(x + 1, y + 1) == 5			// se il valore che si trova in basso a destra di una posizione è una dama nera
					&& board.getValueAt(x + 2, y + 2) == 1) // e la casella che si trova in basso a destra di 2 posizioni è vuota
				addPointToArrayList(x, y, x + 2, y + 2);	// aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}
	}
	
	/**
	 * 
	 * Controlla se le dame nere possono mangiare. Oltre a controllare in basso a destra e in basso a sinistra controlla 
	 * nelle altre 2 direzioni. Inoltre può mangiare sia dame che pedine. 
	 * @param x: ascissa della cella della matrice
	 * @param y: ordinata della cella della matrice
	 */
	private static void checkEatBlackLady(int x, int y) {

		try {
			if (board.getValueAt(x - 1, y - 1) == 2 		// se il valore che si trova in alto a sinistra di una posizione è una pedina bianca
					&& board.getValueAt(x - 2, y - 2) == 1) // e la casella che si trova in alto a sinistra di 2 posizioni è vuota
				addPointToArrayList(x, y, x - 2, y - 2);	// aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}
		try {
			if (board.getValueAt(x - 1, y + 1) == 2			// se il valore che si trova in basso a sinistra di una posizione è una pedina bianca
					&& board.getValueAt(x - 2, y + 2) == 1)	// e la casella che si trova in basso a sinistra di 2 posizioni è vuota
				addPointToArrayList(x, y, x - 2, y + 2);	// aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}
		try {
			if (board.getValueAt(x + 1, y - 1) == 2			// se il valore che si trova in alto a destra di una posizione è una pedina bianca
					&& board.getValueAt(x + 2, y - 2) == 1) // e la casella che si trova in alto a destra di 2 posizioni è vuota
				addPointToArrayList(x, y, x + 2, y - 2);	// aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}
		try {
			if (board.getValueAt(x + 1, y + 1) == 2			// se il valore che si trova in basso a destra di una posizione è una pedina bianca
					&& board.getValueAt(x + 2, y + 2) == 1) // e la casella che si trova in basso a destra di 2 posizioni è vuota
				addPointToArrayList(x, y, x + 2, y + 2);	// aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}
		try {
			if (board.getValueAt(x - 1, y - 1) == 4			// se il valore che si trova in alto a sinistra di una posizione è una dama bianca
					&& board.getValueAt(x - 2, y - 2) == 1) // e la casella che si trova in alto a sinistra di 2 posizioni è vuota
				addPointToArrayList(x, y, x - 2, y - 2);	// aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}
		try {
			if (board.getValueAt(x - 1, y + 1) == 4			// se il valore che si trova in basso a sinistra di una posizione è una dama bianca
					&& board.getValueAt(x - 2, y + 2) == 1) // e la casella che si trova in basso a sinistra di 2 posizioni è vuota
				addPointToArrayList(x, y, x - 2, y + 2);	// aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}
		try {
			if (board.getValueAt(x + 1, y - 1) == 4			// se il valore che si trova in alto a destra di una posizione è una dama nera
					&& board.getValueAt(x + 2, y - 2) == 1) // e la casella che si trova in alto a destra di 2 posizioni è vuota
				addPointToArrayList(x, y, x + 2, y - 2);	// aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}
		try {
			if (board.getValueAt(x + 1, y + 1) == 4			// se il valore che si trova in basso a destra di una posizione è una dama nera
					&& board.getValueAt(x + 2, y + 2) == 1) // e la casella che si trova in basso a destra di 2 posizioni è vuota
				addPointToArrayList(x, y, x + 2, y + 2);	// aggiungo la mossa tra le possibili mangiate
		} catch (Exception e) {
		}
	}
	/**
	 * Aggiunta della mossa passata come parametro alla lista di possibili mangiate
	 * @param srcX: ascissa della cella in cui si trova la possibile pedina che può mangiare
	 * @param srcY: ordinate della cella in cui si trova la possibili pedina che può mangiare
	 * @param destX: ascissa della destinazione della mangiata
	 * @param destY: ordinata della destinazione della mangiata
	 */
	private static void addPointToArrayList(int srcX, int srcY, int destX, int destY) {

		int[] coordinates = new int[4]; 

		coordinates[0] = srcX;
		coordinates[1] = srcY;
		coordinates[2] = destX;
		coordinates[3] = destY;

		pawnThatCanEat.add(coordinates);
	}
	
	/**
	 * 
	 * @param newSrcX: ascissa della nuova posizione 
	 * @param newSrcY: ordinata della nuova posizione
	 */
	static void canEatMore(int newSrcX, int newSrcY) {

		boolean modified = false; // indica se trovo o meno delle ulteriori pedine da mangiare

		if (whiteRound) { // se il turno è bianco

			// se il pezzo alla posizione (newSrcX, newSrcY) è una pedina bianca 
			if (board.getValueAt(newSrcX, newSrcY) == 2) {
				try {
					if (checkMovePawnNO(newSrcX, newSrcY, newSrcX - 2,
							newSrcY - 2)) { // se posso muovere la pedina di due posizione in alto a sinistra

						pawnInMultipleEat.add(new int[] { newSrcX, newSrcY,
								newSrcX - 2, newSrcY - 2 }); // alla lista contenente le mangiate multiple ci aggiungo la mossa in cui
															 // la pedina si posta in alto a sinistra di 2
						if (!modified) {	// se ho mangiato delle pedine
							canEatMore++; 	// incremento il numero di pedine che posso mangiare di fila
							modified = true; // ho trovato un'altra pedina
						}
					}
				} catch (Exception e) {
				}

				try {
					if (checkMovePawnNE(newSrcX, newSrcY, newSrcX - 2,
							newSrcY + 2)) { // se posso muovermi in alto a destra di 2 posizioni

						pawnInMultipleEat.add(new int[] { newSrcX, newSrcY,
								newSrcX - 2, newSrcY + 2 }); // alla lista contenente le mangiate multiple ci aggiungo la mossa in cui
						 									 // la pedina si posta in alto a destra di 2
						if (!modified) { 	// se ho mangiato delle pedine
							canEatMore++;	// incremento il numero di pedine che posso mangiare di fila
							modified = true;// ho trovato un'altra pedina 
						}
					}
				} catch (Exception e) {
				}
				;
			}

			// dama bianca
			if (board.getValueAt(newSrcX, newSrcY) == 4) {
				try {
					if (checkMoveWhiteLadyNO(newSrcX, newSrcY, newSrcX - 2,
							newSrcY - 2)) { // se posso muovermi in alto a destra di 2 posizioni

						pawnInMultipleEat.add(new int[] { newSrcX, newSrcY,
								newSrcX - 2, newSrcY - 2 });// alla lista contenente le mangiate multiple ci aggiungo la mossa in cui
						 									// la pedina si posta in alto a destra di 2
						if (!modified) { // se ho mangiato delle pedine
							canEatMore++; // incremento il numero di pedine che posso mangiare di fila
							modified = true; // ho trovato un'altra pedina 
						}
					}
				} catch (Exception e) {
				}
				;
				try {
					if (checkMoveWhiteLadyNE(newSrcX, newSrcY, newSrcX - 2,
							newSrcY + 2)) {  // se posso muovermi in alto a sinistra
						if (!modified) { 	 // se ho mangiato delle pedine
							canEatMore++;	 // incremento il numero di pedine che posso mangiare di fila
							modified = true; // ho trovato un'altra pedina 
						}
						pawnInMultipleEat.add(new int[] { newSrcX, newSrcY,
								newSrcX - 2, newSrcY + 2 }); // alla lista contenente le mangiate multiple ci aggiungo la mossa in cui
															 // la pedina si posta in alto a sinistra di 2
					}
				} catch (Exception e) {
				}
				;
				try {
					if (checkMoveWhiteLadySO(newSrcX, newSrcY, newSrcX + 2,
							newSrcY - 2)) { // se posso muovermi in basso a sinistra
						if (!modified) {    // se ho mangiato delle pedine 
							canEatMore++;   // incremento il numero di pedine che posso mangiare di fila
							modified = true;// ho trovato un'altra pedina 
						}
						pawnInMultipleEat.add(new int[] { newSrcX, newSrcY,
								newSrcX + 2, newSrcY - 2 }); // alla lista contenente le mangiate multiple ci aggiungo la mossa in cui
															 // la pedina si posta in basso a sinistra di 2
					}
				} catch (Exception e) {
				}
				;
				try {
					if (checkMoveWhiteLadySE(newSrcX, newSrcY, newSrcX + 2,
							newSrcY + 2)) { 	// se posso muovermi in basso a destra
						if (!modified) {  		// se ho mangiato delle pedine
							canEatMore++; 		// incremento il numero di pedine che posso mangiare di fila
							modified = true;	// ho trovato un'altra pedina 
						}
						pawnInMultipleEat.add(new int[] { newSrcX, newSrcY,
								newSrcX + 2, newSrcY + 2 }); // alla lista contenente le mangiate multiple ci aggiungo la mossa in cui
															 // la pedina si posta in basso a destra di 2
					}
				} catch (Exception e) {
				}
				;
			}
		
		// se il turno è nero
		} else {
			// pedina nera
			if (board.getValueAt(newSrcX, newSrcY) == 3) {
				try {

					if (checkMoveBlackPawnSO(newSrcX, newSrcY, newSrcX + 2,
							newSrcY - 2)) {  // se posso muovermi in basso a sinistra
						if (!modified) {	 // se ho mangiato delle pedine
							canEatMore++;	 // incremento il numero di pedine che posso mangiare di fila
							modified = true; // ho trovato un'altra pedina
						}
						pawnInMultipleEat.add(new int[] { newSrcX, newSrcY,
								newSrcX + 2, newSrcY - 2 }); // alla lista contenente le mangiate multiple ci aggiungo la mossa in cui
					}										 // la pedina si posta in basso a sinistra di 2
				} catch (Exception e) {
				}
				;
				try {
					if (checkMoveBlackPawnSE(newSrcX, newSrcY, newSrcX + 2,
							newSrcY + 2)) {	 // se posso muovermi in basso a destra			
						if (!modified) {	 // se ho mangiato delle pedine
							canEatMore++;	 // incremento il numero di pedine che posso mangiare di fila
							modified = true; // ho trovato un'altra pedina
						}
						pawnInMultipleEat.add(new int[] { newSrcX, newSrcY,
								newSrcX + 2, newSrcY + 2 }); // alla lista contenente le mangiate multiple ci aggiungo la mossa in cui
					}										 // la pedina si posta in basso a destra di 2
				} catch (Exception e) {
				}
				;

			}

			// dama nera
			if (board.getValueAt(newSrcX, newSrcY) == 5) {
				try {
					if (checkMoveBlackLadyNO(newSrcX, newSrcY, newSrcX - 2,
							newSrcY - 2)) {  // se posso muovermi in alto a sinistra	
						if (!modified) {	 // se ho mangiato delle pedine
							canEatMore++;	 // incremento il numero di pedine che posso mangiare di fila	 
							modified = true; // ho trovato un'altra pedina
						}
						pawnInMultipleEat.add(new int[] { newSrcX, newSrcY,
								newSrcX - 2, newSrcY - 2 }); // alla lista contenente le mangiate multiple ci aggiungo la mossa in cui
					}										 // la pedina si posta in alto a sinistra di 2
				} catch (Exception e) {
				}
				;
				try {
					if (checkMoveBlackLadyNE(newSrcX, newSrcY, newSrcX - 2,
							newSrcY + 2)) {  // se posso muovermi in alto a destra	
						if (!modified) {	 // se ho mangiato delle pedine
							canEatMore++;	 // incremento il numero di pedine che posso mangiare di fila
							modified = true; // ho trovato un'altra pedina
						}
						pawnInMultipleEat.add(new int[] { newSrcX, newSrcY,
								newSrcX - 2, newSrcY + 2 }); // alla lista contenente le mangiate multiple ci aggiungo la mossa in cui
					}										 // la pedina si posta in alto a destra di 2
				} catch (Exception e) {
				}
				;
				try {
					if (checkMoveBlackLadySO(newSrcX, newSrcY, newSrcX + 2,
							newSrcY - 2)) {  // se posso muovermi in basso a sinistra	
						if (!modified) {	 // se ho mangiato delle pedine
							canEatMore++;	 // incremento il numero di pedine che posso mangiare di fila
							modified = true; // ho trovato un'altra pedina
						}
						pawnInMultipleEat.add(new int[] { newSrcX, newSrcY,
								newSrcX + 2, newSrcY - 2 }); // alla lista contenente le mangiate multiple ci aggiungo la mossa in cui
					}										 // la pedina si posta in basso a sinistra di 2
				} catch (Exception e) {
				}
				;
				try {
					if (checkMoveBlackLadySE(newSrcX, newSrcY, newSrcX + 2,
							newSrcY + 2)) {  // se posso muovermi in basso a destra
						if (!modified) {	 // se ho mangiato delle pedine
							canEatMore++;	 // incremento il numero di pedine che posso mangiare di fila
							modified = true; // ho trovato un'altra pedina
						}
						pawnInMultipleEat.add(new int[] { newSrcX, newSrcY,
								newSrcX + 2, newSrcY + 2 }); // alla lista contenente le mangiate multiple ci aggiungo la mossa in cui
					}										 // la pedina si posta in basso a destra di 2
				} catch (Exception e) {
				}
				;
			}
		}

		if (!modified)
			canEatMore = 0;

	}
	/**
	 * Controllo se i parametri rappresentano una mossa valida
	 * @param srcX: ascissa della cella in cui si trova il pezzo da muovere
	 * @param srcY: ordinata della cella in cui si trova il pezzo da muovere
	 * @param destX: ascissa della destinazion
	 * @param destY: ordinata della destinazione
	 * @return
	 */
	private static boolean canMove(int srcX, int srcY, int destX, int destY) {

		if (whiteRound) { // se tocca al bianco

			// se la pedina che voglio muovere non è ne una dama ne una pedina bianca 
			if (board.getValueAt(srcX, srcY) != 2
					&& board.getValueAt(srcX, srcY) != 4) {
				return false; // MOSSA ILLEGALE
			}
			if (checkWhiteDestination(srcX, srcY, destX, destY)) {
				return true;
			}
			return false;
		} else {

			if (board.getValueAt(srcX, srcY) != 3
					&& board.getValueAt(srcX, srcY) != 5) {
				return false;
			}
			if (checkBlackDestination(srcX, srcY, destX, destY)) {
				return true;
			}
			return false;
		}
	}
	/**
	 * 
	 * @param srcX: ascissa della posizione in cui si trova la pedina bianca
	 * @param srcY: ordinata della posizione in cui si trova la pedina bianca
	 * @param destX: ascissa della posizione in cui voglio muovere la pedina bianca 
	 * @param destY: ordinata della posizione in cui voglio muovere la pedina bianca 
	 * @return la cella in cui mi voglio spostare è valida
	 */
	private static boolean checkWhiteDestination(int srcX, int srcY, int destX,
			int destY) {

		// se la destinazione non è una casella vuota nera non può muovere
		if (board.getValueAt(destX, destY) != 1) {
			return false;
		}

		// se sono una pedina bianca
		if (board.getValueAt(srcX, srcY) == 2) {

			// una bianca non puo muovere verso il basso ne di lato
			// valori inversi per la relazione matrice schermo
			// if(destY >= srcY)
			if (destX >= srcX)
				return false;

			// se mi vuomo verso sinistra
			// valori inversi per la relazione matrice schermo
			// if(destX < srcX)
			if (destY < srcY) {
				if (checkMovePawnNO(srcX, srcY, destX, destY))
					return true;
			}
			// altrimenti se mi muovo verso destra
			else {
				if (checkMovePawnNE(srcX, srcY, destX, destY))
					return true;
			}
		}// fine if sono una pedina normale

		// se sono una dama bianca
		if (board.getValueAt(srcX, srcY) == 4) {

			if (checkMoveWhiteLadyNE(srcX, srcY, destX, destY))
				return true;
			if (checkMoveWhiteLadyNO(srcX, srcY, destX, destY))
				return true;
			if (checkMoveWhiteLadySE(srcX, srcY, destX, destY))
				return true;
			if (checkMoveWhiteLadySO(srcX, srcY, destX, destY))
				return true;

			return false;
		}

		return false;
	}

	private static boolean checkMovePawnNO(int srcX, int srcY, int destX, int destY) {

		if (board.getValueAt(destX, destY) != 1)
			return false;

		if (hasEaten) {
			if (destY == srcY - 2 && destX == srcX - 2
					&& (board.getValueAt(srcX - 1, srcY - 1) == 5)) {
				return true;
			}
		}

		// una bianca puo muovere di una casella
		// valori inversi per la relazione matrice schermo
		// if(destX == srcX-1 && destY == srcY-1)
		if (destY == srcY - 1 && destX == srcX - 1)
			return true; // posso muovere
		// posso muovermi di due se c'è una pedina da mangiare in mezzo
		if (destY == srcY - 2 && destX == srcX - 2
				&& (board.getValueAt(srcX - 1, srcY - 1) == 3)) {
			return true;
		}

		return false;

	}

	private static boolean checkMovePawnNE(int srcX, int srcY, int destX, int destY) {

		if (board.getValueAt(destX, destY) != 1)
			return false;

		if (hasEaten) {
			if (destY == srcY + 2 && destX == srcX - 2
					&& (board.getValueAt(srcX - 1, srcY + 1) == 5)) {
				return true;
			}
		}

		// una bianca puo muovere di una casella
		// valori inversi per la relazione matrice schermo
		// if(destX == srcX+1 && destY == srcY+1)
		if (destY == srcY + 1 && destX == srcX - 1)
			return true; // posso muovere

		// posso muovermi di due se c'è una pedina da mangiare in mezzo
		if (destY == srcY + 2 && destX == srcX - 2
				&& (board.getValueAt(srcX - 1, srcY + 1) == 3)) {
			return true;
		}

		return false;
	}

	private static boolean checkMoveWhiteLadyNE(int srcX, int srcY, int destX,
			int destY) {

		if (board.getValueAt(destX, destY) != 1)
			return false;

		// una dama puo muovere di una casella o di due caselle
		// valori inversi per la relazione matrice schermo
		// if(destX == srcX+1 && destY == srcY+1)
		if (destY == srcY + 1 && destX == srcX - 1)
			return true; // posso muovere

		// posso muovermi di due se c'è una pedina da mangiare in mezzo
		if (destY == srcY + 2 && destX == srcX - 2
				&& (board.getValueAt(srcX - 1, srcY + 1) == 3)) {
			return true;
		}
		// posso muovermi di due se c'è una dama da mangiare in mezzo
		if (destY == srcY + 2 && destX == srcX - 2
				&& (board.getValueAt(srcX - 1, srcY + 1) == 5)) {
			return true;
		}

		return false;
	}

	private static boolean checkMoveWhiteLadyNO(int srcX, int srcY, int destX,
			int destY) {

		if (board.getValueAt(destX, destY) != 1)
			return false;
		// una dama puo muovere di una casella o di due caselle
		// valori inversi per la relazione matrice schermo
		// if(destX == srcX+1 && destY == srcY+1)
		if (destY == srcY - 1 && destX == srcX - 1)
			return true; // posso muovere

		// posso muovermi di due se c'è una pedina da mangiare in mezzo
		if (destY == srcY - 2 && destX == srcX - 2
				&& (board.getValueAt(srcX - 1, srcY - 1) == 3)) {
			return true;
		}
		// posso muovermi di due se c'è una dama da mangiare in mezzo
		if (destY == srcY - 2 && destX == srcX - 2
				&& (board.getValueAt(srcX - 1, srcY - 1) == 5)) {
			return true;
		}

		return false;
	}

	private static boolean checkMoveWhiteLadySE(int srcX, int srcY, int destX,
			int destY) {

		if (board.getValueAt(destX, destY) != 1)
			return false;
		// una dama puo muovere di una casella o di due caselle
		// valori inversi per la relazione matrice schermo
		// if(destX == srcX+1 && destY == srcY+1)
		if (destY == srcY + 1 && destX == srcX + 1)
			return true; // posso muovere

		// posso muovermi di due se c'è una pedina da mangiare in mezzo
		if (destY == srcY + 2 && destX == srcX + 2
				&& (board.getValueAt(srcX + 1, srcY + 1) == 3)) {
			return true;
		}
		// posso muovermi di due se c'è una dama da mangiare in mezzo
		if (destY == srcY + 2 && destX == srcX + 2
				&& (board.getValueAt(srcX + 1, srcY + 1) == 5)) {
			return true;
		}

		return false;
	}

	private static boolean checkMoveWhiteLadySO(int srcX, int srcY, int destX,
			int destY) {

		if (board.getValueAt(destX, destY) != 1)
			return false;
		// una dama puo muovere di una casella o di due caselle
		// valori inversi per la relazione matrice schermo
		// if(destX == srcX+1 && destY == srcY+1)
		if (destY == srcY - 1 && destX == srcX + 1)
			return true; // posso muovere

		// posso muovermi di due se c'è una pedina da mangiare in mezzo
		if (destY == srcY - 2 && destX == srcX + 2
				&& (board.getValueAt(srcX + 1, srcY - 1) == 3)) {
			return true;
		}
		// posso muovermi di due se c'è una dama da mangiare in mezzo
		if (destY == srcY - 2 && destX == srcX + 2
				&& (board.getValueAt(srcX + 1, srcY - 1) == 5)) {
			return true;
		}

		return false;
	}

	// metodi per player vs player

	private static boolean checkBlackDestination(int srcX, int srcY, int destX,
			int destY) {

		// se la destinazione non è una casella vuota nera non puo muovere
		if (board.getValueAt(destX, destY) != 1) {
			return false;
		}

		// se sono una pedina normale
		if (board.getValueAt(srcX, srcY) == 3) {

			// una nera non puo muovere verso l'alto ne di lato
			// valori inversi per la relazione matrice schermo
			// if(destY <= srcY)
			if (destX <= srcX)
				return false;

			// se mi vuomo verso sinistra
			// valori inversi per la relazione matrice schermo
			// if(destX < srcX)
			if (destY < srcY) {
				if (checkMoveBlackPawnSO(srcX, srcY, destX, destY))
					return true;
			}
			// altrimenti se mi muovo verso destra
			else {
				if (checkMoveBlackPawnSE(srcX, srcY, destX, destY))
					return true;
			}
		}// fine if sono una pedina normale

		// se sono una dama
		if (board.getValueAt(srcX, srcY) == 5) {

			if (checkMoveBlackLadyNE(srcX, srcY, destX, destY))
				return true;
			if (checkMoveBlackLadyNO(srcX, srcY, destX, destY))
				return true;
			if (checkMoveBlackLadySE(srcX, srcY, destX, destY))
				return true;
			if (checkMoveBlackLadySO(srcX, srcY, destX, destY))
				return true;

			return false;
		}

		return false;

	}

	static boolean checkMoveBlackPawnSO(int srcX, int srcY, int destX,
			int destY) {

		// controllo di muovere in una casella valida
		if (board.getValueAt(destX, destY) != 1)
			return false;

		if (hasEaten) {
			if (destY == srcY - 2 && destX == srcX + 2
					&& (board.getValueAt(srcX + 1, srcY - 1) == 4)) {
				return true;
			}
		}

		// una nera puo muovere di una casella
		// valori inversi per la relazione matrice schermo
		if (destY == srcY - 1 && destX == srcX + 1)
			return true; // posso muovere
		// posso muovermi di due se c'è una pedina da mangiare in mezzo
		if (destY == srcY - 2 && destX == srcX + 2
				&& (board.getValueAt(srcX + 1, srcY - 1) == 2)) {
			return true;
		}

		return false;

	}

	static boolean checkMoveBlackPawnSE(int srcX, int srcY, int destX,
			int destY) {

		// controllo di muovere in una casella valida
		if (board.getValueAt(destX, destY) != 1)
			return false;

		if (hasEaten) {
			if (destY == srcY + 2 && destX == srcX + 2
					&& (board.getValueAt(srcX + 1, srcY + 1) == 4)) {
				return true;
			}
		}

		// una nera puo muovere di una casella
		// valori inversi per la relazione matrice schermo
		if (destY == srcY + 1 && destX == srcX + 1)
			return true; // posso muovere
		// posso muovermi di due se c'è una pedina da mangiare in mezzo
		if (destY == srcY + 2 && destX == srcX + 2
				&& (board.getValueAt(srcX + 1, srcY + 1) == 2)) {
			return true;
		}

		return false;
	}

	// implementare i controlli delle dame
	static boolean checkMoveBlackLadyNE(int srcX, int srcY, int destX,
			int destY) {

		// controllo di muovere in una casella valida
		if (board.getValueAt(destX, destY) != 1)
			return false;
		// una dama puo muovere di una casella o di due caselle
		// valori inversi per la relazione matrice schermo
		// if(destX == srcX+1 && destY == srcY+1)
		if (destY == srcY + 1 && destX == srcX - 1)
			return true; // posso muovere

		// posso muovermi di due se c'è una pedina da mangiare in mezzo
		if (destY == srcY + 2 && destX == srcX - 2
				&& (board.getValueAt(srcX - 1, srcY + 1) == 2)) {
			return true;
		}
		// posso muovermi di due se c'è una dama da mangiare in mezzo
		if (destY == srcY + 2 && destX == srcX - 2
				&& (board.getValueAt(srcX - 1, srcY + 1) == 4)) {
			return true;
		}

		return false;
	}

	static boolean checkMoveBlackLadyNO(int srcX, int srcY, int destX,
			int destY) {
		// controllo di muovere in una casella valida
		if (board.getValueAt(destX, destY) != 1)
			return false;
		// una dama puo muovere di una casella o di due caselle
		// valori inversi per la relazione matrice schermo
		// if(destX == srcX+1 && destY == srcY+1)
		if (destY == srcY - 1 && destX == srcX - 1)
			return true; // posso muovere

		// posso muovermi di due se c'è una pedina da mangiare in mezzo
		if (destY == srcY - 2 && destX == srcX - 2
				&& (board.getValueAt(srcX - 1, srcY - 1) == 2)) {
			return true;
		}
		// posso muovermi di due se c'è una dama da mangiare in mezzo
		if (destY == srcY - 2 && destX == srcX - 2
				&& (board.getValueAt(srcX - 1, srcY - 1) == 4)) {
			return true;
		}

		return false;
	}

	static boolean checkMoveBlackLadySE(int srcX, int srcY, int destX,
			int destY) {
		// controllo di muovere in una casella valida
		if (board.getValueAt(destX, destY) != 1)
			return false;
		// una dama puo muovere di una casella o di due caselle
		// valori inversi per la relazione matrice schermo
		// if(destX == srcX+1 && destY == srcY+1)
		if (destY == srcY + 1 && destX == srcX + 1)
			return true; // posso muovere

		// posso muovermi di due se c'è una pedina da mangiare in mezzo
		if (destY == srcY + 2 && destX == srcX + 2
				&& (board.getValueAt(srcX + 1, srcY + 1) == 2)) {
			return true;
		}
		// posso muovermi di due se c'è una dama da mangiare in mezzo
		if (destY == srcY + 2 && destX == srcX + 2
				&& (board.getValueAt(srcX + 1, srcY + 1) == 4)) {
			return true;
		}

		return false;
	}

	static boolean checkMoveBlackLadySO(int srcX, int srcY, int destX,
			int destY) {
		// controllo di muovere in una casella valida
		if (board.getValueAt(destX, destY) != 1)
			return false;
		// una dama puo muovere di una casella o di due caselle
		// valori inversi per la relazione matrice schermo
		// if(destX == srcX+1 && destY == srcY+1)
		if (destY == srcY - 1 && destX == srcX + 1)
			return true; // posso muovere

		// posso muovermi di due se c'è una pedina da mangiare in mezzo
		if (destY == srcY - 2 && destX == srcX + 2
				&& (board.getValueAt(srcX + 1, srcY - 1) == 2)) {
			return true;
		}
		// posso muovermi di due se c'è una dama da mangiare in mezzo
		if (destY == srcY - 2 && destX == srcX + 2
				&& (board.getValueAt(srcX + 1, srcY - 1) == 4)) {
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @param destX: ascissa cella in cui la pedina si è appena mossa
	 * @param destY: ordinata cella in cui la pedina si è appena mossa
	 */
	private static void checkPromotion(int destX, int destY) {
		
		// se il turno è bianco e la mia pedina va nella prima riga a partire dall'alto
		if (whiteRound && destX == 0 && board.getValueAt(destX, destY) == 2)
			board.setValueAt(destX, destY, 4); // diventa dama

		// se il turno è bianco e la mia pedina va nella prima riga a partire dal basso
		if (!whiteRound && destX == 7 && board.getValueAt(destX, destY) == 3)
			board.setValueAt(destX, destY, 5); // diventa dama
	}
	
	/**
	 * Controlla se un giocatore (sia esso umano o macchina) ha vinto
	 */
	static void checkWin() {

		int numberOfWhitePawn = 0; // numero di pedine bianche
		int numberOfBlackPawn = 0; // numero di pedine nere

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {

				if (board.getValueAt(i, j) == 2 || board.getValueAt(i, j) == 4)
					numberOfWhitePawn++; // incremento il contatore di pedine bianche
				if (board.getValueAt(i, j) == 3 || board.getValueAt(i, j) == 5)
					numberOfBlackPawn++; // incremento il contatore di pedine nere
			}
		}

		if (numberOfWhitePawn == 0) { // se non ci sono pedine bianche
			JOptionPane.showMessageDialog(null, "Nero vince!");
			endGame();
		}

		if (numberOfBlackPawn == 0) { // se non ci sono pedine nere
			JOptionPane.showMessageDialog(null, "Bianco vince!");
			endGame();
		}

	}
	
	/**
	 * 
	 * @return Controlla che non ci sia la situazione di stallo
	 */
	private static boolean checkStallSituation() {
		
		// se qualcuno può muoversi oppure se qualcuno può mangiare allora non c'è stallo
		return !( someoneCanMove() || someoneCanEat() );

	}
	/**
	 * 
	 * @return indica se ci sono dei pezzi della damiera che si possono muovere
	 */
	
	private static boolean someoneCanMove(){
		
		//problema indici inversi attenzione
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				
				if (whiteRound){ // se tocca al bianco
					if(board.getValueAt(i, j) == 2) { // se sto analizzando una pedina bianca
					
						try{
							if(canMove(i, j, i-1, j+1)){ // e se posso muovere la pedina in alto a destra
								return true;			 // non c'è stallo
							}
						}catch(Exception e){}
						try{
							if(canMove(i, j, i-1, j-1)){ // e se posso muovere la pedina in alto a sinistra
								return true;			 // non c'è stallo
							}
						}catch(Exception e){}
					}
				}
				else{
					if(board.getValueAt(i, j) == 3) {
					
						try{
							if(canMove(i, j, i+1, j+1)){ // se posso muovere in basso a destra
								return true;			 // non c'è stallo
							}
						}catch(Exception e){}
						try{
							if(canMove(i, j, i+1, j-1)){ // se posso muovere 
								return true;			 // non c'è stallo
							}
						}catch(Exception e){}
					}
				}
				
				if(board.getValueAt(i, j) == 5 || board.getValueAt(i, j) == 4){
					try{
						if(canMove(i, j, i-1, j+1)){	// se posso muovere in basso a sinistra
							return true;				// non c'è stallo
						}
					}catch(Exception e){}
					try{
						if(canMove(i, j, i-1, j-1)){	// se posso muovere in alto a sinistra
							return true;				// non c'è stallo
						}
					}catch(Exception e){}
					try{
						if(canMove(i, j, i+1, j+1)){	// se posso muovere in basso a destra
							return true;				// non c'è stallo
						}
					}catch(Exception e){}
					try{
						if(canMove(i, j, i+1, j-1)){	// se posso muovere in alto a destra
							return true;				// non c'è stallo
						}
					}catch(Exception e){}
				}
				
			}//fine for i
		}//fine for j
		
		return false;	// c'è stallo
	}
	/**
	 * 
	 * @return indica se c'è un pezzo che nella damiera può muoversi. Utilizzato per verificare la situazione di stallo
	 */
	private static boolean someoneCanEat(){
		
		canEat(); // richiamo il canEat che istanzia l'arraylist contentente tutte le possibili mangiate della partita in corso
		
		if(!pawnThatCanEat.isEmpty()){  // se l'array è vuoto significa che non sono possibili mangiate
			pawnThatCanEat.clear(); 	// lo cancello per evitare eccezioni
			return true;
		}
		return false;
	}
	/**
	 * Determina la fine del gioco
	 */
	static void endGame() {

		graphic.getMenu().setRoundLabel("");
		graphic.getMenu().setIllegalLabel("");
		graphic.getMenu().setPlayButton();
		
		canEatMore = 0;
		whiteRound = true;

		board.stop();
		graphic.repaint(board.getBoard());
		
	}


}
