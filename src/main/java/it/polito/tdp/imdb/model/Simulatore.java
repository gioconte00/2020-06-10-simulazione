package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.model.Evento.EventType;


public class Simulatore {
	
	
	//dati input
	private int numGiorni;
	
	
	//dati output
	private List<Actor> attoriIntervistati;
	private int numPause;
	
	//stato mondo
	Graph<Actor, DefaultWeightedEdge> grafo;
	List<Actor> attori;
	List<String> generiAttori;
	
	//coda eventi
	PriorityQueue<Evento> queue;
	
	
	//costruttore 
	public Simulatore(Graph<Actor, DefaultWeightedEdge> grafo) {
		this.grafo = grafo;
	}

	public void init(int numGiorni) {
		
		this.numGiorni = numGiorni;
		
		this.attoriIntervistati = new ArrayList<Actor>();
		this.numPause = 0;
		
		this.queue = new PriorityQueue<Evento>();
		this.attori = new ArrayList<Actor>(this.grafo.vertexSet());	
		this.generiAttori = new ArrayList<String>();
		
		double random = Math.random()*this.grafo.vertexSet().size();
		this.queue.add(new Evento(EventType.INTERVISTA, 1, this.attori.get((int)random)));
		this.attori.remove(this.attori.get((int)random));
		
	}
	
	public void run() {
		
		while(!this.queue.isEmpty()) {
			Evento e = this.queue.poll();
			processEvent(e);
		}
		
		
	}

	private void processEvent(Evento e) {
		
		switch(e.getTipo()) {
		
		case INTERVISTA: 
			
			this.attoriIntervistati.add(e.getAttore());
			this.generiAttori.add(e.getAttore().getGender());
			
			Double random = Math.random();
			if(random<0.6 && e.getGiorno()<this.numGiorni) {
				if(this.generiAttori.size()>2 && Math.random()<0.9 &&
						this.generiAttori.get(this.generiAttori.size()-2).equals(e.getAttore().getGender())) {
					queue.add(new Evento(EventType.RIPOSO, e.getGiorno()+1, null));
				
				} else {
					
					//scelgo l'attore casulmente
					Actor actor = this.attori.get((int)Math.random()*this.attori.size());
					queue.add(new Evento(EventType.INTERVISTA, e.getGiorno()+1, actor));
					this.attori.remove(actor);
				}				
			} else if(random>=0.6 && e.getGiorno()<this.numGiorni){
				
				if(this.generiAttori.size()>2 && Math.random()<0.9 &&
						this.generiAttori.get(this.generiAttori.size()-2).equals(e.getAttore().getGender())) {
					queue.add(new Evento(EventType.RIPOSO, e.getGiorno()+1, null));
				
				} else {
					
					//chiede consiglio a l'attore del gg prima
					Actor actor = e.getAttore();
					List<Actor> vicini = Graphs.neighborListOf(this.grafo, actor);
					
					//se non ha vicini --> modo casuale
					if(vicini.size()==0) {
						Actor attore = this.attori.get((int)Math.random()*this.attori.size());
						queue.add(new Evento(EventType.INTERVISTA, e.getGiorno()+1, attore));
						this.attori.remove(attore);
					}
					else {
						Actor aa = cercaConsiglio(vicini, actor);
						this.queue.add(new Evento(EventType.INTERVISTA, e.getGiorno()+1, aa));
						this.attori.remove(aa);
					}
				}
			}
				
			break;
			
		case RIPOSO:
			
			this.numPause++;
			
			if(e.getGiorno()<this.numGiorni) {
				Actor actor = this.attori.get((int)Math.random()*this.attori.size());
				queue.add(new Evento(EventType.INTERVISTA, e.getGiorno()+1, actor));
				this.attori.remove(actor);
			}
			
			break;
			
		}
		
	}

	private Actor cercaConsiglio(List<Actor> vicini, Actor attore) {
		
		List<Actor> gradoMax = new ArrayList<Actor>();
		double max = 0;
		
		
		for(Actor a : vicini) {
			if(this.grafo.getEdgeWeight(this.grafo.getEdge(attore, a))>max) {
				max = this.grafo.getEdgeWeight(this.grafo.getEdge(attore, a));
				gradoMax = new ArrayList<Actor>();
				gradoMax.add(a);
			}
			else if(this.grafo.getEdgeWeight(this.grafo.getEdge(attore, a))==max) {
				gradoMax.add(a);
			}
		}
		
		if(gradoMax.size()==1) {
			return gradoMax.get(0);
		} else {
			Actor actor = gradoMax.get((int)Math.random()*gradoMax.size());
			return actor;
		}
		
	}

	public List<Actor> getAttoriIntervistati() {
		return attoriIntervistati;
	}

	public int getNumPause() {
		return numPause;
	}
	
	
}
