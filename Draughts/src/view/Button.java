package view;

import java.io.Serializable;

import javax.swing.JButton;
/**
 * Classe che implementa il bottone che forma una cella della damiera.
 * 
 * @author Andrea Franchini & Leonardo Groppo
 *
 */
public class Button extends JButton implements Serializable {
	
	public static final long serialVersionUID = 1L;
	
	// VARIABILI D'ISTANZA
	
	private int x; // ascissa bottone
	private int y; // ordinata bottone
	
	// METODI COSTRUTTORI
	
	public Button(int x, int y){
		super(); // richiamo costruttore JButton
		this.x = x;
		this.y = y;		
	}
	
	// METODI GET
	
	public int getMyX(){
		return this.x;
	}
	

	public int getMyY(){
		return this.y;
	}

}
