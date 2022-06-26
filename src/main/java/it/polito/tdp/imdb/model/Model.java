package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {

	private ImdbDAO dao;
	private Graph<Actor, DefaultWeightedEdge> grafo;
	private Map<Integer, Actor> idMap;
	
	private Simulatore s;
	
	public Model() {
		
		this.dao = new ImdbDAO();
		this.idMap = new HashMap<Integer, Actor>();
	}
	
	
	public String creaGrafo(String genre) {
		
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.dao.getVertici(genre, idMap);
		
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		for(Adiacenza a : this.dao.getArchi(genre, idMap)) {
			if(!this.grafo.containsEdge(a.getA1(), a.getA2())) {
				Graphs.addEdgeWithVertices(this.grafo, a.getA1(), a.getA2(), a.getPeso());
			}
		}
		
		return "Grafo creato con "+this.grafo.vertexSet().size()+" vertici e "+this.grafo.edgeSet().size()+
				" archi.";
		
	}
	
	public Set<Actor> getAttori() {
		return this.grafo.vertexSet();
	}
	
	public List<String> getAllGenre() {
		return this.dao.loadAllGenre();
	}
	
	
	
	public List<Actor> getComponenteConnessa(Actor a) {
		
		ConnectivityInspector<Actor, DefaultWeightedEdge> ci =
					new ConnectivityInspector<Actor, DefaultWeightedEdge>(this.grafo);
		List<Actor> result = new ArrayList<>(ci.connectedSetOf(a));
		result.remove(a);
		Collections.sort(result);
		
		return result;
		
	}
	
	//simulazione
	
	public void doSimulatore(int numGiorni) {
		
		s = new Simulatore(this.grafo);
		s.init(numGiorni);
		s.run();
		
	}
	
	
	public List<Actor> getAttoriIntervistati () {
		return this.s.getAttoriIntervistati();
	}
	
	public int getNumPause() {
		return this.s.getNumPause();
	}
}
