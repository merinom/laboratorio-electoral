package es.upm.dit.isst.models;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Circumscription implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	 @Id @GeneratedValue long id; 
	
	 private long population;
	 private long polled;
	 private String name;
	 private String localization;
	 @ManyToOne
	 private String Simulname;
	 
	 @OneToMany(targetEntity=VotingIntent.class,fetch=FetchType.EAGER)
	 private Collection votingIntents;      
	 
	 @OneToMany(fetch=FetchType.EAGER, mappedBy="circumscription")
	 private List<VotingIntent> votingIntents;      
	
	
}