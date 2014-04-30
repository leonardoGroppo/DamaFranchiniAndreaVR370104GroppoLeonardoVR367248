package view;

import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import model.Board;
import javax.swing.*;

/**
 * Classe che gestisce la grafica e le azioni da eseguire nell'ambito del Menu di gioco
 * @author Andrea Franchini & Leonardo Groppo
 *
 */

public class Menu extends JPanel {

	private JButton play; // bottone per iniziare una partita
	private JButton giveUp; // bottone che, se premuto durante una partita,
							// consente al giocatore di arrendersi
	private JButton options; // bottone per selezionare la modalità di gioco
	private JButton help; // bottone per visionare il regolamento
	private JPanel gameInformation; // pannello contenente le informazioni sulla
									// partita in corso
	private JLabel round; // etichetta che indica se il turno è nero o bianco
	private JLabel illegalMove; // indica se la mossa che si sta tentando di
								// fare è illegale o no

	private Board board; // NON DEVE ESSERCI!!!
	private GraphicInterface graphic; // JFrame in cui è contenuto il Menu

	// IMPOSTO LE DIMENSIONI DEL MENU
	private final int OPTIONS_WND_HEIGHT = 300;
	private final int OPTIONS_WND_WIDTH = 75;
	private final int HELP_WND_HEIGHT = 545;
	private final int HELP_WND_WIDTH = 440;

	private JFrame optionFrame;
	private JPanel optionPanel;
	private JComboBox optionComboBox;

	private JFrame helpFrame;
	private JPanel helpPanel;
	private JTextArea helpText;

	public Menu(Board b, GraphicInterface i) {

		this.board = b;
		this.graphic = i;

		setLayout();
		createButton();
		setIcon();
		addIconToPanel();

		setVisibility();

		createOptionsFrame();
		createHelpFrame();

	}

	/**
	 * Imposto il layout del menu
	 */
	private void setLayout() {
		this.setLayout(new GridLayout(4, 1)); // il menu è una colonna di 4
												// celle
	}

	private void createButton() {
		// CREAZIONE BOTTONE PLAY
		play = new JButton();
		play.setName("play");
		setListener(play);

		// CREAZIONE DEL BOTTONE OPZIONI
		options = new JButton();
		options.setName("options");
		setListener(options);

		// CREAZIONE DEL BOTTONE HELP
		help = new JButton();
		help.setName("help");
		setListener(help);

		// CREAZINE DEL COMPONENTE CONTENENTE LE INFORMAZIONI SULLA PARTITA
		// CORRENTE
		gameInformation = new JPanel();
		gameInformation.setLayout(new GridLayout(4, 1));
		round = new JLabel("Turno: ");
		illegalMove = new JLabel();
		gameInformation.add(round);
		gameInformation.add(illegalMove);

	}
	
	/**
	 * Impostazione delle icone dei bottoni del menu
	 */
	private void setIcon() {

		play.setIcon(new ImageIcon("images/play.png"));
		options.setIcon(new ImageIcon("images/options.png"));
		help.setIcon(new ImageIcon("images/info.png"));
	}
	/**
	 * Aggiunta deli bottoni al pannello Menu
	 */
	private void addIconToPanel() {
		this.add(play);
		this.add(options);
		this.add(help);
		this.add(gameInformation);
	}
	
	/**
	 * Impostazione delle visibilità dei componenti
	 */
	private void setVisibility() {
		play.setVisible(true);
		options.setVisible(true);
		help.setVisible(true);
		gameInformation.setVisible(false);
	}

	/**
	 * Creazione del Frame  per scegliere la modalità di gioco
	 */
	private void createOptionsFrame() {

		optionFrame = new JFrame("Options");
		optionPanel = new JPanel();
		optionPanel.add(new JLabel("Modalità: "));

		optionComboBox = new JComboBox(); 						// Combobox è il compoentne con il menu a tendina
		optionComboBox.addItem("Human vs Computer (LV. 2)");	
		optionComboBox.addItem("Human vs Computer (LV. 1)");
		optionComboBox.addItem("Human vs Human");
		optionComboBox.addActionListener(new ActionListener() { // imposto un listener che acquisice la modalità di gioco scelta

			@Override
			public void actionPerformed(ActionEvent e) {

				switch (optionComboBox.getSelectedIndex()) { // in base alla mdoalità scelta

				case 0:
					graphic.getGameController().setPlayerVsComputer(true); 	// dichiaro che la partita è tra umano e computer
					graphic.getGameController().setSmartAI(true);		 	// richiamo il metodo del controllore che gestisce la partita
																			// in modalità livello 2
					break;

				case 1:
					graphic.getGameController().setPlayerVsComputer(true); // dichiaro che la pritta è tra umano e computer
					graphic.getGameController().setSmartAI(false);		   // richiamo il metodo del controllore che gestisce la partita
																		   // in modalità livello 1
					break;

				case 2:
					graphic.getGameController().setPlayerVsComputer(false); // richiamo il controllore che gestisce la modalità di gioco
																			// umano vs umano
					break;

				default:
					break;

				}

			}

		});
		
		// AGGIUNGO COMPONENTI AL MENU
		optionPanel.add(optionComboBox);
		optionFrame.add(optionPanel);
		
		// IMPOSTO IL FRAME CON LE INFORMAZIONI SULL PARTITA CORRENTE
		optionFrame.setSize(OPTIONS_WND_HEIGHT, OPTIONS_WND_WIDTH);
		optionFrame.setLocation(graphic.getWINDOW_X() + 50,
				graphic.getWINDOW_Y() + 50);
		optionFrame.setResizable(false);
		optionFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		optionFrame.setVisible(false);
		
		
		graphic.getGameController().setPlayerVsComputer(true);
		graphic.getGameController().setSmartAI(true);
	}
	
	/**
	 * crewazione del frame contente le istruzioni del gioco
	 */
	private void createHelpFrame() {

		helpFrame = new JFrame("Help");
		helpPanel = new JPanel();

		helpText = new JTextArea(
				"IL MOVIMENTO DEI PEZZI\n"
						+ "- Il gioco è sempre iniziato dal giocatore che conduce la partita con le pedine bianche.\n"
						+ "- I giocatori effettuano, alternativamente, una mossa per volta: bianco, nero, bianco ecc.\n"
						+ "- Le pedine muovono sempre in avanti di una casella sulle caselle scure e quando raggiungono \n"
						+ "la base avversaria diventano dame.\n"
						+ "- La dama si contrassegna sovrapponendo un altro pezzo dello stesso colore.\n"
						+ "- La dama può muovere in avanti o indietro di una casella sempre sulle caselle scure.\n\n"
						+ "LE REGOLE DI PRESA\n"
						+ "- La presa è obbligatoria: infatti, quando una pedina incontra una pedina di colore diverso,\n"
						+ "con una casella libera dietro, sulla stessa diagonale, è obbligata a prenderla \n"
						+ "(si dice anche catturarla o mangiarla).\n"
						+ " La pedina, dopo la prima presa, qualora si trovi nelle condizioni di poter nuovamente prendere, \n"
						+ "deve continuare a catturare pezzi (fino a un massimo di tre), potendo anche prendere la dama.\n"
						+ "- La pedina può prendere solo in avanti, lungo le due diagonali.\n"
						+ "- La dama può prendere (catturare, mangiare) su tutte e quattro le diagonali.\n"
						+ "- La dama, dopo la prima presa, qualora si trovi nelle condizioni di poter nuovamente prendere, \n"
						+ "deve continuare a catturare pezzi.\n\n"
						+ "ESITO DELLA PARTITA\n"
						+ "- L'avversario non ha più pezzi.\n"
						+ "- L'avversario non ha più pezzi da muovere, e quindi, non ha possibilità di effettuare alcuna mossa \n"
						+ "avendo tutti i pezzi bloccati.\n"
						+ "- L'avversario abbandona per resa;\n");

		helpText.setEditable(false); // non è modificabile

		helpPanel.add(helpText);
		helpFrame.add(helpPanel);
		
		helpFrame.setSize(HELP_WND_HEIGHT, HELP_WND_WIDTH);
		helpFrame.setResizable(false);
		helpFrame.setLocation(graphic.getWINDOW_X() + 60,
				graphic.getWINDOW_Y() + 60);
		helpFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		helpFrame.setVisible(false);

	}

	private void setListener(final JButton but) {

		but.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				switch (but.getName()) { // in base al bottone premuto

				case "play":	// nel caso venga premuto play
					newGame(but); // inizia una nuova partita
					break;

				case "stop":	// nel caso in cui venga premuto stop
					stopGame(but); // termina la partita corrente e se ne può scegliere una nuova
					break;

				case "options":	// nel caso in cui venga premuto opzioni
					showOptions(); // mostra le varie modalità di gioco
					break;

				case "help":
					showHelp(); // mostra le istruzioni
					break;
				default:
					;
				}

			}
		});

	}
	
	/**
	 * 
	 * @param but: bottone premuto. Viene passato per modificarlo dopo il click: cambio icona e nome.
	 */
	private void newGame(JButton but) {

		gameInformation.setVisible(true);
		round.setText(round.getText() + graphic.getGameController().getRound());
		graphic.getGameController().getBoard().start();
		graphic.getGameController().setWhiteRound(true);
		graphic.getGameController().getPawnThatCanEat().clear(); // svuoto arraylist contenente le pedina che possono essere mangiate
		setRoundLabel("Turno: Bianco");
		graphic.repaint(board.getBoard());
		but.setIcon(new ImageIcon("images/stop.png")); // cambio icona bottone passato come parametro
		but.setName("stop");						   // cambio nome del bottone passato come parametro
	}
	
	/**
	 * 
	 * @param but: bottone premuto. Viene passato per modificarlo dopo il click: cambio icona e nome.
	 */
	private void stopGame(JButton but) {

		JOptionPane.showMessageDialog(null, graphic.getGameController().getRound() + " si è arreso!"); // messaggio che il giocatore corrente si è arreso
		gameInformation.setVisible(false);
		round.setText("Turno: ");
		graphic.getGameController().getPawnThatCanEat().clear();
		board.stop();
		graphic.repaint(board.getBoard());
		but.setIcon(new ImageIcon("images/play.png"));
		but.setName("play");
	}

	private void showOptions() {

		if (board.isGameInProgress()) {
			optionComboBox.setEnabled(false);
		} else {
			optionComboBox.setEnabled(true);
		}

		optionFrame.setVisible(true);
	}

	private void showHelp() {

		helpFrame.setVisible(true);
	}

	public void setRoundLabel(String s) {
		round.setText(s);
	}

	public void setIllegalLabel(String s) {
		illegalMove.setText(s);
	}

	public void setPlayButton() {
		play.setIcon(new ImageIcon("images/play.png"));
		play.setName("play");
		play.setVisible(true);
	}

}
