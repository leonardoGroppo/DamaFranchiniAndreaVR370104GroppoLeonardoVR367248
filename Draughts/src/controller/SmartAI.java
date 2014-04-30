package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JOptionPane;

import model.Board;

/**
 * Classe che gestice l'intelligenza artificiale di livello 2. Estende thread
 * perchè per rendere visibili le mosse che il computer effettua, lo si deve far
 * "dormire" per un certo periodo di tempo.
 * 
 * @author Andrea Franchini & Leonardo Groppo
 * 
 */

public class SmartAI extends Thread {

	// VARIABILI D'ISTANZA

	ArrayList<int[]> pawnsToSave; // lista con le pedine da salvare
	ArrayList<int[]> smartMove; // liste con le mosse che il computer può
								// effettuare
	ArrayList<int[]> movesWithSamePriority; // lista con le mosse aventi le
											// stesse priorità
	Random r; // serve per estrarre una mossa a caso nel caso ci siano mosse con
				// la stessa priorità
	int index; // indice della mossa estratta a caso tra le possibili mosse
	int[] coordinates; // array che contiene la mossa scelta dal computer
	boolean hasDoneSomething; // indica se è stata effettuato un qualsiasi tipo
								// di mossa

	// METODO COSTRUTTORE

	public SmartAI() {

		GameController.pawnThatCanMove = new ArrayList();
		pawnsToSave = new ArrayList();
		smartMove = new ArrayList();
		movesWithSamePriority = new ArrayList();
		r = new Random();

	}

	@Override
	public void run() {

		// server per sapere se ho fatto una mossa
		hasDoneSomething = false;

		// imposto le priorità e faccio la mossa migliore
		smartMove();

	}

	/**
	 * Raggruppa tutti i metodi dell'intelligenza artificiale.
	 */
	private void smartMove() {

		checkMoves(); // controlla le mosse possibili

		calcPositionalPriority(); // inizia a calcolare la priorità di una mossa
									// anche in base alla posizione della
									// pedina:
									// più è indietro più ha priorità. Questo
									// per evitare che le pedine più avanzate
									// vengano mangiate
									// dall'avversario

		calcLadyPriority(); // verifico se può diventare una dama e in base al
							// risultato assegno una determinata priorità alla
							// mossa

		calcEatPriority(); // verifico se può mangiare e in base all'esito
							// assegno una priorità alla mossa.

		calcSaveYourselfPriority(); // verifico che una pedina non sia a rischio
									// di essere mangiata. In questo caso la
									// faccio scappare assegndo una priorità
									// alta

		intelligenceMove(); // nel caso in cui non ci siano mosse particolari.
							// Prevedo la mossa successiva del bianco.

		GameController.graphic.repaint(GameController.board.getBoard()); // aggiorno
																			// interfaccio
																			// grafica

	}// fine smartMove

	/**
	 * Scansiona tutte le possibili mosse che il computer può effettuare
	 */
	private void checkMoves() {

		GameController.canEat(); // istanzia la lista con le pedine che possono
									// mangiare

		for (int i = 0; i < GameController.pawnThatCanEat.size(); i++) {
			int[] thisEat = { GameController.pawnThatCanEat.get(i)[0],
					GameController.pawnThatCanEat.get(i)[1],
					GameController.pawnThatCanEat.get(i)[2],
					GameController.pawnThatCanEat.get(i)[3], 0 };
			smartMove.add(thisEat); // aggiungo alle mosse effettuabili dal
									// computer tutte le possibili mangiate
		}

		// prendo tutte le mie pedine che posso muovere e mi salvo chi puo
		// muovere dove
		// creo un'arraylist contentente la mossa e in aggiunta un intero che ne
		// indica la priorit�

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {

				// sono una pedina
				if (GameController.board.getValueAt(i, j) == 3) {
					try {
						if (GameController.checkMoveBlackPawnSE(i, j, i + 1,
								j + 1)) {
							smartMove.add(new int[] { i, j, i + 1, j + 1, 0 });
						}
					} catch (Exception e) {
					}
					try {
						if (GameController.checkMoveBlackPawnSO(i, j, i + 1,
								j - 1)) {
							smartMove.add(new int[] { i, j, i + 1, j - 1, 0 });
						}
					} catch (Exception e) {
					}
				}

				// sono una dama
				if (GameController.board.getValueAt(i, j) == 5) {
					try {
						if (GameController.checkMoveBlackLadySE(i, j, i + 1,
								j + 1)) {
							smartMove.add(new int[] { i, j, i + 1, j + 1, 0 });
						}
					} catch (Exception e) {
					}
					try {
						if (GameController.checkMoveBlackLadySO(i, j, i + 1,
								j - 1)) {
							smartMove.add(new int[] { i, j, i + 1, j - 1, 0 });
						}
					} catch (Exception e) {
					}
					try {
						if (GameController.checkMoveBlackLadyNE(i, j, i - 1,
								j + 1)) {
							smartMove.add(new int[] { i, j, i - 1, j + 1, 0 });
						}
					} catch (Exception e) {
					}
					try {
						if (GameController.checkMoveBlackLadyNO(i, j, i - 1,
								j - 1)) {
							smartMove.add(new int[] { i, j, i - 1, j - 1, 0 });
						}
					} catch (Exception e) {
					}
				}

			}
		}// fine doppio for
	}

	/**
	 * Calcola la priorità di una mossa basandosi anche sulla posizione iniziale
	 * della pedina
	 */
	private void calcPositionalPriority() {

		// imposto la priorità per ogni mossa

		Iterator it = smartMove.iterator();
		while (it.hasNext()) {

			int[] thisMove = (int[]) it.next();

			// se sono una pedina
			if (GameController.board.getValueAt(thisMove[0], thisMove[1]) == 3) {

				// di base, una pedina avanzata tenderà ad essere scoperta
				// quindi tendo a portare avanti le pedine arretrate per
				// proteggere le avanzate
				thisMove[4] += 8 - thisMove[0];

				// se posso diventare dama, aumento molto la priorità della
				// mossa
				if (thisMove[0] == 6) {
					try {
						if (GameController.checkMoveBlackPawnSE(thisMove[0],
								thisMove[1], 7, thisMove[1] + 1)) {
							thisMove[4] += 10;
						} else {
							try {
								if (GameController.checkMoveBlackPawnSO(
										thisMove[0], thisMove[1], 7,
										thisMove[1] - 1))
									thisMove[4] += 10;
							} catch (Exception e) {
							}
							;
						}
					} catch (Exception e) {
					}
					;
				}
				// se c'è il rischio di essere mangiati con quella mossa
				if (canBeEaten(thisMove[0], thisMove[1], thisMove[2],
						thisMove[3])) {
					thisMove[4] -= 50; // abbasso molto la priorità
				}

			}

		}// fine while

	}

	/**
	 * Calcolo priorità di una mossa della dama
	 */
	private void calcLadyPriority() {

		// copio la damiera per effettuare delle simulazioni
		int[][] backupBoard = new int[8][8];

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				backupBoard[i][j] = GameController.board.getBoard()[i][j];
			}
		}

		// cerco nello smartMove le mosse delle dame
		for (int i = 0; i < smartMove.size(); i++) {
			if (GameController.board.getValueAt(smartMove.get(i)[0],
					smartMove.get(i)[1]) == 5) {

				// se sto analizzando una dama che si sposta (non che mangia)
				if (smartMove.get(i)[1] - smartMove.get(i)[3] == 1
						|| smartMove.get(i)[1] - smartMove.get(i)[3] == -1) {

					GameController.movePawn(smartMove.get(i)[0],
							smartMove.get(i)[1], smartMove.get(i)[2],
							smartMove.get(i)[3]);
					GameController.graphic.repaint(backupBoard);

					// se dalla destinazione della mossa puo mangiare
					GameController.canEatMore(smartMove.get(i)[2],
							smartMove.get(i)[3]);
					if (GameController.canEatMore > 0) {

						smartMove.get(i)[4] += 20;
						GameController.canEatMore = 0;
						GameController.pawnInMultipleEat.clear();

					}
					// se non puo mangiare
					else {

						// do comunque una priorità base pari a 7
						smartMove.get(i)[4] += 7;
						GameController.canEatMore = 0;
						GameController.pawnInMultipleEat.clear();
					}

					// ripristino il board
					for (int k = 0; k < 8; k++) {
						for (int j = 0; j < 8; j++) {
							GameController.board.getBoard()[k][j] = backupBoard[k][j];
						}
					}

					// dopo aver guardato la mossa guardo se la mossa mi porta
					// ad essere mangiato
					if (canBeEaten(smartMove.get(i)[0], smartMove.get(i)[1],
							smartMove.get(i)[2], smartMove.get(i)[3]))
						smartMove.get(i)[4] -= 50;
				}
			}

		}

	}

	/**
	 * Calcolo della priorità di una mangiata
	 */
	private void calcEatPriority() {

		// guardo se posso mangiare
		GameController.canEat();

		// se non ci sono mangiate non imposto nessuna priorità di mangiata
		try {
			if (GameController.pawnThatCanEat.isEmpty()) {
				GameController.pawnThatCanEat.clear();
				return;
			}
		} catch (Exception e) {
		}
		;

		// se ci sono mangiate scorro i due arraylist

		for (int j = 0; j < GameController.pawnThatCanEat.size(); j++) {

			int[] thisEatMove = GameController.pawnThatCanEat.get(j);

			for (int i = 0; i < smartMove.size(); i++) {

				boolean equals = true;

				for (int k = 0; k < 4; k++) {

					if (thisEatMove[k] != smartMove.get(i)[k])
						equals = false;
				}
				// se ho trovato una mangiata corrispondente a una delle mosse
				// che il computer può fare...
				if (equals) {
					smartMove.get(i)[4] += 200; // massima priorità alla
												// mangiata

					GameController.canEatMore(smartMove.get(i)[2],
							smartMove.get(i)[3]); // controllo se posso mangiare
													// ancora

					if (GameController.canEatMore > 0) {
						smartMove.get(i)[4] += 200; // massima priorità alla
													// mangiata multipla
						GameController.pawnInMultipleEat.clear();
						GameController.canEatMore = 0;
					} else { // se effettuo questa mossa e al turno successivo
								// vengo mangiato, priorità minima!
						if (canBeEaten(smartMove.get(i)[0],
								smartMove.get(i)[1], smartMove.get(i)[2],
								smartMove.get(i)[3]))
							smartMove.get(i)[4] -= 50;
					}
				}

			}// fine for
		}// fine iterator

	}// fine calcEatPriority()

	/**
	 * 
	 * @param srcX
	 *            : ascissa della cella dove si trova la pedina da muovere
	 * @param srcY
	 *            : ordinata della cella dove si trova la pedina muovere
	 * @param destX
	 *            : ascissa della cella dovee voglio muovere la pedina
	 * @param destY
	 *            : ordinata della cella dove voglio muovere la pedina
	 * @return indica se la mossa che voglio effettuare comporterà la mangiata
	 *         del pezzo che muovo o meno.
	 */
	private boolean canBeEaten(int srcX, int srcY, int destX, int destY) {

		// copio il board per effettuare delle simulazioni
		int[][] backupBoard = new int[8][8];

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				backupBoard[i][j] = GameController.board.getBoard()[i][j];
			}
		}

		// controllo se è un semplice spostamento spostamento
		if (srcX - destX == 1 || srcX - destX == -1) {

			GameController.movePawn(srcX, srcY, destX, destY);
		}
		// se non è uno spostamento sarà una mangiata singola o multipla. E'
		// indifferente.
		else {
			// simulo la mangiata
			GameController.eat(srcX, srcY, destX, destY);
		}

		// simulo di essere il bianco
		GameController.whiteRound = true;
		// controllo se il bianco puo mangiarmi
		GameController.canEat();

		// se il bianco non puo mangiare
		if (GameController.pawnThatCanEat.isEmpty()) {

			// ripristino il board
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					GameController.board.getBoard()[i][j] = backupBoard[i][j];
				}
			}

			// smetto di simulare il turno bianco
			GameController.whiteRound = false;

			return false; // non vengo mangiato
		}

		Iterator iterEatMoves = GameController.pawnThatCanEat.iterator();
		while (iterEatMoves.hasNext()) {

			int[] thisEatMove = (int[]) iterEatMoves.next();

			if ((thisEatMove[0] + thisEatMove[2]) / 2 == destX) {
				if ((thisEatMove[1] + thisEatMove[3]) / 2 == destY) {

					// ripristino il board
					for (int i = 0; i < 8; i++) {
						for (int j = 0; j < 8; j++) {
							GameController.board.getBoard()[i][j] = backupBoard[i][j];
						}
					}

					// smetto di simulare il turno bianco
					GameController.whiteRound = false;
					GameController.pawnThatCanEat.clear();
					return true; // se mi muovo lì verrò mangiato
				}
			}

		}

		// se il bianco puo mangiare, ma non la pedina nera che muovo
		// tutto ok non modifico nessuna priorità

		// ripristino il board
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				GameController.board.getBoard()[i][j] = backupBoard[i][j];
			}
		}

		// smetto di simulare il turno bianco
		GameController.whiteRound = false;
		GameController.pawnThatCanEat.clear();

		return false;
	}

	/**
	 * Calcolo della priorità nel caso in cui ci siano pedine a rischio di
	 * essere mangiate
	 */

	private void calcSaveYourselfPriority() {

		// suppongo di essere il bianco e vedo se posso mangiare
		GameController.whiteRound = true;
		GameController.canEat();

		// se il bianco non puo mangiare non fare niente
		if (GameController.pawnThatCanEat.isEmpty()) {
			GameController.whiteRound = false;
			GameController.pawnThatCanEat.clear();

		} else {

			// altrimenti mi salvo le mie pedine che possono essere mangiate
			int[] pawnToSave = new int[2];

			for (int i = 0; i < GameController.pawnThatCanEat.size(); i++) {

				// in pawnThatCanEat la posizione 0 e 1 sono le coordinate della
				// pedina bianca
				// nelle posizioni 2 e 3 c'è la casella vuota oltre la pedina
				// nera da mangiare
				// devo ricavare la casella in mezzo per salvarmi la posizione
				// della mia
				// pedina nera da salvare
				// il valore sarà la media tra le coordinate sorgenti e quelle
				// destinazione

				pawnToSave[0] = (int) (GameController.pawnThatCanEat.get(i)[0] + GameController.pawnThatCanEat
						.get(i)[2]) / 2;
				pawnToSave[1] = (int) (GameController.pawnThatCanEat.get(i)[1] + GameController.pawnThatCanEat
						.get(i)[3]) / 2;

				// aggiungo la pedina da salvare all'array di tutte le mie
				// pedine da salvare
				pawnsToSave.add(pawnToSave);
			}

			// finisco di simulare di essere il bianco
			GameController.whiteRound = false;
			GameController.pawnThatCanEat.clear();

			for (int i = 0; i < pawnsToSave.size(); i++) {

				pawnToSave[0] = pawnsToSave.get(i)[0];
				pawnToSave[1] = pawnsToSave.get(i)[1];

				for (int j = 0; j < smartMove.size(); j++) {

					if (pawnToSave[0] == smartMove.get(j)[0]
							&& pawnToSave[1] == smartMove.get(j)[1]) {

						smartMove.get(j)[4] += 40;
					}

				}

			}// fine for scorrimento pedine da salvare

			pawnsToSave.clear();

		}// fine else

	}
	/**
	 * Calcola la priorità delle mosse nel caso in cui non ci siano promozioni, mangiate o fughe
	 */
	private void intelligenceMove() {
		try {
			if (smartMove.isEmpty() && GameController.board.isGameInProgress()) { // se non posso effettuare nessuna mossa = stallo.
				JOptionPane.showMessageDialog(null, "Stallo: Nero perde.");
				GameController.endGame();
			}
		} catch (Exception e) {
		}
		;

		int[] moveWithMaxPriority = smartMove.get(0); 

		// cerco la mossa con la priorità maggiore
		for (int i = 1; i < smartMove.size(); i++) {

			if (smartMove.get(i)[4] > moveWithMaxPriority[4]) {
				moveWithMaxPriority = smartMove.get(i);
			}
		}

		// controllo se ci sono mosse con priorità uguale a quella maggiore
		// e le aggiungo a movesWithSamePriority
		for (int i = 0; i < smartMove.size(); i++) {

			if (smartMove.get(i)[4] == moveWithMaxPriority[4]) {
				movesWithSamePriority.add(smartMove.get(i));
			}
		}

		// estraggo una mossa casuale tra quelle che hanno priorita massima
		index = r.nextInt(movesWithSamePriority.size());
		coordinates = movesWithSamePriority.get(index);

		// controllo se è uno spostamento
		if (coordinates[2] - coordinates[0] == 1
				|| coordinates[2] - coordinates[0] == -1) {
			// effettuo la mossa
			try {
				this.sleep(1000);
			} catch (Exception e) {
			}

			GameController.movePawn(coordinates[0], coordinates[1],
					coordinates[2], coordinates[3]);
			hasDoneSomething = true;
		}
		// se non è uno spostamento sarà una mangiata singola o doppia che sia
		else {

			try {
				this.sleep(1000);
			} catch (Exception e) {
			}

			GameController.eat(coordinates[0], coordinates[1], coordinates[2],
					coordinates[3]);
			hasDoneSomething = true;

			GameController.canEatMore(coordinates[2], coordinates[3]);
			while (GameController.canEatMore != 0
					&& !GameController.pawnInMultipleEat.isEmpty()) {

				GameController.graphic.repaint(GameController.board.getBoard());

				try {
					this.sleep(1000);
				} catch (Exception e) {
				}
				
				// scelgo a caso la mossa
				index = r.nextInt(GameController.pawnInMultipleEat.size());
				coordinates = GameController.pawnInMultipleEat.get(index);
				
				// effettuo la mossa
				GameController.eat(coordinates[0], coordinates[1],
						coordinates[2], coordinates[3]);

				GameController.pawnInMultipleEat.clear();
				GameController.canEatMore(coordinates[2], coordinates[3]);
				if (GameController.canEatMore == 3) {
					GameController.canEatMore = 0;
				}
			}// fine while mangiata multipla

		}

		GameController.checkWin();

		// cambio turno
		GameController.changeTurn();

	}

}