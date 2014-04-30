package model;

/**
 * Questa classe contiene la struttura dati su cui si basa il gioco:
 * una matrice di interi 8x8
 * @author Andrea Franchini & Leonardo Groppo 
 * 
 */
public class Board {
	/**
	 * VARIABILI D'ISTANZA: - int board[][]: matrice d'interi: � la nostra
	 * struttura dati. - boolean gameInProgress: indica se il gioco � in cordo
	 * oppure no. Viene utilizzata dalla grafica per evitare di cambaire
	 * modalit� di gioco a partita in corso.
	 * 
	 * CELL ID CODES: 
	 * 0: indica una cella bianca vuota
	 * 1: indica una cella nera vuota
	 * 2: indica una cella con una pedina bianca
	 * 3: indica una cella con una pedina nera
	 * 4: indica una cella con una dama bianca
	 * 5: indica una cella con una dama nera
	 * 
	 */
	
	private int board[][]; // matrice d'interi: la nostra struttura dati.
	private boolean gameInProgress; // indica se c'è una partita in corso o meno. E' utilizzata per prevenire il cambio di modalità
									// di gioco durante una partita.

	// METODI COSTRUTTORI

	public Board() {

		board = new int[8][8]; // creo la matrice

		instance(); // metodo di inizializzazione della matrice: la riempie con i valori sopra indicati,
					// basandosi sulla posizione

	}

	/**
	 * 
	 * @param empty: indica se creare una matrice vuota o meno. E' utilizzata nella fase in cui si decide la modalità di gioco
	 *           
	 */
	public Board(boolean empty) {

		board = new int[8][8]; // creates a 8x8 matrix

		instanceEmpty();

	}
	
	/**
	 * Crea una damiera vuota. Questo metodo è utilizzato durante la decisione del tipo di partita
	 */
	private void instanceEmpty() {

		gameInProgress = false; // non è in corso una partita

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {

				// se la somma deghli indici è pari, creo una cella vuota bianca
				if (!((i + j) % 2 == 0))
					board[i][j] = 0;

				else {
					// altrimenti creeo una cella vuota nera
					board[i][j] = 1;
				} 
			} // end for j
		} // end for i

	} // end instanceEmpty
	
	
	/**
	 * it creates the classic initial draughtboard
	 */
	private void instance() {

		gameInProgress = true; // sta iniziando una nuova partita

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {

				// se la somma tra i due indici di iterazione è pari, significa che in qualla posizione (i,j) deve esserci 
				// una cella bianca(id 0)
				if (!((i + j) % 2 == 0))
					board[i][j] = 0;

				else {
					// altrimenti, se siamo nelle prime 3 righe creo una cella nera con pedina nera(id 3)
					if (i < 3)
						board[i][j] = 3;
					else {
						// altrimenti,,se siamo nelle ultime 3 righe creo una cella nera con pedina bianca
						if (i > 4)
							board[i][j] = 2;
						else
							// in tutti gli altri casi, creo una cella vuota nera
							board[i][j] = 1;
					}
				}

			}// fine for j
		}// fine for i
	} // fine instance
	
	
	// METODI GET
	
	
	/**
	 * @return restituisce la struttra dati della damiera
	 */
	public int[][] getBoard() {
		return board;
	}
	
	/**
	 * 
	 * @param x: ascissa x richiesta
	 * @param y: ordinata y richiesta
	 * @return l'elemento della damiera alla posizione (x,y)
	 */
	
	public int getValueAt(int x, int y) {

		return board[x][y];
	}
	
	/**
	 * 
	 * @return indica se è in corso una partita o meno
	 */
	public boolean isGameInProgress() {
		return gameInProgress;
	}

	
	// METODI SET
	
	/**
	 * 
	 * @param b: nuova matrice passata come parametro da sostituire a quella corrente. 
	 */
	
	public void setBoard(int b[][]){
		this.board = b;
	}

	/**
	 * 
	 * @param x: ascissa di una cella della damiera
	 * @param y: ordinata di una cella della damiera
	 * @param value: valore da inserire nella damiera avente cella con coordinata (x,y)
	 */

	public void setValueAt(int x, int y, int value) {

		board[x][y] = value; // imposto valore
	}
	
	

	/**
	 * Utilizzato in fase di test per verificare la corretta predisposizione delle celle
	 */
	@Override
	public String toString() {

		String s = "";

		for (int i = 0; i < 8; i++) {

			for (int j = 0; j < 8; j++) {
				s += board[i][j] + " ";
			}
			s += "\n";
		}

		return s;
	}
	
	/**
	 * Creo la matrice per con la disposizione iniziale di una partita
	 */
	public void start() {
		instance();
	}

	/**
	 * Creo la matrice vuota utilizzata in fase di non gioco(scelta di una nuova modalità oppure resa)
	 */
	public void stop() {
		instanceEmpty();
	}
}
