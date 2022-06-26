package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Adiacenza;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO {
	
	public List<Actor> listAllActors(){
		String sql = "SELECT * FROM actors";
		List<Actor> result = new ArrayList<Actor>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				
				result.add(actor);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Movie> listAllMovies(){
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), 
						res.getInt("year"), res.getDouble("rank"));
				
				result.add(movie);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Director> listAllDirectors(){
		String sql = "SELECT * FROM directors";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> loadAllGenre () {
		
		String sql = "SELECT distinct genre "
				+ "FROM movies_genres "
				+ "ORDER BY genre";
		
		List<String> result = new ArrayList<String>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
		
				result.add(res.getString("genre"));
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public void getVertici(String genre, Map<Integer, Actor> idMap) {
		
		String sql = "SELECT distinct a.id, a.first_name, a.last_name, a.gender "
				+ "FROM movies_genres g, actors a, roles r "
				+ "WHERE a.id=r.actor_id AND r.movie_id=g.movie_id AND g.genre = ?";
		
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genre);
			ResultSet res = st.executeQuery();
			while (res.next()) {
			
				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				
				idMap.put(actor.getId(), actor);
			}
			conn.close();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		
		}		
	}
	
	
	public List<Adiacenza> getArchi(String genre, Map<Integer, Actor> idMap) {
		
		
		String sql = "SELECT a1.id AS id1, a2.id AS id2, COUNT(DISTINCT g.movie_id) AS peso "
				+ "FROM movies_genres g, actors a1, actors a2, roles r1, roles r2 "
				+ "WHERE a1.id=r1.actor_id AND a2.id = r2.actor_id AND r1.movie_id=g.movie_id "
				+ "	AND r2.movie_id = g.movie_id AND g.genre = ? AND a1.id<a2.id "
				+ "GROUP BY a1.id, a2.id ";
			
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genre);
			ResultSet res = st.executeQuery();
			while (res.next()) {
		
				result.add(new Adiacenza(idMap.get(res.getInt("id1")), idMap.get(res.getInt("id2")),
									res.getInt("peso")));
				
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}	
		
	}
	
}
