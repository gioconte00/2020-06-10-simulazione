package it.polito.tdp.imdb.model;

public class Evento implements Comparable<Evento>{
	
	public enum EventType {
		INTERVISTA,
		RIPOSO
	}
	
	
	private EventType tipo;
	private Integer giorno;
	private Actor attore;
	

	
	public Evento(EventType tipo, Integer giorno, Actor attore) {
		super();
		this.tipo = tipo;
		this.giorno = giorno;
		this.attore = attore;
	}




	public EventType getTipo() {
		return tipo;
	}




	public Integer getGiorno() {
		return giorno;
	}




	public Actor getAttore() {
		return attore;
	}




	@Override
	public int compareTo(Evento o) {
		
		return this.giorno.compareTo(o.giorno);
	}
	
	

}
