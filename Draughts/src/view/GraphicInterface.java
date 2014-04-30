package view;

import java.awt.BorderLayout;
import javax.swing.*;
import controller.GameController;
import model.Board;

/**
 * Questa classe specifica il JFrame contenente tutti i componenti grafici
 * @author Andrea Franchini & Leonardo Groppo. 
 *
 */

public class GraphicInterface extends JFrame{

	private final int HEIGHT = 800;
	private final int WIDTH = 650;
	private final int WINDOW_X = 200;
	private final int WINDOW_Y = 50;
	
	private Menu menu; // JPanel che contiene i componenti grafici del menu
	private Game game; // JPanel che contine i componenti grafici della damiera
	private Board board;
	private GameController controller; // controllore del gioco
	
	// METODI COSTRUTTORE
	
	public GraphicInterface(Board b){
		super("Dama Italiana - Andrea Franchini & Leonardo Groppo"); // titolo della finestra
		
		setWindow(); // imposto il JFrame
		
		addComponent(b); // aggiungo al JFrame la damiera
		
		this.setVisible(true); // rendo visibile il contenuto
		
	}
	
	/**
	 * Imposta le caratteristiche dei componenti grafici
	 */
	private void setWindow(){
		
		this.setSize(HEIGHT, WIDTH); 					// imposto la dimensione
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);	
		this.setResizable(false);						// rendo la finestra NON ridimensionabile
		this.setLocation(WINDOW_X, WINDOW_Y);			// metto la finestra in una determinata posizione sullo schermo
		this.setLayout(new BorderLayout());				// il JFrame è composto da una BorderLayout
		
	}
	
	/**
	 * 
	 * @param b: damiera 
	 */
	private void addComponent(Board b){
		
		board = b; 
		
		controller = new GameController(board, this); // creo nuovo controllore
		
		menu = new Menu(b, this);					  // creo nuovo menu
		this.add(menu, BorderLayout.WEST);			  // aggiungo il menu nella parte OVEST del BorderLayout
		
		game = new Game(board, controller, this);	
		add(game, BorderLayout.CENTER);				  // aggiungo il GAME
	}
	
	public Game getGame(){
		return game;
	}
	public Menu getMenu(){
		return menu;
	}
	public GameController getGameController(){
		return controller;
	}
	public final int getWINDOW_X(){
		return WINDOW_X;
	}
	public final int getWINDOW_Y(){
		return WINDOW_Y;
	}
	
	public void repaint(int board[][]){
		try{
			game.repaint(board);
		}catch(Exception e){};
		//compromesso ingegneristico
		this.setVisible(true);
	}
}
