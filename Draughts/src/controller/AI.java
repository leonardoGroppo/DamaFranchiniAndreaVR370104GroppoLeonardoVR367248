package controller;

import java.util.*;

import javax.*;
import javax.swing.JOptionPane;

/**
 * Classe che gestisce il livello 1 dell'intelligenza artificiale. Estende
 * thread perchè per rendere visibili le mosse che il computer effettua, lo si
 * deve far "dormire" per un certo periodo di tempo.
 * 
 * @author Andrea Franchini & Leonardo Groppo
 * 
 */
public class AI extends Thread {

	// VARIABILI D'ISTANZA
	ArrayList<int[]> pawnsToSave; // lista delle pedine da salvare
	ArrayList<int[]> smartMove; // lista delle mosse che l'intelligenza
								// artificiale può effettuare
	Random r; // serve per estrarre una mossa a caso nel caso ci siano mosse con
				// la stessa priorità
	int index; // indice della mossa estratta a caso tra le possibili mosse
	int[] coordinates; // array che contiene la mossa scelta dal computer
	boolean hasDoneSomething; // indica se è stata effettuato un qualsiasi tipo
								// di mossa

	// METODO COSTRUTTORE

	public AI() {

		GameController.pawnThatCanMove = new ArrayList();
		pawnsToSave = new ArrayList();
		smartMove = new ArrayList();
		r = new Random();

	}

	@Override
	public void run() {

		try {
			this.sleep(500); // attendi 500 millisecondi
		} catch (Exception e) {
		}

		// serve per sapere se ho fatto un'azione
		// dentrO i metodi (ordinati per priorità) se muovo, mangio o scappo
		// registro
		// il fatto che ho compiuto un'azione, quindi i mio turno dovra finire
		hasDoneSomething = false;

		// se non ho fatto nulla
		if (!hasDoneSomething) {
			// verifico se posso effettuare una mangiata
			canYouEat();
		}

		// se ho effettuato qualche tipo di mossa
		if (!hasDoneSomething) {
			// verifica se puoi diventare una dama
			canYouBecomeLady();
		}

		if (!hasDoneSomething) {
			// se ho una pedina che rischia di essere mangiata, devo salvarla
			saveYourPawn();
		}

		if (!hasDoneSomething) {
			// se non hai niente da fare, muovi a caso!
			randomMove();
		}

		// controllo se posso mangiare qualcosa
		checkStall();

	}

	/**
	 * Verifica se il computer può effettuare una mangiata
	 */
	private void canYouEat() {

		GameController.canEat(); // richiamo il canEat che inizializza la lista
									// con le pedine che posso mangiare
		if (!GameController.pawnThatCanEat.isEmpty()) {

			index = r.nextInt(GameController.pawnThatCanEat.size()); // estraggo
																		// un
																		// numero
																		// a
																		// caso
																		// compreso
																		// tra 0
																		// e il
																		// n° di
																		// possibili
																		// mangiate
			coordinates = GameController.pawnThatCanEat.get(index); // assegno a
																	// coordinates
																	// la
																	// mangiata
																	// scelta a
																	// caso
			GameController.eat(coordinates[0], coordinates[1], coordinates[2],
					coordinates[3]); // effettuo la mangiata

			GameController.canEatMore(coordinates[2], coordinates[3]); // controllo
																		// se
																		// posso
																		// effettuare
																		// un'altra
																		// mangiata
			while (GameController.canEatMore != 0
					&& !GameController.pawnInMultipleEat.isEmpty()) { // finchè
																		// ho
																		// pedine
																		// da
																		// mangiare

				try {
					sleep(1000); // arresto il thread per 1 secondo
				} catch (Exception e) {
				}

				index = r.nextInt(GameController.pawnInMultipleEat.size()); // estraggo
																			// un
																			// numero
																			// a
																			// caso
																			// compreso
																			// tra
																			// 0
																			// e
																			// il
																			// n°
																			// di
																			// possibili
																			// mangiate
				coordinates = GameController.pawnInMultipleEat.get(index); // assegno
																			// a
																			// coordinates
																			// la
																			// mangiata
																			// scelta
																			// a
																			// caso
				GameController.eat(coordinates[0], coordinates[1],
						coordinates[2], coordinates[3]); // effettuo la mangiata

				GameController.pawnInMultipleEat.clear(); // svuoto la lista
				GameController.canEatMore(coordinates[2], coordinates[3]); // verifico
																			// se
																			// posso
																			// mangiare
																			// ancora
				if (GameController.canEatMore == 3) { // se sono alla terza
														// mangiata consecutiva
					GameController.canEatMore = 0; // non posso mangiare più in
													// quel turno
				}
			}

			hasDoneSomething = true; // dichiaro che ho effettuato qualche tipo
										// di mossa
		}
	}

	/**
	 * Verifica se c'è una pedina che può diventare dama
	 */
	private void canYouBecomeLady() {

		for (int i = 0; i < 8; i++) { // analizzo l'ultima riga
			if (GameController.board.getValueAt(6, i) == 3) { // se trovo una
																// pedina nera
				if (GameController.checkMoveBlackPawnSE(6, i, 7, i + 1)) { // se
																			// si
																			// può
																			// muovere
																			// in
																			// basso
																			// a
																			// destra
					GameController.movePawn(6, i, 7, i + 1); // la muovo per
																// farla
																// diventare
																// dama
					hasDoneSomething = true; // dichiaro di aver effettuato un
												// qualsiasi tipo di mossa
				}
				if (GameController.checkMoveBlackPawnSO(6, i, 7, i - 1)) { // se
																			// non
																			// si
																			// può
																			// muovere
																			// a
																			// SE,
																			// controllo
																			// che
																			// si
																			// possa
																			// muovere
																			// in
																			// basso
																			// a
																			// sinistra
					GameController.movePawn(6, i, 7, i - 1); // la muovo per
																// farla
																// diventare
																// dama
					hasDoneSomething = true; // dichiaro di aver effettuato un
												// qualsiasi tipo di mossa
				}
			}
		}
	}

	/**
	 * Verifica se la pedina è a rischio di essere mangiata. In questo caso, la
	 * faccio scappare.
	 */
	private void saveYourPawn() {
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

			// salvo una pedina di quelle che possono essere mangiate

			// prendo una pedina a caso
			index = r.nextInt(pawnsToSave.size());
			coordinates = pawnsToSave.get(index);

			// se sono una pedina nera che sta per essere mangiata
			if (GameController.board.getValueAt(coordinates[0], coordinates[1]) == 3) {

				// e posso muoverla in una direzione, allora la muovo
				try {
					if (GameController.checkMoveBlackPawnSE(coordinates[0],
							coordinates[1], coordinates[0] + 1,
							coordinates[1] + 1)) {
						GameController.movePawn(coordinates[0], coordinates[1],
								coordinates[0] + 1, coordinates[1] + 1);
						hasDoneSomething = true;
					}
				} catch (Exception e) {
				}
				try {
					if (GameController.checkMoveBlackPawnSO(coordinates[0],
							coordinates[1], coordinates[0] + 1,
							coordinates[1] - 1)) {
						GameController.movePawn(coordinates[0], coordinates[1],
								coordinates[0] + 1, coordinates[1] - 1);
						hasDoneSomething = true;
					}
				} catch (Exception e) {
				}
			}

			// se sono una dama che sta per essere mangiata
			if (GameController.board.getValueAt(coordinates[0], coordinates[1]) == 5) {

				// e posso muovermi in una direzione, la muovo
				try {
					if (GameController.checkMoveBlackLadySE(coordinates[0],
							coordinates[1], coordinates[0] + 1,
							coordinates[1] + 1)) {
						GameController.movePawn(coordinates[0], coordinates[1],
								coordinates[0] + 1, coordinates[1] + 1);
						hasDoneSomething = true;
					}
				} catch (Exception e) {
				}
				try {
					if (GameController.checkMoveBlackLadySO(coordinates[0],
							coordinates[1], coordinates[0] + 1,
							coordinates[1] - 1)) {
						GameController.movePawn(coordinates[0], coordinates[1],
								coordinates[0] + 1, coordinates[1] - 1);
						hasDoneSomething = true;
					}
				} catch (Exception e) {
				}
				try {
					if (GameController.checkMoveBlackLadyNE(coordinates[0],
							coordinates[1], coordinates[0] - 1,
							coordinates[1] + 1)) {
						GameController.movePawn(coordinates[0], coordinates[1],
								coordinates[0] - 1, coordinates[1] + 1);
						hasDoneSomething = true;
					}
				} catch (Exception e) {
				}
				try {
					if (GameController.checkMoveBlackLadyNO(coordinates[0],
							coordinates[1], coordinates[0] - 1,
							coordinates[1] - 1)) {
						GameController.movePawn(coordinates[0], coordinates[1],
								coordinates[0] - 1, coordinates[1] - 1);
						hasDoneSomething = true;
					}
				} catch (Exception e) {
				}
			}
		}
	}
	/**
	 * Questo metodo viene invocato nel caso in cui non ci siano situazioni rilevanti. 
	 */
	private void randomMove() {

		// analizzo la damiera per analizzare tutte le possibili mosse che posso effettuare
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {

				if (GameController.board.getValueAt(i, j) == 3) {
					try {
						if (GameController.checkMoveBlackPawnSE(i, j, i + 1,
								j + 1)) {
							GameController.pawnThatCanMove.add(new int[] { i,
									j, i + 1, j + 1 });
						}
					} catch (Exception e) {
					}
					try {
						if (GameController.checkMoveBlackPawnSO(i, j, i + 1,
								j - 1)) {
							GameController.pawnThatCanMove.add(new int[] { i,
									j, i + 1, j - 1 });
						}
					} catch (Exception e) {
					}
				}

				if (GameController.board.getValueAt(i, j) == 5) {
					try {
						if (GameController.checkMoveBlackLadySE(i, j, i + 1,
								j + 1)) {
							GameController.pawnThatCanMove.add(new int[] { i,
									j, i + 1, j + 1 });
						}
					} catch (Exception e) {
					}
					try {
						if (GameController.checkMoveBlackLadySO(i, j, i + 1,
								j - 1)) {
							GameController.pawnThatCanMove.add(new int[] { i,
									j, i + 1, j - 1 });
						}
					} catch (Exception e) {
					}
					try {
						if (GameController.checkMoveBlackLadyNE(i, j, i - 1,
								j + 1)) {
							GameController.pawnThatCanMove.add(new int[] { i,
									j, i - 1, j + 1 });
						}
					} catch (Exception e) {
					}
					try {
						if (GameController.checkMoveBlackLadyNO(i, j, i - 1,
								j - 1)) {
							GameController.pawnThatCanMove.add(new int[] { i,
									j, i - 1, j - 1 });
						}
					} catch (Exception e) {
					}
				}

			}
		}
		// tra le possibili mosse che posso effettuare, ne estraggo una a caso. 
		if (GameController.pawnThatCanMove.size() != 0) {
			index = r.nextInt(GameController.pawnThatCanMove.size());
			coordinates = GameController.pawnThatCanMove.get(index);
			GameController.movePawn(coordinates[0], coordinates[1],
					coordinates[2], coordinates[3]);
			GameController.pawnThatCanMove.clear();
			hasDoneSomething = true;
		}
	}
	/**
	 * Controlla se c'è stallo. 
	 */
	private void checkStall() {

		if (hasDoneSomething == false
				&& GameController.board.isGameInProgress()) {
			JOptionPane.showMessageDialog(null, "Stallo, nero perde");
			GameController.endGame();
		} else {
			GameController.whiteRound = true;
			GameController.graphic.getMenu().setRoundLabel(
					"Turno: " + GameController.getRound());
			GameController.graphic.repaint(GameController.board.getBoard());
		}
	}

}